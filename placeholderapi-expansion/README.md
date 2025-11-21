# Interim PlaceholderAPI Expansion

Standalone PlaceholderAPI expansion for Interim that can be installed independently.

## Installation

### Method 1: PlaceholderAPI eCloud (Recommended)

Coming soon - will be available on PlaceholderAPI's eCloud for automatic download.

### Method 2: Manual Installation

1. **Download the expansion JAR:**
   - `Interim-Expansion-00001.jar`

2. **Place in PlaceholderAPI expansions folder:**
   ```
   plugins/PlaceholderAPI/expansions/Interim-Expansion-00001.jar
   ```

3. **Restart server or reload PlaceholderAPI:**
   ```
   /papi reload
   ```

4. **Verify installation:**
   ```
   /papi list
   ```
   Look for "Interim" in the list of registered expansions.

## Requirements

- **PlaceholderAPI** 2.11.6 or newer
- **Interim** plugin installed and running
- **Paper** 1.21.9 or compatible

## Available Placeholders

### Player Placeholders

| Placeholder | Description | Example |
|-------------|-------------|---------|
| `%interim_player_has_town%` | Whether player has a town | `true` or `false` |
| `%interim_player_has_nation%` | Whether player has a nation | `true` or `false` |
| `%interim_player_rank%` | Player's town rank | `MAYOR`, `ASSISTANT`, `RESIDENT` |
| `%interim_player_rank_formatted%` | Formatted rank with colors | `§6Mayor` |

### Town Placeholders

| Placeholder | Description | Example |
|-------------|-------------|---------|
| `%interim_town_name%` | Town name | `Springfield` |
| `%interim_town_mayor%` | Mayor's username | `Steve` |
| `%interim_town_residents_count%` | Number of residents | `15` |
| `%interim_town_claims_count%` | Number of claims | `42` |
| `%interim_town_bank%` | Town bank balance | `12500.50` |
| `%interim_town_pvp%` | PvP enabled | `true` or `false` |
| `%interim_town_explosions%` | Explosions enabled | `true` or `false` |
| `%interim_town_mobs%` | Mob spawning enabled | `true` or `false` |
| `%interim_town_open%` | Town open to join | `true` or `false` |
| `%interim_town_has_spawn%` | Town has spawn set | `true` or `false` |
| `%interim_town_board%` | Town board message | Custom text |
| `%interim_town_color%` | Town color | `AQUA` |
| `%interim_town_color_formatted%` | Formatted color | `§bAQUA` |
| `%interim_town_founded%` | Timestamp founded | `1700000000000` |
| `%interim_town_assistants_count%` | Number of assistants | `3` |

### Nation Placeholders

| Placeholder | Description | Example |
|-------------|-------------|---------|
| `%interim_nation_name%` | Nation name | `United Empire` |
| `%interim_nation_capital%` | Capital town name | `Springfield` |
| `%interim_nation_leader%` | Nation leader | `Steve` |
| `%interim_nation_towns_count%` | Number of towns | `5` |
| `%interim_nation_bank%` | Nation bank balance | `50000.00` |
| `%interim_nation_board%` | Nation board message | Custom text |
| `%interim_nation_color%` | Nation color | `RED` |
| `%interim_nation_color_formatted%` | Formatted color | `§cRED` |
| `%interim_nation_founded%` | Timestamp founded | `1700000000000` |
| `%interim_nation_allies_count%` | Number of allies | `2` |
| `%interim_nation_enemies_count%` | Number of enemies | `1` |
| `%interim_nation_total_claims%` | Total claims across all towns | `150` |
| `%interim_nation_total_residents%` | Total residents across all towns | `75` |

### Location Placeholders

| Placeholder | Description | Example |
|-------------|-------------|---------|
| `%interim_location_has_claim%` | Current location claimed | `true` or `false` |
| `%interim_location_town%` | Town at location | `Springfield` or `Wilderness` |
| `%interim_location_nation%` | Nation at location | `United Empire` or empty |
| `%interim_location_type%` | Claim type | `TOWN`, `PLOT`, `WILDERNESS` |
| `%interim_location_can_build%` | Player can build here | `true` or `false` |

### Statistics Placeholders

| Placeholder | Description | Example |
|-------------|-------------|---------|
| `%interim_stats_total_towns%` | Total towns on server | `42` |
| `%interim_stats_total_nations%` | Total nations on server | `8` |
| `%interim_stats_total_residents%` | Total residents on server | `250` |
| `%interim_stats_total_claims%` | Total claims on server | `1500` |

## Usage Examples

### In Chat Plugins

**DeluxeChat format:**
```yaml
formats:
  default:
    priority: 1
    prefix: "%interim_town_color_formatted%[%interim_town_name%]§r "
    name_color: "§f"
    name_format: "%player_name%"
```

**EssentialsChat:**
```yaml
format: '%interim_town_color_formatted%[%interim_town_name%]§r {DISPLAYNAME}§7: {MESSAGE}'
```

### In Scoreboard Plugins

**FeatherBoard:**
```yaml
board:
  title: '&6&lServer Stats'
  lines:
    - ''
    - '&7Towns: &f%interim_stats_total_towns%'
    - '&7Nations: &f%interim_stats_total_nations%'
    - '&7Players: &f%interim_stats_total_residents%'
    - ''
    - '&7Your Town: &f%interim_town_name%'
    - '&7Residents: &f%interim_town_residents_count%'
```

### In Tab Plugins

**TAB:**
```yaml
tablist:
  format: '%interim_nation_color_formatted%[%interim_nation_name%]§r %player_name%'
```

### In HolographicDisplays

```yaml
holograms:
  spawn:
    location: world, 0, 65, 0
    lines:
      - '&6&lServer Statistics'
      - '&7Towns: &f%interim_stats_total_towns%'
      - '&7Nations: &f%interim_stats_total_nations%'
      - '&7Claims: &f%interim_stats_total_claims%'
```

## Troubleshooting

### Expansion Not Showing

**Check PlaceholderAPI installed:**
```
/plugins
```

**Check expansion registered:**
```
/papi list
```

**Manually reload:**
```
/papi reload
```

### Placeholders Show as Text

**Parse the placeholder:**
```
/papi parse me %interim_town_name%
```

If it shows as text, ensure:
1. Interim plugin is running
2. Expansion is in the expansions folder
3. PlaceholderAPI reloaded

### Getting Empty Values

Some placeholders return empty when:
- Player doesn't have a town (`town_*` placeholders)
- Player doesn't have a nation (`nation_*` placeholders)
- Location isn't claimed (`location_town`, `location_nation`)

This is expected behavior.

## Development

### Building from Source

```bash
cd placeholderapi-expansion
mvn clean package
```

Output: `target/Interim-Expansion-00001.jar`

### API Integration

The expansion uses reflection to interface with Interim's API, allowing it to work independently while Interim is running.

## Support

- **Documentation:** [Interim Docs](https://github.com/r7l-labs/interim/tree/main/docs)
- **Issues:** [GitHub Issues](https://github.com/r7l-labs/interim/issues)
- **Discord:** Coming soon

## License

This expansion is part of the Interim project and shares the same license.
