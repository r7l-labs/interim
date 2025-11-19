    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            Claim fromClaim = dataManager.getClaim(event.getBlock().getLocation());
            Claim toClaim = dataManager.getClaim(block.getLocation());
            if (toClaim != null && (fromClaim == null || !fromClaim.getTown().equals(toClaim.getTown()))) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            Claim fromClaim = dataManager.getClaim(event.getBlock().getLocation());
            Claim toClaim = dataManager.getClaim(block.getLocation());
            if (toClaim != null && (fromClaim == null || !fromClaim.getTown().equals(toClaim.getTown()))) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        Claim claim = dataManager.getClaim(block.getLocation());
        if (claim != null) {
            // Prevent physics from outside claims affecting inside
            // (e.g., sand/gravel falling, redstone, etc.)
            // For now, just block physics if block is at border
            // TODO: Refine for specific block types if needed
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        Block block = event.getBlock();
        Claim claim = dataManager.getClaim(block.getLocation());
        if (claim != null && event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        Block block = event.getBlock();
        Claim claim = dataManager.getClaim(block.getLocation());
        if (claim != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockRedstone(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        Claim claim = dataManager.getClaim(block.getLocation());
        if (claim != null) {
            // Prevent redstone from crossing claim borders
            event.setNewCurrent(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        // Prevent mob griefing (e.g., Enderman, Wither, etc.)
        Block block = event.getBlock();
        Claim claim = dataManager.getClaim(block.getLocation());
        if (claim != null) {
            event.setCancelled(true);
        }
    }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        // Prevent water/lava flowing from outside into a claim
        Block toBlock = event.getToBlock();
        Block fromBlock = event.getBlock();
        Claim toClaim = dataManager.getClaim(toBlock.getLocation());
        Claim fromClaim = dataManager.getClaim(fromBlock.getLocation());
        // If liquid is flowing from wilderness or another claim into a protected claim, block it
        if (toClaim != null && (fromClaim == null || !fromClaim.getTown().equals(toClaim.getTown()))) {
            event.setCancelled(true);
        }
    }


package org.r7l.interim.listener;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.hanging.*;
import org.r7l.interim.Interim;
import org.r7l.interim.model.*;
import org.r7l.interim.storage.DataManager;

import java.util.List;

public class ProtectionListener implements Listener {
    private final Interim plugin;
    private final DataManager dataManager;

    public ProtectionListener(Interim plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            Claim fromClaim = dataManager.getClaim(event.getBlock().getLocation());
            Claim toClaim = dataManager.getClaim(block.getLocation());
            if (toClaim != null && (fromClaim == null || !fromClaim.getTown().equals(toClaim.getTown()))) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            Claim fromClaim = dataManager.getClaim(event.getBlock().getLocation());
            Claim toClaim = dataManager.getClaim(block.getLocation());
            if (toClaim != null && (fromClaim == null || !fromClaim.getTown().equals(toClaim.getTown()))) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        Claim claim = dataManager.getClaim(block.getLocation());
        if (claim != null) {
            // Prevent physics from outside claims affecting inside
            // (e.g., sand/gravel falling, redstone, etc.)
            // For now, just block physics if block is at border
            // TODO: Refine for specific block types if needed
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        Block block = event.getBlock();
        Claim claim = dataManager.getClaim(block.getLocation());
        if (claim != null && event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        Block block = event.getBlock();
        Claim claim = dataManager.getClaim(block.getLocation());
        if (claim != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockRedstone(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        Claim claim = dataManager.getClaim(block.getLocation());
        if (claim != null) {
            // Prevent redstone from crossing claim borders
            event.setNewCurrent(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        // Prevent mob griefing (e.g., Enderman, Wither, etc.)
        Block block = event.getBlock();
        Claim claim = dataManager.getClaim(block.getLocation());
        if (claim != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        // Prevent water/lava flowing from outside into a claim
        Block toBlock = event.getToBlock();
        Block fromBlock = event.getBlock();
        Claim toClaim = dataManager.getClaim(toBlock.getLocation());
        Claim fromClaim = dataManager.getClaim(fromBlock.getLocation());
        // If liquid is flowing from wilderness or another claim into a protected claim, block it
        if (toClaim != null && (fromClaim == null || !fromClaim.getTown().equals(toClaim.getTown()))) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        if (player.hasPermission("interim.admin")) {
            return;
        }
        
        Claim claim = dataManager.getClaim(block.getLocation());
        
        if (claim == null) {
            // Wilderness - allow
            return;
        }
        
        if (!canBuild(player, claim)) {
            event.setCancelled(true);
            player.sendMessage("§9§l[Interim] §7You cannot break blocks in " + claim.getTown().getName() + "!");
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        if (player.hasPermission("interim.admin")) {
            return;
        }
        
        Claim claim = dataManager.getClaim(block.getLocation());
        
        if (claim == null) {
            // Wilderness - allow
            return;
        }
        
        if (!canBuild(player, claim)) {
            event.setCancelled(true);
            player.sendMessage("§9§l[Interim] §7You cannot place blocks in " + claim.getTown().getName() + "!");
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        
        if (event.getClickedBlock() == null) {
            return;
        }
        
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        
        if (player.hasPermission("interim.admin")) {
            return;
        }
        
        // Always allow doors and trapdoors to be opened by anyone
        String type = block.getType().name();
        if (type.contains("DOOR") || type.contains("TRAPDOOR")) {
            return;
        }

        // Check if it's a protected block (container, etc.)
        if (!isProtectedBlock(block)) {
            return;
        }

        Claim claim = dataManager.getClaim(block.getLocation());

        if (claim == null) {
            // Wilderness - allow
            return;
        }

        // TODO: Add war logic for 1.0.0 (allow block/mob interaction if at war)
        if (!canInteract(player, claim)) {
            event.setCancelled(true);
            player.sendMessage("§9§l[Interim] §7You cannot interact with that in " + claim.getTown().getName() + "!");
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();
        
        Player attacker = null;
        if (damager instanceof Player) {
            attacker = (Player) damager;
        } else if (damager.getType() == EntityType.ARROW && damager instanceof org.bukkit.entity.Projectile) {
            org.bukkit.entity.Projectile projectile = (org.bukkit.entity.Projectile) damager;
            if (projectile.getShooter() instanceof Player) {
                attacker = (Player) projectile.getShooter();
            }
        }
        
        if (attacker == null) {
            return;
        }
        
        if (attacker.hasPermission("interim.admin")) {
            return;
        }
        
        // PvP protection
        if (damaged instanceof Player) {
            Player victim = (Player) damaged;
            
            Claim claim = dataManager.getClaim(victim.getLocation());
            
            if (claim == null) {
                // Wilderness - check config
                if (!plugin.getConfig().getBoolean("protection.wilderness-pvp", true)) {
                    event.setCancelled(true);
                    attacker.sendMessage("§9§l[Interim] §7PvP is disabled in the wilderness!");
                }
                return;
            }
            
            Town town = claim.getTown();
            
            // Check if PvP is enabled in the town
            if (!town.isPvp()) {
                event.setCancelled(true);
                attacker.sendMessage("§9§l[Interim] §7PvP is disabled in " + town.getName() + "!");
                return;
            }
            
            // Check nation relations
            Resident attackerResident = dataManager.getResident(attacker.getUniqueId());
            Resident victimResident = dataManager.getResident(victim.getUniqueId());
            
            if (attackerResident != null && victimResident != null) {
                if (attackerResident.hasTown() && victimResident.hasTown()) {
                    Town attackerTown = attackerResident.getTown();
                    Town victimTown = victimResident.getTown();
                    
                    // Same town
                    if (attackerTown.equals(victimTown)) {
                        event.setCancelled(true);
                        attacker.sendMessage("§9§l[Interim] §7You cannot attack your town members!");
                        return;
                    }
                    
                    // Allied nations
                    if (attackerTown.hasNation() && victimTown.hasNation()) {
                        Nation attackerNation = attackerTown.getNation();
                        Nation victimNation = victimTown.getNation();
                        
                        if (attackerNation.equals(victimNation)) {
                            event.setCancelled(true);
                            attacker.sendMessage("§9§l[Interim] §7You cannot attack your nation members!");
                            return;
                        }

                        if (attackerNation.isAlly(victimNation.getUuid())) {
                            event.setCancelled(true);
                            attacker.sendMessage("§9§l[Interim] §7You cannot attack your allies!");
                            return;
                        }
                    }
                }
            }
        }
        
        // Entity damage protection (item frames, armor stands, etc.)
        if (!(damaged instanceof Player)) {
            Claim claim = dataManager.getClaim(damaged.getLocation());
            
            if (claim != null && !canBuild(attacker, claim)) {
                event.setCancelled(true);
                attacker.sendMessage("§9§l[Interim] §7You cannot damage entities in " + claim.getTown().getName() + "!");
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getRemover();
        
        if (player.hasPermission("interim.admin")) {
            return;
        }
        
        Claim claim = dataManager.getClaim(event.getEntity().getLocation());
        
        if (claim != null && !canBuild(player, claim)) {
            event.setCancelled(true);
            player.sendMessage("§9§l[Interim] §7You cannot break that in " + claim.getTown().getName() + "!");
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHangingPlace(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        
        if (player.hasPermission("interim.admin")) {
            return;
        }
        
        Claim claim = dataManager.getClaim(event.getEntity().getLocation());
        
        if (claim != null && !canBuild(player, claim)) {
            event.setCancelled(true);
            player.sendMessage("§9§l[Interim] §7You cannot place that in " + claim.getTown().getName() + "!");
        }
    }
    

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        // Prevent explosions outside claims from affecting blocks inside claims
        event.blockList().removeIf(block -> {
            Claim claim = dataManager.getClaim(block.getLocation());
            if (claim == null) return false;
            // If explosion origin is outside claim, block damage
            Claim originClaim = dataManager.getClaim(event.getLocation());
            return !claim.getTown().isExplosions() || (originClaim == null || !originClaim.getTown().equals(claim.getTown()));
        });
    }
    

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        // Prevent block explosions outside claims from affecting blocks inside claims
        event.blockList().removeIf(block -> {
            Claim claim = dataManager.getClaim(block.getLocation());
            if (claim == null) return false;
            Claim originClaim = dataManager.getClaim(event.getBlock().getLocation());
            return !claim.getTown().isExplosions() || (originClaim == null || !originClaim.getTown().equals(claim.getTown()));
        });
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM ||
            event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            return;
        }
        
        Claim claim = dataManager.getClaim(event.getLocation());
        
        if (claim != null && !claim.getTown().isMobSpawning()) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Resident resident = dataManager.getOrCreateResident(player.getUniqueId(), player.getName());
        resident.setOnline(true);
        resident.setName(player.getName()); // Update name if changed
        
        // Show invites
        List<Invite> invites = dataManager.getInvites(player.getUniqueId());
        if (!invites.isEmpty()) {
            player.sendMessage("§9§l[Interim] §7You have " + invites.size() + " pending town invite(s)!");
            player.sendMessage("§9§l[Interim] §7Type /town invites to view them.");
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Resident resident = dataManager.getResident(player.getUniqueId());
        if (resident != null) {
            resident.setOnline(false);
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Only check if player moved to a different chunk
        if (event.getFrom().getChunk().equals(event.getTo().getChunk())) {
            return;
        }
        
        Player player = event.getPlayer();
        Claim fromClaim = dataManager.getClaim(event.getFrom());
        Claim toClaim = dataManager.getClaim(event.getTo());
        
        // Determine towns for from/to claims (null = wilderness)
        Town fromTown = (fromClaim == null) ? null : fromClaim.getTown();
        Town toTown = (toClaim == null) ? null : toClaim.getTown();

        // Only act if the player moved into a different town (or between wilderness and a town).
        // This avoids spamming when moving between multiple chunks that belong to the same town.
        if ((fromTown == null && toTown == null) || (fromTown != null && fromTown.equals(toTown))) {
            return;
        }

        // Show the town/wilderness as a title instead of chat to make it more prominent and avoid repetition
        if (toTown == null) {
            player.sendTitle("§7~ Wilderness ~", "", 5, 60, 10);
        } else {
            String title = "§a" + toTown.getName();
            String subtitle = "";
            if (toTown.hasNation()) {
                subtitle = "§e[" + toTown.getNation().getName() + "]";
            }
            player.sendTitle(title, subtitle, 5, 60, 10);
        }
    }
    
    private boolean canBuild(Player player, Claim claim) {
        Resident resident = dataManager.getResident(player.getUniqueId());
        
        if (resident == null) {
            return false;
        }
        
        Town town = claim.getTown();
        
        if (!resident.hasTown()) {
            return false;
        }
        
        // Must be in the same town
        if (!resident.getTown().equals(town)) {
            // Check if ally (optional - can be configured)
            if (resident.hasNation() && town.hasNation()) {
                Nation residentNation = resident.getNation();
                Nation townNation = town.getNation();
                return residentNation.equals(townNation);
            }
            return false;
        }
        
        return true;
    }
    
    private boolean canInteract(Player player, Claim claim) {
        // Same rules as canBuild for now
        return canBuild(player, claim);
    }
    
    private boolean isProtectedBlock(Block block) {
        String type = block.getType().name();
        
        // Containers
        if (type.contains("CHEST") || type.contains("BARREL") || type.contains("SHULKER_BOX")) {
            return true;
        }
        
        // Interactive blocks
        if (type.contains("DOOR") || type.contains("TRAPDOOR") || type.contains("GATE")) {
            return true;
        }
        
        if (type.contains("BUTTON") || type.contains("LEVER")) {
            return true;
        }
        
        if (type.equals("FURNACE") || type.equals("BLAST_FURNACE") || type.equals("SMOKER")) {
            return true;
        }
        
        if (type.contains("ANVIL") || type.equals("CRAFTING_TABLE") || type.equals("ENCHANTING_TABLE")) {
            return true;
        }
        
        if (type.equals("BREWING_STAND") || type.equals("BEACON")) {
            return true;
        }
        
        if (type.contains("HOPPER") || type.contains("DROPPER") || type.contains("DISPENSER")) {
            return true;
        }
        
        return false;
    }
}
