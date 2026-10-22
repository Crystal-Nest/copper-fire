package it.crystalnest.copper_fire.mixin;

import com.blackgear.vanillabackport.common.level.entities.mob.animal.golem.copper_golem.CopperGolem;
import it.crystalnest.copper_fire.fire.FireRegistry;
import it.crystalnest.prometheus.api.type.FireTyped;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Makes Vanilla Backport's copper golems burn with copper fire.
 */
@Mixin(value = CopperGolem.class, remap = false)
public abstract class CopperGolemMixin implements FireTyped {
  @Override
  public ResourceLocation getFireType() {
    return FireRegistry.COPPER_FIRE_TYPE;
  }
}
