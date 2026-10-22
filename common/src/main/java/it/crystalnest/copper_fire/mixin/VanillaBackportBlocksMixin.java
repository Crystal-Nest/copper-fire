package it.crystalnest.copper_fire.mixin;

import com.blackgear.vanillabackport.common.registries.blocks.ModBlocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.function.Function;

/**
 * Adjusts Vanilla Backport's copper lanterns before their block states cache light emission.
 */
@Mixin(value = ModBlocks.class, remap = false)
public abstract class VanillaBackportBlocksMixin {
  /**
   * Applies oxidation-dependent light levels to both unwaxed and waxed lanterns.
   *
   * @param original original block properties factory.
   * @return properties factory with the copper fire light level.
   */
  @ModifyArg(
    method = "<clinit>",
    at = @At(value = "INVOKE", target = "Lcom/blackgear/vanillabackport/common/level/blocks/WeatheringCopperBlocks;create(Ljava/lang/String;Lorg/apache/commons/lang3/function/TriFunction;Ljava/util/function/Function;Ljava/util/function/BiFunction;Ljava/util/function/Function;)Lcom/blackgear/vanillabackport/common/level/blocks/WeatheringCopperBlocks;", ordinal = 0),
    index = 4,
    slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=copper_lantern"))
  )
  private static Function<WeatheringCopper.WeatherState, BlockBehaviour.Properties> changeCopperLanternLightLevel(Function<WeatheringCopper.WeatherState, BlockBehaviour.Properties> original) {
    return weatherState -> original.apply(weatherState).lightLevel(state -> 14 - weatherState.ordinal());
  }
}
