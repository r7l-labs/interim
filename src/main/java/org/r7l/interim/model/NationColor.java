package org.r7l.interim.model;

import org.bukkit.ChatColor;

public enum NationColor {
    WHITE(ChatColor.WHITE),
    RED(ChatColor.RED),
    BLUE(ChatColor.BLUE),
    GREEN(ChatColor.GREEN),
    YELLOW(ChatColor.YELLOW),
    GOLD(ChatColor.GOLD),
    AQUA(ChatColor.AQUA),
    LIGHT_PURPLE(ChatColor.LIGHT_PURPLE),
    DARK_GREEN(ChatColor.DARK_GREEN),
    DARK_AQUA(ChatColor.DARK_AQUA),
    DARK_PURPLE(ChatColor.DARK_PURPLE);
    
    private final ChatColor chatColor;
    
    NationColor(ChatColor chatColor) {
        this.chatColor = chatColor;
    }
    
    public ChatColor getChatColor() {
        return chatColor;
    }
}
