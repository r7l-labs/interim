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
 * Town management GUI
 */
public class TownMenuGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    
    public TownMenuGUI(Interim plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 54, 
            ChatColor.DARK_GREEN + "✦ " + ChatColor.GREEN + "Town Menu" + ChatColor.DARK_GREEN + " ✦");
        
        buildMenu();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu() {
        Resident resident = plugin.getDataManager().getResident(player.getUniqueId());
        
        // Decorative border
        ItemStack border = createItem(Material.GREEN_STAINED_GLASS_PANE, " ", null);
        for (int i : new int[]{0, 1, 7, 8, 9, 17, 36, 44, 45, 46, 52, 53}) {
            inventory.setItem(i, border);
        }
        
        // Town info (top center)
        if (resident != null && resident.hasTown()) {
            inventory.setItem(4, createTownInfo(resident.getTown()));
        } else {
            inventory.setItem(4, createItem(Material.BARRIER, ChatColor.RED + "No Town", 
                List.of(ChatColor.GRAY + "You are not in a town")));
        }
        
        if (resident != null && resident.hasTown()) {
            boolean isMayor = resident.isMayor();
            boolean isAssistant = resident.isAssistant();
            boolean hasPerms = isMayor || isAssistant;
            
            // Actions
            inventory.setItem(19, createTownActionButton(Material.COMPASS, "Set Spawn", 
                "Set the town spawn point", hasPerms, "Mayor/Assistant"));
            inventory.setItem(20, createTownActionButton(Material.NAME_TAG, "Rename Town", 
                "Change your town's name", isMayor, "Mayor"));
            inventory.setItem(21, createTownActionButton(Material.WRITABLE_BOOK, "Set Tag", 
                "Set town tag (4 chars)", hasPerms, "Mayor/Assistant"));
            inventory.setItem(22, createTownActionButton(Material.PAINTING, "Set Board", 
                "Set town message board", hasPerms, "Mayor/Assistant"));
            inventory.setItem(23, createTownActionButton(Material.PAPER, "Invite Player", 
                "Invite a player to town", hasPerms, "Mayor/Assistant"));
            inventory.setItem(24, createTownActionButton(Material.IRON_DOOR, "Kick Player", 
                "Remove a resident", hasPerms, "Mayor/Assistant"));
            inventory.setItem(25, createTownActionButton(Material.EMERALD, "Promote", 
                "Promote to assistant", isMayor, "Mayor"));
            
            inventory.setItem(28, createTownActionButton(Material.REDSTONE, "Demote", 
                "Demote from assistant", isMayor, "Mayor"));
            inventory.setItem(29, createTownActionButton(Material.PLAYER_HEAD, "Residents", 
                "View all residents", true, "Everyone"));
            inventory.setItem(30, createTownActionButton(Material.FILLED_MAP, "Claims", 
                "View all town claims", true, "Everyone"));
            inventory.setItem(31, createTownActionButton(Material.GOLD_INGOT, "Economy", 
                "Manage town funds", hasPerms, "Mayor/Assistant"));
            inventory.setItem(32, createTownActionButton(Material.ENDER_EYE, "Leave Town", 
                "Leave this town", !isMayor, "Not Mayor"));
            inventory.setItem(33, createTownActionButton(Material.TNT, "Delete Town", 
                "Permanently delete town", isMayor, "Mayor"));
            inventory.setItem(34, createTownActionButton(Material.NETHER_STAR, "Switch Town", 
                "Change primary town", resident.getTownCount() > 1, "Multi-town"));
            
        } else {
            // No town - show create/join options
            inventory.setItem(22, createItem(Material.EMERALD_BLOCK, 
                ChatColor.GREEN + "✚ Create Town", 
                List.of(
                    ChatColor.GRAY + "Start your own town!",
                    "",
                    ChatColor.GRAY + "Cost: " + ChatColor.GOLD + "$" + plugin.getConfig().getDouble("economy.town-creation-cost", 1000.0),
                    "",
                    ChatColor.YELLOW + "Click to create!"
                )));
            
            inventory.setItem(31, createItem(Material.OAK_SIGN, 
                ChatColor.AQUA + "Browse Towns", 
                List.of(
                    ChatColor.GRAY + "View all available towns",
                    "",
                    ChatColor.YELLOW + "Click to browse!"
                )));
        }
        
        // Back button
        inventory.setItem(49, createItem(Material.ARROW, ChatColor.YELLOW + "← Back to Main Menu", null));
    }
    
    private ItemStack createTownInfo(Town town) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
        lore.add(ChatColor.GRAY + "Tag: " + ChatColor.WHITE + "[" + town.getName().substring(0, Math.min(4, town.getName().length())) + "]");
        lore.add(ChatColor.GRAY + "Mayor: " + ChatColor.YELLOW + plugin.getDataManager().getResident(town.getMayor()).getName());
        lore.add(ChatColor.GRAY + "Residents: " + ChatColor.WHITE + town.getResidentCount());
        lore.add(ChatColor.GRAY + "Claims: " + ChatColor.WHITE + town.getClaimCount() + "/" + 100);
        lore.add(ChatColor.GRAY + "Bank: " + ChatColor.GOLD + "$" + String.format("%.2f", town.getBank()));
        
        if (town.hasNation()) {
            lore.add(ChatColor.GRAY + "Nation: " + ChatColor.GOLD + town.getNation().getName());
        }
        
        lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
        
        return createItem(Material.EMERALD_BLOCK, 
            ChatColor.GREEN + "⚑ " + town.getName(), lore);
    }
    
    private ItemStack createTownActionButton(Material material, String name, String description, boolean enabled, String requirement) {
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
            if (slot == 22) {
                player.closeInventory();
                player.performCommand("town create");
            } else if (slot == 31) {
                player.closeInventory();
                new TownListGUI(plugin, player, 0).open();
            }
            return;
        }
        
        boolean isMayor = resident.isMayor();
        boolean isAssistant = resident.isAssistant();
        boolean hasPerms = isMayor || isAssistant;
        
        player.closeInventory();
        
        switch (slot) {
            case 19: // Set Spawn
                if (hasPerms) player.performCommand("town setspawn");
                break;
            case 20: // Rename
                if (isMayor) {
                    player.sendMessage(ChatColor.YELLOW + "Type in chat: " + ChatColor.WHITE + "/town rename <new name>");
                }
                break;
            case 21: // Set Tag
                if (hasPerms) {
                    player.sendMessage(ChatColor.YELLOW + "Type in chat: " + ChatColor.WHITE + "/town tag <tag>");
                }
                break;
            case 22: // Set Board
                if (hasPerms) {
                    player.sendMessage(ChatColor.YELLOW + "Type in chat: " + ChatColor.WHITE + "/town board <message>");
                }
                break;
            case 23: // Invite
                if (hasPerms) {
                    player.sendMessage(ChatColor.YELLOW + "Type in chat: " + ChatColor.WHITE + "/town invite <player>");
                }
                break;
            case 24: // Kick
                if (hasPerms) {
                    new ResidentListGUI(plugin, player, resident.getTown(), "kick").open();
                }
                break;
            case 25: // Promote
                if (isMayor) {
                    new ResidentListGUI(plugin, player, resident.getTown(), "promote").open();
                }
                break;
            case 28: // Demote
                if (isMayor) {
                    new ResidentListGUI(plugin, player, resident.getTown(), "demote").open();
                }
                break;
            case 29: // Residents
                new ResidentListGUI(plugin, player, resident.getTown(), "view").open();
                break;
            case 30: // Claims
                new ClaimMapGUI(plugin, player).open();
                break;
            case 31: // Economy
                if (hasPerms) {
                    new EconomyMenuGUI(plugin, player).open();
                }
                break;
            case 32: // Leave
                if (!isMayor) {
                    new ConfirmActionGUI(plugin, player, "Leave Town", 
                        "Are you sure you want to leave " + resident.getTown().getName() + "?",
                        () -> player.performCommand("town leave"),
                        () -> new TownMenuGUI(plugin, player).open()).open();
                }
                break;
            case 33: // Delete
                if (isMayor) {
                    new ConfirmActionGUI(plugin, player, "Delete Town", 
                        "Are you sure you want to permanently delete " + resident.getTown().getName() + "?",
                        () -> player.performCommand("town delete"),
                        () -> new TownMenuGUI(plugin, player).open()).open();
                }
                break;
            case 34: // Switch Town
                if (resident.getTownCount() > 1) {
                    new TownSwitchGUI(plugin, player).open();
                }
                break;
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
