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

/**
 * GUI for declaring war on another nation
 */
public class DeclareWarGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    private final Nation attackerNation;
    
    public DeclareWarGUI(Interim plugin, Player player, Nation attackerNation) {
        this.plugin = plugin;
        this.player = player;
        this.attackerNation = attackerNation;
        this.inventory = Bukkit.createInventory(this, 54, 
            ChatColor.DARK_RED + "⚔ Declare War");
        
        buildMenu();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu() {
        // Get all nations excluding own nation and current enemies/allies
        List<Nation> eligibleNations = new ArrayList<>();
        for (Nation nation : plugin.getDataManager().getNations()) {
            if (!nation.getUuid().equals(attackerNation.getUuid()) &&
                !attackerNation.isEnemy(nation.getUuid()) &&
                !attackerNation.isAlly(nation.getUuid())) {
                eligibleNations.add(nation);
            }
        }
        
        for (int i = 0; i < Math.min(eligibleNations.size(), 45); i++) {
            Nation target = eligibleNations.get(i);
            inventory.setItem(i, createNationItem(target));
        }
        
        if (eligibleNations.isEmpty()) {
            inventory.setItem(22, createItem(Material.BARRIER, 
                ChatColor.GRAY + "No Valid Targets",
                List.of(
                    ChatColor.GRAY + "No nations available",
                    ChatColor.GRAY + "to declare war on"
                )));
        }
        
        // Back button
        inventory.setItem(49, createItem(Material.ARROW, ChatColor.YELLOW + "← Back", null));
    }
    
    private ItemStack createNationItem(Nation nation) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Towns: " + ChatColor.WHITE + nation.getTownCount());
        lore.add(ChatColor.GRAY + "Allies: " + ChatColor.GREEN + nation.getAllyCount());
        lore.add("");
        lore.add(ChatColor.GRAY + "War Cost: " + ChatColor.GOLD + "$5000");
        lore.add(ChatColor.GRAY + "Minimum Wager: " + ChatColor.GOLD + "$2000");
        lore.add("");
        lore.add(ChatColor.RED + "Click to declare war!");
        
        return createItem(Material.IRON_SWORD, ChatColor.RED + nation.getName(), lore);
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
            new WarMenuGUI(plugin, player).open();
            return;
        }
        
        List<Nation> eligibleNations = new ArrayList<>();
        for (Nation nation : plugin.getDataManager().getNations()) {
            if (!nation.getUuid().equals(attackerNation.getUuid()) &&
                !attackerNation.isEnemy(nation.getUuid()) &&
                !attackerNation.isAlly(nation.getUuid())) {
                eligibleNations.add(nation);
            }
        }
        
        if (slot < eligibleNations.size()) {
            Nation target = eligibleNations.get(slot);
            player.closeInventory();
            
            // Open war goal selection
            new WarGoalGUI(plugin, player, attackerNation, target).open();
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
