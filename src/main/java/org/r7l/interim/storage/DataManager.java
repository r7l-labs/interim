package org.r7l.interim.storage;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.r7l.interim.model.*;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class DataManager {
    private final File dataFolder;
    private final Gson gson;
    
    private final Map<UUID, Town> towns;
    private final Map<String, Town> townsByName;
    private final Map<UUID, Nation> nations;
    private final Map<String, Nation> nationsByName;
    private final Map<UUID, Resident> residents;
    private final Map<String, Claim> claims; // Key: world:x,z
    private final Map<UUID, List<Invite>> invites;
    
    public DataManager(File dataFolder) {
        this.dataFolder = dataFolder;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Location.class, new LocationAdapter())
                .create();
        
        this.towns = new HashMap<>();
        this.townsByName = new HashMap<>();
        this.nations = new HashMap<>();
        this.nationsByName = new HashMap<>();
        this.residents = new HashMap<>();
        this.claims = new HashMap<>();
        this.invites = new HashMap<>();
        
        // Ensure directories exist and migrate legacy data if present
        createDirectories();
        migrateLegacyData();
    }
    
    private void createDirectories() {
        new File(dataFolder, "towns").mkdirs();
        new File(dataFolder, "nations").mkdirs();
        new File(dataFolder, "residents").mkdirs();
        new File(dataFolder, "claims").mkdirs();
    }

    /**
     * Migrate legacy data from the plugin root data folder into the new data subfolder.
     *
     * Some older versions stored the data directly in the plugin folder (e.g. <plugin>/towns,
     * <plugin>/claims) while newer versions use a dedicated <plugin>/data/ subfolder. To avoid
     * data loss on upgrade, detect legacy directories and move them into the new location.
     */
    private void migrateLegacyData() {
        File pluginFolder = dataFolder.getParentFile();
        if (pluginFolder == null || !pluginFolder.exists()) return;

        String[] entries = {"towns", "nations", "residents", "claims"};
        for (String name : entries) {
            File newDir = new File(dataFolder, name);
            File legacyDir = new File(pluginFolder, name);

            // If legacy exists and new is empty (or doesn't exist), attempt to move
            if (legacyDir.exists()) {
                // If newDir already contains files, skip migration for safety
                boolean newHasContent = newDir.exists() && (newDir.listFiles() != null && newDir.listFiles().length > 0);
                if (newHasContent) continue;

                try {
                    // Ensure parent exists
                    newDir.getParentFile().mkdirs();

                    // Move legacy directory to new location (attempt atomic move, fallback to copy)
                    java.nio.file.Path src = legacyDir.toPath();
                    java.nio.file.Path dst = newDir.toPath();
                    try {
                        java.nio.file.Files.move(src, dst, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        Bukkit.getLogger().info("Migrated legacy data: " + name + " -> data/" + name);
                    } catch (IOException ex) {
                        // Fallback: copy recursively
                        copyDirectoryRecursive(legacyDir, newDir);
                        // Attempt to delete legacy after copy
                        deleteDirectoryRecursive(legacyDir);
                        Bukkit.getLogger().info("Copied legacy data: " + name + " -> data/" + name);
                    }
                } catch (Exception e) {
                    Bukkit.getLogger().warning("Failed to migrate legacy data for '" + name + "': " + e.getMessage());
                }
            }
        }
    }

    private void copyDirectoryRecursive(File src, File dst) throws IOException {
        if (src.isDirectory()) {
            if (!dst.exists()) dst.mkdirs();
            String[] children = src.list();
            if (children != null) {
                for (String child : children) {
                    copyDirectoryRecursive(new File(src, child), new File(dst, child));
                }
            }
        } else {
            java.nio.file.Files.copy(src.toPath(), dst.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void deleteDirectoryRecursive(File dir) {
        if (!dir.exists()) return;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) deleteDirectoryRecursive(f);
                else f.delete();
            }
        }
        dir.delete();
    }
    
    // Town methods
    public void addTown(Town town) {
        towns.put(town.getUuid(), town);
        townsByName.put(town.getName().toLowerCase(), town);
    }
    
    public void removeTown(Town town) {
        towns.remove(town.getUuid());
        townsByName.remove(town.getName().toLowerCase());
        
        // Delete the JSON file
        File file = new File(dataFolder, "towns/" + town.getUuid().toString() + ".json");
        if (file.exists()) {
            file.delete();
        }
    }
    
    public Town getTown(UUID uuid) {
        return towns.get(uuid);
    }
    
    public Town getTown(String name) {
        return townsByName.get(name.toLowerCase());
    }
    
    public Collection<Town> getTowns() {
        return new ArrayList<>(towns.values());
    }
    
    public boolean townExists(String name) {
        return townsByName.containsKey(name.toLowerCase());
    }
    
    // Nation methods
    public void addNation(Nation nation) {
        nations.put(nation.getUuid(), nation);
        nationsByName.put(nation.getName().toLowerCase(), nation);
    }
    
    public void removeNation(Nation nation) {
        nations.remove(nation.getUuid());
        nationsByName.remove(nation.getName().toLowerCase());
        
        // Delete the JSON file
        File file = new File(dataFolder, "nations/" + nation.getUuid().toString() + ".json");
        if (file.exists()) {
            file.delete();
        }
    }
    
    public Nation getNation(UUID uuid) {
        return nations.get(uuid);
    }
    
    public Nation getNation(String name) {
        return nationsByName.get(name.toLowerCase());
    }
    
    public Collection<Nation> getNations() {
        return new ArrayList<>(nations.values());
    }
    
    public boolean nationExists(String name) {
        return nationsByName.containsKey(name.toLowerCase());
    }
    
    // Resident methods
    public void addResident(Resident resident) {
        residents.put(resident.getUuid(), resident);
    }
    
    public void removeResident(Resident resident) {
        residents.remove(resident.getUuid());
    }
    
    public Resident getResident(UUID uuid) {
        return residents.get(uuid);
    }
    
    public Resident getOrCreateResident(UUID uuid, String name) {
        return residents.computeIfAbsent(uuid, k -> new Resident(uuid, name));
    }
    
    public Collection<Resident> getResidents() {
        return new ArrayList<>(residents.values());
    }
    
    // Claim methods
    public void addClaim(Claim claim) {
        claims.put(claim.getCoordString(), claim);
    }
    
    public void removeClaim(Claim claim) {
        claims.remove(claim.getCoordString());
    }
    
    public Claim getClaim(String worldName, int x, int z) {
        return claims.get(worldName + ":" + x + "," + z);
    }
    
    public Claim getClaim(Location location) {
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;
        return getClaim(location.getWorld().getName(), chunkX, chunkZ);
    }
    
    public Collection<Claim> getClaims() {
        return new ArrayList<>(claims.values());
    }
    
    public boolean isClaimed(String worldName, int x, int z) {
        return claims.containsKey(worldName + ":" + x + "," + z);
    }
    
    // Invite methods
    public void addInvite(UUID playerUuid, Invite invite) {
        invites.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(invite);
    }
    
    public void removeInvite(UUID playerUuid, Invite invite) {
        List<Invite> playerInvites = invites.get(playerUuid);
        if (playerInvites != null) {
            playerInvites.remove(invite);
            if (playerInvites.isEmpty()) {
                invites.remove(playerUuid);
            }
        }
    }
    
    public List<Invite> getInvites(UUID playerUuid) {
        List<Invite> playerInvites = invites.get(playerUuid);
        if (playerInvites == null) {
            return new ArrayList<>();
        }
        // Remove expired invites
        playerInvites.removeIf(Invite::isExpired);
        return new ArrayList<>(playerInvites);
    }
    
    public void clearInvites(UUID playerUuid) {
        invites.remove(playerUuid);
    }
    
    // Save/Load methods
    public void saveAll() {
        saveTowns();
        saveNations();
        saveResidents();
        saveClaims();
    }
    
    public void loadAll() {
        loadResidents();
        loadClaims();
        loadTowns();
        loadNations();
        linkData();
    }
    
    private void saveTowns() {
        File townsDir = new File(dataFolder, "towns");
        for (Town town : towns.values()) {
            File file = new File(townsDir, town.getUuid().toString() + ".json");
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(serializeTown(town), writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void loadTowns() {
        File townsDir = new File(dataFolder, "towns");
        if (!townsDir.exists()) return;
        
        File[] files = townsDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return;
        
        for (File file : files) {
            try (Reader reader = new FileReader(file)) {
                JsonObject json = gson.fromJson(reader, JsonObject.class);
                Town town = deserializeTown(json);
                if (town != null) {
                    addTown(town);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void saveNations() {
        File nationsDir = new File(dataFolder, "nations");
        for (Nation nation : nations.values()) {
            File file = new File(nationsDir, nation.getUuid().toString() + ".json");
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(serializeNation(nation), writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void loadNations() {
        File nationsDir = new File(dataFolder, "nations");
        if (!nationsDir.exists()) return;
        
        File[] files = nationsDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return;
        
        for (File file : files) {
            try (Reader reader = new FileReader(file)) {
                JsonObject json = gson.fromJson(reader, JsonObject.class);
                Nation nation = deserializeNation(json);
                if (nation != null) {
                    addNation(nation);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void saveResidents() {
        File residentsFile = new File(dataFolder, "residents/residents.json");
        try (Writer writer = new FileWriter(residentsFile)) {
            JsonArray array = new JsonArray();
            for (Resident resident : residents.values()) {
                array.add(serializeResident(resident));
            }
            gson.toJson(array, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadResidents() {
        File residentsFile = new File(dataFolder, "residents/residents.json");
        if (!residentsFile.exists()) return;
        
        try (Reader reader = new FileReader(residentsFile)) {
            JsonArray array = gson.fromJson(reader, JsonArray.class);
            if (array != null) {
                for (JsonElement element : array) {
                    Resident resident = deserializeResident(element.getAsJsonObject());
                    if (resident != null) {
                        addResident(resident);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void saveClaims() {
        File claimsFile = new File(dataFolder, "claims/claims.json");
        try (Writer writer = new FileWriter(claimsFile)) {
            JsonArray array = new JsonArray();
            for (Claim claim : claims.values()) {
                array.add(serializeClaim(claim));
            }
            gson.toJson(array, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadClaims() {
        File claimsFile = new File(dataFolder, "claims/claims.json");
        if (!claimsFile.exists()) return;
        
        try (Reader reader = new FileReader(claimsFile)) {
            JsonArray array = gson.fromJson(reader, JsonArray.class);
            if (array != null) {
                for (JsonElement element : array) {
                    Claim claim = deserializeClaim(element.getAsJsonObject());
                    if (claim != null) {
                        addClaim(claim);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void linkData() {
        // Link residents to towns
        for (Resident resident : residents.values()) {
            // Will be linked when deserializing
        }
        
        // Link towns to nations
        for (Town town : towns.values()) {
            // Will be linked when deserializing
        }
        
        // Link claims to towns
        for (Claim claim : claims.values()) {
            Town town = getTown(claim.getTown().getUuid());
            if (town != null) {
                town.addClaim(claim);
                claim.setTown(town);
            }
        }
    }
    
    // Serialization methods
    private JsonObject serializeTown(Town town) {
        JsonObject json = new JsonObject();
        json.addProperty("uuid", town.getUuid().toString());
        json.addProperty("name", town.getName());
        json.addProperty("mayor", town.getMayor().toString());
        
        JsonArray residentsArray = new JsonArray();
        for (UUID uuid : town.getResidents()) {
            residentsArray.add(uuid.toString());
        }
        json.add("residents", residentsArray);
        
        JsonArray assistantsArray = new JsonArray();
        for (UUID uuid : town.getAssistants()) {
            assistantsArray.add(uuid.toString());
        }
        json.add("assistants", assistantsArray);
        
        if (town.getNation() != null) {
            json.addProperty("nation", town.getNation().getUuid().toString());
        }
        
        if (town.getSpawn() != null) {
            json.add("spawn", gson.toJsonTree(town.getSpawn()));
        }
        
        json.addProperty("bank", town.getBank());
        json.addProperty("founded", town.getFounded());
        json.addProperty("open", town.isOpen());
        json.addProperty("pvp", town.isPvp());
        json.addProperty("explosions", town.isExplosions());
        json.addProperty("mobSpawning", town.isMobSpawning());
        json.addProperty("board", town.getBoard());
        
        return json;
    }
    
    private Town deserializeTown(JsonObject json) {
        UUID uuid = UUID.fromString(json.get("uuid").getAsString());
        String name = json.get("name").getAsString();
        UUID mayor = UUID.fromString(json.get("mayor").getAsString());
        
        Town town = new Town(name, mayor);
        // Use reflection to set UUID
        try {
            var field = Town.class.getDeclaredField("uuid");
            field.setAccessible(true);
            field.set(town, uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JsonArray residentsArray = json.getAsJsonArray("residents");
        for (JsonElement element : residentsArray) {
            UUID residentUuid = UUID.fromString(element.getAsString());
            town.addResident(residentUuid);
            Resident resident = getResident(residentUuid);
            if (resident != null) {
                resident.setTown(town);
            }
        }
        
        JsonArray assistantsArray = json.getAsJsonArray("assistants");
        for (JsonElement element : assistantsArray) {
            town.addAssistant(UUID.fromString(element.getAsString()));
        }
        
        if (json.has("spawn")) {
            town.setSpawn(gson.fromJson(json.get("spawn"), Location.class));
        }
        
        town.setBank(json.get("bank").getAsDouble());
        town.setOpen(json.get("open").getAsBoolean());
        town.setPvp(json.get("pvp").getAsBoolean());
        town.setExplosions(json.get("explosions").getAsBoolean());
        town.setMobSpawning(json.get("mobSpawning").getAsBoolean());
        town.setBoard(json.get("board").getAsString());
        
        return town;
    }
    
    private JsonObject serializeNation(Nation nation) {
        JsonObject json = new JsonObject();
        json.addProperty("uuid", nation.getUuid().toString());
        json.addProperty("name", nation.getName());
        json.addProperty("capital", nation.getCapital().toString());
        
        JsonArray townsArray = new JsonArray();
        for (UUID uuid : nation.getTowns()) {
            townsArray.add(uuid.toString());
        }
        json.add("towns", townsArray);
        
        JsonArray alliesArray = new JsonArray();
        for (UUID uuid : nation.getAllies()) {
            alliesArray.add(uuid.toString());
        }
        json.add("allies", alliesArray);
        
        JsonArray enemiesArray = new JsonArray();
        for (UUID uuid : nation.getEnemies()) {
            enemiesArray.add(uuid.toString());
        }
        json.add("enemies", enemiesArray);
        
        json.addProperty("bank", nation.getBank());
        json.addProperty("founded", nation.getFounded());
        json.addProperty("board", nation.getBoard());
        json.addProperty("color", nation.getColor().name());
        
        return json;
    }
    
    private Nation deserializeNation(JsonObject json) {
        UUID uuid = UUID.fromString(json.get("uuid").getAsString());
        String name = json.get("name").getAsString();
        UUID capital = UUID.fromString(json.get("capital").getAsString());
        
        Nation nation = new Nation(name, capital);
        // Use reflection to set UUID
        try {
            var field = Nation.class.getDeclaredField("uuid");
            field.setAccessible(true);
            field.set(nation, uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JsonArray townsArray = json.getAsJsonArray("towns");
        for (JsonElement element : townsArray) {
            UUID townUuid = UUID.fromString(element.getAsString());
            nation.addTown(townUuid);
            Town town = getTown(townUuid);
            if (town != null) {
                town.setNation(nation);
            }
        }
        
        JsonArray alliesArray = json.getAsJsonArray("allies");
        for (JsonElement element : alliesArray) {
            nation.addAlly(UUID.fromString(element.getAsString()));
        }
        
        JsonArray enemiesArray = json.getAsJsonArray("enemies");
        for (JsonElement element : enemiesArray) {
            nation.addEnemy(UUID.fromString(element.getAsString()));
        }
        
        nation.setBank(json.get("bank").getAsDouble());
        nation.setBoard(json.get("board").getAsString());
        nation.setColor(NationColor.valueOf(json.get("color").getAsString()));
        
        return nation;
    }
    
    private JsonObject serializeResident(Resident resident) {
        JsonObject json = new JsonObject();
        json.addProperty("uuid", resident.getUuid().toString());
        json.addProperty("name", resident.getName());
        
        // Save multiple towns
        JsonArray townsArray = new JsonArray();
        JsonObject ranksObject = new JsonObject();
        
        for (Map.Entry<UUID, Town> entry : resident.getTowns().entrySet()) {
            townsArray.add(entry.getKey().toString());
            TownRank rank = resident.getRank(entry.getKey());
            ranksObject.addProperty(entry.getKey().toString(), rank != null ? rank.name() : "RESIDENT");
        }
        
        json.add("towns", townsArray);
        json.add("ranks", ranksObject);
        
        // Save primary town
        Town primaryTown = resident.getPrimaryTown();
        if (primaryTown != null) {
            json.addProperty("primaryTown", primaryTown.getUuid().toString());
        }
        
        json.addProperty("joinedTown", resident.getJoinedTown());
        return json;
    }
    
    private Resident deserializeResident(JsonObject json) {
        UUID uuid = UUID.fromString(json.get("uuid").getAsString());
        String name = json.get("name").getAsString();
        
        Resident resident = new Resident(uuid, name);
        
        // Load multiple towns
        if (json.has("towns") && json.has("ranks")) {
            JsonArray townsArray = json.getAsJsonArray("towns");
            JsonObject ranksObject = json.getAsJsonObject("ranks");
            
            for (JsonElement element : townsArray) {
                UUID townUuid = UUID.fromString(element.getAsString());
                Town town = getTown(townUuid);
                if (town != null) {
                    TownRank rank = TownRank.RESIDENT;
                    if (ranksObject.has(townUuid.toString())) {
                        rank = TownRank.valueOf(ranksObject.get(townUuid.toString()).getAsString());
                    }
                    resident.addTown(town, rank, false);
                }
            }
            
            // Set primary town
            if (json.has("primaryTown")) {
                UUID primaryTownUuid = UUID.fromString(json.get("primaryTown").getAsString());
                resident.setPrimaryTown(primaryTownUuid);
            }
        } else if (json.has("town")) {
            // Backwards compatibility - single town
            UUID townUuid = UUID.fromString(json.get("town").getAsString());
            Town town = getTown(townUuid);
            if (town != null) {
                TownRank rank = TownRank.RESIDENT;
                if (json.has("rank")) {
                    rank = TownRank.valueOf(json.get("rank").getAsString());
                }
                resident.addTown(town, rank, true);
            }
        }
        
        return resident;
    }
    
    private JsonObject serializeClaim(Claim claim) {
        JsonObject json = new JsonObject();
        json.addProperty("world", claim.getWorldName());
        json.addProperty("x", claim.getX());
        json.addProperty("z", claim.getZ());
        json.addProperty("town", claim.getTown().getUuid().toString());
        json.addProperty("type", claim.getType().name());
        json.addProperty("claimedTime", claim.getClaimedTime());
        return json;
    }
    
    private Claim deserializeClaim(JsonObject json) {
        String worldName = json.get("world").getAsString();
        int x = json.get("x").getAsInt();
        int z = json.get("z").getAsInt();
        UUID townUuid = UUID.fromString(json.get("town").getAsString());
        
        Town town = getTown(townUuid);
        if (town == null) return null;
        
        Claim claim = new Claim(Bukkit.getWorld(worldName), x, z, town);
        claim.setType(ClaimType.valueOf(json.get("type").getAsString()));
        
        return claim;
    }
    
    // Location adapter for Gson
    private static class LocationAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {
        @Override
        public JsonElement serialize(Location src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("world", src.getWorld().getName());
            json.addProperty("x", src.getX());
            json.addProperty("y", src.getY());
            json.addProperty("z", src.getZ());
            json.addProperty("yaw", src.getYaw());
            json.addProperty("pitch", src.getPitch());
            return json;
        }
        
        @Override
        public Location deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            return new Location(
                    Bukkit.getWorld(obj.get("world").getAsString()),
                    obj.get("x").getAsDouble(),
                    obj.get("y").getAsDouble(),
                    obj.get("z").getAsDouble(),
                    obj.get("yaw").getAsFloat(),
                    obj.get("pitch").getAsFloat()
            );
        }
    }
}
