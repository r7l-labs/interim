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
 * Town switcher GUI for players in multiple towns
 */
public class TownSwitchGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    
    public TownSwitchGUI(Interim plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 27, 
            ChatColor.GREEN + "Switch Primary Town");
        
        buildMenu();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu() {
        Resident resident = plugin.getDataManager().getResident(player.getUniqueId());
        
        if (resident == null) return;
        
        List<Town> towns = resident.getTownsList();
        for (int i = 0; i < Math.min(towns.size(), 21); i++) {
            Town town = towns.get(i);
            boolean isPrimary = town.getUuid().equals(resident.getPrimaryTown());
            inventory.setItem(i + 3, createTownItem(town, isPrimary));
        }
        
        // Back button
        inventory.setItem(22, createItem(Material.ARROW, ChatColor.YELLOW + "← Back", null));
    }
    
    private ItemStack createTownItem(Town town, boolean isPrimary) {
        List<String> lore = new ArrayList<>();
        Resident mayor = plugin.getDataManager().getResident(town.getMayor());
        String mayorName = mayor != null ? mayor.getName() : "Unknown";
        lore.add(ChatColor.GRAY + "Mayor: " + ChatColor.YELLOW + mayorName);
        lore.add(ChatColor.GRAY + "Residents: " + ChatColor.WHITE + town.getResidentCount());
        
        if (isPrimary) {
            lore.add("");
            lore.add(ChatColor.GREEN + "✓ Current Primary Town");
        } else {
            lore.add("");
            lore.add(ChatColor.YELLOW + "Click to make primary!");
        }
        
        Material material = isPrimary ? Material.LIME_STAINED_GLASS_PANE : Material.OAK_SIGN;
        ChatColor color = isPrimary ? ChatColor.GREEN : ChatColor.AQUA;
        
        return createItem(material, color + town.getName(), lore);
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
            new TownMenuGUI(plugin, player).open();
            return;
        }
        
        Resident resident = plugin.getDataManager().getResident(player.getUniqueId());
        if (resident == null) return;
        
        List<Town> towns = resident.getTownsList();
        int index = slot - 3;
        
        if (index >= 0 && index < towns.size()) {
            Town town = towns.get(index);
            if (!town.getUuid().equals(resident.getPrimaryTown())) {
                resident.setPrimaryTown(town.getUuid());
                plugin.getDataManager().saveAll();
                player.sendMessage(ChatColor.GREEN + "Primary town set to: " + ChatColor.YELLOW + town.getName());
                player.closeInventory();
                new TownMenuGUI(plugin, player).open();
            }
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
