package org.r7l.interim;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.r7l.interim.command.AdminCommand;
import org.r7l.interim.command.NationCommand;
import org.r7l.interim.command.PlotCommand;
import org.r7l.interim.command.TownCommand;
import org.r7l.interim.config.ConfigUpdater;
import org.r7l.interim.integration.BlueMapIntegration;
import org.r7l.interim.integration.FloodgateIntegration;
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
        
        NationCommand nationCommand = new NationCommand(this);
        getCommand("nation").setExecutor(nationCommand);
        getCommand("nation").setTabCompleter(nationCommand);
        
        PlotCommand plotCommand = new PlotCommand(this);
        getCommand("plot").setExecutor(plotCommand);
        getCommand("plot").setTabCompleter(plotCommand);
        
        AdminCommand adminCommand = new AdminCommand(this);
        getCommand("interimadmin").setExecutor(adminCommand);
        getCommand("interimadmin").setTabCompleter(adminCommand);
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);
        
        // Setup BlueMap integration
        if (getConfig().getBoolean("integrations.bluemap.enabled", true)) {
            setupBlueMap();
        }
        
        // Setup Floodgate/Geyser integration
        if (getConfig().getBoolean("integrations.geyser.enabled", true)) {
            setupFloodgate();
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
        if (rsp == null) {
            getLogger().warning("No economy plugin found! Economy features disabled.");
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
