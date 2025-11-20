package org.r7l.interim.command;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.r7l.interim.Interim;
import org.r7l.interim.model.*;
import org.r7l.interim.storage.DataManager;

import java.util.*;
import java.util.stream.Collectors;

public class AdminCommand implements CommandExecutor, TabCompleter {
    
    private final Interim plugin;
    private final DataManager dataManager;
    
    public AdminCommand(Interim plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("interim.admin")) {
            sender.sendMessage("§cYou don't have permission to use admin commands.");
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "town":
                return handleTownAdmin(sender, args);
            case "recover":
                return handleRecover(sender, args);
            case "nation":
                return handleNationAdmin(sender, args);
            case "resident":
                return handleResidentAdmin(sender, args);
            case "claim":
                return handleClaimAdmin(sender, args);
            case "info":
                return handleInfo(sender, args);
            case "purge":
                return handlePurge(sender, args);
            case "reload":
                return handleReload(sender);
            default:
                sender.sendMessage("§cUnknown subcommand. Use /interimadmin for help.");
                return true;
        }
    }
    
    private boolean handleTownAdmin(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /interimadmin town <create|delete|kick|add|rename|deposit|withdraw|color> [args]");
            return true;
        }
        
        String action = args[1].toLowerCase();
        
        switch (action) {
            case "create":
                return forceCreateTown(sender, args);
            case "delete":
                return forceDeleteTown(sender, args);
            case "kick":
                return forceKickFromTown(sender, args);
            case "add":
                return forceAddToTown(sender, args);
            case "rename":
                return forceRenameTown(sender, args);
            case "deposit":
                return forceDepositTown(sender, args);
            case "withdraw":
                return forceWithdrawTown(sender, args);
            case "color":
                return forceSetTownColor(sender, args);
            default:
                sender.sendMessage("§cUnknown town action: " + action);
                return true;
        }
    }
    
    private boolean handleNationAdmin(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /interimadmin nation <create|delete|kick|add|rename|deposit|withdraw|color> [args]");
            return true;
        }
        
        String action = args[1].toLowerCase();
        
        switch (action) {
            case "create":
                return forceCreateNation(sender, args);
            case "delete":
                return forceDeleteNation(sender, args);
            case "kick":
                return forceKickFromNation(sender, args);
            case "add":
                return forceAddToNation(sender, args);
            case "rename":
                return forceRenameNation(sender, args);
            case "deposit":
                return forceDepositNation(sender, args);
            case "withdraw":
                return forceWithdrawNation(sender, args);
            case "color":
                return forceSetNationColor(sender, args);
            default:
                sender.sendMessage("§cUnknown nation action: " + action);
                return true;
        }
    }
    
    private boolean handleResidentAdmin(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /interimadmin resident <setrank|reset> [args]");
            return true;
        }
        
        String action = args[1].toLowerCase();
        
        switch (action) {
            case "setrank":
                return forceSetRank(sender, args);
            case "reset":
                return forceResetResident(sender, args);
            default:
                sender.sendMessage("§cUnknown resident action: " + action);
                return true;
        }
    }
    
    private boolean handleClaimAdmin(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /interimadmin claim <add|remove|clear|settype> [args]");
            return true;
        }
        
        String action = args[1].toLowerCase();
        
        switch (action) {
            case "add":
                return forceAddClaim(sender, args);
            case "remove":
                return forceRemoveClaim(sender, args);
            case "clear":
                return forceClearClaims(sender, args);
            case "settype":
                return forceSetClaimType(sender, args);
            default:
                sender.sendMessage("§cUnknown claim action: " + action);
                return true;
        }
    }
    
    // Town admin methods
    private boolean forceCreateTown(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /interimadmin town create <townName> <mayorName>");
            return true;
        }
        
        String townName = args[2];
        String mayorName = args[3];
        
        if (dataManager.townExists(townName)) {
            sender.sendMessage("§cTown '" + townName + "' already exists.");
            return true;
        }
        
        OfflinePlayer mayor = Bukkit.getOfflinePlayer(mayorName);
        Resident resident = dataManager.getOrCreateResident(mayor.getUniqueId(), mayor.getName());
        
        // Check if already mayor of another town
        for (Town existingTown : resident.getTownsList()) {
            if (existingTown.getMayor().equals(mayor.getUniqueId())) {
                sender.sendMessage("§cPlayer '" + mayorName + "' is already mayor of '" + existingTown.getName() + "'.");
                return true;
            }
        }
        
        Town town = new Town(townName, mayor.getUniqueId());
        resident.addTown(town, TownRank.MAYOR, resident.getTownCount() == 0);
        
        dataManager.addTown(town);
        dataManager.addResident(resident);
        dataManager.saveAll();
        
        sender.sendMessage("§aForce created town '" + townName + "' with mayor " + mayorName + ".");
        
        Player onlineMayor = mayor.getPlayer();
        if (onlineMayor != null && onlineMayor.isOnline()) {
            onlineMayor.sendMessage("§aYou have been made the mayor of '" + townName + "' by an administrator.");
        }
        
        return true;
    }
    
    private boolean forceDeleteTown(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /interimadmin town delete <townName>");
            return true;
        }
        
        String townName = args[2];
        Town town = dataManager.getTown(townName);
        
        if (town == null) {
            sender.sendMessage("§cTown '" + townName + "' does not exist.");
            return true;
        }
        
        // Remove from nation if part of one
        if (town.hasNation()) {
            Nation nation = town.getNation();
            nation.removeTown(town.getUuid());
        }
        
        // Remove all residents
        for (UUID residentId : new ArrayList<>(town.getResidents())) {
            Resident resident = dataManager.getResident(residentId);
            if (resident != null) {
                resident.removeTown(town);
                
                Player player = Bukkit.getPlayer(residentId);
                if (player != null && player.isOnline()) {
                    player.sendMessage("§cYour town '" + townName + "' has been deleted by an administrator.");
                }
            }
        }
        
        // Remove all claims
        for (Claim claim : new ArrayList<>(town.getClaims())) {
            dataManager.removeClaim(claim);
        }
        
        dataManager.removeTown(town);
        dataManager.saveAll();
        sender.sendMessage("§aForce deleted town '" + townName + "' and all its claims.");
        
        return true;
    }
    
    private boolean forceKickFromTown(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /interimadmin town kick <townName> <playerName>");
            return true;
        }
        
        String townName = args[2];
        String playerName = args[3];
        
        Town town = dataManager.getTown(townName);
        if (town == null) {
            sender.sendMessage("§cTown '" + townName + "' does not exist.");
            return true;
        }
        
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || resident.getTown() != town) {
            sender.sendMessage("§cPlayer '" + playerName + "' is not in town '" + townName + "'.");
            return true;
        }
        
        if (town.getMayor().equals(player.getUniqueId())) {
            sender.sendMessage("§cCannot kick the mayor. Delete the town instead.");
            return true;
        }
        
        town.removeResident(player.getUniqueId());
        resident.removeTown(town);
        
        dataManager.saveAll();
        
        sender.sendMessage("§aForce kicked '" + playerName + "' from town '" + townName + "'.");
        
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null && onlinePlayer.isOnline()) {
            onlinePlayer.sendMessage("§cYou have been kicked from '" + townName + "' by an administrator.");
        }
        
        return true;
    }
    
    private boolean forceAddToTown(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /interimadmin town add <townName> <playerName>");
            return true;
        }
        
        String townName = args[2];
        String playerName = args[3];
        
        Town town = dataManager.getTown(townName);
        if (town == null) {
            sender.sendMessage("§cTown '" + townName + "' does not exist.");
            return true;
        }
        
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        Resident resident = dataManager.getOrCreateResident(player.getUniqueId(), player.getName());
        
        if (resident.isInTown(town)) {
            sender.sendMessage("§cPlayer '" + playerName + "' is already in this town.");
            return true;
        }
        
        town.addResident(player.getUniqueId());
        resident.addTown(town, TownRank.RESIDENT, false);
        
        dataManager.saveAll();
        
        sender.sendMessage("§aForce added '" + playerName + "' to town '" + townName + "'.");
        
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null && onlinePlayer.isOnline()) {
            onlinePlayer.sendMessage("§aYou have been added to '" + townName + "' by an administrator.");
        }
        
        return true;
    }
    
    private boolean forceRenameTown(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /interimadmin town rename <oldName> <newName>");
            return true;
        }
        
        String oldName = args[2];
        String newName = args[3];
        
        Town town = dataManager.getTown(oldName);
        if (town == null) {
            sender.sendMessage("§cTown '" + oldName + "' does not exist.");
            return true;
        }
        
        if (dataManager.townExists(newName)) {
            sender.sendMessage("§cTown '" + newName + "' already exists.");
            return true;
        }
        
        dataManager.removeTown(town);
        town.setName(newName);
        dataManager.addTown(town);
        dataManager.saveAll();
        
        sender.sendMessage("§aForce renamed town '" + oldName + "' to '" + newName + "'.");
        
        return true;
    }
    
    private boolean forceDepositTown(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /interimadmin town deposit <townName> <amount>");
            return true;
        }
        
        String townName = args[2];
        Town town = dataManager.getTown(townName);
        
        if (town == null) {
            sender.sendMessage("§cTown '" + townName + "' does not exist.");
            return true;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid amount: " + args[3]);
            return true;
        }
        
        if (amount <= 0) {
            sender.sendMessage("§cAmount must be positive.");
            return true;
        }
        
        town.deposit(amount);
        dataManager.saveAll();
        
        sender.sendMessage("§aForce deposited $" + amount + " to town '" + townName + "'. New balance: $" + town.getBank());
        
        return true;
    }
    
    private boolean forceWithdrawTown(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /interimadmin town withdraw <townName> <amount>");
            return true;
        }
        
        String townName = args[2];
        Town town = dataManager.getTown(townName);
        
        if (town == null) {
            sender.sendMessage("§cTown '" + townName + "' does not exist.");
            return true;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid amount: " + args[3]);
            return true;
        }
        
        if (amount <= 0) {
            sender.sendMessage("§cAmount must be positive.");
            return true;
        }
        
        town.setBank(town.getBank() - amount);
        dataManager.saveAll();
        
        sender.sendMessage("§aForce withdrew $" + amount + " from town '" + townName + "'. New balance: $" + town.getBank());
        
        return true;
    }
    
    private boolean forceSetTownColor(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /interimadmin town color <townName> <color>");
            sender.sendMessage("§cAvailable colors: WHITE, RED, BLUE, GREEN, YELLOW, GOLD, AQUA, LIGHT_PURPLE, DARK_GREEN, DARK_AQUA, DARK_PURPLE");
            return true;
        }
        
        String townName = args[2];
        Town town = dataManager.getTown(townName);
        
        if (town == null) {
            sender.sendMessage("§cTown '" + townName + "' does not exist.");
            return true;
        }
        
        String colorName = args[3].toUpperCase();
        NationColor color;
        
        try {
            color = NationColor.valueOf(colorName);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cInvalid color: " + args[3]);
            sender.sendMessage("§cAvailable colors: WHITE, RED, BLUE, GREEN, YELLOW, GOLD, AQUA, LIGHT_PURPLE, DARK_GREEN, DARK_AQUA, DARK_PURPLE");
            return true;
        }
        
        town.setColor(color);
        dataManager.saveAll();
        
        // Update BlueMap if integration exists
        if (plugin.getBlueMapIntegration() != null) {
            plugin.getBlueMapIntegration().updateAllMarkers();
        }
        
        sender.sendMessage("§aSet color of town '" + townName + "' to " + color.getChatColor() + color.name());
        
        return true;
    }
    
    // Nation admin methods
    private boolean forceCreateNation(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /interimadmin nation create <nationName> <capitalTownName>");
            return true;
        }
        
        String nationName = args[2];
        String capitalName = args[3];
        
        if (dataManager.nationExists(nationName)) {
            sender.sendMessage("§cNation '" + nationName + "' already exists.");
            return true;
        }
        
        Town capital = dataManager.getTown(capitalName);
        if (capital == null) {
            sender.sendMessage("§cTown '" + capitalName + "' does not exist.");
            return true;
        }
        
        if (capital.hasNation()) {
            sender.sendMessage("§cTown '" + capitalName + "' is already part of a nation.");
            return true;
        }
        
        Nation nation = new Nation(nationName, capital.getUuid());
        capital.setNation(nation);
        
        dataManager.addNation(nation);
        dataManager.saveAll();
        
        sender.sendMessage("§aForce created nation '" + nationName + "' with capital '" + capitalName + "'.");
        
        return true;
    }
    
    private boolean forceDeleteNation(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /interimadmin nation delete <nationName>");
            return true;
        }
        
        String nationName = args[2];
        Nation nation = dataManager.getNation(nationName);
        
        if (nation == null) {
            sender.sendMessage("§cNation '" + nationName + "' does not exist.");
            return true;
        }
        
        // Remove nation from all towns
        for (UUID townUuid : new ArrayList<>(nation.getTowns())) {
            Town town = dataManager.getTown(townUuid);
            if (town != null) {
                town.setNation(null);
            }
        }
        
        dataManager.removeNation(nation);
        dataManager.saveAll();
        sender.sendMessage("§aForce deleted nation '" + nationName + "'.");
        
        return true;
    }
    
    private boolean forceKickFromNation(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /interimadmin nation kick <nationName> <townName>");
            return true;
        }
        
        String nationName = args[2];
        String townName = args[3];
        
        Nation nation = dataManager.getNation(nationName);
        if (nation == null) {
            sender.sendMessage("§cNation '" + nationName + "' does not exist.");
            return true;
        }
        
        Town town = dataManager.getTown(townName);
        if (town == null) {
            sender.sendMessage("§cTown '" + townName + "' does not exist.");
            return true;
        }
        
        if (town.getNation() != nation) {
            sender.sendMessage("§cTown '" + townName + "' is not part of nation '" + nationName + "'.");
            return true;
        }
        
        if (town.getUuid().equals(nation.getCapital())) {
            sender.sendMessage("§cCannot kick the capital town. Delete the nation instead.");
            return true;
        }
        
        nation.removeTown(town.getUuid());
        town.setNation(null);
        
        dataManager.saveAll();
        
        sender.sendMessage("§aForce kicked town '" + townName + "' from nation '" + nationName + "'.");
        
        return true;
    }
    
    private boolean forceAddToNation(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /interimadmin nation add <nationName> <townName>");
            return true;
        }
        
        String nationName = args[2];
        String townName = args[3];
        
        Nation nation = dataManager.getNation(nationName);
        if (nation == null) {
            sender.sendMessage("§cNation '" + nationName + "' does not exist.");
            return true;
        }
        
        Town town = dataManager.getTown(townName);
        if (town == null) {
            sender.sendMessage("§cTown '" + townName + "' does not exist.");
            return true;
        }
        
        if (town.hasNation()) {
            sender.sendMessage("§cTown '" + townName + "' is already part of a nation.");
            return true;
        }
        
        nation.addTown(town.getUuid());
        town.setNation(nation);
        
        dataManager.saveAll();
        
        sender.sendMessage("§aForce added town '" + townName + "' to nation '" + nationName + "'.");
        
        return true;
    }
    
    private boolean forceRenameNation(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /interimadmin nation rename <oldName> <newName>");
            return true;
        }
        
        String oldName = args[2];
        String newName = args[3];
        
        Nation nation = dataManager.getNation(oldName);
        if (nation == null) {
            sender.sendMessage("§cNation '" + oldName + "' does not exist.");
            return true;
        }
        
        if (dataManager.nationExists(newName)) {
            sender.sendMessage("§cNation '" + newName + "' already exists.");
            return true;
        }
        
        dataManager.removeNation(nation);
        nation.setName(newName);
        dataManager.addNation(nation);
        dataManager.saveAll();
        
        sender.sendMessage("§aForce renamed nation '" + oldName + "' to '" + newName + "'.");
        
        return true;
    }
    
    private boolean forceDepositNation(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /interimadmin nation deposit <nationName> <amount>");
            return true;
        }
        
        String nationName = args[2];
        Nation nation = dataManager.getNation(nationName);
        
        if (nation == null) {
            sender.sendMessage("§cNation '" + nationName + "' does not exist.");
            return true;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid amount: " + args[3]);
            return true;
        }
        
        if (amount <= 0) {
            sender.sendMessage("§cAmount must be positive.");
            return true;
        }
        
        nation.deposit(amount);
        dataManager.saveAll();
        
        sender.sendMessage("§aForce deposited $" + amount + " to nation '" + nationName + "'. New balance: $" + nation.getBank());
        
        return true;
    }
    
    private boolean forceWithdrawNation(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /interimadmin nation withdraw <nationName> <amount>");
            return true;
        }
        
        String nationName = args[2];
        Nation nation = dataManager.getNation(nationName);
        
        if (nation == null) {
            sender.sendMessage("§cNation '" + nationName + "' does not exist.");
            return true;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid amount: " + args[3]);
            return true;
        }
        
        if (amount <= 0) {
            sender.sendMessage("§cAmount must be positive.");
            return true;
        }
        
        nation.setBank(nation.getBank() - amount);
        dataManager.saveAll();
        
        sender.sendMessage("§aForce withdrew $" + amount + " from nation '" + nationName + "'. New balance: $" + nation.getBank());
        
        return true;
    }
    
    private boolean forceSetNationColor(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /interimadmin nation color <nationName> <color>");
            sender.sendMessage("§cAvailable colors: WHITE, RED, BLUE, GREEN, YELLOW, GOLD, AQUA, LIGHT_PURPLE, DARK_GREEN, DARK_AQUA, DARK_PURPLE");
            return true;
        }
        
        String nationName = args[2];
        Nation nation = dataManager.getNation(nationName);
        
        if (nation == null) {
            sender.sendMessage("§cNation '" + nationName + "' does not exist.");
            return true;
        }
        
        String colorName = args[3].toUpperCase();
        NationColor color;
        
        try {
            color = NationColor.valueOf(colorName);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cInvalid color: " + args[3]);
            sender.sendMessage("§cAvailable colors: WHITE, RED, BLUE, GREEN, YELLOW, GOLD, AQUA, LIGHT_PURPLE, DARK_GREEN, DARK_AQUA, DARK_PURPLE");
            return true;
        }
        
        nation.setColor(color);
        dataManager.saveAll();
        
        // Update BlueMap if integration exists
        if (plugin.getBlueMapIntegration() != null) {
            plugin.getBlueMapIntegration().updateAllMarkers();
        }
        
        sender.sendMessage("§aSet color of nation '" + nationName + "' to " + color.getChatColor() + color.name());
        
        return true;
    }
    
    // Resident admin methods
    private boolean forceSetRank(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /interimadmin resident setrank <playerName> <rank>");
            sender.sendMessage("§cRanks: MAYOR, ASSISTANT, RESIDENT");
            return true;
        }
        
        String playerName = args[2];
        String rankStr = args[3].toUpperCase();
        
        TownRank rank;
        try {
            rank = TownRank.valueOf(rankStr);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cInvalid rank: " + rankStr);
            sender.sendMessage("§cValid ranks: MAYOR, ASSISTANT, RESIDENT");
            return true;
        }
        
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            sender.sendMessage("§cPlayer '" + playerName + "' is not in a town.");
            return true;
        }
        
        resident.setRank(rank);
        
        Town town = resident.getTown();
        if (rank == TownRank.ASSISTANT) {
            town.addAssistant(player.getUniqueId());
        } else {
            town.removeAssistant(player.getUniqueId());
        }
        
        dataManager.saveAll();
        
        sender.sendMessage("§aForce set rank of '" + playerName + "' to " + rank + ".");
        
        return true;
    }
    
    private boolean forceResetResident(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /interimadmin resident reset <playerName>");
            return true;
        }
        
        String playerName = args[2];
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null) {
            sender.sendMessage("§cPlayer '" + playerName + "' has no resident data.");
            return true;
        }
        
        // Remove from town if in one
        if (resident.hasTown()) {
            Town town = resident.getTown();
            if (town.getMayor().equals(player.getUniqueId())) {
                sender.sendMessage("§cCannot reset mayor. Delete the town instead.");
                return true;
            }
            town.removeResident(player.getUniqueId());
        }
        
        dataManager.removeResident(resident);
        dataManager.saveAll();
        sender.sendMessage("§aForce reset resident data for '" + playerName + "'.");
        
        return true;
    }
    
    // Claim admin methods
    private boolean forceAddClaim(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /interimadmin claim add <townName> [chunkX] [chunkZ] [world]");
            return true;
        }
        
        String townName = args[2];
        Town town = dataManager.getTown(townName);
        
        if (town == null) {
            sender.sendMessage("§cTown '" + townName + "' does not exist.");
            return true;
        }
        
        Chunk chunk;
        World world;
        
        if (args.length >= 6) {
            // Manual coordinates
            try {
                int chunkX = Integer.parseInt(args[3]);
                int chunkZ = Integer.parseInt(args[4]);
                String worldName = args[5];
                
                world = Bukkit.getWorld(worldName);
                if (world == null) {
                    sender.sendMessage("§cWorld '" + worldName + "' does not exist.");
                    return true;
                }
                
                chunk = world.getChunkAt(chunkX, chunkZ);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid chunk coordinates.");
                return true;
            }
        } else {
            // Current location
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cYou must specify chunk coordinates when using console.");
                return true;
            }
            
            Player player = (Player) sender;
            chunk = player.getLocation().getChunk();
            world = player.getWorld();
        }
        
        Claim existingClaim = dataManager.getClaim(world.getName(), chunk.getX(), chunk.getZ());
        if (existingClaim != null) {
            sender.sendMessage("§cThis chunk is already claimed by a town.");
            return true;
        }
        
        Claim claim = new Claim(chunk, town);
        town.addClaim(claim);
        dataManager.addClaim(claim);
        dataManager.saveAll();
        
        sender.sendMessage("§aForce claimed chunk [" + chunk.getX() + ", " + chunk.getZ() + "] in " + world.getName() + " for town '" + townName + "'.");
        
        return true;
    }
    
    private boolean forceRemoveClaim(CommandSender sender, String[] args) {
        if (args.length < 5) {
            sender.sendMessage("§cUsage: /interimadmin claim remove <chunkX> <chunkZ> <world>");
            return true;
        }
        
        try {
            int chunkX = Integer.parseInt(args[2]);
            int chunkZ = Integer.parseInt(args[3]);
            String worldName = args[4];
            
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                sender.sendMessage("§cWorld '" + worldName + "' does not exist.");
                return true;
            }
            
            Claim claim = dataManager.getClaim(worldName, chunkX, chunkZ);
            
            if (claim == null) {
                sender.sendMessage("§cNo claim exists at this location.");
                return true;
            }
            
            Town town = claim.getTown();
            if (town != null) {
                town.removeClaim(claim);
            }
            
            dataManager.removeClaim(claim);
            dataManager.saveAll();
            sender.sendMessage("§aForce removed claim at [" + chunkX + ", " + chunkZ + "] in " + worldName + ".");
            
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid chunk coordinates.");
        }
        
        return true;
    }
    
    private boolean forceClearClaims(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /interimadmin claim clear <townName>");
            return true;
        }
        
        String townName = args[2];
        Town town = dataManager.getTown(townName);
        
        if (town == null) {
            sender.sendMessage("§cTown '" + townName + "' does not exist.");
            return true;
        }
        
        int count = town.getClaims().size();
        
        for (Claim claim : new ArrayList<>(town.getClaims())) {
            dataManager.removeClaim(claim);
        }
        
        town.getClaims().clear();
        dataManager.saveAll();
        
        sender.sendMessage("§aForce cleared " + count + " claims from town '" + townName + "'.");
        
        return true;
    }
    
    private boolean forceSetClaimType(CommandSender sender, String[] args) {
        if (args.length < 6) {
            sender.sendMessage("§cUsage: /interimadmin claim settype <chunkX> <chunkZ> <world> <type>");
            sender.sendMessage("§cTypes: NORMAL, ARENA, EMBASSY, FARM, SHOP, WILDS");
            return true;
        }
        
        try {
            int chunkX = Integer.parseInt(args[2]);
            int chunkZ = Integer.parseInt(args[3]);
            String worldName = args[4];
            String typeStr = args[5].toUpperCase();
            
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                sender.sendMessage("§cWorld '" + worldName + "' does not exist.");
                return true;
            }
            
            ClaimType type;
            try {
                type = ClaimType.valueOf(typeStr);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§cInvalid claim type: " + typeStr);
                sender.sendMessage("§cValid types: NORMAL, ARENA, EMBASSY, FARM, SHOP, WILDS");
                return true;
            }
            
            Claim claim = dataManager.getClaim(worldName, chunkX, chunkZ);
            
            if (claim == null) {
                sender.sendMessage("§cNo claim exists at this location.");
                return true;
            }
            
            claim.setType(type);
            dataManager.saveAll();
            
            sender.sendMessage("§aForce set claim type to " + type + " at [" + chunkX + ", " + chunkZ + "] in " + worldName + ".");
            
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid chunk coordinates.");
        }
        
        return true;
    }
    
    // Info command
    private boolean handleInfo(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /interimadmin info <town|nation|player> <name>");
            return true;
        }
        
        String type = args[1].toLowerCase();
        String name = args[2];
        
        switch (type) {
            case "town":
                Town town = dataManager.getTown(name);
                if (town == null) {
                    sender.sendMessage("§cTown '" + name + "' does not exist.");
                    return true;
                }
                
                sender.sendMessage("§6=== Town: " + town.getName() + " ===");
                sender.sendMessage("§eUUID: §f" + town.getUuid());
                sender.sendMessage("§eMayor: §f" + Bukkit.getOfflinePlayer(town.getMayor()).getName());
                sender.sendMessage("§eResidents: §f" + town.getResidentCount());
                sender.sendMessage("§eClaims: §f" + town.getClaimCount());
                sender.sendMessage("§eBank: §f$" + town.getBank());
                sender.sendMessage("§eNation: §f" + (town.hasNation() ? town.getNation().getName() : "None"));
                sender.sendMessage("§eFounded: §f" + new Date(town.getFounded()));
                break;
                
            case "nation":
                Nation nation = dataManager.getNation(name);
                if (nation == null) {
                    sender.sendMessage("§cNation '" + name + "' does not exist.");
                    return true;
                }
                
                Town capital = dataManager.getTown(nation.getCapital());
                sender.sendMessage("§6=== Nation: " + nation.getName() + " ===");
                sender.sendMessage("§eUUID: §f" + nation.getUuid());
                sender.sendMessage("§eCapital: §f" + (capital != null ? capital.getName() : "Unknown"));
                sender.sendMessage("§eTowns: §f" + nation.getTownCount());
                sender.sendMessage("§eBank: §f$" + nation.getBank());
                sender.sendMessage("§eColor: §f" + nation.getColor());
                sender.sendMessage("§eFounded: §f" + new Date(nation.getFounded()));
                break;
                
            case "player":
                OfflinePlayer player = Bukkit.getOfflinePlayer(name);
                Resident resident = dataManager.getResident(player.getUniqueId());
                
                if (resident == null) {
                    sender.sendMessage("§cPlayer '" + name + "' has no resident data.");
                    return true;
                }
                
                sender.sendMessage("§6=== Player: " + resident.getName() + " ===");
                sender.sendMessage("§eUUID: §f" + resident.getUuid());
                sender.sendMessage("§eTown: §f" + (resident.hasTown() ? resident.getTown().getName() : "None"));
                sender.sendMessage("§eRank: §f" + (resident.getRank() != null ? resident.getRank() : "None"));
                sender.sendMessage("§eJoined Town: §f" + new Date(resident.getJoinedTown()));
                break;
                
            default:
                sender.sendMessage("§cUnknown info type: " + type);
                return true;
        }
        
        return true;
    }
    
    // Purge command
    private boolean handlePurge(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /interimadmin purge <towns|nations|residents|all> [confirm]");
            return true;
        }
        
        String type = args[1].toLowerCase();
        
        if (args.length < 3 || !args[2].equalsIgnoreCase("confirm")) {
            sender.sendMessage("§c§lWARNING: This will permanently delete data!");
            sender.sendMessage("§cTo confirm, use: /interimadmin purge " + type + " confirm");
            return true;
        }
        
        switch (type) {
            case "towns":
                int townCount = dataManager.getTowns().size();
                for (Town town : new ArrayList<>(dataManager.getTowns())) {
                    dataManager.removeTown(town);
                }
                dataManager.saveAll();
                sender.sendMessage("§aPurged " + townCount + " towns.");
                break;
                
            case "nations":
                int nationCount = dataManager.getNations().size();
                for (Nation nation : new ArrayList<>(dataManager.getNations())) {
                    dataManager.removeNation(nation);
                }
                dataManager.saveAll();
                sender.sendMessage("§aPurged " + nationCount + " nations.");
                break;
                
            case "residents":
                int residentCount = dataManager.getResidents().size();
                for (Resident resident : new ArrayList<>(dataManager.getResidents())) {
                    dataManager.removeResident(resident);
                }
                dataManager.saveAll();
                sender.sendMessage("§aPurged " + residentCount + " residents.");
                break;
                
            case "all":
                sender.sendMessage("§cPurging all data...");
                for (Nation nation : new ArrayList<>(dataManager.getNations())) {
                    dataManager.removeNation(nation);
                }
                for (Town town : new ArrayList<>(dataManager.getTowns())) {
                    for (Claim claim : new ArrayList<>(town.getClaims())) {
                        dataManager.removeClaim(claim);
                    }
                    dataManager.removeTown(town);
                }
                for (Resident resident : new ArrayList<>(dataManager.getResidents())) {
                    dataManager.removeResident(resident);
                }
                dataManager.saveAll();
                sender.sendMessage("§aAll plugin data has been purged.");
                break;
                
            default:
                sender.sendMessage("§cUnknown purge type: " + type);
                return true;
        }
        
        return true;
    }
    
    // Reload command
    private boolean handleReload(CommandSender sender) {
        plugin.reloadConfig();
        dataManager.loadAll();
        sender.sendMessage("§aReloaded Interim configuration and data.");
        return true;
    }

    // Recover command - one-time recovery to reattach claims to towns
    private boolean handleRecover(CommandSender sender, String[] args) {
        if (args.length < 2 || !args[1].equalsIgnoreCase("confirm")) {
            sender.sendMessage("§c§lWARNING: This will attempt to modify claim data and create backups.");
            sender.sendMessage("§cTo confirm, use: /interimadmin recover confirm");
            return true;
        }

        sender.sendMessage("§eStarting claim recovery... backups will be created if possible.");
        Map<String, Integer> stats = dataManager.recoverClaims();
        int total = stats.getOrDefault("total", 0);
        int recovered = stats.getOrDefault("recovered", 0);
        int skipped = stats.getOrDefault("skipped", 0);

        sender.sendMessage("§aClaim recovery complete:");
        sender.sendMessage("§eTotal scanned: §f" + total);
        sender.sendMessage("§eRecovered: §f" + recovered);
        sender.sendMessage("§eSkipped: §f" + skipped);

        Bukkit.getLogger().info("Interim: claim recovery finished. total=" + total + " recovered=" + recovered + " skipped=" + skipped);
        // Reload data to ensure in-memory state is consistent
        dataManager.loadAll();

        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6=== Interim Admin Commands ===");
        sender.sendMessage("§e/interimadmin town create <name> <mayor> §7- Force create town");
        sender.sendMessage("§e/interimadmin town delete <name> §7- Force delete town");
        sender.sendMessage("§e/interimadmin town kick <town> <player> §7- Force kick from town");
        sender.sendMessage("§e/interimadmin town add <town> <player> §7- Force add to town");
        sender.sendMessage("§e/interimadmin town rename <old> <new> §7- Force rename town");
        sender.sendMessage("§e/interimadmin town deposit <town> <amount> §7- Add money to town");
        sender.sendMessage("§e/interimadmin town withdraw <town> <amount> §7- Remove money from town");
        sender.sendMessage("§e/interimadmin town color <town> <color> §7- Set town color");
        sender.sendMessage("§e/interimadmin nation create <name> <capital> §7- Force create nation");
        sender.sendMessage("§e/interimadmin nation delete <name> §7- Force delete nation");
        sender.sendMessage("§e/interimadmin nation kick <nation> <town> §7- Force kick town");
        sender.sendMessage("§e/interimadmin nation add <nation> <town> §7- Force add town");
        sender.sendMessage("§e/interimadmin nation rename <old> <new> §7- Force rename nation");
        sender.sendMessage("§e/interimadmin nation color <nation> <color> §7- Set nation color");
        sender.sendMessage("§e/interimadmin resident setrank <player> <rank> §7- Force set rank");
        sender.sendMessage("§e/interimadmin resident reset <player> §7- Reset player data");
        sender.sendMessage("§e/interimadmin claim add <town> [x] [z] [world] §7- Force claim");
        sender.sendMessage("§e/interimadmin claim remove <x> <z> <world> §7- Force unclaim");
        sender.sendMessage("§e/interimadmin claim clear <town> §7- Clear all claims");
        sender.sendMessage("§e/interimadmin claim settype <x> <z> <world> <type> §7- Set claim type");
        sender.sendMessage("§e/interimadmin info <town|nation|player> <name> §7- View detailed info");
        sender.sendMessage("§e/interimadmin purge <towns|nations|residents|all> confirm §7- Purge data");
        sender.sendMessage("§e/interimadmin reload §7- Reload config and data");
        sender.sendMessage("§e/interimadmin recover confirm §7- Attempt one-time recovery of claims (creates backups)");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("interim.admin")) {
            return Collections.emptyList();
        }
        
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            return Arrays.asList("town", "nation", "resident", "claim", "info", "purge", "reload", "recover");
        }
        
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "town":
                    return Arrays.asList("create", "delete", "kick", "add", "rename", "deposit", "withdraw", "color");
                case "nation":
                    return Arrays.asList("create", "delete", "kick", "add", "rename", "deposit", "withdraw", "color");
                case "resident":
                    return Arrays.asList("setrank", "reset");
                case "claim":
                    return Arrays.asList("add", "remove", "clear", "settype");
                case "info":
                    return Arrays.asList("town", "nation", "player");
                case "purge":
                    return Arrays.asList("towns", "nations", "residents", "all");
            }
        }
        
        if (args.length == 3) {
            switch (args[0].toLowerCase()) {
                case "town":
                    if (args[1].equalsIgnoreCase("create")) {
                        return Collections.singletonList("<townName>");
                    }
                    return dataManager.getTowns().stream()
                            .map(Town::getName)
                            .collect(Collectors.toList());
                case "nation":
                    if (args[1].equalsIgnoreCase("create")) {
                        return Collections.singletonList("<nationName>");
                    }
                    return dataManager.getNations().stream()
                            .map(Nation::getName)
                            .collect(Collectors.toList());
                case "resident":
                    return null; // Show online players
                case "info":
                    if (args[1].equalsIgnoreCase("town")) {
                        return dataManager.getTowns().stream()
                                .map(Town::getName)
                                .collect(Collectors.toList());
                    } else if (args[1].equalsIgnoreCase("nation")) {
                        return dataManager.getNations().stream()
                                .map(Nation::getName)
                                .collect(Collectors.toList());
                    } else if (args[1].equalsIgnoreCase("player")) {
                        return null; // Show online players
                    }
                    break;
                case "purge":
                    return Collections.singletonList("confirm");
            }
        }
        
        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("town")) {
                if (args[1].equalsIgnoreCase("create")) {
                    return null; // Show online players for mayor
                }
                if (args[1].equalsIgnoreCase("kick") || args[1].equalsIgnoreCase("add")) {
                    return null; // Show online players
                }
                if (args[1].equalsIgnoreCase("color")) {
                    return Arrays.stream(NationColor.values())
                            .map(Enum::name)
                            .collect(Collectors.toList());
                }
            } else if (args[0].equalsIgnoreCase("nation")) {
                if (args[1].equalsIgnoreCase("kick") || args[1].equalsIgnoreCase("add")) {
                    return dataManager.getTowns().stream()
                            .map(Town::getName)
                            .collect(Collectors.toList());
                }
                if (args[1].equalsIgnoreCase("color")) {
                    return Arrays.stream(NationColor.values())
                            .map(Enum::name)
                            .collect(Collectors.toList());
                }
            } else if (args[0].equalsIgnoreCase("resident") && args[1].equalsIgnoreCase("setrank")) {
                return Arrays.asList("MAYOR", "ASSISTANT", "RESIDENT");
            }
        }
        
        return completions;
    }
}
