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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * GUI showing war history
 */
public class WarHistoryGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    private final Nation nation;
    
    public WarHistoryGUI(Interim plugin, Player player, Nation nation) {
        this.plugin = plugin;
        this.player = player;
        this.nation = nation;
        this.inventory = Bukkit.createInventory(this, 54, 
            ChatColor.GOLD + "War History");
        
        buildMenu();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu() {
        List<War> pastWars = plugin.getDataManager().getPastWars().stream()
            .filter(w -> w.isParticipant(nation.getUuid()))
            .toList();
        
        if (pastWars.isEmpty()) {
            inventory.setItem(22, createItem(Material.BOOK, 
                ChatColor.GRAY + "No War History",
                List.of(ChatColor.GRAY + "Your nation has no past wars")));
        } else {
            for (int i = 0; i < Math.min(pastWars.size(), 45); i++) {
                War war = pastWars.get(i);
                inventory.setItem(i, createWarHistoryItem(war));
            }
        }
        
        // Back button
        inventory.setItem(49, createItem(Material.ARROW, ChatColor.YELLOW + "← Back", null));
    }
    
    private ItemStack createWarHistoryItem(War war) {
        Nation opponent = plugin.getDataManager().getNation(war.getOpponent(nation.getUuid()));
        UUID winner = war.getWinner();
        boolean weWon = winner != null && winner.equals(nation.getUuid());
        
        long durationMs = war.getDuration();
        long days = TimeUnit.MILLISECONDS.toDays(durationMs);
        
        boolean isAttacker = war.getAttackerNation().equals(nation.getUuid());
        int ourPoints = isAttacker ? war.getAttackerPoints() : war.getDefenderPoints();
        int theirPoints = isAttacker ? war.getDefenderPoints() : war.getAttackerPoints();
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
        lore.add(ChatColor.GRAY + "Opponent: " + ChatColor.RED + (opponent != null ? opponent.getName() : "Unknown"));
        lore.add(ChatColor.GRAY + "War Goal: " + ChatColor.YELLOW + war.getWarGoal());
        lore.add("");
        lore.add(ChatColor.GRAY + "Result: " + (weWon ? ChatColor.GREEN + "Victory!" : ChatColor.RED + "Defeat"));
        lore.add(ChatColor.GRAY + "Final Score: " + ChatColor.WHITE + ourPoints + " - " + theirPoints);
        lore.add(ChatColor.GRAY + "Duration: " + ChatColor.WHITE + days + " days");
        lore.add(ChatColor.GRAY + "Total Kills: " + ChatColor.YELLOW + war.getTotalKills());
        lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
        
        Material material = weWon ? Material.EMERALD : Material.REDSTONE;
        String prefix = weWon ? ChatColor.GREEN + "✓ Victory" : ChatColor.RED + "✗ Defeat";
        
        return createItem(material, prefix + ChatColor.GRAY + " vs " + 
            ChatColor.RED + (opponent != null ? opponent.getName() : "Unknown"), lore);
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
