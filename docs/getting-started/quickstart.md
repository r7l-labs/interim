# Quick Start Guide

Get started with Interim in under 5 minutes!

## Creating Your First Town

### 1. Create a Town

Stand in the location where you want your town:

```
/town create MyTown
```

This creates a town and automatically claims the chunk you're standing in as your first claim.

### 2. Claim More Territory

Move to adjacent chunks and claim them:

```
/town claim
```

You can see chunk boundaries with:
```
/map
```

### 3. Invite Members

Invite other players to join your town:

```
/town invite <player>
```

They can accept with:
```
/town join MyTown
```

### 4. Set Town Settings

Configure your town's behavior:

```
/town                    # Opens the town menu GUI
```

Or use commands:
```
/town pvp on             # Enable PvP in your town
/town pvp off            # Disable PvP (default)
/town open true          # Allow anyone to join
/town explosions off     # Prevent explosions
```

### 5. Manage Your Land

View your claims on the map:
```
/map
```

Unclaim a chunk:
```
/town unclaim
```

View claim info:
```
/plot
```

## Building Your Town

### Set a Town Spawn

Stand where you want the spawn point:
```
/town setspawn
```

Players can teleport there with:
```
/town spawn
```

### Manage Residents

Promote players to assistant:
```
/town promote <player>
```

Demote assistants:
```
/town demote <player>
```

Kick members:
```
/town kick <player>
```

### Set Message Board

Add a message that shows when players enter your town:
```
/town board Welcome to MyTown!
```

## Creating a Nation

Once your town is established, form a nation!

### 1. Found a Nation

As a town mayor:
```
/nation create MyNation
```

Your town becomes the capital.

### 2. Invite Towns

Invite other towns to join:
```
/nation invite <townname>
```

Town mayors can accept with:
```
/nation join MyNation
```

### 3. Manage Nation

Set nation color for the map:
```
/nation color #FF0000
```

Set nation board message:
```
/nation board Glory to MyNation!
```

Promote a town mayor to assistant:
```
/nation promote <player>
```

## Diplomacy

### Forming Alliances

As a nation leader or assistant:
```
/nation ally <nationname>
```

Both nations must agree to form an alliance.

### Declaring War

To declare war on another nation:
```
/nation war <nationname>
```

See [War Tutorial](../tutorials/war_tutorial.md) for detailed war mechanics.

## Economy

If Vault is installed, towns and nations have banks:

### Town Banking
```
/town deposit 1000       # Deposit money
/town withdraw 500       # Withdraw money (mayors only)
/town balance            # Check town balance
```

### Nation Banking
```
/nation deposit 1000     # Deposit money
/nation withdraw 500     # Withdraw money (leaders only)
/nation balance          # Check nation balance
```

### Claiming Costs

- Town creation: Configurable (default: free)
- Claiming chunks: Per-chunk cost (default: 100)
- Nation creation: Configurable (default: 1000)

## Plot Management

### Plot Types

Chunks have different types:

- **Normal** - Standard territory, owner-controlled access
- **Embassy** - Other towns can claim in your nation
- **Arena** - PvP enabled, no protection
- **Shop** - Public build access for shops

### Set Plot Type

Stand in a claimed chunk:
```
/plot type embassy
```

### Plot Permissions

Control who can build in specific plots:
```
/plot                    # Opens plot menu
```

## Visualization

### Claim Boundaries

See chunk borders with particles:
```
/map
```

Press **F3 + G** in Minecraft to see chunk boundaries.

### BlueMap Integration

If BlueMap is installed, view your territories on the web map!

Access at: `http://yourserver:8100`

Towns and nations are color-coded and clickable.

## Tips for New Players

### Protection

- **Your claims are protected** - Only you and your residents can build
- **Set town PvP** - Control combat in your territory
- **Assistants can help** - Promote trusted players to manage the town

### Expansion

- **Claim strategically** - Claims must be adjacent (touching)
- **Budget for growth** - Save money for more claims
- **Plan your layout** - Claims are permanent once set

### Cooperation

- **Join a nation** - Strength in numbers!
- **Form alliances** - Work together with other nations
- **Trade resources** - Use shops in town claims

### Map Navigation

- **Use /map** - Shows nearby claims
- **Check town colors** - Each town has a unique color
- **Nation borders** - Nations group multiple towns

## Common Commands

| Command | Description |
|---------|-------------|
| `/town` | Open town menu |
| `/town create <name>` | Create a new town |
| `/town claim` | Claim current chunk |
| `/town invite <player>` | Invite a player |
| `/nation` | Open nation menu |
| `/nation create <name>` | Create a nation |
| `/map` | View claim map |
| `/plot` | Manage current plot |

## Next Steps

- **[Town Commands](../commands/town.md)** - Full command reference
- **[Nation Commands](../commands/nation.md)** - Nation management
- **[War Tutorial](../tutorials/war_tutorial.md)** - Learn warfare
- **[Configuration](configuration.md)** - Customize your server

## Getting Help

If you need assistance:

- Use `/town ?` for town help
- Use `/nation ?` for nation help
- Check [Troubleshooting](../admin/troubleshooting.md)
- Ask in your server's support channels
