package org.r7l.interim.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
import java.util.UUID;

/**
 * Resident list GUI for a town
 */
public class ResidentListGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    private final Town town;
    private final String action;
    
    public ResidentListGUI(Interim plugin, Player player, Town town, String action) {
        this.plugin = plugin;
        this.player = player;
        this.town = town;
        this.action = action;
        
        String title = switch (action) {
            case "kick" -> ChatColor.RED + "Kick Resident";
            case "promote" -> ChatColor.GREEN + "Promote Resident";
            case "demote" -> ChatColor.YELLOW + "Demote Resident";
            default -> ChatColor.AQUA + "Town Residents";
        };
        
        this.inventory = Bukkit.createInventory(this, 54, title);
        buildMenu();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu() {
        List<Resident> residents = new ArrayList<>();
        for (UUID residentId : town.getResidents()) {
            Resident resident = plugin.getDataManager().getResident(residentId);
            if (resident != null) {
                residents.add(resident);
            }
        }
        
        for (int i = 0; i < Math.min(residents.size(), 45); i++) {
            Resident resident = residents.get(i);
            inventory.setItem(i, createResidentItem(resident));
        }
        
        // Back button
        inventory.setItem(49, createItem(Material.ARROW, ChatColor.YELLOW + "← Back", null));
    }
    
    private ItemStack createResidentItem(Resident resident) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Rank: " + ChatColor.AQUA + resident.getRank(town.getUuid()));
        lore.add(ChatColor.GRAY + "Joined: " + ChatColor.WHITE + 
            new java.text.SimpleDateFormat("MMM dd, yyyy").format(new java.util.Date(resident.getJoinedTown())));
        lore.add("");
        
        switch (action) {
            case "kick":
                if (!resident.isMayor(town.getUuid())) {
                    lore.add(ChatColor.RED + "Click to kick!");
                } else {
                    lore.add(ChatColor.GRAY + "Cannot kick mayor");
                }
                break;
            case "promote":
                if (!resident.isAssistant(town.getUuid()) && !resident.isMayor(town.getUuid())) {
                    lore.add(ChatColor.GREEN + "Click to promote!");
                } else {
                    lore.add(ChatColor.GRAY + "Already promoted");
                }
                break;
            case "demote":
                if (resident.isAssistant(town.getUuid()) && !resident.isMayor(town.getUuid())) {
                    lore.add(ChatColor.YELLOW + "Click to demote!");
                } else {
                    lore.add(ChatColor.GRAY + "Cannot demote");
                }
                break;
            default:
                lore.add(ChatColor.YELLOW + "Click for info!");
        }
        
        return createItem(Material.PLAYER_HEAD, ChatColor.AQUA + resident.getName(), lore);
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
            new TownMenuGUI(plugin, player).open();
            return;
        }
        
        List<Resident> residents = new ArrayList<>();
        for (UUID residentId : town.getResidents()) {
            Resident resident = plugin.getDataManager().getResident(residentId);
            if (resident != null) {
                residents.add(resident);
            }
        }
        
        if (slot < residents.size()) {
            Resident target = residents.get(slot);
            player.closeInventory();
            
            switch (action) {
                case "kick":
                    if (!target.isMayor(town.getUuid())) {
                        player.performCommand("town kick " + target.getName());
                    }
                    break;
                case "promote":
                    if (!target.isAssistant(town.getUuid()) && !target.isMayor(town.getUuid())) {
                        player.performCommand("town rank " + target.getName() + " assistant");
                    }
                    break;
                case "demote":
                    if (target.isAssistant(town.getUuid()) && !target.isMayor(town.getUuid())) {
                        player.performCommand("town rank " + target.getName() + " resident");
                    }
                    break;
                case "view":
                    new ResidentInfoGUI(plugin, player, Bukkit.getOfflinePlayer(target.getUuid())).open();
                    break;
            }
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
