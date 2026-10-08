#!/bin/bash

# ================= PROJECT CONFIGURATION =================
PACKAGE_NAME="applegame"
GAME_DISPLAY_NAME="Apple Game"
VERSION="1.0-1"
ARCHITECTURE="amd64"
MAINTAINER="SoufianoDev <contact.soufianodev@gmail.com>"

# *** File paths - ALL FILES ARE IN CURRENT releases/ FOLDER ***
EXPORTED_BINARY="AppleGame-Alpha-Version_Linux.x86_64"     # In releases/
EXPORTED_PCK="AppleGame-Alpha-Version_Linux.pck"          # In releases/
JRE_SOURCE_DIR="../../jvm/jre-amd64-linux"                # Go UP 2 levels to applegame/, then jvm/
GAME_ICON="../../src/main/resources/icons/AppleGame.png"  # Go UP 2 levels to applegame/, then src/

# ================= MAIN SCRIPT =================

echo "============================================"
echo "DEBIAN PACKAGE CREATOR for Godot Kotlin/JVM"
echo "============================================"
echo "Game: $GAME_DISPLAY_NAME"
echo "Working directory: $(pwd)"
echo ""

# Check current location
if [[ ! $(basename "$(pwd)") == "releases" ]]; then
    echo "⚠️  WARNING: You should run this script from the 'releases/' folder!"
    echo "   Current folder: $(pwd)"
    echo "   Expected: .../applegame/releases/"
    read -p "Continue anyway? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Clean previous build
PACKAGE_ROOT="${PACKAGE_NAME}_${VERSION}_${ARCHITECTURE}"
echo "[-] Cleaning previous build (if exists)..."
[ -d "$PACKAGE_ROOT" ] && rm -rf "$PACKAGE_ROOT"
[ -f "${PACKAGE_ROOT}.deb" ] && rm -f "${PACKAGE_ROOT}.deb"

# 1. Create directory structure
echo "[1] Creating package structure..."
mkdir -p "${PACKAGE_ROOT}/opt/${PACKAGE_NAME}" \
         "${PACKAGE_ROOT}/usr/share/applications" \
         "${PACKAGE_ROOT}/usr/games" \
         "${PACKAGE_ROOT}/DEBIAN"

# 2. Verify and copy game files
echo "[2] Copying game files..."
echo "    Binary: $EXPORTED_BINARY"
echo "    PCK:    $EXPORTED_PCK"
echo "    JRE:    $JRE_SOURCE_DIR"
echo "    Icon:   $GAME_ICON"
echo ""

# Check main files
ERROR_COUNT=0
[ ! -f "$EXPORTED_BINARY" ] && echo "❌ ERROR: Binary not found: $EXPORTED_BINARY" && ERROR_COUNT=$((ERROR_COUNT+1))
[ ! -f "$EXPORTED_PCK" ] && echo "⚠️  WARNING: PCK file not found: $EXPORTED_PCK (may be embedded)" 
[ ! -d "$JRE_SOURCE_DIR" ] && echo "❌ ERROR: JRE folder not found: $JRE_SOURCE_DIR" && ERROR_COUNT=$((ERROR_COUNT+1))
[ ! -f "$GAME_ICON" ] && echo "⚠️  WARNING: Icon not found: $GAME_ICON"

if [ $ERROR_COUNT -gt 0 ]; then
    echo ""
    echo "💡 TROUBLESHOOTING:"
    echo "   Current directory: $(pwd)"
    echo "   Contents:"
    ls -la
    echo ""
    echo "   JRE expected at: $(realpath "$JRE_SOURCE_DIR" 2>/dev/null || echo "$JRE_SOURCE_DIR")"
    exit 1
fi

# Copy game files
echo "    Copying binary and data..."
cp "$EXPORTED_BINARY" "${PACKAGE_ROOT}/opt/${PACKAGE_NAME}/"
cp "$EXPORTED_PCK" "${PACKAGE_ROOT}/opt/${PACKAGE_NAME}/" 2>/dev/null || true

echo "    Copying JRE (this may take a moment)..."
cp -r "$JRE_SOURCE_DIR" "${PACKAGE_ROOT}/opt/${PACKAGE_NAME}/"

echo "    Copying icon..."
cp "$GAME_ICON" "${PACKAGE_ROOT}/opt/${PACKAGE_NAME}/icon.png" 2>/dev/null || {
    echo "    Creating placeholder icon..."
    # Create simple icon if ImageMagick is available
    convert -size 256x256 xc:#3498db -fill white -pointsize 40 -gravity center -draw "text 0,0 'GAME'" "${PACKAGE_ROOT}/opt/${PACKAGE_NAME}/icon.png" 2>/dev/null || \
    echo "    Using default game icon"
}

