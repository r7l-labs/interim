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
 * GUI for selecting war goal when declaring war
 */
public class WarGoalGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    private final Nation attacker;
    private final Nation defender;
    
    public WarGoalGUI(Interim plugin, Player player, Nation attacker, Nation defender) {
        this.plugin = plugin;
        this.player = player;
        this.attacker = attacker;
        this.defender = defender;
        this.inventory = Bukkit.createInventory(this, 27, 
            ChatColor.DARK_RED + "Select War Goal");
        
        buildMenu();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu() {
        // Territory Conquest
        inventory.setItem(10, createItem(Material.GRASS_BLOCK, 
            ChatColor.GREEN + "Territory Conquest",
            List.of(
                ChatColor.GRAY + "Goal: Capture enemy towns",
                ChatColor.GRAY + "Victory: Control 60% of towns",
                ChatColor.GRAY + "Bonus: +100 points per town",
                "",
                ChatColor.YELLOW + "Click to select!"
            )));
        
        // Economic Dominance
        inventory.setItem(11, createItem(Material.GOLD_INGOT, 
            ChatColor.GOLD + "Economic Dominance",
            List.of(
                ChatColor.GRAY + "Goal: Plunder enemy resources",
                ChatColor.GRAY + "Victory: Steal $50,000",
                ChatColor.GRAY + "Bonus: +50 points per $5000",
                "",
                ChatColor.YELLOW + "Click to select!"
            )));
        
        // Political Subjugation
        inventory.setItem(12, createItem(Material.NETHER_STAR, 
            ChatColor.LIGHT_PURPLE + "Political Subjugation",
            List.of(
                ChatColor.GRAY + "Goal: Force vassalization",
                ChatColor.GRAY + "Victory: Defeat capital mayor 10x",
                ChatColor.GRAY + "Bonus: Enemy becomes vassal",
                "",
                ChatColor.YELLOW + "Click to select!"
            )));
        
        // Resource Control
        inventory.setItem(13, createItem(Material.DIAMOND, 
            ChatColor.AQUA + "Resource Control",
            List.of(
                ChatColor.GRAY + "Goal: Control strategic points",
                ChatColor.GRAY + "Victory: Hold 5 key territories",
                ChatColor.GRAY + "Bonus: +20 points per hour held",
                "",
                ChatColor.YELLOW + "Click to select!"
            )));
        
        // Total War
        inventory.setItem(14, createItem(Material.TNT, 
            ChatColor.DARK_RED + "Total War",
            List.of(
                ChatColor.GRAY + "Goal: Complete annihilation",
                ChatColor.GRAY + "Victory: Reach 500 war points",
                ChatColor.GRAY + "Bonus: All kill/capture points x2",
                "",
                ChatColor.YELLOW + "Click to select!"
            )));
        
        // Target info
        List<String> targetLore = new ArrayList<>();
        targetLore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
        targetLore.add(ChatColor.GRAY + "Target: " + ChatColor.RED + defender.getName());
        targetLore.add(ChatColor.GRAY + "Towns: " + ChatColor.WHITE + defender.getTownCount());
        targetLore.add(ChatColor.GRAY + "Bank: " + ChatColor.GOLD + "$" + String.format("%.2f", defender.getBank()));
        targetLore.add("");
        targetLore.add(ChatColor.GRAY + "War Cost: " + ChatColor.GOLD + "$5000");
        targetLore.add(ChatColor.GRAY + "Daily Upkeep: " + ChatColor.GOLD + "$500");
        targetLore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
        
        inventory.setItem(4, createItem(Material.NETHERITE_SWORD, 
            ChatColor.RED + "War Against " + defender.getName(), targetLore));
        
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
            new DeclareWarGUI(plugin, player, attacker).open();
            return;
        }
        
        String warGoal = switch (slot) {
            case 10 -> "Territory Conquest";
            case 11 -> "Economic Dominance";
            case 12 -> "Political Subjugation";
            case 13 -> "Resource Control";
            case 14 -> "Total War";
            default -> null;
        };
        
        if (warGoal != null) {
            player.closeInventory();
            
            // Check if nation can afford war
            double warCost = plugin.getConfig().getDouble("war.declaration-cost", 5000.0);
            if (attacker.getBank() < warCost) {
                player.sendMessage(ChatColor.RED + "Your nation cannot afford to declare war!");
                player.sendMessage(ChatColor.RED + "Cost: " + ChatColor.GOLD + "$" + warCost);
                player.sendMessage(ChatColor.RED + "Bank: " + ChatColor.GOLD + "$" + String.format("%.2f", attacker.getBank()));
                return;
            }
            
            // Create war
            double wagerAmount = plugin.getConfig().getDouble("war.minimum-wager", 2000.0);
            War war = new War(attacker.getUuid(), defender.getUuid(), warGoal, wagerAmount);
            plugin.getDataManager().addWar(war);
            
            // Deduct cost
            attacker.withdraw(warCost);
            plugin.getDataManager().saveAll();
            
            // Add to enemies
            attacker.addEnemy(defender.getUuid());
            defender.addEnemy(attacker.getUuid());
            
            // Announce
            String announcement = ChatColor.DARK_RED + "⚔ WAR DECLARED! ⚔\n" +
                ChatColor.RED + attacker.getName() + ChatColor.GRAY + " has declared war on " +
                ChatColor.RED + defender.getName() + ChatColor.GRAY + "!\n" +
                ChatColor.YELLOW + "War Goal: " + ChatColor.WHITE + warGoal;
            
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(announcement);
            }
            
            player.sendMessage(ChatColor.GREEN + "War declared successfully!");
            new WarMenuGUI(plugin, player).open();
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
