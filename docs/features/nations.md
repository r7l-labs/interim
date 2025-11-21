# Nations

Nations are alliances of multiple towns working together. Form a nation to increase your power, coordinate with allies, and engage in diplomacy.

## Overview

A **nation** is a federation of towns with:

- **Leader** - The nation founder (must be a town mayor)
- **Assistants** - Other mayors who help manage
- **Capital** - The leader's town
- **Member Towns** - All towns in the nation
- **Bank** - Shared national treasury
- **Allies/Enemies** - Diplomatic relationships

## Creating a Nation

### Requirements

- Must be a town mayor
- Permission: `interim.nation.create`
- Economy cost (if enabled): Configurable in `config.yml`

### Command

```
/nation create <name>
```

**Example:**
```
/nation create TheEmpire
```

Your town becomes the capital automatically.

### Nation Names

- Must be unique
- Alphanumeric characters only
- 3-32 characters long
- Cannot be changed later

## Nation Management

### Leader Commands

As the nation leader, you control the nation:

```
/nation invite <town>          # Invite a town
/nation kick <town>            # Remove a town
/nation promote <player>       # Make another mayor assistant
/nation demote <player>        # Remove assistant rank
/nation enemy <nation>         # Declare enemy status
/nation neutral <nation>       # Return to neutral
/nation delete                 # Disband the nation
```

### Assistant Commands

Nation assistants (promoted mayors) can:

```
/nation invite <town>          # Invite towns
/nation kick <town>            # Remove towns
/nation ally <nation>          # Propose alliances
/nation enemy <nation>         # Declare enemies
```

### Member Commands

All nation members can:

```
/nation                        # Open nation menu
/nation info <nation>          # View nation details
/nation deposit <amount>       # Deposit to nation bank
/nation leave                  # Leave the nation (mayors only)
```

## Adding Towns

### Inviting Towns

Invite another town to join:

```
/nation invite <townname>
```

**Requirements:**
- Leader or assistant rank
- Target town must not be in another nation
- Target town must have a mayor

### Town Acceptance

The invited town's mayor can accept:

```
/nation join <nationname>
```

Or view all invites:

```
/nation invites
```

### Removing Towns

Kick a town from the nation:

```
/nation kick <townname>
```

**Requirements:**
- Leader or assistant rank
- Cannot kick the capital town
- Assistants cannot kick other assistant's towns

## Capital Management

### Capital Town

The **capital** is the leader's town:

- Cannot be removed from the nation
- Cannot leave voluntarily
- Only way to remove: disband the nation

### Changing Capital

Transfer capital status (if enabled):

```
/nation capital <townname>
```

**Requirements:**
- Must be nation leader
- Target must be a nation member town
- Transfers nation leadership to that town's mayor

## Nation Settings

### Board Message

Set a message shown when players enter nation territory:

```
/nation board <message>
```

**Example:**
```
/nation board Glory to The Empire! United we stand!
```

### Nation Colors

Set the color shown on maps for all member towns:

```
/nation color <hex>
```

**Example:**
```
/nation color #FF0000    # Red
```

**Admin override:**
```
/interimadmin nationcolor <nation> <hex>
```

