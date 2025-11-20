# 🏛️ Interim

[![Release](https://img.shields.io/github/v/release/r7l-labs/interim)](https://github.com/r7l-labs/interim/releases)
[![License](https://img.shields.io/github/license/r7l-labs/interim)](LICENSE)
[![Paper](https://img.shields.io/badge/Paper-1.21.9-blue)](https://papermc.io/)

**Interim** is a full-featured nations, towns, and claiming plugin for Paper 1.21.9 servers. Create political servers with advanced territory management, protection systems, economy integration, and BlueMap visualization.

## ✨ Key Features

- 🏘️ **Towns & Nations** - Create towns, form nations, and build alliances
- 🗺️ **Chunk-Based Claiming** - Claim territory with customizable costs and limits
- 🛡️ **Advanced Protection** - Comprehensive block, entity, PvP, and explosion protection
- 💰 **Economy Integration** - Vault support with town/nation banks and upkeep systems
- 🗺️ **BlueMap Integration** - Visualize territories on web maps with custom colors
- 📊 **War System** - Declare wars, set goals, and track history
- 🎨 **Customizable Colors** - Set unique colors for towns and nations
- 🔄 **Auto-Updates** - Built-in update checker and downloader
- 📝 **Intuitive Commands** - Full tab completion and detailed help menus

## 📦 Features

### 🏘️ Towns
- Create and manage towns with mayors, assistants, and residents
- Chunk-based claiming system with configurable costs and limits
- Town banks with deposit/withdraw functionality
- Town spawns for easy teleportation
- Customizable settings: PvP, explosions, mob spawning, open/closed
- Town boards for announcements
- Invite system with automatic expiration
- Custom town colors for map visualization

### 🏛️ Nations
- Form nations from multiple towns
- Capital system with special privileges
- Alliance and enemy relations between nations
- Nation banks separate from town banks
- Custom nation colors for identification
- Nation boards for communication
- War system with goals and history tracking

### 🛡️ Protection
- Block break/place protection in claimed chunks
- Container and interactive block protection
- Entity damage protection (item frames, armor stands, paintings)
- Explosion protection (configurable per town)
- Advanced PvP protection:
  - Town member protection
  - Nation member protection
  - Ally protection
  - Configurable wilderness PvP
- Mob spawning control per town

### 💰 Economy
- Vault integration for economy features
- Configurable costs for town/nation creation and claiming
- Town and nation banks for shared resources
- Optional upkeep system (configurable)

### 🗺️ BlueMap Integration
- Visualize all territories on web maps
- Custom colors for towns and nations
- Color hierarchy: nationless towns use town color, nation members use nation color
- Territory outlines with proper boundary detection
- Force update command for troubleshooting

### ⚔️ War System
- Declare wars between nations
- Set war goals and track progress
- War history and statistics
- Surrender and peace treaty mechanics
- Active war GUI for monitoring conflicts

### 🔧 Administration
- Comprehensive admin commands for all features
- Force create/delete towns and nations
- Manual claim management
- Set town/nation colors
- Force update BlueMap markers
- Built-in update checker and auto-downloader
- Data recovery tools
- Purge commands with confirmations

## 📚 Commands

### 🏘️ Town Commands (`/town` or `/t`)

| Command | Description |
|---------|-------------|
| `/town create <name>` | Create a new town |
| `/town delete [confirm]` | Delete your town |
| `/town invite <player>` | Invite a player to your town |
| `/town kick <player>` | Kick a player from your town |
| `/town leave` | Leave your current town |
| `/town claim` | Claim the chunk you're standing in |
| `/town unclaim` | Unclaim the chunk you're standing in |
| `/town spawn [town]` | Teleport to town spawn |
| `/town setspawn` | Set your town's spawn point |
| `/town deposit <amount>` | Deposit money to town bank |
| `/town withdraw <amount>` | Withdraw money from town bank |
| `/town info [town]` | View town information |
| `/town list` | List all towns |
| `/town toggle <option>` | Toggle settings (pvp/explosions/mobs/open) |
| `/town rank <player> <rank>` | Set a player's rank |
| `/town board <message>` | Set the town board message |
| `/town rename <name>` | Rename your town |

### 🏛️ Nation Commands (`/nation` or `/n`)

| Command | Description |
|---------|-------------|
| `/nation create <name>` | Create a new nation |
| `/nation delete [confirm]` | Delete your nation |
| `/nation add <town>` | Add a town to your nation |
| `/nation kick <town>` | Kick a town from your nation |
| `/nation leave` | Leave your current nation |
| `/nation ally <nation>` | Set another nation as an ally |
| `/nation enemy <nation>` | Set another nation as an enemy |
| `/nation neutral <nation>` | Set another nation as neutral |
| `/nation deposit <amount>` | Deposit money to nation bank |
| `/nation withdraw <amount>` | Withdraw money from nation bank |
| `/nation info [nation]` | View nation information |
| `/nation list` | List all nations |
| `/nation board <message>` | Set the nation board message |
| `/nation rename <name>` | Rename your nation |

### 📍 Plot Commands (`/plot` or `/p`)

| Command | Description |
|---------|-------------|
| `/plot info` | View information about the current chunk |
| `/plot accept <town>` | Accept a town invite |
| `/plot deny <town>` | Deny a town invite |
| `/plot invites` | View all pending invites |

### 🗺️ Map Commands (`/map` or `/m`)

| Command | Description |
|---------|-------------|
| `/map` | View claim map of surrounding area |

### 🛠️ Admin Commands (`/interimadmin`)

| Command | Description |
|---------|-------------|
| `/interimadmin town create <name> <mayor>` | Force create town |
| `/interimadmin town delete <name>` | Force delete town |
| `/interimadmin town color <town> <color>` | Set town color |
| `/interimadmin nation create <name> <capital>` | Force create nation |
| `/interimadmin nation delete <name>` | Force delete nation |
| `/interimadmin nation color <nation> <color>` | Set nation color |
| `/interimadmin claim add <town> [x] [z] [world]` | Force claim |
| `/interimadmin claim remove <x> <z> <world>` | Force unclaim |
| `/interimadmin bluemap` | Force update BlueMap markers |
| `/interimadmin update check` | Check for plugin updates |
| `/interimadmin update download` | Download latest plugin version |
| `/interimadmin reload` | Reload configuration |

**Available Colors:** WHITE, RED, BLUE, GREEN, YELLOW, GOLD, AQUA, LIGHT_PURPLE, DARK_GREEN, DARK_AQUA, DARK_PURPLE

## 🔑 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `interim.admin` | Full admin access, bypass all protections | op |
| `interim.town.create` | Create towns | true |
| `interim.nation.create` | Create nations | true |

## ⚙️ Configuration

Comprehensive `config.yml` with all settings:

```yaml
# Town Settings
town:
  creation-cost: 1000.0
  claim-cost: 100.0
  max-claims: 100
  upkeep-per-chunk: 10.0
  default-pvp: false
  default-explosions: false
  default-mob-spawning: true

# Nation Settings
nation:
  creation-cost: 5000.0
  min-towns: 2
  daily-upkeep: 500.0

# Protection Settings
protection:
  block-break: true
  block-place: true
  container-access: true
  entity-damage: true
  pvp: true
  wilderness-pvp: true

# Economy Settings
economy:
  enabled: true
  use-vault: true
```

See full config in [config.yml](src/main/resources/config.yml)

## 💾 Data Storage

All data is stored in JSON format in `plugins/Interim/data/`:
- `towns/*.json` - Individual town data
- `nations/*.json` - Individual nation data
- `residents/residents.json` - Resident data
- `claims/claims.json` - Claim data

**Auto-save:**
- Every 5 minutes (configurable)
- On server shutdown
- After major changes

## 👥 Rank System

### Town Ranks
1. **👑 Mayor** - Full town control
   - Create/delete, claim/unclaim
   - Invite/kick, manage settings
   - Bank withdrawal, promotions

2. **⭐ Assistant** - Trusted helper
   - Claim/unclaim chunks
   - Invite members
   - Set spawn and board

3. **👤 Resident** - Basic member
   - Build in town territory
   - Use town spawn
   - Deposit to bank

## 🚀 Quick Start

1. **Install Dependencies**
   ```bash
   # Required
   Paper 1.21.9 server
   
   # Optional (for economy)
   Vault plugin
   Economy plugin (e.g., EssentialsX)
   
   # Optional (for map visualization)
   BlueMap plugin
   ```

2. **Install Plugin**
   - Download latest release from [GitHub Releases](https://github.com/r7l-labs/interim/releases)
   - Place JAR in `plugins/` folder
   - Start server

3. **Check for Updates**
   ```
   /interimadmin update check
   /interimadmin update download
   ```

4. **Configure**
   - Edit `plugins/Interim/config.yml`
   - Reload: `/interimadmin reload`

5. **Create Your First Town**
   ```
   /town create MyTown
   /town claim
   /town setspawn
   ```

## 🛠️ Building from Source

```bash
# Clone repository
git clone https://github.com/r7l-labs/interim.git
cd interim

# Build with Maven
mvn clean package

# Or use the build script
./build.sh

# JAR will be in target/interim-<version>.jar
```

**Requirements:**
- Java 21 or higher
- Maven 3.6+

## 🗺️ BlueMap Setup

1. Install BlueMap plugin
2. Interim automatically detects and integrates
3. View territories on your web map with custom colors
4. Use `/interimadmin bluemap` to force updates

**Features:**
- Territory outlines with proper boundaries
- Custom colors per town/nation
- Hover labels with town names
- Automatic updates on claim changes

## 🔄 Auto-Update Feature

Stay up-to-date effortlessly:

```bash
# Check for updates
/interimadmin update check

# Download latest version
/interimadmin update download

# Restart server to apply
```

Updates download to `plugins/update/` for automatic installation on restart.

## 📖 Documentation

- [War System Guide](docs/war.md)
- [War Tutorial](docs/war_tutorial.md)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

See [LICENSE](LICENSE) file for details.

## 🙏 Support

- **Issues:** [GitHub Issues](https://github.com/r7l-labs/interim/issues)
- **Discussions:** [GitHub Discussions](https://github.com/r7l-labs/interim/discussions)

---

<div align="center">

**Made with ❤️ by [r7l-labs](https://github.com/r7l-labs)**

⭐ Star us on GitHub if you find this useful!

</div>
