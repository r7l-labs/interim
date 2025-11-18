package org.r7l.interim.command;

import org.bukkit.ChatColor;
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

public class TownCommand implements CommandExecutor, TabCompleter {
    private final Interim plugin;
    private final DataManager dataManager;
    
    public TownCommand(Interim plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "create":
                return handleCreate(sender, args);
            case "delete":
                return handleDelete(sender, args);
            case "invite":
                return handleInvite(sender, args);
            case "kick":
                return handleKick(sender, args);
            case "leave":
                return handleLeave(sender, args);
            case "claim":
                return handleClaim(sender, args);
            case "unclaim":
                return handleUnclaim(sender, args);
            case "spawn":
                return handleSpawn(sender, args);
            case "setspawn":
                return handleSetSpawn(sender, args);
            case "deposit":
                return handleDeposit(sender, args);
            case "withdraw":
                return handleWithdraw(sender, args);
            case "info":
                return handleInfo(sender, args);
            case "list":
                return handleList(sender, args);
            case "toggle":
                return handleToggle(sender, args);
            case "rank":
                return handleRank(sender, args);
            case "board":
                return handleBoard(sender, args);
            case "rename":
                return handleRename(sender, args);
            default:
                sendHelp(sender);
                return true;
        }
    }
    
    private boolean handleCreate(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can create towns.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /town create <name>");
            return true;
        }
        
        String townName = args[1];
        
        // Validate town name
        int minLength = plugin.getConfig().getInt("town.min-name-length", 3);
        int maxLength = plugin.getConfig().getInt("town.max-name-length", 32);
        
        if (townName.length() < minLength || townName.length() > maxLength) {
            player.sendMessage(ChatColor.RED + "Town name must be between " + minLength + " and " + maxLength + " characters.");
            return true;
        }
        
        if (!townName.matches("[a-zA-Z0-9_]+")) {
            player.sendMessage(ChatColor.RED + "Town name can only contain letters, numbers, and underscores.");
            return true;
        }
        
        // Check if town name is taken
        if (dataManager.townExists(townName)) {
            player.sendMessage(ChatColor.RED + "A town with that name already exists!");
            return true;
        }
        
        // Check economy
        double cost = plugin.getConfig().getDouble("town.creation-cost", 1000.0);
        if (plugin.getEconomy() != null && !plugin.getEconomy().has(player, cost)) {
            player.sendMessage(ChatColor.RED + "You need " + cost + " to create a town!");
            return true;
        }
        
        // Create town
        Resident resident = dataManager.getOrCreateResident(player.getUniqueId(), player.getName());
        Town town = new Town(townName, player.getUniqueId());
        dataManager.addTown(town);
        resident.addTown(town, TownRank.MAYOR, true);
        
        // Charge player
        if (plugin.getEconomy() != null) {
            plugin.getEconomy().withdrawPlayer(player, cost);
        }
        
        player.sendMessage(ChatColor.GREEN + "Town '" + townName + "' has been created!");
        return true;
    }
    
    private boolean handleDelete(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can delete towns.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(ChatColor.RED + "You are not in a town!");
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!town.getMayor().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Only the mayor can delete the town!");
            return true;
        }
        
        if (args.length < 2 || !args[1].equalsIgnoreCase("confirm")) {
            player.sendMessage(ChatColor.YELLOW + "Are you sure you want to delete " + town.getName() + "?");
            player.sendMessage(ChatColor.YELLOW + "Type '/town delete confirm' to confirm.");
            return true;
        }
        
        // Remove all claims
        for (Claim claim : town.getClaims()) {
            dataManager.removeClaim(claim);
        }
        
        // Remove all residents
        for (UUID residentUuid : town.getResidents()) {
            Resident r = dataManager.getResident(residentUuid);
            if (r != null) {
                r.removeTown(town);
            }
        }
        
        // Remove from nation if applicable
        if (town.hasNation()) {
            Nation nation = town.getNation();
            nation.removeTown(town.getUuid());
            if (nation.getTownCount() == 0) {
                dataManager.removeNation(nation);
            }
        }
        
        dataManager.removeTown(town);
        player.sendMessage(ChatColor.GREEN + "Town has been deleted!");
        return true;
    }
    
    private boolean handleInvite(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can invite to towns.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(ChatColor.RED + "You are not in a town!");
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!resident.isMayor() && !resident.isAssistant()) {
            player.sendMessage(ChatColor.RED + "You don't have permission to invite players!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /town invite <player>");
            return true;
        }
        
        Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }
        
        Resident targetResident = dataManager.getOrCreateResident(target.getUniqueId(), target.getName());
        if (targetResident.isInTown(town)) {
            player.sendMessage(ChatColor.RED + "That player is already in this town!");
            return true;
        }
        
        // Create invite
        Invite invite = new Invite(town.getUuid(), town.getName(), player.getUniqueId(), player.getName(), 300000); // 5 minutes
        dataManager.addInvite(target.getUniqueId(), invite);
        
        player.sendMessage(ChatColor.GREEN + "Invited " + target.getName() + " to " + town.getName() + "!");
        target.sendMessage(ChatColor.GREEN + "You have been invited to " + town.getName() + " by " + player.getName() + "!");
        target.sendMessage(ChatColor.YELLOW + "Type '/town accept " + town.getName() + "' to accept or '/town deny " + town.getName() + "' to deny.");
        
        return true;
    }
    
    private boolean handleKick(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can kick from towns.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(ChatColor.RED + "You are not in a town!");
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!resident.isMayor() && !resident.isAssistant()) {
            player.sendMessage(ChatColor.RED + "You don't have permission to kick players!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /town kick <player>");
            return true;
        }
        
        Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }
        
        if (target.getUniqueId().equals(town.getMayor())) {
            player.sendMessage(ChatColor.RED + "You cannot kick the mayor!");
            return true;
        }
        
        Resident targetResident = dataManager.getResident(target.getUniqueId());
        if (targetResident == null || !targetResident.isInTown(town)) {
            player.sendMessage(ChatColor.RED + "That player is not in your town!");
            return true;
        }
        
        town.removeResident(target.getUniqueId());
        targetResident.removeTown(town);
        
        player.sendMessage(ChatColor.GREEN + "Kicked " + target.getName() + " from the town!");
        target.sendMessage(ChatColor.RED + "You have been kicked from " + town.getName() + "!");
        
        return true;
    }
    
    private boolean handleLeave(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can leave towns.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(ChatColor.RED + "You are not in a town!");
            return true;
        }
        
        Town town = resident.getTown();
        
        if (town.getMayor().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "The mayor cannot leave! Use /town delete to delete the town.");
            return true;
        }
        
        town.removeResident(player.getUniqueId());
        resident.removeTown(town);
        
        player.sendMessage(ChatColor.GREEN + "You have left " + town.getName() + "!");
        
        return true;
    }
    
    private boolean handleClaim(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can claim chunks.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(ChatColor.RED + "You are not in a town!");
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!resident.isMayor() && !resident.isAssistant()) {
            player.sendMessage(ChatColor.RED + "You don't have permission to claim chunks!");
            return true;
        }
        
        int chunkX = player.getLocation().getBlockX() >> 4;
        int chunkZ = player.getLocation().getBlockZ() >> 4;
        
        if (dataManager.isClaimed(player.getWorld().getName(), chunkX, chunkZ)) {
            player.sendMessage(ChatColor.RED + "This chunk is already claimed!");
            return true;
        }
        
        int maxClaims = plugin.getConfig().getInt("town.max-claims", 100);
        if (town.getClaimCount() >= maxClaims) {
            player.sendMessage(ChatColor.RED + "Your town has reached the maximum number of claims!");
            return true;
        }
        
        double cost = plugin.getConfig().getDouble("town.claim-cost", 100.0);
        if (plugin.getEconomy() != null && !town.withdraw(cost)) {
            player.sendMessage(ChatColor.RED + "Your town doesn't have enough money! Need: " + cost);
            return true;
        }
        
        Claim claim = new Claim(player.getLocation().getChunk(), town);
        dataManager.addClaim(claim);
        town.addClaim(claim);
        
        player.sendMessage(ChatColor.GREEN + "Chunk claimed for " + town.getName() + "!");
        
        return true;
    }
    
    private boolean handleUnclaim(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can unclaim chunks.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(ChatColor.RED + "You are not in a town!");
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!resident.isMayor() && !resident.isAssistant()) {
            player.sendMessage(ChatColor.RED + "You don't have permission to unclaim chunks!");
            return true;
        }
        
        Claim claim = dataManager.getClaim(player.getLocation());
        if (claim == null || !claim.getTown().equals(town)) {
            player.sendMessage(ChatColor.RED + "This chunk is not claimed by your town!");
            return true;
        }
        
        dataManager.removeClaim(claim);
        town.removeClaim(claim);
        
        player.sendMessage(ChatColor.GREEN + "Chunk unclaimed!");
        
        return true;
    }
    
    private boolean handleSpawn(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can teleport.");
            return true;
        }
        
        Player player = (Player) sender;
        
        Town town;
        if (args.length >= 2) {
            town = dataManager.getTown(args[1]);
            if (town == null) {
                player.sendMessage(ChatColor.RED + "Town not found!");
                return true;
            }
        } else {
            Resident resident = dataManager.getResident(player.getUniqueId());
            if (resident == null || !resident.hasTown()) {
                player.sendMessage(ChatColor.RED + "You are not in a town!");
                return true;
            }
            town = resident.getTown();
        }
        
        if (town.getSpawn() == null) {
            player.sendMessage(ChatColor.RED + "That town doesn't have a spawn set!");
            return true;
        }
        
        player.teleport(town.getSpawn());
        player.sendMessage(ChatColor.GREEN + "Teleported to " + town.getName() + " spawn!");
        
        return true;
    }
    
    private boolean handleSetSpawn(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can set spawns.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(ChatColor.RED + "You are not in a town!");
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!resident.isMayor() && !resident.isAssistant()) {
            player.sendMessage(ChatColor.RED + "You don't have permission to set the spawn!");
            return true;
        }
        
        Claim claim = dataManager.getClaim(player.getLocation());
        if (claim == null || !claim.getTown().equals(town)) {
            player.sendMessage(ChatColor.RED + "You can only set spawn in your town's territory!");
            return true;
        }
        
        town.setSpawn(player.getLocation());
        
        // Refresh particle effect for the spawn
        if (plugin.getParticleManager() != null) {
            plugin.getParticleManager().refreshParticleEffect(town);
        }
        
        player.sendMessage(ChatColor.GREEN + "Town spawn has been set!");
        
        return true;
    }
    
    private boolean handleDeposit(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can deposit money.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(ChatColor.RED + "You are not in a town!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /town deposit <amount>");
            return true;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid amount!");
            return true;
        }
        
        if (amount <= 0) {
            player.sendMessage(ChatColor.RED + "Amount must be positive!");
            return true;
        }
        
        if (plugin.getEconomy() == null || !plugin.getEconomy().has(player, amount)) {
            player.sendMessage(ChatColor.RED + "You don't have enough money!");
            return true;
        }
        
        plugin.getEconomy().withdrawPlayer(player, amount);
        resident.getTown().deposit(amount);
        
        player.sendMessage(ChatColor.GREEN + "Deposited " + amount + " to the town bank!");
        
        return true;
    }
    
    private boolean handleWithdraw(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can withdraw money.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(ChatColor.RED + "You are not in a town!");
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!resident.isMayor()) {
            player.sendMessage(ChatColor.RED + "Only the mayor can withdraw money!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /town withdraw <amount>");
            return true;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid amount!");
            return true;
        }
        
        if (amount <= 0) {
            player.sendMessage(ChatColor.RED + "Amount must be positive!");
            return true;
        }
        
        if (!town.withdraw(amount)) {
            player.sendMessage(ChatColor.RED + "The town doesn't have enough money!");
            return true;
        }
        
        if (plugin.getEconomy() != null) {
            plugin.getEconomy().depositPlayer(player, amount);
        }
        
        player.sendMessage(ChatColor.GREEN + "Withdrew " + amount + " from the town bank!");
        
        return true;
    }
    
    private boolean handleInfo(CommandSender sender, String[] args) {
        Town town;
        
        if (args.length >= 2) {
            town = dataManager.getTown(args[1]);
            if (town == null) {
                sender.sendMessage(ChatColor.RED + "Town not found!");
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: /town info <town>");
                return true;
            }
            Player player = (Player) sender;
            Resident resident = dataManager.getResident(player.getUniqueId());
            if (resident == null || !resident.hasTown()) {
                sender.sendMessage(ChatColor.RED + "You are not in a town!");
                return true;
            }
            town = resident.getTown();
        }
        
        sender.sendMessage(ChatColor.GOLD + "=== " + town.getName() + " ===");
        Resident mayor = dataManager.getResident(town.getMayor());
        sender.sendMessage(ChatColor.YELLOW + "Mayor: " + (mayor != null ? mayor.getName() : "Unknown"));
        sender.sendMessage(ChatColor.YELLOW + "Residents: " + town.getResidentCount());
        sender.sendMessage(ChatColor.YELLOW + "Claims: " + town.getClaimCount());
        sender.sendMessage(ChatColor.YELLOW + "Bank: " + town.getBank());
        if (town.hasNation()) {
            sender.sendMessage(ChatColor.YELLOW + "Nation: " + town.getNation().getName());
        }
        sender.sendMessage(ChatColor.YELLOW + "Open: " + (town.isOpen() ? "Yes" : "No"));
        sender.sendMessage(ChatColor.YELLOW + "PvP: " + (town.isPvp() ? "Enabled" : "Disabled"));
        
        return true;
    }
    
    private boolean handleList(CommandSender sender, String[] args) {
        Collection<Town> towns = dataManager.getTowns();
        
        if (towns.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No towns exist!");
            return true;
        }
        
        sender.sendMessage(ChatColor.GOLD + "=== Towns (" + towns.size() + ") ===");
        for (Town town : towns) {
            sender.sendMessage(ChatColor.YELLOW + "- " + town.getName() + " (" + town.getResidentCount() + " residents)");
        }
        
        return true;
    }
    
    private boolean handleToggle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can toggle settings.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(ChatColor.RED + "You are not in a town!");
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!resident.isMayor()) {
            player.sendMessage(ChatColor.RED + "Only the mayor can toggle settings!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /town toggle <pvp|explosions|mobs|open>");
            return true;
        }
        
        switch (args[1].toLowerCase()) {
            case "pvp":
                town.setPvp(!town.isPvp());
                player.sendMessage(ChatColor.GREEN + "PvP " + (town.isPvp() ? "enabled" : "disabled") + "!");
                break;
            case "explosions":
                town.setExplosions(!town.isExplosions());
                player.sendMessage(ChatColor.GREEN + "Explosions " + (town.isExplosions() ? "enabled" : "disabled") + "!");
                break;
            case "mobs":
                town.setMobSpawning(!town.isMobSpawning());
                player.sendMessage(ChatColor.GREEN + "Mob spawning " + (town.isMobSpawning() ? "enabled" : "disabled") + "!");
                break;
            case "open":
                town.setOpen(!town.isOpen());
                player.sendMessage(ChatColor.GREEN + "Town is now " + (town.isOpen() ? "open" : "closed") + "!");
                break;
            default:
                player.sendMessage(ChatColor.RED + "Invalid option! Options: pvp, explosions, mobs, open");
                break;
        }
        
        return true;
    }
    
    private boolean handleRank(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can manage ranks.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(ChatColor.RED + "You are not in a town!");
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!resident.isMayor()) {
            player.sendMessage(ChatColor.RED + "Only the mayor can manage ranks!");
            return true;
        }
        
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /town rank <player> <assistant|resident>");
            return true;
        }
        
        Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }
        
        Resident targetResident = dataManager.getResident(target.getUniqueId());
        if (targetResident == null || !targetResident.hasTown() || !targetResident.getTown().equals(town)) {
            player.sendMessage(ChatColor.RED + "That player is not in your town!");
            return true;
        }
        
        String rankStr = args[2].toLowerCase();
        if (rankStr.equals("assistant")) {
            town.addAssistant(target.getUniqueId());
            targetResident.setRank(TownRank.ASSISTANT);
            player.sendMessage(ChatColor.GREEN + target.getName() + " is now an assistant!");
            target.sendMessage(ChatColor.GREEN + "You have been promoted to assistant!");
        } else if (rankStr.equals("resident")) {
            town.removeAssistant(target.getUniqueId());
            targetResident.setRank(TownRank.RESIDENT);
            player.sendMessage(ChatColor.GREEN + target.getName() + " is now a resident!");
            target.sendMessage(ChatColor.YELLOW + "You have been demoted to resident!");
        } else {
            player.sendMessage(ChatColor.RED + "Invalid rank! Options: assistant, resident");
        }
        
        return true;
    }
    
    private boolean handleBoard(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can set the board.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(ChatColor.RED + "You are not in a town!");
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!resident.isMayor() && !resident.isAssistant()) {
            player.sendMessage(ChatColor.RED + "You don't have permission to set the board!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /town board <message>");
            return true;
        }
        
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        town.setBoard(message);
        player.sendMessage(ChatColor.GREEN + "Town board updated!");
        
        return true;
    }
    
    private boolean handleRename(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can rename towns.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(ChatColor.RED + "You are not in a town!");
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!resident.isMayor()) {
            player.sendMessage(ChatColor.RED + "Only the mayor can rename the town!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /town rename <name>");
            return true;
        }
        
        String newName = args[1];
        
        // Validate town name
        int minLength = plugin.getConfig().getInt("town.min-name-length", 3);
        int maxLength = plugin.getConfig().getInt("town.max-name-length", 32);
        
        if (newName.length() < minLength || newName.length() > maxLength) {
            player.sendMessage(ChatColor.RED + "Town name must be between " + minLength + " and " + maxLength + " characters.");
            return true;
        }
        
        if (!newName.matches("[a-zA-Z0-9_]+")) {
            player.sendMessage(ChatColor.RED + "Town name can only contain letters, numbers, and underscores.");
            return true;
        }
        
        if (dataManager.townExists(newName)) {
            player.sendMessage(ChatColor.RED + "A town with that name already exists!");
            return true;
        }
        
        String oldName = town.getName();
        town.setName(newName);
        
        // Update the townsByName map
        dataManager.removeTown(town);
        dataManager.addTown(town);
        
        player.sendMessage(ChatColor.GREEN + "Town renamed from " + oldName + " to " + newName + "!");
        
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Town Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/town create <name> - Create a town");
        sender.sendMessage(ChatColor.YELLOW + "/town delete - Delete your town");
        sender.sendMessage(ChatColor.YELLOW + "/town invite <player> - Invite a player");
        sender.sendMessage(ChatColor.YELLOW + "/town kick <player> - Kick a player");
        sender.sendMessage(ChatColor.YELLOW + "/town leave - Leave your town");
        sender.sendMessage(ChatColor.YELLOW + "/town claim - Claim a chunk");
        sender.sendMessage(ChatColor.YELLOW + "/town unclaim - Unclaim a chunk");
        sender.sendMessage(ChatColor.YELLOW + "/town spawn [town] - Teleport to town spawn");
        sender.sendMessage(ChatColor.YELLOW + "/town setspawn - Set town spawn");
        sender.sendMessage(ChatColor.YELLOW + "/town deposit <amount> - Deposit money");
        sender.sendMessage(ChatColor.YELLOW + "/town withdraw <amount> - Withdraw money");
        sender.sendMessage(ChatColor.YELLOW + "/town info [town] - View town info");
        sender.sendMessage(ChatColor.YELLOW + "/town list - List all towns");
        sender.sendMessage(ChatColor.YELLOW + "/town toggle <option> - Toggle settings");
        sender.sendMessage(ChatColor.YELLOW + "/town rank <player> <rank> - Set player rank");
        sender.sendMessage(ChatColor.YELLOW + "/town board <message> - Set town board");
        sender.sendMessage(ChatColor.YELLOW + "/town rename <name> - Rename town");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create", "delete", "invite", "kick", "leave", "claim", "unclaim",
                    "spawn", "setspawn", "deposit", "withdraw", "info", "list", "toggle", "rank", "board", "rename")
                    .stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("toggle")) {
                return Arrays.asList("pvp", "explosions", "mobs", "open")
                        .stream()
                        .filter(s -> s.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        
        return new ArrayList<>();
    }
}
