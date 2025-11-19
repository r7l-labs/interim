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
import org.r7l.interim.model.Nation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Nation relation management GUI (allies/enemies)
 */
public class NationRelationGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    private final Nation nation;
    private final String newRelation;
    private final String currentRelation;
    
    public NationRelationGUI(Interim plugin, Player player, Nation nation, String newRelation, String currentRelation) {
        this.plugin = plugin;
        this.player = player;
        this.nation = nation;
        this.newRelation = newRelation;
        this.currentRelation = currentRelation;
        
        String title = currentRelation.equals("ally") ? 
            ChatColor.RED + "Remove Ally" : ChatColor.GREEN + "End War";
        
        this.inventory = Bukkit.createInventory(this, 54, title);
        buildMenu();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu() {
        List<UUID> relationIds = currentRelation.equals("ally") ? 
            new ArrayList<>(nation.getAllies()) : new ArrayList<>(nation.getEnemies());
        
        for (int i = 0; i < Math.min(relationIds.size(), 45); i++) {
            Nation otherNation = plugin.getDataManager().getNation(relationIds.get(i));
            if (otherNation != null) {
                inventory.setItem(i, createNationItem(otherNation));
            }
        }
        
        if (relationIds.isEmpty()) {
            String message = currentRelation.equals("ally") ? 
                "No allies" : "No enemies";
            inventory.setItem(22, createItem(Material.BARRIER, 
                ChatColor.GRAY + message, null));
        }
        
        // Back button
        inventory.setItem(49, createItem(Material.ARROW, ChatColor.YELLOW + "← Back", null));
    }
    
    private ItemStack createNationItem(Nation otherNation) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Towns: " + ChatColor.WHITE + otherNation.getTownCount());
        lore.add("");
        
        if (currentRelation.equals("ally")) {
            lore.add(ChatColor.GREEN + "Current: Ally");
            lore.add(ChatColor.RED + "Click to remove alliance!");
        } else {
            lore.add(ChatColor.RED + "Current: Enemy");
            lore.add(ChatColor.GREEN + "Click to make peace!");
        }
        
        Material material = currentRelation.equals("ally") ? 
            Material.EMERALD : Material.REDSTONE;
        
        return createItem(material, ChatColor.GOLD + otherNation.getName(), lore);
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
        if (slot == 49) {
            player.closeInventory();
            new NationMenuGUI(plugin, player).open();
            return;
        }
        
        List<UUID> relationIds = currentRelation.equals("ally") ? 
            new ArrayList<>(nation.getAllies()) : new ArrayList<>(nation.getEnemies());
        
        if (slot < relationIds.size()) {
            Nation otherNation = plugin.getDataManager().getNation(relationIds.get(slot));
            if (otherNation == null) return;
            
            player.closeInventory();
            player.performCommand("nation " + newRelation + " " + otherNation.getName());
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
