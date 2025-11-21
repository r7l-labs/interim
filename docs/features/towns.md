# Towns

Towns are the foundation of Interim. They allow players to claim land, protect their builds, and collaborate with others.

## Overview

A **town** is a group of players who share protected territory (claims). Each town has:

- **Mayor** - The founder and leader
- **Assistants** - Trusted players who can help manage
- **Residents** - Regular members
- **Claims** - Protected chunks of land
- **Bank** - Shared economy (if Vault is installed)
- **Settings** - Customizable behavior

## Creating a Town

### Requirements

- Permission: `interim.town.create`
- Economy cost (if enabled): Configurable in `config.yml`
- Must be standing in an unclaimed chunk

### Command

```
/town create <name>
```

**Example:**
```
/town create Riverside
```

This creates the town and automatically claims your first chunk.

### Town Names

- Must be unique
- Alphanumeric characters only
- 3-32 characters long
- Cannot be changed later

## Town Management

### Mayor Commands

As the mayor, you have full control:

```
/town invite <player>          # Invite a player
/town kick <player>            # Remove a resident
/town promote <player>         # Make assistant
/town demote <player>          # Remove assistant rank
/town setspawn                 # Set town spawn point
/town delete                   # Delete the town permanently
```

### Assistant Commands

Assistants can help manage the town:

```
/town invite <player>          # Invite players
/town kick <player>            # Remove residents
/town claim                    # Claim chunks
/town unclaim                  # Unclaim chunks
/town setspawn                 # Change spawn point
```

### Resident Commands

All residents can:

```
/town                          # Open town menu
/town spawn                    # Teleport to town spawn
/town leave                    # Leave the town
/town deposit <amount>         # Deposit to town bank
```

## Claims

### Claiming Land

Stand in an adjacent unclaimed chunk:

```
/town claim
```

**Rules:**
- Must be adjacent to existing claims (configurable)
- Costs money per chunk (if economy enabled)
- Cannot claim in disabled worlds
- Must respect minimum distance from other towns

### Viewing Claims

See nearby claims on a map:

```
/map
```

Particle borders will show:
- **Your town** - Your configured color
- **Your nation** - Nation color
- **Other towns** - Their colors
- **Wilderness** - No particles

### Unclaiming Land

Stand in a claimed chunk:

```
/town unclaim
```

**Note:** You cannot unclaim your last chunk. Delete the town instead.

### Claim Types

Each chunk can have a type that changes its behavior:

#### Normal (Default)
- Standard protection
- Only town members can build
- Respects town PvP setting

#### Embassy
- Allows other towns to claim within your nation
- Used for diplomacy and trade

#### Arena
- PvP always enabled
- No protection (anyone can break blocks)
- Useful for town events

#### Shop
- Public build access
- Anyone can place/break blocks
- Containers still protected

**Set claim type:**
```
/plot type <type>
```

## Town Settings

### GUI Menu

Open the interactive menu:

```
/town
```

Click items to toggle settings:
- PvP enabled/disabled
- Explosions enabled/disabled
- Mob spawning enabled/disabled
- Open town (public join)
- Public build access

### Command-Based Settings

Or use commands directly:

```
/town pvp <on|off>             # Toggle PvP
/town explosions <on|off>      # Toggle explosion damage
/town mobs <on|off>            # Toggle mob spawning
/town open <true|false>        # Toggle open join
/town public <true|false>      # Toggle public build
```

### Board Message

Set a message shown when players enter your town:

```
/town board <message>
```

**Example:**
```
/town board Welcome to Riverside! Home of the finest fishermen.
```

## Town Spawn

### Setting the Spawn

Stand where you want the spawn point:

```
/town setspawn
```

**Requirements:**
- Must be in your town's claims
- Mayor or assistant rank

### Using the Spawn

Teleport to your town spawn:

```
/town spawn
```

**Cooldown:** Configurable in `config.yml`

## Town Colors

### Setting Town Color

Set the color shown on maps:

```
/town color <hex>
```

**Example:**
```
/town color #00FF00    # Green
```

**Admin override:**
```
/interimadmin towncolor <town> <hex>
```

### Color Inheritance

If your town is in a nation, color priority:

1. Town-specific color (if set)
2. Nation color (if town is in nation)
3. Default color (if neither set)

## Town Economy

### Town Bank

If Vault is enabled, towns have a shared bank:

```
/town deposit <amount>         # Anyone can deposit
/town withdraw <amount>        # Mayor only
/town balance                  # Check balance
```

### Uses of Town Bank

- Pay for new claims
- Save for nation creation
- Fund town projects
- Pay war costs

### Interest

Earn interest on your balance (if enabled):

```yaml
economy:
  interest-rate: 0.01  # 1% per day
```

## Resident Management

### Viewing Residents

See all town members:

```
/town residents
```

Shows:
- Mayor (⭐)
- Assistants (✦)
- Residents (•)

