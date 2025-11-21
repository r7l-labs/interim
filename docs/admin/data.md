# Data Management

Guide to managing Interim's data files and backups.

## Data Structure

```
plugins/Interim/
├── config.yml              # Main configuration
├── data/                   # All plugin data
│   ├── towns/             # Town JSON files
│   ├── nations/           # Nation JSON files
│   ├── residents/         # Resident JSON files
│   ├── claims/            # Claim JSON files
│   └── wars/              # War JSON files (if active)
└── logs/                  # Transaction & error logs
    ├── transactions.log   # Economy transactions
    └── errors.log         # Error logging
```

## Data Format

### JSON Storage (Default)

Each entity stored as separate JSON file:

**Town example (`data/towns/uuid.json`):**
```json
{
  "uuid": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Riverside",
  "mayor": "player-uuid",
  "assistants": ["assistant-uuid"],
  "residents": ["resident1-uuid", "resident2-uuid"],
  "bank": 10000.0,
  "founded": 1700000000000,
  "settings": {
    "pvp": false,
    "explosions": false,
    "mobSpawning": true
  }
}
```

### Why JSON?

- Human-readable
- Easy to edit manually
- Git-friendly for backups
- Portable across systems

## Manual Saves

### Force Save

Save all data immediately:

```
/interimadmin save
```

**Saves:**
- All towns
- All nations
- All residents  
- All claims
- All wars

### Auto-Save

Automatic saves occur:

**Interval:**
```yaml
general:
  save-interval: 6000  # Ticks (6000 = 5 minutes)
```

**Triggers:**
- Server shutdown
- Plugin disable
- Scheduled interval
- Manual `/interimadmin save`

## Backups

### Manual Backup

1. **Save current data:**
   ```
   /interimadmin save
   ```

2. **Copy data folder:**
   ```bash
   cp -r plugins/Interim/data/ backups/interim-$(date +%Y%m%d)/
   ```

3. **Verify backup:**
   ```bash
   ls -lh backups/interim-$(date +%Y%m%d)/
   ```

### Automatic Backups

Configure in `config.yml`:

```yaml
advanced:
  # Backup interval (hours, 0 = disabled)
  backup-interval: 24
  
  # Backup retention (days)
  backup-retention: 7
```

**Backup location:**
```
plugins/Interim/backups/
├── 2025-11-21-00-00/
├── 2025-11-20-00-00/
└── 2025-11-19-00-00/
```

### Backup Script

Create automated backup script:

```bash
#!/bin/bash
# backup-interim.sh

DATE=$(date +%Y%m%d-%H%M)
BACKUP_DIR="/path/to/backups/interim-$DATE"

# Create backup directory
mkdir -p "$BACKUP_DIR"

# Copy data
cp -r /path/to/plugins/Interim/data/* "$BACKUP_DIR/"

# Compress
tar -czf "$BACKUP_DIR.tar.gz" "$BACKUP_DIR"
rm -rf "$BACKUP_DIR"

# Keep last 7 days only
find /path/to/backups/ -name "interim-*.tar.gz" -mtime +7 -delete

echo "Backup completed: $BACKUP_DIR.tar.gz"
```

**Cron job (daily at 3 AM):**
```
0 3 * * * /path/to/backup-interim.sh
```

## Restoration

### From Backup

1. **Stop server:**
   ```
   /stop
   ```

2. **Backup current (just in case):**
   ```bash
   mv plugins/Interim/data plugins/Interim/data.old
   ```

3. **Restore from backup:**
   ```bash
   cp -r backups/interim-20251121/data plugins/Interim/
   ```

4. **Start server:**
   ```
   ./start.sh
   ```

5. **Verify:**
   ```
   /town list
   /nation list
   ```

### Partial Restoration

Restore only specific data:

**Towns only:**
```bash
cp -r backups/interim-20251121/towns/* plugins/Interim/data/towns/
```

**Nations only:**
```bash
cp -r backups/interim-20251121/nations/* plugins/Interim/data/nations/
```

## Data Migration

### Server Transfer

1. **On old server:**
   ```
   /interimadmin save
   ```

2. **Copy data:**
   ```bash
   scp -r plugins/Interim/data user@newserver:/plugins/Interim/
   ```

3. **On new server:**
   ```
   /interimadmin reload
   ```

### Version Updates

**Automatic migration:**
- Plugin auto-migrates on version change
- Backup created before migration
- Console logs migration process

**Manual verification:**
```
/town list    # Check towns loaded
/nation list  # Check nations loaded
```

## Cleanup

### Remove Inactive

**Inactive towns (no residents logged in 30+ days):**

Coming soon - Auto-cleanup feature

**Manual check:**
```
/interimadmin inactive towns 30
```

### Database Cleanup

Runs automatically:

```yaml
performance:
  cleanup-interval: 24  # Hours
```

**Removes:**
- Expired invites
- Old war data
- Orphaned claims
- Stale cache

## Data Corruption

### Detection

**Signs:**
- Towns not loading
- Claims missing
- Economy errors
- Console errors: `Failed to load...`

**Check logs:**
```bash
grep ERROR logs/latest.log | grep Interim
```

### Recovery

1. **Stop server**

2. **Find corrupted file:**
   ```bash
   # Check JSON validity
   for file in plugins/Interim/data/towns/*.json; do
     jq . "$file" > /dev/null 2>&1 || echo "Corrupted: $file"
   done
   ```

3. **Remove or fix corrupted file:**
   ```bash
   mv corrupted-file.json corrupted-file.json.bak
   ```

4. **Restore from backup** (if available)

5. **Start server**

### Prevention

- Regular backups
- Monitor disk space
- Avoid forceful shutdowns
- Use UPS for power loss

## Performance

### Large Datasets

For servers with 1000+ towns:

**Enable caching:**
```yaml
performance:
  resident-cache-size: 5000
  claim-cache-size: 10000
  async-chunks: true
```

**Batch operations:**
```yaml
performance:
  batch-size: 500
```

### Optimization

**Compress old data:**
```bash
tar -czf data-archive.tar.gz plugins/Interim/data/old/
```

**See:** [Performance Guide](performance.md)

## Troubleshooting

### "Failed to save data"

**Check:**
- Disk space: `df -h`
- File permissions: `ls -la plugins/Interim/data/`
- Console errors

**Solutions:**
- Free disk space
- Fix permissions: `chmod 755 plugins/Interim/data/`
- Check for write errors in console

### Data Not Loading

**Check:**
- Files exist: `ls plugins/Interim/data/towns/`
- JSON valid: `jq . file.json`
- Console errors

**Solutions:**
- Restore from backup
- Fix JSON syntax
- Check file permissions

## See Also

- [Configuration Guide](../getting-started/configuration.md)
- [Admin Commands](../commands/admin.md)
- [Performance Guide](performance.md)
- [Troubleshooting Guide](troubleshooting.md)
