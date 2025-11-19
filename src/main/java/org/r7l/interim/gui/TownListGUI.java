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
import org.r7l.interim.model.Resident;
import org.r7l.interim.model.Town;

import java.util.ArrayList;
import java.util.List;

/**
 * Town list browser GUI
 */
public class TownListGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    private final int page;
    private static final int PAGE_SIZE = 45;
    
    public TownListGUI(Interim plugin, Player player, int page) {
        this.plugin = plugin;
        this.player = player;
        this.page = page;
        this.inventory = Bukkit.createInventory(this, 54, 
            ChatColor.GREEN + "Towns - Page " + (page + 1));
        
        buildMenu();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu() {
        List<Town> towns = new ArrayList<>(plugin.getDataManager().getTowns());
        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, towns.size());
        
        for (int i = start; i < end; i++) {
            Town town = towns.get(i);
            inventory.setItem(i - start, createTownItem(town));
        }
        
        // Navigation
        if (page > 0) {
            inventory.setItem(45, createItem(Material.ARROW, ChatColor.YELLOW + "← Previous Page", null));
        }
        
        if (end < towns.size()) {
            inventory.setItem(53, createItem(Material.ARROW, ChatColor.YELLOW + "Next Page →", null));
        }
        
        // Back button
        inventory.setItem(49, createItem(Material.BARRIER, ChatColor.RED + "✗ Close", null));
    }
    
    private ItemStack createTownItem(Town town) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Mayor: " + ChatColor.YELLOW + plugin.getDataManager().getResident(town.getMayor()).getName());
        lore.add(ChatColor.GRAY + "Residents: " + ChatColor.WHITE + town.getResidentCount());
        lore.add(ChatColor.GRAY + "Claims: " + ChatColor.WHITE + town.getClaimCount());
        
        if (town.hasNation()) {
            lore.add(ChatColor.GRAY + "Nation: " + ChatColor.GOLD + town.getNation().getName());
        }
        
        lore.add("");
        lore.add(ChatColor.YELLOW + "Click for more info!");
        
        return createItem(Material.OAK_SIGN, ChatColor.GREEN + town.getName(), lore);
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
            new TownListGUI(plugin, player, page - 1).open();
            return;
        }
        
        List<Town> towns = new ArrayList<>(plugin.getDataManager().getTowns());
        if (slot == 53 && (page + 1) * PAGE_SIZE < towns.size()) {
            player.closeInventory();
            new TownListGUI(plugin, player, page + 1).open();
            return;
        }
        
        int index = page * PAGE_SIZE + slot;
        if (index < towns.size()) {
            Town town = towns.get(index);
            player.closeInventory();
            player.performCommand("town info " + town.getName());
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
