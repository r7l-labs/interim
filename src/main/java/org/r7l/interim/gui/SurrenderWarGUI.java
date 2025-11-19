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
import org.r7l.interim.model.War;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI for surrendering in a war
 */
public class SurrenderWarGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    private final Nation nation;
    private final War war;
    
    public SurrenderWarGUI(Interim plugin, Player player, Nation nation, War war) {
        this.plugin = plugin;
        this.player = player;
        this.nation = nation;
        this.war = war;
        this.inventory = Bukkit.createInventory(this, 27, 
            ChatColor.RED + "Surrender War");
        
        buildMenu();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu() {
        Nation opponent = plugin.getDataManager().getNation(war.getOpponent(nation.getUuid()));
        
        List<String> warInfoLore = new ArrayList<>();
        warInfoLore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
        warInfoLore.add(ChatColor.GRAY + "Opponent: " + ChatColor.RED + (opponent != null ? opponent.getName() : "Unknown"));
        warInfoLore.add(ChatColor.GRAY + "War Goal: " + ChatColor.YELLOW + war.getWarGoal());
        warInfoLore.add("");
        warInfoLore.add(ChatColor.RED + "Surrender Consequences:");
        warInfoLore.add(ChatColor.GRAY + "• Lose all captured territory");
        warInfoLore.add(ChatColor.GRAY + "• Pay full war reparations");
        warInfoLore.add(ChatColor.GRAY + "• Lose wager amount: " + ChatColor.GOLD + "$" + String.format("%.2f", war.getWagerAmount()));
        warInfoLore.add(ChatColor.GRAY + "• 30-day cooldown for new wars");
        warInfoLore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
        
        inventory.setItem(4, createItem(Material.RED_BANNER, 
            ChatColor.RED + "Surrender Information", warInfoLore));
        
        // Confirm surrender
        inventory.setItem(11, createItem(Material.LIME_WOOL, 
            ChatColor.GREEN + "Confirm Surrender",
            List.of(
                ChatColor.GRAY + "Accept defeat and end the war",
                "",
                ChatColor.RED + "This cannot be undone!",
                ChatColor.YELLOW + "Click to confirm"
            )));
        
        // Cancel
        inventory.setItem(15, createItem(Material.RED_WOOL, 
            ChatColor.RED + "Cancel",
            List.of(
                ChatColor.GRAY + "Keep fighting!",
                "",
                ChatColor.YELLOW + "Click to go back"
            )));
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
            // Confirm surrender
            Nation opponent = plugin.getDataManager().getNation(war.getOpponent(nation.getUuid()));
            
            // End war
            war.setActive(false);
            war.setEndTime(System.currentTimeMillis());
            
            // Set winner points to ensure opponent wins
            boolean isAttacker = war.getAttackerNation().equals(nation.getUuid());
            if (isAttacker) {
                war.setDefenderPoints(war.getAttackerPoints() + 100);
            } else {
                war.setAttackerPoints(war.getDefenderPoints() + 100);
            }
            
            // Pay reparations
            double reparations = war.getWagerAmount();
            if (nation.getBank() >= reparations) {
                nation.withdraw(reparations);
                if (opponent != null) {
                    opponent.deposit(reparations);
                }
            }
            
            // Remove enemy status
            nation.removeEnemy(war.getOpponent(nation.getUuid()));
            if (opponent != null) {
                opponent.removeEnemy(nation.getUuid());
            }
            
            plugin.getDataManager().saveAll();
            
            // Announce
            String announcement = ChatColor.RED + "⚔ WAR ENDED ⚔\n" +
                ChatColor.WHITE + nation.getName() + ChatColor.GRAY + " has surrendered to " +
                ChatColor.WHITE + (opponent != null ? opponent.getName() : "Unknown") + ChatColor.GRAY + "!";
            
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(announcement);
            }
            
            player.sendMessage(ChatColor.RED + "Your nation has surrendered.");
            
        } else if (slot == 15) {
            // Cancel
            new WarMenuGUI(plugin, player).open();
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
