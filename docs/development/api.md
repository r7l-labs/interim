# API Documentation

Developer API for integrating with Interim.

## Getting Started

### Maven Dependency

Add to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.r7l-labs</groupId>
        <artifactId>interim</artifactId>
        <version>dev-0.2.16</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Getting the API

```java
import org.r7l.interim.Interim;

Interim interim = (Interim) Bukkit.getPluginManager().getPlugin("Interim");
DataManager dataManager = interim.getDataManager();
```

## Core Classes

### DataManager

Main API access point:

```java
DataManager dm = interim.getDataManager();

// Get town
Town town = dm.getTown(uuid);
Town town = dm.getTownByName("TownName");

// Get nation
Nation nation = dm.getNation(uuid);
Nation nation = dm.getNationByName("NationName");

// Get resident
Resident resident = dm.getResident(playerUUID);

// Get claim
Claim claim = dm.getClaim(world, chunkX, chunkZ);
```

### Town

```java
Town town = dataManager.getTown(uuid);

// Properties
UUID uuid = town.getUuid();
String name = town.getName();
UUID mayor = town.getMayor();
Set<UUID> assistants = town.getAssistants();
Set<UUID> residents = town.getResidents();
double bank = town.getBank();

// Settings
boolean pvp = town.isPvp();
boolean explosions = town.isExplosions();
boolean mobSpawning = town.isMobSpawning();
```

### Nation

```java
Nation nation = dataManager.getNation(uuid);

UUID uuid = nation.getUuid();
String name = nation.getName();
UUID capital = nation.getCapital();
Set<UUID> towns = nation.getTowns();
double bank = nation.getBank();
```

## Events

Coming soon: Event system for hooks.

## See Also

- [Building Guide](building.md)
- [Contributing Guide](contributing.md)
- [GitHub Repository](https://github.com/r7l-labs/interim)
