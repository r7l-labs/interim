# Floodgate Integration

Interim supports Floodgate for Bedrock Edition (Minecraft PE/Console) compatibility.

## Overview

**Floodgate** allows Bedrock Edition players to join Java Edition servers. Interim automatically:

- Detects Bedrock players
- Adjusts GUI layouts
- Optimizes interactions
- Handles UUID differences

## Installation

### Prerequisites

1. **GeyserMC**
   - Download from [GeyserMC.org](https://geysermc.org/)
   - Translates Bedrock to Java protocol

2. **Floodgate**
   - Download from [GeyserMC.org](https://geysermc.org/)
   - Allows Bedrock authentication
   - Must match GeyserMC version

### Setup Steps

1. **Install GeyserMC:**
   ```
   plugins/Geyser-Spigot.jar
   ```

2. **Install Floodgate:**
   ```
   plugins/floodgate-spigot.jar
   ```

3. **Start server** to generate configs

4. **Configure GeyserMC:**
   ```yaml
   # plugins/Geyser-Spigot/config.yml
   bedrock:
     address: 0.0.0.0
     port: 19132
   ```

5. **Enable in Interim:**
   ```yaml
   # plugins/Interim/config.yml
   integrations:
     floodgate:
       enabled: true
   ```

6. **Restart server**

## Configuration

### Interim Settings

```yaml
integrations:
  floodgate:
    # Enable Floodgate compatibility
    enabled: true
```

### GeyserMC Settings

Key configurations in `plugins/Geyser-Spigot/config.yml`:

```yaml
bedrock:
  # Port for Bedrock clients
  port: 19132
  
# Allow Bedrock players without Java account
auth-type: floodgate
```

### Floodgate Settings

```yaml
# plugins/floodgate/config.yml
username-prefix: "."    # Bedrock usernames prefixed with .

# Example: .BedrockPlayer
```

## Features

### Automatic Detection

Interim automatically detects Bedrock players via Floodgate API.

**Internally handles:**
- UUID format differences (Java vs Bedrock)
- Username prefixes (. prefix)
- Input method differences

### GUI Optimization

For Bedrock players:

**Adjusted layouts:**
- Simpler menus
- Larger buttons
- Touch-friendly design

**Form conversion:**
- Java GUIs → Bedrock forms
- Better mobile experience

### Command Support

All commands work identically:

```
/town create MyTown      # Works on Bedrock
/nation join Empire      # Works on Bedrock
/map                     # Works on Bedrock
```

### Username Handling

**Java player:**
```
Username: Steve
UUID: Normal Java UUID
```

**Bedrock player:**
```
Username: .BedrockSteve  (note the . prefix)
UUID: Floodgate UUID
```

**In plugin:**
- Both treated equally
- UUIDs properly resolved
- No functional difference

## Bedrock-Specific Considerations

### GUI Limitations

Bedrock forms have different capabilities:

**Java GUI features:**
- Custom items
- Drag-and-drop
- Hover tooltips

**Bedrock forms:**
- Button-based
- Simpler navigation
- No drag-and-drop

**Interim handles:**
- Converts complex GUIs to simple forms
- Maintains functionality
- Optimizes for mobile

### Particle Visualization

Bedrock clients:

**Limited particles:**
- Fewer particle types
- Different rendering
- May not match Java

**Map command:**
```
/map    # Still works, particles may look different
```

### Color Support

Bedrock color codes:

**Supported:**
- Basic color codes (&a, &c, etc.)
- Hex colors (limited)
- Text formatting

**In plugin:**
- Town colors work
- Nation colors work
- Board messages work

## Troubleshooting

### Bedrock Players Can't Join

**Check:**
1. GeyserMC installed: `/plugins`
2. Floodgate installed: `/plugins`
3. Port open: 19132 (UDP)
4. Auth type: `floodgate`

**Solutions:**
- Install GeyserMC and Floodgate
- Open port 19132 (UDP) in firewall
- Check `auth-type: floodgate` in Geyser config

### GUI Not Working for Bedrock

**Check:**
1. Floodgate integration enabled in Interim
2. GeyserMC version matches Floodgate
3. Console for errors

**Solutions:**
- Enable: `integrations.floodgate.enabled: true`
- Update GeyserMC and Floodgate to matching versions
- Check console for GUI errors

### Username Issues

**Problem:**
Bedrock usernames show with . prefix

**This is normal:**
```
Java: Steve
Bedrock: .Steve
```

**In-game display:**
- Both show correctly
- Plugin handles prefix internally
- No functional issues

### UUID Conflicts

**Problem:**
Bedrock UUIDs different from Java

**Floodgate handles:**
- Generates stable UUIDs for Bedrock
- No collisions with Java UUIDs
- Persistent across sessions

**In plugin:**
- UUIDs properly stored
- Cross-platform compatible
- No manual intervention needed

## Limitations

### Bedrock-Specific

**Not supported:**
- Some advanced GUI features
- Exact Java particle rendering
- Custom resource packs (limited)

**Workarounds:**
- Plugin provides Bedrock-friendly forms
- Particles still functional, may differ visually
- Core gameplay unaffected

### Performance

**Bedrock clients:**
- May have higher latency
- Mobile connection variability
- Limited device performance

**Optimization:**
- Keep GUI simple
- Reduce particle density
- Optimize update frequency

## Configuration Examples

### Full Crossplay Server

```yaml
# Interim config.yml
integrations:
  floodgate:
    enabled: true

# Optimize for mobile
claims:
  visualization:
    density: 0.5           # Lighter particles
    continuous: false      # Better performance
```

```yaml
# Geyser config.yml
auth-type: floodgate
bedrock:
  port: 19132
```

### Java-Focused with Bedrock Support

```yaml
# Interim config.yml
integrations:
  floodgate:
    enabled: true

# Standard settings
claims:
  visualization:
    density: 1.0
    continuous: true
```

## Testing

### Verify Integration

1. **Check Floodgate loaded:**
   ```
   /plugins
   ```
   Look for Floodgate in green

2. **Test Bedrock connection:**
   - Connect from Bedrock client
   - Port: 19132
   - Username should have . prefix

3. **Test plugin features:**
   ```
   /town create TestTown
   /town menu
   /map
   ```

4. **Verify GUI:**
   - Open town menu
   - Should show Bedrock form
   - All options functional

## See Also

- [Installation Guide](../getting-started/installation.md)
- [Configuration Guide](../getting-started/configuration.md)
- [Troubleshooting](../admin/troubleshooting.md)
- [GeyserMC Documentation](https://wiki.geysermc.org/)
- [Floodgate Documentation](https://wiki.geysermc.org/floodgate/)
