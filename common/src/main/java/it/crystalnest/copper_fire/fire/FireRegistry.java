package it.crystalnest.copper_fire.fire;

import it.crystalnest.cobweb.api.registry.CobwebEntry;
import it.crystalnest.prometheus.api.Fire;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.FireRegistrar;
import it.crystalnest.prometheus.api.block.CustomFireBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fire registry.
 */
public final class FireRegistry {
  /**
   * Fire type of Copper Fire.
   */
  @ApiStatus.Internal
  public static final ResourceLocation COPPER_FIRE_TYPE = ResourceLocation.withDefaultNamespace("copper");

  static {
    // Vanilla Backport registers these under minecraft. Store IDs so NeoForge's deferred
    // registries do not need to be resolved during mod construction.
    Fire.Builder builder = FireManager.fireBuilder(COPPER_FIRE_TYPE)
      .setDefaultComponents()
      .removeComponents(Fire.Component.LANTERN_BLOCK, Fire.Component.LANTERN_ITEM)
      .setComponent(Fire.Component.FLAME_PARTICLE, ResourceLocation.withDefaultNamespace("copper_fire_flame"))
      .setLight(14)
      .setDamage(1.5F)
      .setCanRainDouse(true);
    for (String oxidation : new String[] { "", "exposed_", "weathered_", "oxidized_" }) {
      for (String waxed : new String[] { "", "waxed_" }) {
        ResourceLocation lantern = ResourceLocation.withDefaultNamespace(waxed + oxidation + "copper_lantern");
        builder.addToComponent(Fire.Component.LANTERN_BLOCK, lantern);
        builder.addToComponent(Fire.Component.LANTERN_ITEM, lantern);
      }
    }
    FireManager.registerFire(builder.build());
    FireRegistrar.registerFireSource(COPPER_FIRE_TYPE, MapColor.COLOR_GREEN, CustomFireBlock::new);
    FireRegistrar.registerDefaultFireComponents(COPPER_FIRE_TYPE, Fire.Component.CAMPFIRE_BLOCK, Fire.Component.CAMPFIRE_ITEM);
  }

  /**
   * {@link CobwebEntry} for Copper Fire Charge.
   */
  public static final CobwebEntry<FireChargeItem> COPPER_FIRE_CHARGE = FireRegistrar.registerFireCharge(COPPER_FIRE_TYPE);

  private FireRegistry() {}

  /**
   * Called outside to load the class and register.
   */
  public static void register() {}
}
