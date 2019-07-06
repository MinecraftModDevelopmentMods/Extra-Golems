//package com.mcmoddev.golems.entity.ai;
//
//import com.mcmoddev.golems.entity.base.GolemBase;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.entity.ai.goal.TargetGoal;
//import net.minecraft.world.gen.feature.structure.VillagePieces.Village;
//
//import java.util.EnumSet;
//
//import java.util.EnumSet;
//
//public class EntityAIDefendAgainstMonsters extends TargetGoal {
//
//	final GolemBase entityGolem;
//	/**
//	 * The aggressor of the iron golem's village which is now the golem's attack target.
//	 */
//	LivingEntity villageAgressorTarget;
//	// private List villageAgressors = new ArrayList();
//
//	public EntityAIDefendAgainstMonsters(final GolemBase golem) {
//		super(golem, false, true);
//		this.entityGolem = golem;
//		this.setMutexFlags(EnumSet.of(Flag.TARGET));
//	}
//
//	public boolean shouldExecute() {
//		final Village village = this.entityGolem.getVillage();
//
//		if (village == null) {
//			return false;
//		} else {
//			this.villageAgressorTarget = village.findNearestVillageAggressor(this.entityGolem);
//
//			if (!this.isSuitableTarget(this.villageAgressorTarget, false)) {
//				if (this.taskOwner.getRNG().nextInt(20) == 0) {
//					this.villageAgressorTarget = village
//							.findNearestVillageAggressor(this.entityGolem);
//					return this.isSuitableTarget(this.villageAgressorTarget, false);
//				} else {
//					return false;
//				}
//			} else {
//				return true;
//			}
//		}
//	}
//
//	/**
//	 * Execute a one shot task or start executing a continuous task.
//	 */
//	@Override
//	public void startExecuting() {
//		this.entityGolem.setAttackTarget(this.villageAgressorTarget);
//		super.startExecuting();
//	}
//}
