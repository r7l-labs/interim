# Admin Commands

Complete reference for administrative commands.

## Command Overview

| Command | Description | Permission |
|---------|-------------|------------|
| `/interimadmin` | Open admin menu | `interim.admin` |
| `/interimadmin reload` | Reload configuration | `interim.admin.reload` |
| `/interimadmin save` | Force save all data | `interim.admin.save` |
| `/interimadmin deletetown <town>` | Delete a town | `interim.admin.town.delete` |
| `/interimadmin deletenation <nation>` | Delete a nation | `interim.admin.nation.delete` |
| `/interimadmin towncolor <town> <hex>` | Set town color | `interim.admin.town.color` |
| `/interimadmin nationcolor <nation> <hex>` | Set nation color | `interim.admin.nation.color` |
| `/interimadmin forceclaim <town>` | Force claim chunk | `interim.admin.forceclaim` |
| `/interimadmin bluemap update` | Force BlueMap update | `interim.admin.bluemap` |
| `/interimadmin update check` | Check for updates | `interim.admin.update` |
| `/interimadmin update download` | Download latest version | `interim.admin.update` |
| `/interimadmin eco give <type> <name> <amount>` | Give money | `interim.admin.economy` |
| `/interimadmin eco take <type> <name> <amount>` | Take money | `interim.admin.economy` |
| `/interimadmin eco set <type> <name> <amount>` | Set balance | `interim.admin.economy` |

## Detailed Commands

### `/interimadmin`

Opens the admin control panel GUI.

**Permission:** `interim.admin`

**Usage:**
```
/interimadmin
```

**GUI Features:**
- Quick access to common admin tasks
- Server statistics
- Data management
- Update checker

---

### `/interimadmin reload`

Reload configuration files without restarting the server.

**Permission:** `interim.admin.reload`

**Usage:**
```
/interimadmin reload
```

**Reloads:**
- `config.yml` settings
- Message configurations
- GUI layouts
- Integration settings

**Note:** Does not reload data files (towns, nations, etc.)

---

### `/interimadmin save`

Manually trigger a save of all plugin data.

**Permission:** `interim.admin.save`

**Usage:**
```
/interimadmin save
```

**Saves:**
- All towns
- All nations
- All residents
- All claims
- All war data

**Note:** Data is auto-saved every 5 minutes by default.

---

### `/interimadmin deletetown <town>`

Forcibly delete a town.

**Permission:** `interim.admin.town.delete`

**Usage:**
```
/interimadmin deletetown <town>
```

**Arguments:**
- `<town>` - Town name

**Consequences:**
- All claims unclaimed
- All residents lose membership
- Town bank cleared
- Cannot be undone

**Example:**
```
/interimadmin deletetown InactiveTown
/interimadmin deletetown GrieferTown
```

**Use cases:**
- Removing inactive towns
- Moderating rule violations
- Cleaning up test towns

---

### `/interimadmin deletenation <nation>`

Forcibly disband a nation.

**Permission:** `interim.admin.nation.delete`

**Usage:**
```
/interimadmin deletenation <nation>
```

**Arguments:**
- `<nation>` - Nation name

**Consequences:**
- All member towns become independent
- Nation bank cleared
- All wars/alliances end
- Cannot be undone

**Example:**
```
/interimadmin deletenation InactiveNation
```

---

### `/interimadmin towncolor <town> <hex>`

Set or override a town's map color.

**Permission:** `interim.admin.town.color`

**Usage:**
```
/interimadmin towncolor <town> <hex>
```

