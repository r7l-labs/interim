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
 * GUI for offering peace treaty
 */
public class PeaceTreatyGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    private final Nation nation;
    
    public PeaceTreatyGUI(Interim plugin, Player player, Nation nation) {
        this.plugin = plugin;
        this.player = player;
        this.nation = nation;
        this.inventory = Bukkit.createInventory(this, 27, 
            ChatColor.GREEN + "Offer Peace");
        
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
            inventory.setItem(13, createItem(Material.WHITE_BANNER, 
                ChatColor.GRAY + "No Active Wars",
                List.of(ChatColor.GRAY + "Your nation is at peace")));
        } else {
            for (int i = 0; i < Math.min(activeWars.size(), 18); i++) {
                War war = activeWars.get(i);
                inventory.setItem(i, createWarItem(war));
            }
        }
        
        // Back button
        inventory.setItem(22, createItem(Material.ARROW, ChatColor.YELLOW + "← Back", null));
    }
    
    private ItemStack createWarItem(War war) {
        Nation opponent = plugin.getDataManager().getNation(war.getOpponent(nation.getUuid()));
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Opponent: " + ChatColor.RED + (opponent != null ? opponent.getName() : "Unknown"));
        lore.add("");
        lore.add(ChatColor.GRAY + "Peace terms:");
        lore.add(ChatColor.YELLOW + "• Current war ends immediately");
        lore.add(ChatColor.YELLOW + "• 7-day non-aggression pact");
        lore.add(ChatColor.YELLOW + "• Each side keeps captured territory");
        lore.add("");
        lore.add(ChatColor.GREEN + "Click to offer peace!");
        
        return createItem(Material.WHITE_BANNER, 
            ChatColor.GREEN + "Peace Treaty", lore);
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
            new WarMenuGUI(plugin, player).open();
            return;
        }
        
        List<War> activeWars = plugin.getDataManager().getActiveWars().stream()
            .filter(w -> w.isParticipant(nation.getUuid()))
            .toList();
        
        if (slot < activeWars.size()) {
            War war = activeWars.get(slot);
            Nation opponent = plugin.getDataManager().getNation(war.getOpponent(nation.getUuid()));
            
            player.closeInventory();
            player.sendMessage(ChatColor.GREEN + "Peace offer sent to " + 
                ChatColor.WHITE + (opponent != null ? opponent.getName() : "Unknown") + 
                ChatColor.GREEN + "!");
            player.sendMessage(ChatColor.GRAY + "They must accept via their War Menu.");
            
            // TODO: Implement peace offer system with accept/reject
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
