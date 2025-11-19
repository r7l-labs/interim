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
import org.r7l.interim.model.Town;

import java.util.List;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;
 

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
                // If this war has a specific target town, only count kills inside that town
                java.util.UUID targetTownId = war.getTargetTown();
                if (targetTownId != null) {
                    Town targetTown = plugin.getDataManager().getTown(targetTownId);
                    if (targetTown == null) continue;
                    // Use victim location to determine where the kill occurred
                    var loc = victim.getLocation();
                    var claim = targetTown.getClaimAt(loc);
                    if (claim == null) {
                        // Kill did not occur inside the target town; ignore
                        continue;
                    }
                }

                // Record kill/death and award points
                war.addKill(killer.getUniqueId());
                war.addDeath(victim.getUniqueId());

                final int pointsPerKill = 10; // as shown in UI
                if (war.getAttackerNation().equals(killerNationId)) {
                    war.addAttackerPoints(pointsPerKill);
                } else if (war.getDefenderNation().equals(killerNationId)) {
                    war.addDefenderPoints(pointsPerKill);
                }

                // Check for auto-end threshold
                int threshold = plugin.getConfig().getInt("war.points-to-win", 500);
                boolean ended = false;
                String winnerName = null;
                if (war.getAttackerPoints() >= threshold) {
                    // attacker wins
                    war.setActive(false);
                    war.setEndTime(System.currentTimeMillis());
                    ended = true;
                    winnerName = plugin.getDataManager().getNation(war.getAttackerNation()).getName();
                } else if (war.getDefenderPoints() >= threshold) {
                    war.setActive(false);
                    war.setEndTime(System.currentTimeMillis());
                    ended = true;
                    winnerName = plugin.getDataManager().getNation(war.getDefenderNation()).getName();
                }

                // Persist immediately
                data.saveAll();

                // Notify killer and optionally the nations
                killer.sendMessage(ChatColor.GREEN + "+" + pointsPerKill + " war points! (Kill registered)");
                String announcement = ChatColor.GRAY + "[War] " + ChatColor.YELLOW + killer.getName() + ChatColor.GRAY + " scored a kill against " + ChatColor.RED + victim.getName() + ChatColor.GRAY + " (" + war.getWarGoal() + ")";
                Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(announcement));

                if (ended) {
                    String endAnn = ChatColor.DARK_GREEN + "⚔ WAR ENDED! ⚔\n" + ChatColor.GOLD + winnerName + ChatColor.GRAY + " has won the war between " + ChatColor.RED + plugin.getDataManager().getNation(war.getAttackerNation()).getName() + ChatColor.GRAY + " and " + ChatColor.RED + plugin.getDataManager().getNation(war.getDefenderNation()).getName();
                    Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(endAnn));

                    // Send Discord webhook if configured
                    String webhook = plugin.getConfig().getString("war.discord-webhook", "");
                    if (webhook != null && !webhook.isBlank()) {
                        final String content = "{\"content\":\"War ended: " + escapeJson(endAnn) + "\"}";
                        sendDiscordWebhookAsync(webhook, content);
                    }
                }

                break;
            }
        }
    }

    private void sendDiscordWebhookAsync(String webhookUrl, String jsonPayload) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                    .build();

                client.send(request, HttpResponse.BodyHandlers.discarding());
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to POST war webhook: " + e.getMessage());
            }
        });
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
