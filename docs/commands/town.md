# Town Commands

Complete reference for all town-related commands.

## Command Overview

| Command | Description | Permission |
|---------|-------------|------------|
| `/town` | Open town menu GUI | `interim.town.menu` |
| `/town create <name>` | Create a new town | `interim.town.create` |
| `/town delete` | Delete your town | `interim.town.delete` |
| `/town claim` | Claim current chunk | `interim.town.claim` |
| `/town unclaim` | Unclaim current chunk | `interim.town.unclaim` |
| `/town invite <player>` | Invite a player | `interim.town.invite` |
| `/town kick <player>` | Remove a resident | `interim.town.kick` |
| `/town join <town>` | Join a town | `interim.town.join` |
| `/town leave` | Leave your town | `interim.town.leave` |
| `/town spawn` | Teleport to town spawn | `interim.town.spawn` |
| `/town setspawn` | Set town spawn point | `interim.town.setspawn` |
| `/town info [town]` | View town information | `interim.town.info` |
| `/town list` | List all towns | `interim.town.list` |
| `/town promote <player>` | Make player assistant | `interim.town.promote` |
| `/town demote <player>` | Remove assistant rank | `interim.town.demote` |
| `/town deposit <amount>` | Deposit to town bank | `interim.town.deposit` |
| `/town withdraw <amount>` | Withdraw from bank | `interim.town.withdraw` |
| `/town balance` | Check town balance | `interim.town.balance` |
| `/town pvp <on\|off>` | Toggle town PvP | `interim.town.settings` |
| `/town explosions <on\|off>` | Toggle explosions | `interim.town.settings` |
| `/town mobs <on\|off>` | Toggle mob spawning | `interim.town.settings` |
| `/town open <true\|false>` | Toggle open join | `interim.town.settings` |
| `/town public <true\|false>` | Toggle public build | `interim.town.settings` |
| `/town board <message>` | Set town message | `interim.town.board` |
| `/town color <hex>` | Set town color | `interim.town.color` |
| `/town residents` | List town residents | `interim.town.residents` |
| `/town claims` | List town claims | `interim.town.claims` |
| `/town invites` | View your invites | `interim.town.invites` |

## Detailed Commands

### `/town`

Opens the interactive town menu GUI.

**Permission:** `interim.town.menu`

**Usage:**
```
/town
```

**GUI Features:**
- View town information
- Manage settings (PvP, explosions, mobs)
- View residents
- Access economy features
- Quick navigation

---

### `/town create <name>`

Create a new town and automatically claim your first chunk.

**Permission:** `interim.town.create`

**Usage:**
```
/town create <name>
```

**Arguments:**
- `<name>` - Town name (3-32 alphanumeric characters)

**Requirements:**
- Not already in a town
- Standing in unclaimed chunk
- Sufficient funds (if economy enabled)

**Example:**
```
/town create Riverside
/town create NewHeaven
```

**Cost:** Configurable in `config.yml` (`town.creation-cost`)

---

### `/town delete`

Permanently delete your town.

**Permission:** `interim.town.delete`

**Usage:**
```
/town delete
```

**Requirements:**
- Must be town mayor
- Town cannot be a nation capital

**Consequences:**
- All claims are unclaimed
- All residents lose membership
- Town bank refunded to mayor
- **Cannot be undone**

**Confirmation:** Opens confirmation GUI.

---

### `/town claim`

Claim the chunk you're currently standing in.

**Permission:** `interim.town.claim`

**Usage:**
```
/town claim
```

**Requirements:**
- Member of a town (mayor/assistant/with permission)
- Standing in unclaimed chunk
- Chunk is adjacent to existing claims (unless disabled)
- Sufficient funds (if economy enabled)
- Within claim limit

**Cost:** Configurable (`town.claim-cost` per chunk)

**See:** [Claims Guide](../features/claims.md)

---

### `/town unclaim`

Release the chunk you're standing in.

**Permission:** `interim.town.unclaim`

**Usage:**
```
/town unclaim
```

**Requirements:**
- Mayor or assistant rank
- Standing in your town's claim
- Cannot unclaim last chunk (delete town instead)

