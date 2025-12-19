package it.crystalnest.copper_fire.mixin;

import it.crystalnest.copper_fire.fire.FireRegistry;
import it.crystalnest.prometheus.api.type.FireTyped;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.coppergolem.CopperGolem;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Injects into {@link CopperGolem} to alter fire behavior for consistency.
 */
@Mixin(CopperGolem.class)
public abstract class CopperGolemMixin implements FireTyped {
  @Override
  public ResourceLocation getFireType() {
    return FireRegistry.COPPER_FIRE_TYPE;
  }
}
