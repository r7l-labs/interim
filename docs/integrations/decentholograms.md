# DecentHolograms Integration

Interim integrates with [DecentHolograms](https://www.spigotmc.org/resources/decentholograms-1-8-1-20-6-papi-support-many-features.96927/) to display floating text holograms at town spawns and nation capitals.

## Features

- **Town Spawn Holograms**: Automatically created at town spawn points
- **Nation Capital Holograms**: Displayed at nation capital towns
- **Custom Holograms**: Create custom holograms for events or locations
- **Auto-Update**: Holograms update automatically when town/nation data changes
- **PlaceholderAPI Support**: Works with PlaceholderAPI for dynamic content

## Installation

1. **Install DecentHolograms:**
   ```bash
   # Download from SpigotMC
   https://www.spigotmc.org/resources/96927/
   ```

2. **Place in plugins folder:**
   ```
   plugins/
   ├── DecentHolograms.jar
   ├── Interim.jar
   └── PlaceholderAPI.jar (optional, for placeholders)
   ```

3. **Restart server**

4. **Verify integration:**
   ```
   /plugins
   ```
   Look for both DecentHolograms and Interim in green

## Configuration

Edit `plugins/Interim/config.yml`:

```yaml
integrations:
  decentholograms:
    # Enable DecentHolograms integration
    enabled: true
    
    # Create holograms at town spawns
    town-spawns: true
    
    # Create holograms at nation capitals
    nation-capitals: true
    
    # Update interval in seconds (0 = update on changes only)
    update-interval: 60
```

### Configuration Options

| Setting | Default | Description |
|---------|---------|-------------|
| `enabled` | `true` | Master switch for hologram integration |
| `town-spawns` | `true` | Create holograms at town spawns |
| `nation-capitals` | `true` | Create holograms at nation capitals |
| `update-interval` | `60` | How often to refresh holograms (seconds) |

## Town Spawn Holograms

Automatically created when a town sets its spawn point.

### Display Format

```
╔═══════════════╗
║ TownName ║
╚═══════════════╝
Mayor: PlayerName
Residents: 15
Claims: 42
Nation: NationName
⚑ Town Spawn ⚑
```

### Features

- **Town Name**: Displayed in town color
- **Mayor**: Current town mayor
- **Statistics**: Resident count and claim count
- **Nation**: Shows nation if town is in one
- **Location**: Appears 3 blocks above spawn point

### Creation

Holograms are created automatically when:
- Town spawn is set with `/town setspawn`
- Town is created with a spawn point
- Integration is enabled and holograms are refreshed

### Updates

Holograms update when:
- Town name changes
- Mayor changes
- Residents join/leave
- Claims are added/removed
- Town joins/leaves nation
- Spawn location changes

## Nation Capital Holograms

Created at the spawn point of a nation's capital town.

### Display Format

```
✦═══════════════✦
⚔ NationName ⚔
✦═══════════════✦
Capital: TownName
Leader: PlayerName
Towns: 5
Allies: 2 Enemies: 1

★ Nation Capital ★
```

### Features

- **Nation Name**: Displayed in nation color
- **Capital Town**: Shows capital name
- **Leader**: Capital town's mayor
- **Statistics**: Town count, ally count, enemy count
- **Location**: Appears 5 blocks above capital spawn

### Creation

Created automatically when:
- Nation is formed
- Capital town has a spawn point
- Integration enabled

### Updates

Updates when:
- Nation name changes
- Capital changes
- Towns join/leave
- Alliances/enemies change
- Capital spawn moves

## Custom Holograms

Create custom holograms for special events or locations.

### Via Commands

DecentHolograms commands work alongside Interim:

```bash
# Create custom hologram
/holo create <name> <text>

# Edit hologram
/holo edit <name>

# Delete hologram
/holo delete <name>
```

### Via API

```java
Interim interim = (Interim) Bukkit.getPluginManager().getPlugin("Interim");
DecentHologramsIntegration holograms = interim.getHologramsIntegration();

// Create custom hologram
Location location = new Location(world, x, y, z);
List<String> lines = Arrays.asList(
    "§6Welcome to the Arena!",
    "§7Fights start every hour",
    "§cPvP Enabled"
);
holograms.createCustomHologram("arena_welcome", location, lines);

// Remove custom hologram
holograms.removeCustomHologram("arena_welcome");
```

## Hologram Management

### Refresh All Holograms

Manually refresh all holograms:

```bash
/interimadmin reload
```

This recreates all holograms with current data.

### Remove All Holograms

Disable integration in config:

```yaml
integrations:
  decentholograms:
    enabled: false
```

Then reload:
```bash
/interimadmin reload
```

All Interim holograms will be removed.

### Selective Removal

Remove only town holograms:
```yaml
integrations:
  decentholograms:
    town-spawns: false
    nation-capitals: true
```

Remove only nation holograms:
```yaml
integrations:
  decentholograms:
    town-spawns: true
    nation-capitals: false
```

## PlaceholderAPI Integration

Use PlaceholderAPI placeholders in custom holograms:

```bash
/holo create stats §6Server Statistics
/holo addline stats §7Towns: §f%interim_stats_total_towns%
/holo addline stats §7Nations: §f%interim_stats_total_nations%
/holo addline stats §7Claims: §f%interim_stats_total_claims%
```

### Available Placeholders

See [PlaceholderAPI Integration](placeholderapi.md) for full list.

Common ones for holograms:
- `%interim_stats_total_towns%` - Total towns
- `%interim_stats_total_nations%` - Total nations
- `%interim_stats_total_residents%` - Total residents
- `%interim_town_name%` - Player's town name
- `%interim_nation_name%` - Player's nation name

## Troubleshooting

### Holograms Not Appearing

**Check DecentHolograms is installed:**
```bash
/plugins
```
Look for DecentHolograms in green.

**Check integration enabled:**
```bash
# View config
cat plugins/Interim/config.yml | grep decentholograms -A 5
```

**Check spawn points set:**
```bash
/town spawn
```
Holograms only appear where spawns exist.

**Manually refresh:**
```bash
/interimadmin reload
```

### Holograms Not Updating

**Check update interval:**
```yaml
integrations:
  decentholograms:
    update-interval: 60  # Try lower value
```

**Manual update:**
```bash
/interimadmin reload
```

**Check console for errors:**
```bash
tail -f logs/latest.log | grep -i hologram
```

### Holograms in Wrong Location

**Relocate town spawn:**
```bash
/town setspawn
```

Hologram will move automatically.

**For nation capitals:**
Move the capital town's spawn.

### Performance Issues

**Increase update interval:**
```yaml
integrations:
  decentholograms:
    update-interval: 300  # 5 minutes
```

**Disable auto-updates:**
```yaml
integrations:
  decentholograms:
    update-interval: 0  # Only update on changes
```

**Reduce hologram count:**
```yaml
integrations:
  decentholograms:
    town-spawns: false  # Disable town holograms
    nation-capitals: true  # Keep only nation holograms
```

## API Usage

### Get Integration

```java
import org.r7l.interim.Interim;
import org.r7l.interim.integration.DecentHologramsIntegration;

Interim interim = (Interim) Bukkit.getPluginManager().getPlugin("Interim");
DecentHologramsIntegration holograms = interim.getHologramsIntegration();

if (holograms != null && holograms.isEnabled()) {
    // Integration available
}
```

### Create Town Hologram

```java
import org.r7l.interim.model.Town;

Town town = dataManager.getTown(townUuid);
if (town != null && town.getSpawn() != null) {
    holograms.createTownHologram(town);
}
```

### Update Town Hologram

```java
// After modifying town data
town.setName("NewName");
dataManager.saveTown(town);

// Update hologram
holograms.updateTownHologram(town);
```

### Remove Town Hologram

```java
holograms.removeTownHologram(town);
```

### Create Nation Capital Hologram

```java
import org.r7l.interim.model.Nation;

Nation nation = dataManager.getNation(nationUuid);
holograms.createNationCapitalHologram(nation);
```

### Refresh All Holograms

```java
holograms.refreshAllHolograms();
```

## Examples

### Welcome Signs

Create welcome holograms at important locations:

```java
Location entrance = new Location(world, 100, 65, 200);
List<String> lines = Arrays.asList(
    "§6§l✦ Welcome to the Server! ✦",
    "",
    "§7Type §f/town create <name>",
    "§7to start your own town!",
    "",
    "§7Current Stats:",
    "§f%interim_stats_total_towns% §7towns",
    "§f%interim_stats_total_residents% §7players"
);

holograms.createCustomHologram("spawn_welcome", entrance, lines);
```

### War Announcements

During wars, create battlefield holograms:

```java
Location battlefield = warzone.getCenter();
List<String> lines = Arrays.asList(
    "§c⚔ ACTIVE WAR ZONE ⚔",
    "",
    "§6" + attackerNation.getName() + " §7vs §6" + defenderNation.getName(),
    "§7Score: §c" + attackerPoints + " §7- §c" + defenderPoints,
    "",
    "§cPvP Enabled!"
);

holograms.createCustomHologram("war_" + warUuid, battlefield, lines);
```

### Achievement Displays

Show server achievements:

```java
List<String> lines = Arrays.asList(
    "§6§l🏆 Server Records 🏆",
    "",
    "§7Largest Town:",
    "§f%interim_stats_largest_town%",
    "",
    "§7Largest Nation:",
    "§f%interim_stats_largest_nation%",
    "",
    "§7Wealthiest Town:",
    "§f%interim_stats_wealthiest_town%"
);

holograms.createCustomHologram("records", location, lines);
```

## See Also

- [PlaceholderAPI Integration](placeholderapi.md) - Placeholder support
- [BlueMap Integration](bluemap.md) - Web map integration
- [Configuration Guide](../getting-started/configuration.md) - Config options
- [API Documentation](../development/api.md) - Developer API
- [DecentHolograms Wiki](https://wiki.decentholograms.eu/) - DecentHolograms docs
