package org.r7l.interim.visual;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.r7l.interim.model.Claim;
import org.r7l.interim.Interim;
import org.r7l.interim.model.Town;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpawnParticleManager {
    
    private final Interim plugin;
    private final Map<UUID, BukkitRunnable> particleTasks;
    private boolean enabled;
    
    public SpawnParticleManager(Interim plugin) {
        this.plugin = plugin;
        this.particleTasks = new HashMap<>();
        this.enabled = plugin.getConfig().getBoolean("visual.spawn-particles.enabled", true);
    }
    
    public void startParticleEffect(Town town) {
        if (!enabled || town.getSpawn() == null) {
            return;
        }
        
        // Stop existing task if any
        stopParticleEffect(town.getUuid());
        
        BukkitRunnable task = new BukkitRunnable() {
            private double angle = 0;
            
            @Override
            public void run() {
                Location spawn = town.getSpawn();
                if (spawn == null || spawn.getWorld() == null) {
                    cancel();
                    return;
                }
                
                World world = spawn.getWorld();
                
                // Create a spiral column effect
                for (int i = 0; i < 20; i++) {
                    double y = spawn.getY() + (i * 0.5);
                    double radius = 0.5;
                    
                    double x = spawn.getX() + radius * Math.cos(angle + (i * 0.3));
                    double z = spawn.getZ() + radius * Math.sin(angle + (i * 0.3));
                    
                    Location particleLoc = new Location(world, x, y, z);
                    
                    // Use REDSTONE particles which are compatible with Bedrock Edition
                    Particle.DustOptions dustOptions = new Particle.DustOptions(
                            Color.fromRGB(100, 200, 255), 1.0f
                    );
                    
                    // Spawn particles visible to both Java and Bedrock players
                    world.spawnParticle(Particle.DUST, particleLoc, 1, 0, 0, 0, 0, dustOptions);
                }
                
                // Also add a base ring
                for (int i = 0; i < 8; i++) {
                    double ringAngle = (Math.PI * 2 / 8) * i;
                    double x = spawn.getX() + 0.7 * Math.cos(ringAngle + angle);
                    double z = spawn.getZ() + 0.7 * Math.sin(ringAngle + angle);
                    
                    Location ringLoc = new Location(world, x, spawn.getY() + 0.1, z);
                    
                    Particle.DustOptions dustOptions = new Particle.DustOptions(
                            Color.fromRGB(150, 220, 255), 1.2f
                    );
                    
                    world.spawnParticle(Particle.DUST, ringLoc, 1, 0, 0, 0, 0, dustOptions);
                }
                
                angle += 0.15;
                if (angle > Math.PI * 2) {
                    angle = 0;
                }
            }
        };
        
        task.runTaskTimer(plugin, 0L, 2L); // Run every 2 ticks (0.1 seconds)
        particleTasks.put(town.getUuid(), task);
    }
    
    public void stopParticleEffect(UUID townUuid) {
        BukkitRunnable task = particleTasks.remove(townUuid);
        if (task != null) {
            task.cancel();
        }
    }
    
    public void refreshParticleEffect(Town town) {
        stopParticleEffect(town.getUuid());
        startParticleEffect(town);
    }
    
    public void startAllParticleEffects() {
        if (!enabled) {
            return;
        }
        
        for (Town town : plugin.getDataManager().getTowns()) {
            if (town.getSpawn() != null) {
                startParticleEffect(town);
            }
        }
    }
    
    public void stopAllParticleEffects() {
        for (BukkitRunnable task : particleTasks.values()) {
            task.cancel();
        }
        particleTasks.clear();
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            startAllParticleEffects();
        } else {
            stopAllParticleEffects();
        }
    }
    
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Show claim borders as particles to a single player for a duration (seconds).
     */
    public void showClaimBorders(Player player, java.util.Collection<Claim> claims, int durationSeconds) {
        if (!enabled || player == null || claims == null || claims.isEmpty()) return;

        World world = player.getWorld();
        // task to spawn border particles periodically
        BukkitRunnable task = new BukkitRunnable() {
            int ticksLeft = durationSeconds * 20;

            @Override
            public void run() {
                if (ticksLeft <= 0 || !player.isOnline()) {
                    cancel();
                    return;
                }

                int playerChunkX = player.getLocation().getBlockX() >> 4;
                int playerChunkZ = player.getLocation().getBlockZ() >> 4;
                int radius = plugin.getConfig().getInt("town.view-radius-chunks", 10);

                for (Claim claim : claims) {
                    if (!claim.getWorldName().equals(world.getName())) continue;
                    int chunkX = claim.getX();
                    int chunkZ = claim.getZ();

                    // skip claims outside configured radius
                    int dx = Math.abs(chunkX - playerChunkX);
                    int dz = Math.abs(chunkZ - playerChunkZ);
                    if (Math.max(dx, dz) > radius) continue;

                    int baseX = chunkX << 4;
                    int baseZ = chunkZ << 4;
                    double y = player.getLocation().getY();

                    // Create continuous particle streams along chunk boundaries
                    // Using smaller spacing (0.5 blocks) for a continuous appearance
                    for (double i = 0; i <= 16; i += 0.5) {
                        // North edge (z = baseZ)
                        Location nLoc = new Location(world, baseX + i, y, baseZ + 0.5);
                        world.spawnParticle(Particle.DUST, nLoc, 1, 0, 0, 0, 0, new Particle.DustOptions(org.bukkit.Color.fromRGB(0, 200, 255), 1.0f));
                        // South edge (z = baseZ+16)
                        Location sLoc = new Location(world, baseX + i, y, baseZ + 16 - 0.5);
                        world.spawnParticle(Particle.DUST, sLoc, 1, 0, 0, 0, 0, new Particle.DustOptions(org.bukkit.Color.fromRGB(0, 200, 255), 1.0f));
                    }
                    
                    for (double i = 0; i <= 16; i += 0.5) {
                        // West edge (x = baseX)
                        Location wLoc = new Location(world, baseX + 0.5, y, baseZ + i);
                        world.spawnParticle(Particle.DUST, wLoc, 1, 0, 0, 0, 0, new Particle.DustOptions(org.bukkit.Color.fromRGB(0, 200, 255), 1.0f));
                        // East edge (x = baseX+16)
                        Location eLoc = new Location(world, baseX + 16 - 0.5, y, baseZ + i);
                        world.spawnParticle(Particle.DUST, eLoc, 1, 0, 0, 0, 0, new Particle.DustOptions(org.bukkit.Color.fromRGB(0, 200, 255), 1.0f));
                    }
                }

                ticksLeft -= 5; // runs every 5 ticks below
            }
        };

        task.runTaskTimer(plugin, 0L, 5L);
    }
}
