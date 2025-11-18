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
import org.r7l.interim.model.Town;
import org.r7l.interim.model.Resident;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * GUI to view all towns a player is a member of, all nations, and edit settings if owner.
 */
public class TownNationOverviewGUI implements InventoryHolder {
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    private final Resident resident;

    public TownNationOverviewGUI(Interim plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.resident = plugin.getDataManager().getResident(player.getUniqueId());
        this.inventory = Bukkit.createInventory(this, 54, ChatColor.BLUE + "Towns & Nations Overview");
        buildOverview();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void buildOverview() {
        int slot = 0;
        // Show all towns the player is a member of
        if (resident != null) {
            for (UUID townId : resident.getTowns().keySet()) {
                Town town = plugin.getDataManager().getTown(townId);
                if (town == null) continue;
                ItemStack item = new ItemStack(Material.BOOK);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.AQUA + town.getName());
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Members: " + town.getResidents().size());
                lore.add(ChatColor.GRAY + "Claims: " + town.getClaimCount());
                if (resident.isMayor(town.getUuid()) || resident.isAssistant(town.getUuid())) {
                    lore.add(ChatColor.YELLOW + "Click to edit settings");
                }
                meta.setLore(lore);
                item.setItemMeta(meta);
                inventory.setItem(slot++, item);
            }
        }
        // Show all nations
        for (Nation nation : plugin.getDataManager().getNations()) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + nation.getName());
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Towns: " + nation.getTowns().size());
            lore.add(ChatColor.GRAY + "Bank: " + nation.getBank());
            if (nation.getCapital() != null) {
                Town capital = plugin.getDataManager().getTown(nation.getCapital());
                if (capital != null) lore.add(ChatColor.GRAY + "Capital: " + capital.getName());
            }
            if (resident != null && nation.getTowns().contains(resident.getPrimaryTown() != null ? resident.getPrimaryTown().getUuid() : null)) {
                lore.add(ChatColor.YELLOW + "Click to edit settings");
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            inventory.setItem(slot++, item);
        }
    }

    public void open() {
        player.openInventory(inventory);
    }
}
