package com.mcmoddev.golems;

import com.mcmoddev.golems.block.GolemHeadBlock;
import com.mcmoddev.golems.container.GolemContainer;
import com.mcmoddev.golems.container.behavior.GolemBehaviors;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.network.SummonGolemCommand;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SpawnUtil;
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
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

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

		private static final TagKey<GolemContainer> VILLAGER_SUMMONABLE = TagKey.create(ExtraGolems.Keys.GOLEM_CONTAINERS, new ResourceLocation(ExtraGolems.MODID, "villager_summonable"));

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
			if(event.getEntity() instanceof AbstractVillager && event.getSource().isProjectile()
					&& event.getSource().getEntity() instanceof GolemBase golem
					&& golem.getContainer().hasBehavior(GolemBehaviors.SHOOT_ARROWS)) {
				event.setCanceled(true);
			}
		}

		/**
		 * Prevents mobs from targeting inert Furnace Golems
		 **/
		@SubscribeEvent
		public static void onTargetEvent(final LivingChangeTargetEvent event) {
			if (event.getEntity() instanceof Mob mob && event.getNewTarget() instanceof GolemBase target) {
				// clear the attack target
				if (target.getContainer().hasBehavior(GolemBehaviors.USE_FUEL) && !target.hasFuel()) {
					mob.setTarget(null);
					mob.setLastHurtByMob(null);
				}
			}
		}

		/**
		 * Used to force villagers to spawn golems other than iron golems.
		 * Checks if the villager is awake and wants to spawn a golem,
		 * then checks for nearby villagers, following the same logic as
		 * regular golem summoning.
		 * @param event the living tick event
		 */
		@SubscribeEvent
		public static void onLivingUpdate(final LivingEvent.LivingTickEvent event) {
			if (event.getEntity().isEffectiveAi()
					&& event.getEntity().level instanceof ServerLevel serverLevel
					&& ExtraGolems.CONFIG.villagerSummonChance() > 0
					&& event.getEntity() instanceof Villager villager
					&& !villager.isSleeping()
					&& villager.tickCount % 90L == 0L
					&& !checkForNearbyGolem(villager)
					&& villager.wantsToSpawnGolem(villager.tickCount)
					&& villager.getRandom().nextInt(100) < ExtraGolems.CONFIG.villagerSummonChance()) {
				// locate nearby villagers who also want to summon a golem
				AABB aabb = villager.getBoundingBox().inflate(10.0D, 10.0D, 10.0D);
				List<Villager> nearbyVillagers = serverLevel.getEntitiesOfClass(Villager.class, aabb)
						.stream().filter((e) -> e.wantsToSpawnGolem(villager.tickCount))
						.limit(5L).toList();
				// ensure a minimum number of villagers to summon a golem
				if (nearbyVillagers.size() >= 3) {
					// attempt to summon a golem
					Optional<GolemBase> entity = SpawnUtil.trySpawnMob(EGRegistry.GOLEM.get(), MobSpawnType.MOB_SUMMONED, serverLevel, villager.blockPosition(), 10, 8, 6, SpawnUtil.Strategy.LEGACY_IRON_GOLEM);
					if(entity.isPresent()) {
						GolemBase golem = entity.get();
						// determine material
						ResourceLocation material = getGolemToSpawn(villager.getLevel(), villager.blockPosition(), villager.getRandom());
						if(null == material) {
							golem.discard();
							return;
						}
						golem.setMaterial(material);
						// randomize texture if applicable
						if (golem.getTextureCount() > 0) {
							golem.randomizeTexture(serverLevel, villager.blockPosition());
						}
						// finalize spawn (required to adjust current health to max health)
						golem.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(villager.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
						// reset golem detected flags
						nearbyVillagers.forEach(GolemSensor::golemDetected);
					}
				}
			}
		}

		/**
		 * @param level the level
		 * @param pos an approximate block position for the entity
		 * @param random a random generator
		 * @return a random golem material from the config, or the empty material if the config is empty
		 */
		private static ResourceLocation getGolemToSpawn(final Level level, final BlockPos pos, final RandomSource random) {
			final Registry<GolemContainer> registry = level.registryAccess().registryOrThrow(ExtraGolems.Keys.GOLEM_CONTAINERS);
			final Optional<Holder<GolemContainer>> oHolder = registry.getOrCreateTag(VILLAGER_SUMMONABLE).getRandomElement(random);
			final GolemContainer container = oHolder.orElse(Holder.direct(GolemContainer.EMPTY)).get();
			return registry.getKey(container);
		}

		/**
		 * Checks if the nearest living entity memory contains any golem or subclass.
		 * Required because the version used by villagers only checks entity type.
		 * @param villager the villager
		 * @return true if a nearby golem was detected.
		 */
		private static boolean checkForNearbyGolem(LivingEntity villager) {
			Optional<List<LivingEntity>> optional = villager.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES);
			if (optional.isPresent()) {
				boolean flag = optional.get().stream().anyMatch(e -> e instanceof IronGolem);
				if (flag) {
					GolemSensor.golemDetected(villager);
					return true;
				}
			}
			return false;
		}
	}
}
