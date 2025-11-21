# BlueMap Integration

Interim integrates with BlueMap to display towns and nations on an interactive web map.

## Overview

**BlueMap** is a web-based map for Minecraft servers. With Interim integration:

- Towns appear as colored regions
- Nations have distinct borders
- Claims are clickable for info
- Real-time updates
- Color customization

## Installation

### Prerequisites

1. **BlueMap Plugin**
   - Download from [SpigotMC](https://www.spigotmc.org/resources/bluemap.83557/)
   - Compatible with Paper/Spigot 1.21+

2. **Interim Plugin**
   - Already installed

### Setup Steps

1. **Install BlueMap:**
   ```
   plugins/BlueMap-x.x.x.jar
   ```

2. **Start server** to generate BlueMap config

3. **Configure BlueMap** (optional):
   ```
   plugins/BlueMap/core.conf
   ```

4. **Enable in Interim:**
   ```yaml
   # plugins/Interim/config.yml
   integrations:
     bluemap:
       enabled: true
       update-interval: 60
       show-names: true
       show-nations: true
       detail-level: "DETAILED"
   ```

5. **Restart server**

6. **Access web map:**
   ```
   http://yourserver:8100
   ```

## Configuration

### Interim Settings

Edit `plugins/Interim/config.yml`:

```yaml
integrations:
  bluemap:
    # Enable BlueMap integration
    enabled: true
    
    # Auto-update interval (seconds)
    # How often claims sync to map
    update-interval: 60
    
    # Show town names on markers
    show-names: true
    
    # Show nation borders
    show-nations: true
    
    # Detail level for markers
    # Options: BASIC, DETAILED, FULL
    detail-level: "DETAILED"
```

### Detail Levels

**BASIC:**
- Town name only
- Minimal info

**DETAILED (Recommended):**
- Town name
- Resident count
- Claim count
- Mayor name

**FULL:**
- All basic/detailed info
- Bank balance
- Settings (PvP, explosions)
- Founded date
- Board message

### Update Interval

Controls how frequently the map refreshes:

**Real-time (expensive):**
```yaml
update-interval: 10    # 10 seconds
```

**Balanced:**
```yaml
update-interval: 60    # 1 minute (recommended)
```

**Performance:**
```yaml
update-interval: 300   # 5 minutes
```

## Features

### Town Visualization

Towns appear as:

**Colored regions:**
- Uses town color (`/town color #FF0000`)
- Falls back to nation color
- Default color if none set

**Clickable markers:**
- Click to view town info
- Shows residents, claims, etc.

**Borders:**
- Clear boundaries between towns
- Different styles for different statuses

### Nation Borders

Nations display with:

**Unified colors:**
- All member towns in nation color
- Or individual town colors

**Thick borders:**
- Nation boundaries more prominent
- Distinguished from town borders

**Grouping:**
- Towns grouped under nation name
- Expandable lists in UI

### Claim Info

Click any claim to see:

- Town name
- Nation (if any)
- Owner (mayor)
- Resident count
- Bank balance (if detailed)
- Settings

### Legend

Map automatically includes:

- Color key for all towns
- Nation listings
- Alliance information
- War zones (if active wars)

## Colors

### Setting Colors

**Town colors:**
```
/town color #FF0000      # Red
/town color #00FF00      # Green
/town color #0000FF      # Blue
```

**Nation colors:**
```
/nation color #FF0000
```

**Admin override:**
```
/interimadmin towncolor <town> #FFFFFF
/interimadmin nationcolor <nation> #FFFFFF
```

### Color Inheritance

Priority (highest to lowest):

1. **Town-specific color** - Set by town
2. **Nation color** - If town in nation
3. **Default color** - Plugin default

### Recommended Colors

For visibility on maps:

```
Bright colors:
#FF0000  - Red
#00FF00  - Green
#0000FF  - Blue
#FFFF00  - Yellow
#FF00FF  - Magenta
#00FFFF  - Cyan

Avoid:
#000000  - Black (invisible)
#FFFFFF  - White (hard to see)
#808080  - Gray (blends in)
```

## Forcing Updates

### Manual Update

Push changes immediately:

```
/interimadmin bluemap update
```

**Use when:**
- After bulk claim changes
- After color updates
- Map not reflecting changes
- Testing visualization

### Auto-Update

Happens automatically based on `update-interval`:

```yaml
update-interval: 60    # Updates every 60 seconds
```

### Performance Considerations

**Frequent updates (10-30s):**
- ✓ Real-time map
- ✗ Higher CPU usage
- Use on powerful servers

**Moderate updates (60-120s):**
- ✓ Good balance
- ✓ Acceptable lag
- Recommended for most

**Infrequent updates (300+s):**
- ✓ Low CPU usage
- ✗ Delayed map updates
- Use on weak servers

## Troubleshooting

### Claims Not Showing

**Check:**
1. BlueMap installed: `/plugins`
2. Integration enabled in config
3. BlueMap rendered the area
4. Forced update: `/interimadmin bluemap update`

**Solutions:**
- Install BlueMap
- Enable: `integrations.bluemap.enabled: true`
- Render map: `/bluemap update`
- Force update: `/interimadmin bluemap update`

### Colors Not Displaying

**Check:**
1. Town/nation has color set
2. BlueMap supports colors (recent version)
3. Forced update after color change

**Solutions:**
- Set color: `/town color #FF0000`
- Update BlueMap to latest version
- Force update: `/interimadmin bluemap update`

### Map Not Updating

**Check:**
1. `update-interval` setting
2. Server console for errors
3. BlueMap web interface loading

**Solutions:**
- Lower interval: `update-interval: 30`
- Check console for errors
- Restart BlueMap: `/bluemap reload`
- Force update: `/interimadmin bluemap update`

### Performance Issues

**Symptoms:**
- Server lag during updates
- TPS drops
- Slow map loading

**Solutions:**

Increase interval:
```yaml
update-interval: 300
```

Reduce detail:
```yaml
detail-level: "BASIC"
```

Disable nation borders:
```yaml
show-nations: false
```

## Web Interface

### Accessing the Map

**Default URL:**
```
http://yourserver:8100
```

**Custom domain:**
Configure in BlueMap's `webserver.conf`

### Map Controls

**Navigation:**
- Left-click drag: Pan
- Scroll: Zoom
- Right-click: Measure

**Layers:**
- Toggle town layer
- Toggle nation layer
- Toggle markers

**Search:**
- Search for towns
- Search for nations
- Filter by status

### Mobile Access

BlueMap is mobile-friendly:

```
http://yourserver:8100
```

Works on:
- iOS Safari
- Android Chrome
- Mobile browsers

## Advanced Features

### Custom Markers

Coming soon: Custom markers for:
- Town spawns
- Important buildings
- Nation capitals
- Embassies

### War Zones

During active wars:
- War zones highlighted
- Battle locations marked
- Captured territory shown

### Alliance Visualization

Allied nations:
- Similar colors
- Grouped in legend
- Connected on map

## Integration with Other Plugins

### Dynmap

If you prefer Dynmap over BlueMap:

Coming soon: Dynmap integration support

### Pl3xMap

Alternative to BlueMap:

Coming soon: Pl3xMap integration support

## Configuration Examples

### High Detail Server

```yaml
integrations:
  bluemap:
    enabled: true
    update-interval: 30
    show-names: true
    show-nations: true
    detail-level: "FULL"
```

### Performance Server

```yaml
integrations:
  bluemap:
    enabled: true
    update-interval: 300
    show-names: true
    show-nations: false
    detail-level: "BASIC"
```

### PvP Server

```yaml
integrations:
  bluemap:
    enabled: true
    update-interval: 60
    show-names: false        # Hide town names (stealth)
    show-nations: true
    detail-level: "BASIC"
```

## See Also

- [Configuration Guide](../getting-started/configuration.md)
- [Claims Feature](../features/claims.md)
- [Towns Feature](../features/towns.md)
- [Nations Feature](../features/nations.md)
- [Admin Commands](../commands/admin.md)
- [Performance Tuning](../admin/performance.md)
