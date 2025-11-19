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

public class NationCommand implements CommandExecutor, TabCompleter {
    private final Interim plugin;
    private final DataManager dataManager;
    
    public NationCommand(Interim plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }

    // convenience wrappers for consistent messaging
    private String pref(String msg) { return plugin.pref(msg); }
    private String success(String msg) { return plugin.success(msg); }
    private String error(String msg) { return plugin.error(msg); }
    private String info(String msg) { return plugin.info(msg); }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Open the nation menu GUI when running /nation with no arguments
            if (sender instanceof Player) {
                Player player = (Player) sender;
                new org.r7l.interim.gui.NationMenuGUI(plugin, player).open();
                return true;
            }
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "create":
                return handleCreate(sender, args);
            case "delete":
                return handleDelete(sender, args);
            case "add":
                return handleAdd(sender, args);
            case "kick":
                return handleKick(sender, args);
            case "leave":
                return handleLeave(sender, args);
            case "ally":
                return handleAlly(sender, args);
            case "enemy":
                return handleEnemy(sender, args);
            case "neutral":
                return handleNeutral(sender, args);
            case "deposit":
                return handleDeposit(sender, args);
            case "withdraw":
                return handleWithdraw(sender, args);
            case "info":
                return handleInfo(sender, args);
            case "list":
                return handleList(sender, args);
            case "tag":
                return handleTag(sender, args);
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
            sender.sendMessage(ChatColor.RED + "Only players can create nations.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(ChatColor.RED + "You must be in a town to create a nation!");
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!town.getMayor().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Only the mayor can create a nation!");
            return true;
        }
        
        if (town.hasNation()) {
            player.sendMessage(ChatColor.RED + "Your town is already in a nation!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /nation create <name>");
            return true;
        }
        
        String nationName = args[1];
        
        // Validate nation name
        int minLength = plugin.getConfig().getInt("nation.min-name-length", 3);
        int maxLength = plugin.getConfig().getInt("nation.max-name-length", 32);
        
        if (nationName.length() < minLength || nationName.length() > maxLength) {
            player.sendMessage(ChatColor.RED + "Nation name must be between " + minLength + " and " + maxLength + " characters.");
            return true;
        }
        
        if (!nationName.matches("[a-zA-Z0-9_]+")) {
            player.sendMessage(ChatColor.RED + "Nation name can only contain letters, numbers, and underscores.");
            return true;
        }
        
        if (dataManager.nationExists(nationName)) {
            player.sendMessage(ChatColor.RED + "A nation with that name already exists!");
            return true;
        }
        
        // Check economy
        double cost = plugin.getConfig().getDouble("nation.creation-cost", 5000.0);
        if (plugin.getEconomy() != null && !town.withdraw(cost)) {
            player.sendMessage(ChatColor.RED + "Your town needs " + cost + " to create a nation!");
            return true;
        }
        
        // Create nation
        Nation nation = new Nation(nationName, town.getUuid());
        dataManager.addNation(nation);
        town.setNation(nation);
        
