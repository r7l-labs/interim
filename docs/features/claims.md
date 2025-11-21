# Claims & Territory

Learn how to claim land, manage your territory, and understand protection mechanics.

## Overview

**Claims** are chunks of land that your town owns and protects. Each claim:

- Is exactly 16×16 blocks (one Minecraft chunk)
- Costs money to claim (if economy enabled)
- Protects against griefing
- Can have custom settings (plot types)
- Shows on `/map` and BlueMap

## Claiming Basics

### Your First Claim

When you create a town, the chunk you're standing in is automatically claimed.

### Claiming Additional Land

Stand in an unclaimed chunk adjacent to your territory:

```
/town claim
```

**Requirements:**
- Must be in your town (mayor, assistant, or have permission)
- Chunk must be adjacent to existing claims (unless disabled)
- Must have enough money (if economy enabled)
- Chunk cannot be in a disabled world

### Viewing Claims

See nearby territory:

```
/map
```

Particle borders appear showing:
- **Your town claims** - Your town color
- **Allied claims** - Friendly colors
- **Enemy claims** - Hostile colors  
- **Neutral claims** - Other town colors
- **Wilderness** - No particles

### Chunk Boundaries

Press **F3 + G** in Minecraft to see chunk borders client-side.

## Adjacent Requirement

By default, claims must be **adjacent** (touching) to existing claims.

```
┌─────┬─────┬─────┐
│ New │ Old │     │  ✓ Valid - touches existing claim
├─────┼─────┼─────┤
│     │     │ New │  ✗ Invalid - isolated from town
└─────┴─────┴─────┘
```

### Disabling Adjacent Requirement

In `config.yml`:

```yaml
claims:
  require-adjacent: false
```

Now towns can claim anywhere.

## Claim Costs

### Setting Costs

Configure in `config.yml`:

```yaml
town:
  creation-cost: 0.0       # First claim (town creation)
  claim-cost: 100.0        # Each additional claim
```

### Paying for Claims

Money is taken from:

1. Player's balance (if Vault installed)
2. Town bank (if player lacks funds)

### Free Claims

Set `claim-cost: 0.0` for free claiming.

## Unclaiming Land

### Manual Unclaim

Stand in a claim you want to release:

```
/town unclaim
```

**Rules:**
- Cannot unclaim your last chunk (delete town instead)
- No refund (configurable)
- Adjacent claims stay valid

### Unclaim on Town Delete

When a town is deleted, all claims are automatically released.

## Plot Types

Each claim can have a special type that changes behavior.

### Normal (Default)

Standard protection:

- Only town members can build
- Respects town PvP setting
- Full container protection

### Embassy

Allow foreign towns to claim within your nation:

```
/plot type embassy
```

**Uses:**
- Diplomatic missions
- Trade posts
- Inter-nation cooperation

**Requirements:**
- Town must be in a nation
- Useful for nation territories

### Arena

PvP-enabled plot with no protection:

```
/plot type arena
```

**Effects:**
- PvP always enabled
- No block protection
- No container protection

**Uses:**
- Town PvP arenas
- Combat events
- Tournament grounds

### Shop

Public build access for commerce:

```
/plot type shop
```

**Effects:**
- Anyone can place/break blocks
- Containers still protected
- Useful for market districts

**Uses:**
- Player shops
- Trade districts
- Public markets

### Changing Plot Type

Stand in the claim:

```
/plot type <normal|embassy|arena|shop>
```

## Claim Map

### Using the Map Command

View nearby claims:

```
/map
```

**Display:**
- Current location marked
- Claim colors match town colors
- Nation borders highlighted
- Legend shows ownership

### Map Symbols

```
██  Your town
▓▓  Your nation
▒▒  Allied nations
░░  Neutral towns
──  Enemy nations
..  Wilderness
```

### Particle Visualization

Borders appear with particles:

**Configuration:**
```yaml
claims:
  visualization:
    particle: "REDSTONE_DUST"
    density: 1.0
    height: 2
    duration: 30
    continuous: true
```

**Particle types:**
- `REDSTONE_DUST` - Customizable color
- `FLAME` - Fire particles
- `VILLAGER_HAPPY` - Green sparkles
- `END_ROD` - White beams

## Claim Limits

### Per-Town Limits

Set maximum claims per town:

```yaml
town:
  max-claims: -1    # -1 = unlimited
```

**Example limits:**
- Small server: `max-claims: 50`
- Medium server: `max-claims: 200`
- Large server: `max-claims: -1`

### Checking Limits

View current usage:

