package it.crystalnest.copper_fire.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Loot modifier.
 */
public final class LootModifier extends net.neoforged.neoforge.common.loot.LootModifier {
  /**
   * {@link Supplier} for this {@link Codec}.
   */
  public static final MapCodec<LootModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> codecStart(instance).and(instance.group(
    Codec.FLOAT.optionalFieldOf("replacement_chance", 0F).forGetter(modifier -> modifier.replacementChance),
    Codec.INT.optionalFieldOf("empty_weight", 0).forGetter(modifier -> modifier.emptyWeight),
    ItemEntry.CODEC.listOf().optionalFieldOf("items", List.of()).forGetter(modifier -> modifier.items),
    EnchantmentEntry.CODEC.listOf().optionalFieldOf("enchantments", List.of()).forGetter(modifier -> modifier.enchantments)
  )).apply(instance, LootModifier::new));

  /**
   * Optional chance to replace the generated loot with the loot from this modifier.
   * If 0 or less, the modifier will always add its loot to the generated loot.
   */
  private final float replacementChance;

  /**
   * Weight for the empty (nothing) entry.
   */
  private final int emptyWeight;

  /**
   * Item entries to potentially add to the loot.
   */
  private final List<ItemEntry> items;

  /**
   * Enchanted book entries to potentially add to the loot.
   */
  private final List<EnchantmentEntry> enchantments;

  /**
   * @param condition optional loot item condition.
   * @param priority native loot modifier priority.
   * @param replacementChance probability of replacing the original loot.
   * @param emptyWeight weight for the empty entry.
   * @param items item entries.
   * @param enchantments enchanted book entries.
   */
  private LootModifier(Optional<Holder<LootItemCondition>> condition, int priority, float replacementChance, int emptyWeight, List<ItemEntry> items, List<EnchantmentEntry> enchantments) {
    super(condition, priority);
    this.replacementChance = replacementChance;
    this.emptyWeight = emptyWeight;
    this.items = items;
    this.enchantments = enchantments;
  }

  @Override
  @NotNull
  protected ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> generatedLoot, @NotNull LootContext context) {
    HolderLookup.RegistryLookup<Enchantment> lookup = context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
    List<ResolvedEnchantment> resolved = new ArrayList<>();
    for (EnchantmentEntry entry : enchantments) {
      lookup.get(ResourceKey.create(Registries.ENCHANTMENT, entry.enchantment())).ifPresent(holder -> resolved.add(new ResolvedEnchantment(holder, entry.weight())));
    }
    int totalWeight = emptyWeight + items.stream().mapToInt(ItemEntry::weight).sum() + resolved.stream().mapToInt(ResolvedEnchantment::weight).sum();
    if (totalWeight > 0 && (replacementChance <= 0 || context.getRandom().nextFloat() < replacementChance)) {
      if (replacementChance > 0) {
        generatedLoot.clear();
      }
      ItemStack item = selectItem(context, resolved, totalWeight);
      if (!item.isEmpty()) {
        generatedLoot.add(item);
      }
    }
    return generatedLoot;
  }

  /**
   * Selects an item from the nonempty weighted pool.
   * Returns an empty stack when the roll selects the empty entry.
   *
   * @param context loot context supplying randomness.
   * @param resolved enchantments available in the current world.
   * @param totalWeight positive total weight of the pool.
   * @return selected item or an empty stack.
   */
  private ItemStack selectItem(LootContext context, List<ResolvedEnchantment> resolved, int totalWeight) {
    int roll = context.getRandom().nextInt(totalWeight);
    int cumulative = emptyWeight;
    if (roll >= cumulative) {
      for (ItemEntry item : items) {
        cumulative += item.weight();
        if (roll < cumulative) {
          int count = item.minCount() + (item.maxCount() > item.minCount() ? context.getRandom().nextInt(item.maxCount() - item.minCount() + 1) : 0);
          return new ItemStack(item.item(), count);
        }
      }
      for (ResolvedEnchantment enchantment : resolved) {
        cumulative += enchantment.weight();
        if (roll < cumulative) {
          int maxLevel = enchantment.holder().value().getMaxLevel();
          return EnchantmentHelper.createBook(new EnchantmentInstance(enchantment.holder(), maxLevel > 1 ? 1 + context.getRandom().nextInt(maxLevel) : 1));
        }
      }
    }
    return ItemStack.EMPTY;
  }

  @NotNull
  @Override
  public MapCodec<? extends IGlobalLootModifier> codec() {
    return CODEC;
  }

  /**
   * An item entry in the weighted pool.
   *
   * @param item item to add.
   * @param weight weight for this entry.
   * @param minCount minimum item count (inclusive).
   * @param maxCount maximum item count (inclusive).
   */
  private record ItemEntry(Holder<Item> item, int weight, int minCount, int maxCount) {
    /**
     * {@link Codec}.
     */
    public static final Codec<ItemEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("item").forGetter(ItemEntry::item),
      Codec.INT.optionalFieldOf("weight", 1).forGetter(ItemEntry::weight),
      Codec.INT.optionalFieldOf("min_count", 1).forGetter(ItemEntry::minCount),
      Codec.INT.optionalFieldOf("max_count", 1).forGetter(ItemEntry::maxCount)
    ).apply(instance, ItemEntry::new));
  }

  /**
   * An enchanted book entry in the weighted pool.
   *
   * @param enchantment {@link Identifier} of the enchantment.
   * @param weight weight for this entry.
   */
  private record EnchantmentEntry(Identifier enchantment, int weight) {
    /**
     * {@link Codec}.
     */
    public static final Codec<EnchantmentEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Identifier.CODEC.fieldOf("enchantment").forGetter(EnchantmentEntry::enchantment),
      Codec.INT.optionalFieldOf("weight", 1).forGetter(EnchantmentEntry::weight)
    ).apply(instance, EnchantmentEntry::new));
  }

  /**
   * A resolved enchantment entry with a valid {@link Holder}.
   *
   * @param holder resolved enchantment holder.
   * @param weight weight for this entry.
   */
  private record ResolvedEnchantment(Holder<Enchantment> holder, int weight) {}
}
