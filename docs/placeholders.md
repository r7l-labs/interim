# PlaceholderAPI Placeholders

Interim provides comprehensive PlaceholderAPI support for use in chat plugins, scoreboards, tab lists, and more.

## Installation

1. Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
2. Install Interim
3. Placeholders will be automatically registered

## Usage

Use placeholders in any plugin that supports PlaceholderAPI:
```
%interim_<placeholder>%
```

## Player/Resident Placeholders

Information about the player viewing the placeholder.

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%interim_player_has_town%` | Whether player is in a town | `true` or `false` |
| `%interim_player_has_nation%` | Whether player's town is in a nation | `true` or `false` |
| `%interim_player_rank%` | Player's town rank | `MAYOR`, `ASSISTANT`, `RESIDENT` |
| `%interim_player_rank_formatted%` | Formatted rank with colors | `§6Mayor`, `§eAssistant`, `§aResident` |

## Town Placeholders

Information about the player's town. Returns empty string if player has no town.

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%interim_town_name%` | Town name | `Springfield` |
| `%interim_town_mayor%` | Mayor's name | `Steve` |
| `%interim_town_residents_count%` | Number of residents | `15` |
| `%interim_town_claims_count%` | Number of claimed chunks | `42` |
| `%interim_town_bank%` | Town bank balance | `12500.50` |
| `%interim_town_pvp%` | PvP enabled status | `true` or `false` |
| `%interim_town_explosions%` | Explosions enabled | `true` or `false` |
| `%interim_town_mobs%` | Mob spawning enabled | `true` or `false` |
| `%interim_town_open%` | Open town status | `true` or `false` |
| `%interim_town_has_spawn%` | Has spawn set | `true` or `false` |
| `%interim_town_board%` | Town board message | `Welcome!` |
| `%interim_town_color%` | Town color name | `AQUA` |
| `%interim_town_color_formatted%` | Town color with formatting | `§bAQUA` |
| `%interim_town_founded%` | Founded timestamp | `1700000000000` |
| `%interim_town_assistants_count%` | Number of assistants | `3` |

## Nation Placeholders

Information about the player's nation. Returns empty string if player has no nation.

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%interim_nation_name%` | Nation name | `Empire` |
| `%interim_nation_capital%` | Capital town name | `Springfield` |
| `%interim_nation_leader%` | Nation leader (capital mayor) | `Steve` |
| `%interim_nation_towns_count%` | Number of towns | `8` |
| `%interim_nation_bank%` | Nation bank balance | `50000.00` |
| `%interim_nation_board%` | Nation board message | `Long live the Empire!` |
| `%interim_nation_color%` | Nation color name | `RED` |
| `%interim_nation_color_formatted%` | Nation color with formatting | `§cRED` |
| `%interim_nation_founded%` | Founded timestamp | `1700000000000` |
| `%interim_nation_allies_count%` | Number of allied nations | `2` |
| `%interim_nation_enemies_count%` | Number of enemy nations | `1` |
| `%interim_nation_total_claims%` | Total claims across all towns | `156` |
| `%interim_nation_total_residents%` | Total residents across all towns | `47` |

## Location Placeholders

Information about the chunk the player is currently standing in.

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%interim_location_has_claim%` | Whether chunk is claimed | `true` or `false` |
| `%interim_location_town%` | Town name or "Wilderness" | `Springfield` or `Wilderness` |
| `%interim_location_nation%` | Nation name (if town has nation) | `Empire` |
| `%interim_location_type%` | Claim type | `NORMAL`, `SPAWN`, `WILDERNESS` |
| `%interim_location_can_build%` | Whether player can build here | `true` or `false` |

## Statistics Placeholders

Server-wide statistics. Works for all players.

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%interim_stats_total_towns%` | Total number of towns | `24` |
| `%interim_stats_total_nations%` | Total number of nations | `6` |
| `%interim_stats_total_residents%` | Total number of residents | `187` |
| `%interim_stats_total_claims%` | Total number of claimed chunks | `542` |
| `%interim_stats_largest_town%` | Town with most residents | `Springfield` |
| `%interim_stats_wealthiest_town%` | Town with most money | `Richville` |
| `%interim_stats_largest_nation%` | Nation with most towns | `Empire` |

## Example Uses

### Chat Format
```yaml
# In your chat plugin config
format: "%interim_town_color_formatted%[%interim_town_name%]§r %player%: %message%"
# Output: §b[Springfield]§r Steve: Hello!
```

### Tab List
```yaml
# In your tab plugin config
header: |
  &6=== Server Stats ===
  &eTowns: &f%interim_stats_total_towns%
  &eNations: &f%interim_stats_total_nations%
  &ePlayers: &f%interim_stats_total_residents%
```

### Scoreboard
```yaml
# In your scoreboard plugin config
lines:
  - "&6&lYour Town"
  - "&eName: &f%interim_town_name%"
  - "&eResidents: &f%interim_town_residents_count%"
  - "&eClaims: &f%interim_town_claims_count%"
  - "&eBank: &f$%interim_town_bank%"
  - ""
  - "&6&lYour Rank"
  - "&e%interim_player_rank_formatted%"
```

### Action Bar / Boss Bar
```yaml
# Show current location info
"%interim_location_town% - %interim_location_nation%"
# Output: Springfield - Empire
```

### Holograms
```yaml
# In your hologram plugin
- "&6Top Town"
- "&e%interim_stats_largest_town%"
- ""
- "&6Wealthiest Town"
- "&e%interim_stats_wealthiest_town%"
```

## Notes

- Placeholders return empty strings when data is not available (e.g., player has no town)
- Boolean placeholders return `true` or `false` as strings
- Numeric placeholders return formatted strings
- Use conditional placeholders from other plugins (like [Conditional Placeholders](https://www.spigotmc.org/resources/conditional-placeholders.81281/)) for advanced logic

## Troubleshooting

**Placeholders not working?**

1. Ensure PlaceholderAPI is installed: `/plugins`
2. Check if Interim is registered: `/papi list`
3. Reload PlaceholderAPI: `/papi reload`
4. Check console for errors when Interim loads

**Getting empty values?**

- Make sure the player has the required data (e.g., is in a town for town placeholders)
- Check that claims/towns/nations exist in the database
- Try `/interim reload` to reload plugin data
