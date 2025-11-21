# Nation Commands

Complete reference for all nation-related commands.

## Command Overview

| Command | Description | Permission |
|---------|-------------|------------|
| `/nation` | Open nation menu GUI | `interim.nation.menu` |
| `/nation create <name>` | Create a nation | `interim.nation.create` |
| `/nation delete` | Disband the nation | `interim.nation.delete` |
| `/nation invite <town>` | Invite a town | `interim.nation.invite` |
| `/nation kick <town>` | Remove a town | `interim.nation.kick` |
| `/nation join <nation>` | Join a nation | `interim.nation.join` |
| `/nation leave` | Leave your nation | `interim.nation.leave` |
| `/nation info [nation]` | View nation info | `interim.nation.info` |
| `/nation list` | List all nations | `interim.nation.list` |
| `/nation promote <player>` | Make mayor assistant | `interim.nation.promote` |
| `/nation demote <player>` | Remove assistant rank | `interim.nation.demote` |
| `/nation deposit <amount>` | Deposit to bank | `interim.nation.deposit` |
| `/nation withdraw <amount>` | Withdraw from bank | `interim.nation.withdraw` |
| `/nation balance` | Check nation balance | `interim.nation.balance` |
| `/nation ally <nation>` | Propose alliance | `interim.nation.ally` |
| `/nation enemy <nation>` | Declare enemy | `interim.nation.enemy` |
| `/nation neutral <nation>` | Return to neutral | `interim.nation.neutral` |
| `/nation war <nation>` | Declare war | `interim.nation.war` |
| `/nation peace <nation>` | Propose peace | `interim.nation.peace` |
| `/nation board <message>` | Set nation message | `interim.nation.board` |
| `/nation color <hex>` | Set nation color | `interim.nation.color` |
| `/nation towns` | List member towns | `interim.nation.towns` |
| `/nation capital <town>` | Change capital | `interim.nation.capital` |
| `/nation invites` | View nation invites | `interim.nation.invites` |

## Detailed Commands

### `/nation`

Opens the interactive nation menu GUI.

**Permission:** `interim.nation.menu`

**Usage:**
```
/nation
```

**Requirements:**
- Your town must be in a nation

**GUI Features:**
- View nation information
- Manage diplomacy
- Access economy
- View wars

---

### `/nation create <name>`

Create a new nation with your town as capital.

**Permission:** `interim.nation.create`

**Usage:**
```
/nation create <name>
```

**Arguments:**
- `<name>` - Nation name (3-32 alphanumeric characters)

**Requirements:**
- Must be a town mayor
- Town not in another nation
- Sufficient funds (if economy enabled)

**Example:**
```
/nation create TheEmpire
/nation create Federation
```

**Cost:** Configurable (`nation.creation-cost`)

---

### `/nation delete`

Permanently disband your nation.

**Permission:** `interim.nation.delete`

**Usage:**
```
/nation delete
```

**Requirements:**
- Must be nation leader

**Consequences:**
- All towns become independent
- Nation bank refunded to leader
- All alliances/wars end
- **Cannot be undone**

**Confirmation:** Opens confirmation GUI.

---

### `/nation invite <town>`

Invite a town to join your nation.

**Permission:** `interim.nation.invite`

**Usage:**
```
/nation invite <town>
```

**Arguments:**
- `<town>` - Town name

**Requirements:**
- Leader or assistant rank
- Target town not in another nation
- Town must have a mayor

**Example:**
```
/nation invite Riverside
```

---

### `/nation kick <town>`

Remove a town from your nation.

**Permission:** `interim.nation.kick`

**Usage:**
```
/nation kick <town>
```

**Arguments:**
- `<town>` - Town name

**Requirements:**
- Leader or assistant rank
- Cannot kick the capital
- Assistants cannot kick other assistant's towns

**Example:**
```
/nation kick RebelTown
```

---

### `/nation join <nation>`

