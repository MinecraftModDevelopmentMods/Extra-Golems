package com.mcmoddev.golems.data.behavior.data;

import com.mcmoddev.golems.entity.IExtraGolem;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * {@link com.mcmoddev.golems.data.behavior.Behavior}s can attach this to an
 * {@link com.mcmoddev.golems.entity.IExtraGolem} to store instance data.
 * @see com.mcmoddev.golems.data.behavior.Behavior#onAttachData(IExtraGolem)
 */
public interface IBehaviorData extends INBTSerializable<CompoundTag> {

}
