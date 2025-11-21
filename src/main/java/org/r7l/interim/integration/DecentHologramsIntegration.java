package org.r7l.interim.integration;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.r7l.interim.Interim;
import org.r7l.interim.model.Nation;
import org.r7l.interim.model.Town;
import org.r7l.interim.storage.DataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Integration with DecentHolograms for displaying town and nation information.
 * Creates holograms at town spawns and other important locations.
 */
public class DecentHologramsIntegration {
    
    private final Interim plugin;
    private final DataManager dataManager;
    private final Logger logger;
    private boolean enabled;
    private final Map<UUID, String> townHolograms; // Town UUID -> Hologram name
    private final Map<UUID, String> nationHolograms; // Nation UUID -> Hologram name
    
    public DecentHologramsIntegration(Interim plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
        this.logger = plugin.getLogger();
        this.enabled = false;
        this.townHolograms = new ConcurrentHashMap<>();
        this.nationHolograms = new ConcurrentHashMap<>();
    }
    
    /**
     * Enables the DecentHolograms integration.
     */
    public void enable() {
        if (Bukkit.getPluginManager().getPlugin("DecentHolograms") == null) {
            logger.warning("DecentHolograms not found! Hologram integration disabled.");
            return;
        }
        
        try {
            enabled = true;
            logger.info("DecentHolograms integration enabled!");
            
            // Create holograms for all towns with spawns
            createAllTownHolograms();
        } catch (Exception e) {
            logger.severe("Failed to enable DecentHolograms integration: " + e.getMessage());
            enabled = false;
        }
    }
    
    /**
     * Disables the DecentHolograms integration and removes all holograms.
     */
    public void disable() {
        if (!enabled) return;
        
        try {
            // Remove all town holograms
            for (String hologramName : townHolograms.values()) {
                removeHologram(hologramName);
            }
            townHolograms.clear();
            
            // Remove all nation holograms
            for (String hologramName : nationHolograms.values()) {
                removeHologram(hologramName);
            }
            nationHolograms.clear();
            
            enabled = false;
            logger.info("DecentHolograms integration disabled!");
        } catch (Exception e) {
            logger.severe("Error disabling DecentHolograms integration: " + e.getMessage());
        }
    }
    
    /**
     * Checks if the DecentHolograms integration is enabled.
     * 
     * @return true if enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Creates holograms for all towns that have spawn points set.
     */
    public void createAllTownHolograms() {
        if (!enabled) return;
        
        for (Town town : dataManager.getTowns()) {
            if (town.getSpawn() != null) {
                createTownHologram(town);
            }
        }
    }
    
    /**
     * Creates or updates a hologram for a town at its spawn location.
     * 
     * @param town The town to create a hologram for
     */
    public void createTownHologram(Town town) {
        if (!enabled || town.getSpawn() == null) return;
        
        try {
            String hologramName = "interim_town_" + town.getUuid().toString();
            Location location = town.getSpawn().clone().add(0, 3, 0); // 3 blocks above spawn
            
            // Remove existing hologram if it exists
            if (townHolograms.containsKey(town.getUuid())) {
                removeHologram(townHolograms.get(town.getUuid()));
            }
            
            // Create hologram lines
            List<String> lines = buildTownHologramLines(town);
            
            // Create hologram
            Hologram hologram = DHAPI.createHologram(hologramName, location, lines);
            if (hologram != null) {
                townHolograms.put(town.getUuid(), hologramName);
            }
        } catch (Exception e) {
            logger.warning("Failed to create hologram for town " + town.getName() + ": " + e.getMessage());
        }
    }
    
