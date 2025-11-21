# Installation Guide

This guide will help you install and set up Interim on your Paper server.

## Prerequisites

### Required
- **Paper 1.21.9** server (or compatible fork)
- **Java 21** or higher

### Optional (but recommended)
- **Vault** - For economy integration
- **An economy plugin** - EssentialsX, CMI, etc.
- **PlaceholderAPI** - For placeholder support
- **BlueMap** - For web map visualization

## Installation Steps

### 1. Download the Plugin

Download the latest release from:
- [GitHub Releases](https://github.com/r7l-labs/interim/releases/latest)

Or use the built-in updater:
```
/interimadmin update download
```

### 2. Install Dependencies (Optional)

If you want economy features:
1. Download [Vault](https://www.spigotmc.org/resources/vault.34315/)
2. Download an economy plugin like [EssentialsX](https://essentialsx.net/)
3. Place both JARs in your `plugins/` folder

For placeholder support:
1. Download [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
2. Place JAR in your `plugins/` folder

For web map visualization:
1. Download [BlueMap](https://www.spigotmc.org/resources/bluemap.83557/)
2. Place JAR in your `plugins/` folder

### 3. Install Interim

1. Place `interim-<version>.jar` in your server's `plugins/` folder
2. Start or restart your server
3. The plugin will generate default configuration files

### 4. Configure the Plugin

After first startup, edit the configuration files:

```
plugins/Interim/
├── config.yml          # Main configuration
└── data/              # Data storage (auto-generated)
    ├── towns/
    ├── nations/
    ├── residents/
    └── claims/
```

See [Configuration Guide](configuration.md) for detailed settings.

### 5. Set Permissions

Grant permissions to your players:

```yaml
# For LuckPerms
lp group default permission set interim.town.create true
lp group default permission set interim.nation.create true

# For PermissionsEx
pex group default add interim.town.create
pex group default add interim.nation.create
```

See [Permissions](../admin/permissions.md) for complete list.

### 6. Verify Installation

Check that the plugin loaded successfully:
```
/plugins
```

You should see Interim in green.

Test basic functionality:
```
/town create TestTown
/town claim
```

## Post-Installation

### Configure Economy (if using Vault)

1. Ensure Vault and your economy plugin are loaded
2. Check economy is working: `/balance`
3. Adjust costs in `config.yml`:
   - `town.creation-cost`
   - `town.claim-cost`
   - `nation.creation-cost`

### Set Up Auto-Save

The plugin auto-saves every 5 minutes by default. To adjust:

```yaml
general:
  save-interval: 6000  # In ticks (20 ticks = 1 second)
```

### Configure Protection

Adjust protection settings to match your server:

```yaml
protection:
  block-break: true
  block-place: true
  container-access: true
  entity-damage: true
  pvp: true
  wilderness-pvp: true  # Allow PvP in unclaimed areas
```

## Updating

### Automatic Update

Use the built-in updater:
```
/interimadmin update check
/interimadmin update download
```

Restart the server to apply the update.

### Manual Update

1. Stop your server
2. Backup your `plugins/Interim/` folder
3. Replace the old JAR with the new one
4. Start your server

**Note:** Data files are automatically migrated when needed.

## Uninstallation

To completely remove Interim:

1. Stop your server
2. Delete `plugins/interim-<version>.jar`
3. (Optional) Delete `plugins/Interim/` folder to remove all data

**Warning:** Deleting the data folder will remove all towns, nations, and claims permanently!

## Troubleshooting

If you encounter issues during installation:

- **Plugin won't load:** Check you're using Paper 1.21.9 and Java 21
- **Commands not working:** Check permissions are set correctly
- **Economy not working:** Verify Vault and economy plugin are installed
- **Data not saving:** Check file permissions on `plugins/Interim/` folder

See [Troubleshooting Guide](../admin/troubleshooting.md) for more help.

## Next Steps

- [Quick Start Guide](quickstart.md) - Create your first town
- [Configuration Reference](configuration.md) - Customize settings
- [Command Reference](../commands/town.md) - Learn all commands
