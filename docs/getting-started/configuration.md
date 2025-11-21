# Configuration Reference

Complete reference for `config.yml` settings.

## Configuration File Location

```
plugins/Interim/config.yml
```

After editing, reload with:
```
/interimadmin reload
```

## General Settings

```yaml
general:
  # Language file to use (future feature)
  language: "en_US"
  
  # Auto-save interval in ticks (20 ticks = 1 second)
  # Default: 6000 ticks = 5 minutes
  save-interval: 6000
  
  # Enable debug logging
  debug: false
```

## Town Settings

```yaml
town:
  # Cost to create a town (requires Vault)
  creation-cost: 0.0
  
  # Cost per chunk claimed
  claim-cost: 100.0
  
  # Minimum distance between towns (in chunks)
  min-distance: 5
  
  # Maximum number of claims per town
  # Set to -1 for unlimited
  max-claims: -1
  
  # Require economy for town creation
  require-economy: false
  
  # Allow mayors to delete their own towns
  allow-mayor-delete: true
  
  # Default town settings for new towns
  defaults:
    # PvP enabled in town claims
    pvp: false
    
    # Explosions damage blocks in claims
    explosions: false
    
    # Mob spawning in claims
    mob-spawning: true
    
    # Town is open (anyone can join)
    open: false
    
    # Public build access (anyone can build)
    public: false
```

## Nation Settings

```yaml
nation:
  # Cost to create a nation (requires Vault)
  creation-cost: 1000.0
  
  # Minimum number of towns required to form a nation
  min-towns: 1
  
  # Maximum number of towns in a nation
  # Set to -1 for unlimited
  max-towns: -1
  
  # Capital town can be changed
  allow-capital-change: true
  
  # Require economy for nation creation
  require-economy: false
  
  # Allow nation leaders to disband their nations
  allow-leader-disband: true
```

## Claim Settings

```yaml
claims:
  # Must claims be adjacent (touching existing claims)
  require-adjacent: true
  
  # Allow claiming in other worlds
  multiworld: true
  
  # Worlds where claiming is disabled
  disabled-worlds:
    - "world_nether"
    - "world_the_end"
  
  # Chunk border visualization
  visualization:
    # Particle type for claim borders
    # Options: REDSTONE_DUST, FLAME, VILLAGER_HAPPY, etc.
    particle: "REDSTONE_DUST"
    
    # Particle density (particles per block)
    # Higher = more particles = more visibility
    density: 1.0
    
    # Height of particle line (blocks above ground)
    height: 2
    
    # Duration particles are shown (seconds)
    duration: 30
    
    # Use continuous line (true) or dotted line (false)
    continuous: true
```

## Protection Settings

```yaml
protection:
  # Prevent block breaking in claims
  block-break: true
  
  # Prevent block placement in claims
  block-place: true
  
  # Prevent container access (chests, furnaces, etc.)
  container-access: true
  
  # Prevent entity damage (item frames, armor stands, etc.)
  entity-damage: true
  
  # Enable PvP in claims (overridden by town setting)
  pvp: false
  
  # Enable PvP in wilderness (unclaimed areas)
  wilderness-pvp: true
  
  # Prevent explosions in claims
  explosions: true
  
  # Prevent fire spread in claims
  fire-spread: true
  
  # Prevent mob spawning in claims
  mob-spawning: false
  
  # Allow residents to build in their town
  resident-build: true
  
  # Allow nation members to build in nation territory
  nation-build: false
  
  # Allow allies to build in allied territory
  ally-build: false
```

## War Settings

```yaml
war:
  # Enable war system
  enabled: true
  
  # Minimum time between war declarations (hours)
  declaration-cooldown: 24
  
  # War duration before automatic peace (hours)
  max-duration: 168  # 7 days
  
  # Allow capturing enemy territory
  territory-capture: true
  
  # War goal types available
  available-goals:
    - "CONQUEST"
    - "WEALTH"
    - "HUMILIATION"
  
  # Cost to declare war (requires Vault)
  declaration-cost: 5000.0
  
  # Allow surrendering in war
  allow-surrender: true
  
  # Penalty for surrendering
  surrender-penalty: 0.5  # 50% of war goal
```

## Economy Settings

