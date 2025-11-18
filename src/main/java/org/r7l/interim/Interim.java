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
import org.r7l.interim.listener.ProtectionListener;
import org.r7l.interim.storage.DataManager;

import java.io.File;

public class Interim extends JavaPlugin {
    private DataManager dataManager;
    private Economy economy;
    
    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();
        
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
        
        // Start auto-save task
        startAutoSave();
        
        getLogger().info("Interim has been enabled!");
        getLogger().info("Towns: " + dataManager.getTowns().size());
        getLogger().info("Nations: " + dataManager.getNations().size());
        getLogger().info("Residents: " + dataManager.getResidents().size());
    }
    
    @Override
    public void onDisable() {
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
}
