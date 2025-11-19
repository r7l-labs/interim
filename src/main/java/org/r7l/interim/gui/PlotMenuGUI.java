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
import org.r7l.interim.model.Claim;
import org.r7l.interim.model.ClaimType;
import org.r7l.interim.model.Resident;

import java.util.ArrayList;
import java.util.List;

/**
 * Plot management GUI
 */
public class PlotMenuGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    
    public PlotMenuGUI(Interim plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 27, 
            ChatColor.YELLOW + "✦ Plot Menu ✦");
        
        buildMenu();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu() {
        Resident resident = plugin.getDataManager().getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) return;
        
        Claim claim = plugin.getDataManager().getClaim(
            player.getWorld().getName(),
            player.getLocation().getChunk().getX(),
            player.getLocation().getChunk().getZ()
        );
        
        boolean inOwnClaim = claim != null && claim.getTown().getUuid().equals(resident.getTown().getUuid());
        boolean hasPerms = resident.isMayor() || resident.isAssistant();
        
        // Current plot info
        if (inOwnClaim) {
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Type: " + ChatColor.AQUA + claim.getType());
            lore.add(ChatColor.GRAY + "PvP: " + (claim.isPvpEnabled() ? ChatColor.GREEN + "✓" : ChatColor.RED + "✗"));
            lore.add(ChatColor.GRAY + "Explosions: " + (claim.isExplosionsEnabled() ? ChatColor.GREEN + "✓" : ChatColor.RED + "✗"));
            lore.add(ChatColor.GRAY + "Mobs: " + (claim.isMobSpawningEnabled() ? ChatColor.GREEN + "✓" : ChatColor.RED + "✗"));
            
            inventory.setItem(4, createItem(Material.GRASS_BLOCK, 
                ChatColor.GREEN + "Current Plot", lore));
        } else {
            inventory.setItem(4, createItem(Material.BARRIER, 
                ChatColor.RED + "Not in Town Claim",
                List.of(ChatColor.GRAY + "Stand in a town claim")));
        }
        
        if (inOwnClaim && hasPerms) {
            // Set type options
            inventory.setItem(10, createItem(Material.PAPER, ChatColor.WHITE + "Normal Plot",
                List.of(ChatColor.GRAY + "Standard residential plot", "", ChatColor.YELLOW + "Click to set!")));
            
            inventory.setItem(11, createItem(Material.DIAMOND_SWORD, ChatColor.RED + "Arena",
                List.of(ChatColor.GRAY + "PvP arena plot", "", ChatColor.YELLOW + "Click to set!")));
            
            inventory.setItem(12, createItem(Material.ENDER_PEARL, ChatColor.LIGHT_PURPLE + "Embassy",
                List.of(ChatColor.GRAY + "Foreign embassy plot", "", ChatColor.YELLOW + "Click to set!")));
            
            inventory.setItem(13, createItem(Material.CHEST, ChatColor.GOLD + "Shop",
                List.of(ChatColor.GRAY + "Shopping district plot", "", ChatColor.YELLOW + "Click to set!")));
            
            inventory.setItem(14, createItem(Material.GRASS_BLOCK, ChatColor.GREEN + "Farm",
                List.of(ChatColor.GRAY + "Agricultural plot", "", ChatColor.YELLOW + "Click to set!")));
            
            inventory.setItem(15, createItem(Material.COBBLESTONE, ChatColor.GRAY + "Bank",
                List.of(ChatColor.GRAY + "Financial district plot", "", ChatColor.YELLOW + "Click to set!")));
            
            inventory.setItem(16, createItem(Material.NETHER_STAR, ChatColor.AQUA + "Inn",
                List.of(ChatColor.GRAY + "Inn/tavern plot", "", ChatColor.YELLOW + "Click to set!")));
        } else {
            inventory.setItem(13, createItem(Material.BARRIER, ChatColor.RED + "No Permission",
                List.of(ChatColor.GRAY + "Mayor/Assistant only")));
        }
        
        // Back button
        inventory.setItem(22, createItem(Material.ARROW, ChatColor.YELLOW + "← Back", null));
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
        Resident resident = plugin.getDataManager().getResident(player.getUniqueId());
        
        if (slot == 22) {
            player.closeInventory();
            new MainMenuGUI(plugin, player).open();
            return;
        }
        
        if (resident == null || !resident.hasTown()) return;
        
        boolean hasPerms = resident.isMayor() || resident.isAssistant();
        if (!hasPerms) return;
        
        player.closeInventory();
        
        String type = switch (slot) {
            case 10 -> "normal";
            case 11 -> "arena";
            case 12 -> "embassy";
            case 13 -> "shop";
            case 14 -> "farm";
            case 15 -> "bank";
            case 16 -> "inn";
            default -> null;
        };
        
        if (type != null) {
            player.performCommand("plot set type " + type);
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
