package org.r7l.interim.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
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
 * Interactive GUI map showing claimed chunks around the player
 */
public class ClaimMapGUI implements InventoryHolder {
    
    private final Interim plugin;
    private final Player player;
    private final Inventory inventory;
    private final int centerChunkX;
    private final int centerChunkZ;
    private final String worldName;
    
    // Map dimensions (9 wide x 6 tall = 54 slots)
    private static final int MAP_WIDTH = 9;
    private static final int MAP_HEIGHT = 6;
    private static final int TOTAL_SLOTS = MAP_WIDTH * MAP_HEIGHT;
    
    // Map range (how many chunks to show in each direction from center)
    private static final int RANGE_X = 4; // 9 chunks wide (4 left, center, 4 right)
    private static final int RANGE_Z = 3; // 6 chunks tall (3 up, center, 3 down) - adjusted
    
    public ClaimMapGUI(Interim plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.centerChunkX = player.getLocation().getChunk().getX();
        this.centerChunkZ = player.getLocation().getChunk().getZ();
        this.worldName = player.getWorld().getName();
        
        this.inventory = Bukkit.createInventory(this, TOTAL_SLOTS, 
            ChatColor.DARK_GREEN + "Claim Map " + ChatColor.GRAY + "(" + centerChunkX + ", " + centerChunkZ + ")");
        
        buildMap();
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    private void buildMap() {
        int slot = 0;
        
        // Build map from top to bottom, left to right
        for (int z = centerChunkZ - RANGE_Z; z <= centerChunkZ + RANGE_Z; z++) {
            for (int x = centerChunkX - RANGE_X; x <= centerChunkX + RANGE_X; x++) {
                if (slot >= TOTAL_SLOTS) break;
                
                ItemStack item = createChunkItem(x, z);
                inventory.setItem(slot, item);
                slot++;
            }
        }
    }
    
    private ItemStack createChunkItem(int chunkX, int chunkZ) {
        Claim claim = plugin.getDataManager().getClaim(worldName, chunkX, chunkZ);
        boolean isPlayerChunk = (chunkX == centerChunkX && chunkZ == centerChunkZ);
        
        ItemStack item;
        List<String> lore = new ArrayList<>();
        
        if (claim != null) {
            // Claimed chunk
            Town town = claim.getTown();
            
            // Determine material based on ownership
            Resident resident = plugin.getDataManager().getResident(player.getUniqueId());
            boolean isOwnTown = resident != null && resident.isInTown(town);
            
            if (isOwnTown) {
                item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            } else if (town.hasNation() && resident != null && resident.hasNation() 
                       && town.getNation().equals(resident.getNation())) {
                item = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
            } else {
                item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            }
            
            ItemMeta meta = item.getItemMeta();
            
            if (isPlayerChunk) {
                meta.setDisplayName(ChatColor.YELLOW + "» " + ChatColor.WHITE + 
                    "Chunk [" + chunkX + ", " + chunkZ + "]" + ChatColor.YELLOW + " «");
            } else {
                meta.setDisplayName(ChatColor.WHITE + "Chunk [" + chunkX + ", " + chunkZ + "]");
            }
            
            lore.add(ChatColor.GRAY + "Owner: " + ChatColor.GREEN + town.getName());
            lore.add(ChatColor.GRAY + "Type: " + ChatColor.AQUA + claim.getType().toString());
            
            if (town.hasNation()) {
                lore.add(ChatColor.GRAY + "Nation: " + ChatColor.GOLD + town.getNation().getName());
            }
            
            lore.add("");
            lore.add(ChatColor.GRAY + "Protection Flags:");
            lore.add(ChatColor.GRAY + "  PvP: " + (town.isPvp() ? ChatColor.RED + "✔" : ChatColor.GREEN + "✘"));
            lore.add(ChatColor.GRAY + "  Explosions: " + (town.isExplosions() ? ChatColor.RED + "✔" : ChatColor.GREEN + "✘"));
            lore.add(ChatColor.GRAY + "  Mobs: " + (town.isMobSpawning() ? ChatColor.GREEN + "✔" : ChatColor.RED + "✘"));
            
            // Add action hints
            if (isOwnTown && resident != null && (resident.isMayor(town.getUuid()) || resident.isAssistant(town.getUuid()))) {
                lore.add("");
                lore.add(ChatColor.YELLOW + "Click to unclaim");
            }
            
            meta.setLore(lore);
            item.setItemMeta(meta);
            
        } else {
            // Unclaimed chunk
            item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            
            if (isPlayerChunk) {
                meta.setDisplayName(ChatColor.YELLOW + "» " + ChatColor.WHITE + 
                    "Chunk [" + chunkX + ", " + chunkZ + "]" + ChatColor.YELLOW + " «");
            } else {
                meta.setDisplayName(ChatColor.WHITE + "Chunk [" + chunkX + ", " + chunkZ + "]");
            }
            
            lore.add(ChatColor.GRAY + "Status: " + ChatColor.RED + "Unclaimed");
            lore.add(ChatColor.GRAY + "World: " + ChatColor.WHITE + worldName);
            
            Resident resident = plugin.getDataManager().getResident(player.getUniqueId());
            if (resident != null && resident.hasTown()) {
                Town town = resident.getTown();
                if (resident.isMayor() || resident.isAssistant()) {
                    lore.add("");
                    lore.add(ChatColor.GREEN + "Click to claim for " + town.getName());
                    
                    // Check if adjacent to existing claims
                    boolean isAdjacent = isAdjacentToTownClaim(town, chunkX, chunkZ);
                    if (!isAdjacent && plugin.getConfig().getBoolean("town.require-adjacent-claims", true)) {
                        lore.add(ChatColor.RED + "⚠ Must be adjacent to existing claim");
                    }
                }
            } else {
                lore.add("");
                lore.add(ChatColor.GRAY + "Join a town to claim");
            }
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    private boolean isAdjacentToTownClaim(Town town, int chunkX, int chunkZ) {
        // Check if chunk is adjacent to any of the town's existing claims
        for (Claim claim : town.getClaims()) {
            if (!claim.getWorldName().equals(worldName)) continue;
            
            int dx = Math.abs(claim.getX() - chunkX);
            int dz = Math.abs(claim.getZ() - chunkZ);
            
            // Adjacent if one coordinate matches and other is 1 away
            if ((dx == 1 && dz == 0) || (dx == 0 && dz == 1)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Handle click on a chunk in the map
     */
    public void handleClick(Player player, int slot) {
        if (slot < 0 || slot >= TOTAL_SLOTS) return;
        
        // Calculate which chunk was clicked
        int row = slot / MAP_WIDTH;
        int col = slot % MAP_WIDTH;
        
        int clickedChunkX = centerChunkX - RANGE_X + col;
        int clickedChunkZ = centerChunkZ - RANGE_Z + row;
        
        Claim existingClaim = plugin.getDataManager().getClaim(worldName, clickedChunkX, clickedChunkZ);
        Resident resident = plugin.getDataManager().getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(ChatColor.RED + "You must be in a town to claim land!");
            return;
        }
        
        Town town = resident.getTown();
        
        // Check permissions
        if (!resident.isMayor() && !resident.isAssistant()) {
            player.sendMessage(ChatColor.RED + "Only the mayor and assistants can claim/unclaim land!");
            return;
        }
        
        if (existingClaim != null) {
            // Try to unclaim
            if (!existingClaim.getTown().equals(town)) {
                player.sendMessage(ChatColor.RED + "This chunk belongs to " + existingClaim.getTown().getName() + "!");
                return;
            }
            
            // Unclaim the chunk
            town.removeClaim(existingClaim);
            plugin.getDataManager().removeClaim(existingClaim);
            plugin.getDataManager().saveAll();
            
            player.sendMessage(ChatColor.GREEN + "Unclaimed chunk [" + clickedChunkX + ", " + clickedChunkZ + "]!");
            
        } else {
            // Try to claim
            
            // Check max claims
            int maxClaims = plugin.getConfig().getInt("town.max-claims", 100);
            if (town.getClaimCount() >= maxClaims) {
                player.sendMessage(ChatColor.RED + "Your town has reached the maximum number of claims!");
                return;
            }
            
            // Check adjacency requirement
            if (plugin.getConfig().getBoolean("town.require-adjacent-claims", true)) {
                if (town.getClaimCount() > 0 && !isAdjacentToTownClaim(town, clickedChunkX, clickedChunkZ)) {
                    player.sendMessage(ChatColor.RED + "Claims must be adjacent to your existing territory!");
                    return;
                }
            }
            
            // Check economy
            double cost = plugin.getConfig().getDouble("town.claim-cost", 100.0);
            if (plugin.getEconomy() != null && !town.withdraw(cost)) {
                player.sendMessage(ChatColor.RED + "Your town doesn't have enough money! Cost: " + cost);
                return;
            }
            
            // Claim the chunk
            Chunk chunk = player.getWorld().getChunkAt(clickedChunkX, clickedChunkZ);
            Claim newClaim = new Claim(chunk, town);
            plugin.getDataManager().addClaim(newClaim);
            town.addClaim(newClaim);
            plugin.getDataManager().saveAll();
            
            player.sendMessage(ChatColor.GREEN + "Claimed chunk [" + clickedChunkX + ", " + clickedChunkZ + "] for " + town.getName() + "!");
        }
        
        // Refresh the map
        player.closeInventory();
        ClaimMapGUI newMap = new ClaimMapGUI(plugin, player);
        player.openInventory(newMap.getInventory());
    }
    
    /**
     * Open this GUI for the player
     */
    public void open() {
        player.openInventory(inventory);
    }
}
