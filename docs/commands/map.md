# Map Commands

Complete reference for map visualization commands.

## Command Overview

| Command | Description | Permission |
|---------|-------------|------------|
| `/map` | Show nearby claims with particles | `interim.map.view` |

## Detailed Commands

### `/map`

Display nearby claims using particle borders.

**Permission:** `interim.map.view`

**Usage:**
```
/map
```

**Effects:**
- Shows chunk borders with colored particles
- Your town in your configured color
- Other towns in their colors
- Wilderness has no particles
- Displays for 30 seconds (configurable)

**Display:**
```
Legend:
██  Your town (green)
▓▓  Your nation (light green)
▒▒  Allied nations (blue)
░░  Neutral towns (white)
──  Enemy nations (red)
..  Wilderness (none)
```

---

## Visualization

### Particle Colors

Claims display in their configured colors:

**Your town:**
- Uses your town color: `/town color #00FF00`

**Nation members:**
- Uses nation color if town has no color set
- Individual town color overrides nation color

**Other towns:**
- Display in their configured colors
- Default color if none set

### Particle Types

Configured in `config.yml`:

```yaml
claims:
  visualization:
    particle: "REDSTONE_DUST"    # Particle type
    density: 1.0                 # Particles per block
    height: 2                    # Height above ground
    duration: 30                 # Display seconds
    continuous: true             # Solid vs dotted line
```

**Available particles:**
- `REDSTONE_DUST` - Customizable color (recommended)
- `FLAME` - Fire particles
- `VILLAGER_HAPPY` - Green sparkles
- `END_ROD` - White beams
- `ELECTRIC_SPARK` - Electric effect

---

## Client-Side Chunk Borders

### Minecraft F3 + G

Press **F3 + G** in Minecraft to toggle client-side chunk borders.

**Benefits:**
- Always visible (no duration limit)
- No particles needed
- Performance friendly
- Works in all versions

**Display:**
- Blue lines at chunk boundaries
- Works independently of `/map` command

---

## Configuration

### Adjusting Visualization

Edit `config.yml`:

```yaml
claims:
  visualization:
    # Particle type
    particle: "REDSTONE_DUST"
    
    # How many particles per block (higher = more visible)
    density: 1.0
    
    # Height above ground level
    height: 2
    
    # How long particles display (seconds)
    duration: 30
    
    # Continuous line (true) or dotted line (false)
    continuous: true
```

**Performance tuning:**

High performance servers:
```yaml
continuous: true
density: 1.0
```

Low performance servers:
```yaml
continuous: false
density: 0.5
```

---

## BlueMap Integration

If BlueMap is installed, view claims on the web map.

**Access:**
```
http://yourserver:8100
```

**Features:**
- Permanent visualization
- No particle lag
- Click claims for info
- Nation borders highlighted
- Color-coded territories

**See:** [BlueMap Integration](../integrations/bluemap.md)

---

## Troubleshooting

### Particles Not Showing

**Check:**
1. Client particle setting (Options > Video Settings > Particles)
2. Standing too far (only shows nearby chunks)
3. Duration expired (run `/map` again)
4. Particle type supported in your version

**Solutions:**
- Enable particles: Set to "All" in Minecraft settings
- Move closer to claims
- Increase duration in config
- Try different particle type

### Performance Issues

**Symptoms:**
- Lag when running `/map`
- Low FPS near borders
- Server TPS drop

**Solutions:**

Reduce density:
```yaml
density: 0.5
```

Shorten duration:
```yaml
duration: 15
```

Use dotted lines:
```yaml
continuous: false
```

Change particle type:
```yaml
particle: "ELECTRIC_SPARK"  # Lighter weight
```

### Colors Not Showing

**Check:**
1. Town/nation has color set
2. Using REDSTONE_DUST particle type
3. Client supports colored particles

**Set colors:**
```
/town color #FF0000
/nation color #00FF00
```

**Note:** Only REDSTONE_DUST particles support custom colors.

---

## Examples

### Basic Usage

Show nearby claims:
```
/map
```

Result: Particles appear at chunk borders for 30 seconds.

---

### With Custom Colors

1. Set town color:
```
/town color #FF0000    # Red
```

2. Show map:
```
/map
```

Result: Your town borders appear in red.

---

### Nation View

If your town is in a nation:

1. Set nation color:
```
/nation color #0000FF    # Blue
```

2. Show map:
```
/map
```

Result: All nation towns appear in blue (unless they have individual colors).

---

## Permission Nodes

### Player Permissions

```yaml
interim.map.view: true           # Use /map command
```

### Wildcard Permission

```yaml
interim.map.*: true
```

---

## Related Features

### Setting Colors

**Town color:**
```
/town color <hex>
```

**Nation color:**
```
/nation color <hex>
```

**Admin override:**
```
/interimadmin towncolor <town> <hex>
/interimadmin nationcolor <nation> <hex>
```

See [Town Commands](town.md) and [Nation Commands](nation.md).

---

## See Also

- [Claims Feature Guide](../features/claims.md)
- [BlueMap Integration](../integrations/bluemap.md)
- [Configuration Guide](../getting-started/configuration.md)
- [Town Commands](town.md)
- [Performance Tuning](../admin/performance.md)
