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
import org.r7l.interim.model.Resident;
import org.r7l.interim.model.War;

import java.util.*;

/**
 * GUI showing war statistics and leaderboards
 */
public class WarStatsGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    
    public WarStatsGUI(Interim plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 54, 
            ChatColor.GOLD + "War Statistics");
        
        buildMenu();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu() {
        // Global stats
        List<War> allWars = new ArrayList<>();
        allWars.addAll(plugin.getDataManager().getActiveWars());
        allWars.addAll(plugin.getDataManager().getPastWars());
        
        int totalWars = allWars.size();
        int activeWars = plugin.getDataManager().getActiveWars().size();
        int totalKills = allWars.stream().mapToInt(War::getTotalKills).sum();
        int totalTownsCaptured = allWars.stream().mapToInt(w -> w.getCapturedTowns().size()).sum();
        
        List<String> globalLore = new ArrayList<>();
        globalLore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
        globalLore.add(ChatColor.GRAY + "Total Wars: " + ChatColor.WHITE + totalWars);
        globalLore.add(ChatColor.GRAY + "Active Wars: " + ChatColor.RED + activeWars);
        globalLore.add(ChatColor.GRAY + "Total Kills: " + ChatColor.YELLOW + totalKills);
        globalLore.add(ChatColor.GRAY + "Towns Captured: " + ChatColor.GOLD + totalTownsCaptured);
        globalLore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
        
        inventory.setItem(4, createItem(Material.NETHER_STAR, 
            ChatColor.GOLD + "Global War Statistics", globalLore));
        
        // Top warrior (most kills across all wars)
        Map<UUID, Integer> totalPlayerKills = new HashMap<>();
        for (War war : allWars) {
            war.getPlayerKills().forEach((uuid, kills) -> 
                totalPlayerKills.merge(uuid, kills, Integer::sum));
        }
        
        List<String> topWarriorLore = new ArrayList<>();
        topWarriorLore.add(ChatColor.GRAY + "Most Kills All-Time:");
        topWarriorLore.add("");
        
        totalPlayerKills.entrySet().stream()
            .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
            .limit(5)
            .forEach(entry -> {
                Resident resident = plugin.getDataManager().getResident(entry.getKey());
                if (resident != null) {
                    topWarriorLore.add(ChatColor.YELLOW + "⚔ " + ChatColor.WHITE + 
                        resident.getName() + ChatColor.GRAY + " - " + ChatColor.RED + entry.getValue());
                }
            });
        
        inventory.setItem(19, createItem(Material.NETHERITE_SWORD, 
            ChatColor.RED + "Top Warriors", topWarriorLore));
        
        // Most victorious nation
        Map<UUID, Integer> nationVictories = new HashMap<>();
        for (War war : plugin.getDataManager().getPastWars()) {
            UUID winner = war.getWinner();
            if (winner != null) {
                nationVictories.merge(winner, 1, Integer::sum);
            }
        }
        
        List<String> topNationLore = new ArrayList<>();
        topNationLore.add(ChatColor.GRAY + "Most Victories:");
        topNationLore.add("");
        
        nationVictories.entrySet().stream()
            .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
            .limit(5)
            .forEach(entry -> {
                Nation nation = plugin.getDataManager().getNation(entry.getKey());
                if (nation != null) {
                    topNationLore.add(ChatColor.GOLD + "★ " + ChatColor.WHITE + 
                        nation.getName() + ChatColor.GRAY + " - " + ChatColor.GREEN + entry.getValue());
                }
            });
        
        inventory.setItem(21, createItem(Material.GOLDEN_SWORD, 
            ChatColor.GOLD + "Victorious Nations", topNationLore));
        
        // Most wars participated
        Map<UUID, Integer> nationParticipation = new HashMap<>();
        for (War war : allWars) {
            nationParticipation.merge(war.getAttackerNation(), 1, Integer::sum);
            nationParticipation.merge(war.getDefenderNation(), 1, Integer::sum);
        }
        
        List<String> participationLore = new ArrayList<>();
        participationLore.add(ChatColor.GRAY + "Most Wars Fought:");
        participationLore.add("");
        
        nationParticipation.entrySet().stream()
            .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
            .limit(5)
            .forEach(entry -> {
                Nation nation = plugin.getDataManager().getNation(entry.getKey());
                if (nation != null) {
                    participationLore.add(ChatColor.RED + "⚔ " + ChatColor.WHITE + 
                        nation.getName() + ChatColor.GRAY + " - " + ChatColor.YELLOW + entry.getValue());
                }
            });
        
        inventory.setItem(23, createItem(Material.IRON_SWORD, 
            ChatColor.RED + "Most Active Nations", participationLore));
        
        // Your personal stats
        Resident resident = plugin.getDataManager().getResident(player.getUniqueId());
        if (resident != null) {
            int yourKills = totalPlayerKills.getOrDefault(player.getUniqueId(), 0);
            int yourDeaths = 0;
            for (War war : allWars) {
                yourDeaths += war.getDeaths(player.getUniqueId());
            }
            
            int yourWars = 0;
            if (resident.hasNation()) {
                Nation nation = resident.getNation();
                yourWars = (int) allWars.stream()
                    .filter(w -> w.isParticipant(nation.getUuid()))
                    .count();
            }
            
            List<String> yourLore = new ArrayList<>();
            yourLore.add(ChatColor.GRAY + "Your War Record:");
            yourLore.add("");
            yourLore.add(ChatColor.GRAY + "Total Kills: " + ChatColor.GREEN + yourKills);
            yourLore.add(ChatColor.GRAY + "Total Deaths: " + ChatColor.RED + yourDeaths);
            if (yourDeaths > 0) {
                double kdr = (double) yourKills / yourDeaths;
                yourLore.add(ChatColor.GRAY + "K/D Ratio: " + ChatColor.YELLOW + String.format("%.2f", kdr));
            }
            yourLore.add(ChatColor.GRAY + "Wars Participated: " + ChatColor.AQUA + yourWars);
            
            inventory.setItem(25, createItem(Material.PLAYER_HEAD, 
                ChatColor.AQUA + "Your Statistics", yourLore));
        }
        
        // War goals breakdown
        Map<String, Integer> goalStats = new HashMap<>();
        for (War war : allWars) {
            goalStats.merge(war.getWarGoal(), 1, Integer::sum);
        }
        
        List<String> goalLore = new ArrayList<>();
        goalLore.add(ChatColor.GRAY + "Popular War Goals:");
        goalLore.add("");
        
        goalStats.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .forEach(entry -> {
                goalLore.add(ChatColor.YELLOW + "• " + ChatColor.WHITE + 
                    entry.getKey() + ChatColor.GRAY + " (" + entry.getValue() + ")");
            });
        
        inventory.setItem(40, createItem(Material.BEACON, 
            ChatColor.LIGHT_PURPLE + "War Goals", goalLore));
        
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
        if (slot == 49) {
            player.closeInventory();
            new WarMenuGUI(plugin, player).open();
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
