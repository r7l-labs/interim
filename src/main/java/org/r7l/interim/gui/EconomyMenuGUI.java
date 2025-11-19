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
 * Economy management GUI
 */
public class EconomyMenuGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    
    public EconomyMenuGUI(Interim plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 27, 
            ChatColor.GOLD + "✦ Economy Menu ✦");
        
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
        boolean hasPerms = resident.isMayor() || resident.isAssistant();
        
        // Town bank info
        List<String> townBankLore = new ArrayList<>();
        townBankLore.add(ChatColor.GRAY + "Balance: " + ChatColor.GOLD + "$" + String.format("%.2f", town.getBank()));
        townBankLore.add("");
        townBankLore.add(ChatColor.YELLOW + "Town Treasury");
        
        inventory.setItem(4, createItem(Material.GOLD_BLOCK, 
            ChatColor.GOLD + "⚜ Town Bank ⚜", townBankLore));
        
        // Deposit
        inventory.setItem(11, createItem(Material.EMERALD, 
            ChatColor.GREEN + "⇧ Deposit", 
            List.of(
                ChatColor.GRAY + "Add money to town bank",
                "",
                ChatColor.YELLOW + "Click to deposit!"
            )));
        
        // Withdraw (if permitted)
        if (hasPerms) {
            inventory.setItem(13, createItem(Material.GOLD_INGOT, 
                ChatColor.RED + "⇩ Withdraw", 
                List.of(
                    ChatColor.GRAY + "Take money from town bank",
                    ChatColor.GRAY + "Requires: " + ChatColor.YELLOW + "Mayor/Assistant",
                    "",
                    ChatColor.YELLOW + "Click to withdraw!"
                )));
        } else {
            inventory.setItem(13, createItem(Material.GRAY_STAINED_GLASS_PANE, 
                ChatColor.GRAY + "⇩ Withdraw", 
                List.of(
                    ChatColor.RED + "✗ Mayor/Assistant only"
                )));
        }
        
        // View transactions (if exists in future)
        inventory.setItem(15, createItem(Material.WRITABLE_BOOK, 
            ChatColor.AQUA + "⇄ Transactions", 
            List.of(
                ChatColor.GRAY + "View transaction history",
                "",
                ChatColor.GRAY + "Coming soon!"
            )));
        
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
        
        player.closeInventory();
        
        switch (slot) {
            case 11: // Deposit
                player.sendMessage(ChatColor.YELLOW + "Type in chat: " + ChatColor.WHITE + "/town deposit <amount>");
                break;
            case 13: // Withdraw
                if (hasPerms) {
                    player.sendMessage(ChatColor.YELLOW + "Type in chat: " + ChatColor.WHITE + "/town withdraw <amount>");
                }
                break;
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
}
