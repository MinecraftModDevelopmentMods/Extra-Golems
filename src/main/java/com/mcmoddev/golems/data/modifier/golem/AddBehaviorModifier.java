package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.Behavior;
import com.mcmoddev.golems.data.behavior.BehaviorList;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.modifier.Modifier;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Optional;

/**
 * Adds all of the given behaviors to the {@link BehaviorList.Builder}
 */
@SuppressWarnings("rawtypes")
@Immutable
public class AddBehaviorModifier extends Modifier {

	public static final Codec<AddBehaviorModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EGCodecUtils.listOrElementCodec(Behavior.DIRECT_CODEC).fieldOf("behavior").forGetter(AddBehaviorModifier::getBehaviors),
			BehaviorList.HOLDER_CODEC.optionalFieldOf("reference").forGetter(o -> Optional.ofNullable(o.behaviorList)),
			Codec.BOOL.optionalFieldOf("replace", false).forGetter(AddBehaviorModifier::replace)
	).apply(instance, AddBehaviorModifier::new));

	private final List<Behavior> behaviors;
	private final @Nullable Holder<BehaviorList> behaviorList;
	private final boolean replace;

	public AddBehaviorModifier(List<Behavior> behaviors, Optional<Holder<BehaviorList>> behaviorList, boolean replace) {
		this.behaviors = behaviors;
		this.behaviorList = behaviorList.orElse(null);
		this.replace = replace;
	}

	//// GETTERS ////

	public List<Behavior> getBehaviors() {
		return behaviors;
	}

	@Nullable
	public Holder<BehaviorList> getBehaviorList() {
		return behaviorList;
	}

	public boolean replace() {
		return replace;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		builder.behaviors(b -> {
			if(replace()) {
				b.clear();
			}
			// resolve holders
			if(getBehaviorList() != null) {
				b.addAllHolders(getBehaviorList().get().getBehaviors());
			}
			// add elements
			b.addAll(this.getBehaviors());
		});
	}

	@Override
	public Codec<? extends Modifier> getCodec() {
		return EGRegistry.GolemModifierReg.ADD_BEHAVIOR.get();
	}
}