**Note:** No refund by default.

---

### `/town invite <player>`

Invite a player to join your town.

**Permission:** `interim.town.invite`

**Usage:**
```
/town invite <player>
```

**Arguments:**
- `<player>` - Player name

**Requirements:**
- Mayor or assistant rank
- Target player not in another town
- Player must be online

**Example:**
```
/town invite Steve
```

**Expiration:** Invites expire after 7 days.

---

### `/town kick <player>`

Remove a player from your town.

**Permission:** `interim.town.kick`

**Usage:**
```
/town kick <player>
```

**Arguments:**
- `<player>` - Resident name

**Requirements:**
- Mayor or assistant rank
- Cannot kick the mayor
- Assistants cannot kick other assistants

**Example:**
```
/town kick Griefer123
```

---

### `/town join <town>`

Accept an invitation to join a town.

**Permission:** `interim.town.join`

**Usage:**
```
/town join <town>
```

**Arguments:**
- `<town>` - Town name

**Requirements:**
- Must have an active invite from that town
- Not currently in another town

**Example:**
```
/town join Riverside
```

---

### `/town leave`

Leave your current town.

**Permission:** `interim.town.leave`

**Usage:**
```
/town leave
```

**Requirements:**
- Must be in a town
- Cannot leave if you're the mayor

**Note:** Mayors must delete the town or transfer leadership.

---

### `/town spawn`

Teleport to your town's spawn point.

**Permission:** `interim.town.spawn`

**Usage:**
```
/town spawn
```

**Requirements:**
- Member of a town
- Town has set a spawn point

**Cooldown:** Configurable

---

### `/town setspawn`

Set the town spawn point at your current location.

**Permission:** `interim.town.setspawn`

**Usage:**
```
/town setspawn
```

**Requirements:**
- Mayor or assistant rank
- Standing in your town's claims

---

### `/town info [town]`

View detailed information about a town.

**Permission:** `interim.town.info`

**Usage:**
```
/town info           # Your town
/town info <name>    # Another town
```

**Displays:**
- Mayor and assistants
- Resident count
- Claim count
- Bank balance (if Vault enabled)
- Nation membership
- Founded date
- Settings (PvP, explosions, etc.)

**Example:**
```
/town info
/town info Riverside
```

---

### `/town list`

List all towns on the server.

**Permission:** `interim.town.list`

**Usage:**
```
/town list
```

**Displays:**
- Town names
- Resident counts
- Nation membership

**Format:** Opens GUI with clickable town list.

---

### `/town promote <player>`

Promote a resident to assistant rank.

**Permission:** `interim.town.promote`

**Usage:**
```
/town promote <player>
```

**Arguments:**
- `<player>` - Resident name

**Requirements:**
- Must be town mayor
- Player must be a resident

**Example:**
```
/town promote Steve
```

---

### `/town demote <player>`

Remove assistant rank from a player.

**Permission:** `interim.town.demote`

**Usage:**
```
/town demote <player>
```

**Arguments:**
- `<player>` - Assistant name

**Requirements:**
- Must be town mayor
- Player must be an assistant

**Example:**
```
/town demote Steve
```

---

### `/town deposit <amount>`

Deposit money into the town bank.

**Permission:** `interim.town.deposit`

**Usage:**
```
/town deposit <amount>
```

**Arguments:**
- `<amount>` - Amount to deposit

**Requirements:**
- Vault and economy plugin installed
- Sufficient balance

**Example:**
```
/town deposit 1000
/town deposit 500.50
```

---

### `/town withdraw <amount>`

Withdraw money from the town bank.

**Permission:** `interim.town.withdraw`

**Usage:**
```
/town withdraw <amount>
```

**Arguments:**
- `<amount>` - Amount to withdraw

**Requirements:**
- Must be town mayor
- Town bank has sufficient funds

**Example:**
```
/town withdraw 500
```

---

### `/town balance`

Check the town bank balance.

**Permission:** `interim.town.balance`

**Usage:**
```
/town balance
```

**Displays:** Current town bank balance.

---

