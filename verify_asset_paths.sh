#!/bin/bash
# Apple Game - Asset Path Verification Script
# This script verifies all scene files have correct asset paths

echo "================================================"
echo "Apple Game - Asset Path Verification"
echo "================================================"
echo ""

# Check for any remaining incorrect paths
echo "1. Checking for incorrect build paths..."
INCORRECT_COUNT=$(grep -r "res://build/resources" scenes/ 2>/dev/null | wc -l)
if [ $INCORRECT_COUNT -eq 0 ]; then
    echo "   ✅ No incorrect build paths found!"
else
    echo "   ❌ Found $INCORRECT_COUNT incorrect paths"
    grep -r "res://build/resources" scenes/ 2>/dev/null
fi
echo ""

# Verify all scene files have correct paths
echo "2. Verifying scene files..."
SCENES=$(find scenes -name "*.tscn" -type f)
for scene in $SCENES; do
    # Check for asset references
    ASSETS=$(grep "res://src/main/resources/assets" "$scene" | wc -l)
    if [ $ASSETS -gt 0 ]; then
        echo "   ✅ $scene - Uses correct paths ($ASSETS asset references)"
    else
        # It's ok if a scene doesn't have assets
        echo "   ℹ️  $scene - No asset references"
    fi
done
echo ""

# List all asset directories
echo "3. Verifying asset files exist..."
if [ -d "src/main/resources/assets" ]; then
    ASSET_COUNT=$(find src/main/resources/assets -type f | wc -l)
    echo "   ✅ Found $ASSET_COUNT asset files in src/main/resources/assets/"
else
    echo "   ❌ Asset directory not found!"
fi
echo ""

echo "4. Summary of asset locations:"
echo "   - Source assets: src/main/resources/assets/"
echo "   - Build artifacts: build/resources/ (not used in scenes)"
echo "   - Scene path format: res://src/main/resources/assets/filename"
echo ""

echo "================================================"
echo "Verification Complete!"
echo "================================================"
