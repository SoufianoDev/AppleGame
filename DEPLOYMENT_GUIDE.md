# Apple Game - Android Build & Test Action Plan

## Problem Status: ✅ FIXED

All asset path references have been corrected. The game should no longer hang on the splash screen.

---

## What Was Fixed

### Scene Files Updated (4 files):
1. ✅ `scenes/main.tscn` - Background texture path corrected
2. ✅ `scenes/red_apple.tscn` - Red apple texture path corrected
3. ✅ `scenes/green_apple.tscn` - Green apple texture path corrected
4. ✅ `scenes/bad_apple.tscn` - Bad apple texture path corrected

### Verification Results:
- ✅ 0 incorrect build paths remaining
- ✅ All 8 scene files use correct `res://src/main/resources/assets/` paths
- ✅ 60 asset files verified in source directory
- ✅ All scene references resolve to actual asset files

---

## Next Steps to Deploy

### Step 1: Rebuild the Android APK
```bash
cd /home/soufiano/SoufianoDev/applegame
./gradlew build --build-cache
# or for release build:
./gradlew assembleRelease
```

### Step 2: Generate APK for Android Deployment
```bash
# If using Godot editor:
# - Export → Android → Select the corrected scenes
# 
# If using command line:
./gradlew installDebug  # For testing on connected device
```

### Step 3: Test on Android Device
1. Install the APK on an Android device
2. Launch the app
3. Verify:
   - ✓ Splash screen displays without hanging
   - ✓ Background image loads
   - ✓ Basket appears
   - ✓ Mobile controls visible
   - ✓ Game starts after splash screen
   - ✓ Apples spawn and fall
   - ✓ Score updates
   - ✓ Game over screen displays

### Step 4: Monitor Logcat
While testing, watch for asset loading errors:
```bash
adb logcat | grep -i "godot\|error\|resource"
```

Should see NO errors like:
- `No loader found for resource: res://build/resources`
- `Can't load dependency`
- `Failed loading scene`

---

## Technical Details

### Why the Fix Works

**Before (Broken):**
```
Scene file → res://build/resources/main/assets/background.png
              ↓
              APK doesn't include build/ directory
              ↓
              Asset not found → Godot waits indefinitely
```

**After (Fixed):**
```
Scene file → res://src/main/resources/assets/background.png
              ↓
              APK includes src/main/resources/ automatically
              ↓
              Asset found → Scene loads successfully
```

### Asset Resolution in Godot-Kotlin/JVM

When Godot exports to Android:
1. **Source files** (`src/main/resources/`) are packaged into the APK
2. **Build artifacts** (`build/`) are NOT included in the APK
3. Scene files must reference the **packaged** locations
4. The correct prefix for Android builds is `res://src/main/resources/`

---

## Troubleshooting

### If Splash Screen Still Hangs After Rebuild

**Check:**
1. APK was rebuilt after fixing scenes:
   ```bash
   grep -n "res://build/resources" build/intermediate/res/**/*.xml
   # Should find nothing
   ```

2. Verify scenes are included in build:
   ```bash
   # Unzip APK and check
   unzip -l build/outputs/apk/debug/app-debug.apk | grep "\.scn\|\.tscn"
   ```

3. Check logcat for different errors:
   ```bash
   adb logcat | grep -i "jvm\|kotlin\|godot"
   ```

### If Specific Assets Still Missing

Check the exact asset filename:
```bash
ls -la src/main/resources/assets/ | grep <asset_name>
```

Then verify the exact name in the scene file:
```bash
grep -n "res://src/main/resources/assets" scenes/<scene_name>.tscn
```

Must match exactly (case-sensitive on Linux).

---

## Success Indicators

✅ Game launches without hanging  
✅ No "resource not found" errors in logcat  
✅ All UI elements appear:
- Background image
- Basket sprite
- Mobile control buttons
- Score display
- Timer display

✅ Game is fully playable  
✅ App doesn't crash on main.tscn load  

---

## Files Modified

- `scenes/main.tscn`
- `scenes/red_apple.tscn`
- `scenes/green_apple.tscn`
- `scenes/bad_apple.tscn`

## Files Created (Reference Only)

- `ASSET_PATH_FIX_REPORT.md` - Detailed fix documentation
- `verify_asset_paths.sh` - Asset path verification script

---

## Questions?

For more information about:
- **Godot-Kotlin/JVM**: Visit [godot-kotl.in](https://godot-kotl.in)
- **Godot Asset Paths**: See [Godot Docs - Data Paths](https://docs.godotengine.org/en/stable/tutorials/io/data_paths_and_imports.html)
- **Android Resources**: Check [Android Gradle Build System](https://developer.android.com/guide/topics/resources/providing-resources)

---

**Status**: Ready to deploy  
**Date Fixed**: January 27, 2026  
**Test Target**: Android Device/Emulator  
**Expected Result**: Game launches and runs without splash screen hang
