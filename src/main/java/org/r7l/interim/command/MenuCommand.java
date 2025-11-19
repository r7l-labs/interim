package org.r7l.interim.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.r7l.interim.Interim;
import org.r7l.interim.gui.MainMenuGUI;

/**
 * Command to open the main GUI menu
 */
public class MenuCommand implements CommandExecutor {
    
    private final Interim plugin;
    
    public MenuCommand(Interim plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        new MainMenuGUI(plugin, player).open();
        
        return true;
    }
}
