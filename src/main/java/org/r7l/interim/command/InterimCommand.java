package org.r7l.interim.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.r7l.interim.Interim;
import org.r7l.interim.gui.TownNationOverviewGUI;

public class InterimCommand implements CommandExecutor {
    private final Interim plugin;
    public InterimCommand(Interim plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        Player player = (Player) sender;
        TownNationOverviewGUI gui = new TownNationOverviewGUI(plugin, player);
        gui.open();
        return true;
    }
}
