# Performance Tuning

Optimize Interim for large servers and better performance.

## Configuration Optimization

### Cache Settings

For large servers (1000+ towns):

```yaml
performance:
  resident-cache-size: 5000
  claim-cache-size: 10000
```

Small servers:
```yaml
performance:
  resident-cache-size: 500
  claim-cache-size: 1000
```

### Async Operations

Enable async chunk processing:

```yaml
performance:
  async-chunks: true
  batch-size: 500
```

### Save Frequency

Reduce save frequency for less load:

```yaml
general:
  save-interval: 12000  # 10 minutes vs 5 minutes default
```

## Visualization Optimization

### Particle Density

Lower particle usage:

```yaml
claims:
  visualization:
    density: 0.5
    continuous: false
```

### BlueMap Updates

Reduce update frequency:

```yaml
integrations:
  bluemap:
    update-interval: 300  # 5 minutes
    detail-level: "BASIC"
```

## See Also

- [Configuration Guide](../getting-started/configuration.md)
- [Troubleshooting](troubleshooting.md)
