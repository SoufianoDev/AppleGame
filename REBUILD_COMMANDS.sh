#!/bin/bash
# Apple Game - Complete Rebuild Script
# Run this to rebuild from clean state with fixed asset paths

set -e  # Exit on error

PROJECT_DIR="/home/soufiano/SoufianoDev/applegame"

echo "================================================================================"
echo "                    APPLE GAME - COMPLETE REBUILD"
echo "================================================================================"
echo ""

# Step 1: Navigate to project
echo "📁 Changing to project directory..."
cd "$PROJECT_DIR"

# Step 2: Clean gradle
echo "🧹 Cleaning gradle build artifacts..."
./gradlew clean

# Step 3: Build the project
echo "🔨 Building project..."
./gradlew build

# Step 4: Build for Android
echo "📱 Building Android APK..."
./gradlew assembleDebug

echo ""
echo "================================================================================"
echo "                         BUILD COMPLETE!"
echo "================================================================================"
echo ""
echo "Next steps:"
echo "1. Find APK at: build/outputs/apk/debug/app-debug.apk"
echo "2. Install on device: adb install -r build/outputs/apk/debug/app-debug.apk"
echo "3. Monitor logs: adb logcat | grep -i 'error\|resource'"
echo "4. Launch game and verify splash screen loads"
echo ""
echo "================================================================================"

