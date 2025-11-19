package org.r7l.interim.integration;

import org.r7l.interim.Interim;

/**
 * A safe, reflection-friendly placeholder integration stub.
 *
 * This class will detect if PlaceholderAPI is present at runtime. Full
 * compile-time integration was avoided so the plugin can be built without
 * PlaceholderAPI on the classpath. If PlaceholderAPI is present, we currently
 * log that we detected it; a runtime registration could be added later.
 */
public class PlaceholderIntegration {
    private final Interim plugin;

    public PlaceholderIntegration(Interim plugin) {
        this.plugin = plugin;
    }

    public void registerIfPresent() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            plugin.getLogger().info("PlaceholderAPI detected — runtime expansion registration is not implemented in this build.");
        } catch (ClassNotFoundException e) {
            // PlaceholderAPI not present; nothing to do
        }
    }
}
