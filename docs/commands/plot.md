# Plot Commands

Complete reference for plot (claim) management commands.

## Command Overview

| Command | Description | Permission |
|---------|-------------|------------|
| `/plot` | Open plot menu GUI | `interim.plot.menu` |
| `/plot info` | View current plot details | `interim.plot.info` |
| `/plot type <type>` | Change plot type | `interim.plot.type` |

## Detailed Commands

### `/plot`

Opens the interactive plot management GUI for the chunk you're standing in.

**Permission:** `interim.plot.menu`

**Usage:**
```
/plot
```

**Requirements:**
- Standing in a claimed chunk

**GUI Features:**
- View plot information
- Change plot type
- View permissions
- See owner town

---

### `/plot info`

Display detailed information about the current plot.

**Permission:** `interim.plot.info`

**Usage:**
```
/plot info
```

**Requirements:**
- Standing in a claimed chunk

**Displays:**
- Town owner
- Plot type (Normal, Embassy, Arena, Shop)
- Coordinates
- World name
- Protection settings

**Example Output:**
```
Plot Information:
Town: Riverside
Type: Normal
Location: world @ -5, 10
Protection: Full
PvP: Disabled
```

---

### `/plot type <type>`

Change the plot type of the current chunk.

**Permission:** `interim.plot.type`

**Usage:**
```
/plot type <type>
```

**Arguments:**
- `<type>` - One of: `normal`, `embassy`, `arena`, `shop`

**Requirements:**
- Mayor or assistant rank
- Standing in your town's claim

**Plot Types:**

#### Normal
Standard protection:
```
/plot type normal
```
- Only town members can build
- Full protection enabled
- Respects town PvP setting

#### Embassy
Diplomatic plot:
```
/plot type embassy
```
- Allows foreign towns to claim (in nation territory)
- Used for embassies and trade posts
- Town must be in a nation

#### Arena
PvP zone:
```
/plot type arena
```
- PvP always enabled
- No block protection
- No entity protection
- Good for combat areas

#### Shop
Public building:
```
/plot type shop
```
- Anyone can place/break blocks
- Containers still protected
- Ideal for market districts

**Example:**
```
/plot type shop      # Make this plot a shop
/plot type arena     # Make this plot an arena
/plot type normal    # Return to normal protection
```

---

## Plot Types Explained

### Normal Plot (Default)

**Protection:**
- ✓ Block breaking/placing
- ✓ Container access
- ✓ Entity damage
- ✓ Door/button usage

**Who can build:**
- Town members only

**PvP:**
- Follows town setting

**Use cases:**
- Residential areas
- Private builds
- Town infrastructure

---

### Embassy Plot

**Protection:**
- ✓ Normal protection until foreign town claims
- Allows claiming by other nation members

**Requirements:**
- Town must be in a nation

**Who can build:**
- Town members
- Foreign claimants (if they claim it)

**PvP:**
- Follows claimant's town setting

**Use cases:**
- Diplomatic missions
- Inter-nation cooperation
- Cultural exchange centers

---

### Arena Plot

**Protection:**
- ✗ No block protection
- ✗ No entity protection
- ✗ No container protection

**Who can build:**
- Everyone

**PvP:**
- Always enabled

**Use cases:**
- PvP arenas
- Combat tournaments
- Town events

---

### Shop Plot

**Protection:**
- ✗ Block breaking/placing open to all
- ✓ Container access protected
- ✓ Entity damage protected

**Who can build:**
- Everyone (blocks only)
- Containers protected

**PvP:**
- Follows town setting

**Use cases:**
- Player shops
- Market districts
- Trade centers
- Public builds

---

## Related Commands

### Claiming Plots

Claim chunks with town commands:

```
/town claim          # Claim current chunk
/town unclaim        # Unclaim current chunk
```

See [Town Commands](town.md) for details.

### Viewing Plots

See claim map:

```
/map                 # Show nearby claims with particles
```

See [Map Commands](map.md) for details.

---

## Permission Nodes

### Player Permissions

```yaml
interim.plot.menu: true          # Open plot menu
interim.plot.info: true          # View plot info
interim.plot.type: true          # Change plot type
```

### Wildcard Permission

Grant all plot permissions:

```yaml
interim.plot.*: true
```

---

## Configuration

Plot behavior is configured globally:

```yaml
protection:
  # Default protection settings
  block-break: true
  block-place: true
  container-access: true
  entity-damage: true
  pvp: false
  
  # Build permissions
  resident-build: true
  nation-build: false
  ally-build: false
```

See [Configuration Guide](../getting-started/configuration.md).

---

## Troubleshooting

### "You don't have permission to change plot type"

**Causes:**
1. Not mayor or assistant
2. Not in your town's claim
3. Missing permission node

**Solutions:**
- Ask your mayor for promotion
- Verify you're in your town's claim: `/plot info`
- Check permissions: `interim.plot.type`

### "Embassy plots only work in nation territory"

**Cause:**
- Town not in a nation

**Solution:**
- Join or create a nation first
- See [Nations Guide](../features/nations.md)

### "Cannot change plot type - standing in wilderness"

**Cause:**
- Current chunk is unclaimed

**Solution:**
- Claim it first: `/town claim`
- Or move to a claimed chunk

---

## Examples

### Setting Up a Shop District

1. Claim the area:
```
/town claim
/town claim
/town claim
```

2. Convert to shop plots:
```
/plot type shop
```
Move to next chunk and repeat.

3. Result: Public market where anyone can build shops.

---

### Creating a PvP Arena

1. Claim the arena area:
```
/town claim
```

2. Convert to arena:
```
/plot type arena
```

3. Result: PvP enabled zone with no protection.

---

### Embassy Setup

1. Ensure town is in nation:
```
/nation info
```

2. Claim embassy district:
```
/town claim
```

3. Mark as embassy:
```
/plot type embassy
```

4. Foreign towns can now claim here.

---

## See Also

- [Claims Feature Guide](../features/claims.md)
- [Protection System](../features/protection.md)
- [Town Commands](town.md)
- [Map Commands](map.md)
- [Permissions Reference](../admin/permissions.md)
