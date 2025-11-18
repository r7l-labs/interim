package org.r7l.interim.model;

import java.util.*;

public class Resident {
    private final UUID uuid;
    private String name;
    private final Map<UUID, Town> towns; // townUUID -> Town
    private final Map<UUID, TownRank> ranks; // townUUID -> Rank
    private UUID primaryTown; // The main/primary town
    private long joinedTown;
    private boolean online;
    
    public Resident(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.towns = new HashMap<>();
        this.ranks = new HashMap<>();
        this.primaryTown = null;
        this.joinedTown = System.currentTimeMillis();
        this.online = false;
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Town getTown() {
        return primaryTown != null ? towns.get(primaryTown) : null;
    }
    
    public Town getPrimaryTown() {
        return getTown();
    }
    
    public Map<UUID, Town> getTowns() {
        return new HashMap<>(towns);
    }
    
    public List<Town> getTownsList() {
        return new ArrayList<>(towns.values());
    }
    
    public void setTown(Town town) {
        // For backwards compatibility - sets as primary town
        if (town == null) {
            if (primaryTown != null) {
                towns.remove(primaryTown);
                ranks.remove(primaryTown);
                primaryTown = null;
            }
        } else {
            addTown(town, TownRank.RESIDENT, true);
        }
    }
    
    public void addTown(Town town, TownRank rank, boolean setPrimary) {
        towns.put(town.getUuid(), town);
        ranks.put(town.getUuid(), rank);
        if (setPrimary || primaryTown == null) {
            primaryTown = town.getUuid();
            this.joinedTown = System.currentTimeMillis();
        }
    }
    
    public void removeTown(UUID townUuid) {
        towns.remove(townUuid);
        ranks.remove(townUuid);
        if (primaryTown != null && primaryTown.equals(townUuid)) {
            // Set another town as primary if available
            primaryTown = towns.isEmpty() ? null : towns.keySet().iterator().next();
        }
    }
    
    public void removeTown(Town town) {
        removeTown(town.getUuid());
    }
    
    public void setPrimaryTown(UUID townUuid) {
        if (towns.containsKey(townUuid)) {
            primaryTown = townUuid;
        }
    }
    
    public boolean isInTown(UUID townUuid) {
        return towns.containsKey(townUuid);
    }
    
    public boolean isInTown(Town town) {
        return isInTown(town.getUuid());
    }
    
    public TownRank getRank() {
        return primaryTown != null ? ranks.get(primaryTown) : TownRank.RESIDENT;
    }
    
    public TownRank getRank(UUID townUuid) {
        return ranks.getOrDefault(townUuid, TownRank.RESIDENT);
    }
    
    public TownRank getRank(Town town) {
        return getRank(town.getUuid());
    }
    
    public void setRank(TownRank rank) {
        if (primaryTown != null) {
            ranks.put(primaryTown, rank);
        }
    }
    
    public void setRank(UUID townUuid, TownRank rank) {
        if (towns.containsKey(townUuid)) {
            ranks.put(townUuid, rank);
        }
    }
    
    public long getJoinedTown() {
        return joinedTown;
    }
    
    public boolean isOnline() {
        return online;
    }
    
    public void setOnline(boolean online) {
        this.online = online;
    }
    
    public boolean hasTown() {
        return !towns.isEmpty();
    }
    
    public int getTownCount() {
        return towns.size();
    }
    
    public Nation getNation() {
        Town primaryT = getTown();
        return primaryT != null ? primaryT.getNation() : null;
    }
    
    public boolean hasNation() {
        return getNation() != null;
    }
    
    public boolean isMayor() {
        return getRank() == TownRank.MAYOR;
    }
    
    public boolean isAssistant() {
        return getRank() == TownRank.ASSISTANT;
    }
    
    public boolean isMayor(UUID townUuid) {
        return getRank(townUuid) == TownRank.MAYOR;
    }
    
    public boolean isAssistant(UUID townUuid) {
        return getRank(townUuid) == TownRank.ASSISTANT;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resident resident = (Resident) o;
        return uuid.equals(resident.uuid);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
