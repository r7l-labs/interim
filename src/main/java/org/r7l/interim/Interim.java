package org.r7l.interim;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.r7l.interim.command.AdminCommand;
import org.r7l.interim.command.MapCommand;
import org.r7l.interim.command.MenuCommand;
import org.r7l.interim.command.NationCommand;
import org.r7l.interim.command.PlotCommand;
import org.r7l.interim.command.TownCommand;
import org.r7l.interim.config.ConfigUpdater;
import org.r7l.interim.integration.BlueMapIntegration;
import org.r7l.interim.integration.FloodgateIntegration;
import org.r7l.interim.listener.GUIListener;
import org.r7l.interim.listener.ProtectionListener;
import org.r7l.interim.storage.DataManager;
import org.r7l.interim.visual.SpawnParticleManager;

import java.io.File;

public class Interim extends JavaPlugin {
    private DataManager dataManager;
    private Economy economy;
    private BlueMapIntegration blueMapIntegration;
    private FloodgateIntegration floodgateIntegration;
    private SpawnParticleManager particleManager;
    // Active teleport sessions (player UUID -> session)
    private final java.util.Map<java.util.UUID, TeleportSession> activeTeleports = new java.util.concurrent.ConcurrentHashMap<>();
    
    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();
        
        // Update config version and add missing keys
        ConfigUpdater configUpdater = new ConfigUpdater(this);
        configUpdater.updateConfig();
        
        // Initialize data manager
        File dataFolder = new File(getDataFolder(), "data");
        dataManager = new DataManager(dataFolder);
        
        // Load data
        getLogger().info("Loading data...");
        dataManager.loadAll();
        getLogger().info("Data loaded successfully!");
        
        // Setup economy
        if (getConfig().getBoolean("economy.enabled", true) && 
            getConfig().getBoolean("economy.use-vault", true)) {
            setupEconomy();
        }
        
        // Register commands
    TownCommand townCommand = new TownCommand(this);
    getCommand("town").setExecutor(townCommand);
    getCommand("town").setTabCompleter(townCommand);

    // Register /interim and its aliases to open the overview GUI
    getCommand("interim").setExecutor(new org.r7l.interim.command.InterimCommand(this));
        
        NationCommand nationCommand = new NationCommand(this);
        getCommand("nation").setExecutor(nationCommand);
        getCommand("nation").setTabCompleter(nationCommand);
        
        PlotCommand plotCommand = new PlotCommand(this);
        getCommand("plot").setExecutor(plotCommand);
        getCommand("plot").setTabCompleter(plotCommand);
        
        AdminCommand adminCommand = new AdminCommand(this);
        getCommand("interimadmin").setExecutor(adminCommand);
        getCommand("interimadmin").setTabCompleter(adminCommand);
        
        MapCommand mapCommand = new MapCommand(this);
        getCommand("map").setExecutor(mapCommand);
        getCommand("map").setTabCompleter(mapCommand);
        
        MenuCommand menuCommand = new MenuCommand(this);
        getCommand("menu").setExecutor(menuCommand);
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        
        // Setup BlueMap integration
        if (getConfig().getBoolean("integrations.bluemap.enabled", true)) {
            setupBlueMap();
        }
        
        // Setup Floodgate/Geyser integration
        if (getConfig().getBoolean("integrations.geyser.enabled", true)) {
            setupFloodgate();
        }

        // PlaceholderAPI integration: if PlaceholderAPI is present we will support it in a follow-up
        // Create a reflection-safe placeholder integration stub and call registration check
        try {
            new org.r7l.interim.integration.PlaceholderIntegration(this).registerIfPresent();
        } catch (Throwable t) {
            getLogger().fine("PlaceholderIntegration check failed: " + t.getMessage());
        }
        
        // Initialize particle manager
        particleManager = new SpawnParticleManager(this);
        particleManager.startAllParticleEffects();
        
        // Start auto-save task
        startAutoSave();
        
