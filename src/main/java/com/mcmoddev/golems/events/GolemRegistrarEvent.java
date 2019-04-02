package com.mcmoddev.golems.events;

import net.minecraftforge.eventbus.api.Event;

/**
 * (Experimental) This event is fired simply to alert any child mods that it
 * is safe to access the {@link com.mcmoddev.golems.util.config.GolemRegistrar}
 * <br> This event is NOT cancelable.
 * <br> This event does NOT have a result.
 * <p>While Golems can be added to the {@code GolemRegistrar} at any time,
 * doing so before the {@code RegistryEvent.Register<EntityType<?>>}
 * event makes it possible for the Extra Golems mod to register
 * golems and their render classes automatically, rather than
 * forcing the child mod to keep track of the golems it has added.
 * @see com.mcmoddev.golems.util.config.GolemRegistrar#registerGolem(Class, 
 * com.mcmoddev.golems.util.config.GolemContainer)
 * @see com.mcmoddev.golems.util.config.GolemContainer.Builder
 **/
public class GolemRegistrarEvent extends Event { }