### `/town pvp <on|off>`

Toggle PvP in your town's claims.

**Permission:** `interim.town.settings`

**Usage:**
```
/town pvp on
/town pvp off
```

**Requirements:**
- Mayor or assistant rank

**Default:** Off (no PvP)

---

### `/town explosions <on|off>`

Toggle explosion damage in claims.

**Permission:** `interim.town.settings`

**Usage:**
```
/town explosions on
/town explosions off
```

**Requirements:**
- Mayor or assistant rank

**Default:** Off (no explosion damage)

---

### `/town mobs <on|off>`

Toggle mob spawning in claims.

**Permission:** `interim.town.settings`

**Usage:**
```
/town mobs on
/town mobs off
```

**Requirements:**
- Mayor or assistant rank

**Default:** On (mobs spawn normally)

---

### `/town open <true|false>`

Toggle whether anyone can join the town.

**Permission:** `interim.town.settings`

**Usage:**
```
/town open true
/town open false
```

**Requirements:**
- Mayor or assistant rank

**Default:** False (invite-only)

---

### `/town public <true|false>`

Toggle public build access in claims.

**Permission:** `interim.town.settings`

**Usage:**
```
/town public true
/town public false
```

**Requirements:**
- Mayor or assistant rank

**Default:** False (residents only)

---

### `/town board <message>`

Set the message displayed when players enter your town.

**Permission:** `interim.town.board`

**Usage:**
```
/town board <message>
```

**Arguments:**
- `<message>` - Any text (supports color codes with &)

**Requirements:**
- Mayor or assistant rank

**Example:**
```
/town board Welcome to Riverside!
/town board &6Golden Town &7- Trade District
```

---

### `/town color <hex>`

Set the town's color on maps.

**Permission:** `interim.town.color`

**Usage:**
```
/town color <hex>
```

**Arguments:**
- `<hex>` - Hex color code (e.g., #FF0000)

**Requirements:**
- Mayor or assistant rank

**Example:**
```
/town color #00FF00    # Green
/town color #FF0000    # Red
/town color #0000FF    # Blue
```

---

### `/town residents`

View list of all town residents.

**Permission:** `interim.town.residents`

**Usage:**
```
/town residents
```

**Displays:**
- Mayor (⭐)
- Assistants (✦)
- Residents (•)
- Online status

---

### `/town claims`

View list of all town claims.

**Permission:** `interim.town.claims`

**Usage:**
```
/town claims
```

**Displays:**
- World name
- Chunk coordinates
- Plot type

---

### `/town invites`

View all your pending town invitations.

**Permission:** `interim.town.invites`

**Usage:**
```
/town invites
```

**Displays:**
- Town names
- Invite timestamps
- Accept/decline buttons

---

## Permission Nodes

### Player Permissions

```yaml
interim.town.menu: true          # Open town menu
interim.town.create: true        # Create towns
interim.town.delete: true        # Delete own town
interim.town.claim: true         # Claim land
interim.town.unclaim: true       # Unclaim land
interim.town.invite: true        # Invite players
interim.town.kick: true          # Kick residents
interim.town.join: true          # Join towns
interim.town.leave: true         # Leave town
interim.town.spawn: true         # Use town spawn
interim.town.setspawn: true      # Set spawn point
interim.town.info: true          # View town info
interim.town.list: true          # List towns
interim.town.promote: true       # Promote residents
interim.town.demote: true        # Demote assistants
interim.town.deposit: true       # Deposit money
interim.town.withdraw: true      # Withdraw money
interim.town.balance: true       # Check balance
interim.town.settings: true      # Change settings
interim.town.board: true         # Set board message
interim.town.color: true         # Set town color
interim.town.residents: true     # View residents
interim.town.claims: true        # View claims
interim.town.invites: true       # View invites
```

### Wildcard Permission

Grant all town permissions:

```yaml
interim.town.*: true
```

## See Also

- [Towns Feature Guide](../features/towns.md)
- [Nation Commands](nation.md)
- [Plot Commands](plot.md)
- [Admin Commands](admin.md)
- [Permissions Reference](../admin/permissions.md)
