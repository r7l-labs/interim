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
 * Nation list browser GUI
 */
public class NationListGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    private final int page;
    private static final int PAGE_SIZE = 45;
    
    public NationListGUI(Interim plugin, Player player, int page) {
        this.plugin = plugin;
        this.player = player;
        this.page = page;
        this.inventory = Bukkit.createInventory(this, 54, 
            ChatColor.GOLD + "Nations - Page " + (page + 1));
        
        buildMenu();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu() {
        List<Nation> nations = new ArrayList<>(plugin.getDataManager().getNations());
        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, nations.size());
        
        for (int i = start; i < end; i++) {
            Nation nation = nations.get(i);
            inventory.setItem(i - start, createNationItem(nation));
        }
        
        // Navigation
        if (page > 0) {
            inventory.setItem(45, createItem(Material.ARROW, ChatColor.YELLOW + "← Previous Page", null));
        }
        
        if (end < nations.size()) {
            inventory.setItem(53, createItem(Material.ARROW, ChatColor.YELLOW + "Next Page →", null));
        }
        
        // Back button
        inventory.setItem(49, createItem(Material.BARRIER, ChatColor.RED + "✗ Close", null));
    }
    
    private ItemStack createNationItem(Nation nation) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Towns: " + ChatColor.WHITE + nation.getTownCount());
        lore.add(ChatColor.GRAY + "Allies: " + ChatColor.GREEN + nation.getAllies().size());
        lore.add(ChatColor.GRAY + "Enemies: " + ChatColor.RED + nation.getEnemies().size());
        lore.add("");
        lore.add(ChatColor.YELLOW + "Click for more info!");
        
        return createItem(Material.NETHER_STAR, ChatColor.GOLD + nation.getName(), lore);
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
            new MainMenuGUI(plugin, player).open();
            return;
        }
        
        if (slot == 45 && page > 0) {
            player.closeInventory();
            new NationListGUI(plugin, player, page - 1).open();
            return;
        }
        
        List<Nation> nations = new ArrayList<>(plugin.getDataManager().getNations());
        if (slot == 53 && (page + 1) * PAGE_SIZE < nations.size()) {
            player.closeInventory();
            new NationListGUI(plugin, player, page + 1).open();
            return;
        }
        
        int index = page * PAGE_SIZE + slot;
        if (index < nations.size()) {
            Nation nation = nations.get(index);
            player.closeInventory();
            player.performCommand("nation info " + nation.getName());
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
