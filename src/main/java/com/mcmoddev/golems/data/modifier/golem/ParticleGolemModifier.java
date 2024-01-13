package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.modifier.GolemModifier;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

import javax.annotation.concurrent.Immutable;

/**
 * Sets the group of the golem
 */
@Immutable
public class ParticleGolemModifier extends GolemModifier {

	public static final Codec<ParticleGolemModifier> CODEC = ParticleTypes.CODEC
			.xmap(ParticleGolemModifier::new, ParticleGolemModifier::getParticle)
			.fieldOf("particle").codec();

	private final ParticleOptions particle;

	public ParticleGolemModifier(ParticleOptions particle) {
		this.particle = particle;
	}

	//// GETTERS ////

	public ParticleOptions getParticle() {
		return particle;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		builder.particle(getParticle());
	}

	@Override
	public Codec<? extends GolemModifier> getCodec() {
		return EGRegistry.GolemModifierReg.PARTICLE.get();
	}
}
