package org.r7l.interim.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.r7l.interim.Interim;
import org.r7l.interim.gui.ClaimMapGUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Command to open the interactive claim map GUI
 */
public class MapCommand implements CommandExecutor, TabCompleter {
    
    private final Interim plugin;
    
    public MapCommand(Interim plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can view the claim map!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Open the GUI
        ClaimMapGUI gui = new ClaimMapGUI(plugin, player);
        gui.open();
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
