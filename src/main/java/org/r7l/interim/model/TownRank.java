package org.r7l.interim.model;

public enum TownRank {
    MAYOR("Mayor", 3),
    ASSISTANT("Assistant", 2),
    RESIDENT("Resident", 1);
    
    private final String displayName;
    private final int level;
    
    TownRank(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getLevel() {
        return level;
    }
    
    public boolean isAtLeast(TownRank rank) {
        return this.level >= rank.level;
    }
}
