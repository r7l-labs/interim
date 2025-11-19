package org.r7l.interim.model;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

public class Claim {
    private final String worldName;
    private final int x;
    private final int z;
    private Town town;
    private ClaimType type;
    private long claimedTime;
    private boolean pvpEnabled;
    private boolean explosionsEnabled;
    private boolean mobSpawningEnabled;
    
    public Claim(World world, int x, int z, Town town) {
        this.worldName = world.getName();
        this.x = x;
        this.z = z;
        this.town = town;
        this.type = ClaimType.NORMAL;
        this.claimedTime = System.currentTimeMillis();
        this.pvpEnabled = false;
        this.explosionsEnabled = false;
        this.mobSpawningEnabled = true;
    }
    
    public Claim(Chunk chunk, Town town) {
        this(chunk.getWorld(), chunk.getX(), chunk.getZ(), town);
    }
    
    public String getWorldName() {
        return worldName;
    }
    
    public int getX() {
        return x;
    }
    
    public int getZ() {
        return z;
    }
    
    public Town getTown() {
        return town;
    }
    
    public void setTown(Town town) {
        this.town = town;
    }
    
    public ClaimType getType() {
        return type;
    }
    
    public void setType(ClaimType type) {
        this.type = type;
    }
    
    public long getClaimedTime() {
        return claimedTime;
    }
    
    public String getCoordString() {
        return worldName + ":" + x + "," + z;
    }
    
    public boolean isInClaim(Location location) {
        if (!location.getWorld().getName().equals(worldName)) {
            return false;
        }
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;
        return chunkX == x && chunkZ == z;
    }
    
    public boolean isAdjacentTo(Claim other) {
        if (!this.worldName.equals(other.worldName)) {
            return false;
        }
        int dx = Math.abs(this.x - other.x);
        int dz = Math.abs(this.z - other.z);
        return (dx == 1 && dz == 0) || (dx == 0 && dz == 1);
    }
    
    public boolean isPvpEnabled() {
        return pvpEnabled;
    }
    
    public void setPvpEnabled(boolean pvpEnabled) {
        this.pvpEnabled = pvpEnabled;
    }
    
    public boolean isExplosionsEnabled() {
        return explosionsEnabled;
    }
    
    public void setExplosionsEnabled(boolean explosionsEnabled) {
        this.explosionsEnabled = explosionsEnabled;
    }
    
    public boolean isMobSpawningEnabled() {
        return mobSpawningEnabled;
    }
    
    public void setMobSpawningEnabled(boolean mobSpawningEnabled) {
        this.mobSpawningEnabled = mobSpawningEnabled;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Claim claim = (Claim) o;
        return x == claim.x && z == claim.z && worldName.equals(claim.worldName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(worldName, x, z);
    }
}
