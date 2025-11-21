# Building from Source

Guide to building Interim from source code.

## Prerequisites

- **Java 21 JDK** or higher
- **Maven 3.6+**
- **Git**

## Clone Repository

```bash
git clone https://github.com/r7l-labs/interim.git
cd interim
```

## Build

### Quick Build

```bash
./build.sh
```

### Manual Build

```bash
export JAVA_HOME=/path/to/java-21
mvn clean package
```

### Output

Built JAR:
```
target/interim-<version>.jar
```

## Dependencies

Automatically resolved by Maven:

- Paper API 1.21.9
- Gson 2.10.1
- BlueMap API 2.7.2
- Vault API 1.7
- PlaceholderAPI 2.11.6

## Development

### IDE Setup

**IntelliJ IDEA:**
1. File > Open > Select `pom.xml`
2. Set Java 21 SDK
3. Maven auto-imports dependencies

**Eclipse:**
1. File > Import > Maven > Existing Maven Project
2. Select project directory
3. Set Java 21 compiler

### Running Tests

```bash
mvn test
```

## See Also

- [Contributing Guide](contributing.md)
- [API Documentation](api.md)
- [GitHub Repository](https://github.com/r7l-labs/interim)
