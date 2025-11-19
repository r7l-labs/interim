package org.r7l.interim.model;

import java.util.*;

public class Nation {
    private final UUID uuid;
    private String name;
    private final UUID capital; // UUID of capital town
    private final Set<UUID> towns; // UUIDs of member towns
    private final Set<UUID> allies;
    private final Set<UUID> enemies;
    private double bank;
    private long founded;
    private String board;
    private NationColor color;
    private String tag;
    
    public Nation(String name, UUID capital) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.capital = capital;
        this.towns = new HashSet<>();
        this.allies = new HashSet<>();
        this.enemies = new HashSet<>();
        this.bank = 0.0;
        this.founded = System.currentTimeMillis();
        this.board = "";
        this.color = NationColor.WHITE;
        this.tag = "";
        
        // Capital is automatically a member
        this.towns.add(capital);
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public UUID getCapital() {
        return capital;
    }
    
    public Set<UUID> getTowns() {
        return new HashSet<>(towns);
    }
    
    public void addTown(UUID town) {
        towns.add(town);
    }
    
    public void removeTown(UUID town) {
        towns.remove(town);
    }
    
    public boolean hasTown(UUID town) {
        return towns.contains(town);
    }
    
    public int getTownCount() {
        return towns.size();
    }
    
    public Set<UUID> getAllies() {
        return new HashSet<>(allies);
    }
    
    public void addAlly(UUID nation) {
        allies.add(nation);
        enemies.remove(nation);
    }
    
    public void removeAlly(UUID nation) {
        allies.remove(nation);
    }
    
    public boolean isAlly(UUID nation) {
        return allies.contains(nation);
    }
    
    public Set<UUID> getEnemies() {
        return new HashSet<>(enemies);
    }
    
    public void addEnemy(UUID nation) {
        enemies.add(nation);
        allies.remove(nation);
    }
    
    public void removeEnemy(UUID nation) {
        enemies.remove(nation);
    }
    
    public boolean isEnemy(UUID nation) {
        return enemies.contains(nation);
    }
    
    public double getBank() {
        return bank;
    }
    
    public void setBank(double bank) {
        this.bank = bank;
    }
    
    public void deposit(double amount) {
        this.bank += amount;
    }
    
    public boolean withdraw(double amount) {
        if (bank >= amount) {
            bank -= amount;
            return true;
        }
        return false;
    }
    
    public long getFounded() {
        return founded;
    }
    
    public String getBoard() {
        return board;
    }
    
    public void setBoard(String board) {
        this.board = board;
    }

    public String getTag() {
        if (tag != null && !tag.isEmpty()) return tag;
        // Generate tag from first 4 characters of name
        return name.length() <= 4 ? name : name.substring(0, 4);
    }

    public void setTag(String tag) {
        this.tag = tag == null ? "" : tag;
    }
    
    public NationColor getColor() {
        return color;
    }
    
    public void setColor(NationColor color) {
        this.color = color;
    }
    
    
    public int getAllyCount() {
        return allies.size();
    }
    
    public int getEnemyCount() {
        return enemies.size();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nation nation = (Nation) o;
        return uuid.equals(nation.uuid);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
