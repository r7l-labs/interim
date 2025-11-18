package org.r7l.interim.model;

import java.util.UUID;

public class Invite {
    private final UUID townUuid;
    private final String townName;
    private final UUID senderUuid;
    private final String senderName;
    private final long timestamp;
    private final long expiresAt;
    
    public Invite(UUID townUuid, String townName, UUID senderUuid, String senderName, long duration) {
        this.townUuid = townUuid;
        this.townName = townName;
        this.senderUuid = senderUuid;
        this.senderName = senderName;
        this.timestamp = System.currentTimeMillis();
        this.expiresAt = timestamp + duration;
    }
    
    public UUID getTownUuid() {
        return townUuid;
    }
    
    public String getTownName() {
        return townName;
    }
    
    public UUID getSenderUuid() {
        return senderUuid;
    }
    
    public String getSenderName() {
        return senderName;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public long getExpiresAt() {
        return expiresAt;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
    
    public long getTimeRemaining() {
        return Math.max(0, expiresAt - System.currentTimeMillis());
    }
}
