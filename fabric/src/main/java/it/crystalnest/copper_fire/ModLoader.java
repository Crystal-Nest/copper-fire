package it.crystalnest.copper_fire;

import it.crystalnest.copper_fire.handler.CreativeModeTabEventsHandler;
import it.crystalnest.copper_fire.handler.LootTableEventsHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import org.jetbrains.annotations.ApiStatus;

/**
 * Mod loader.
 */
@ApiStatus.Internal
public final class ModLoader implements ModInitializer {
  @Override
  public void onInitialize() {
    CommonModLoader.init();
    LootTableEvents.MODIFY.register(LootTableEventsHandler::handle);
    ItemGroupEvents.MODIFY_ENTRIES_ALL.register(CreativeModeTabEventsHandler::handle);
  }
}
