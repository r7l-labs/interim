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

/**
 * Nation management GUI
 */
public class NationMenuGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    
    public NationMenuGUI(Interim plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 54, 
            ChatColor.DARK_PURPLE + "✦ " + ChatColor.GOLD + "Nation Menu" + ChatColor.DARK_PURPLE + " ✦");
        
        buildMenu();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu() {
        Resident resident = plugin.getDataManager().getResident(player.getUniqueId());
        
        // Decorative border
        ItemStack border = createItem(Material.YELLOW_STAINED_GLASS_PANE, " ", null);
        for (int i : new int[]{0, 1, 7, 8, 9, 17, 36, 44, 45, 46, 52, 53}) {
            inventory.setItem(i, border);
        }
        
        // Nation info (top center)
        if (resident != null && resident.hasNation()) {
            inventory.setItem(4, createNationInfo(resident.getNation()));
        } else {
            inventory.setItem(4, createItem(Material.BARRIER, ChatColor.RED + "No Nation", 
                List.of(ChatColor.GRAY + "Your town is not in a nation")));
        }
        
        if (resident != null && resident.hasTown()) {
            Town town = resident.getTown();
            boolean isCapitalMayor = resident.hasNation() && resident.isMayor() && 
                town.getUuid().equals(resident.getNation().getCapital());
            boolean canManage = resident.hasNation() && isCapitalMayor;
            boolean isMayor = resident.isMayor();
            
            if (resident.hasNation()) {
                // Nation actions
                inventory.setItem(19, createActionButton(Material.NAME_TAG, "Rename Nation", 
                    "Change nation name", canManage, "Capital Mayor"));
                inventory.setItem(20, createActionButton(Material.WRITABLE_BOOK, "Set Tag", 
                    "Set nation tag", canManage, "Capital Mayor"));
                inventory.setItem(21, createActionButton(Material.PAINTING, "Set Board", 
                    "Set nation message", canManage, "Capital Mayor"));
                inventory.setItem(22, createActionButton(Material.PAPER, "Invite Town", 
                    "Invite town to nation", canManage, "Capital Mayor"));
                inventory.setItem(23, createActionButton(Material.IRON_DOOR, "Kick Town", 
                    "Remove a town", canManage, "Capital Mayor"));
                inventory.setItem(24, createActionButton(Material.DIAMOND, "Set Capital", 
                    "Change nation capital", canManage, "Capital Mayor"));
                inventory.setItem(25, createActionButton(Material.REDSTONE, "Declare War", 
                    "War against nation", canManage, "Capital Mayor"));
                
                inventory.setItem(28, createActionButton(Material.EMERALD, "Make Peace", 
                    "End war with nation", canManage, "Capital Mayor"));
                inventory.setItem(29, createActionButton(Material.GOLD_INGOT, "Ally", 
                    "Ally with nation", canManage, "Capital Mayor"));
                inventory.setItem(30, createActionButton(Material.COAL, "Remove Ally", 
                    "Remove alliance", canManage, "Capital Mayor"));
                inventory.setItem(31, createActionButton(Material.OAK_SIGN, "Towns", 
                    "View all towns", true, "Everyone"));
                inventory.setItem(32, createActionButton(Material.ENDER_EYE, "Leave Nation", 
                    "Your town leaves", isMayor, "Mayor"));
                inventory.setItem(33, createActionButton(Material.TNT, "Delete Nation", 
                    "Permanently delete", canManage, "Capital Mayor"));
                
            } else {
                // No nation - show create/join
                inventory.setItem(22, createItem(Material.NETHER_STAR, 
                    ChatColor.GOLD + "✚ Create Nation", 
                    List.of(
                        ChatColor.GRAY + "Found a new nation!",
                        "",
                        ChatColor.GRAY + "Cost: " + ChatColor.GOLD + "$" + plugin.getConfig().getDouble("economy.nation-creation-cost", 5000.0),
                        ChatColor.GRAY + "Requires: " + ChatColor.YELLOW + "Mayor",
                        "",
                        ChatColor.YELLOW + "Click to create!"
                    )));
                
                inventory.setItem(31, createItem(Material.ENCHANTED_BOOK, 
                    ChatColor.AQUA + "Browse Nations", 
                    List.of(
                        ChatColor.GRAY + "View all nations",
                        "",
                        ChatColor.YELLOW + "Click to browse!"
                    )));
            }
            
        } else {
            inventory.setItem(22, createItem(Material.BARRIER, ChatColor.RED + "No Town", 
                List.of(ChatColor.GRAY + "Join a town first!")));
        }
        
        // Back button
        inventory.setItem(49, createItem(Material.ARROW, ChatColor.YELLOW + "← Back to Main Menu", null));
    }
    
    private ItemStack createNationInfo(Nation nation) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
        lore.add(ChatColor.GRAY + "Tag: " + ChatColor.WHITE + "[" + nation.getName().substring(0, Math.min(4, nation.getName().length())) + "]");
        
        Town capital = plugin.getDataManager().getTown(nation.getCapital());
        if (capital != null) {
            lore.add(ChatColor.GRAY + "Capital: " + ChatColor.YELLOW + capital.getName());
        }
        
        lore.add(ChatColor.GRAY + "Towns: " + ChatColor.WHITE + nation.getTownCount());
        lore.add(ChatColor.GRAY + "Bank: " + ChatColor.GOLD + "$" + String.format("%.2f", nation.getBank()));
        lore.add(ChatColor.GRAY + "Allies: " + ChatColor.GREEN + nation.getAllies().size());
        lore.add(ChatColor.GRAY + "Enemies: " + ChatColor.RED + nation.getEnemies().size());
        lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
        
        return createItem(Material.NETHER_STAR, 
            ChatColor.GOLD + "⚑ " + nation.getName(), lore);
    }
    
    private ItemStack createActionButton(Material material, String name, String description, boolean enabled, String requirement) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + description);
        lore.add("");
        
        if (enabled) {
            lore.add(ChatColor.GREEN + "✓ Available");
            lore.add(ChatColor.YELLOW + "Click to use!");
        } else {
            lore.add(ChatColor.RED + "✗ Requires: " + requirement);
        }
        
        Material displayMaterial = enabled ? material : Material.GRAY_STAINED_GLASS_PANE;
        ChatColor color = enabled ? ChatColor.AQUA : ChatColor.GRAY;
        
        return createItem(displayMaterial, color + name, lore);
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
        
        if (slot == 49) {
            player.closeInventory();
            new MainMenuGUI(plugin, player).open();
            return;
        }
        
        if (resident == null || !resident.hasTown()) {
            return;
        }
        
        Town town = resident.getTown();
        boolean isCapitalMayor = resident.hasNation() && resident.isMayor() && 
            town.getUuid().equals(resident.getNation().getCapital());
        boolean canManage = resident.hasNation() && isCapitalMayor;
        boolean isMayor = resident.isMayor();
        
        player.closeInventory();
        
        if (!resident.hasNation()) {
            if (slot == 22 && isMayor) {
                player.performCommand("nation create");
            } else if (slot == 31) {
                new NationListGUI(plugin, player, 0).open();
            }
            return;
        }
        
        switch (slot) {
            case 19: // Rename
                if (canManage) {
                    player.sendMessage(ChatColor.YELLOW + "Type in chat: " + ChatColor.WHITE + "/nation rename <name>");
                }
                break;
            case 20: // Set Tag
                if (canManage) {
                    player.sendMessage(ChatColor.YELLOW + "Type in chat: " + ChatColor.WHITE + "/nation tag <tag>");
                }
                break;
            case 21: // Set Board
                if (canManage) {
                    player.sendMessage(ChatColor.YELLOW + "Type in chat: " + ChatColor.WHITE + "/nation board <message>");
                }
                break;
            case 22: // Invite Town
                if (canManage) {
                    player.sendMessage(ChatColor.YELLOW + "Type in chat: " + ChatColor.WHITE + "/nation invite <town>");
                }
                break;
            case 23: // Kick Town
                if (canManage) {
                    new NationTownListGUI(plugin, player, resident.getNation(), "kick").open();
                }
                break;
            case 24: // Set Capital
                if (canManage) {
                    new NationTownListGUI(plugin, player, resident.getNation(), "capital").open();
                }
                break;
            case 25: // Declare War
                if (canManage) {
                    player.sendMessage(ChatColor.YELLOW + "Type in chat: " + ChatColor.WHITE + "/nation enemy <nation>");
                }
                break;
            case 28: // Make Peace
                if (canManage) {
                    new NationRelationGUI(plugin, player, resident.getNation(), "neutral", "enemy").open();
                }
                break;
            case 29: // Ally
                if (canManage) {
                    player.sendMessage(ChatColor.YELLOW + "Type in chat: " + ChatColor.WHITE + "/nation ally <nation>");
                }
                break;
            case 30: // Remove Ally
                if (canManage) {
                    new NationRelationGUI(plugin, player, resident.getNation(), "neutral", "ally").open();
                }
                break;
            case 31: // Towns
                new NationTownListGUI(plugin, player, resident.getNation(), "view").open();
                break;
            case 32: // Leave
                if (isMayor) {
                    new ConfirmActionGUI(plugin, player, "Leave Nation", 
                        "Are you sure you want to leave " + resident.getNation().getName() + "?",
                        () -> player.performCommand("nation leave"),
                        () -> new NationMenuGUI(plugin, player).open()).open();
                }
                break;
            case 33: // Delete
                if (canManage) {
                    new ConfirmActionGUI(plugin, player, "Delete Nation", 
                        "Are you sure you want to permanently delete " + resident.getNation().getName() + "?",
                        () -> player.performCommand("nation delete"),
                        () -> new NationMenuGUI(plugin, player).open()).open();
                }
                break;
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
