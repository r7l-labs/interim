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
 * GUI for selecting the target town when declaring a (kill) war
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
        this.inventory = Bukkit.createInventory(this, 54,
            ChatColor.DARK_RED + "Select Target Town");
        
        buildMenu();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu() {
        // Header / info
        List<String> targetLore = new ArrayList<>();
        targetLore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
        targetLore.add(ChatColor.GRAY + "Declare a kill-only war against a specific town of:");
        targetLore.add(ChatColor.RED + defender.getName());
        targetLore.add("");
        targetLore.add(ChatColor.GRAY + "War Cost: " + ChatColor.GOLD + "$" + plugin.getConfig().getDouble("war.declaration-cost", 5000.0));
        targetLore.add(ChatColor.GRAY + "Points to Win: " + ChatColor.YELLOW + plugin.getConfig().getInt("war.points-to-win", 500));
        targetLore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");

        inventory.setItem(4, createItem(Material.NETHERITE_SWORD,
            ChatColor.RED + "War Against " + defender.getName(), targetLore));

        // List defender towns as targets
        int slot = 10;
        for (java.util.UUID townId : defender.getTowns()) {
            var town = plugin.getDataManager().getTown(townId);
            if (town == null) continue;
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Townspeople: " + ChatColor.WHITE + town.getResidentCount());
            lore.add(ChatColor.GRAY + "Claims: " + ChatColor.WHITE + town.getClaimCount());
            lore.add("");
            lore.add(ChatColor.YELLOW + "Click to declare war targeting this town");

            inventory.setItem(slot, createItem(Material.OAK_SIGN, ChatColor.RED + town.getName(), lore));
            slot++;
            if (slot >= 44) break;
        }

        // Back button
        inventory.setItem(49, createItem(Material.ARROW, ChatColor.YELLOW + "← Back", null));
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
        
        // If back clicked
        if (slot == 49) {
            player.closeInventory();
            new DeclareWarGUI(plugin, player, attacker).open();
            return;
        }

        // Town selection slots were placed starting at 10
        if (slot >= 10 && slot < 54) {
            int index = slot - 10;
            java.util.List<java.util.UUID> towns = new java.util.ArrayList<>(defender.getTowns());
            if (index < 0 || index >= towns.size()) return;
            java.util.UUID selectedTownId = towns.get(index);
            var town = plugin.getDataManager().getTown(selectedTownId);
            if (town == null) return;

            player.closeInventory();

            // Check cost
            double warCost = plugin.getConfig().getDouble("war.declaration-cost", 5000.0);
            if (attacker.getBank() < warCost) {
                player.sendMessage(ChatColor.RED + "Your nation cannot afford to declare war!");
                player.sendMessage(ChatColor.RED + "Cost: " + ChatColor.GOLD + "$" + warCost);
                player.sendMessage(ChatColor.RED + "Bank: " + ChatColor.GOLD + "$" + String.format("%.2f", attacker.getBank()));
                return;
            }

            double wagerAmount = plugin.getConfig().getDouble("war.minimum-wager", 2000.0);
            War war = new War(attacker.getUuid(), defender.getUuid(), "Kill War", wagerAmount);
            war.setTargetTown(town.getUuid());
            plugin.getDataManager().addWar(war);

            attacker.withdraw(warCost);
            attacker.addEnemy(defender.getUuid());
            defender.addEnemy(attacker.getUuid());
            plugin.getDataManager().saveAll();

            String announcement = ChatColor.DARK_RED + "⚔ WAR DECLARED! ⚔\n" +
                ChatColor.RED + attacker.getName() + ChatColor.GRAY + " has declared war on " +
                ChatColor.RED + defender.getName() + ChatColor.GRAY + " (target: " + ChatColor.YELLOW + town.getName() + ChatColor.GRAY + ")!\n" +
                ChatColor.YELLOW + "War Type: " + ChatColor.WHITE + "Kill War";

            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(announcement);
            }

            player.sendMessage(ChatColor.GREEN + "War declared successfully against town: " + town.getName());
            new WarMenuGUI(plugin, player).open();
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
