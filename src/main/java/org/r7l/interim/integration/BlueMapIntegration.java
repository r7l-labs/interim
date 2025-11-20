package org.r7l.interim.integration;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.ShapeMarker;
import de.bluecolored.bluemap.api.math.Color;
import de.bluecolored.bluemap.api.math.Shape;
import com.flowpowered.math.vector.Vector2d;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.r7l.interim.Interim;
import org.r7l.interim.model.Claim;
import org.r7l.interim.model.Nation;
import org.r7l.interim.model.NationColor;
import org.r7l.interim.model.Town;
import org.r7l.interim.storage.DataManager;

import java.util.logging.Logger;

public class BlueMapIntegration {
    private final Interim plugin;
    private final DataManager dataManager;
    private final Logger logger;
    private BlueMapAPI blueMapAPI;
    private boolean enabled = false;
    
    private static final String MARKER_SET_ID = "interim_claims";
    private static final String MARKER_SET_LABEL = "Interim Claims";
    
    public BlueMapIntegration(Interim plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
        this.logger = plugin.getLogger();
    }
    
    public void enable() {
        BlueMapAPI.onEnable(api -> {
            this.blueMapAPI = api;
            this.enabled = true;
            logger.info("BlueMap integration enabled!");
            updateAllMarkers();
        });
        
        BlueMapAPI.onDisable(api -> {
            this.blueMapAPI = null;
            this.enabled = false;
            logger.info("BlueMap integration disabled.");
        });
    }
    
    public boolean isEnabled() {
        return enabled && blueMapAPI != null;
    }
    