Accept an invitation to join a nation.

**Permission:** `interim.nation.join`

**Usage:**
```
/nation join <nation>
```

**Arguments:**
- `<nation>` - Nation name

**Requirements:**
- Must be town mayor
- Have active invite from that nation
- Town not in another nation

**Example:**
```
/nation join TheEmpire
```

---

### `/nation leave`

Leave your current nation.

**Permission:** `interim.nation.leave`

**Usage:**
```
/nation leave
```

**Requirements:**
- Must be town mayor
- Town cannot be the capital

**Note:** Capital must disband the nation instead.

---

### `/nation info [nation]`

View detailed nation information.

**Permission:** `interim.nation.info`

**Usage:**
```
/nation info           # Your nation
/nation info <name>    # Another nation
```

**Displays:**
- Leader and capital
- Member towns count
- Total residents
- Total claims
- Bank balance
- Allies and enemies
- Founded date

**Example:**
```
/nation info
/nation info TheEmpire
```

---

### `/nation list`

List all nations on the server.

**Permission:** `interim.nation.list`

**Usage:**
```
/nation list
```

**Displays:**
- Nation names
- Town counts
- Total residents

**Format:** Opens GUI with clickable list.

---

### `/nation promote <player>`

Promote a town mayor to nation assistant.

**Permission:** `interim.nation.promote`

**Usage:**
```
/nation promote <player>
```

**Arguments:**
- `<player>` - Mayor name

**Requirements:**
- Must be nation leader
- Player must be mayor of member town

**Example:**
```
/nation promote Steve
```

---

### `/nation demote <player>`

Remove nation assistant rank.

**Permission:** `interim.nation.demote`

**Usage:**
```
/nation demote <player>
```

**Arguments:**
- `<player>` - Assistant name

**Requirements:**
- Must be nation leader
- Player must be nation assistant

**Example:**
```
/nation demote Steve
```

---

### `/nation deposit <amount>`

Deposit money into nation bank.

**Permission:** `interim.nation.deposit`

**Usage:**
```
/nation deposit <amount>
```

**Arguments:**
- `<amount>` - Amount to deposit

**Requirements:**
- Vault and economy enabled
- Sufficient balance

**Example:**
```
/nation deposit 5000
```

---

### `/nation withdraw <amount>`

Withdraw money from nation bank.

**Permission:** `interim.nation.withdraw`

**Usage:**
```
/nation withdraw <amount>
```

**Arguments:**
- `<amount>` - Amount to withdraw

**Requirements:**
- Must be nation leader
- Sufficient funds in bank

**Example:**
```
/nation withdraw 1000
```

---

### `/nation balance`

Check nation bank balance.

**Permission:** `interim.nation.balance`

**Usage:**
```
/nation balance
```

**Displays:** Current nation bank balance.

---

### `/nation ally <nation>`

Propose or accept an alliance.

**Permission:** `interim.nation.ally`

**Usage:**
```
/nation ally <nation>
```

**Arguments:**
- `<nation>` - Target nation name

**Requirements:**
- Leader or assistant rank
- Target not an enemy
- Both nations must agree

**Example:**
```
/nation ally FriendlyNation
```

**Note:** Both sides must run this command to form alliance.

---

### `/nation enemy <nation>`

Declare another nation as enemy.

**Permission:** `interim.nation.enemy`

**Usage:**
```
/nation enemy <nation>
```

**Arguments:**
- `<nation>` - Target nation name

**Requirements:**
- Leader or assistant rank

**Example:**
```
/nation enemy EvilEmpire
```

---

### `/nation neutral <nation>`

Return to neutral status with a nation.

**Permission:** `interim.nation.neutral`

**Usage:**
```
/nation neutral <nation>
```

**Arguments:**
- `<nation>` - Target nation name

**Requirements:**
- Leader or assistant rank
- Currently allied or enemy with target

