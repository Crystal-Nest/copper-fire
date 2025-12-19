package it.crystalnest.copper_fire.handler;

import it.crystalnest.copper_fire.Constants;
import it.crystalnest.prometheus.api.Fire;
import it.crystalnest.prometheus.api.FireManager;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

/**
 * Handler for creative mode tabs related events.
 */
@EventBusSubscriber(modid = Constants.MOD_ID)
public final class CreativeModeTabEventsHandler {
  /**
   * Handles the {@link BuildCreativeModeTabContentsEvent} event.<br>
   * Adds the copper campfire right after the normal campfire.
   *
   * @param event {@link BuildCreativeModeTabContentsEvent}.
   */
  @SubscribeEvent
  public static void handle(BuildCreativeModeTabContentsEvent event) {
    if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
      // noinspection DataFlowIssue: copper campfire is registered by this mod.
      event.insertAfter(
        Items.CAMPFIRE.getDefaultInstance(),
        FireManager.getComponent(FireManager.COPPER_FIRE_TYPE, Fire.Component.CAMPFIRE_ITEM).getDefaultInstance(),
        CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
      );
    }
  }
}