        getLogger().info("Interim has been enabled!");
        getLogger().info("Towns: " + dataManager.getTowns().size());
        getLogger().info("Nations: " + dataManager.getNations().size());
        getLogger().info("Residents: " + dataManager.getResidents().size());
    }

    // Represents an active teleport countdown for a player
    private static class TeleportSession {
        final java.util.UUID playerUuid;
        final BossBar bar;
        final BukkitRunnable task;
        final org.bukkit.Location startLocation;
        final double costCharged;

        TeleportSession(java.util.UUID playerUuid, BossBar bar, BukkitRunnable task, Location startLocation, double costCharged) {
            this.playerUuid = playerUuid;
            this.bar = bar;
            this.task = task;
            this.startLocation = startLocation;
            this.costCharged = costCharged;
        }
    }

    /**
     * Start a teleport session with a bossbar countdown. The player will be teleported when the countdown finishes.
     * If costCharged &gt; 0 the player was already charged; cancelTeleport(..., true) will refund.
     */
    public void startTeleportSession(Player player, org.r7l.interim.model.Town town, double costCharged, int delaySeconds) {
        // remove any existing session for safety
        cancelTeleportSession(player.getUniqueId(), true, "Teleport interrupted");

        final BossBar bar = Bukkit.createBossBar("Teleporting to " + town.getName() + "...", BarColor.BLUE, BarStyle.SOLID);
        bar.setProgress(1.0);
        bar.addPlayer(player);

        final int total = Math.max(1, delaySeconds);
        final org.bukkit.Location startLoc = player.getLocation().clone();

        BukkitRunnable runnable = new BukkitRunnable() {
            int secondsLeft = total;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    // refund if needed
                    cancelTeleportSession(player.getUniqueId(), true, "You disconnected");
                    cancel();
                    return;
                }

                double progress = (double) secondsLeft / (double) total;
                bar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
                bar.setTitle("Teleporting in " + secondsLeft + "s");

                if (secondsLeft <= 0) {
                    bar.removePlayer(player);
                    bar.setVisible(false);
                    // perform teleport
                    player.teleport(town.getSpawn());
                    player.sendMessage(success("Teleported to " + town.getName() + " spawn!"));
                    String board = town.getBoard();
                    if (board != null && !board.isEmpty()) {
                        player.sendTitle("", board, 10, 60, 10);
                    }
                    // remove session
                    activeTeleports.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                secondsLeft--;
            }
        };

        TeleportSession session = new TeleportSession(player.getUniqueId(), bar, runnable, startLoc, costCharged);
        activeTeleports.put(player.getUniqueId(), session);

        runnable.runTaskTimer(this, 0L, 20L);
    }

    /**
     * Cancel an active teleport session. If refund==true and a cost was charged, refund the player.
     */
    public void cancelTeleportSession(java.util.UUID playerUuid, boolean refund, String reason) {
        TeleportSession session = activeTeleports.remove(playerUuid);
        if (session == null) return;

        // cancel task
        try {
            session.task.cancel();
        } catch (Exception ignored) {}

        // remove bossbar safely
        try {
            Player p = Bukkit.getPlayer(playerUuid);
            if (p != null) {
                session.bar.removePlayer(p);
                session.bar.setVisible(false);
                if (refund && session.costCharged > 0 && economy != null) {
                    economy.depositPlayer(p, session.costCharged);
                    p.sendMessage(info("Teleport cancelled: refund of " + session.costCharged + " issued."));
                } else if (refund && session.costCharged > 0) {
                    p.sendMessage(info("Teleport cancelled."));
                } else if (!refund) {
                    p.sendMessage(info("Teleport cancelled."));
                }
            }
        } catch (Exception ignored) {}
    }

    public boolean hasActiveTeleport(java.util.UUID playerUuid) {
        return activeTeleports.containsKey(playerUuid);
    }

    /**
     * Check whether the provided location is inside any WorldGuard region.
     * This method uses reflection so WorldGuard is optional at compile-time.
     */
    public boolean isLocationInWorldGuardRegion(org.bukkit.Location loc) {
        if (loc == null) return false;
        org.bukkit.plugin.Plugin wg = getServer().getPluginManager().getPlugin("WorldGuard");
        if (wg == null) return false;

        try {
            // Try classic WorldGuardPlugin#getRegionManager(World)
            java.lang.reflect.Method getRegionManager = wg.getClass().getMethod("getRegionManager", org.bukkit.World.class);
            Object regionManager = getRegionManager.invoke(wg, loc.getWorld());
            if (regionManager == null) return false;

            java.lang.reflect.Method getRegions = regionManager.getClass().getMethod("getRegions");
            Object regionsObj = getRegions.invoke(regionManager);
            if (regionsObj instanceof java.util.Map) {
                java.util.Map<?,?> regions = (java.util.Map<?,?>) regionsObj;
                int x = loc.getBlockX();
                int y = loc.getBlockY();
                int z = loc.getBlockZ();
                for (Object region : regions.values()) {
                    if (region == null) continue;
                    try {
                        // Try contains(int x,int y,int z)
                        java.lang.reflect.Method contains = region.getClass().getMethod("contains", int.class, int.class, int.class);
                        Object res = contains.invoke(region, x, y, z);
                        if (res instanceof Boolean && (Boolean) res) return true;
                    } catch (NoSuchMethodException e) {
                        // Try BlockVector3.contains(BlockVector3) style: construct BlockVector3 and call contains
                        try {
                            Class<?> bvClass = Class.forName("com.sk89q.worldedit.math.BlockVector3");
                            java.lang.reflect.Method at = bvClass.getMethod("at", int.class, int.class, int.class);
                            Object bv = at.invoke(null, x, y, z);
                            try {
                                java.lang.reflect.Method containsBV = region.getClass().getMethod("contains", bvClass);
                                Object res2 = containsBV.invoke(region, bv);
                                if (res2 instanceof Boolean && (Boolean) res2) return true;
                            } catch (NoSuchMethodException ex) {
                                // ignore
                            }
                        } catch (ClassNotFoundException cnf) {
                            // WorldEdit/WorldGuard classes not present in expected package; ignore
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Reflection failed; assume no WG regions match
            return false;
        }

        return false;
    }
    
    @Override
    public void onDisable() {
        // Stop particle effects
        if (particleManager != null) {
            particleManager.stopAllParticleEffects();
        }
        
        // Disable BlueMap integration
        if (blueMapIntegration != null) {
            blueMapIntegration.disable();
        }
        
        // Save data
        getLogger().info("Saving data...");
        dataManager.saveAll();
        getLogger().info("Data saved successfully!");
        
        getLogger().info("Interim has been disabled!");
    }
    
    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("Vault not found! Economy features disabled.");
            return;
        }
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null || rsp.getProvider() == null) {
            getLogger().warning("No economy provider found! Economy features disabled.");
            return;
        }
        economy = rsp.getProvider();
        getLogger().info("Hooked into " + economy.getName() + " via Vault!");
    }
    
    private void setupBlueMap() {
        if (getServer().getPluginManager().getPlugin("BlueMap") == null) {
            getLogger().info("BlueMap not found. Map integration disabled.");
            return;
        }
        
        try {
            blueMapIntegration = new BlueMapIntegration(this);
            blueMapIntegration.enable();
            getLogger().info("BlueMap integration initialized!");
        } catch (Exception e) {
            getLogger().warning("Failed to initialize BlueMap integration: " + e.getMessage());
        }
    }
    
    private void setupFloodgate() {
        if (getServer().getPluginManager().getPlugin("floodgate") == null) {
            getLogger().info("Floodgate not found. Bedrock player support limited to Java Edition features.");
            return;
        }
        
        try {
            floodgateIntegration = new FloodgateIntegration(this);
            floodgateIntegration.enable();
        } catch (Exception e) {
            getLogger().warning("Failed to initialize Floodgate integration: " + e.getMessage());
        }
    }
    
    private void startAutoSave() {
        long saveInterval = getConfig().getLong("general.save-interval", 6000); // Default 5 minutes
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getConfig().getBoolean("general.debug", false)) {
                    getLogger().info("Auto-saving data...");
                }
                dataManager.saveAll();
            }
        }.runTaskTimerAsynchronously(this, saveInterval, saveInterval);
    }
    
    public DataManager getDataManager() {
        return dataManager;
    }
    
    public Economy getEconomy() {
        return economy;
    }

    // Messaging helpers to standardize chat output and avoid ChatColor usage
    public String pref(String message) {
        return "§9§l[Interim] §7" + message;
    }

    public String success(String message) {
        return "§9§l[Interim] §a" + message;
    }

    public String error(String message) {
        return "§9§l[Interim] §c" + message;
    }

    public String info(String message) {
        return "§9§l[Interim] §e" + message;
    }
    
    public BlueMapIntegration getBlueMapIntegration() {
        return blueMapIntegration;
    }
    
    public SpawnParticleManager getParticleManager() {
        return particleManager;
    }
    
    public FloodgateIntegration getFloodgateIntegration() {
        return floodgateIntegration;
    }
}
