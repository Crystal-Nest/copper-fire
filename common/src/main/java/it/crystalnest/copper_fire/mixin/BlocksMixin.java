package it.crystalnest.copper_fire.mixin;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperCollection;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.apache.commons.lang3.function.TriFunction;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Injects into {@link Blocks} to alter fire behavior for consistency.
 */
@Mixin(Blocks.class)
public abstract class BlocksMixin {
  /**
   * Modifies the 5th argument of the method {@link WeatheringCopperCollection#registerBlocks(WeatheringCopperCollection, TriFunction, BiFunction, BiFunction, Function)} used to create the copper lanterns during static initialization.<br>
   * Changes the light level of each lantern based on their oxidation level ({@link WeatheringCopper.WeatherState}).
   *
   * @param original original {@link BlockBehaviour.Properties} setter.
   * @return modified {@link BlockBehaviour.Properties} setter.
   */
  @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/WeatheringCopperCollection;registerBlocks(Lnet/minecraft/world/level/block/WeatheringCopperCollection;Lorg/apache/commons/lang3/function/TriFunction;Ljava/util/function/BiFunction;Ljava/util/function/BiFunction;Ljava/util/function/Function;)Lnet/minecraft/world/level/block/WeatheringCopperCollection;"), index = 4, slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;COPPER_LANTERN:Lnet/minecraft/world/level/block/WeatheringCopperCollection;", opcode = Opcodes.GETSTATIC), to = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;COPPER_LANTERN:Lnet/minecraft/world/level/block/WeatheringCopperCollection;", opcode = Opcodes.PUTSTATIC)))
  private static Function<WeatheringCopper.WeatherState, BlockBehaviour.Properties> changeCopperLanternLightLevel(Function<WeatheringCopper.WeatherState, BlockBehaviour.Properties> original) {
    return weatherState -> original.apply(weatherState).lightLevel(state -> 14 - weatherState.ordinal());
  }
}