**Example:**
```
/nation neutral FormerAlly
```

---

### `/nation war <nation>`

Declare war on another nation.

**Permission:** `interim.nation.war`

**Usage:**
```
/nation war <nation>
```

**Arguments:**
- `<nation>` - Target nation name

**Requirements:**
- Leader or assistant rank
- War system enabled
- Sufficient funds for declaration cost
- Not on war cooldown

**Example:**
```
/nation war EnemyNation
```

**See:** [War System](../features/wars.md)

---

### `/nation peace <nation>`

Propose peace to end a war.

**Permission:** `interim.nation.peace`

**Usage:**
```
/nation peace <nation>
```

**Arguments:**
- `<nation>` - Nation you're at war with

**Requirements:**
- Leader or assistant rank
- Currently at war with target
- Both sides must agree

**Example:**
```
/nation peace FormerEnemy
```

---

### `/nation board <message>`

Set nation message displayed in territory.

**Permission:** `interim.nation.board`

**Usage:**
```
/nation board <message>
```

**Arguments:**
- `<message>` - Any text (supports color codes)

**Requirements:**
- Leader or assistant rank

**Example:**
```
/nation board Glory to The Empire!
/nation board &6United we stand!
```

---

### `/nation color <hex>`

Set nation color on maps.

**Permission:** `interim.nation.color`

**Usage:**
```
/nation color <hex>
```

**Arguments:**
- `<hex>` - Hex color code

**Requirements:**
- Leader or assistant rank

**Example:**
```
/nation color #FF0000    # Red
/nation color #00FF00    # Green
```

---

### `/nation towns`

List all member towns in the nation.

**Permission:** `interim.nation.towns`

**Usage:**
```
/nation towns
```

**Displays:**
- Capital (⭐)
- Assistant towns (✦)
- Member towns (•)
- Resident counts

---

### `/nation capital <town>`

Transfer capital status to another town.

**Permission:** `interim.nation.capital`

**Usage:**
```
/nation capital <town>
```

**Arguments:**
- `<town>` - Member town name

**Requirements:**
- Must be nation leader
- Target must be member town
- Feature enabled in config

**Note:** Transfers nation leadership to that town's mayor.

---

### `/nation invites`

View all pending nation invitations.

**Permission:** `interim.nation.invites`

**Usage:**
```
/nation invites
```

**Requirements:**
- Must be town mayor

**Displays:**
- Nation names
- Invite timestamps
- Accept/decline buttons

---

## Permission Nodes

### Player Permissions

```yaml
interim.nation.menu: true        # Open nation menu
interim.nation.create: true      # Create nations
interim.nation.delete: true      # Disband nation
interim.nation.invite: true      # Invite towns
interim.nation.kick: true        # Remove towns
interim.nation.join: true        # Join nations
interim.nation.leave: true       # Leave nation
interim.nation.info: true        # View info
interim.nation.list: true        # List nations
interim.nation.promote: true     # Promote mayors
interim.nation.demote: true      # Demote assistants
interim.nation.deposit: true     # Deposit money
interim.nation.withdraw: true    # Withdraw money
interim.nation.balance: true     # Check balance
interim.nation.ally: true        # Form alliances
interim.nation.enemy: true       # Declare enemies
interim.nation.neutral: true     # Return to neutral
interim.nation.war: true         # Declare war
interim.nation.peace: true       # Propose peace
interim.nation.board: true       # Set message
interim.nation.color: true       # Set color
interim.nation.towns: true       # List towns
interim.nation.capital: true     # Change capital
interim.nation.invites: true     # View invites
```

### Wildcard Permission

Grant all nation permissions:

```yaml
interim.nation.*: true
```

## See Also

- [Nations Feature Guide](../features/nations.md)
- [Town Commands](town.md)
- [War Commands](../features/wars.md)
- [Admin Commands](admin.md)
- [Permissions Reference](../admin/permissions.md)
