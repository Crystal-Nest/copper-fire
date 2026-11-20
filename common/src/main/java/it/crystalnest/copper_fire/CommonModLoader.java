package it.crystalnest.copper_fire;
import it.crystalnest.cobweb.api.pack.DataPack;
import it.crystalnest.cobweb.api.pack.query.PackActivation;
import it.crystalnest.copper_fire.fire.FireRegistry;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

/**
 * Common mod loader.
 */
@ApiStatus.Internal
public final class CommonModLoader {
  private CommonModLoader() {}

  /**
   * Initialize common operations across loaders.
   */
  public static void init() {
    FireRegistry.register();
    DataPack.registrar(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "enchantments"))
      .activation(PackActivation.DEFAULT_ENABLED)
      .register();
  }
}
