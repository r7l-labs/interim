package org.r7l.interim.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.r7l.interim.gui.*;

/**
 * Handles interactions with GUI inventories
 */
public class GUIListener implements Listener {
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        InventoryHolder holder = event.getInventory().getHolder();

        // Claim map GUI
        if (holder instanceof ClaimMapGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // Main menu GUI
        if (holder instanceof MainMenuGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // Town menu GUI
        if (holder instanceof TownMenuGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // Nation menu GUI
        if (holder instanceof NationMenuGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // Economy menu GUI
        if (holder instanceof EconomyMenuGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // Settings menu GUI
        if (holder instanceof SettingsMenuGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // Plot menu GUI
        if (holder instanceof PlotMenuGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // Confirm action GUI
        if (holder instanceof ConfirmActionGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // Town list GUI
        if (holder instanceof TownListGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // Nation list GUI
        if (holder instanceof NationListGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // Resident list GUI
        if (holder instanceof ResidentListGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // Resident info GUI
        if (holder instanceof ResidentInfoGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // Town switch GUI
        if (holder instanceof TownSwitchGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // Invites GUI
        if (holder instanceof InvitesGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot(), event.isLeftClick());
            return;
        }

        // Nation town list GUI
        if (holder instanceof NationTownListGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // Nation relation GUI
        if (holder instanceof NationRelationGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // War menu GUI
        if (holder instanceof WarMenuGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // Declare war GUI
        if (holder instanceof DeclareWarGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // War goal GUI
        if (holder instanceof WarGoalGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // Active wars GUI
        if (holder instanceof ActiveWarsGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // War detail GUI
        if (holder instanceof WarDetailGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // War history GUI
        if (holder instanceof WarHistoryGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // War stats GUI
        if (holder instanceof WarStatsGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // Peace treaty GUI
        if (holder instanceof PeaceTreatyGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // Surrender war GUI
        if (holder instanceof SurrenderWarGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // Town/Nation overview GUI (legacy)
        if (holder instanceof TownNationOverviewGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }

        // Settings editor GUI (legacy)
        if (holder instanceof SettingsEditorGUI gui) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            gui.handleClick(player, event.getRawSlot());
            return;
        }
    }
}
