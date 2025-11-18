package org.r7l.interim.integration;

import org.geysermc.floodgate.api.FloodgateApi;
import org.r7l.interim.Interim;

import java.util.UUID;

public class FloodgateIntegration {
    
    private final Interim plugin;
    private FloodgateApi floodgateApi;
    private boolean enabled;
    
    public FloodgateIntegration(Interim plugin) {
        this.plugin = plugin;
        this.enabled = false;
    }
    
    public void enable() {
        try {
            floodgateApi = FloodgateApi.getInstance();
            enabled = true;
            plugin.getLogger().info("Floodgate integration enabled! Bedrock players fully supported.");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to hook into Floodgate: " + e.getMessage());
            enabled = false;
        }
    }
    
    public void disable() {
        enabled = false;
        floodgateApi = null;
    }
    
    public boolean isEnabled() {
        return enabled && floodgateApi != null;
    }
    
    /**
     * Check if a player is a Bedrock player via Geyser/Floodgate
     */
    public boolean isBedrockPlayer(UUID uuid) {
        if (!isEnabled()) {
            return false;
        }
        
        try {
            return floodgateApi.isFloodgatePlayer(uuid);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get the Bedrock username for a player (without prefix)
     */
    public String getBedrockUsername(UUID uuid) {
        if (!isEnabled() || !isBedrockPlayer(uuid)) {
            return null;
        }
        
        try {
            return floodgateApi.getPlayer(uuid).getUsername();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Get the Java UUID for a Bedrock player
     */
    public UUID getJavaUuid(UUID bedrockUuid) {
        if (!isEnabled()) {
            return bedrockUuid;
        }
        
        try {
            if (isBedrockPlayer(bedrockUuid)) {
                // Bedrock players already have their linked UUID
                return bedrockUuid;
            }
        } catch (Exception e) {
            // Ignore
        }
        
        return bedrockUuid;
    }
    
    /**
     * Format a display name that works for both Java and Bedrock players
     */
    public String getDisplayName(UUID uuid, String defaultName) {
        if (!isEnabled() || !isBedrockPlayer(uuid)) {
            return defaultName;
        }
        
        String bedrockName = getBedrockUsername(uuid);
        return bedrockName != null ? bedrockName : defaultName;
    }
}
