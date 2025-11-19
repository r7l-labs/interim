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
 * Main menu GUI hub for all town and nation operations
 */
public class MainMenuGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    
    public MainMenuGUI(Interim plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 54, 
            ChatColor.DARK_PURPLE + "✦ " + ChatColor.LIGHT_PURPLE + "Interim Menu" + ChatColor.DARK_PURPLE + " ✦");
        
        buildMenu();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu() {
        Resident resident = plugin.getDataManager().getResident(player.getUniqueId());
        
        // Decorative border
        ItemStack border = createItem(Material.PURPLE_STAINED_GLASS_PANE, " ", null);
        for (int i : new int[]{0, 1, 7, 8, 9, 17, 36, 44, 45, 46, 52, 53}) {
            inventory.setItem(i, border);
        }
        
        // Player Info (top center)
        inventory.setItem(4, createPlayerInfo(resident));
        
        // Main action buttons
        inventory.setItem(19, createClaimMapButton());
        inventory.setItem(20, createTownButton(resident));
        inventory.setItem(21, createNationButton(resident));
        inventory.setItem(22, createPlotButton(resident));
        inventory.setItem(23, createResidentButton(resident));
        inventory.setItem(24, createSettingsButton(resident));
        inventory.setItem(25, createEconomyButton(resident));
        inventory.setItem(28, createWarButton());
        
        // Quick actions (bottom row)
        inventory.setItem(38, createQuickAction(Material.COMPASS, ChatColor.AQUA + "Town Spawn", 
            "Teleport to your town spawn"));
        inventory.setItem(39, createQuickAction(Material.ENDER_PEARL, ChatColor.LIGHT_PURPLE + "Nation Spawn", 
            "Teleport to nation capital"));
        inventory.setItem(40, createQuickAction(Material.WRITABLE_BOOK, ChatColor.YELLOW + "Town List", 
            "View all towns"));
        inventory.setItem(41, createQuickAction(Material.ENCHANTED_BOOK, ChatColor.GOLD + "Nation List", 
            "View all nations"));
        inventory.setItem(42, createQuickAction(Material.PLAYER_HEAD, ChatColor.GREEN + "Invites", 
            "Check your invitations"));
        
        // Close button
        inventory.setItem(49, createItem(Material.BARRIER, ChatColor.RED + "Close Menu", 
            List.of(ChatColor.GRAY + "Click to close")));
    }
    
    private ItemStack createPlayerInfo(Resident resident) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
        
        if (resident != null && resident.hasTown()) {
            lore.add(ChatColor.GRAY + "Primary Town: " + ChatColor.GREEN + resident.getTown().getName());
            lore.add(ChatColor.GRAY + "Rank: " + ChatColor.AQUA + resident.getRank());
            
            if (resident.getTownCount() > 1) {
                lore.add(ChatColor.GRAY + "Other Towns: " + ChatColor.YELLOW + (resident.getTownCount() - 1));
            }
            
            if (resident.hasNation()) {
                lore.add(ChatColor.GRAY + "Nation: " + ChatColor.GOLD + resident.getNation().getName());
            }
        } else {
            lore.add(ChatColor.GRAY + "Status: " + ChatColor.RED + "No Town");
            lore.add(ChatColor.YELLOW + "Create or join a town!");
        }
        
        lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
        
        return createItem(Material.PLAYER_HEAD, ChatColor.LIGHT_PURPLE + "⚡ " + player.getName(), lore);
    }
    
    private ItemStack createClaimMapButton() {
        return createItem(Material.FILLED_MAP, 
            ChatColor.AQUA + "▶ " + ChatColor.WHITE + "Claim Map", 
            List.of(
                ChatColor.GRAY + "Open the interactive claim map",
                ChatColor.GRAY + "View and manage territory",
                "",
                ChatColor.GREEN + "Click to open!"
            ));
    }
    
    private ItemStack createTownButton(Resident resident) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Manage your town");
        lore.add("");
        
        if (resident != null && resident.hasTown()) {
            Town town = resident.getTown();
            lore.add(ChatColor.GRAY + "Current Town: " + ChatColor.GREEN + town.getName());
            lore.add(ChatColor.GRAY + "Residents: " + ChatColor.WHITE + town.getResidentCount());
            lore.add(ChatColor.GRAY + "Claims: " + ChatColor.WHITE + town.getClaimCount());
            lore.add(ChatColor.GRAY + "Bank: " + ChatColor.GOLD + "$" + String.format("%.2f", town.getBank()));
        } else {
            lore.add(ChatColor.YELLOW + "Create a new town");
            lore.add(ChatColor.YELLOW + "or join an existing one");
        }
        
        lore.add("");
        lore.add(ChatColor.GREEN + "Click to open town menu!");
        
        return createItem(Material.OAK_SIGN, 
            ChatColor.GREEN + "▶ " + ChatColor.WHITE + "Town Management", lore);
    }
    
    private ItemStack createNationButton(Resident resident) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Manage your nation");
        lore.add("");
        
        if (resident != null && resident.hasNation()) {
            Nation nation = resident.getNation();
            lore.add(ChatColor.GRAY + "Current Nation: " + ChatColor.GOLD + nation.getName());
            lore.add(ChatColor.GRAY + "Towns: " + ChatColor.WHITE + nation.getTownCount());
            lore.add(ChatColor.GRAY + "Bank: " + ChatColor.GOLD + "$" + String.format("%.2f", nation.getBank()));
        } else if (resident != null && resident.hasTown()) {
            lore.add(ChatColor.YELLOW + "Create or join a nation");
            lore.add(ChatColor.GRAY + "Unite multiple towns!");
        } else {
            lore.add(ChatColor.RED + "Join a town first");
        }
        
        lore.add("");
        lore.add(ChatColor.GREEN + "Click to open nation menu!");
        
        return createItem(Material.NETHER_STAR, 
            ChatColor.GOLD + "▶ " + ChatColor.WHITE + "Nation Management", lore);
    }
    
    private ItemStack createPlotButton(Resident resident) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Manage plot permissions");
        lore.add("");
        
        if (resident != null && resident.hasTown()) {
            lore.add(ChatColor.GRAY + "Set permissions for");
            lore.add(ChatColor.GRAY + "specific chunks");
            lore.add(ChatColor.GRAY + "in your town");
        } else {
            lore.add(ChatColor.RED + "Join a town first");
        }
        
        lore.add("");
        lore.add(ChatColor.GREEN + "Click to open plot menu!");
        
        return createItem(Material.GRASS_BLOCK, 
            ChatColor.YELLOW + "▶ " + ChatColor.WHITE + "Plot Management", lore);
    }
    
    private ItemStack createResidentButton(Resident resident) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "View your resident info");
        lore.add("");
        
        if (resident != null) {
            lore.add(ChatColor.GRAY + "Joined: " + ChatColor.WHITE + new java.text.SimpleDateFormat("MMM dd, yyyy").format(new java.util.Date(resident.getJoinedTown())));
            lore.add(ChatColor.GRAY + "Towns: " + ChatColor.WHITE + resident.getTownCount());
        }
        
        lore.add("");
        lore.add(ChatColor.GREEN + "Click to view details!");
        
        return createItem(Material.PLAYER_HEAD, 
            ChatColor.AQUA + "▶ " + ChatColor.WHITE + "Resident Info", lore);
    }
    
    private ItemStack createSettingsButton(Resident resident) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Configure town settings");
        lore.add("");
        
        if (resident != null && resident.hasTown() && (resident.isMayor() || resident.isAssistant())) {
            lore.add(ChatColor.GRAY + "Toggle PvP, explosions,");
            lore.add(ChatColor.GRAY + "mob spawning, and more");
        } else {
            lore.add(ChatColor.RED + "Mayor/Assistant only");
        }
        
        lore.add("");
        lore.add(ChatColor.GREEN + "Click to open settings!");
        
        return createItem(Material.REDSTONE_TORCH, 
            ChatColor.RED + "▶ " + ChatColor.WHITE + "Settings", lore);
    }
    
    private ItemStack createEconomyButton(Resident resident) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Manage finances");
        lore.add("");
        
        if (resident != null && resident.hasTown()) {
            Town town = resident.getTown();
            lore.add(ChatColor.GRAY + "Town Bank: " + ChatColor.GOLD + "$" + String.format("%.2f", town.getBank()));
            
            if (resident.hasNation()) {
                Nation nation = resident.getNation();
                lore.add(ChatColor.GRAY + "Nation Bank: " + ChatColor.GOLD + "$" + String.format("%.2f", nation.getBank()));
            }
            
            lore.add("");
            lore.add(ChatColor.YELLOW + "Deposit or withdraw funds");
        } else {
            lore.add(ChatColor.RED + "Join a town first");
        }
        
        lore.add("");
        lore.add(ChatColor.GREEN + "Click to manage economy!");
        
        return createItem(Material.GOLD_INGOT, 
            ChatColor.GOLD + "▶ " + ChatColor.WHITE + "Economy", lore);
    }
    
    private ItemStack createWarButton() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Declare war on other nations");
        lore.add(ChatColor.GRAY + "and manage active conflicts");
        lore.add("");
        
        if (player.hasPermission("interim.war")) {
            List<org.r7l.interim.model.War> activeWars = plugin.getDataManager().getActiveWars();
            lore.add(ChatColor.GRAY + "Active Wars: " + ChatColor.RED + activeWars.size());
            lore.add("");
            lore.add(ChatColor.GREEN + "Click to open war menu!");
        } else {
            lore.add(ChatColor.RED + "Requires: interim.war permission");
        }
        
        return createItem(Material.NETHERITE_SWORD, 
            ChatColor.RED + "⚔ " + ChatColor.WHITE + "War System", lore);
    }
    
    private ItemStack createQuickAction(Material material, String name, String description) {
        return createItem(material, name, List.of(ChatColor.GRAY + description, "", ChatColor.YELLOW + "Click to use!"));
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
        
        switch (slot) {
            case 19: // Claim Map
                player.closeInventory();
                new ClaimMapGUI(plugin, player).open();
                break;
                
            case 20: // Town Management
                player.closeInventory();
                new TownMenuGUI(plugin, player).open();
                break;
                
            case 21: // Nation Management
                player.closeInventory();
                new NationMenuGUI(plugin, player).open();
                break;
                
            case 22: // Plot Management
                if (resident != null && resident.hasTown()) {
                    player.closeInventory();
                    new PlotMenuGUI(plugin, player).open();
                } else {
                    player.sendMessage(ChatColor.RED + "You must be in a town to manage plots!");
                }
                break;
                
            case 23: // Resident Info
                player.closeInventory();
                new ResidentInfoGUI(plugin, player, player).open();
                break;
                
            case 24: // Settings
                if (resident != null && resident.hasTown() && (resident.isMayor() || resident.isAssistant())) {
                    player.closeInventory();
                    new SettingsMenuGUI(plugin, player).open();
                } else {
                    player.sendMessage(ChatColor.RED + "You must be a mayor or assistant to access settings!");
                }
                break;
                
            case 25: // Economy
                if (resident != null && resident.hasTown()) {
                    player.closeInventory();
                    new EconomyMenuGUI(plugin, player).open();
                } else {
                    player.sendMessage(ChatColor.RED + "You must be in a town to access economy!");
                }
                break;
                
            case 28: // War System
                if (player.hasPermission("interim.war")) {
                    player.closeInventory();
                    new WarMenuGUI(plugin, player).open();
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have permission to access the war system!");
                    player.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.YELLOW + "interim.war");
                }
                break;
                
            case 38: // Town Spawn
                player.closeInventory();
                player.performCommand("town spawn");
                break;
                
            case 39: // Nation Spawn
                player.closeInventory();
                if (resident != null && resident.hasNation()) {
                    Town capital = plugin.getDataManager().getTown(resident.getNation().getCapital());
                    if (capital != null) {
                        player.performCommand("town spawn " + capital.getName());
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Your town is not part of a nation!");
                }
                break;
                
            case 40: // Town List
                player.closeInventory();
                new TownListGUI(plugin, player, 0).open();
                break;
                
            case 41: // Nation List
                player.closeInventory();
                new NationListGUI(plugin, player, 0).open();
                break;
                
            case 42: // Invites
                player.closeInventory();
                new InvitesGUI(plugin, player).open();
                break;
                
            case 49: // Close
                player.closeInventory();
                break;
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
