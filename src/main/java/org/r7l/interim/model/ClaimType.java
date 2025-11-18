package org.r7l.interim.model;

public enum ClaimType {
    NORMAL("Normal"),
    HOME("Home"),
    OUTPOST("Outpost"),
    ARENA("Arena");
    
    private final String displayName;
    
    ClaimType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