### Promoting Players

Give assistant permissions:

```
/town promote <player>
```

**Requirements:**
- Must be mayor
- Player must be a resident

### Demoting Players

Remove assistant rank:

```
/town demote <player>
```

**Requirements:**
- Must be mayor
- Player must be an assistant

### Kicking Players

Remove a player from the town:

```
/town kick <player>
```

**Requirements:**
- Mayor or assistant rank
- Cannot kick the mayor
- Assistants cannot kick other assistants

## Invitations

### Sending Invites

Invite a player to your town:

```
/town invite <player>
```

**Requirements:**
- Mayor or assistant rank
- Player must not be in another town

### Accepting Invites

Players can view their invites:

```
/town invites
```

And accept:

```
/town join <townname>
```

### Invite Expiration

Invites expire after 7 days (configurable).

## Joining a Nation

### Creating a Nation

See [Nations Guide](nations.md) for details:

```
/nation create <name>
```

**Requirements:**
- Must be a town mayor
- Economy cost (if enabled)

### Accepting Nation Invites

If invited to a nation:

```
/nation join <nationname>
```

**Requirements:**
- Must be town mayor

### Leaving a Nation

Exit your current nation:

```
/nation leave
```

**Requirements:**
- Must be town mayor
- Cannot leave if you're the capital (must disband nation instead)

## Protection

### What's Protected

In your town claims:

- Block breaking/placing
- Container access (chests, furnaces, etc.)
- Entity damage (item frames, armor stands, etc.)
- Door/button usage
- Crop trampling
- Vehicle usage

### Who Can Build

**By default:**
- Mayor ✓
- Assistants ✓
- Residents ✓
- Outsiders ✗

**With public build enabled:**
- Everyone ✓

**In plot types:**
- **Shop plots** - Everyone can build
- **Arena plots** - No protection at all

### PvP Rules

PvP in claims follows:

1. Town PvP setting (on/off)
2. War status (always on during war)
3. Arena plots (always on)

**In wilderness:**
- Controlled by `wilderness-pvp` config setting

## Advanced Features

### Town Statistics

View detailed town info:

```
/town info <townname>
```

Shows:
- Resident count
- Claim count
- Bank balance
- Founded date
- Nation membership

### Renaming Towns

Towns cannot be renamed. You must:

1. Create a new town with desired name
2. Migrate residents
3. Reclaim territory
4. Delete old town

### Merging Towns

Not directly supported. To merge:

1. Have all residents leave Town B
2. Mayor of Town A invites them
3. Delete empty Town B
4. Reclaim lost territory in Town A

### Town Allies

Towns automatically ally with:

- Other towns in their nation
- Towns in allied nations

## Deletion

### Deleting Your Town

As mayor:

```
/town delete
```

**Consequences:**
- All claims are unclaimed
- All residents lose membership
- Town bank is refunded to mayor
- Cannot be undone

**Requirements:**
- Must be mayor
- Town must not be a nation capital

### Forced Deletion

Admins can force delete:

```
/interimadmin deletetown <townname>
```

## Configuration

Key config settings for towns:

```yaml
town:
  creation-cost: 0.0            # Cost to create
  claim-cost: 100.0             # Cost per claim
  min-distance: 5               # Chunks between towns
  max-claims: -1                # Claim limit (-1 = unlimited)
  
  defaults:
    pvp: false
    explosions: false
    mob-spawning: true
    open: false
    public: false
```

See [Configuration Guide](../getting-started/configuration.md).

## Permissions

### Player Permissions

```
interim.town.create            # Create towns
interim.town.claim             # Claim land
interim.town.invite            # Invite players
```

### Admin Permissions

```
interim.admin.town.delete      # Force delete towns
interim.admin.town.color       # Change any town color
```

See [Permissions Reference](../admin/permissions.md).

## Placeholders

Use town data in other plugins:

```
%interim_town_name%            # Town name
%interim_town_residents_count% # Member count
%interim_town_claims_count%    # Total claims
%interim_town_bank%            # Bank balance
```

See [PlaceholderAPI Integration](../integrations/placeholderapi.md).

## Troubleshooting

### "Cannot claim here - too close to another town"

- Check `min-distance` in config
- Use `/map` to see nearby claims
- Admin can override with `/interimadmin`

### "Insufficient funds"

- Check claim cost: `/town balance`
- Deposit more: `/town deposit <amount>`
- Admin can adjust costs in config

### "Claim must be adjacent"

- Claims must touch existing claims
- Disable: `claims.require-adjacent: false` in config
- Or manually set claim with admin commands

## See Also

- [Nations](nations.md) - Form multi-town alliances
- [Claims](claims.md) - Detailed claiming mechanics
- [Protection](protection.md) - How protection works
- [Town Commands](../commands/town.md) - Full command list
- [Economy](economy.md) - Banking and costs