**Color hierarchy:**
- Individual town color (highest priority)
- Nation color (if town doesn't have own color)
- Default color (if neither set)

## Diplomacy

### Alliances

#### Proposing an Alliance

Send an alliance request:

```
/nation ally <nationname>
```

**Requirements:**
- Leader or assistant rank
- Target must not be an enemy
- Both nations must agree

#### Accepting Alliance

The other nation's leader/assistant must also run:

```
/nation ally <yournation>
```

Once both agree, the alliance forms.

#### Breaking Alliance

Return to neutral status:

```
/nation neutral <nationname>
```

### Enemies

#### Declaring Enemy

Mark another nation as an enemy:

```
/nation enemy <nationname>
```

**Effects:**
- Cannot form alliance
- Easier to declare war (if war system enabled)
- Shows as red on dynmap/bluemap

#### Ending Hostility

Return to neutral:

```
/nation neutral <nationname>
```

## Warfare

If the war system is enabled, nations can wage war.

### Declaring War

```
/nation war <nationname>
```

See [War System](wars.md) and [War Tutorial](../tutorials/war_tutorial.md) for details.

### War Goals

Choose an objective when declaring war:

- **Conquest** - Capture enemy territory
- **Wealth** - Drain enemy banks
- **Humiliation** - Damage reputation

### Peace Treaties

End an active war:

```
/nation peace <nationname>
```

Both sides must agree to peace.

## Nation Economy

### Nation Bank

If Vault is enabled, nations have a treasury:

```
/nation deposit <amount>       # Anyone can deposit
/nation withdraw <amount>      # Leader only
/nation balance                # Check balance
```

### Uses of Nation Bank

- Fund wars
- Support member towns
- Pay for nation expenses
- Accumulate wealth

### National Income

Nation banks grow through:

- Member donations
- War reparations
- Interest (if enabled)

## Member Management

### Viewing Members

List all towns in the nation:

```
/nation towns
```

Shows:
- Capital (⭐)
- Assistant towns (✦)
- Member towns (•)

### Promoting Towns

Give a mayor assistant status:

```
/nation promote <playername>
```

**Requirements:**
- Must be nation leader
- Player must be a mayor of a member town

### Demoting Towns

Remove assistant status:

```
/nation demote <playername>
```

**Requirements:**
- Must be nation leader

## Leaving a Nation

### As a Regular Town

Town mayors can leave:

```
/nation leave
```

**Consequences:**
- Lose nation benefits
- Lose nation color
- Break alliance with other member towns

### As the Capital

Capital cannot leave. Instead:

```
/nation delete
```

**Consequences:**
- Entire nation disbands
- All towns become independent
- Nation bank distributed (configurable)

## Nation Statistics

### Viewing Nation Info

See detailed information:

```
/nation info <nationname>
```

Shows:
- Leader and capital
- Member towns (count)
- Total residents (sum of all towns)
- Total claims (sum of all towns)
- Bank balance
- Allies and enemies
- Founded date

### Leaderboards

View top nations:

```
/nation top
```

Ranks by:
- Total members
- Total claims
- Bank balance

## Advanced Features

### Embassy System

Towns can set **embassy** plots:

```
/plot type embassy
```

Allows foreign towns to claim within your nation's territory for:

- Trade posts
- Diplomatic missions
- Cultural exchange

### Nation Chat

Coming soon: Private chat for nation members.

### Nation Ranks

Coming soon: Custom ranks beyond leader/assistant.

## Configuration

Key config settings for nations:

```yaml
nation:
  creation-cost: 1000.0         # Cost to create
  min-towns: 1                  # Minimum towns to form
  max-towns: -1                 # Max towns (-1 = unlimited)
  allow-capital-change: true    # Can transfer capital
  allow-leader-disband: true    # Leader can delete nation
```

See [Configuration Guide](../getting-started/configuration.md).

## Permissions

### Player Permissions

```
interim.nation.create          # Create nations
interim.nation.invite          # Invite towns
```

### Admin Permissions

```
interim.admin.nation.delete    # Force delete nations
interim.admin.nation.color     # Change any nation color
```

See [Permissions Reference](../admin/permissions.md).

## Placeholders

Use nation data in other plugins:

```
%interim_nation_name%          # Nation name
%interim_nation_towns_count%   # Number of towns
%interim_nation_total_residents% # All residents
%interim_nation_bank%          # Bank balance
```

See [PlaceholderAPI Integration](../integrations/placeholderapi.md).

## Deletion

### Disbanding Your Nation

As leader:

```
/nation delete
```

**Consequences:**
- All member towns become independent
- Nation bank refunded to leader
- All diplomatic relationships end
- Cannot be undone

**Requirements:**
- Must be nation leader

### Forced Deletion

Admins can force delete:

```
/interimadmin deletenation <nationname>
```

## Troubleshooting

### "Cannot create nation - not a town mayor"

- You must found a town first
- Use `/town create <name>` first
- Then create the nation

### "Town is already in a nation"

- Towns can only be in one nation
- Leave current nation: `/nation leave`
- Then accept new invite

### "Insufficient funds"

- Check nation creation cost in config
- Use town bank: `/town deposit <amount>`
- Admin can adjust costs in config

### "Nation name already exists"

- Names must be unique
- Choose a different name
- Admin can delete inactive nations

## Strategy Tips

### Growing Your Nation

1. **Invite strategically** - Target active towns with good locations
2. **Offer benefits** - Share national bank, provide protection
3. **Build trust** - Help towns with resources and defense

### Diplomatic Relations

1. **Form alliances** - Strength in numbers for wars
2. **Choose enemies wisely** - Don't fight on multiple fronts
3. **Maintain neutrality** - Not everyone needs to be ally or enemy

### Economic Management

1. **Fund the bank** - Encourage town donations
2. **Save for war** - Wars are expensive
3. **Support members** - Help towns with claims and projects

## See Also

- [Towns](towns.md) - Town management basics
- [Wars](wars.md) - War system mechanics
- [War Tutorial](../tutorials/war_tutorial.md) - Step-by-step warfare guide
- [Nation Commands](../commands/nation.md) - Full command list
- [Diplomacy Guide](../tutorials/diplomacy.md) - Advanced diplomatic strategies
