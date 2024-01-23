package com.mcmoddev.golems;

import com.mcmoddev.golems.block.GolemHeadBlock;
import com.mcmoddev.golems.data.GolemContainer;
import com.mcmoddev.golems.data.behavior.ShootArrowsBehavior;
import com.mcmoddev.golems.data.behavior.UseFuelBehavior;
import com.mcmoddev.golems.data.behavior.data.UseFuelBehaviorData;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mcmoddev.golems.network.SummonGolemCommand;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.GolemSensor;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public final class EGEvents {

	public static void register() {
		FMLJavaModLoadingContext.get().getModEventBus().register(ModHandler.class);
		MinecraftForge.EVENT_BUS.register(ForgeHandler.class);
	}

	public static class ModHandler {

	}

	public static class ForgeHandler {

		private static final TagKey<Golem> VILLAGER_SUMMONABLE = TagKey.create(EGRegistry.Keys.GOLEMS, new ResourceLocation(ExtraGolems.MODID, "villager_summonable"));

		@SubscribeEvent
		public static void onAddCommands(final RegisterCommandsEvent event) {
			SummonGolemCommand.register(event.getDispatcher());
		}

		/**
		 * Checks if a Carved Pumpkin was placed and, if so, attempts to spawn a entity
		 * at that location where enabled by the config.
		 **/
		@SubscribeEvent
		public static void onPlacePumpkin(final BlockEvent.EntityPlaceEvent event) {
			// if the config allows it, and the block is a CARVED pumpkin...
			if (!event.isCanceled() && ExtraGolems.CONFIG.pumpkinBuildsGolems() && event.getPlacedBlock().getBlock() == Blocks.CARVED_PUMPKIN
					&& event.getLevel() instanceof Level) {
				// try to spawn an entity!
				GolemHeadBlock.trySpawnGolem(event.getEntity(), (Level) event.getLevel(), event.getPos());
			}
		}

		/**
		 * Prevents arrow-shooting golems from hurting villagers
		 * @param event the living hurt event
		 */
		@SubscribeEvent
		public static void onLivingHurt(final LivingHurtEvent event) {
			if(event.getEntity() instanceof AbstractVillager && event.getSource().is(DamageTypes.MOB_PROJECTILE)
					&& event.getSource().getEntity() instanceof IExtraGolem golem
					&& golem.getContainer(event.getEntity().level().registryAccess()).isPresent()
					&& golem.getContainer(event.getEntity().level().registryAccess()).get().getBehaviors().hasActiveBehavior(ShootArrowsBehavior.class, golem)) {
				event.setCanceled(true);
			}
		}

		/**
		 * Prevents mobs from targeting inert Furnace Golems
		 **/
		@SubscribeEvent
		public static void onTargetEvent(final LivingChangeTargetEvent event) {
			if (event.getEntity() instanceof Mob mob && event.getNewTarget() instanceof IExtraGolem target) {
				// resolve the golem container
				final Optional<GolemContainer> oContainer = target.getContainer(mob.level().registryAccess());
				if(oContainer.isPresent() && oContainer.get().getBehaviors().hasActiveBehavior(UseFuelBehavior.class, target)
						&& target.getBehaviorData(UseFuelBehaviorData.class).map(data -> !data.hasFuel()).orElse(false)) {
					event.setCanceled(true);
					mob.setLastHurtByMob(null);
				}
			}
		}

		@SubscribeEvent
		public static void onMobSummoned(final MobSpawnEvent.SpawnPlacementCheck event) {
			if(event.getEntityType() == EntityType.IRON_GOLEM && event.getSpawnType() == MobSpawnType.MOB_SUMMONED
					&& ExtraGolems.CONFIG.villagerSummonChance() > 0
					&& event.getRandom().nextInt(100) < ExtraGolems.CONFIG.villagerSummonChance()) {
				// determine material
				final ResourceLocation golemId = getGolemToSpawn(event.getLevel().getLevel(), event.getPos(), event.getRandom());
				if(golemId != null) {
					// cancel the event
					event.setCanceled(true);
					// attempt to summon a golem
					GolemBase golem = GolemBase.create(event.getLevel().getLevel(), golemId);
					// randomize texture if applicable
					if (golem.getVariantCount() > 0) {
						golem.randomizeVariant(event.getLevel().getLevel(), event.getPos(), event.getRandom());
					}
					// add the golem entity
					event.getLevel().addFreshEntityWithPassengers(golem);
					// finalize spawn (required to adjust current health to max health)
					golem.finalizeSpawn(event.getLevel(), event.getLevel().getCurrentDifficultyAt(event.getPos()), MobSpawnType.MOB_SUMMONED, null, null);
					// locate nearby villagers
					AABB aabb = golem.getBoundingBox().inflate(10.0D, 10.0D, 10.0D);
					List<Villager> nearbyVillagers = event.getLevel().getEntitiesOfClass(Villager.class, aabb);
					// reset golem detected flags
					nearbyVillagers.forEach(GolemSensor::golemDetected);
				}
			}
		}

		/**
		 * @param level the level
		 * @param pos an approximate block position for the entity
		 * @param random a random generator
		 * @return a random golem material from the config, or null empty material if the config is empty
		 */
		@Nullable
		private static ResourceLocation getGolemToSpawn(final Level level, final BlockPos pos, final RandomSource random) {
			final Registry<Golem> registry = level.registryAccess().registryOrThrow(EGRegistry.Keys.GOLEMS);
			final Optional<Holder<Golem>> oHolder = registry.getOrCreateTag(VILLAGER_SUMMONABLE).getRandomElement(random);
			if(oHolder.isEmpty()) {
				return null;
			}
			final Optional<ResourceKey<Golem>> oKey = oHolder.get().unwrapKey();
			if(oKey.isEmpty()) {
				return null;
			}
			return oKey.get().location();
		}
	}
}
