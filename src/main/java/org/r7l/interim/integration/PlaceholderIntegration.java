package org.r7l.interim.integration;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.r7l.interim.Interim;
import org.r7l.interim.model.*;
import org.r7l.interim.storage.DataManager;

/**
 * PlaceholderAPI integration for Interim.
 * Provides placeholders for towns, nations, residents, and claims.
 */
public class PlaceholderIntegration extends PlaceholderExpansion {
    
    private final Interim plugin;
    private final DataManager dataManager;
    
    public PlaceholderIntegration(Interim plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }
    
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
        return plugin.getDescription().getVersion();
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public boolean canRegister() {
        return true;
    }
    
    @Override
    @Nullable
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return "";
        }
        
        Resident resident = dataManager.getResident(player.getUniqueId());
        if (resident == null) {
            // Return defaults for players without resident data
            return handleNoResident(params);
        }
        
        Town town = resident.getTown();
        Nation nation = town != null ? town.getNation() : null;
        
        // Player/Resident placeholders
        if (params.equals("player_has_town")) {
            return String.valueOf(town != null);
        }
        if (params.equals("player_has_nation")) {
            return String.valueOf(nation != null);
        }
        if (params.equals("player_rank")) {
            return resident.getRank().toString();
        }
        if (params.equals("player_rank_formatted")) {
            return formatRank(resident.getRank());
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
    
    private String handleTownPlaceholder(String param, Town town) {
        switch (param) {
            case "name":
                return town.getName();
            case "mayor":
                OfflinePlayer mayor = Bukkit.getOfflinePlayer(town.getMayor());
                return mayor.getName() != null ? mayor.getName() : "Unknown";
            case "residents_count":
                return String.valueOf(town.getResidents().size());
            case "claims_count":
                return String.valueOf(town.getClaims().size());
            case "bank":
                return String.format("%.2f", town.getBank());
            case "pvp":
                return String.valueOf(town.isPvp());
            case "explosions":
                return String.valueOf(town.isExplosions());
            case "mobs":
                return String.valueOf(town.isMobSpawning());
            case "open":
                return String.valueOf(town.isOpen());
            case "has_spawn":
                return String.valueOf(town.getSpawn() != null);
            case "board":
                return town.getBoard() != null ? town.getBoard() : "";
            case "color":
                return town.getColor().name();
            case "color_formatted":
                return town.getColor().getChatColor() + town.getColor().name();
            case "founded":
                return String.valueOf(town.getFounded());
            case "assistants_count":
                return String.valueOf(town.getAssistants().size());
            default:
                return null;
        }
    }
    
    private String handleNationPlaceholder(String param, Nation nation) {
        switch (param) {
            case "name":
                return nation.getName();
            case "capital":
                Town capital = dataManager.getTown(nation.getCapital());
                return capital != null ? capital.getName() : "Unknown";
            case "leader":
                Town cap = dataManager.getTown(nation.getCapital());
                if (cap == null) return "Unknown";
                OfflinePlayer leader = Bukkit.getOfflinePlayer(cap.getMayor());
                return leader.getName() != null ? leader.getName() : "Unknown";
            case "towns_count":
                return String.valueOf(nation.getTowns().size());
            case "bank":
                return String.format("%.2f", nation.getBank());
            case "board":
                return nation.getBoard() != null ? nation.getBoard() : "";
            case "color":
                return nation.getColor().name();
            case "color_formatted":
                return nation.getColor().getChatColor() + nation.getColor().name();
            case "founded":
                return String.valueOf(nation.getFounded());
            case "allies_count":
                return String.valueOf(nation.getAllies().size());
            case "enemies_count":
                return String.valueOf(nation.getEnemies().size());
            case "total_claims":
                return String.valueOf(nation.getTowns().stream()
                    .map(uuid -> dataManager.getTown(uuid))
                    .filter(t -> t != null)
                    .mapToInt(t -> t.getClaims().size())
                    .sum());
            case "total_residents":
                return String.valueOf(nation.getTowns().stream()
                    .map(uuid -> dataManager.getTown(uuid))
                    .filter(t -> t != null)
                    .mapToInt(t -> t.getResidents().size())
                    .sum());
            default:
                return null;
        }
    }
    
    private String handleLocationPlaceholder(String param, Player player) {
        int chunkX = player.getLocation().getChunk().getX();
        int chunkZ = player.getLocation().getChunk().getZ();
        String world = player.getWorld().getName();
        
        Claim claim = dataManager.getClaim(world, chunkX, chunkZ);
        
        switch (param) {
            case "has_claim":
                return String.valueOf(claim != null);
            case "town":
                if (claim == null) return "Wilderness";
                Town town = claim.getTown();
                return town != null ? town.getName() : "Unknown";
            case "nation":
                if (claim == null) return "";
                Town town2 = claim.getTown();
                if (town2 == null || !town2.hasNation()) return "";
                return town2.getNation().getName();
            case "type":
                if (claim == null) return "WILDERNESS";
                return claim.getType().toString();
            case "can_build":
                if (claim == null) return "true";
                Town claimTown = claim.getTown();
                if (claimTown == null) return "true";
                Resident res = dataManager.getResident(player.getUniqueId());
                if (res == null) return "false";
                return String.valueOf(res.getTown() == claimTown);
            default:
                return null;
        }
    }
    
    private String handleStatsPlaceholder(String param) {
        switch (param) {
            case "total_towns":
                return String.valueOf(dataManager.getTowns().size());
            case "total_nations":
                return String.valueOf(dataManager.getNations().size());
            case "total_residents":
                return String.valueOf(dataManager.getResidents().size());
            case "total_claims":
                return String.valueOf(dataManager.getClaims().size());
            case "largest_town":
                Town largest = dataManager.getTowns().stream()
                    .max((t1, t2) -> Integer.compare(t1.getResidents().size(), t2.getResidents().size()))
                    .orElse(null);
                return largest != null ? largest.getName() : "None";
            case "wealthiest_town":
                Town wealthiest = dataManager.getTowns().stream()
                    .max((t1, t2) -> Double.compare(t1.getBank(), t2.getBank()))
                    .orElse(null);
                return wealthiest != null ? wealthiest.getName() : "None";
            case "largest_nation":
                Nation largestNation = dataManager.getNations().stream()
                    .max((n1, n2) -> Integer.compare(n1.getTowns().size(), n2.getTowns().size()))
                    .orElse(null);
                return largestNation != null ? largestNation.getName() : "None";
            default:
                return null;
        }
    }
    
    private String formatRank(TownRank rank) {
        switch (rank) {
            case MAYOR:
                return "§6Mayor";
            case ASSISTANT:
                return "§eAssistant";
            case RESIDENT:
                return "§aResident";
            default:
                return rank.toString();
        }
    }
    
    public void registerExpansion() {
        if (register()) {
            plugin.getLogger().info("PlaceholderAPI expansion registered successfully");
        } else {
            plugin.getLogger().warning("Failed to register PlaceholderAPI expansion");
        }
    }
}
