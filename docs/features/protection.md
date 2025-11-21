# Protection System

Interim protects your town's territory from griefing, theft, and unauthorized modifications.

## Overview

When you claim land, it becomes **protected**:

- Only authorized players can build
- Containers are locked
- Entities are protected
- PvP can be controlled
- Explosions can be prevented

## What's Protected

### Blocks

In your claims, these are protected:

- **Breaking blocks** - Stone, dirt, ores, etc.
- **Placing blocks** - Building materials
- **Modifying blocks** - Doors, signs, item frames
- **Using blocks** - Buttons, levers, pressure plates

### Containers

All storage is protected:

- **Chests** - Regular and ender
- **Shulker boxes**
- **Barrels**
- **Furnaces**
- **Hoppers**
- **Droppers/Dispensers**
- **Brewing stands**
- **Composters**

### Entities

Living entities and objects:

- **Item frames** - Cannot remove items
- **Armor stands** - Cannot modify/break
- **Paintings** - Cannot remove
- **Minecarts** - Cannot break
- **Boats** - Cannot break
- **Animals** - Cannot harm (unless PvP on)
- **Villagers** - Cannot harm

### Interactions

Player interactions:

- **Doors** - Cannot open (unless allowed)
- **Trapdoors** - Cannot toggle
- **Fence gates** - Cannot open
- **Buttons/levers** - Cannot use
- **Pressure plates** - Cannot trigger
- **Crops** - Cannot trample

## Who Can Build

### Town Members

Default permissions:

| Rank | Can Build | Can Use Items | Can Attack |
|------|-----------|---------------|------------|
| Mayor | ✓ | ✓ | ✓ |
| Assistant | ✓ | ✓ | ✓ |
| Resident | ✓ | ✓ | ✓ |
| Outsider | ✗ | ✗ | Depends on PvP |

### Nation Members

If your town is in a nation:

**Default:**
- Other nation towns: ✗ (cannot build)

**Configurable:**
```yaml
protection:
  nation-build: true    # Allow nation members to build
```

### Allies

Allied nations:

**Default:**
- Allies: ✗ (cannot build)

**Configurable:**
```yaml
protection:
  ally-build: true      # Allow allies to build
```

## Plot Types

Different plot types have different protection:

### Normal

Standard protection:
- Only town members can build
- All interactions blocked for outsiders

### Shop

Public access for commerce:
- Anyone can place/break blocks
- Containers still protected
- Good for player shops

**Set:**
```
/plot type shop
```

### Arena

No protection for PvP:
- No block protection
- No entity protection
- PvP always enabled

**Set:**
```
/plot type arena
```

### Embassy

For foreign towns:
- Allows claiming within your nation
- Normal protection once claimed

**Set:**
```
/plot type embassy
```

## PvP Control

### Town PvP Setting

Control combat in your claims:

```
/town pvp on     # Enable PvP
/town pvp off    # Disable PvP (default)
```

### Wilderness PvP

Unclaimed areas:

```yaml
protection:
  wilderness-pvp: true    # PvP in wilderness
```

### War PvP

During active wars:
- PvP automatically enabled in war zones
- Overrides town PvP setting
- Returns to normal after war ends

### Arena PvP

In arena plots:
- PvP always enabled
- Ignores town setting
- No death penalties (configurable)

## Explosion Protection

### Town Setting

Control explosion damage:

```
/town explosions on     # Allow explosions
/town explosions off    # Prevent explosions (default)
```

**Protects against:**
- TNT
- Creepers
- Ghast fireballs
- Wither explosions
- End crystals

### Global Setting

Server-wide default:

```yaml
protection:
  explosions: true      # Prevent explosions in claims
```

## Mob Control

### Mob Spawning

Control mob spawning in claims:

```
/town mobs on      # Allow spawning
/town mobs off     # Prevent spawning
```

**Affects:**
- Hostile mobs (zombies, skeletons, etc.)
- Passive mobs (cows, pigs, etc.)
- Does not affect mob eggs or spawn eggs

### Mob Damage

Prevent mobs from harming entities:

```yaml
protection:
  mob-spawning: false    # Prevent natural spawning
```

## Fire Protection

### Fire Spread

Prevent fire from spreading:

```yaml
protection:
  fire-spread: true      # Block fire spread in claims
```

**Protects against:**
- Natural fire spread
- Lava ignition
- Flint and steel (if not member)

### Lightning

Lightning strikes in claims:
- Can damage blocks (configurable)
- Can start fires (configurable)

