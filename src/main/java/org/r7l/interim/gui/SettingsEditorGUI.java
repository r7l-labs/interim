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
import org.r7l.interim.model.Town;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GUI for editing town settings (toggles for PvP, explosions, mob spawning, etc.)
 */
public class SettingsEditorGUI implements InventoryHolder {
    private final Inventory inventory;
    private final Player player;
    private final Town town;
    private final Interim plugin;
    private final Map<Integer, String> toggleSlots = new HashMap<>();

    public SettingsEditorGUI(Interim plugin, Player player, Town town) {
        this.plugin = plugin;
        this.player = player;
        this.town = town;
        this.inventory = Bukkit.createInventory(this, 27, ChatColor.BLUE + "Town Settings");
        buildSettings();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void buildSettings() {
        toggleSlots.clear();
        // Use fixed slots for clarity
        int pvpSlot = 11;
        int explosionsSlot = 13;
        int mobsSlot = 15;

        inventory.setItem(pvpSlot, createToggleItem("PvP", town.isPvp()));
        toggleSlots.put(pvpSlot, "pvp");

        inventory.setItem(explosionsSlot, createToggleItem("Explosions", town.isExplosions()));
        toggleSlots.put(explosionsSlot, "explosions");

        inventory.setItem(mobsSlot, createToggleItem("Mob Spawning", town.isMobSpawning()));
        toggleSlots.put(mobsSlot, "mobs");
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

    public void handleClick(Player clicker, int rawSlot) {
        if (!clicker.getUniqueId().equals(player.getUniqueId())) return; // only owner player actions
        if (!toggleSlots.containsKey(rawSlot)) return;

        String key = toggleSlots.get(rawSlot);
        switch (key) {
            case "pvp" -> {
                town.setPvp(!town.isPvp());
            }
            case "explosions" -> {
                town.setExplosions(!town.isExplosions());
            }
            case "mobs" -> {
                town.setMobSpawning(!town.isMobSpawning());
            }
        }

        // Persist changes
        plugin.getDataManager().saveAll();

        // Rebuild to reflect new states
        buildSettings();
    }
}