```yaml
economy:
  # Enable economy features (requires Vault)
  enabled: true
  
  # Bank interest rate (per day)
  # 0.01 = 1% daily interest
  interest-rate: 0.01
  
  # Maximum bank balance
  # Set to -1 for unlimited
  max-balance: -1
  
  # Tax collection
  taxes:
    # Enable tax system
    enabled: false
    
    # Default tax rate (per day)
    rate: 10.0
    
    # Remove residents who can't pay taxes
    remove-on-unpaid: false
```

## Integration Settings

```yaml
integrations:
  # BlueMap web map integration
  bluemap:
    # Enable BlueMap markers
    enabled: true
    
    # Update interval (seconds)
    update-interval: 60
    
    # Show town names on markers
    show-names: true
    
    # Show nation borders
    show-nations: true
    
    # Marker detail level
    # Options: BASIC, DETAILED, FULL
    detail-level: "DETAILED"
  
  # PlaceholderAPI integration
  placeholderapi:
    # Enable placeholder expansion
    enabled: true
  
  # Vault economy integration
  vault:
    # Enable Vault integration
    enabled: true
  
  # Floodgate (Bedrock support) integration
  floodgate:
    # Enable Floodgate compatibility
    enabled: true
```

## Performance Settings

```yaml
performance:
  # Cache size for resident lookups
  resident-cache-size: 1000
  
  # Cache size for claim lookups
  claim-cache-size: 5000
  
  # Async chunk loading
  async-chunks: true
  
  # Batch size for bulk operations
  batch-size: 100
  
  # Database cleanup interval (hours)
  cleanup-interval: 24
```

## GUI Settings

```yaml
gui:
  # Menu titles
  titles:
    main-menu: "&6&lInterim Menu"
    town-menu: "&e&lTown: %town%"
    nation-menu: "&b&lNation: %nation%"
    
  # GUI sounds
  sounds:
    enabled: true
    click: "UI_BUTTON_CLICK"
    success: "ENTITY_PLAYER_LEVELUP"
    error: "ENTITY_VILLAGER_NO"
```

## Message Settings

```yaml
messages:
  # Prefix for all plugin messages
  prefix: "&8[&6Interim&8]&r"
  
  # Colors
  colors:
    primary: "&6"
    secondary: "&e"
    accent: "&b"
    success: "&a"
    error: "&c"
    warning: "&e"
    info: "&7"
  
  # Date format for timestamps
  date-format: "yyyy-MM-dd HH:mm:ss"
  
  # Number formatting
  number-format: "#,###.##"
```

## Advanced Settings

```yaml
advanced:
  # Data storage format
  # Options: JSON, YAML (JSON recommended)
  storage-format: "JSON"
  
  # Compress data files
  compress-data: false
  
  # Backup interval (hours)
  # Set to 0 to disable
  backup-interval: 24
  
  # Backup retention (days)
  backup-retention: 7
  
  # Thread pool size for async operations
  thread-pool-size: 4
  
  # API rate limits (requests per minute)
  rate-limits:
    github: 60
    default: 100
```

## Configuration Examples

### High Performance Server

```yaml
performance:
  resident-cache-size: 5000
  claim-cache-size: 10000
  async-chunks: true
  batch-size: 500
  cleanup-interval: 12

general:
  save-interval: 12000  # 10 minutes
```

### Hardcore PvP Server

```yaml
protection:
  pvp: true
  wilderness-pvp: true

town:
  defaults:
    pvp: true
    
war:
  enabled: true
  declaration-cooldown: 0
  territory-capture: true
  allow-surrender: false
```

### Economy-Focused Server

```yaml
town:
  creation-cost: 5000.0
  claim-cost: 500.0
  require-economy: true

nation:
  creation-cost: 50000.0
  require-economy: true

economy:
  enabled: true
  interest-rate: 0.02
  taxes:
    enabled: true
    rate: 50.0
    remove-on-unpaid: true
```

### Creative/Build Server

```yaml
town:
  creation-cost: 0.0
  claim-cost: 0.0
  require-economy: false

protection:
  wilderness-pvp: false
  
war:
  enabled: false
  
claims:
  require-adjacent: false
```

## Configuration Validation

After editing `config.yml`, check for errors:

```
/interimadmin reload
```

If there are errors, check the server console for details.

## Default Values

To reset to defaults:

1. Stop the server
2. Delete `plugins/Interim/config.yml`
3. Start the server (new config generated)

Or reset specific sections by deleting them from the file.

## See Also

- [Installation Guide](installation.md)
- [Performance Tuning](../admin/performance.md)
- [Troubleshooting](../admin/troubleshooting.md)