## Bypass Protection

### Admin Bypass

With permission:

```
interim.admin.bypass
```

Allows:
- Building anywhere
- Opening any container
- Breaking any block
- Useful for moderation

**Grant to admins:**
```
/lp group admin permission set interim.admin.bypass true
```

### Temporary Access

Coming soon: Grant temporary build permissions.

## Configuration

### Protection Settings

```yaml
protection:
  # Block actions
  block-break: true          # Prevent breaking
  block-place: true          # Prevent placing
  
  # Interactions
  container-access: true     # Protect chests
  entity-damage: true        # Protect entities
  
  # Combat
  pvp: false                 # Default PvP setting
  wilderness-pvp: true       # PvP in unclaimed
  
  # Environment
  explosions: true           # Prevent explosions
  fire-spread: true          # Prevent fire
  mob-spawning: false        # Prevent mob spawns
  
  # Build permissions
  resident-build: true       # Residents can build
  nation-build: false        # Nation members can build
  ally-build: false          # Allies can build
```

### Fine-Tuning

**High Protection:**
```yaml
protection:
  block-break: true
  block-place: true
  container-access: true
  entity-damage: true
  pvp: false
  wilderness-pvp: false
  explosions: true
  fire-spread: true
  mob-spawning: false
```

**PvP Server:**
```yaml
protection:
  block-break: true
  block-place: true
  container-access: true
  entity-damage: true
  pvp: true
  wilderness-pvp: true
  explosions: false
  fire-spread: false
  mob-spawning: true
```

**Creative Server:**
```yaml
protection:
  block-break: false
  block-place: false
  container-access: false
  entity-damage: false
  pvp: false
  wilderness-pvp: false
  explosions: false
  fire-spread: false
  mob-spawning: false
```

## Special Cases

### Redstone

Redstone works normally:
- Doors can be opened by redstone
- Pistons can push/pull blocks
- Hoppers can transfer items

### Liquids

Water and lava:
- Can flow within claims
- Cannot flow from outside into claims
- Cannot flow from claims into wilderness

### Projectiles

Arrows and other projectiles:
- Can be shot into claims
- Damage follows PvP rules
- Item frames protected

### Vehicles

Minecarts and boats:
- Cannot be broken by outsiders
- Can be entered (configurable)
- Collisions work normally

## Troubleshooting

### "You cannot build here"

**Causes:**
1. Not a member of the town
2. Not in your town's claim
3. Plot type doesn't allow building

**Solutions:**
- Join the town: `/town join`
- Get invited: Ask mayor
- Check claim ownership: `/plot`

### "This container is protected"

**Causes:**
1. Container in protected claim
2. You're not a town member

**Solutions:**
- Join the town
- Ask for access
- Admin bypass: `interim.admin.bypass`

### Redstone Not Working

**Check:**
1. Doors/pistons in same claim
2. Redstone crosses claim border
3. Adjacent claim owner

**Solutions:**
- Keep redstone within single claim
- Or get both claims owned by same town

### Mobs Still Spawning

**Check:**
1. Town mob setting: `/town mobs`
2. Global config: `mob-spawning`
3. Spawner vs natural spawn

**Note:** Spawners not affected by protection.

## Events Protected

Full list of protected events:

**Block Events:**
- BlockBreakEvent
- BlockPlaceEvent
- BlockIgniteEvent
- BlockBurnEvent
- BlockExplodeEvent
- BlockSpreadEvent

**Entity Events:**
- EntityDamageByEntityEvent
- EntityExplodeEvent
- EntitySpawnEvent
- EntityChangeBlockEvent
- EntityInteractEvent

**Player Events:**
- PlayerInteractEvent
- PlayerInteractEntityEvent
- PlayerBucketEmptyEvent
- PlayerBucketFillEvent

**Hanging Events:**
- HangingBreakEvent
- HangingPlaceEvent

**Vehicle Events:**
- VehicleDamageEvent
- VehicleDestroyEvent

## Permissions

```
interim.admin.bypass           # Bypass all protection
interim.town.build             # Build in own town
interim.plot.type              # Change plot types
```

See [Permissions Reference](../admin/permissions.md).

## See Also

- [Claims](claims.md) - How to claim land
- [Towns](towns.md) - Town management
- [Plot Commands](../commands/plot.md) - Plot management
- [Configuration](../getting-started/configuration.md) - Protection settings
- [Troubleshooting](../admin/troubleshooting.md)
