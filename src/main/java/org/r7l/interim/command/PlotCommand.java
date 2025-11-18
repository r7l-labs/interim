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

public class PlotCommand implements CommandExecutor, TabCompleter {
    private final Interim plugin;
    private final DataManager dataManager;
    
    public PlotCommand(Interim plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use plot commands.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            return handleInfo(player);
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "info":
            case "i":
                return handleInfo(player);
            case "accept":
                return handleAccept(player, args);
            case "deny":
                return handleDeny(player, args);
            case "invites":
                return handleInvites(player);
            default:
                player.sendMessage(ChatColor.RED + "Unknown command. Use /plot info, /plot accept, /plot deny, or /plot invites");
                return true;
        }
    }
    
    private boolean handleInfo(Player player) {
        Claim claim = dataManager.getClaim(player.getLocation());
        
        if (claim == null) {
            player.sendMessage(ChatColor.YELLOW + "This chunk is wilderness!");
            return true;
        }
        
        Town town = claim.getTown();
        player.sendMessage(ChatColor.GOLD + "=== Plot Info ===");
        player.sendMessage(ChatColor.YELLOW + "Town: " + town.getName());
        player.sendMessage(ChatColor.YELLOW + "Type: " + claim.getType().getDisplayName());
        player.sendMessage(ChatColor.YELLOW + "Coordinates: " + claim.getX() + ", " + claim.getZ());
        
        if (town.hasNation()) {
            player.sendMessage(ChatColor.YELLOW + "Nation: " + town.getNation().getName());
        }
        
        return true;
    }
    
    private boolean handleAccept(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /plot accept <town> or /town accept <town>");
            return true;
        }
        
        String townName = args[1];
        List<Invite> invites = dataManager.getInvites(player.getUniqueId());
        
        Invite invite = null;
        for (Invite i : invites) {
            if (i.getTownName().equalsIgnoreCase(townName)) {
                invite = i;
                break;
            }
        }
        
        if (invite == null) {
            player.sendMessage(ChatColor.RED + "You don't have an invite from that town!");
            return true;
        }
        
        Town town = dataManager.getTown(invite.getTownUuid());
        if (town == null) {
            player.sendMessage(ChatColor.RED + "That town no longer exists!");
            dataManager.removeInvite(player.getUniqueId(), invite);
            return true;
        }
        
        Resident resident = dataManager.getOrCreateResident(player.getUniqueId(), player.getName());
        if (resident.hasTown()) {
            player.sendMessage(ChatColor.RED + "You are already in a town!");
            return true;
        }
        
        // Accept invite
        town.addResident(player.getUniqueId());
        resident.setTown(town);
        resident.setRank(TownRank.RESIDENT);
        dataManager.removeInvite(player.getUniqueId(), invite);
        
        player.sendMessage(ChatColor.GREEN + "You have joined " + town.getName() + "!");
        
        // Notify town members
        for (UUID memberUuid : town.getResidents()) {
            Player member = plugin.getServer().getPlayer(memberUuid);
            if (member != null && member.isOnline()) {
                member.sendMessage(ChatColor.GREEN + player.getName() + " has joined the town!");
            }
        }
        
        return true;
    }
    
    private boolean handleDeny(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /plot deny <town> or /town deny <town>");
            return true;
        }
        
        String townName = args[1];
        List<Invite> invites = dataManager.getInvites(player.getUniqueId());
        
        Invite invite = null;
        for (Invite i : invites) {
            if (i.getTownName().equalsIgnoreCase(townName)) {
                invite = i;
                break;
            }
        }
        
        if (invite == null) {
            player.sendMessage(ChatColor.RED + "You don't have an invite from that town!");
            return true;
        }
        
        dataManager.removeInvite(player.getUniqueId(), invite);
        player.sendMessage(ChatColor.YELLOW + "You have denied the invite from " + townName + "!");
        
        return true;
    }
    
    private boolean handleInvites(Player player) {
        List<Invite> invites = dataManager.getInvites(player.getUniqueId());
        
        if (invites.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "You have no pending invites!");
            return true;
        }
        
        player.sendMessage(ChatColor.GOLD + "=== Pending Invites ===");
        for (Invite invite : invites) {
            long secondsRemaining = invite.getTimeRemaining() / 1000;
            player.sendMessage(ChatColor.YELLOW + "- " + invite.getTownName() + 
                    " (from " + invite.getSenderName() + ") - " + secondsRemaining + "s remaining");
        }
        player.sendMessage(ChatColor.YELLOW + "Use /town accept <town> or /town deny <town>");
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("info", "accept", "deny", "invites")
                    .stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }
}
