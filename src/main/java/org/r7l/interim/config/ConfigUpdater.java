package org.r7l.interim.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.r7l.interim.Interim;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * Handles automatic config.yml version updates and missing key additions
 */
public class ConfigUpdater {
    
    private final Interim plugin;
    
    public ConfigUpdater(Interim plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Update the config file with version comment and add missing keys
     */
    public void updateConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        
        if (!configFile.exists()) {
            // First time setup, save default config
            plugin.saveDefaultConfig();
            return;
        }
        
        try {
            // Read current config
            List<String> lines = Files.readAllLines(configFile.toPath());
            
            // Update version in header
            String currentVersion = plugin.getDescription().getVersion();
            boolean versionUpdated = false;
            
            for (int i = 0; i < lines.size() && i < 5; i++) {
                String line = lines.get(i);
                if (line.startsWith("# Version:")) {
                    String oldVersion = line.substring("# Version:".length()).trim();
                    if (!oldVersion.equals(currentVersion)) {
                        lines.set(i, "# Version: " + currentVersion);
                        versionUpdated = true;
                        plugin.getLogger().info("Updated config version: " + oldVersion + " -> " + currentVersion);
                    }
                    break;
                } else if (i == 1 && line.startsWith("# Interim Configuration")) {
                    // Add version line if it doesn't exist
                    lines.add(2, "# Version: " + currentVersion);
                    versionUpdated = true;
                    plugin.getLogger().info("Added version to config: " + currentVersion);
                    break;
                }
            }
            
            // Load both configurations
            FileConfiguration currentConfig = YamlConfiguration.loadConfiguration(configFile);
            FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                new InputStreamReader(plugin.getResource("config.yml"))
            );
            
            // Find and add missing keys
            Set<String> missingKeys = new HashSet<>();
            Set<String> defaultKeys = defaultConfig.getKeys(true);
            
            for (String key : defaultKeys) {
                if (!currentConfig.contains(key)) {
                    missingKeys.add(key);
                }
            }
            
            if (!missingKeys.isEmpty() || versionUpdated) {
                // Merge missing keys into current config
                for (String key : missingKeys) {
                    currentConfig.set(key, defaultConfig.get(key));
                    plugin.getLogger().info("Added missing config key: " + key);
                }
                
                // Save the updated config
                if (versionUpdated) {
                    // Write lines back with updated version
                    Files.write(configFile.toPath(), lines);
                }
                
                // Save merged config (this will add missing keys)
                if (!missingKeys.isEmpty()) {
                    currentConfig.save(configFile);
                }
                
                // Reload config in plugin
                plugin.reloadConfig();
                
                plugin.getLogger().info("Config updated successfully!");
            }
            
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to update config: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
