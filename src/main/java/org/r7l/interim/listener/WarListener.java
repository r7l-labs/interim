package org.r7l.interim.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.r7l.interim.Interim;
import org.r7l.interim.model.Resident;
import org.r7l.interim.model.War;

import java.util.List;

/**
 * Listener that awards war points on player kills when nations are at war.
 */
public class WarListener implements Listener {
    private final Interim plugin;

    public WarListener(Interim plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null) return;
        if (killer.getUniqueId().equals(victim.getUniqueId())) return; // suicide

        // Ignore admins
        if (killer.hasPermission("interim.admin") || victim.hasPermission("interim.admin")) return;

        var data = plugin.getDataManager();

        Resident killerRes = data.getResident(killer.getUniqueId());
        Resident victimRes = data.getResident(victim.getUniqueId());

        if (killerRes == null || victimRes == null) return;
        if (!killerRes.hasNation() || !victimRes.hasNation()) return;

        var killerNationId = killerRes.getNation().getUuid();
        var victimNationId = victimRes.getNation().getUuid();
        if (killerNationId.equals(victimNationId)) return; // same nation

        // Find any active war involving both nations
        List<War> activeWars = data.getActiveWars();
        for (War war : activeWars) {
            if (war.isParticipant(killerNationId) && war.isParticipant(victimNationId)) {
                // Record kill/death and award points
                war.addKill(killer.getUniqueId());
                war.addDeath(victim.getUniqueId());

                final int pointsPerKill = 10; // as shown in UI
                if (war.getAttackerNation().equals(killerNationId)) {
                    war.addAttackerPoints(pointsPerKill);
                } else if (war.getDefenderNation().equals(killerNationId)) {
                    war.addDefenderPoints(pointsPerKill);
                }

                // Persist immediately
                data.saveAll();

                // Notify killer and optionally the nations
                killer.sendMessage(ChatColor.GREEN + "+" + pointsPerKill + " war points! (Kill registered)");
                String announcement = ChatColor.GRAY + "[War] " + ChatColor.YELLOW + killer.getName() + ChatColor.GRAY + " scored a kill against " + ChatColor.RED + victim.getName() + ChatColor.GRAY + " (" + war.getWarGoal() + ")";
                Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(announcement));
                break;
            }
        }
    }
}
