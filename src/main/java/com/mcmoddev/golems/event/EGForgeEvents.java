package com.mcmoddev.golems.event;

import com.mcmoddev.golems.EGConfig;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.block.GolemHeadBlock;
import com.mcmoddev.golems.container.GolemContainer;
import com.mcmoddev.golems.container.behavior.GolemBehaviors;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.network.SGolemContainerPacket;
import com.mcmoddev.golems.network.SGolemModelPacket;
import com.mcmoddev.golems.network.SummonGolemCommand;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.sensing.GolemSensor;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class EGForgeEvents {

	@SubscribeEvent
	public static void addReloadListeners(final AddReloadListenerEvent event) {
		event.addListener(ExtraGolems.GOLEM_CONTAINERS);
		event.addListener(ExtraGolems.GOLEM_RENDER_SETTINGS);
	}

	@SubscribeEvent
	public static void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
		Player player = event.getEntity();
		// reload golem containers
		if (player instanceof ServerPlayer) {
			ExtraGolems.GOLEM_CONTAINERS.getEntries().forEach(e -> e.getValue().ifPresent(c -> ExtraGolems.CHANNEL.send(PacketDistributor.ALL.noArg(), new SGolemContainerPacket(e.getKey(), c))));
			ExtraGolems.GOLEM_RENDER_SETTINGS.getEntries().forEach(e -> e.getValue().ifPresent(c -> ExtraGolems.CHANNEL.send(PacketDistributor.ALL.noArg(), new SGolemModelPacket(e.getKey(), c))));
		}
	}

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
	 * Prevents mobs from targeting inert Furnace Golems
	 **/
	@SubscribeEvent
	public static void onTargetEvent(final LivingSetAttackTargetEvent event) {
		if (event.getEntity() instanceof Mob mob && event.getTarget() instanceof GolemBase target) {
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
					// assign golem material
					ResourceLocation material = getGolemToSpawn(villager.blockPosition(), villager.getRandom());
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
	 * @param pos an approximate block position for the entity
	 * @param random a random generator
	 * @return a random golem material from the config, or the empty material if the config is empty
	 */
	private static ResourceLocation getGolemToSpawn(final BlockPos pos, final RandomSource random) {
		final List<ResourceLocation> options = ExtraGolems.CONFIG.getVillagerGolems();
		final ResourceLocation choice = options.isEmpty() ? GolemContainer.EMPTY_MATERIAL : options.get(random.nextInt(options.size()));
		return choice;
	}
}
