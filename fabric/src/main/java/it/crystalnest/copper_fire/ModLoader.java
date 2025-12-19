package it.crystalnest.copper_fire;

import it.crystalnest.copper_fire.handler.CreativeModeTabEventsHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import org.jetbrains.annotations.ApiStatus;

/**
 * Mod loader.
 */
@ApiStatus.Internal
public final class ModLoader implements ModInitializer {
  @Override
  public void onInitialize() {
    CommonModLoader.init();
    ItemGroupEvents.MODIFY_ENTRIES_ALL.register(CreativeModeTabEventsHandler::handle);
  }
}
