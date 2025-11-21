# Economy System

Interim integrates with Vault to provide town and nation economies.

## Overview

If **Vault** and an economy plugin are installed, Interim adds:

- Town banks for shared wealth
- Nation treasuries
- Claim costs
- War expenses
- Interest on balances
- Transaction logging

## Requirements

### Required Plugins

1. **Vault** - [Download](https://www.spigotmc.org/resources/vault.34315/)
2. **Economy Plugin** - One of:
   - EssentialsX Economy
   - CMI
   - GriefPrevention
   - Any Vault-compatible economy

### Installation

1. Install Vault and economy plugin
2. Restart server
3. Verify economy works: `/balance`
4. Interim automatically detects Vault

## Town Economy

### Town Bank

Towns have a shared bank account:

```
/town balance                  # Check balance
/town deposit <amount>         # Deposit money
/town withdraw <amount>        # Withdraw (mayors only)
```

### Who Can Access

| Action | Mayor | Assistant | Resident |
|--------|-------|-----------|----------|
| View balance | ✓ | ✓ | ✓ |
| Deposit | ✓ | ✓ | ✓ |
| Withdraw | ✓ | ✗ | ✗ |

### Town Expenses

Money is automatically deducted from town bank for:

**Claiming:**
```
/town claim    # Costs: claim-cost per chunk
```

**Town Creation:**
```
/town create   # Costs: creation-cost (if enabled)
```

**Fallback:** If town bank lacks funds, deducts from player's balance.

### Income Sources

Towns earn money through:

- Resident donations (`/town deposit`)
- Interest on balance (if enabled)
- War reparations (if winner)
- Admin grants

## Nation Economy

### Nation Bank

Nations have a separate treasury:

```
/nation balance                # Check balance
/nation deposit <amount>       # Deposit money
/nation withdraw <amount>      # Withdraw (leader only)
```

### Who Can Access

| Action | Leader | Assistant | Member |
|--------|--------|-----------|--------|
| View balance | ✓ | ✓ | ✓ |
| Deposit | ✓ | ✓ | ✓ |
| Withdraw | ✓ | ✗ | ✗ |

### Nation Expenses

Money is used for:

**Nation Creation:**
```
/nation create    # Costs: creation-cost
```

**War Declarations:**
```
/nation war       # Costs: declaration-cost
```

**Peace Treaties:**
```
/nation peace     # May cost negotiated amount
```

### Income Sources

Nations earn through:

- Member donations
- Town contributions
- War spoils
- Interest (if enabled)

## Configuration

### Basic Settings

```yaml
economy:
  enabled: true              # Enable economy features
  interest-rate: 0.01        # 1% daily interest
  max-balance: -1            # -1 = unlimited
```

### Costs

```yaml
town:
  creation-cost: 0.0         # Free town creation
  claim-cost: 100.0          # 100 per chunk
  require-economy: false     # Can create without money

nation:
  creation-cost: 1000.0      # 1000 to form nation
  require-economy: false     # Can create without money

war:
  declaration-cost: 5000.0   # 5000 to declare war
```

### Interest System

Enable passive income:

```yaml
economy:
  enabled: true
  interest-rate: 0.01        # 1% per day
```

**Example:**
- Town has 10,000 in bank
- Daily interest: 10,000 × 0.01 = 100
- Balance after 7 days: 10,700

**Calculation:**
- Runs daily at server midnight
- Compounds (interest on interest)
- Applies to both towns and nations

## Money Management

### Depositing

Anyone in the town/nation can deposit:

```
/town deposit 1000
/nation deposit 5000
```

Money transfers from player balance to bank.

### Withdrawing

Only leaders can withdraw:

```
/town withdraw 500           # Mayor only
/nation withdraw 2000        # Nation leader only
```

### Checking Balances

View financial status:

```
/town info <town>            # Includes bank balance
/nation info <nation>        # Includes bank balance
/town balance                # Quick balance check
/nation balance              # Quick balance check
```

## Transaction Logging

All economic activity is logged:

**Location:**
```
plugins/Interim/logs/transactions.log
```

**Format:**
```
[2025-01-15 10:30:45] Player123 deposited 1000 to TownName
[2025-01-15 10:31:20] TownName claimed chunk (-5, 10) for 100
[2025-01-15 10:45:00] MayorName withdrew 500 from TownName
```

## War Economy

### War Costs

Declaring war costs money:

```yaml
war:
  declaration-cost: 5000.0
```

Deducted from nation bank immediately.

### War Spoils

Winners can gain:

- Bank plunder (based on war goal)
- Territory claims
- Reparation payments

**War goal: Wealth**
- Attacker gets % of defender's bank
- Configurable in war settings

**War goal: Conquest**
- Winner claims territory (no direct money)

### Surrender Costs

Surrendering may cost:

```yaml
war:
  surrender-penalty: 0.5     # 50% of war goal value
```

## Taxation (Coming Soon)

Future feature for recurring costs:

```yaml
economy:
  taxes:
    enabled: false
    rate: 10.0               # Daily tax per resident
    remove-on-unpaid: false  # Kick non-payers
```

## Rank-Based Payments (Coming Soon)

Future feature for rank salaries:

```yaml
economy:
  salaries:
    enabled: false
    mayor: 100.0             # Daily mayor salary
    assistant: 50.0          # Daily assistant salary
```

## Admin Commands

### Grant Money

Give money to towns/nations:

```
/interimadmin eco give <town|nation> <name> <amount>
```

### Take Money

Remove money from banks:

```
/interimadmin eco take <town|nation> <name> <amount>
```

### Set Balance

Set exact balance:

```
/interimadmin eco set <town|nation> <name> <amount>
```

## Economy Placeholders

Use in other plugins:

```
%interim_town_bank%          # Town balance
%interim_nation_bank%        # Nation balance
%interim_wealthiest_town%    # Richest town name
```

See [PlaceholderAPI Integration](../integrations/placeholderapi.md).

## Troubleshooting

### "Economy not enabled"

**Check:**
1. Vault installed: `/plugins`
2. Economy plugin installed: `/plugins`
3. Economy works: `/balance`
4. Config: `economy.enabled: true`

### "Insufficient funds"

**Solutions:**
- Check balance: `/town balance` or `/balance`
- Deposit more: `/town deposit <amount>`
- Ask mayor to withdraw from bank
- Admin can grant funds

### "Town bank is empty"

**Solutions:**
- Residents deposit: `/town deposit <amount>`
- Wait for interest (if enabled)
- Win wars for spoils
- Admin grant: `/interimadmin eco give`

### Interest Not Working

**Check:**
1. Enabled: `economy.enabled: true`
2. Rate set: `interest-rate: 0.01` (not 0)
3. Wait until daily tick (midnight)
4. Check logs for calculation

## Configuration Examples

### Free-to-Play Server

No costs:

```yaml
town:
  creation-cost: 0.0
  claim-cost: 0.0
  require-economy: false

nation:
  creation-cost: 0.0
  require-economy: false

war:
  declaration-cost: 0.0

economy:
  enabled: false
```

### Economy-Focused Server

High costs and interest:

```yaml
town:
  creation-cost: 10000.0
  claim-cost: 500.0
  require-economy: true

nation:
  creation-cost: 100000.0
  require-economy: true

war:
  declaration-cost: 50000.0

economy:
  enabled: true
  interest-rate: 0.02        # 2% daily
  max-balance: 1000000       # 1 million cap
```

### Balanced Server

Moderate costs:

```yaml
town:
  creation-cost: 1000.0
  claim-cost: 100.0
  require-economy: false

nation:
  creation-cost: 5000.0
  require-economy: false

war:
  declaration-cost: 10000.0

economy:
  enabled: true
  interest-rate: 0.01        # 1% daily
  max-balance: -1            # Unlimited
```

## Best Practices

### For Mayors

1. **Keep reserves** - Save for expansion
2. **Encourage deposits** - Ask residents to contribute
3. **Budget claims** - Plan before claiming
4. **Invest wisely** - Consider interest

### For Nation Leaders

1. **Coordinate funding** - Ask towns to donate
2. **Save for war** - Wars are expensive
3. **Manage withdrawals** - Don't drain the bank
4. **Plan expansion** - Budget for growth

### For Server Admins

1. **Balance costs** - Match your player economy
2. **Monitor inflation** - Adjust interest if needed
3. **Set limits** - Use `max-balance` to prevent hoarding
4. **Enable logging** - Track suspicious transactions

## See Also

- [Configuration Guide](../getting-started/configuration.md)
- [Towns](towns.md) - Town banking
- [Nations](nations.md) - Nation treasuries
- [Wars](wars.md) - War economy
- [Vault Integration](../integrations/vault.md)