    /**
     * Builds the lines for a town hologram with town information.
     * 
     * @param town The town to build lines for
     * @return List of formatted hologram lines
     */
    private List<String> buildTownHologramLines(Town town) {
        List<String> lines = new ArrayList<>();
        String colorCode = town.getColor().getChatColor().toString();
        
        lines.add(colorCode + "╔═══════════════╗");
        lines.add(colorCode + "║ " + town.getName() + " ║");
        lines.add(colorCode + "╚═══════════════╝");
        lines.add("§7Mayor: §f" + getMayorName(town));
        lines.add("§7Residents: §f" + town.getResidents().size());
        lines.add("§7Claims: §f" + town.getClaims().size());
        
        if (town.hasNation()) {
            Nation nation = town.getNation();
            lines.add("§7Nation: " + nation.getColor().getChatColor() + nation.getName());
        } else {
            lines.add("§7Nation: §cNone");
        }
        
        lines.add("");
        lines.add("§e⚑ Town Spawn ⚑");
        
        return lines;
    }
    
    /**
     * Updates an existing town hologram with new information.
     * 
     * @param town The town to update the hologram for
     */
    public void updateTownHologram(Town town) {
        if (!enabled) return;
        
        String hologramName = townHolograms.get(town.getUuid());
        if (hologramName == null) {
            // Hologram doesn't exist, create it
            if (town.getSpawn() != null) {
                createTownHologram(town);
            }
            return;
        }
        
        try {
            Hologram hologram = DHAPI.getHologram(hologramName);
            if (hologram == null) {
                // Hologram was deleted, recreate it
                townHolograms.remove(town.getUuid());
                createTownHologram(town);
                return;
            }
            
            // Update lines
            List<String> lines = buildTownHologramLines(town);
            DHAPI.setHologramLines(hologram, lines);
            
            // Update location if spawn changed
            if (town.getSpawn() != null) {
                Location newLocation = town.getSpawn().clone().add(0, 3, 0);
                hologram.setLocation(newLocation);
            }
        } catch (Exception e) {
            logger.warning("Failed to update hologram for town " + town.getName() + ": " + e.getMessage());
        }
    }
    
    /**
     * Removes the hologram for a town.
     * 
     * @param town The town to remove the hologram for
     */
    public void removeTownHologram(Town town) {
        if (!enabled) return;
        
        String hologramName = townHolograms.remove(town.getUuid());
        if (hologramName != null) {
            removeHologram(hologramName);
        }
    }
    
