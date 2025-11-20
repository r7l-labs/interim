package org.r7l.interim.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class UpdateChecker {
    
    private final Plugin plugin;
    private final String githubRepo = "r7l-labs/interim";
    private final String currentVersion;
    private String latestVersion;
    private String downloadUrl;
    
    public UpdateChecker(Plugin plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
    }
    
    /**
     * Check if an update is available
     * @return true if update is available
     */
    public boolean checkForUpdate() {
        try {
            URL url = new URL("https://api.github.com/repos/" + githubRepo + "/releases/latest");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
                latestVersion = json.get("tag_name").getAsString();
                
                // Get download URL for the JAR file
                if (json.has("assets") && json.getAsJsonArray("assets").size() > 0) {
                    JsonObject asset = json.getAsJsonArray("assets").get(0).getAsJsonObject();
                    downloadUrl = asset.get("browser_download_url").getAsString();
                }
                
                return !currentVersion.equals(latestVersion);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Download and install the update
     * @return true if successful
     */
    public boolean downloadUpdate() {
        if (downloadUrl == null) {
            plugin.getLogger().warning("No download URL available");
            return false;
        }
        
        try {
            plugin.getLogger().info("Downloading update from: " + downloadUrl);
            
            URL url = new URL(downloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept", "application/octet-stream");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);
            connection.setInstanceFollowRedirects(true);
            
            // Handle redirects manually to ensure we follow them
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || 
                responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
                String newUrl = connection.getHeaderField("Location");
                connection = (HttpURLConnection) new URL(newUrl).openConnection();
                connection.setRequestProperty("Accept", "application/octet-stream");
            }
            
            if (connection.getResponseCode() == 200) {
                // Get the plugin file location
                File pluginFile = getPluginFile();
                File updateFolder = new File(plugin.getDataFolder().getParentFile().getParentFile(), "plugins/update");
                
                if (!updateFolder.exists()) {
                    updateFolder.mkdirs();
                }
                
                File downloadFile = new File(updateFolder, pluginFile.getName());
                
                // Download to update folder
                try (InputStream in = connection.getInputStream();
                     FileOutputStream out = new FileOutputStream(downloadFile)) {
                    
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    long totalBytes = 0;
                    
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                        totalBytes += bytesRead;
                    }
                    
                    plugin.getLogger().info("Downloaded " + totalBytes + " bytes to " + downloadFile.getPath());
                    return true;
                }
            } else {
                plugin.getLogger().warning("Failed to download update. Response code: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to download update: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get the current plugin JAR file
     */
    private File getPluginFile() {
        try {
            return new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to get plugin file location: " + e.getMessage());
            return new File("plugins/" + plugin.getName() + ".jar");
        }
    }
    
    public String getCurrentVersion() {
        return currentVersion;
    }
    
    public String getLatestVersion() {
        return latestVersion;
    }
    
    public String getDownloadUrl() {
        return downloadUrl;
    }
}
