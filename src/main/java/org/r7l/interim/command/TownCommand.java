package org.r7l.interim.command;

// chat color constants deprecated; use Interim messaging helpers instead
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.scheduler.BukkitRunnable;
// ChatColor deprecated — use plugin messaging helpers instead
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

    // convenience wrappers to centralize message formatting
    private String pref(String msg) { return plugin.pref(msg); }
    private String success(String msg) { return plugin.success(msg); }
    private String error(String msg) { return plugin.error(msg); }
    private String info(String msg) { return plugin.info(msg); }

    // Check whether removing a claim would disconnect the town's claims
    private boolean wouldDisconnect(Town town, Claim remove) {
        List<Claim> remaining = new ArrayList<>();
        for (Claim c : town.getClaims()) {
            if (!c.equals(remove)) remaining.add(c);
        }
        if (remaining.isEmpty()) return false;

        Set<Claim> visited = new HashSet<>();
        Deque<Claim> stack = new ArrayDeque<>();
        stack.push(remaining.get(0));
        visited.add(remaining.get(0));

        while (!stack.isEmpty()) {
            Claim cur = stack.pop();
            for (Claim n : remaining) {
                if (!visited.contains(n) && cur.isAdjacentTo(n)) {
                    visited.add(n);
                    stack.push(n);
                }
            }
        }

        return visited.size() != remaining.size();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Open the town menu GUI when running /town with no arguments
            if (sender instanceof Player) {
                Player player = (Player) sender;
                new org.r7l.interim.gui.TownMenuGUI(plugin, player).open();
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
            case "invite":
                return handleInvite(sender, args);
            case "kick":
                return handleKick(sender, args);
            case "leave":
                return handleLeave(sender, args);
            case "claim":
                return handleClaim(sender, args);
            case "view":
                return handleView(sender, args);
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
            case "tag":
                return handleTag(sender, args);
            case "toggle":
                return handleToggle(sender, args);
            case "rank":
                return handleRank(sender, args);
            case "board":
                return handleBoard(sender, args);
            case "rename":
                return handleRename(sender, args);
            case "accept":
                return handleAccept(sender, args);
            case "deny":
                return handleDeny(sender, args);
            default:
                sendHelp(sender);
                return true;
        }
    }
    
    private boolean handleCreate(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(error("Only players can create towns."));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length < 2) {
            player.sendMessage(error("Usage: /town create <name>"));
            return true;
        }
        
        String townName = args[1];
        
        // Validate town name
        int minLength = plugin.getConfig().getInt("town.min-name-length", 3);
        int maxLength = plugin.getConfig().getInt("town.max-name-length", 32);
        
        if (townName.length() < minLength || townName.length() > maxLength) {
            player.sendMessage(error("Town name must be between " + minLength + " and " + maxLength + " characters."));
            return true;
        }
        
        if (!townName.matches("[a-zA-Z0-9_]+")) {
            player.sendMessage(error("Town name can only contain letters, numbers, and underscores."));
            return true;
        }
        
        // Check if town name is taken
        if (dataManager.townExists(townName)) {
            player.sendMessage(error("A town with that name already exists!"));
            return true;
        }
        
        // Check economy
        double cost = plugin.getConfig().getDouble("town.creation-cost", 1000.0);
        if (plugin.getEconomy() != null && !plugin.getEconomy().has(player, cost)) {
            player.sendMessage(error("You need " + cost + " to create a town!"));
            return true;
        }
        
        // Create town
        Resident resident = dataManager.getOrCreateResident(player.getUniqueId(), player.getName());
        // Prevent creating multiple towns per player
        if (resident.hasTown()) {
            player.sendMessage(error("You already have a town. You cannot create multiple towns."));
            return true;
        }
        Town town = new Town(townName, player.getUniqueId());
        dataManager.addTown(town);
        resident.addTown(town, TownRank.MAYOR, true);
        
        // Charge player
        if (plugin.getEconomy() != null) {
            plugin.getEconomy().withdrawPlayer(player, cost);
        }
        
        player.sendMessage(success("Town '" + townName + "' has been created!"));

        // Only allow claiming in configured worlds
        List<String> allowedWorlds = plugin.getConfig().getStringList("town.allowed-claim-worlds");
        String currentWorld = player.getWorld().getName();
        if (!allowedWorlds.contains(currentWorld)) {
            player.sendMessage(error("Claiming is not allowed in this world."));
            return true;
        }
        // Auto-claim the chunk where the player is standing. If successful, set spawn (homeblock).
        int chunkX = player.getLocation().getBlockX() >> 4;
        int chunkZ = player.getLocation().getBlockZ() >> 4;
        boolean autoClaimed = false;
        if (!dataManager.isClaimed(currentWorld, chunkX, chunkZ)) {
            Claim claim = new Claim(player.getLocation().getChunk(), town, player.getUniqueId());
            dataManager.addClaim(claim);
            town.addClaim(claim);
            autoClaimed = true;
            player.sendMessage(success("Starting chunk claimed for " + town.getName() + "!"));
        }

        // Only set spawn if we successfully auto-claimed the homeblock chunk
        if (autoClaimed) {
            town.setSpawn(player.getLocation());
            if (plugin.getParticleManager() != null) {
                plugin.getParticleManager().startParticleEffect(town);
            }
            player.sendMessage(success("Town spawn set at your current location (homeblock)."));
        } else {
            // Do not set spawn if the chunk could not be claimed. Instruct the player to set spawn after claiming a homeblock.
            player.sendMessage(info("Town created, but spawn was not set because this chunk could not be claimed."));
            player.sendMessage(info("Claim a chunk for your town and then use /town setspawn to set the homeblock spawn."));
        }
        // Broadcast creation to server with a pretty message
        Bukkit.getServer().broadcastMessage(plugin.success("Town '" + town.getName() + "' has been created by " + player.getName() + "!"));
        return true;
    }
    
    private boolean handleDelete(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(error("Only players can delete towns."));
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(error("You are not in a town!"));
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!town.getMayor().equals(player.getUniqueId())) {
            player.sendMessage(error("Only the mayor can delete the town!"));
            return true;
        }
        
        if (args.length < 2 || !args[1].equalsIgnoreCase("confirm")) {
            player.sendMessage(info("Are you sure you want to delete " + town.getName() + "?"));
            player.sendMessage(info("Type '/town delete confirm' to confirm."));
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
        player.sendMessage(success("Town has been deleted!"));
        // Broadcast deletion
        Bukkit.getServer().broadcastMessage(plugin.info("Town '" + town.getName() + "' has been deleted by " + player.getName() + "!"));
        return true;
    }
    
    private boolean handleInvite(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(error("Only players can invite to towns."));
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(error("You are not in a town!"));
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!resident.isMayor() && !resident.isAssistant()) {
            player.sendMessage(error("You don't have permission to invite players!"));
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(error("Usage: /town invite <player>"));
            return true;
        }
        
        Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(error("Player not found!"));
            return true;
        }
        
        Resident targetResident = dataManager.getOrCreateResident(target.getUniqueId(), target.getName());
        if (targetResident.isInTown(town)) {
            player.sendMessage(error("That player is already in this town!"));
            return true;
        }
        
        // Create invite
        Invite invite = new Invite(town.getUuid(), town.getName(), player.getUniqueId(), player.getName(), 300000); // 5 minutes
        dataManager.addInvite(target.getUniqueId(), invite);
        
    player.sendMessage(success("Invited " + target.getName() + " to " + town.getName() + "!"));
    target.sendMessage(success("You have been invited to " + town.getName() + " by " + player.getName() + "!"));
    target.sendMessage(info("Type '/town accept " + town.getName() + "' to accept or '/town deny " + town.getName() + "' to deny."));
        
        return true;
    }
    
    private boolean handleKick(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(error("Only players can kick from towns."));
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(error("You are not in a town!"));
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!resident.isMayor() && !resident.isAssistant()) {
            player.sendMessage(error("You don't have permission to kick players!"));
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(error("Usage: /town kick <player>"));
            return true;
        }
        
        Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(error("Player not found!"));
            return true;
        }
        
        if (target.getUniqueId().equals(town.getMayor())) {
            player.sendMessage(error("You cannot kick the mayor!"));
            return true;
        }
        
        Resident targetResident = dataManager.getResident(target.getUniqueId());
        if (targetResident == null || !targetResident.isInTown(town)) {
            player.sendMessage(error("That player is not in your town!"));
            return true;
        }
        
    town.removeResident(target.getUniqueId());
    targetResident.removeTown(town);

    player.sendMessage(success("Kicked " + target.getName() + " from the town!"));
    target.sendMessage(error("You have been kicked from " + town.getName() + "!"));
        
        return true;
    }
    
    private boolean handleLeave(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(error("Only players can leave towns."));
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(error("You are not in a town!"));
            return true;
        }
        
        Town town = resident.getTown();
        
        if (town.getMayor().equals(player.getUniqueId())) {
            player.sendMessage(error("The mayor cannot leave! Use /town delete to delete the town."));
            return true;
        }
        
        town.removeResident(player.getUniqueId());
        resident.removeTown(town);
        
    player.sendMessage(success("You have left " + town.getName() + "!"));
        
        return true;
    }

    private boolean handleTag(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(error("Only players can set the town tag."));
            return true;
        }

        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(error("You are not in a town!"));
            return true;
        }

        Town town = resident.getTown();
        if (!resident.isMayor() && !resident.isAssistant()) {
            player.sendMessage(error("You don't have permission to set the town tag!"));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(info("Usage: /town tag <4-char-tag>"));
            return true;
        }

        String tag = args[1];
        if (tag.length() > 4 || tag.length() < 1) {
            player.sendMessage(error("Tag must be 1-4 characters long."));
            return true;
        }

        town.setTag(tag);
        dataManager.saveAll();
        player.sendMessage(success("Town tag set to '" + tag + "'."));
        return true;
    }
    
    private boolean handleClaim(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(error("Only players can claim chunks."));
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(error("You are not in a town!"));
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!resident.isMayor() && !resident.isAssistant()) {
            player.sendMessage(error("You don't have permission to claim chunks!"));
            return true;
        }
        
        int chunkX = player.getLocation().getBlockX() >> 4;
        int chunkZ = player.getLocation().getBlockZ() >> 4;

        // Prevent claiming WorldGuard regions
        if (plugin.isLocationInWorldGuardRegion(player.getLocation())) {
            player.sendMessage(error("This area is protected by WorldGuard and cannot be claimed."));
            return true;
        }
        
        if (dataManager.isClaimed(player.getWorld().getName(), chunkX, chunkZ)) {
            player.sendMessage(error("This chunk is already claimed!"));
            return true;
        }
        // Enforce per-player claim limit within their town
        int maxPerPlayer = plugin.getConfig().getInt("town.max-claims-per-player", 12);
        long playerOwned = town.getClaims().stream().filter(c -> player.getUniqueId().equals(c.getOwner())).count();
        if (playerOwned >= maxPerPlayer) {
            player.sendMessage(error("You have reached your personal claim limit in this town (" + maxPerPlayer + ")!"));
            return true;
        }
            // Prevent disconnected claims: must be adjacent to an existing claim
            boolean isAdjacent = false;
            for (Claim c : town.getClaims()) {
                if (c.getWorldName().equals(player.getWorld().getName())) {
                    if ((Math.abs(c.getX() - chunkX) == 1 && c.getZ() == chunkZ) ||
                        (Math.abs(c.getZ() - chunkZ) == 1 && c.getX() == chunkX)) {
                        isAdjacent = true;
                        break;
                    }
                }
            }
            if (town.getClaimCount() > 0 && !isAdjacent) {
                player.sendMessage(error("You can only claim chunks adjacent to your town's existing claims!"));
                return true;
            }
        
        int maxClaims = plugin.getConfig().getInt("town.max-claims", 100);
        if (town.getClaimCount() >= maxClaims) {
            player.sendMessage(error("Your town has reached the maximum number of claims!"));
            return true;
        }
        
        double cost = plugin.getConfig().getDouble("town.claim-cost", 100.0);
        if (plugin.getEconomy() != null && !town.withdraw(cost)) {
            player.sendMessage(error("Your town doesn't have enough money! Need: " + cost));
            return true;
        }
        
        Claim claim = new Claim(player.getLocation().getChunk(), town, player.getUniqueId());
        dataManager.addClaim(claim);
        town.addClaim(claim);
        
    player.sendMessage(success("Chunk claimed for " + town.getName() + "!"));
        
        return true;
    }

    private boolean handleView(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(error("Only players can view claim borders."));
            return true;
        }

        Player player = (Player) sender;
        Town town;
        if (args.length >= 2) {
            town = dataManager.getTown(args[1]);
            if (town == null) {
                player.sendMessage(error("Town not found!"));
                return true;
            }
        } else {
            Resident resident = dataManager.getResident(player.getUniqueId());
            if (resident == null || !resident.hasTown()) {
                player.sendMessage(error("You are not in a town!"));
                return true;
            }
            town = resident.getTown();
        }

        java.util.Set<Claim> claims = town.getClaims();
        if (claims.isEmpty()) {
            player.sendMessage(info("This town has no claims."));
            return true;
        }

        int duration = plugin.getConfig().getInt("town.view-duration-seconds", 15);
        if (plugin.getParticleManager() != null) {
            plugin.getParticleManager().showClaimBorders(player, claims, duration);
            player.sendMessage(success("Showing claim borders for " + town.getName() + " for " + duration + "s."));
        } else {
            player.sendMessage(info("Particle effects are disabled on this server."));
        }

        return true;
    }
    
    private boolean handleUnclaim(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(error("Only players can unclaim chunks."));
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(error("You are not in a town!"));
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!resident.isMayor() && !resident.isAssistant()) {
            player.sendMessage(error("You don't have permission to unclaim chunks!"));
            return true;
        }
        
        Claim claim = dataManager.getClaim(player.getLocation());
        if (claim == null || !claim.getTown().equals(town)) {
            player.sendMessage(error("This chunk is not claimed by your town!"));
            return true;
        }

        // Prevent unclaiming the town homeblock (chunk containing the spawn)
        if (town.getSpawn() != null) {
            int spawnChunkX = town.getSpawn().getBlockX() >> 4;
            int spawnChunkZ = town.getSpawn().getBlockZ() >> 4;
            if (claim.getX() == spawnChunkX && claim.getZ() == spawnChunkZ && claim.getWorldName().equals(town.getSpawn().getWorld().getName())) {
                player.sendMessage(error("You cannot unclaim the town homeblock. Move the spawn first with /town setspawn to another claimed chunk."));
                return true;
            }
        }

        // Prevent disconnecting the town's claims
        if (wouldDisconnect(town, claim)) {
            player.sendMessage(error("You cannot unclaim that chunk because it would disconnect your town's territory!"));
            return true;
        }

        dataManager.removeClaim(claim);
        town.removeClaim(claim);

        player.sendMessage(success("Chunk unclaimed!"));
        
        return true;
    }
    
    private boolean handleSpawn(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(error("Only players can teleport."));
            return true;
        }
        
        Player player = (Player) sender;
        
        Town town;
        if (args.length >= 2) {
            town = dataManager.getTown(args[1]);
            if (town == null) {
                player.sendMessage(error("Town not found!"));
                return true;
            }
        } else {
            Resident resident = dataManager.getResident(player.getUniqueId());
            if (resident == null || !resident.hasTown()) {
                player.sendMessage(error("You are not in a town!"));
                return true;
            }
            town = resident.getTown();
        }
        
        if (town.getSpawn() == null) {
            player.sendMessage(error("That town doesn't have a spawn set!"));
            return true;
        }

        double cost = plugin.getConfig().getDouble("town.teleport-cost", 200.0);
        int delaySeconds = plugin.getConfig().getInt("town.teleport-delay-seconds", 15);

        // If player is teleporting to their own town, make it free
        Resident caller = dataManager.getResident(player.getUniqueId());
        boolean isOwnTown = caller != null && caller.hasTown() && caller.getTown().equals(town);
        if (isOwnTown) {
            cost = 0.0;
        }

        // Check economy if cost > 0
        if (cost > 0.0 && plugin.getEconomy() != null && !plugin.getEconomy().has(player, cost)) {
            player.sendMessage(error("You need " + cost + " to teleport to that town!"));
            return true;
        }

        // Charge player now (no refund on cancel) if applicable
        if (cost > 0.0 && plugin.getEconomy() != null) {
            plugin.getEconomy().withdrawPlayer(player, cost);
        }

        // Start managed teleport session (handles bossbar, cancellation, refund)
        plugin.startTeleportSession(player, town, cost, delaySeconds);

        return true;
    }
    
    private boolean handleSetSpawn(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(error("Only players can set spawns."));
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(error("You are not in a town!"));
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!resident.isMayor() && !resident.isAssistant()) {
            player.sendMessage(error("You don't have permission to set the spawn!"));
            return true;
        }
        
        Claim claim = dataManager.getClaim(player.getLocation());
        if (claim == null || !claim.getTown().equals(town)) {
            player.sendMessage(error("You can only set spawn in your town's territory!"));
            return true;
        }
        
        town.setSpawn(player.getLocation());
        
        // Refresh particle effect for the spawn
        if (plugin.getParticleManager() != null) {
            plugin.getParticleManager().refreshParticleEffect(town);
        }
        
    player.sendMessage(success("Town spawn has been set!"));
        
        return true;
    }
    
    private boolean handleDeposit(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(error("Only players can deposit money."));
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(error("You are not in a town!"));
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(error("Usage: /town deposit <amount>"));
            return true;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(error("Invalid amount!"));
            return true;
        }
        
        if (amount <= 0) {
            player.sendMessage(error("Amount must be positive!"));
            return true;
        }
        
        if (plugin.getEconomy() == null || !plugin.getEconomy().has(player, amount)) {
            player.sendMessage(error("You don't have enough money!"));
            return true;
        }
        
        plugin.getEconomy().withdrawPlayer(player, amount);
        resident.getTown().deposit(amount);
        
    player.sendMessage(success("Deposited " + amount + " to the town bank!"));
        
        return true;
    }
    
    private boolean handleWithdraw(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(error("Only players can withdraw money."));
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(error("You are not in a town!"));
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!resident.isMayor()) {
            player.sendMessage(error("Only the mayor can withdraw money!"));
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(error("Usage: /town withdraw <amount>"));
            return true;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(error("Invalid amount!"));
            return true;
        }
        
        if (amount <= 0) {
            player.sendMessage(error("Amount must be positive!"));
            return true;
        }
        
        if (!town.withdraw(amount)) {
            player.sendMessage(error("The town doesn't have enough money!"));
            return true;
        }
        
        if (plugin.getEconomy() != null) {
            plugin.getEconomy().depositPlayer(player, amount);
        }
        
    player.sendMessage(success("Withdrew " + amount + " from the town bank!"));
        
        return true;
    }
    
    private boolean handleInfo(CommandSender sender, String[] args) {
        Town town;
        
        if (args.length >= 2) {
            town = dataManager.getTown(args[1]);
            if (town == null) {
                sender.sendMessage(error("Town not found!"));
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(error("Usage: /town info <town>"));
                return true;
            }
            Player player = (Player) sender;
            Resident resident = dataManager.getResident(player.getUniqueId());
            if (resident == null || !resident.hasTown()) {
                sender.sendMessage(error("You are not in a town!"));
                return true;
            }
            town = resident.getTown();
        }
        
        sender.sendMessage(pref("=== " + town.getName() + " ==="));
        Resident mayor = dataManager.getResident(town.getMayor());
        sender.sendMessage(info("Mayor: " + (mayor != null ? mayor.getName() : "Unknown")));
        sender.sendMessage(info("Residents: " + town.getResidentCount()));
        sender.sendMessage(info("Claims: " + town.getClaimCount()));
        sender.sendMessage(info("Bank: " + town.getBank()));
        if (town.hasNation()) {
            sender.sendMessage(info("Nation: " + town.getNation().getName()));
        }
        sender.sendMessage(info("Open: " + (town.isOpen() ? "Yes" : "No")));
        sender.sendMessage(info("PvP: " + (town.isPvp() ? "Enabled" : "Disabled")));
        
        // If sender is a player, also show the town board as a subtitle
        if (sender instanceof Player) {
            Player p = (Player) sender;
            String board = town.getBoard();
            if (board != null && !board.isEmpty()) {
                // fadeIn 10 ticks, stay 60 ticks, fadeOut 10 ticks
                p.sendTitle("", board, 10, 60, 10);
            }
        }

        return true;
    }
    
    private boolean handleList(CommandSender sender, String[] args) {
        Collection<Town> towns = dataManager.getTowns();
        
        if (towns.isEmpty()) {
            sender.sendMessage(info("No towns exist!"));
            return true;
        }

        sender.sendMessage(pref("=== Towns (" + towns.size() + ") ==="));
        for (Town town : towns) {
            sender.sendMessage(info("- " + town.getName() + " (" + town.getResidentCount() + " residents)"));
        }
        
        return true;
    }
    
    private boolean handleToggle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(error("Only players can toggle settings."));
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(error("You are not in a town!"));
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!resident.isMayor()) {
            player.sendMessage(error("Only the mayor can toggle settings!"));
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(error("Usage: /town toggle <pvp|explosions|mobs|open>"));
            return true;
        }
        
        switch (args[1].toLowerCase()) {
            case "pvp":
                town.setPvp(!town.isPvp());
                player.sendMessage(success("PvP " + (town.isPvp() ? "enabled" : "disabled") + "!"));
                break;
            case "explosions":
                town.setExplosions(!town.isExplosions());
                player.sendMessage(success("Explosions " + (town.isExplosions() ? "enabled" : "disabled") + "!"));
                break;
            case "mobs":
                town.setMobSpawning(!town.isMobSpawning());
                player.sendMessage(success("Mob spawning " + (town.isMobSpawning() ? "enabled" : "disabled") + "!"));
                break;
            case "open":
                town.setOpen(!town.isOpen());
                player.sendMessage(success("Town is now " + (town.isOpen() ? "open" : "closed") + "!"));
                break;
            default:
                player.sendMessage(error("Invalid option! Options: pvp, explosions, mobs, open"));
                break;
        }
        
        return true;
    }
    
    private boolean handleRank(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(error("Only players can manage ranks."));
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(error("You are not in a town!"));
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!resident.isMayor()) {
            player.sendMessage(error("Only the mayor can manage ranks!"));
            return true;
        }
        
        if (args.length < 3) {
            player.sendMessage(error("Usage: /town rank <player> <assistant|resident>"));
            return true;
        }
        
        Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(error("Player not found!"));
            return true;
        }
        
        Resident targetResident = dataManager.getResident(target.getUniqueId());
        if (targetResident == null || !targetResident.hasTown() || !targetResident.getTown().equals(town)) {
            player.sendMessage(error("That player is not in your town!"));
            return true;
        }
        
        String rankStr = args[2].toLowerCase();
        if (rankStr.equals("assistant")) {
            town.addAssistant(target.getUniqueId());
            targetResident.setRank(TownRank.ASSISTANT);
            player.sendMessage(success(target.getName() + " is now an assistant!"));
            target.sendMessage(success("You have been promoted to assistant!"));
        } else if (rankStr.equals("resident")) {
            town.removeAssistant(target.getUniqueId());
            targetResident.setRank(TownRank.RESIDENT);
            player.sendMessage(success(target.getName() + " is now a resident!"));
            target.sendMessage(info("You have been demoted to resident!"));
        } else {
            player.sendMessage(error("Invalid rank! Options: assistant, resident"));
        }
        
        return true;
    }
    
    private boolean handleBoard(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(error("Only players can set the board."));
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(error("You are not in a town!"));
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!resident.isMayor() && !resident.isAssistant()) {
            player.sendMessage(error("You don't have permission to set the board!"));
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(error("Usage: /town board <message>"));
            return true;
        }
        
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        town.setBoard(message);
    player.sendMessage(success("Town board updated!"));
        
        return true;
    }
    
    private boolean handleRename(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(error("Only players can rename towns."));
            return true;
        }
        
        Player player = (Player) sender;
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null || !resident.hasTown()) {
            player.sendMessage(error("You are not in a town!"));
            return true;
        }
        
        Town town = resident.getTown();
        
        if (!resident.isMayor()) {
            player.sendMessage(error("Only the mayor can rename the town!"));
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(error("Usage: /town rename <name>"));
            return true;
        }
        
        String newName = args[1];
        
        // Validate town name
        int minLength = plugin.getConfig().getInt("town.min-name-length", 3);
        int maxLength = plugin.getConfig().getInt("town.max-name-length", 32);
        
        if (newName.length() < minLength || newName.length() > maxLength) {
            player.sendMessage(error("Town name must be between " + minLength + " and " + maxLength + " characters."));
            return true;
        }
        
        if (!newName.matches("[a-zA-Z0-9_]+")) {
            player.sendMessage(error("Town name can only contain letters, numbers, and underscores."));
            return true;
        }
        
        if (dataManager.townExists(newName)) {
            player.sendMessage(error("A town with that name already exists!"));
            return true;
        }
        
        String oldName = town.getName();
        town.setName(newName);
        
        // Update the townsByName map
        dataManager.removeTown(town);
        dataManager.addTown(town);
        
    player.sendMessage(success("Town renamed from " + oldName + " to " + newName + "!"));
        
        return true;
    }
    
    private boolean handleAccept(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(error("Only players can accept invites."));
            return true;
        }
        Player player = (Player) sender;
        List<Invite> invites = dataManager.getInvites(player.getUniqueId());
        if (invites.isEmpty()) {
            player.sendMessage(error("You have no pending invites."));
            return true;
        }
        String townName = args.length > 1 ? args[1] : null;
        Invite invite = null;
        if (townName != null) {
            for (Invite i : invites) {
                if (i.getTownName().equalsIgnoreCase(townName)) {
                    invite = i;
                    break;
                }
            }
        } else {
            invite = invites.get(0);
        }
        if (invite == null) {
            player.sendMessage(error("No invite found for that town."));
            return true;
        }
        Town town = dataManager.getTown(invite.getTownName());
        if (town == null) {
            player.sendMessage(error("That town no longer exists."));
            dataManager.removeInvite(player.getUniqueId(), invite);
            return true;
        }
        Resident resident = dataManager.getOrCreateResident(player.getUniqueId(), player.getName());
        resident.addTown(town, TownRank.RESIDENT, true);
        dataManager.removeInvite(player.getUniqueId(), invite);
    player.sendMessage(success("You have joined " + town.getName() + "!"));
        return true;
    }

    private boolean handleDeny(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(error("Only players can deny invites."));
            return true;
        }
        Player player = (Player) sender;
        List<Invite> invites = dataManager.getInvites(player.getUniqueId());
        if (invites.isEmpty()) {
            player.sendMessage(error("You have no pending invites."));
            return true;
        }
        String townName = args.length > 1 ? args[1] : null;
        Invite invite = null;
        if (townName != null) {
            for (Invite i : invites) {
                if (i.getTownName().equalsIgnoreCase(townName)) {
                    invite = i;
                    break;
                }
            }
        } else {
            invite = invites.get(0);
        }
        if (invite == null) {
            player.sendMessage(error("No invite found for that town."));
            return true;
        }
        dataManager.removeInvite(player.getUniqueId(), invite);
    player.sendMessage(info("You have denied the invite to " + invite.getTownName() + "."));
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
    sender.sendMessage(plugin.pref("=== Town Commands ==="));
    sender.sendMessage(plugin.info("/town create <name> - Create a town"));
    sender.sendMessage(plugin.info("/town delete - Delete your town"));
    sender.sendMessage(plugin.info("/town invite <player> - Invite a player"));
    sender.sendMessage(plugin.info("/town kick <player> - Kick a player"));
    sender.sendMessage(plugin.info("/town leave - Leave your town"));
    sender.sendMessage(plugin.info("/town claim - Claim a chunk"));
    sender.sendMessage(plugin.info("/town unclaim - Unclaim a chunk"));
    sender.sendMessage(plugin.info("/town spawn [town] - Teleport to town spawn"));
    sender.sendMessage(plugin.info("/town setspawn - Set town spawn"));
    sender.sendMessage(plugin.info("/town deposit <amount> - Deposit money"));
    sender.sendMessage(plugin.info("/town withdraw <amount> - Withdraw money"));
    sender.sendMessage(plugin.info("/town info [town] - View town info"));
    sender.sendMessage(plugin.info("/town list - List all towns"));
    sender.sendMessage(plugin.info("/town toggle <option> - Toggle settings"));
    sender.sendMessage(plugin.info("/town rank <player> <rank> - Set player rank"));
    sender.sendMessage(plugin.info("/town board <message> - Set town board"));
    sender.sendMessage(plugin.info("/town rename <name> - Rename town"));
    sender.sendMessage(plugin.info("/town accept <town> - Accept a town invite"));
    sender.sendMessage(plugin.info("/town deny <town> - Deny a town invite"));
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create", "delete", "invite", "kick", "leave", "claim", "unclaim",
                    "spawn", "setspawn", "deposit", "withdraw", "info", "list", "toggle", "rank", "board", "rename", "accept", "deny")
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