    public void updateAllMarkers() {
        if (!isEnabled()) return;
        
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                for (BlueMapMap map : blueMapAPI.getMaps()) {
                    updateMarkersForMap(map);
                }
                logger.info("Updated BlueMap markers for all towns and claims.");
            } catch (Exception e) {
                logger.warning("Error updating BlueMap markers: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    private void updateMarkersForMap(BlueMapMap map) {
    String worldName = map.getWorld().getSaveFolder().getFileName().toString();
    World world = Bukkit.getWorld(worldName);
        
        if (world == null) return;
        
        // Create or get marker set
        MarkerSet markerSet = map.getMarkerSets().computeIfAbsent(
            MARKER_SET_ID,
            key -> MarkerSet.builder()
                .label(MARKER_SET_LABEL)
                .toggleable(true)
                .defaultHidden(false)
                .build()
        );
        
        // Clear existing markers
        markerSet.getMarkers().clear();
        
        // Create one marker per town (grouping all their claims)
        for (Town town : dataManager.getTowns()) {
            // Filter claims for this world
            java.util.List<Claim> worldClaims = new java.util.ArrayList<>();
            for (Claim claim : town.getClaims()) {
                if (claim.getWorldName().equals(world.getName())) {
                    worldClaims.add(claim);
                }
            }
            
            if (worldClaims.isEmpty()) continue;
            
            Color townColor = getTownColor(town);
            String markerId = "town_" + town.getUuid().toString() + "_" + world.getName();
            
            // Create outline shape that only draws outer boundaries
            Shape townShape = createTerritoryOutline(worldClaims);
            
            // Create shape marker for the entire town
            ShapeMarker marker = ShapeMarker.builder()
                .label(town.getName())
                .detail(getTownDetail(town, worldClaims.size()))
                .shape(townShape, 0f) // y at 0
                .lineColor(townColor)
                .lineWidth(2)
                .fillColor(new Color(
                    townColor.getRed(),
                    townColor.getGreen(),
                    townColor.getBlue(),
                    0.2f // 20% opacity for fill
                ))
                .depthTestEnabled(false)
                .build();
            
            markerSet.put(markerId, marker);
        }
    }
    
    private Shape createTerritoryOutline(java.util.List<Claim> claims) {
        if (claims.isEmpty()) {
            return Shape.builder().build();
        }
        
        // Create a set for quick chunk lookup
        java.util.Set<String> claimSet = new java.util.HashSet<>();
        for (Claim claim : claims) {
            claimSet.add(claim.getX() + "," + claim.getZ());
        }
        
        // Collect all edge segments (only draw edges that border non-claimed chunks)
        java.util.List<EdgeSegment> edges = new java.util.ArrayList<>();
        
        for (Claim claim : claims) {
            int cx = claim.getX();
            int cz = claim.getZ();
            int x1 = cx * 16;
            int z1 = cz * 16;
            int x2 = x1 + 16;
            int z2 = z1 + 16;
            
            // Check each side - only add edge if neighbor is not claimed
            // North side (z-)
            if (!claimSet.contains((cx) + "," + (cz - 1))) {
                edges.add(new EdgeSegment(x1, z1, x2, z1));
            }
            // East side (x+)
            if (!claimSet.contains((cx + 1) + "," + (cz))) {
                edges.add(new EdgeSegment(x2, z1, x2, z2));
            }
            // South side (z+)
            if (!claimSet.contains((cx) + "," + (cz + 1))) {
                edges.add(new EdgeSegment(x2, z2, x1, z2));
            }
            // West side (x-)
            if (!claimSet.contains((cx - 1) + "," + (cz))) {
                edges.add(new EdgeSegment(x1, z2, x1, z1));
            }
        }
        
        if (edges.isEmpty()) {
            return Shape.builder().build();
        }
        
        // Trace the boundary by connecting edges in order
        Shape.Builder shapeBuilder = Shape.builder();
        java.util.Set<EdgeSegment> usedEdges = new java.util.HashSet<>();
        java.util.List<EdgeSegment> boundary = new java.util.ArrayList<>();
        
        // Start with first edge
        EdgeSegment current = edges.get(0);
        boundary.add(current);
        usedEdges.add(current);
        shapeBuilder.addPoint(new Vector2d(current.x1, current.z1));
        
        double currentX = current.x2;
        double currentZ = current.z2;
        
        // Keep tracing until we return to start or can't find next edge
        int maxIterations = edges.size() * 2; // Prevent infinite loops
        int iterations = 0;
        
        while (iterations < maxIterations && usedEdges.size() < edges.size()) {
            iterations++;
            
            // Find an unused edge that starts at current endpoint
            EdgeSegment next = null;
            for (EdgeSegment edge : edges) {
                if (usedEdges.contains(edge)) continue;
                
                // Check if this edge connects to current point
                double epsilon = 0.001;
                if (Math.abs(edge.x1 - currentX) < epsilon && Math.abs(edge.z1 - currentZ) < epsilon) {
                    next = edge;
                    break;
                }
                // Also check if edge is reversed (end connects to our current point)
                if (Math.abs(edge.x2 - currentX) < epsilon && Math.abs(edge.z2 - currentZ) < epsilon) {
                    // Create reversed edge
                    next = new EdgeSegment(edge.x2, edge.z2, edge.x1, edge.z1);
                    usedEdges.add(edge); // Mark original as used
                    break;
                }
            }
            
            if (next == null) {
                // Can't find connecting edge, might be multiple polygons
                // Start a new polygon with an unused edge if available
                for (EdgeSegment edge : edges) {
                    if (!usedEdges.contains(edge)) {
                        next = edge;
                        usedEdges.add(edge);
                        shapeBuilder.addPoint(new Vector2d(edge.x1, edge.z1));
                        currentX = edge.x2;
                        currentZ = edge.z2;
                        break;
                    }
                }
                if (next == null) break; // No more edges
                continue;
            }
            
            boundary.add(next);
            if (!usedEdges.contains(next)) {
                usedEdges.add(next);
            }
            shapeBuilder.addPoint(new Vector2d(next.x1, next.z1));
            
            currentX = next.x2;
            currentZ = next.z2;
        }
        
        return shapeBuilder.build();
    }
    
    // Helper class to represent an edge segment
    private static class EdgeSegment {
        final double x1, z1, x2, z2;
        
        EdgeSegment(double x1, double z1, double x2, double z2) {
            this.x1 = x1;
            this.z1 = z1;
            this.x2 = x2;
            this.z2 = z2;
        }
    }
    
    private Shape createChunkShape(int chunkX, int chunkZ) {
        int x1 = chunkX * 16;
        int z1 = chunkZ * 16;
        int x2 = x1 + 16;
        int z2 = z1 + 16;
        
        return Shape.builder()
            .addPoint(new Vector2d(x1, z1))
            .addPoint(new Vector2d(x2, z1))
            .addPoint(new Vector2d(x2, z2))
            .addPoint(new Vector2d(x1, z2))
            .build();
    }
    
    private Color getTownColor(Town town) {
        if (town.hasNation()) {
            // Towns with nations use nation color
            Nation nation = town.getNation();
            return getNationColor(nation);
        }
        
        // Nationless towns use their own town color
        return getTownColorFromEnum(town.getColor());
    }
    
    private Color getTownColorFromEnum(NationColor townColor) {
        switch (townColor) {
            case RED:
                return new Color(255, 0, 0, 1.0f);
            case BLUE:
                return new Color(0, 0, 255, 1.0f);
            case GREEN:
                return new Color(0, 255, 0, 1.0f);
            case YELLOW:
                return new Color(255, 255, 0, 1.0f);
            case GOLD:
                return new Color(255, 215, 0, 1.0f);
            case AQUA:
                return new Color(0, 255, 255, 1.0f);
            case LIGHT_PURPLE:
                return new Color(255, 0, 255, 1.0f);
            case DARK_GREEN:
                return new Color(0, 128, 0, 1.0f);
            case DARK_AQUA:
                return new Color(0, 139, 139, 1.0f);
            case DARK_PURPLE:
                return new Color(128, 0, 128, 1.0f);
            case WHITE:
            default:
                return new Color(255, 255, 255, 1.0f);
        }
    }
    
    private Color getNationColor(Nation nation) {
        switch (nation.getColor()) {
            case RED:
                return new Color(255, 0, 0, 1.0f);
            case BLUE:
                return new Color(0, 0, 255, 1.0f);
            case GREEN:
                return new Color(0, 255, 0, 1.0f);
            case YELLOW:
                return new Color(255, 255, 0, 1.0f);
            case GOLD:
                return new Color(255, 215, 0, 1.0f);
            case AQUA:
                return new Color(0, 255, 255, 1.0f);
            case LIGHT_PURPLE:
                return new Color(255, 0, 255, 1.0f);
            case DARK_GREEN:
                return new Color(0, 128, 0, 1.0f);
            case DARK_AQUA:
                return new Color(0, 139, 139, 1.0f);
            case DARK_PURPLE:
                return new Color(128, 0, 128, 1.0f);
            case WHITE:
            default:
                return new Color(255, 255, 255, 1.0f);
        }
    }
    
    private String getTownDetail(Town town, int claimCount) {
        StringBuilder detail = new StringBuilder();
        
        detail.append("<b>Town:</b> ").append(town.getName()).append("<br>");
        
        if (town.hasNation()) {
            detail.append("<b>Nation:</b> ").append(town.getNation().getName()).append("<br>");
        }
        
        detail.append("<b>Mayor:</b> ").append(Bukkit.getOfflinePlayer(town.getMayor()).getName()).append("<br>");
        detail.append("<b>Residents:</b> ").append(town.getResidentCount()).append("<br>");
        detail.append("<b>Claims:</b> ").append(claimCount).append(" chunks<br>");
        detail.append("<b>Bank:</b> $").append(String.format("%.2f", town.getBank())).append("<br>");
        
        return detail.toString();
    }
    
    public void updateTownMarkers(Town town) {
        if (!isEnabled()) return;
        
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::updateAllMarkers);
    }
    
    public void removeTownMarkers(Town town) {
        if (!isEnabled()) return;
        
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::updateAllMarkers);
    }
    
    public void disable() {
        if (!isEnabled()) return;
        
        try {
            // Clear all markers
            for (BlueMapMap map : blueMapAPI.getMaps()) {
                map.getMarkerSets().remove(MARKER_SET_ID);
            }
            logger.info("Cleared BlueMap markers.");
        } catch (Exception e) {
            logger.warning("Error clearing BlueMap markers: " + e.getMessage());
        }
        
        this.enabled = false;
        this.blueMapAPI = null;
    }
}
