package org.r7l.interim.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.r7l.interim.Interim;

import java.util.ArrayList;
import java.util.List;

/**
 * Confirmation dialog GUI
 */
public class ConfirmActionGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    private final Runnable onConfirm;
    private final Runnable onCancel;
    
    public ConfirmActionGUI(Interim plugin, Player player, String title, String message, 
                            Runnable onConfirm, Runnable onCancel) {
        this.plugin = plugin;
        this.player = player;
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
        this.inventory = Bukkit.createInventory(this, 27, 
            ChatColor.RED + "⚠ " + title);
        
        buildMenu(message);
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu(String message) {
        // Border
        ItemStack border = createItem(Material.GRAY_STAINED_GLASS_PANE, " ", null);
        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, border);
        }
        
        // Message
        inventory.setItem(13, createItem(Material.PAPER, ChatColor.YELLOW + "Confirmation", 
            List.of(ChatColor.GRAY + message)));
        
        // Confirm button
        inventory.setItem(11, createItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "✓ Confirm", 
            List.of(ChatColor.GRAY + "Click to confirm")));
        
        // Cancel button
        inventory.setItem(15, createItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "✗ Cancel", 
            List.of(ChatColor.GRAY + "Click to cancel")));
    }
    
    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null) {
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }
    
    public void handleClick(Player player, int slot) {
        player.closeInventory();
        
        if (slot == 11) {
            // Confirm
            if (onConfirm != null) {
                onConfirm.run();
            }
        } else if (slot == 15) {
            // Cancel
            if (onCancel != null) {
                onCancel.run();
            }
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
