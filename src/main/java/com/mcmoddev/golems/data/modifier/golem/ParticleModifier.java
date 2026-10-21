package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.modifier.Modifier;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

import javax.annotation.concurrent.Immutable;

/**
 * Sets the particle of the golem
 */
@Immutable
public class ParticleModifier extends Modifier {

	public static final MapCodec<ParticleModifier> CODEC = ParticleTypes.CODEC
			.xmap(ParticleModifier::new, ParticleModifier::getParticle)
			.fieldOf("particle");

	private final ParticleOptions particle;

	public ParticleModifier(ParticleOptions particle) {
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
	public MapCodec<? extends Modifier> getCodec() {
		return EGRegistry.GolemModifierReg.PARTICLE.get();
	}
}
