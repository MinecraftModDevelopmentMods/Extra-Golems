package com.golems.entity.ai;

import com.golems.entity.GolemBase;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.village.Village;

public class EntityAIDefendAgainstMonsters extends EntityAITarget
{
    GolemBase entityGolem;
    /** The aggressor of the iron golem's village which is now the golem's attack target. */
    EntityLivingBase villageAgressorTarget;
    public EntityAIDefendAgainstMonsters(GolemBase golem)
    {
        super(golem, false, true);
        this.entityGolem = golem;
        this.setMutexBits(1);
    }
    
    public boolean shouldExecute()
    {
        Village village = this.entityGolem.getVillage();

        if (village == null)
        {
            return false;
        }
        else
        {
            this.villageAgressorTarget = village.findNearestVillageAggressor(this.entityGolem);

            if (!this.isSuitableTarget(this.villageAgressorTarget, false))
            {
                if (this.taskOwner.getRNG().nextInt(20) == 0)
                {
                    this.villageAgressorTarget = village.findNearestVillageAggressor(this.entityGolem);
                    return this.isSuitableTarget(this.villageAgressorTarget, false);
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return true;
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.entityGolem.setAttackTarget(this.villageAgressorTarget);
        super.startExecuting();
    }
}