        player.sendMessage(ChatColor.GREEN + "Nation '" + nationName + "' has been created!");
        return true;
    }
    
    private boolean handleDelete(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can delete nations.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasNation()) {
            player.sendMessage(ChatColor.RED + "You are not in a nation!");
            return true;
        }
        
        Nation nation = resident.getNation();
        Town capitalTown = dataManager.getTown(nation.getCapital());
        
        if (capitalTown == null || !capitalTown.getMayor().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Only the capital's mayor can delete the nation!");
            return true;
        }
        
        if (args.length < 2 || !args[1].equalsIgnoreCase("confirm")) {
            player.sendMessage(ChatColor.YELLOW + "Are you sure you want to delete " + nation.getName() + "?");
            player.sendMessage(ChatColor.YELLOW + "Type '/nation delete confirm' to confirm.");
            return true;
        }
        
        // Remove all towns from nation
        for (UUID townUuid : nation.getTowns()) {
            Town town = dataManager.getTown(townUuid);
            if (town != null) {
                town.setNation(null);
            }
        }
        
        dataManager.removeNation(nation);
        player.sendMessage(ChatColor.GREEN + "Nation has been deleted!");
        return true;
    }
    
    private boolean handleAdd(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can add towns to nations.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasNation()) {
            player.sendMessage(ChatColor.RED + "You are not in a nation!");
            return true;
        }
        
        Nation nation = resident.getNation();
        Town capitalTown = dataManager.getTown(nation.getCapital());
        
        if (capitalTown == null || !capitalTown.getMayor().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Only the capital's mayor can add towns!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /nation add <town>");
            return true;
        }
        
        Town town = dataManager.getTown(args[1]);
        if (town == null) {
            player.sendMessage(ChatColor.RED + "Town not found!");
            return true;
        }
        
        if (town.hasNation()) {
            player.sendMessage(ChatColor.RED + "That town is already in a nation!");
            return true;
        }
        
        town.setNation(nation);
        nation.addTown(town.getUuid());
        
        player.sendMessage(ChatColor.GREEN + town.getName() + " has been added to " + nation.getName() + "!");
        
        // Notify town mayor
        Player mayor = plugin.getServer().getPlayer(town.getMayor());
        if (mayor != null && mayor.isOnline()) {
            mayor.sendMessage(ChatColor.GREEN + "Your town has been added to the nation " + nation.getName() + "!");
        }
        
        return true;
    }

    private boolean handleTag(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(error("Only players can set the nation tag."));
            return true;
        }

        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        if (resident == null || !resident.hasNation()) {
            player.sendMessage(error("You are not in a nation!"));
            return true;
        }

        Nation nation = resident.getNation();
        Town capital = dataManager.getTown(nation.getCapital());
        if (capital == null || !capital.getMayor().equals(player.getUniqueId())) {
            player.sendMessage(error("Only the capital's mayor can set the nation tag!"));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(info("Usage: /nation tag <4-char-tag>"));
            return true;
        }

        String tag = args[1];
        if (tag.length() > 4 || tag.length() < 1) {
            player.sendMessage(error("Tag must be 1-4 characters long."));
            return true;
        }

        nation.setTag(tag);
        dataManager.saveAll();
        player.sendMessage(success("Nation tag set to '" + tag + "'."));
        return true;
    }
    
    private boolean handleKick(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can kick towns from nations.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasNation()) {
            player.sendMessage(ChatColor.RED + "You are not in a nation!");
            return true;
        }
        
        Nation nation = resident.getNation();
        Town capitalTown = dataManager.getTown(nation.getCapital());
        
        if (capitalTown == null || !capitalTown.getMayor().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Only the capital's mayor can kick towns!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /nation kick <town>");
            return true;
        }
        
        Town town = dataManager.getTown(args[1]);
        if (town == null) {
            player.sendMessage(ChatColor.RED + "Town not found!");
            return true;
        }
        
        if (!town.hasNation() || !town.getNation().equals(nation)) {
            player.sendMessage(ChatColor.RED + "That town is not in your nation!");
            return true;
        }
        
        if (town.getUuid().equals(nation.getCapital())) {
            player.sendMessage(ChatColor.RED + "You cannot kick the capital town!");
            return true;
        }
        
        nation.removeTown(town.getUuid());
        town.setNation(null);
        
        player.sendMessage(ChatColor.GREEN + town.getName() + " has been kicked from the nation!");
        
        // Notify town mayor
        Player mayor = plugin.getServer().getPlayer(town.getMayor());
        if (mayor != null && mayor.isOnline()) {
            mayor.sendMessage(ChatColor.RED + "Your town has been kicked from " + nation.getName() + "!");
        }
        
        return true;
    }
    
    private boolean handleLeave(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can leave nations.");
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
            player.sendMessage(ChatColor.RED + "Only the mayor can make the town leave the nation!");
            return true;
        }
        
        if (!town.hasNation()) {
            player.sendMessage(ChatColor.RED + "Your town is not in a nation!");
            return true;
        }
        
        Nation nation = town.getNation();
        
        if (town.getUuid().equals(nation.getCapital())) {
            player.sendMessage(ChatColor.RED + "The capital cannot leave! Use /nation delete to delete the nation.");
            return true;
        }
        
        nation.removeTown(town.getUuid());
        town.setNation(null);
        
        player.sendMessage(ChatColor.GREEN + "Your town has left " + nation.getName() + "!");
        
        return true;
    }
    
    private boolean handleAlly(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can manage alliances.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasNation()) {
            player.sendMessage(ChatColor.RED + "You are not in a nation!");
            return true;
        }
        
        Nation nation = resident.getNation();
        Town capitalTown = dataManager.getTown(nation.getCapital());
        
        if (capitalTown == null || !capitalTown.getMayor().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Only the capital's mayor can manage alliances!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /nation ally <nation>");
            return true;
        }
        
        Nation targetNation = dataManager.getNation(args[1]);
        if (targetNation == null) {
            player.sendMessage(ChatColor.RED + "Nation not found!");
            return true;
        }
        
        if (targetNation.equals(nation)) {
            player.sendMessage(ChatColor.RED + "You cannot ally with yourself!");
            return true;
        }
        
        nation.addAlly(targetNation.getUuid());
        player.sendMessage(ChatColor.GREEN + "You are now allied with " + targetNation.getName() + "!");
        
        return true;
    }
    
    private boolean handleEnemy(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can manage enemies.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasNation()) {
            player.sendMessage(ChatColor.RED + "You are not in a nation!");
            return true;
        }
        
        Nation nation = resident.getNation();
        Town capitalTown = dataManager.getTown(nation.getCapital());
        
        if (capitalTown == null || !capitalTown.getMayor().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Only the capital's mayor can manage enemies!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /nation enemy <nation>");
            return true;
        }
        
        Nation targetNation = dataManager.getNation(args[1]);
        if (targetNation == null) {
            player.sendMessage(ChatColor.RED + "Nation not found!");
            return true;
        }
        
        if (targetNation.equals(nation)) {
            player.sendMessage(ChatColor.RED + "You cannot be enemies with yourself!");
            return true;
        }
        
        nation.addEnemy(targetNation.getUuid());
        player.sendMessage(ChatColor.GREEN + "You are now enemies with " + targetNation.getName() + "!");
        
        return true;
    }
    
    private boolean handleNeutral(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can manage neutrality.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasNation()) {
            player.sendMessage(ChatColor.RED + "You are not in a nation!");
            return true;
        }
        
        Nation nation = resident.getNation();
        Town capitalTown = dataManager.getTown(nation.getCapital());
        
        if (capitalTown == null || !capitalTown.getMayor().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Only the capital's mayor can manage neutrality!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /nation neutral <nation>");
            return true;
        }
        
        Nation targetNation = dataManager.getNation(args[1]);
        if (targetNation == null) {
            player.sendMessage(ChatColor.RED + "Nation not found!");
            return true;
        }
        
        nation.removeAlly(targetNation.getUuid());
        nation.removeEnemy(targetNation.getUuid());
        player.sendMessage(ChatColor.GREEN + "You are now neutral with " + targetNation.getName() + "!");
        
        return true;
    }
    
    private boolean handleDeposit(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can deposit money.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasNation()) {
            player.sendMessage(ChatColor.RED + "You are not in a nation!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /nation deposit <amount>");
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
        resident.getNation().deposit(amount);
        
        player.sendMessage(ChatColor.GREEN + "Deposited " + amount + " to the nation bank!");
        
        return true;
    }
    
    private boolean handleWithdraw(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can withdraw money.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasNation()) {
            player.sendMessage(ChatColor.RED + "You are not in a nation!");
            return true;
        }
        
        Nation nation = resident.getNation();
        Town capitalTown = dataManager.getTown(nation.getCapital());
        
        if (capitalTown == null || !capitalTown.getMayor().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Only the capital's mayor can withdraw money!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /nation withdraw <amount>");
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
        
        if (!nation.withdraw(amount)) {
            player.sendMessage(ChatColor.RED + "The nation doesn't have enough money!");
            return true;
        }
        
        if (plugin.getEconomy() != null) {
            plugin.getEconomy().depositPlayer(player, amount);
        }
        
        player.sendMessage(ChatColor.GREEN + "Withdrew " + amount + " from the nation bank!");
        
        return true;
    }
    
    private boolean handleInfo(CommandSender sender, String[] args) {
        Nation nation;
        
        if (args.length >= 2) {
            nation = dataManager.getNation(args[1]);
            if (nation == null) {
                sender.sendMessage(ChatColor.RED + "Nation not found!");
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: /nation info <nation>");
                return true;
            }
            Player player = (Player) sender;
            Resident resident = dataManager.getResident(player.getUniqueId());
            if (resident == null || !resident.hasNation()) {
                sender.sendMessage(ChatColor.RED + "You are not in a nation!");
                return true;
            }
            nation = resident.getNation();
        }
        
        sender.sendMessage(ChatColor.GOLD + "=== " + nation.getName() + " ===");
        Town capital = dataManager.getTown(nation.getCapital());
        sender.sendMessage(ChatColor.YELLOW + "Capital: " + (capital != null ? capital.getName() : "Unknown"));
        sender.sendMessage(ChatColor.YELLOW + "Towns: " + nation.getTownCount());
        sender.sendMessage(ChatColor.YELLOW + "Bank: " + nation.getBank());
        sender.sendMessage(ChatColor.YELLOW + "Allies: " + nation.getAllies().size());
        sender.sendMessage(ChatColor.YELLOW + "Enemies: " + nation.getEnemies().size());
        
        return true;
    }
    
    private boolean handleList(CommandSender sender, String[] args) {
        Collection<Nation> nations = dataManager.getNations();
        
        if (nations.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No nations exist!");
            return true;
        }
        
        sender.sendMessage(ChatColor.GOLD + "=== Nations (" + nations.size() + ") ===");
        for (Nation nation : nations) {
            sender.sendMessage(ChatColor.YELLOW + "- " + nation.getName() + " (" + nation.getTownCount() + " towns)");
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
        
        if (resident == null || !resident.hasNation()) {
            player.sendMessage(ChatColor.RED + "You are not in a nation!");
            return true;
        }
        
        Nation nation = resident.getNation();
        Town capitalTown = dataManager.getTown(nation.getCapital());
        
        if (capitalTown == null || !capitalTown.getMayor().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Only the capital's mayor can set the board!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /nation board <message>");
            return true;
        }
        
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        nation.setBoard(message);
        player.sendMessage(ChatColor.GREEN + "Nation board updated!");
        
        return true;
    }
    
    private boolean handleRename(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can rename nations.");
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasNation()) {
            player.sendMessage(ChatColor.RED + "You are not in a nation!");
            return true;
        }
        
        Nation nation = resident.getNation();
        Town capitalTown = dataManager.getTown(nation.getCapital());
        
        if (capitalTown == null || !capitalTown.getMayor().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Only the capital's mayor can rename the nation!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /nation rename <name>");
            return true;
        }
        
        String newName = args[1];
        
        // Validate nation name
        int minLength = plugin.getConfig().getInt("nation.min-name-length", 3);
        int maxLength = plugin.getConfig().getInt("nation.max-name-length", 32);
        
        if (newName.length() < minLength || newName.length() > maxLength) {
            player.sendMessage(ChatColor.RED + "Nation name must be between " + minLength + " and " + maxLength + " characters.");
            return true;
        }
        
        if (!newName.matches("[a-zA-Z0-9_]+")) {
            player.sendMessage(ChatColor.RED + "Nation name can only contain letters, numbers, and underscores.");
            return true;
        }
        
        if (dataManager.nationExists(newName)) {
            player.sendMessage(ChatColor.RED + "A nation with that name already exists!");
            return true;
        }
        
        String oldName = nation.getName();
        nation.setName(newName);
        
        // Update the nationsByName map
        dataManager.removeNation(nation);
        dataManager.addNation(nation);
        
        player.sendMessage(ChatColor.GREEN + "Nation renamed from " + oldName + " to " + newName + "!");
        
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Nation Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/nation create <name> - Create a nation");
        sender.sendMessage(ChatColor.YELLOW + "/nation delete - Delete your nation");
        sender.sendMessage(ChatColor.YELLOW + "/nation add <town> - Add a town");
        sender.sendMessage(ChatColor.YELLOW + "/nation kick <town> - Kick a town");
        sender.sendMessage(ChatColor.YELLOW + "/nation leave - Leave the nation");
        sender.sendMessage(ChatColor.YELLOW + "/nation ally <nation> - Set as ally");
        sender.sendMessage(ChatColor.YELLOW + "/nation enemy <nation> - Set as enemy");
        sender.sendMessage(ChatColor.YELLOW + "/nation neutral <nation> - Set as neutral");
        sender.sendMessage(ChatColor.YELLOW + "/nation deposit <amount> - Deposit money");
        sender.sendMessage(ChatColor.YELLOW + "/nation withdraw <amount> - Withdraw money");
        sender.sendMessage(ChatColor.YELLOW + "/nation info [nation] - View nation info");
        sender.sendMessage(ChatColor.YELLOW + "/nation list - List all nations");
        sender.sendMessage(ChatColor.YELLOW + "/nation board <message> - Set nation board");
        sender.sendMessage(ChatColor.YELLOW + "/nation rename <name> - Rename nation");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create", "delete", "add", "kick", "leave", "ally", "enemy", "neutral",
                    "deposit", "withdraw", "info", "list", "board", "rename")
                    .stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }
}