```
/town info <townname>
```

Shows: `Claims: 15/50`

## World Configuration

### Disabled Worlds

Prevent claiming in specific worlds:

```yaml
claims:
  disabled-worlds:
    - "world_nether"
    - "world_the_end"
    - "resource_world"
```

### Multi-World Support

Enable claiming across dimensions:

```yaml
claims:
  multiworld: true
```

Towns can have claims in multiple worlds.

## Minimum Distance

### Town Spacing

Enforce distance between towns:

```yaml
town:
  min-distance: 5    # Chunks between towns
```

**Example:**
```
Town A claims             Minimum 5 chunks           Town B claims
├─────┼─────┤ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ├─────┼─────┤
```

### Checking Distance

When claiming fails:
```
"Cannot claim - too close to Town X (3 chunks away, minimum 5 required)"
```

### Override (Admin)

Admins can force claims:

```
/interimadmin forceclaim <town>
```

## Claim Info

### Plot Details

Stand in a claim and run:

```
/plot
```

Shows:
- Town owner
- Plot type
- Permissions
- Settings (PvP, explosions, etc.)

### Town Claim List

View all your town's claims:

```
/town claims
```

Lists coordinates and world for each claim.

## BlueMap Integration

If BlueMap is installed, claims appear on the web map.

### Viewing on Map

1. Access BlueMap: `http://yourserver:8100`
2. Toggle nation layers
3. Click claims for info

### Claim Colors

Claims use town/nation colors:

```
/town color #00FF00       # Set town color
/nation color #FF0000     # Set nation color
```

### Forcing Updates

Push changes to BlueMap:

```
/interimadmin bluemap update
```

**Configuration:**
```yaml
integrations:
  bluemap:
    enabled: true
    update-interval: 60     # Seconds between auto-updates
    show-names: true
    show-nations: true
```

## Advanced Features

### Claim Borders

Borders use **continuous lines** by default:

```yaml
claims:
  visualization:
    continuous: true      # Solid line
```

Set `false` for dotted borders (better performance).

### Claim Permissions

Coming soon: Per-plot permission systems.

### Claim Groups

Coming soon: Group claims into districts.

## Protection Mechanics

### What's Protected

In your claims:

- ✓ Block breaking/placing
- ✓ Container access
- ✓ Entity damage (item frames, paintings, etc.)
- ✓ Door/button usage
- ✓ Crop trampling
- ✓ Vehicle placement

### Who Can Build

**Default:**
- Mayor: ✓
- Assistants: ✓
- Residents: ✓
- Outsiders: ✗

**Shop plots:**
- Everyone: ✓

**Arena plots:**
- No protection

### Bypass Protection

With permission:

```
interim.admin.bypass
```

Admins can build anywhere.

## Performance

### Claim Caching

Claims are cached for fast lookups:

```yaml
performance:
  claim-cache-size: 5000    # Number of claims cached
```

### Async Loading

Claims load asynchronously:

```yaml
performance:
  async-chunks: true
```

## Troubleshooting

### "Cannot claim - must be adjacent"

- Check `require-adjacent` in config
- Or use `/map` to find existing claims
- Ensure chunk touches your territory

### "Cannot claim - too close to another town"

- Check `min-distance` setting
- Use `/map` to locate nearby towns
- Admin can override with force claim

### "Claim limit reached"

- Check limit: `/town info`
- Unclaim unused land: `/town unclaim`
- Admin can adjust: `max-claims` in config

### "Cannot claim in this world"

- World is in `disabled-worlds` list
- Admin can enable world in config

### Particles Not Showing

- Check you ran `/map` recently
- Verify client particle settings
- Check `visualization` config section
- Restart client if needed

## Configuration Reference

```yaml
claims:
  require-adjacent: true
  multiworld: true
  disabled-worlds: []
  
  visualization:
    particle: "REDSTONE_DUST"
    density: 1.0
    height: 2
    duration: 30
    continuous: true

town:
  claim-cost: 100.0
  max-claims: -1
  min-distance: 5
```

See [Configuration Guide](../getting-started/configuration.md).

## Permissions

```
interim.town.claim             # Claim land
interim.town.unclaim           # Unclaim land
interim.admin.forceclaim       # Force claim (admin)
interim.admin.bypass           # Bypass protection
```

See [Permissions Reference](../admin/permissions.md).

## See Also

- [Towns](towns.md) - Town management
- [Protection](protection.md) - How protection works
- [BlueMap Integration](../integrations/bluemap.md) - Web map setup
- [Plot Commands](../commands/plot.md) - Full command reference