# 3. Create launcher
echo "[3] Creating launcher..."
cat > "${PACKAGE_ROOT}/usr/games/${PACKAGE_NAME}" << 'EOF'
#!/bin/bash
# Apple Game Launcher
GAME_DIR="/opt/applegame"
cd "$GAME_DIR"
exec "./AppleGame-Alpha-Version_Linux.x86_64" "$@"
EOF
chmod 755 "${PACKAGE_ROOT}/usr/games/${PACKAGE_NAME}"

# 4. Create desktop entry
echo "[4] Creating desktop entry..."
cat > "${PACKAGE_ROOT}/usr/share/applications/${PACKAGE_NAME}.desktop" << EOF
[Desktop Entry]
Version=1.0
Type=Application
Name=$GAME_DISPLAY_NAME
Comment=A game built with Godot and Kotlin
Exec=/usr/games/$PACKAGE_NAME
Icon=/opt/$PACKAGE_NAME/icon.png
Terminal=false
Categories=Game;
Keywords=game;godot;
EOF

# 5. Create control file
echo "[5] Creating package metadata..."
INSTALLED_SIZE=$(du -sk "${PACKAGE_ROOT}/opt" 2>/dev/null | cut -f1)
[ -z "$INSTALLED_SIZE" ] && INSTALLED_SIZE=250000  # Default ~250MB

cat > "${PACKAGE_ROOT}/DEBIAN/control" << EOF
Package: $PACKAGE_NAME
Version: $VERSION
Architecture: $ARCHITECTURE
Maintainer: $MAINTAINER
Installed-Size: $INSTALLED_SIZE
Depends: libc6 (>= 2.31), libstdc++6 (>= 4.8.1)
Section: games
Priority: optional
Homepage: https://soufianodev.github.io
Description: $GAME_DISPLAY_NAME - Kotlin/JVM Edition
 A complete game built with Godot Engine and Kotlin/JVM.
 This package includes an embedded Java Runtime Environment (JRE)
 for seamless execution without requiring Java installation.
EOF

# 6. Create post-install script
echo "[6] Creating installation scripts..."
cat > "${PACKAGE_ROOT}/DEBIAN/postinst" << 'EOF'
#!/bin/sh
set -e
# Update desktop database
if [ -x "$(command -v update-desktop-database)" ]; then
    update-desktop-database /usr/share/applications 2>/dev/null || true
fi
# Set proper permissions
chmod 755 /opt/applegame/AppleGame-Alpha-Version_Linux.x86_64 2>/dev/null || true
echo ""
echo "✨ Apple Game has been successfully installed!"
echo ""
echo "To play the game:"
echo "   • Launch from Applications → Games → Apple Game"
echo "   • Or type 'applegame' in terminal"
echo ""
echo "To uninstall: sudo apt remove applegame"
echo ""
EOF
chmod 755 "${PACKAGE_ROOT}/DEBIAN/postinst"

# Create pre-remove script
cat > "${PACKAGE_ROOT}/DEBIAN/prerm" << 'EOF'
#!/bin/sh
set -e
# Cleanup before removal
exit 0
EOF
chmod 755 "${PACKAGE_ROOT}/DEBIAN/prerm"

# 7. Build the package
echo "[7] Building Debian package..."
echo "    This may take a minute due to JRE size..."

if ! command -v dpkg-deb >/dev/null 2>&1; then
    echo "❌ ERROR: dpkg-deb not found!"
    echo "    Install with: sudo apt install dpkg"
    exit 1
fi

if dpkg-deb --build --root-owner-group "${PACKAGE_ROOT}" >/tmp/deb-build.log 2>&1; then
    if [ -f "${PACKAGE_ROOT}.deb" ]; then
        PACKAGE_SIZE=$(du -h "${PACKAGE_ROOT}.deb" | cut -f1)
        echo ""
        echo "============================================"
        echo "✅ PACKAGE CREATED SUCCESSFULLY!"
        echo "============================================"
        echo "📦 Package: ${PACKAGE_ROOT}.deb"
        echo "💾 Size:    $PACKAGE_SIZE"
        echo ""
        echo "📋 INSTALLATION:"
        echo "   cd $(pwd)"
        echo "   sudo apt install ./${PACKAGE_ROOT}.deb"
        echo ""
        echo "🎮 TO PLAY:"
        echo "   Type: applegame"
        echo "   Or find in Applications menu"
        echo ""
        echo "🗑️  UNINSTALL:"
        echo "   sudo apt remove applegame"
        echo ""
        echo "🔍 CHECK CONTENTS:"
        echo "   dpkg -c ${PACKAGE_ROOT}.deb | head -20"
        echo ""
    else
        echo "❌ ERROR: Package file not created!"
        echo "    Check log: /tmp/deb-build.log"
        cat /tmp/deb-build.log | tail -20
    fi
else
    echo "❌ ERROR: Failed to build package!"
    echo "    Check log: /tmp/deb-build.log"
    cat /tmp/deb-build.log | tail -20
fi

# Cleanup
echo ""
echo "[8] Cleaning temporary files..."
rm -rf "${PACKAGE_ROOT}"
echo "    Done!"
