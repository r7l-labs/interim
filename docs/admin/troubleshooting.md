# Troubleshooting Guide

Solutions to common Interim issues.

## Common Issues

### Plugin Won't Load

**Symptoms:**
- Plugin shows red in `/plugins`
- Console errors on startup
- Commands don't work

**Solutions:**

1. **Check Java version:**
   ```bash
   java -version  # Must be Java 21+
   ```

2. **Check Paper version:**
   ```
   /version  # Must be 1.21.9+
   ```

3. **Check dependencies:**
   - Vault (for economy)
   - PlaceholderAPI (for placeholders)

4. **Check console errors:**
   ```bash
   grep "Interim" logs/latest.log
   ```

### Commands Not Working

**Symptoms:**
- `/town` does nothing
- "Unknown command" errors
- Permission denied

**Solutions:**

1. **Check permissions:**
   ```
   /lp user <player> permission check interim.town.create
   ```

2. **Grant permissions:**
   ```
   /lp group default permission set interim.town.* true
   ```

3. **Check plugin loaded:**
   ```
   /plugins | grep Interim
   ```

### Can't Claim Land

**Symptoms:**
- "Cannot claim here"
- "Too close to another town"
- "Must be adjacent"

**Solutions:**

1. **Check adjacency:**
   - Claims must touch existing claims
   - Disable: `claims.require-adjacent: false`

2. **Check distance:**
   - Minimum distance from other towns
   - Adjust: `town.min-distance: 5`

3. **Check world:**
   - World may be disabled
   - Remove from: `claims.disabled-worlds`

4. **Check funds:**
   - Need money if economy enabled
   - Cost: `town.claim-cost`

### Economy Not Working

**Symptoms:**
- "Economy not enabled"
- Bank commands fail
- No cost for claiming

**Solutions:**

1. **Install Vault:**
   - Download from SpigotMC
   - Place in `plugins/`

2. **Install economy plugin:**
   - EssentialsX recommended
   - Must load before Interim

3. **Enable in config:**
   ```yaml
   economy:
     enabled: true
   ```

4. **Verify economy:**
   ```
   /balance
   /eco give <player> 1000
   ```

### BlueMap Not Showing Claims

**Symptoms:**
- Claims not on web map
- Colors not displaying
- Map outdated

**Solutions:**

1. **Check BlueMap installed:**
   ```
   /plugins | grep BlueMap
   ```

2. **Enable integration:**
   ```yaml
   integrations:
     bluemap:
       enabled: true
   ```

3. **Force update:**
   ```
   /interimadmin bluemap update
   ```

4. **Check BlueMap rendered:**
   ```
   /bluemap update
   ```

### Particles Not Showing

**Symptoms:**
- `/map` shows nothing
- No chunk borders
- Particles invisible

**Solutions:**

1. **Check client settings:**
   - Options > Video Settings > Particles: "All"

2. **Check particle type:**
   ```yaml
   claims:
     visualization:
       particle: "REDSTONE_DUST"
   ```

3. **Try different particle:**
   ```yaml
   particle: "FLAME"
   ```

4. **Run command again:**
   ```
   /map
   ```

### Performance Issues

**Symptoms:**
- Server lag
- TPS drops
- Slow map command
- Lag during saves

**Solutions:**

1. **Reduce particle density:**
   ```yaml
   claims:
     visualization:
       density: 0.5
       continuous: false
   ```

2. **Increase save interval:**
   ```yaml
   general:
     save-interval: 12000  # 10 minutes
   ```

3. **Enable async:**
   ```yaml
   performance:
     async-chunks: true
   ```

4. **Increase cache:**
   ```yaml
   performance:
     resident-cache-size: 5000
     claim-cache-size: 10000
   ```

See: [Performance Guide](performance.md)

### Data Not Saving

**Symptoms:**
- Changes lost on restart
- Towns disappear
- Claims reset

**Solutions:**

1. **Check disk space:**
   ```bash
   df -h
   ```

2. **Check permissions:**
   ```bash
   ls -la plugins/Interim/data/
   ```

3. **Force save:**
   ```
   /interimadmin save
   ```

4. **Check console errors:**
   ```bash
   grep "save" logs/latest.log
   ```

5. **Verify files exist:**
   ```bash
   ls plugins/Interim/data/towns/
   ```

### PlaceholderAPI Not Working

**Symptoms:**
- Placeholders show as text
- `%interim_town_name%` not replaced
- Chat format broken

**Solutions:**

1. **Install PlaceholderAPI:**
   - Download from Spigot
   - Place in `plugins/`

2. **Enable integration:**
   ```yaml
   integrations:
     placeholderapi:
       enabled: true
   ```

3. **Restart server**

4. **Test placeholder:**
   ```
   /papi parse me %interim_town_name%
   ```

See: [PlaceholderAPI Integration](../integrations/placeholderapi.md)

## Error Messages

### "Town name already exists"

**Cause:** Name is taken

**Solution:** Choose different name or delete old town

### "You are already in a town"

**Cause:** Can only be in one town

**Solution:** Leave current town first: `/town leave`

### "Insufficient funds"

**Cause:** Not enough money

**Solutions:**
- Deposit to town bank: `/town deposit <amount>`
- Ask admin for funds
- Reduce costs in config

### "Cannot kick the mayor"

**Cause:** Mayors can't be kicked

**Solution:** Mayor must delete town or transfer leadership

### "Town is a nation capital"

**Cause:** Capital can't leave nation

**Solution:** Disband nation first or change capital

## Console Errors

### "Failed to load town UUID"

**Cause:** Corrupted town data file

**Solution:**
1. Find file: `plugins/Interim/data/towns/<uuid>.json`
2. Validate JSON: `jq . file.json`
3. Fix or restore from backup

### "NullPointerException"

**Cause:** Missing data or plugin bug

**Solution:**
1. Check console for full stack trace
2. Report to GitHub issues
3. Include error logs

### "Could not connect to database"

**Cause:** MySQL connection failed (if using MySQL)

**Solution:**
- Check MySQL running
- Verify credentials in config
- Test connection

## Getting Help

### Information to Provide

When asking for help, include:

1. **Server version:**
   ```
   /version
   ```

2. **Plugin version:**
   ```
   /plugins | grep Interim
   ```

3. **Error logs:**
   ```bash
   grep ERROR logs/latest.log | grep Interim
   ```

4. **Config (if relevant):**
   ```yaml
   # Relevant config section
   ```

5. **Steps to reproduce**

### Where to Get Help

- **GitHub Issues:** https://github.com/r7l-labs/interim/issues
- **Discord:** (if available)
- **SpigotMC:** (if posted there)

## Debug Mode

### Enable Debug Logging

```yaml
general:
  debug: true
```

**Restart server**

**Check logs:**
```bash
tail -f logs/latest.log | grep Interim
```

**Provides:**
- Detailed operation logs
- Data load/save info
- Performance metrics
- Error contexts

## See Also

- [Installation Guide](../getting-started/installation.md)
- [Configuration Guide](../getting-started/configuration.md)
- [Data Management](data.md)
- [Performance Guide](performance.md)
- [Admin Commands](../commands/admin.md)