**Arguments:**
- `<town>` - Town name
- `<hex>` - Hex color code (e.g., #FF0000)

**Example:**
```
/interimadmin towncolor Riverside #00FF00
/interimadmin towncolor NewTown #FF0000
```

**Use cases:**
- Setting colors for new towns
- Fixing color conflicts
- Event decoration
- Moderation (marking rule violators)

---

### `/interimadmin nationcolor <nation> <hex>`

Set or override a nation's map color.

**Permission:** `interim.admin.nation.color`

**Usage:**
```
/interimadmin nationcolor <nation> <hex>
```

**Arguments:**
- `<nation>` - Nation name
- `<hex>` - Hex color code

**Example:**
```
/interimadmin nationcolor TheEmpire #0000FF
```

---

### `/interimadmin forceclaim <town>`

Force claim the current chunk for a town.

**Permission:** `interim.admin.forceclaim`

**Usage:**
```
/interimadmin forceclaim <town>
```

**Arguments:**
- `<town>` - Town name to claim for

**Bypasses:**
- Adjacent requirement
- Economy costs
- Minimum distance
- Claim limits
- Disabled worlds

**Example:**
```
/interimadmin forceclaim Riverside
```

**Use cases:**
- Helping new players
- Fixing claim issues
- Event setup
- Testing

---

### `/interimadmin bluemap update`

Force push all claim data to BlueMap immediately.

**Permission:** `interim.admin.bluemap`

**Usage:**
```
/interimadmin bluemap update
```

**Effects:**
- Updates all markers
- Refreshes colors
- Syncs claim changes
- Rebuilds nation borders

**Use cases:**
- After bulk claim changes
- After color updates
- Troubleshooting map issues

---

### `/interimadmin update check`

Check if a newer version is available on GitHub.

**Permission:** `interim.admin.update`

**Usage:**
```
/interimadmin update check
```

**Displays:**
- Current version
- Latest version
- Release notes
- Download link

**Example output:**
```
Current version: dev-0.2.16
Latest version: dev-0.2.17
New version available!

Changes:
- Fixed bug with claim borders
- Added new economy features

Use /interimadmin update download to download.
```

---

### `/interimadmin update download`

Download the latest version from GitHub.

**Permission:** `interim.admin.update`

**Usage:**
```
/interimadmin update download
```

**Process:**
1. Downloads latest release JAR
2. Saves to `plugins/Interim/updates/`
3. Instructions to restart server

**Notes:**
- Requires internet connection
- GitHub API rate limits apply
- Manual restart required to apply

**After download:**
```
Server restart required to apply update.
The new JAR will replace the old one on startup.
```

---

### `/interimadmin eco give <type> <name> <amount>`

Give money to a town or nation.

**Permission:** `interim.admin.economy`

**Usage:**
```
/interimadmin eco give <type> <name> <amount>
```

**Arguments:**
- `<type>` - Either `town` or `nation`
- `<name>` - Town/nation name
- `<amount>` - Amount to give

**Requirements:**
- Vault and economy plugin installed

**Example:**
```
/interimadmin eco give town Riverside 10000
/interimadmin eco give nation TheEmpire 50000
```

**Use cases:**
- Compensating for bugs
- Event rewards
- Starting bonuses
- Moderation compensation

---

### `/interimadmin eco take <type> <name> <amount>`

Remove money from a town or nation bank.

**Permission:** `interim.admin.economy`

**Usage:**
```
/interimadmin eco take <type> <name> <amount>
```

**Arguments:**
- `<type>` - Either `town` or `nation`
- `<name>` - Town/nation name
- `<amount>` - Amount to remove

**Example:**
```
/interimadmin eco take town GrieferTown 5000
/interimadmin eco take nation EvilEmpire 100000
```

**Use cases:**
- Fining rule violations
- Balancing economy
- Removing exploited funds

---

### `/interimadmin eco set <type> <name> <amount>`

Set exact balance for a town or nation.

**Permission:** `interim.admin.economy`

**Usage:**
```
/interimadmin eco set <type> <name> <amount>
```

**Arguments:**
- `<type>` - Either `town` or `nation`
- `<name>` - Town/nation name
- `<amount>` - New balance

**Example:**
```
/interimadmin eco set town TestTown 0
/interimadmin eco set nation TheEmpire 1000000
```

**Use cases:**
- Resetting balances
- Fixing economy bugs
- Event setup

---

## Permission Nodes

### Admin Permissions

```yaml
interim.admin: true                    # Admin menu access
interim.admin.reload: true             # Reload config
interim.admin.save: true               # Force save
interim.admin.town.delete: true        # Delete towns
interim.admin.nation.delete: true      # Delete nations
interim.admin.town.color: true         # Set town colors
interim.admin.nation.color: true       # Set nation colors
interim.admin.forceclaim: true         # Force claim land
interim.admin.bluemap: true            # Force BlueMap update
interim.admin.update: true             # Check/download updates
interim.admin.economy: true            # Manage economy
interim.admin.bypass: true             # Bypass protection
```

### Wildcard Permission

Grant all admin permissions:

```yaml
interim.admin.*: true
```

---

## Best Practices

### Regular Maintenance

**Daily:**
```
/interimadmin save           # Backup data
```

**Weekly:**
```
/interimadmin update check   # Check for updates
```

**Monthly:**
- Review inactive towns
- Clean up abandoned claims
- Check economy balance

### Backup Strategy

Before major actions:
```
/interimadmin save           # Save current state
```

Then backup the data folder:
```
/backup plugins/Interim/data/
```

### Update Process

1. Check for updates:
```
/interimadmin update check
```

2. Download if available:
```
/interimadmin update download
```

3. Save all data:
```
/interimadmin save
```

4. Restart server:
```
/restart
```

5. Verify:
```
/plugins
```

---

## Troubleshooting

### "Failed to reload configuration"

**Check:**
- YAML syntax in `config.yml`
- File permissions
- Console for specific errors

**Fix:**
- Validate YAML syntax
- Restore from backup if needed

### "Cannot delete town - is a nation capital"

**Solution:**
1. Delete the nation first:
```
/interimadmin deletenation <nation>
```

2. Then delete the town:
```
/interimadmin deletetown <town>
```

### "Update download failed"

**Causes:**
- No internet connection
- GitHub API rate limit
- Invalid release

**Solutions:**
- Check internet connection
- Wait for rate limit reset (1 hour)
- Download manually from GitHub

### "Economy command failed"

**Check:**
1. Vault installed: `/plugins`
2. Economy plugin installed
3. Economy enabled in config

---

## See Also

- [Permissions Reference](../admin/permissions.md)
- [Data Management](../admin/data.md)
- [Troubleshooting Guide](../admin/troubleshooting.md)
- [Configuration Guide](../getting-started/configuration.md)
- [Performance Tuning](../admin/performance.md)
