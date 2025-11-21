package org.r7l.interim.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * Standalone PlaceholderAPI expansion for Interim.
 * This can be installed directly into PlaceholderAPI's expansions folder.
 * 
 * Provides 40+ placeholders for towns, nations, residents, claims, and statistics.
 * Requires Interim plugin to be installed on the server.
 */
public class InterimExpansion extends PlaceholderExpansion {
    
    private Plugin interimPlugin;
    private Object dataManager;
    
    @Override
    @NotNull
    public String getIdentifier() {
        return "interim";
    }
    
    @Override
    @NotNull
    public String getAuthor() {
        return "r7l-labs";
    }
    
    @Override
    @NotNull
    public String getVersion() {
        return "00002";
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public boolean canRegister() {
        // Check if Interim plugin is installed
        interimPlugin = Bukkit.getPluginManager().getPlugin("Interim");
        if (interimPlugin == null) {
            return false;
        }
        
        // Get DataManager via reflection
        try {
            Method getDataManager = interimPlugin.getClass().getMethod("getDataManager");
            dataManager = getDataManager.invoke(interimPlugin);
            return dataManager != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    @Nullable
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null || dataManager == null) {
            return "";
        }
        
        try {
            // Get resident
            Object resident = invokeMethod(dataManager, "getResident", player.getUniqueId());
            
            if (resident == null) {
                return handleNoResident(params);
            }
            
            // Get town
            Object town = invokeMethod(resident, "getTown");
            Object nation = null;
            if (town != null) {
                nation = invokeMethod(town, "getNation");
            }
            
            // Player/Resident placeholders
            if (params.equals("player_has_town")) {
                return String.valueOf(town != null);
            }
            if (params.equals("player_has_nation")) {
                return String.valueOf(nation != null);
            }
            if (params.equals("player_rank")) {
                Object rank = invokeMethod(resident, "getRank");
                return rank != null ? rank.toString() : "RESIDENT";
            }
            if (params.equals("player_rank_formatted")) {
                Object rank = invokeMethod(resident, "getRank");
                return formatRank(rank != null ? rank.toString() : "RESIDENT");
            }
            
            // Town placeholders
            if (params.startsWith("town_")) {
                if (town == null) return "";
                return handleTownPlaceholder(params.substring(5), town);
            }
            
            // Nation placeholders
            if (params.startsWith("nation_")) {
                if (nation == null) return "";
                return handleNationPlaceholder(params.substring(7), nation);
            }
            
            // Location-based placeholders
            if (params.startsWith("location_")) {
                if (!(player instanceof Player)) return "";
                return handleLocationPlaceholder(params.substring(9), (Player) player);
            }
            
            // Statistics placeholders
            if (params.startsWith("stats_")) {
                return handleStatsPlaceholder(params.substring(6));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private String handleNoResident(String params) {
        if (params.equals("player_has_town") || params.equals("player_has_nation")) {
            return "false";
        }
        if (params.equals("player_rank") || params.equals("player_rank_formatted")) {
            return "None";
        }
        if (params.startsWith("stats_")) {
            return handleStatsPlaceholder(params.substring(6));
        }
        return "";
    }
    
    private String handleTownPlaceholder(String param, Object town) {
        try {
            switch (param) {
                case "name":
                    return String.valueOf(invokeMethod(town, "getName"));
                case "mayor":
                    Object mayorUuid = invokeMethod(town, "getMayor");
                    if (mayorUuid instanceof java.util.UUID) {
                        OfflinePlayer mayor = Bukkit.getOfflinePlayer((java.util.UUID) mayorUuid);
                        return mayor.getName() != null ? mayor.getName() : "Unknown";
                    }
                    return "Unknown";
                case "residents_count":
                    Object residents = invokeMethod(town, "getResidents");
                    if (residents instanceof java.util.Collection) {
                        return String.valueOf(((java.util.Collection<?>) residents).size());
                    }
                    return "0";
                case "claims_count":
                    Object claims = invokeMethod(town, "getClaims");
                    if (claims instanceof java.util.Collection) {
                        return String.valueOf(((java.util.Collection<?>) claims).size());
                    }
                    return "0";
                case "bank":
                    Object bank = invokeMethod(town, "getBank");
                    return String.format("%.2f", bank != null ? (Double) bank : 0.0);
                case "pvp":
                    return String.valueOf(invokeMethod(town, "isPvp"));
                case "explosions":
                    return String.valueOf(invokeMethod(town, "isExplosions"));
                case "mobs":
                    return String.valueOf(invokeMethod(town, "isMobSpawning"));
                case "open":
                    return String.valueOf(invokeMethod(town, "isOpen"));
                case "has_spawn":
                    Object spawn = invokeMethod(town, "getSpawn");
                    return String.valueOf(spawn != null);
                case "board":
                    Object board = invokeMethod(town, "getBoard");
                    return board != null ? String.valueOf(board) : "";
                case "color":
                    Object color = invokeMethod(town, "getColor");
                    return color != null ? color.toString() : "WHITE";
                case "color_formatted":
                    Object colorObj = invokeMethod(town, "getColor");
                    if (colorObj != null) {
                        Object chatColor = invokeMethod(colorObj, "getChatColor");
                        return chatColor + colorObj.toString();
                    }
                    return "WHITE";
                case "founded":
                    Object founded = invokeMethod(town, "getFounded");
                    return String.valueOf(founded);
                case "assistants_count":
                    Object assistants = invokeMethod(town, "getAssistants");
                    if (assistants instanceof java.util.Collection) {
                        return String.valueOf(((java.util.Collection<?>) assistants).size());
                    }
                    return "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private String handleNationPlaceholder(String param, Object nation) {
        try {
            switch (param) {
                case "name":
                    return String.valueOf(invokeMethod(nation, "getName"));
                case "capital":
                    Object capitalUuid = invokeMethod(nation, "getCapital");
                    if (capitalUuid instanceof java.util.UUID) {
                        Object capital = invokeMethod(dataManager, "getTown", capitalUuid);
                        return capital != null ? String.valueOf(invokeMethod(capital, "getName")) : "Unknown";
                    }
                    return "Unknown";
                case "leader":
                    Object capUuid = invokeMethod(nation, "getCapital");
                    if (capUuid instanceof java.util.UUID) {
                        Object cap = invokeMethod(dataManager, "getTown", capUuid);
                        if (cap != null) {
                            Object mayorUuid = invokeMethod(cap, "getMayor");
                            if (mayorUuid instanceof java.util.UUID) {
                                OfflinePlayer leader = Bukkit.getOfflinePlayer((java.util.UUID) mayorUuid);
                                return leader.getName() != null ? leader.getName() : "Unknown";
                            }
                        }
                    }
                    return "Unknown";
                case "towns_count":
                    Object towns = invokeMethod(nation, "getTowns");
                    if (towns instanceof java.util.Collection) {
                        return String.valueOf(((java.util.Collection<?>) towns).size());
                    }
                    return "0";
                case "bank":
                    Object bank = invokeMethod(nation, "getBank");
                    return String.format("%.2f", bank != null ? (Double) bank : 0.0);
                case "board":
                    Object board = invokeMethod(nation, "getBoard");
                    return board != null ? String.valueOf(board) : "";
                case "color":
                    Object color = invokeMethod(nation, "getColor");
                    return color != null ? color.toString() : "WHITE";
                case "color_formatted":
                    Object colorObj = invokeMethod(nation, "getColor");
                    if (colorObj != null) {
                        Object chatColor = invokeMethod(colorObj, "getChatColor");
                        return chatColor + colorObj.toString();
                    }
                    return "WHITE";
                case "founded":
                    Object founded = invokeMethod(nation, "getFounded");
                    return String.valueOf(founded);
                case "allies_count":
                    Object allies = invokeMethod(nation, "getAllies");
                    if (allies instanceof java.util.Collection) {
                        return String.valueOf(((java.util.Collection<?>) allies).size());
                    }
                    return "0";
                case "enemies_count":
                    Object enemies = invokeMethod(nation, "getEnemies");
                    if (enemies instanceof java.util.Collection) {
                        return String.valueOf(((java.util.Collection<?>) enemies).size());
                    }
                    return "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private String handleLocationPlaceholder(String param, Player player) {
        try {
            int chunkX = player.getLocation().getChunk().getX();
            int chunkZ = player.getLocation().getChunk().getZ();
            String world = player.getWorld().getName();
            
            Object claim = invokeMethod(dataManager, "getClaim", world, chunkX, chunkZ);
            
            switch (param) {
                case "has_claim":
                    return String.valueOf(claim != null);
                case "town":
                    if (claim == null) return "Wilderness";
                    Object town = invokeMethod(claim, "getTown");
                    return town != null ? String.valueOf(invokeMethod(town, "getName")) : "Unknown";
                case "nation":
                    if (claim == null) return "";
                    Object town2 = invokeMethod(claim, "getTown");
                    if (town2 == null) return "";
                    Object hasNation = invokeMethod(town2, "hasNation");
                    if (hasNation != null && !(Boolean) hasNation) return "";
                    Object nation = invokeMethod(town2, "getNation");
                    return nation != null ? String.valueOf(invokeMethod(nation, "getName")) : "";
                case "type":
                    if (claim == null) return "WILDERNESS";
                    Object type = invokeMethod(claim, "getType");
                    return type != null ? type.toString() : "WILDERNESS";
                case "can_build":
                    if (claim == null) return "true";
                    Object claimTown = invokeMethod(claim, "getTown");
                    if (claimTown == null) return "true";
                    Object resident = invokeMethod(dataManager, "getResident", player.getUniqueId());
                    if (resident == null) return "false";
                    Object playerTown = invokeMethod(resident, "getTown");
                    return String.valueOf(playerTown == claimTown);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private String handleStatsPlaceholder(String param) {
        try {
            switch (param) {
                case "total_towns":
                    Object towns = invokeMethod(dataManager, "getTowns");
                    if (towns instanceof java.util.Collection) {
                        return String.valueOf(((java.util.Collection<?>) towns).size());
                    }
                    return "0";
                case "total_nations":
                    Object nations = invokeMethod(dataManager, "getNations");
                    if (nations instanceof java.util.Collection) {
                        return String.valueOf(((java.util.Collection<?>) nations).size());
                    }
                    return "0";
                case "total_residents":
                    Object residents = invokeMethod(dataManager, "getResidents");
                    if (residents instanceof java.util.Collection) {
                        return String.valueOf(((java.util.Collection<?>) residents).size());
                    }
                    return "0";
                case "total_claims":
                    Object claims = invokeMethod(dataManager, "getClaims");
                    if (claims instanceof java.util.Collection) {
                        return String.valueOf(((java.util.Collection<?>) claims).size());
                    }
                    return "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private String formatRank(String rank) {
        switch (rank) {
            case "MAYOR":
                return "§6Mayor";
            case "ASSISTANT":
                return "§eAssistant";
            case "RESIDENT":
                return "§aResident";
            default:
                return rank;
        }
    }
    
    /**
     * Helper method to invoke methods via reflection.
     */
    private Object invokeMethod(Object obj, String methodName, Object... args) {
        try {
            Class<?>[] paramTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i].getClass();
                // Handle primitives
                if (args[i] instanceof Integer) paramTypes[i] = int.class;
                else if (args[i] instanceof String) paramTypes[i] = String.class;
            }
            
            Method method = obj.getClass().getMethod(methodName, paramTypes);
            return method.invoke(obj, args);
        } catch (Exception e) {
            // Try without args if failed
            try {
                Method method = obj.getClass().getMethod(methodName);
                return method.invoke(obj);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }
}
