package it.crystalnest.copper_fire.handler;

import it.crystalnest.copper_fire.fire.FireRegistry;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.Supplier;

/**
 * {@link LootTableEvents} handler.
 */
public final class LootTableEventsHandler {
  /**
   * {@link ResourceLocation} of the loot table for the dispensers in the chambers of the trial chambers.
   */
  private static final ResourceLocation DISPENSERS_TRIAL_CHAMBERS_CHAMBER_IDENTIFIER = ResourceLocation.withDefaultNamespace("dispensers/trial_chambers/chamber");

  /**
   * {@link ResourceLocation} of the loot table for the ominous-rare rewards in the trial chambers.
   */
  private static final ResourceLocation CHESTS_TRIAL_CHAMBERS_REWARD_OMINOUS_RARE_IDENTIFIER = ResourceLocation.withDefaultNamespace("chests/trial_chambers/reward_ominous_rare");

  /**
   * {@link ResourceLocation} of the loot table for the rare rewards in the trial chambers.
   */
  private static final ResourceLocation CHESTS_TRIAL_CHAMBERS_REWARD_RARE_IDENTIFIER = ResourceLocation.withDefaultNamespace("chests/trial_chambers/reward_rare");

  private LootTableEventsHandler() {}

  /**
   * Handles modifying Vanilla loot table to include Soul Flame enchantment.
   *
   * @param key loot table key.
   * @param builder builder of the loot table being loaded.
   * @param source loot table source.
   * @param provider holder reference provider.
   */
  public static void handle(ResourceKey<LootTable> key, LootTable.Builder builder, LootTableSource source, HolderLookup.Provider provider) {
    if (key.location().equals(DISPENSERS_TRIAL_CHAMBERS_CHAMBER_IDENTIFIER)) {
      builder.modifyPools(pool -> addItem(pool, FireRegistry.COPPER_FIRE_CHARGE, 3, UniformGenerator.between(4, 8)));
    } else if (key.location().equals(CHESTS_TRIAL_CHAMBERS_REWARD_OMINOUS_RARE_IDENTIFIER) || key.location().equals(CHESTS_TRIAL_CHAMBERS_REWARD_RARE_IDENTIFIER)) {
      builder.modifyPools(pool -> addEnchantment(provider, pool, "copper_fire_aspect", 1));
      builder.modifyPools(pool -> addEnchantment(provider, pool, "copper_flame", 1));
    }
  }

  /**
   * Adds the specified item to the given pool with the given weight and count.
   *
   * @param pool loot pool builder to which the item will be added.
   * @param item item to add.
   * @param weight weight for the item.
   * @param count item count.
   */
  private static void addItem(LootPool.Builder pool, Supplier<? extends Item> item, int weight, NumberProvider count) {
    pool.add(LootItem.lootTableItem(item.get()).setWeight(weight).apply(SetItemCountFunction.setCount(count)));
  }

  /**
   * Adds the specified enchantment book, if present in the game, to the given loot table with the given weight.
   *
   * @param provider registry provider.
   * @param pool loot pool builder to which the enchantment book will be added.
   * @param name enchantment ID.
   * @param weight weight for the enchanted book.
   */
  private static void addEnchantment(HolderLookup.Provider provider, LootPool.Builder pool, String name, int weight) {
    provider.lookupOrThrow(Registries.ENCHANTMENT).get(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.withDefaultNamespace(name))).ifPresent(enchantment -> pool.add(
      LootItem.lootTableItem(Items.BOOK).setWeight(weight).apply(new EnchantRandomlyFunction.Builder().withEnchantment(enchantment))
    ));
  }
}
