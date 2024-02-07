package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.GolemContainer;
import com.mcmoddev.golems.data.behavior.util.GolemPredicate;
import com.mcmoddev.golems.data.behavior.util.TooltipPredicate;
import com.mcmoddev.golems.data.behavior.util.TriggerType;
import com.mcmoddev.golems.data.behavior.util.UpdateTarget;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mcmoddev.golems.util.DeferredHolderSet;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.util.EGComponentUtils;
import com.mcmoddev.golems.util.PredicateUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;


/**
 * This behavior allows an entity to change its golem or variant
 * when an item is used on the entity
 **/
@Immutable
public class ItemUpdateGolemBehavior extends Behavior {

	public static final Codec<ItemUpdateGolemBehavior> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EGCodecUtils.MIN_MAX_INTS_CODEC.optionalFieldOf("variant", MinMaxBounds.Ints.ANY).forGetter(Behavior::getVariantBounds),
			TooltipPredicate.CODEC.optionalFieldOf("tooltip", TooltipPredicate.NORMAL).forGetter(Behavior::getTooltipPredicate),
			UpdateTarget.CODEC.fieldOf("apply").forGetter(ItemUpdateGolemBehavior::getApply),
			DeferredHolderSet.codec(BuiltInRegistries.ITEM.key()).optionalFieldOf("item", DeferredHolderSet.empty()).forGetter(ItemUpdateGolemBehavior::getItem),
			Codec.STRING.optionalFieldOf("display_name", "").forGetter(ItemUpdateGolemBehavior::getDisplayNameKey),
			EGCodecUtils.listOrElementCodec(GolemPredicate.CODEC).optionalFieldOf("predicate", ImmutableList.of(GolemPredicate.ALWAYS)).forGetter(ItemUpdateGolemBehavior::getPredicates),
			Codec.BOOL.optionalFieldOf("consume", false).forGetter(ItemUpdateGolemBehavior::consume),
			Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("chance", 1.0D).forGetter(ItemUpdateGolemBehavior::getChance),
			ForgeRegistries.SOUND_EVENTS.getCodec().optionalFieldOf("sound").forGetter(o -> Optional.ofNullable(o.sound)),
			ParticleTypes.CODEC.optionalFieldOf("particle").forGetter(o -> Optional.ofNullable(o.particle))
	).apply(instance, ItemUpdateGolemBehavior::new));

	/** The golem and variant **/
	private final UpdateTarget apply;
	/** The items associated with the update **/
	private final DeferredHolderSet<Item> item;
	/** The display name translation key **/
	private final String displayNameKey;
	/** The conditions to update the golem and variant **/
	private final List<GolemPredicate> predicates;
	/** The conditions to update the golem and variant as a single predicate **/
	private final Predicate<IExtraGolem> predicate;
	/** True to consume the item, if any **/
	private final boolean consume;
	/** The percent chance **/
	private final double chance;
	/** The sound to play when interacting **/
	private final @Nullable SoundEvent sound;
	/** The particle to spawn when interacting **/
	private final @Nullable ParticleOptions particle;

	public ItemUpdateGolemBehavior(MinMaxBounds.Ints variant, TooltipPredicate tooltipPredicate, UpdateTarget apply,
								   DeferredHolderSet<Item> item, String displayNameKey, List<GolemPredicate> predicates, boolean consume, double chance,
								   Optional<SoundEvent> sound, Optional<ParticleOptions> particle) {
		super(variant, tooltipPredicate);
		this.apply = apply;
		this.item = item;
		this.displayNameKey = displayNameKey;
		this.predicates = predicates;
		this.predicate = PredicateUtils.and(predicates);
		this.consume = consume;
		this.chance = chance;
		this.sound = sound.orElse(null);
		this.particle = particle.orElse(null);
	}

	//// GETTERS ////

	public UpdateTarget getApply() {
		return apply;
	}

	public List<GolemPredicate> getPredicates() {
		return predicates;
	}

	public DeferredHolderSet<Item> getItem() {
		return item;
	}

	public String getDisplayNameKey() {
		return displayNameKey;
	}

	public boolean consume() {
		return consume;
	}

	public double getChance() {
		return chance;
	}

	@Nullable
	public SoundEvent getSound() {
		return sound;
	}

	@Nullable
	public ParticleOptions getParticle() {
		return particle;
	}

	@Override
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.ITEM_UPDATE_GOLEM.get();
	}

	//// METHODS ////

	@Override
	public void onMobInteract(IExtraGolem entity, Player player, InteractionHand hand) {
		// only handle mainhand
		if(hand != InteractionHand.MAIN_HAND) {
			return;
		}
		// determine held item
		final ItemStack item = player.getItemInHand(hand);
		final Mob mob = entity.asMob();
		// validate and use item
		if((getItem().isEmpty() || getItem().get(BuiltInRegistries.ITEM).contains(item.getItemHolder()))
				&& this.predicate.test(entity)
				&& mob.getRandom().nextDouble() < getChance()
				&& getApply().apply(entity)) {
			// swing hand
			player.swing(hand);
			// consume item
			if(consume() && !player.getAbilities().instabuild) {
				item.shrink(1);
			}
			// play sound
			if(sound != null) {
				mob.playSound(sound, 1.0F, mob.getVoicePitch());
			}
			// spawn particle
			if(particle != null) {
				((ServerLevel)player.level()).sendParticles(particle, mob.getX(), mob.getY(0.6F), mob.getZ(), 10, 0.4D, 0.5D, 0.4D, 0);
			}
		}
	}

	@Override
	public List<Component> createDescriptions(RegistryAccess registryAccess) {
		// create filtered list of predicates, ignoring ALWAYS and NEVER
		final List<GolemPredicate> filteredPredicates = predicates.stream()
				.filter(p -> p != GolemPredicate.ALWAYS && p != GolemPredicate.NEVER)
				.collect(ImmutableList.toImmutableList());
		// create predicate text, if any
		final Optional<Component> predicateText = EGComponentUtils.combineWithAnd(filteredPredicates, GolemPredicate::getDescriptionId);

		// resolve display name of the item
		Component itemName;
		if(this.displayNameKey != null && !this.displayNameKey.isEmpty()) {
			itemName = Component.translatable(this.displayNameKey);
		} else if(!this.item.isEmpty()) {
			final Item randomItem = this.item.get(BuiltInRegistries.ITEM).get(0).get();
			itemName = randomItem.getDescription();
		} else {
			itemName = Component.translatable(PREFIX + "item_update_golem.empty_hand");
		}
		itemName = itemName.copy().withStyle(ChatFormatting.LIGHT_PURPLE);

		// create description when golem can change
		if(apply.getGolem() != null) {
			// load golem from ID
			GolemContainer container = GolemContainer.getOrCreate(registryAccess, apply.getGolem());
			Component name = container.getTypeName().copy().withStyle(ChatFormatting.BLUE);
			if(predicateText.isPresent()) {
				return ImmutableList.of(Component.translatable(PREFIX + "item_update_golem.golem.predicate", itemName, name, predicateText.get()));
			}
			return ImmutableList.of(Component.translatable(PREFIX + "item_update_golem.golem", itemName, name));
		}

		// create description when variant can change
		if(predicateText.isPresent()) {
			return ImmutableList.of(Component.translatable(PREFIX + "item_update_golem.variant.predicate", itemName, predicateText.get()));
		}
		return ImmutableList.of(Component.translatable(PREFIX + "item_update_golem.variant", itemName));
	}

	//// EQUALITY ////


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ItemUpdateGolemBehavior)) return false;
		if (!super.equals(o)) return false;
		ItemUpdateGolemBehavior that = (ItemUpdateGolemBehavior) o;
		return consume == that.consume && Double.compare(that.chance, chance) == 0 && apply.equals(that.apply) && item.equals(that.item) && predicates.equals(that.predicates) && Objects.equals(sound, that.sound) && Objects.equals(particle, that.particle);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), apply, item, predicates, consume, chance, sound, particle);
	}
}
