package org.r7l.interim.model;

import java.util.*;

/**
 * Represents a war between two nations
 */
public class War {
    private final UUID uuid;
    private final UUID attackerNation;
    private final UUID defenderNation;
    private final long startTime;
    private long endTime;
    private boolean active;
    private int attackerPoints;
    private int defenderPoints;
    private String warGoal;
    private double wagerAmount;
    private final Map<UUID, Integer> playerKills; // Player UUID -> kill count
    private final Map<UUID, Integer> playerDeaths; // Player UUID -> death count
    private final Set<UUID> capturedTowns; // Towns captured during war
    
    public War(UUID attackerNation, UUID defenderNation, String warGoal, double wagerAmount) {
        this.uuid = UUID.randomUUID();
        this.attackerNation = attackerNation;
        this.defenderNation = defenderNation;
        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
        this.active = true;
        this.attackerPoints = 0;
        this.defenderPoints = 0;
        this.warGoal = warGoal;
        this.wagerAmount = wagerAmount;
        this.playerKills = new HashMap<>();
        this.playerDeaths = new HashMap<>();
        this.capturedTowns = new HashSet<>();
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    public UUID getAttackerNation() {
        return attackerNation;
    }
    
    public UUID getDefenderNation() {
        return defenderNation;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public long getEndTime() {
        return endTime;
    }
    
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public int getAttackerPoints() {
        return attackerPoints;
    }
    
    public void setAttackerPoints(int attackerPoints) {
        this.attackerPoints = attackerPoints;
    }
    
    public void addAttackerPoints(int points) {
        this.attackerPoints += points;
    }
    
    public int getDefenderPoints() {
        return defenderPoints;
    }
    
    public void setDefenderPoints(int defenderPoints) {
        this.defenderPoints = defenderPoints;
    }
    
    public void addDefenderPoints(int points) {
        this.defenderPoints += points;
    }
    
    public String getWarGoal() {
        return warGoal;
    }
    
    public void setWarGoal(String warGoal) {
        this.warGoal = warGoal;
    }
    
    public double getWagerAmount() {
        return wagerAmount;
    }
    
    public void setWagerAmount(double wagerAmount) {
        this.wagerAmount = wagerAmount;
    }
    
    public Map<UUID, Integer> getPlayerKills() {
        return new HashMap<>(playerKills);
    }
    
    public void addKill(UUID player) {
        playerKills.put(player, playerKills.getOrDefault(player, 0) + 1);
    }
    
    public int getKills(UUID player) {
        return playerKills.getOrDefault(player, 0);
    }
    
    public Map<UUID, Integer> getPlayerDeaths() {
        return new HashMap<>(playerDeaths);
    }
    
    public void addDeath(UUID player) {
        playerDeaths.put(player, playerDeaths.getOrDefault(player, 0) + 1);
    }
    
    public int getDeaths(UUID player) {
        return playerDeaths.getOrDefault(player, 0);
    }
    
    public Set<UUID> getCapturedTowns() {
        return new HashSet<>(capturedTowns);
    }
    
    public void addCapturedTown(UUID town) {
        capturedTowns.add(town);
    }
    
    public void removeCapturedTown(UUID town) {
        capturedTowns.remove(town);
    }
    
    public int getTotalKills() {
        return playerKills.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    public int getTotalDeaths() {
        return playerDeaths.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    public boolean isParticipant(UUID nationId) {
        return attackerNation.equals(nationId) || defenderNation.equals(nationId);
    }
    
    public UUID getOpponent(UUID nationId) {
        if (attackerNation.equals(nationId)) {
            return defenderNation;
        } else if (defenderNation.equals(nationId)) {
            return attackerNation;
        }
        return null;
    }
    
    public long getDuration() {
        if (endTime > 0) {
            return endTime - startTime;
        }
        return System.currentTimeMillis() - startTime;
    }
    
    public UUID getWinner() {
        if (active) {
            return null;
        }
        return attackerPoints > defenderPoints ? attackerNation : defenderNation;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        War war = (War) o;
        return uuid.equals(war.uuid);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
