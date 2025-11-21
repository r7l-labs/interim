# Vault Integration

Interim integrates with Vault for economy features.

## Overview

**Vault** is an API that connects economy plugins. With Vault:

- Town banks
- Nation treasuries
- Claim costs
- War expenses
- Transaction logging

## Installation

### Prerequisites

1. **Vault Plugin**
   - Download from [SpigotMC](https://www.spigotmc.org/resources/vault.34315/)
   - Latest version recommended

2. **Economy Plugin** - Choose one:
   - **EssentialsX** ([Download](https://essentialsx.net/))
   - **CMI** ([Spigot](https://www.spigotmc.org/resources/cmi.3742/))
   - **GriefPrevention**
   - Any Vault-compatible economy

### Setup Steps

1. **Install Vault:**
   ```
   plugins/Vault.jar
   ```

2. **Install economy plugin:**
   ```
   plugins/EssentialsX.jar
   plugins/EssentialsXChat.jar
   plugins/EssentialsXSpawn.jar
   ```

3. **Start server**

4. **Verify economy works:**
   ```
   /balance
   /eco give <player> 1000
   ```

5. **Enable in Interim:**
   ```yaml
   # plugins/Interim/config.yml
   integrations:
     vault:
       enabled: true
   
   economy:
     enabled: true
   ```

6. **Restart server**

## Configuration

### Enable Economy

```yaml
economy:
  enabled: true              # Master economy toggle
  interest-rate: 0.01        # 1% daily interest
  max-balance: -1            # -1 = unlimited
```

### Set Costs

```yaml
town:
  creation-cost: 0.0         # Town creation cost
  claim-cost: 100.0          # Per-chunk cost
  require-economy: false     # Must have money to create

nation:
  creation-cost: 1000.0      # Nation creation cost
  require-economy: false     # Must have money to create

war:
  declaration-cost: 5000.0   # War declaration cost
```

### Integration Settings

```yaml
integrations:
  vault:
    enabled: true            # Enable Vault integration
```

## Features

### Town Banks

Every town has a bank:

```
/town balance                # Check balance
/town deposit 1000           # Deposit money
/town withdraw 500           # Withdraw (mayors only)
```

**See:** [Economy Guide](../features/economy.md)

### Nation Banks

Nations have treasuries:

```
/nation balance              # Check balance
/nation deposit 5000         # Deposit money
/nation withdraw 1000        # Withdraw (leaders only)
```

### Automatic Costs

Money is deducted for:

- Creating towns (`town.creation-cost`)
- Claiming chunks (`town.claim-cost`)
- Creating nations (`nation.creation-cost`)
- Declaring wars (`war.declaration-cost`)

### Interest System

Banks earn interest:

```yaml
economy:
  interest-rate: 0.01        # 1% per day
```

**Calculation:**
- Runs daily at midnight
- Compounds (interest on interest)
- Applies to towns and nations

**Example:**
- Bank: 10,000
- Rate: 0.01 (1%)
- Daily income: 100
- After week: 10,700

### Transaction Logging

All transactions logged:

```
plugins/Interim/logs/transactions.log
```

**Format:**
```
[2025-11-21 10:30:45] Player123 deposited 1000 to TownName
[2025-11-21 10:31:20] TownName claimed chunk (-5, 10) for 100
[2025-11-21 10:45:00] MayorName withdrew 500 from TownName
```

## Economy Plugins

### EssentialsX (Recommended)

**Why:**
- Free and open source
- Most popular
- Well maintained
- Simple setup

**Commands:**
```
/balance              # Check balance
/eco give <player> <amount>
/eco take <player> <amount>
/eco set <player> <amount>
/baltop               # Richest players
```

**Config:**
```yaml
# EssentialsX config.yml
economy-log-enabled: true
economy-log-update-enabled: true
```

### CMI

**Why:**
- Feature-rich
- Professional support
- Many addons

**Premium plugin** - Requires purchase

### Custom Economy

**Requirements:**
1. Implements Vault Economy API
2. Registered with Vault
3. Handles transactions

**See:** Vault API documentation

## Troubleshooting

### "Economy not enabled"

**Check:**
1. Vault installed: `/plugins | grep Vault`
2. Economy plugin installed
3. Economy works: `/balance`
4. Config: `economy.enabled: true`

**Solutions:**
- Install Vault
- Install economy plugin (EssentialsX)
- Enable in config
- Restart server

### "No economy plugin found"

**Check:**
1. Economy plugin loaded before Interim
2. Economy registered with Vault
3. Console for Vault economy hook messages

**Solutions:**
- Ensure economy plugin loads first
- Check plugin load order
- Restart in correct order

### "Insufficient funds"

**Solutions:**
- Check balance: `/balance`
- Get money: `/eco give <player> 1000` (admin)
- Use town bank: `/town deposit <amount>`
- Adjust costs in config

### Interest Not Working

**Check:**
1. Enabled: `economy.enabled: true`
2. Rate set: `interest-rate: 0.01` (not 0)
3. Wait until daily tick (midnight server time)
4. Check logs for calculations

**Debug:**
Enable debug logging:
```yaml
general:
  debug: true
```

Check console for interest calculations.

## Admin Commands

### Give Money to Towns/Nations

```
/interimadmin eco give town <name> <amount>
/interimadmin eco give nation <name> <amount>
```

**Example:**
```
/interimadmin eco give town Riverside 10000
/interimadmin eco give nation TheEmpire 50000
```

### Take Money

```
/interimadmin eco take town <name> <amount>
/interimadmin eco take nation <name> <amount>
```

### Set Balance

```
/interimadmin eco set town <name> <amount>
/interimadmin eco set nation <name> <amount>
```

**See:** [Admin Commands](../commands/admin.md)

## Configuration Examples

### Free Server

No economy required:

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

High costs:

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

## Performance

### Transaction Load

Heavy economy usage:

**Symptoms:**
- Lag during mass claims
- Slow bank operations
- TPS drops during interest calculations

**Solutions:**

Use async operations:
```yaml
performance:
  async-chunks: true
```

Batch transactions:
```yaml
performance:
  batch-size: 100
```

### Database Optimization

For large servers:

**Enable caching:**
```yaml
performance:
  resident-cache-size: 5000
  claim-cache-size: 10000
```

**See:** [Performance Guide](../admin/performance.md)

## See Also

- [Economy Feature Guide](../features/economy.md)
- [Configuration Guide](../getting-started/configuration.md)
- [Admin Commands](../commands/admin.md)
- [Troubleshooting](../admin/troubleshooting.md)
