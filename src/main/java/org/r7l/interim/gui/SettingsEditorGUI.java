package org.r7l.interim.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.r7l.interim.model.Town;
import org.r7l.interim.model.Nation;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI for editing town/nation settings (toggles for PvP, explosions, mob spawning, etc.)
 */
public class SettingsEditorGUI implements InventoryHolder {
    private final Inventory inventory;
    private final Player player;
    private final Town town;
    private final Nation nation;

    public SettingsEditorGUI(Player player, Town town, Nation nation) {
        this.player = player;
        this.town = town;
        this.nation = nation;
        this.inventory = Bukkit.createInventory(this, 27, ChatColor.BLUE + "Settings Editor");
        buildSettings();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void buildSettings() {
        int slot = 0;
        if (town != null) {
            inventory.setItem(slot++, createToggleItem("PvP", town.isPvp()));
            inventory.setItem(slot++, createToggleItem("Explosions", town.isExplosions()));
            inventory.setItem(slot++, createToggleItem("Mob Spawning", town.isMobSpawning()));
            // Add more toggles here as needed
        }
        if (nation != null) {
            inventory.setItem(slot++, createToggleItem("Nation Board", true)); // Example toggle
            // Add more nation toggles here
        }
    }

    private ItemStack createToggleItem(String name, boolean enabled) {
        ItemStack item = new ItemStack(enabled ? Material.LIME_DYE : Material.GRAY_DYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + name + ": " + (enabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to toggle");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void open() {
        player.openInventory(inventory);
    }
}
