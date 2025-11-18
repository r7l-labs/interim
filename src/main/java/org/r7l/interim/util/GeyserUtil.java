package org.r7l.interim.util;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.r7l.interim.Interim;

import java.util.UUID;

/**
 * Utility class for handling Geyser/Bedrock player compatibility
 */
public class GeyserUtil {
    
    /**
     * Get a player-friendly display name that works for both Java and Bedrock players
     */
    public static String getDisplayName(Interim plugin, UUID uuid, String defaultName) {
        if (plugin.getFloodgateIntegration() != null && plugin.getFloodgateIntegration().isEnabled()) {
            return plugin.getFloodgateIntegration().getDisplayName(uuid, defaultName);
        }
        return defaultName;
    }
    
    /**
     * Check if a player is a Bedrock player
     */
    public static boolean isBedrockPlayer(Interim plugin, UUID uuid) {
        if (plugin.getFloodgateIntegration() != null && plugin.getFloodgateIntegration().isEnabled()) {
            return plugin.getFloodgateIntegration().isBedrockPlayer(uuid);
        }
        return false;
    }
    
    /**
     * Check if a player is a Bedrock player
     */
    public static boolean isBedrockPlayer(Interim plugin, Player player) {
        return isBedrockPlayer(plugin, player.getUniqueId());
    }
    
    /**
     * Get a formatted player name for display (handles Bedrock prefixes)
     */
    public static String getPlayerName(Interim plugin, OfflinePlayer player) {
        if (player == null) {
            return "Unknown";
        }
        
        String name = player.getName();
        if (name == null) {
            name = player.getUniqueId().toString();
        }
        
        return getDisplayName(plugin, player.getUniqueId(), name);
    }
    
    /**
     * Send a message that's formatted appropriately for the player's client
     * Bedrock players may need simplified formatting in some cases
     */
    public static void sendMessage(CommandSender sender, String message) {
        // For now, send as-is. Can be enhanced later if Bedrock needs different formatting
        sender.sendMessage(message);
    }
    
    /**
     * Check if Geyser integration is available and enabled
     */
    public static boolean isGeyserEnabled(Interim plugin) {
        return plugin.getFloodgateIntegration() != null && plugin.getFloodgateIntegration().isEnabled();
    }
}
