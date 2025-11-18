#!/bin/bash

# Interim Build Script
# Builds the plugin and optionally copies it to a test server

echo "Building Interim plugin..."

# Clean and build
mvn clean package

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ Build successful!"
    echo "JAR location: target/interim-dev-0.0.8.jar"
    echo ""
    
    # Optional: Copy to test server
    if [ -d "test-server/plugins" ]; then
        echo "Copying to test server..."
        cp target/interim-dev-0.0.8.jar test-server/plugins/
        echo "✓ Copied to test server!"
    fi
else
    echo ""
    echo "✗ Build failed!"
    exit 1
fi
