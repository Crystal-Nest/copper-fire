package it.crystalnest.copper_fire.fire;

import it.crystalnest.prometheus.api.Fire;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.FireRegistrar;
import it.crystalnest.prometheus.api.block.CustomCampfireBlock;
import it.crystalnest.prometheus.api.block.CustomFireBlock;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fire registry.
 */
public final class FireRegistry {
  /**
   * Fire type of Copper Fire (from {@link FireManager#COPPER_FIRE_TYPE}).
   */
  @ApiStatus.Internal
  public static final ResourceLocation COPPER_FIRE_TYPE = FireManager.COPPER_FIRE_TYPE;

  static {
    // noinspection DataFlowIssue: key of COPPER_FIRE_FLAME is sure to be defined.
    Fire.Builder builder = FireManager.fireBuilder(COPPER_FIRE_TYPE)
      .setDefaultComponents()
      .removeComponents(Fire.Component.LANTERN_ITEM)
      .setComponent(Fire.Component.FLAME_PARTICLE, BuiltInRegistries.PARTICLE_TYPE.getKey(ParticleTypes.COPPER_FIRE_FLAME))
      .setComponent(Fire.Component.LANTERN_BLOCK, Blocks.COPPER_LANTERN.asList().stream().map(BuiltInRegistries.BLOCK::getKey).toArray(ResourceLocation[]::new))
      .setLight(14)
      .setDamage(1.5F)
      .setCanRainDouse(true);
    Items.COPPER_LANTERN.forEach(item -> builder.addToComponent(Fire.Component.LANTERN_ITEM, BuiltInRegistries.ITEM.getKey(item)));
    FireManager.registerFire(builder.build());

    FireRegistrar.registerFireSource(COPPER_FIRE_TYPE, MapColor.COLOR_GREEN, CustomFireBlock::new);
    FireRegistrar.registerCampfire(COPPER_FIRE_TYPE, false, CustomCampfireBlock::new);
    FireRegistrar.registerCampfireItem(COPPER_FIRE_TYPE, BlockItem::new, new Item.Properties().component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
  }

  private FireRegistry() {}

  /**
   * Called outside to load the class and register.
   */
  public static void register() {}
}
