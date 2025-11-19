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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Detailed view of a specific war
 */
public class WarDetailGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    private final Nation viewerNation;
    private final War war;
    
    public WarDetailGUI(Interim plugin, Player player, Nation viewerNation, War war) {
        this.plugin = plugin;
        this.player = player;
        this.viewerNation = viewerNation;
        this.war = war;
        this.inventory = Bukkit.createInventory(this, 54, 
            ChatColor.RED + "War Details");
        
        buildMenu();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu() {
        Nation attacker = plugin.getDataManager().getNation(war.getAttackerNation());
        Nation defender = plugin.getDataManager().getNation(war.getDefenderNation());
        boolean isAttacker = war.getAttackerNation().equals(viewerNation.getUuid());
        
        // War info header
        long durationMs = war.getDuration();
        long days = TimeUnit.MILLISECONDS.toDays(durationMs);
        long hours = TimeUnit.MILLISECONDS.toHours(durationMs) % 24;
        
        List<String> headerLore = new ArrayList<>();
        headerLore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
        headerLore.add(ChatColor.GRAY + "Attacker: " + ChatColor.RED + (attacker != null ? attacker.getName() : "Unknown"));
        headerLore.add(ChatColor.GRAY + "Defender: " + ChatColor.BLUE + (defender != null ? defender.getName() : "Unknown"));
        headerLore.add("");
        headerLore.add(ChatColor.GRAY + "War Goal: " + ChatColor.YELLOW + war.getWarGoal());
        headerLore.add(ChatColor.GRAY + "Wager: " + ChatColor.GOLD + "$" + String.format("%.2f", war.getWagerAmount()));
        headerLore.add(ChatColor.GRAY + "Duration: " + ChatColor.WHITE + days + "d " + hours + "h");
        headerLore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
        
        inventory.setItem(4, createItem(Material.NETHERITE_SWORD, 
            ChatColor.RED + "War Information", headerLore));
        
        // Score display
        List<String> attackerLore = new ArrayList<>();
        attackerLore.add(ChatColor.GRAY + "Points: " + ChatColor.GREEN + war.getAttackerPoints());
        attackerLore.add(ChatColor.GRAY + "Kills: " + ChatColor.YELLOW + getTeamKills(war.getAttackerNation(), war));
        attackerLore.add(ChatColor.GRAY + "Towns Captured: " + ChatColor.GOLD + war.getCapturedTowns().size());
        
        inventory.setItem(19, createItem(Material.RED_BANNER, 
            ChatColor.RED + (attacker != null ? attacker.getName() : "Unknown"), attackerLore));
        
        List<String> defenderLore = new ArrayList<>();
        defenderLore.add(ChatColor.GRAY + "Points: " + ChatColor.GREEN + war.getDefenderPoints());
        defenderLore.add(ChatColor.GRAY + "Kills: " + ChatColor.YELLOW + getTeamKills(war.getDefenderNation(), war));
        defenderLore.add(ChatColor.GRAY + "Defense Bonus: " + ChatColor.AQUA + "+10%");
        
        inventory.setItem(25, createItem(Material.BLUE_BANNER, 
            ChatColor.BLUE + (defender != null ? defender.getName() : "Unknown"), defenderLore));
        
        // Progress bar
        int totalPoints = war.getAttackerPoints() + war.getDefenderPoints();
        if (totalPoints > 0) {
            int attackerPercent = (war.getAttackerPoints() * 100) / totalPoints;
            inventory.setItem(21, createItem(Material.RED_STAINED_GLASS_PANE, 
                ChatColor.RED + String.valueOf(attackerPercent) + "%", null));
            inventory.setItem(22, createItem(Material.YELLOW_STAINED_GLASS_PANE, 
                ChatColor.YELLOW + "VS", null));
            inventory.setItem(23, createItem(Material.BLUE_STAINED_GLASS_PANE, 
                ChatColor.BLUE + String.valueOf(100 - attackerPercent) + "%", null));
        }
        
        // Top killers
        List<String> topKillersLore = new ArrayList<>();
        topKillersLore.add(ChatColor.GRAY + "Top 5 Warriors:");
        topKillersLore.add("");
        
        Map<UUID, Integer> kills = war.getPlayerKills();
        kills.entrySet().stream()
            .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
            .limit(5)
            .forEach(entry -> {
                Resident resident = plugin.getDataManager().getResident(entry.getKey());
                if (resident != null) {
                    topKillersLore.add(ChatColor.YELLOW + "⚔ " + ChatColor.WHITE + 
                        resident.getName() + ChatColor.GRAY + " - " + ChatColor.RED + entry.getValue());
                }
            });
        
        inventory.setItem(38, createItem(Material.IRON_SWORD, 
            ChatColor.GOLD + "Top Killers", topKillersLore));
        
        // Your stats
        int yourKills = war.getKills(player.getUniqueId());
        int yourDeaths = war.getDeaths(player.getUniqueId());
        
        List<String> yourStatsLore = new ArrayList<>();
        yourStatsLore.add(ChatColor.GRAY + "Your Performance:");
        yourStatsLore.add("");
        yourStatsLore.add(ChatColor.GRAY + "Kills: " + ChatColor.GREEN + yourKills);
        yourStatsLore.add(ChatColor.GRAY + "Deaths: " + ChatColor.RED + yourDeaths);
        if (yourDeaths > 0) {
            double kdr = (double) yourKills / yourDeaths;
            yourStatsLore.add(ChatColor.GRAY + "K/D Ratio: " + ChatColor.YELLOW + String.format("%.2f", kdr));
        }
        
        inventory.setItem(40, createItem(Material.PLAYER_HEAD, 
            ChatColor.AQUA + "Your Statistics", yourStatsLore));
        
        // War objectives
        List<String> objectivesLore = new ArrayList<>();
        objectivesLore.add(ChatColor.GRAY + "Victory Conditions:");
        objectivesLore.add("");
        
        switch (war.getWarGoal()) {
            case "Territory Conquest":
                objectivesLore.add(ChatColor.YELLOW + "• Capture 60% of enemy towns");
                objectivesLore.add(ChatColor.YELLOW + "• Or reach 500 war points");
                break;
            case "Economic Dominance":
                objectivesLore.add(ChatColor.YELLOW + "• Plunder $50,000");
                objectivesLore.add(ChatColor.YELLOW + "• Or reach 500 war points");
                break;
            case "Political Subjugation":
                objectivesLore.add(ChatColor.YELLOW + "• Defeat capital mayor 10x");
                objectivesLore.add(ChatColor.YELLOW + "• Or reach 500 war points");
                break;
            case "Resource Control":
                objectivesLore.add(ChatColor.YELLOW + "• Control 5 key territories");
                objectivesLore.add(ChatColor.YELLOW + "• Or reach 500 war points");
                break;
            case "Total War":
                objectivesLore.add(ChatColor.YELLOW + "• Reach 500 war points");
                break;
        }
        
        inventory.setItem(42, createItem(Material.BEACON, 
            ChatColor.LIGHT_PURPLE + "War Objectives", objectivesLore));
        
        // Back button
        inventory.setItem(49, createItem(Material.ARROW, ChatColor.YELLOW + "← Back", null));
    }
    
    private int getTeamKills(UUID nationId, War war) {
        Nation nation = plugin.getDataManager().getNation(nationId);
        if (nation == null) return 0;
        
        int kills = 0;
        for (UUID townId : nation.getTowns()) {
            org.r7l.interim.model.Town town = plugin.getDataManager().getTown(townId);
            if (town != null) {
                for (UUID residentId : town.getResidents()) {
                    kills += war.getKills(residentId);
                }
            }
        }
        return kills;
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
            new ActiveWarsGUI(plugin, player, viewerNation).open();
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
