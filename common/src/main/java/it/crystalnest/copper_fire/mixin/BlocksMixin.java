package it.crystalnest.copper_fire.mixin;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.function.Function;

@Mixin(Blocks.class)
public class BlocksMixin {
  @ModifyArg(
    method = "<clinit>",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/WeatheringCopperBlocks;create(Ljava/lang/String;Lorg/apache/commons/lang3/function/TriFunction;Ljava/util/function/Function;Ljava/util/function/BiFunction;Ljava/util/function/Function;)Lnet/minecraft/world/level/block/WeatheringCopperBlocks;"),
    index = 4,
    slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=copper_lantern"))
  )
  private static Function<WeatheringCopper.WeatherState, BlockBehaviour.Properties> changeCopperLanternLightLevel(Function<WeatheringCopper.WeatherState, BlockBehaviour.Properties> original) {
    return weatherState -> original.apply(weatherState).lightLevel(state -> 14 - weatherState.ordinal());
  }
}
