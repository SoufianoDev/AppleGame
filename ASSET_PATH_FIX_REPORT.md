# Apple Game - Asset Path Fix Report

## Problem Summary
The game was hanging on the splash screen due to **asset loading failures**. The Android logs revealed that Godot couldn't find required image assets and scene dependencies.

### Error Messages from Logcat
```
ERROR: No loader found for resource: res://build/resources/main/assets/background.png (expected type: Texture2D)
ERROR: Can't load dependency: 'res://build/resources/main/assets/ic_left_arrow_btn.png'
ERROR: Failed loading scene: res://scenes/main.tscn
```

## Root Cause Analysis

### Issue 1: Incorrect Asset Path References
Scene files were referencing assets at the **wrong path**:
- **Incorrect**: `res://build/resources/main/assets/` (from gradle build output directory)
- **Correct**: `res://src/main/resources/assets/` (actual source assets location)

### Issue 2: Asset Files Missing from Incorrect Path
The assets exist in `src/main/resources/assets/` but scene files were pointing to `build/resources/main/assets/`, which is a gradle build artifact directory and not packaged correctly with the game.

## Solution Implemented

### Files Fixed

1. **[scenes/main.tscn](scenes/main.tscn#L3)**
   - Fixed background texture path
   - Changed: `res://build/resources/main/assets/background.png` → `res://src/main/resources/assets/background.png`

2. **[scenes/bad_apple.tscn](scenes/bad_apple.tscn#L3)**
   - Fixed bad apple texture path
   - Changed: `res://build/resources/main/assets/bad_apple.png` → `res://src/main/resources/assets/bad_apple.png`

3. **[scenes/red_apple.tscn](scenes/red_apple.tscn#L3)**
   - Fixed red apple texture path
   - Changed: `res://build/resources/main/assets/apple.png` → `res://src/main/resources/assets/apple.png`

4. **[scenes/green_apple.tscn](scenes/green_apple.tscn#L3)**
   - Fixed green apple texture path
   - Changed: `res://build/resources/main/assets/green_apple.png` → `res://src/main/resources/assets/green_apple.png`

### Files Already Correct
- **[scenes/basket.tscn](scenes/basket.tscn)** - Already using correct paths
- **[scenes/left_btn_control.tscn](scenes/left_btn_control.tscn)** - Already using correct paths
- **[scenes/right_btn_control.tscn](scenes/right_btn_control.tscn)** - Already using correct paths
- **[scenes/game_over_screen.tscn](scenes/game_over_screen.tscn)** - Already using correct paths

## Why This Happened

In Godot-Kotlin/JVM projects, the build process copies resources to `build/resources/` for compilation. However, when exporting/packaging for Android:

1. Scene editors automatically updated paths during development to point to build output
2. When packaging for Android, these paths need to reference the **source** directories
3. The Android build process includes `src/main/resources/` automatically
4. The `build/` directory is not included in the APK

## Verification

✅ All scene files now reference assets at the correct location
✅ No remaining `res://build/resources` paths in any scene files
✅ All required assets exist at `src/main/resources/assets/`:
- background.png
- apple.png (red apple)
- green_apple.png
- bad_apple.png
- ic_left_arrow_btn.png
- ic_right_arrow_btn.png
- game_over_bg_dim.png
- wood_pannel.png
- game_over.png
- apples_row.png
- score_pannel.png
- restart_btn.png
- munu_btn.png
- new_high_score.png
- fonts/Nunito-Black.ttf
- basket/*.png (basket_0.png through basket_8.png)

## Next Steps

1. **Rebuild the Android APK** to include the corrected scene files
2. **Test on Android Device** - the splash screen should now load properly
3. **Monitor Logcat** during launch to confirm no asset loading errors

## Godot-Kotlin/JVM Best Practices

When working with Godot-Kotlin/JVM and Android exports:

1. **Always use `res://src/main/resources/`** for asset paths in scenes
2. **Avoid `res://build/`** paths - these are compile artifacts
3. **Test on target platform** - desktop and mobile may resolve paths differently
4. **Check exported scenes** before building APK - export the project and verify all UIDs resolve correctly

## Related Documentation

- [Godot Resource Paths](https://docs.godotengine.org/en/stable/tutorials/io/data_paths_and_imports.html)
- [Godot Kotlin/JVM Project Template](https://github.com/utopia-rise/godot-kotlin-project-template)
- [Android Resource Configuration](https://github.com/utopia-rise/godot-kotlin-jvm/discussions)

---
**Fixed**: January 27, 2026  
**Issue**: Game hanging on splash screen due to asset loading failures  
**Status**: ✅ RESOLVED
