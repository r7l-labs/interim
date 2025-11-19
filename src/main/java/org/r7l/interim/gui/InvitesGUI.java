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
import org.r7l.interim.model.Invite;

import java.util.ArrayList;
import java.util.List;

/**
 * Invites GUI
 */
public class InvitesGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    
    public InvitesGUI(Interim plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 27, 
            ChatColor.LIGHT_PURPLE + "✦ Your Invites ✦");
        
        buildMenu();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu() {
        // Get invites from DataManager's pending invites
        // For now, show a placeholder since the invite system needs to be integrated
        inventory.setItem(13, createItem(Material.PAPER, 
            ChatColor.YELLOW + "Invite System",
            List.of(
                ChatColor.GRAY + "Check /town invites",
                ChatColor.GRAY + "for pending invitations"
            )));
        
        // Back button
        inventory.setItem(22, createItem(Material.ARROW, ChatColor.YELLOW + "← Back", null));
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
    
    public void handleClick(Player player, int slot, boolean isLeftClick) {
        if (slot == 22) {
            player.closeInventory();
            new MainMenuGUI(plugin, player).open();
            return;
        }
        
        // Simplified - just close and tell user to use commands
        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.WHITE + "/town join <town>" + 
            ChatColor.YELLOW + " to accept invitations");
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
