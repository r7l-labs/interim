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
import org.r7l.interim.model.Resident;
import org.r7l.interim.model.Town;

import java.util.ArrayList;
import java.util.List;

/**
 * Settings management GUI
 */
public class SettingsMenuGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    
    public SettingsMenuGUI(Interim plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 54, 
            ChatColor.RED + "✦ Town Settings ✦");
        
        buildMenu();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMenu() {
        Resident resident = plugin.getDataManager().getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) return;
        
        Town town = resident.getTown();
        
        // Get current chunk claim for toggle display
        Claim claim = plugin.getDataManager().getClaim(
            player.getWorld().getName(),
            player.getLocation().getChunk().getX(),
            player.getLocation().getChunk().getZ()
        );
        
        // Note: Claim flags are stored per-claim but not in the Claim model yet
        boolean pvpEnabled = false;
        boolean explosionsEnabled = false;
        boolean mobsEnabled = false;
        
        // Decorative border
        ItemStack border = createItem(Material.RED_STAINED_GLASS_PANE, " ", null);
        for (int i : new int[]{0, 1, 7, 8, 9, 17, 36, 44, 45, 46, 52, 53}) {
            inventory.setItem(i, border);
        }
        
        // Town info
        inventory.setItem(4, createItem(Material.REDSTONE_TORCH, 
            ChatColor.RED + "⚙ " + town.getName() + " Settings",
            List.of(ChatColor.GRAY + "Configure your town")));
        
        // PvP Toggle
        inventory.setItem(20, createToggleItem(Material.DIAMOND_SWORD, "PvP", 
            pvpEnabled, "Enable/disable PvP in claims"));
        
        // Explosions Toggle
        inventory.setItem(22, createToggleItem(Material.TNT, "Explosions", 
            explosionsEnabled, "Enable/disable explosions"));
        
        // Mob Spawning Toggle
        inventory.setItem(24, createToggleItem(Material.ZOMBIE_HEAD, "Mob Spawning", 
            mobsEnabled, "Enable/disable mob spawning"));
        
        // Public Status
        inventory.setItem(29, createToggleItem(Material.OAK_DOOR, "Public Town", 
            town.isOpen(), "Allow anyone to join"));
        
        // Tax Settings
        inventory.setItem(31, createItem(Material.GOLD_INGOT, 
            ChatColor.GOLD + "⚖ Tax Settings",
            List.of(
                ChatColor.GRAY + "Configure town taxes",
                "",
                ChatColor.YELLOW + "Coming soon!"
            )));
        
        // Plot Permissions
        inventory.setItem(33, createItem(Material.GRASS_BLOCK, 
            ChatColor.GREEN + "⚑ Plot Permissions",
            List.of(
                ChatColor.GRAY + "Set default plot permissions",
                "",
                ChatColor.YELLOW + "Click to configure!"
            )));
        
        // Back button
        inventory.setItem(49, createItem(Material.ARROW, ChatColor.YELLOW + "← Back", null));
    }
    
    private ItemStack createToggleItem(Material material, String name, boolean enabled, String description) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + description);
        lore.add("");
        lore.add(ChatColor.GRAY + "Status: " + (enabled ? ChatColor.GREEN + "✓ Enabled" : ChatColor.RED + "✗ Disabled"));
        lore.add("");
        lore.add(ChatColor.YELLOW + "Click to toggle!");
        
        return createItem(material, 
            (enabled ? ChatColor.GREEN : ChatColor.RED) + name, lore);
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
        
        if (resident == null || !resident.hasTown()) return;
        
        player.closeInventory();
        
        switch (slot) {
            case 20: // PvP
                player.performCommand("plot set pvp");
                Bukkit.getScheduler().runTaskLater(plugin, () -> 
                    new SettingsMenuGUI(plugin, player).open(), 2L);
                break;
            case 22: // Explosions
                player.performCommand("plot set explosions");
                Bukkit.getScheduler().runTaskLater(plugin, () -> 
                    new SettingsMenuGUI(plugin, player).open(), 2L);
                break;
            case 24: // Mobs
                player.performCommand("plot set mobs");
                Bukkit.getScheduler().runTaskLater(plugin, () -> 
                    new SettingsMenuGUI(plugin, player).open(), 2L);
                break;
            case 29: // Public
                player.performCommand("town toggle public");
                Bukkit.getScheduler().runTaskLater(plugin, () -> 
                    new SettingsMenuGUI(plugin, player).open(), 2L);
                break;
            case 33: // Plot Permissions
                new PlotMenuGUI(plugin, player).open();
                break;
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
