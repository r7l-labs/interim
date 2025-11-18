# Interim - A Comprehensive Nations Plugin for Paper 1.21.9

**Interim** is a full-featured nations, towns, and claiming plugin for Paper 1.21.9 servers. It provides everything you need to create a political server with towns, nations, claiming mechanics, protection, and economy integration.

## Features

### Towns
- **Create and manage towns** with mayors, assistants, and residents
- **Chunk-based claiming system** with configurable costs and limits
- **Town banks** with deposit/withdraw functionality
- **Town spawns** for easy teleportation
- **Customizable settings**: PvP, explosions, mob spawning, and open/closed towns
- **Town boards** for announcements
- **Invite system** with automatic expiration

### Nations
- **Form nations** from multiple towns
- **Capital system** with special privileges
- **Alliance and enemy relations** between nations
- **Nation banks** separate from town banks
- **Nation colors** for identification
- **Nation boards** for communication

### Protection
- **Block break/place protection** in claimed chunks
- **Container and interactive block protection**
- **Entity damage protection** (item frames, armor stands, paintings)
- **Explosion protection** (configurable per town)
- **PvP protection** with:
  - Town member protection
  - Nation member protection
  - Ally protection
  - Configurable wilderness PvP
- **Mob spawning control** per town

### Economy
- **Vault integration** for economy features
- **Costs for**:
  - Town creation
  - Nation creation
  - Chunk claiming
- **Town and nation banks** for shared resources
- **Upkeep system** (configurable in config.yml)

### User Experience
- **Territory notifications** when moving between claims
- **Pending invite notifications** on join
- **Comprehensive command system** with tab completion
- **Detailed information displays** for towns and nations
- **Rank system** with different permission levels

## Commands

### Town Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/town create <name>` | Create a new town | `interim.town.create` |
| `/town delete [confirm]` | Delete your town | - |
| `/town invite <player>` | Invite a player to your town | - |
| `/town kick <player>` | Kick a player from your town | - |
| `/town leave` | Leave your current town | - |
| `/town claim` | Claim the chunk you're standing in | - |
| `/town unclaim` | Unclaim the chunk you're standing in | - |
| `/town spawn [town]` | Teleport to town spawn | - |
| `/town setspawn` | Set your town's spawn point | - |
| `/town deposit <amount>` | Deposit money to town bank | - |
| `/town withdraw <amount>` | Withdraw money from town bank | - |
| `/town info [town]` | View town information | - |
| `/town list` | List all towns | - |
| `/town toggle <option>` | Toggle town settings (pvp/explosions/mobs/open) | - |
| `/town rank <player> <rank>` | Set a player's rank | - |
| `/town board <message>` | Set the town board message | - |
| `/town rename <name>` | Rename your town | - |

**Aliases:** `/t`

### Nation Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/nation create <name>` | Create a new nation | `interim.nation.create` |
| `/nation delete [confirm]` | Delete your nation | - |
| `/nation add <town>` | Add a town to your nation | - |
| `/nation kick <town>` | Kick a town from your nation | - |
| `/nation leave` | Leave your current nation | - |
| `/nation ally <nation>` | Set another nation as an ally | - |
| `/nation enemy <nation>` | Set another nation as an enemy | - |
| `/nation neutral <nation>` | Set another nation as neutral | - |
| `/nation deposit <amount>` | Deposit money to nation bank | - |
| `/nation withdraw <amount>` | Withdraw money from nation bank | - |
| `/nation info [nation]` | View nation information | - |
| `/nation list` | List all nations | - |
| `/nation board <message>` | Set the nation board message | - |
| `/nation rename <name>` | Rename your nation | - |

**Aliases:** `/n`

### Plot Commands
| Command | Description |
|---------|-------------|
| `/plot info` | View information about the current chunk |
| `/plot accept <town>` | Accept a town invite |
| `/plot deny <town>` | Deny a town invite |
| `/plot invites` | View all pending invites |

**Aliases:** `/p`

**Note:** You can also use `/town accept <town>` and `/town deny <town>` for invites.

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `interim.admin` | Bypass all protections | op |
| `interim.town.create` | Create towns | true |
| `interim.nation.create` | Create nations | true |

## Configuration

The plugin comes with a comprehensive `config.yml`:

```yaml
# Town Settings
town:
  creation-cost: 1000.0          # Cost to create a town
  max-name-length: 32            # Maximum town name length
  min-name-length: 3             # Minimum town name length
  claim-cost: 100.0              # Cost per chunk claimed
  max-claims: 100                # Maximum chunks per town
  upkeep-per-chunk: 10.0         # Daily upkeep cost per chunk
  default-pvp: false             # Default PvP setting
  default-explosions: false      # Default explosions setting
  default-mob-spawning: true     # Default mob spawning setting

# Nation Settings
nation:
  creation-cost: 5000.0          # Cost to create a nation
  max-name-length: 32            # Maximum nation name length
  min-name-length: 3             # Minimum nation name length
  min-towns: 2                   # Minimum towns to form a nation (not enforced)
  daily-upkeep: 500.0            # Daily upkeep cost
  allow-neutral: true            # Allow neutral nations

# General Settings
general:
  debug: false                   # Enable debug mode
  language: en                   # Language (not implemented)
  save-interval: 6000            # Auto-save interval (ticks)
  max-invites: 10                # Max pending invites per player

# Protection Settings
protection:
  block-break: true              # Protect blocks from breaking
  block-place: true              # Protect against block placement
  container-access: true         # Protect containers
  entity-damage: true            # Protect entities
  pvp: true                      # Enable PvP protection
  wilderness-pvp: true           # Allow PvP in wilderness

# Economy Settings
economy:
  enabled: true                  # Enable economy features
  use-vault: true                # Use Vault for economy
```

## Data Storage

All data is stored in JSON format in the `plugins/Interim/data/` directory:
- `towns/*.json` - Individual town data files
- `nations/*.json` - Individual nation data files
- `residents/residents.json` - All resident data
- `claims/claims.json` - All claim data

The plugin automatically saves data:
- Every 5 minutes (configurable)
- On server shutdown
- When major changes occur

## Rank System

### Town Ranks
1. **Mayor** - Full control over the town
   - Create/delete town
   - Claim/unclaim chunks
   - Invite/kick members
   - Manage settings
   - Set spawn
   - Withdraw from bank
   - Promote/demote members

2. **Assistant** - Trusted helper
   - Claim/unclaim chunks
   - Invite members
   - Set spawn
   - Set board message

3. **Resident** - Basic member
   - Build in town territory
   - Use town spawn
   - Deposit to bank

## Building

To build the plugin:

```bash
mvn clean package
```

The compiled JAR will be in the `target/` directory.

## Dependencies

- **Paper 1.21.9** (required)
- **Vault** (optional, for economy features)
- **An economy plugin** like EssentialsX (optional)

## Installation

1. Download or build the plugin JAR
2. Place it in your server's `plugins/` folder
3. (Optional) Install Vault and an economy plugin
4. Start your server
5. Configure the plugin in `plugins/Interim/config.yml`
6. Reload or restart your server

## Support

For issues, suggestions, or contributions, please visit the GitHub repository.

## License

See LICENSE file for details.

---

**Made with ❤️ by r7l-labs**
