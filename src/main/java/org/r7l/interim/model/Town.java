package org.r7l.interim.model;

import org.bukkit.Location;

import java.util.*;

public class Town {
    private final UUID uuid;
    private String name;
    private final UUID mayor;
    private final Set<UUID> residents;
    private final Set<UUID> assistants;
    private final Set<Claim> claims;
    private Nation nation;
    private Location spawn;
    private double bank;
    private long founded;
    private boolean open;
    private boolean pvp;
    private boolean explosions;
    private boolean mobSpawning;
    private String board;
    private String tag;
    private NationColor color;
    
    public Town(String name, UUID mayor) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.mayor = mayor;
        this.residents = new HashSet<>();
        this.assistants = new HashSet<>();
        this.claims = new HashSet<>();
        this.nation = null;
        this.spawn = null;
        this.bank = 0.0;
        this.founded = System.currentTimeMillis();
        this.open = false;
        this.pvp = false;
        this.explosions = false;
        this.mobSpawning = true;
        this.board = "";
        this.tag = "";
        this.color = NationColor.AQUA; // Default cyan color for new towns
        
        // Mayor is automatically a resident
        this.residents.add(mayor);
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
    
    public UUID getMayor() {
        return mayor;
    }
    
    public Set<UUID> getResidents() {
        return new HashSet<>(residents);
    }
    
    public void addResident(UUID resident) {
        residents.add(resident);
    }
    
    public void removeResident(UUID resident) {
        residents.remove(resident);
        assistants.remove(resident);
    }
    
    public boolean hasResident(UUID resident) {
        return residents.contains(resident);
    }
    
    public Set<UUID> getAssistants() {
        return new HashSet<>(assistants);
    }
    
    public void addAssistant(UUID assistant) {
        if (residents.contains(assistant)) {
            assistants.add(assistant);
        }
    }
    
    public void removeAssistant(UUID assistant) {
        assistants.remove(assistant);
    }
    
    public boolean isAssistant(UUID resident) {
        return assistants.contains(resident);
    }
    
    public Set<Claim> getClaims() {
        return new HashSet<>(claims);
    }
    
    public void addClaim(Claim claim) {
        claims.add(claim);
        claim.setTown(this);
    }
    
    public void removeClaim(Claim claim) {
        claims.remove(claim);
    }
    
    public boolean hasClaim(Claim claim) {
        return claims.contains(claim);
    }
    
    public int getClaimCount() {
        return claims.size();
    }
    
    public Nation getNation() {
        return nation;
    }
    
    public void setNation(Nation nation) {
        this.nation = nation;
    }
    
    public boolean hasNation() {
        return nation != null;
    }
    
    public Location getSpawn() {
        return spawn;
    }
    
    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }
    
    public double getBank() {
        return bank;
    }
    
    public void setBank(double bank) {
        this.bank = bank;
    }
    
    public void deposit(double amount) {
        this.bank += amount;
    }
    
    public boolean withdraw(double amount) {
        if (bank >= amount) {
            bank -= amount;
            return true;
        }
        return false;
    }
    
    public long getFounded() {
        return founded;
    }
    
    public boolean isOpen() {
        return open;
    }
    
    public void setOpen(boolean open) {
        this.open = open;
    }
    
    public boolean isPvp() {
        return pvp;
    }
    
    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }
    
    public boolean isExplosions() {
        return explosions;
    }
    
    public void setExplosions(boolean explosions) {
        this.explosions = explosions;
    }
    
    public boolean isMobSpawning() {
        return mobSpawning;
    }
    
    public void setMobSpawning(boolean mobSpawning) {
        this.mobSpawning = mobSpawning;
    }
    
    public String getBoard() {
        return board;
    }
    
    public void setBoard(String board) {
        this.board = board;
    }

    public String getTag() {
        if (tag != null && !tag.isEmpty()) return tag;
        // Generate tag from first 4 characters of name
        return name.length() <= 4 ? name : name.substring(0, 4);
    }

    public void setTag(String tag) {
        this.tag = tag == null ? "" : tag;
    }

    public NationColor getColor() {
        return color != null ? color : NationColor.AQUA;
    }

    public void setColor(NationColor color) {
        this.color = color;
    }
    
    public int getResidentCount() {
        return residents.size();
    }
    
    
    public int getMaxClaims() {
        // Base claims + bonus per resident
        return 10 + (residents.size() * 2);
    }
    
    public Claim getClaimAt(Location location) {
        for (Claim claim : claims) {
            if (claim.isInClaim(location)) {
                return claim;
            }
        }
        return null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Town town = (Town) o;
        return uuid.equals(town.uuid);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
