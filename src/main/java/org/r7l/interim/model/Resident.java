package org.r7l.interim.model;

import java.util.*;

public class Resident {
    private final UUID uuid;
    private String name;
    private Town town;
    private TownRank rank;
    private long joinedTown;
    private boolean online;
    
    public Resident(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.town = null;
        this.rank = TownRank.RESIDENT;
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
        return town;
    }
    
    public void setTown(Town town) {
        this.town = town;
        if (town != null) {
            this.joinedTown = System.currentTimeMillis();
        }
    }
    
    public TownRank getRank() {
        return rank;
    }
    
    public void setRank(TownRank rank) {
        this.rank = rank;
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
        return town != null;
    }
    
    public Nation getNation() {
        return town != null ? town.getNation() : null;
    }
    
    public boolean hasNation() {
        return getNation() != null;
    }
    
    public boolean isMayor() {
        return rank == TownRank.MAYOR;
    }
    
    public boolean isAssistant() {
        return rank == TownRank.ASSISTANT;
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
