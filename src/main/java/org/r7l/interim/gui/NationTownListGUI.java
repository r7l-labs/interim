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
import org.r7l.interim.model.Town;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Nation town list GUI
 */
public class NationTownListGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    private final Nation nation;
    private final String action;
    
    public NationTownListGUI(Interim plugin, Player player, Nation nation, String action) {
        this.plugin = plugin;
        this.player = player;
        this.nation = nation;
        this.action = action;
        
        String title = switch (action) {
            case "kick" -> ChatColor.RED + "Kick Town";
            case "capital" -> ChatColor.YELLOW + "Set Capital";
            default -> ChatColor.AQUA + "Nation Towns";
        };
        
        this.inventory = Bukkit.createInventory(this, 54, title);
        buildMenu();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu() {
        List<UUID> townIds = new ArrayList<>(nation.getTowns());
        
        for (int i = 0; i < Math.min(townIds.size(), 45); i++) {
            Town town = plugin.getDataManager().getTown(townIds.get(i));
            if (town != null) {
                inventory.setItem(i, createTownItem(town));
            }
        }
        
        // Back button
        inventory.setItem(49, createItem(Material.ARROW, ChatColor.YELLOW + "← Back", null));
    }
    
    private ItemStack createTownItem(Town town) {
        List<String> lore = new ArrayList<>();
        Resident mayor = plugin.getDataManager().getResident(town.getMayor());
        String mayorName = mayor != null ? mayor.getName() : "Unknown";
        lore.add(ChatColor.GRAY + "Mayor: " + ChatColor.YELLOW + mayorName);
        lore.add(ChatColor.GRAY + "Residents: " + ChatColor.WHITE + town.getResidentCount());
        
        boolean isCapital = town.getUuid().equals(nation.getCapital());
        
        if (isCapital) {
            lore.add("");
            lore.add(ChatColor.GOLD + "⚑ Capital");
        }
        
        lore.add("");
        
        switch (action) {
            case "kick":
                if (!isCapital) {
                    lore.add(ChatColor.RED + "Click to kick!");
                } else {
                    lore.add(ChatColor.GRAY + "Cannot kick capital");
                }
                break;
            case "capital":
                if (!isCapital) {
                    lore.add(ChatColor.YELLOW + "Click to set as capital!");
                } else {
                    lore.add(ChatColor.GRAY + "Already capital");
                }
                break;
            default:
                lore.add(ChatColor.YELLOW + "Click for info!");
        }
        
        Material material = isCapital ? Material.NETHER_STAR : Material.OAK_SIGN;
        
        return createItem(material, ChatColor.GREEN + town.getName(), lore);
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
            new NationMenuGUI(plugin, player).open();
            return;
        }
        
        List<UUID> townIds = new ArrayList<>(nation.getTowns());
        
        if (slot < townIds.size()) {
            Town town = plugin.getDataManager().getTown(townIds.get(slot));
            if (town == null) return;
            
            boolean isCapital = town.getUuid().equals(nation.getCapital());
            player.closeInventory();
            
            switch (action) {
                case "kick":
                    if (!isCapital) {
                        player.performCommand("nation kick " + town.getName());
                    }
                    break;
                case "capital":
                    if (!isCapital) {
                        player.performCommand("nation capital " + town.getName());
                    }
                    break;
                case "view":
                    player.performCommand("town info " + town.getName());
                    break;
            }
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
