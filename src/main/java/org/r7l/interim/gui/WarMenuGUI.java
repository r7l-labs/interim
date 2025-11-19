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
import org.r7l.interim.model.War;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * War management GUI
 */
public class WarMenuGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    
    public WarMenuGUI(Interim plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 54, 
            ChatColor.DARK_RED + "⚔ " + ChatColor.RED + "War System" + ChatColor.DARK_RED + " ⚔");
        
        buildMenu();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu() {
        // Check permission
        if (!player.hasPermission("interim.war")) {
            inventory.setItem(22, createItem(Material.BARRIER, 
                ChatColor.RED + "No Permission",
                List.of(
                    ChatColor.GRAY + "You need the",
                    ChatColor.YELLOW + "interim.war",
                    ChatColor.GRAY + "permission to access",
                    ChatColor.GRAY + "the war system"
                )));
            inventory.setItem(49, createItem(Material.ARROW, ChatColor.YELLOW + "← Back", null));
            return;
        }
        
        Resident resident = plugin.getDataManager().getResident(player.getUniqueId());
        
        // Decorative border
        ItemStack border = createItem(Material.RED_STAINED_GLASS_PANE, " ", null);
        for (int i : new int[]{0, 1, 7, 8, 9, 17, 36, 44, 45, 46, 52, 53}) {
            inventory.setItem(i, border);
        }
        
        // War info header
        List<War> activeWars = plugin.getDataManager().getActiveWars();
        List<String> headerLore = new ArrayList<>();
        headerLore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
        headerLore.add(ChatColor.GRAY + "Active Wars: " + ChatColor.RED + activeWars.size());
        
        if (resident != null && resident.hasNation()) {
            Nation nation = resident.getNation();
            long activeWarsCount = activeWars.stream()
                .filter(w -> w.isParticipant(nation.getUuid()))
                .count();
            headerLore.add(ChatColor.GRAY + "Your Nation's Wars: " + ChatColor.YELLOW + activeWarsCount);
        }
        
        headerLore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━");
        inventory.setItem(4, createItem(Material.NETHERITE_SWORD, 
            ChatColor.RED + "⚔ War System ⚔", headerLore));
        
        if (resident != null && resident.hasNation()) {
            Nation nation = resident.getNation();
            boolean isCapitalMayor = resident.isMayor() && 
                resident.getTown().getUuid().equals(nation.getCapital());
            
            // Main actions
            inventory.setItem(19, createWarActionButton(Material.DIAMOND_SWORD, "Declare War", 
                "Start a war against another nation", isCapitalMayor, "Capital Mayor"));
            
            inventory.setItem(20, createWarActionButton(Material.SHIELD, "Active Wars", 
                "View your nation's active wars", true, "Everyone"));
            
            inventory.setItem(21, createWarActionButton(Material.PAPER, "War History", 
                "View past wars and outcomes", true, "Everyone"));
            
            inventory.setItem(22, createWarActionButton(Material.BOOK, "War Statistics", 
                "View war stats and leaderboards", true, "Everyone"));
            
            inventory.setItem(23, createWarActionButton(Material.EMERALD, "Offer Peace", 
                "Negotiate peace treaty", isCapitalMayor, "Capital Mayor"));
            
            inventory.setItem(24, createWarActionButton(Material.GOLD_INGOT, "War Treasury", 
                "Manage war funds", isCapitalMayor, "Capital Mayor"));
            
            inventory.setItem(25, createWarActionButton(Material.BELL, "Surrender", 
                "Surrender current war", isCapitalMayor, "Capital Mayor"));
            
            // Quick info
            inventory.setItem(38, createItem(Material.IRON_SWORD, 
                ChatColor.AQUA + "War Points",
                List.of(
                    ChatColor.GRAY + "Kill enemy: " + ChatColor.GREEN + "+10 points",
                    ChatColor.GRAY + "Capture town: " + ChatColor.GREEN + "+50 points",
                    ChatColor.GRAY + "Hold territory: " + ChatColor.GREEN + "+5 points/hour",
                    ChatColor.GRAY + "First to 500 points wins!"
                )));
            
            inventory.setItem(39, createItem(Material.GOLD_BLOCK, 
                ChatColor.GOLD + "War Costs",
                List.of(
                    ChatColor.GRAY + "Declaration: " + ChatColor.YELLOW + "$5000",
                    ChatColor.GRAY + "Daily upkeep: " + ChatColor.YELLOW + "$500",
                    ChatColor.GRAY + "Winner takes wager",
                    ChatColor.GRAY + "Loser pays reparations"
                )));
            
            inventory.setItem(40, createItem(Material.CLOCK, 
                ChatColor.YELLOW + "War Duration",
                List.of(
                    ChatColor.GRAY + "Minimum: " + ChatColor.WHITE + "3 days",
                    ChatColor.GRAY + "Maximum: " + ChatColor.WHITE + "14 days",
                    ChatColor.GRAY + "Auto-ends at max duration",
                    ChatColor.GRAY + "Or when goal is reached"
                )));
            
            inventory.setItem(41, createItem(Material.BEACON, 
                ChatColor.LIGHT_PURPLE + "War Goals",
                List.of(
                    ChatColor.GRAY + "• Territory Conquest",
                    ChatColor.GRAY + "• Economic Dominance",
                    ChatColor.GRAY + "• Political Subjugation",
                    ChatColor.GRAY + "• Resource Control",
                    ChatColor.GRAY + "Set goal when declaring"
                )));
            
        } else {
            inventory.setItem(22, createItem(Material.BARRIER, 
                ChatColor.RED + "No Nation",
                List.of(ChatColor.GRAY + "Join a nation to participate in wars")));
        }
        
        // Back button
        inventory.setItem(49, createItem(Material.ARROW, ChatColor.YELLOW + "← Back to Main Menu", null));
    }
    
    private ItemStack createWarActionButton(Material material, String name, String description, boolean enabled, String requirement) {
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
        ChatColor color = enabled ? ChatColor.RED : ChatColor.GRAY;
        
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
        if (!player.hasPermission("interim.war")) {
            if (slot == 49) {
                player.closeInventory();
                new MainMenuGUI(plugin, player).open();
            }
            return;
        }
        
        Resident resident = plugin.getDataManager().getResident(player.getUniqueId());
        
        if (slot == 49) {
            player.closeInventory();
            new MainMenuGUI(plugin, player).open();
            return;
        }
        
        if (resident == null || !resident.hasNation()) {
            return;
        }
        
        Nation nation = resident.getNation();
        boolean isCapitalMayor = resident.isMayor() && 
            resident.getTown().getUuid().equals(nation.getCapital());
        
        player.closeInventory();
        
        switch (slot) {
            case 19: // Declare War
                if (isCapitalMayor) {
                    new DeclareWarGUI(plugin, player, nation).open();
                }
                break;
            case 20: // Active Wars
                new ActiveWarsGUI(plugin, player, nation).open();
                break;
            case 21: // War History
                new WarHistoryGUI(plugin, player, nation).open();
                break;
            case 22: // War Statistics
                new WarStatsGUI(plugin, player).open();
                break;
            case 23: // Offer Peace
                if (isCapitalMayor) {
                    new PeaceTreatyGUI(plugin, player, nation).open();
                }
                break;
            case 24: // War Treasury
                if (isCapitalMayor) {
                    player.sendMessage(ChatColor.YELLOW + "War treasury management coming soon!");
                }
                break;
            case 25: // Surrender
                if (isCapitalMayor) {
                    List<War> activeWars = plugin.getDataManager().getActiveWars().stream()
                        .filter(w -> w.isParticipant(nation.getUuid()))
                        .toList();
                    if (!activeWars.isEmpty()) {
                        new SurrenderWarGUI(plugin, player, nation, activeWars.get(0)).open();
                    } else {
                        player.sendMessage(ChatColor.RED + "Your nation is not at war!");
                    }
                }
                break;
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