    /**
     * Creates a custom hologram at a specific location with custom lines.
     * Useful for special events, nation capitals, or custom markers.
     * 
     * @param name Unique name for the hologram
     * @param location Location to place the hologram
     * @param lines Lines of text to display
     * @return true if created successfully, false otherwise
     */
    public boolean createCustomHologram(String name, Location location, List<String> lines) {
        if (!enabled) return false;
        
        try {
            String hologramName = "interim_custom_" + name;
            Hologram hologram = DHAPI.createHologram(hologramName, location, lines);
            return hologram != null;
        } catch (Exception e) {
            logger.warning("Failed to create custom hologram " + name + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Removes a custom hologram by name.
     * 
     * @param name The name of the custom hologram to remove
     */
    public void removeCustomHologram(String name) {
        if (!enabled) return;
        
        String hologramName = "interim_custom_" + name;
        removeHologram(hologramName);
    }
    
    /**
     * Creates a hologram at a nation's capital town spawn.
     * 
     * @param nation The nation to create a hologram for
     */
    public void createNationCapitalHologram(Nation nation) {
        if (!enabled) return;
        
        Town capital = dataManager.getTown(nation.getCapital());
        if (capital == null || capital.getSpawn() == null) return;
        
        try {
            String hologramName = "interim_nation_" + nation.getUuid().toString();
            Location location = capital.getSpawn().clone().add(0, 5, 0); // 5 blocks above spawn
            
            // Remove existing hologram if it exists
            if (nationHolograms.containsKey(nation.getUuid())) {
                removeHologram(nationHolograms.get(nation.getUuid()));
            }
            
            // Create hologram lines
            List<String> lines = buildNationHologramLines(nation, capital);
            
            // Create hologram
            Hologram hologram = DHAPI.createHologram(hologramName, location, lines);
            if (hologram != null) {
                nationHolograms.put(nation.getUuid(), hologramName);
            }
        } catch (Exception e) {
            logger.warning("Failed to create nation hologram for " + nation.getName() + ": " + e.getMessage());
        }
    }
    
    /**
     * Builds the lines for a nation capital hologram.
     * 
     * @param nation The nation
     * @param capital The capital town
     * @return List of formatted hologram lines
     */
    private List<String> buildNationHologramLines(Nation nation, Town capital) {
        List<String> lines = new ArrayList<>();
        String colorCode = nation.getColor().getChatColor().toString();
        
        lines.add(colorCode + "✦═══════════════✦");
        lines.add(colorCode + "⚔ " + nation.getName() + " ⚔");
        lines.add(colorCode + "✦═══════════════✦");
        lines.add("§7Capital: §f" + capital.getName());
        lines.add("§7Leader: §f" + getMayorName(capital));
        lines.add("§7Towns: §f" + nation.getTowns().size());
        lines.add("§7Allies: §a" + nation.getAllies().size() + " §7Enemies: §c" + nation.getEnemies().size());
        lines.add("");
        lines.add("§6★ Nation Capital ★");
        
        return lines;
    }
    
    /**
     * Updates a nation capital hologram.
     * 
     * @param nation The nation to update
     */
    public void updateNationCapitalHologram(Nation nation) {
        if (!enabled) return;
        
        String hologramName = nationHolograms.get(nation.getUuid());
        if (hologramName == null) {
            createNationCapitalHologram(nation);
            return;
        }
        
        try {
            Hologram hologram = DHAPI.getHologram(hologramName);
            if (hologram == null) {
                nationHolograms.remove(nation.getUuid());
                createNationCapitalHologram(nation);
                return;
            }
            
            Town capital = dataManager.getTown(nation.getCapital());
            if (capital == null) return;
            
            List<String> lines = buildNationHologramLines(nation, capital);
            DHAPI.setHologramLines(hologram, lines);
            
            // Update location if capital spawn changed
            if (capital.getSpawn() != null) {
                Location newLocation = capital.getSpawn().clone().add(0, 5, 0);
                hologram.setLocation(newLocation);
            }
        } catch (Exception e) {
            logger.warning("Failed to update nation hologram for " + nation.getName() + ": " + e.getMessage());
        }
    }
    
    /**
     * Removes a nation capital hologram.
     * 
     * @param nation The nation to remove the hologram for
     */
    public void removeNationCapitalHologram(Nation nation) {
        if (!enabled) return;
        
        String hologramName = nationHolograms.remove(nation.getUuid());
        if (hologramName != null) {
            removeHologram(hologramName);
        }
    }
    
    /**
     * Helper method to remove a hologram by name.
     * 
     * @param hologramName The name of the hologram to remove
     */
    private void removeHologram(String hologramName) {
        try {
            Hologram hologram = DHAPI.getHologram(hologramName);
            if (hologram != null) {
                hologram.delete();
            }
        } catch (Exception e) {
            logger.warning("Failed to remove hologram " + hologramName + ": " + e.getMessage());
        }
    }
    
    /**
     * Gets the mayor's name for display in holograms.
     * 
     * @param town The town
     * @return The mayor's name or "Unknown"
     */
    private String getMayorName(Town town) {
        var mayor = Bukkit.getOfflinePlayer(town.getMayor());
        return mayor.getName() != null ? mayor.getName() : "Unknown";
    }
    
    /**
     * Refreshes all holograms, recreating them with updated information.
     */
    public void refreshAllHolograms() {
        if (!enabled) return;
        
        logger.info("Refreshing all holograms...");
        
        // Refresh town holograms
        for (Town town : dataManager.getTowns()) {
            if (town.getSpawn() != null) {
                updateTownHologram(town);
            }
        }
        
        // Refresh nation holograms
        for (Nation nation : dataManager.getNations()) {
            updateNationCapitalHologram(nation);
        }
        
        logger.info("Holograms refreshed!");
    }
}
