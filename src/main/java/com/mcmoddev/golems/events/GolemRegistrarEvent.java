package com.mcmoddev.golems.events;

import com.mcmoddev.golems.util.config.GolemContainer;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is fired after all Extra Golems {@code GolemContainer}
 * registration has finished to alert any child mods that it is safe to access
 * the {@link com.mcmoddev.golems.util.config.GolemRegistrar} <br>
 * This event is NOT cancelable. <br>
 * This event does NOT have a result.
 * <p>
 * While Golems can be added to the {@code GolemRegistrar} at any time, doing so
 * before the {@code RegistryEvent.Register<EntityType<?>>} event makes it
 * possible for the Extra Golems mod to register golems and their render classes
 * automatically, rather than forcing the child mod to keep track of the golems
 * it has added.
 * <p>
 * It is also possible to use this event to modify existing GolemContainers, by
 * adding additional building Blocks or BlockTags.
 *
 * @see com.mcmoddev.golems.util.config.GolemRegistrar#registerGolem(GolemContainer)
 * @see GolemContainer.Builder
 **/
public class GolemRegistrarEvent extends Event {
}
