package org.r7l.interim.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.r7l.interim.gui.ClaimMapGUI;

/**
 * Handles interactions with GUI inventories
 */
public class GUIListener implements Listener {
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        InventoryHolder holder = event.getInventory().getHolder();
        
        if (holder instanceof ClaimMapGUI) {
            event.setCancelled(true); // Prevent item pickup
            
            if (event.getCurrentItem() == null) {
                return;
            }
            
            Player player = (Player) event.getWhoClicked();
            ClaimMapGUI gui = (ClaimMapGUI) holder;
            
            // Handle the click
            gui.handleClick(player, event.getRawSlot());
        }
    }
}
