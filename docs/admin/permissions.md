# Permissions Reference

Complete list of all Interim permission nodes.

## Permission Structure

Permissions follow this pattern:
```
interim.<category>.<action>
```

## Default Permissions

By default, players have:
```yaml
interim.town.*: true      # All town permissions
interim.nation.*: true    # All nation permissions
interim.plot.*: true      # All plot permissions
interim.map.*: true       # Map viewing
```

Admins need:
```yaml
interim.admin.*: true     # All admin permissions
```

## Town Permissions

### Basic Permissions

```yaml
interim.town.menu: true           # Open town GUI
interim.town.create: true         # Create towns
interim.town.delete: true         # Delete own town
interim.town.join: true           # Join towns
interim.town.leave: true          # Leave town
interim.town.info: true           # View town info
interim.town.list: true           # List all towns
interim.town.invites: true        # View invites
```

### Management Permissions

```yaml
interim.town.invite: true         # Invite players
interim.town.kick: true           # Remove residents
interim.town.promote: true        # Promote to assistant
interim.town.demote: true         # Demote assistants
interim.town.setspawn: true       # Set spawn point
interim.town.spawn: true          # Use town spawn
```

### Claim Permissions

```yaml
interim.town.claim: true          # Claim chunks
interim.town.unclaim: true        # Unclaim chunks
interim.town.claims: true         # List claims
```

### Settings Permissions

```yaml
interim.town.settings: true       # Change settings (PvP, explosions, etc.)
interim.town.board: true          # Set board message
interim.town.color: true          # Set town color
```

### Economy Permissions

```yaml
interim.town.deposit: true        # Deposit to bank
interim.town.withdraw: true       # Withdraw from bank
interim.town.balance: true        # Check balance
```

### Resident Permissions

```yaml
interim.town.residents: true      # List residents
```

### Wildcard

```yaml
interim.town.*: true              # All town permissions
```

## Nation Permissions

### Basic Permissions

```yaml
interim.nation.menu: true         # Open nation GUI
interim.nation.create: true       # Create nations
interim.nation.delete: true       # Disband nation
interim.nation.join: true         # Join nations
interim.nation.leave: true        # Leave nation
interim.nation.info: true         # View nation info
interim.nation.list: true         # List all nations
interim.nation.invites: true      # View invites
```

### Management Permissions

```yaml
interim.nation.invite: true       # Invite towns
interim.nation.kick: true         # Remove towns
interim.nation.promote: true      # Promote mayors
interim.nation.demote: true       # Demote assistants
interim.nation.capital: true      # Change capital
interim.nation.towns: true        # List member towns
```

### Diplomacy Permissions

```yaml
interim.nation.ally: true         # Form alliances
interim.nation.enemy: true        # Declare enemies
interim.nation.neutral: true      # Return to neutral
interim.nation.war: true          # Declare war
interim.nation.peace: true        # Propose peace
```

### Settings Permissions

```yaml
interim.nation.board: true        # Set board message
interim.nation.color: true        # Set nation color
```

### Economy Permissions

```yaml
interim.nation.deposit: true      # Deposit to bank
interim.nation.withdraw: true     # Withdraw from bank
interim.nation.balance: true      # Check balance
```

### Wildcard

```yaml
interim.nation.*: true            # All nation permissions
```

## Plot Permissions

```yaml
interim.plot.menu: true           # Open plot GUI
interim.plot.info: true           # View plot info
interim.plot.type: true           # Change plot type
```

### Wildcard

```yaml
interim.plot.*: true              # All plot permissions
```

## Map Permissions

```yaml
interim.map.view: true            # Use /map command
```

### Wildcard

```yaml
interim.map.*: true               # All map permissions
```

## Admin Permissions

### Management Permissions

```yaml
interim.admin: true               # Admin menu access
interim.admin.reload: true        # Reload config
interim.admin.save: true          # Force save data
interim.admin.bypass: true        # Bypass protection
```

### Town Administration

```yaml
interim.admin.town.delete: true   # Delete any town
interim.admin.town.color: true    # Set any town color
interim.admin.forceclaim: true    # Force claim land
```

### Nation Administration

```yaml
interim.admin.nation.delete: true # Delete any nation
interim.admin.nation.color: true  # Set any nation color
```

### Economy Administration

```yaml
interim.admin.economy: true       # Manage economy
                                  # (give/take/set money)
```

### Integration Administration

```yaml
interim.admin.bluemap: true       # Force BlueMap update
interim.admin.update: true        # Check/download updates
```

### Wildcard

```yaml
interim.admin.*: true             # All admin permissions
```

## Permission Groups

### Default Player

Recommended for all players:

```yaml
permissions:
  - interim.town.*
  - interim.nation.*
  - interim.plot.*
  - interim.map.*
```

### Moderator

For moderators:

```yaml
permissions:
  - interim.town.*
  - interim.nation.*
  - interim.plot.*
  - interim.map.*
  - interim.admin.bypass        # Build anywhere
  - interim.admin.save          # Force save
```

### Administrator

For admins:

```yaml
permissions:
  - interim.admin.*             # All admin permissions
```

## Plugin Configuration

### LuckPerms

Grant to default group:

```
/lp group default permission set interim.town.* true
/lp group default permission set interim.nation.* true
/lp group default permission set interim.plot.* true
/lp group default permission set interim.map.* true
```

Grant to admin group:

```
/lp group admin permission set interim.admin.* true
```

Grant to specific player:

```
/lp user Steve permission set interim.admin.bypass true
```

### PermissionsEx

Grant to default group:

```
/pex group default add interim.town.*
/pex group default add interim.nation.*
/pex group default add interim.plot.*
/pex group default add interim.map.*
```

Grant to admin group:

```
/pex group admin add interim.admin.*
```

### GroupManager

Edit `groups.yml`:

```yaml
groups:
  default:
    permissions:
      - interim.town.*
      - interim.nation.*
      - interim.plot.*
      - interim.map.*
  
  admin:
    permissions:
      - interim.admin.*
```

## Permission Negation

### Deny Specific Permissions

Remove abilities from players:

**LuckPerms:**
```
/lp group default permission set interim.town.create false
```

**PermissionsEx:**
```
/pex group default add -interim.town.create
```

### Example: Restrict Town Creation

Allow joining but not creating:

```yaml
permissions:
  - interim.town.*              # All town permissions
  - -interim.town.create        # Except creating
```

## Troubleshooting

### Player Can't Use Commands

**Check:**
1. Permission granted: `/lp user <player> permission check interim.town.create`
2. Plugin loaded: `/plugins`
3. Permissions plugin loaded before Interim

**Solutions:**
- Grant permission
- Reload permissions: `/lp reloadconfig`
- Check plugin load order

### Admin Commands Not Working

**Check:**
1. Has `interim.admin` or `interim.admin.*`
2. Op status: `/op <player>`
3. Console for errors

**Solutions:**
- Grant admin permission
- Make player op (temporary)
- Check permission inheritance

### Wildcard Not Working

**Check:**
1. Permission plugin supports wildcards
2. Correct syntax: `interim.town.*` not `interim.town*`
3. Permission inherited properly

**Solutions:**
- Update permission plugin
- Use correct wildcard syntax
- Grant individual permissions if needed

## See Also

- [Installation Guide](../getting-started/installation.md)
- [Configuration Guide](../getting-started/configuration.md)
- [Admin Commands](../commands/admin.md)
- [Troubleshooting Guide](troubleshooting.md)
