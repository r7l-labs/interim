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
import java.util.concurrent.TimeUnit;

/**
 * GUI showing active wars for a nation
 */
public class ActiveWarsGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    private final Nation nation;
    
    public ActiveWarsGUI(Interim plugin, Player player, Nation nation) {
        this.plugin = plugin;
        this.player = player;
        this.nation = nation;
        this.inventory = Bukkit.createInventory(this, 54, 
            ChatColor.RED + "Active Wars");
        
        buildMenu();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu() {
        List<War> activeWars = plugin.getDataManager().getActiveWars().stream()
            .filter(w -> w.isParticipant(nation.getUuid()))
            .toList();
        
        if (activeWars.isEmpty()) {
            inventory.setItem(22, createItem(Material.WHITE_BANNER, 
                ChatColor.GRAY + "No Active Wars",
                List.of(ChatColor.GRAY + "Your nation is at peace")));
        } else {
            for (int i = 0; i < Math.min(activeWars.size(), 45); i++) {
                War war = activeWars.get(i);
                inventory.setItem(i, createWarItem(war));
            }
        }
        
        // Back button
        inventory.setItem(49, createItem(Material.ARROW, ChatColor.YELLOW + "← Back", null));
    }
    
    private ItemStack createWarItem(War war) {
        Nation opponent = plugin.getDataManager().getNation(war.getOpponent(nation.getUuid()));
        boolean isAttacker = war.getAttackerNation().equals(nation.getUuid());
        
        int ourPoints = isAttacker ? war.getAttackerPoints() : war.getDefenderPoints();
        int theirPoints = isAttacker ? war.getDefenderPoints() : war.getAttackerPoints();
        
        long durationMs = war.getDuration();
        long days = TimeUnit.MILLISECONDS.toDays(durationMs);
        long hours = TimeUnit.MILLISECONDS.toHours(durationMs) % 24;
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
        lore.add(ChatColor.GRAY + "Opponent: " + ChatColor.RED + (opponent != null ? opponent.getName() : "Unknown"));
        lore.add(ChatColor.GRAY + "War Goal: " + ChatColor.YELLOW + war.getWarGoal());
        lore.add("");
        lore.add(ChatColor.GRAY + "Your Points: " + ChatColor.GREEN + ourPoints);
        lore.add(ChatColor.GRAY + "Their Points: " + ChatColor.RED + theirPoints);
        lore.add("");
        lore.add(ChatColor.GRAY + "Duration: " + ChatColor.WHITE + days + "d " + hours + "h");
        lore.add(ChatColor.GRAY + "Total Kills: " + ChatColor.YELLOW + war.getTotalKills());
        lore.add(ChatColor.GRAY + "Captured Towns: " + ChatColor.GOLD + war.getCapturedTowns().size());
        lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
        lore.add("");
        lore.add(ChatColor.YELLOW + "Click for details!");
        
        Material material = ourPoints > theirPoints ? Material.LIME_BANNER : 
                           ourPoints < theirPoints ? Material.RED_BANNER : Material.YELLOW_BANNER;
        
        String status = ourPoints > theirPoints ? ChatColor.GREEN + "⬆ Winning" : 
                       ourPoints < theirPoints ? ChatColor.RED + "⬇ Losing" : ChatColor.YELLOW + "⬌ Tied";
        
        return createItem(material, status + ChatColor.GRAY + " vs " + 
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
            return;
        }
        
        List<War> activeWars = plugin.getDataManager().getActiveWars().stream()
            .filter(w -> w.isParticipant(nation.getUuid()))
            .toList();
        
        if (slot < activeWars.size()) {
            War war = activeWars.get(slot);
            player.closeInventory();
            new WarDetailGUI(plugin, player, nation, war).open();
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
