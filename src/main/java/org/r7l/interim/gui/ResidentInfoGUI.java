package org.r7l.interim.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.r7l.interim.Interim;
import org.r7l.interim.model.Resident;
import org.r7l.interim.model.Town;

import java.util.ArrayList;
import java.util.List;

/**
 * Resident info GUI
 */
public class ResidentInfoGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    private final OfflinePlayer target;
    
    public ResidentInfoGUI(Interim plugin, Player player, OfflinePlayer target) {
        this.plugin = plugin;
        this.player = player;
        this.target = target;
        this.inventory = Bukkit.createInventory(this, 27, 
            ChatColor.AQUA + target.getName() + "'s Info");
        
        buildMenu();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu() {
        Resident resident = plugin.getDataManager().getResident(target.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            inventory.setItem(13, createItem(Material.BARRIER, ChatColor.RED + "No Town Data", 
                List.of(ChatColor.GRAY + "This player is not in a town")));
        } else {
            // Player info
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
            lore.add(ChatColor.GRAY + "Primary Town: " + ChatColor.GREEN + resident.getTown().getName());
            lore.add(ChatColor.GRAY + "Rank: " + ChatColor.AQUA + resident.getRank());
            lore.add(ChatColor.GRAY + "Towns: " + ChatColor.WHITE + resident.getTownCount());
            lore.add(ChatColor.GRAY + "Joined: " + ChatColor.WHITE + 
                new java.text.SimpleDateFormat("MMM dd, yyyy").format(new java.util.Date(resident.getJoinedTown())));
            
            if (resident.hasNation()) {
                lore.add(ChatColor.GRAY + "Nation: " + ChatColor.GOLD + resident.getNation().getName());
            }
            
            lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
            
            inventory.setItem(13, createItem(Material.PLAYER_HEAD, 
                ChatColor.AQUA + "⚡ " + target.getName(), lore));
        }
        
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
    
    public void handleClick(Player player, int slot) {
        if (slot == 22) {
            player.closeInventory();
            new MainMenuGUI(plugin, player).open();
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
