# AppleGame

A fast-paced arcade-style game developed with Godot 4.4 and Kotlin using the Godot-Kotlin JVM binding. Players control a basket to catch falling apples while avoiding bad apples in a 60-second challenge.

## Project Overview

AppleGame is a simple yet engaging 2D game where the objective is to maximize your score by catching red and green apples while avoiding bad apples. The game features progressive difficulty that increases over time, requiring players to react faster as the game advances.

### Game Screen Layout

#### Visual Representation

```
+--------------------------------------------------------------+
|  SCORE: 35                                                   |
|  TIME:  04                                                   |
|                                                              |
|  ~~~~~~~~~~~~~~~~~~~~~~~~~ SKY ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ |
|                                                              |
|        O(Red)          O(Green)         O(Bad)               |
|         |               |                 *                  |
|         |               |                / \                 |
|         |               |               /   \                |
|        \|/             \|/             v   v                 |
|                                                              |
|                                                              |
|                                                              |
|                                                              |
|                    _____________                             |
|                   |             |                            |
|                   |   BASKET    |  <- Player controlled      |
|                   |_____________|                            |
|                                                              |
|  ======================== GRASS ============================ |
+--------------------------------------------------------------+
```

#### Layout Zones

**1. HUD Zone (Top Left)**
```
[Score: 35]
[Time: 04/60]
```
- Fixed position, always visible
- Shows current score and remaining game time
- White text on dark background for clarity

**2. Gameplay Area (Center)**
- Resolution: 1920 x 1080 pixels
- Blue sky background with cloud decorations
- Falling objects (apples) spawn at random X positions from top
- Basket positioned at bottom center, player-controlled

**3. Game Objects**
```
Falling Apples:
├─ Red Apple (O)    → +10 points
├─ Green Apple (O)  → +15 points
└─ Bad Apple (*)    → Hazard (avoid)

Player Basket:
├─ Position: Bottom center, Y ~80% down
├─ Width: ~80 pixels
├─ Height: ~40 pixels
└─ Movement: A/D keys or arrow keys (left/right)
```

**4. Ground/Grass Area**
- Visual indicator at bottom of screen
- Collision boundary for apples
- Green colored strip

#### Coordinate System

```
(0, 0) ─────────────────── (1920, 0)
  ┌─────────────────────────────────┐
  │        Screen Bounds            │
  │                                 │
  │  Apple spawn area:              │
  │  X: 0 to 1920 (random)          │
  │  Y: -100 to 0 (top)             │
  │                                 │
  │  Gameplay area:                 │
  │  X: 0 to 1920                   │
  │  Y: 0 to 1000                   │
  │                                 │
  │  Basket position:               │
  │  X: Controlled by player        │
  │  Y: ~850 (bottom)               │
  │                                 │
  │  Collision zone (basket):       │
  │  Area extends ~50px above base  │
  └─────────────────────────────────┘
(0, 1080) ──────────────── (1920, 1080)
```

## Game Mechanics

- **Player Control**: Use 'A' and 'D' keys (or arrow keys) to move the basket left and right
- **Apples**: Three types of falling objects
    - Red Apples: Standard scoring items (+10 points)
    - Green Apples: Alternative scoring items (+15 points)
    - Bad Apples: Obstacles to avoid (game hazard)
- **Scoring**: Earn points by catching good apples
- **Difficulty**: Game automatically increases spawn rate over 60 seconds
- **Duration**: Game runs for exactly 60 seconds

### Difficulty Progression

```
Spawn Interval Over Time
├─ Start: 1.5 seconds between apples
├─ Decrease by factor: 0.98 per frame
├─ Minimum interval: 0.5 seconds (fastest)
└─ Result: Game gets progressively harder as time passes

Timeline (60 seconds):
0s ─────────────────── 30s ─────────────────── 60s
□ □ □              □ □ □ □ □ □           □ □ □ □ □ □ □
(slower)           (medium speed)       (very fast)
```

### Game State Flow

```
         START
           |
           v
    Load Apple Scenes
           |
           v
    Initialize Game Timer (60s)
           |
           v
    +-----------+
    | Game Loop |
    +-----------+
    |
    +---> Check Elapsed Time
    |
    +---> Decrease Spawn Interval
    |
    +---> Spawn Apples (random type)
    |
    +---> Update Basket Position
    |
    +---> Check Collisions
    |
    +---> Update Score
    |
    +---> Repeat until time < 60s
           |
           v
        GAME OVER
           |
           v
    Display Final Score
```

## Project Structure

```
applegame/
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── godot/
│       │       ├── entry/                   # Auto-generated registrar classes
│       │       │   ├── MainRegistrar.kt
│       │       │   ├── BasketRegistrar.kt
│       │       │   ├── GameOverScreenRegistrar.kt
│       │       │   ├── ScoreControllerRegistrar.kt
│       │       │   └── TimingModeGameplayRegistrar.kt
│       │       │
│       │       └── game/
│       │           ├── Main.kt              # Main game controller
│       │           │   - UI binding (score, timer, mobile controls)
│       │           │   - Lifecycle management (start/restart)
│       │           │   - Signal routing from gameplay to UI
│       │           │   - Mobile control opacity configuration
│       │           │
│       │           ├── TimingModeGameplay.kt # Core gameplay logic
│       │           │   - Apple spawning with wave system
│       │           │   - Burst spawning mechanics
│       │           │   - Smart positioning algorithm
│       │           │   - Difficulty progression
│       │           │   - Physics variation per apple type
│       │           │   - Timer management
│       │           │   - Cleanup & restart handling
│       │           │
│       │           ├── Basket.kt            # Player controller
│       │           │   - Unified input (keyboard + mobile)
│       │           │   - Touch control handlers
│       │           │   - Movement with lean animation
│       │           │   - Squash & stretch on collision
│       │           │   - Dynamic texture (0-8 apples)
│       │           │   - Screen boundary clamping
│       │           │
│       │           ├── ScoreController.kt   # Score management
│       │           │   - Score arithmetic (add/set/reset)
│       │           │   - Dynamic formatting (K/M/B)
│       │           │   - Anti-cheat detection
│       │           │
│       │           ├── GameOverScreen.kt    # End screen UI
│       │           │   - Score display
│       │           │   - Restart button with animation
│       │           │   - Button press/release handling
│       │           │   - Tween animation management
│       │           │
│       │           ├── AnimationStyle.kt    # Animation presets
│       │           │   - BUTTON_PRESS style
│       │           │   - BUTTON_RELEASE style
│       │           │
│       │           └── apples/
│       │               ├── BaseApple.kt     # Base apple class
│       │               │   - RigidBody2D physics
│       │               │   - Collision detection
│       │               │   - Auto-cleanup on exit
│       │               │
│       │               ├── RedApple.kt      # Standard apple (10 pts)
│       │               ├── GreenApple.kt    # Premium apple (15 pts)
│       │               └── BadApple.kt      # Hazard (-1 apple count)
│       │
│       └── resources/
│           ├── assets/                      # Game assets
│           │   ├── basket/                  # Basket states (0-8 apples)
│           │   │   ├── basket_0.png
│           │   │   ├── basket_1.png
│           │   │   └── ... (up to basket_8.png)
│           │   │
│           │   ├── fonts/                   # UI fonts
│           │   ├── ic_left_arrow_btn.png    # Mobile left control
│           │   ├── ic_right_arrow_btn.png   # Mobile right control
│           │   ├── red_apple.png
│           │   ├── green_apple.png
│           │   ├── bad_apple.png
│           │   ├── game_over.png            # Game over text
│           │   ├── restart_btn.png          # Restart button
│           │   ├── score_pannel.png         # Score background
│           │   └── ... (additional UI assets)
│           │
│           └── META-INF/
│
├── scenes/
│   ├── main.tscn                           # Main game scene
│   │   ├── Main (Node2D) - Game controller
│   │   ├── Basket (Area2D) - Player
│   │   ├── CanvasLayer - Score display
│   │   ├── CanvasLayer2 - Timer display
│   │   └── MobileControls (CanvasLayer)
│   │       ├── LeftBtnControl (TextureButton)
│   │       └── RightBtnControl (TextureButton)
│   │
│   ├── game_over_screen.tscn               # Game over UI
│   │   └── BackgroundDim/CenterContainer
│   │       └── ContentRoot/ScorePannelRoot
│   │           ├── ScoreNumber (Label)
│   │           └── RestartButton (TextureButton)
│   │
│   ├── basket.tscn                         # Basket scene
│   │   └── Area2D with Sprite2D
│   │
│   ├── red_apple.tscn                      # Red apple scene
│   │   └── RigidBody2D with Sprite2D
│   │
│   ├── green_apple.tscn                    # Green apple scene
│   │   └── RigidBody2D with Sprite2D
│   │
│   ├── bad_apple.tscn                      # Bad apple scene
│   │   └── RigidBody2D with Sprite2D
│   │
│   ├── left_btn_control.tscn               # Mobile left control
│   │   └── TextureButton
│   │
│   └── right_btn_control.tscn              # Mobile right control
│       └── TextureButton
│
├── gdj/
│   └── godot/
│       └── game/                           # Generated script registration files (.gdj)
│           ├── Main.gdj
│           ├── Basket.gdj
│           ├── GameOverScreen.gdj
│           ├── ScoreController.gdj
│           ├── TimingModeGameplay.gdj
│           └── apples/
│               ├── RedApple.gdj
│               ├── GreenApple.gdj
│               └── BadApple.gdj
│
├── build/                                   # Build output (generated)
│   ├── classes/                             # Compiled Kotlin classes
│   ├── generated/                           # KSP generated code
│   ├── libs/                                # JAR files
│   └── resources/                           # Packaged resources
│
├── gradle/
│   └── wrapper/                             # Gradle wrapper
│
├── build.gradle.kts                        # Gradle build configuration
├── settings.gradle.kts                     # Gradle settings
├── gradle.properties                       # Gradle properties
├── godot_kotlin_configuration.json         # Kotlin JVM runtime configuration
├── project.godot                           # Godot project configuration
├── icon.svg.import                         # Icon asset import config
└── README.md                                # This file
```

## Key Components

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                     Main.kt (Scene Root)                     │
│  - UI Binding (Score, Timer, Mobile Controls)               │
│  - Lifecycle Management (Start/Restart)                     │
│  - Signal Routing (Gameplay → UI)                           │
│  - Mobile Controls Opacity Configuration                    │
└───────────────┬──────────────────────┬──────────────────────┘
                │                      │
                v                      v
    ┌───────────────────┐    ┌─────────────────────┐
    │ TimingModeGameplay│    │   GameOverScreen    │
    │  (Gameplay Logic) │    │   (End Screen UI)   │
    └────────┬──────────┘    └──────────┬──────────┘
             │                           │
             │ Signals:                  │ Signals:
             │ - scoreUpdated            │ - restartRequested
             │ - timerUpdated            │
             │ - gameOverSignal          │ Uses:
             │                           │ - AnimationStyle
             │ Creates & Manages:        │   (Button animations)
             │                           │
    ┌────────┴────────┬─────────────────┴──────────┐
    │                 │                             │
    v                 v                             v
┌────────────┐  ┌──────────────┐        ┌─────────────────┐
│  Basket.kt │  │ScoreController│        │  Mobile Controls│
│  (Player)  │  │ (Score Mgmt) │        │  (Touch Buttons)│
│            │  │              │        │                 │
│ - Unified  │  │ - Add/Set    │        │ - Left Button   │
│   Input    │  │ - Format     │        │ - Right Button  │
│ - Mobile   │  │   (K/M/B)    │        │ - Opacity Ctrl  │
│   Handlers │  │ - Anti-cheat │        │                 │
│ - Squash & │  │              │        │ Connected to:   │
│   Stretch  │  │              │        │ Basket handlers │
│ - Dynamic  │  │              │        │                 │
│   Texture  │  │              │        │                 │
└────────────┘  └──────────────┘        └─────────────────┘
    ^
    │ Collects
    │
┌───┴─────────────────────────────────┐
│         Apple Hierarchy              │
│                                      │
│  ┌──────────────────┐               │
│  │  BaseApple.kt    │               │
│  │ (RigidBody2D)    │               │
│  └────────┬─────────┘               │
│           │                          │
│  ┌────────┼────────┬────────┐       │
│  │        │        │         │       │
│  v        v        v         v       │
│ Red    Green     Bad     Custom     │
│(10pt)  (15pt)  (Hazard) (Future)    │
│                                      │
│ Physics Variations:                  │
│ - Red: Standard gravity + drift     │
│ - Green: Light (0.7x) + floaty      │
│ - Bad: Heavy (1.6x) + torque        │
└──────────────────────────────────────┘
```

### Main.kt (Scene Root Controller)
Main responsibilities:
- **UI Binding**: Connects score labels, timer labels, and mobile control buttons
- **Lifecycle Management**: Creates/destroys TimingModeGameplay instances
- **Signal Routing**: Receives signals from gameplay and updates UI
- **Mobile Controls**: Configures opacity and connects touch buttons to Basket
- **Restart Handling**: Manages cleanup and restart flow

**Key Properties:**
- `gameDuration`: Total game time (60 seconds, configurable)
- `defaultMobileOpacity`: Initial opacity for mobile controls (0.1 = 10%)

**Mobile Control Methods:**
- `applyMobileOpacity(opacity)`: Set button transparency (0.0-1.0)
- `showMobileControls()`: Full opacity (1.0)
- `hideMobileControls()`: Invisible (0.0)
- `fadeMobileControls()`: Semi-transparent (0.5)

### TimingModeGameplay.kt (Core Gameplay)
Handles all gameplay mechanics:
- **Wave System**: Tracks waves and apples spawned per wave
- **Burst Spawning**: Spawns 1-3 apples based on wave and random roll
- **Smart Positioning**: Ensures apples don't spawn too close (180px min)
- **Physics Variation**: Applies different gravity/impulse per apple type
- **Difficulty Scaling**: Decreases spawn interval over time
- **Timer Management**: Counts down from 60s and triggers game over
- **Cleanup**: Explicit cleanup method for proper restart

**Key Properties:**
- `initialSpawnInterval`: 1.8s (starting spawn rate)
- `minSpawnInterval`: 0.4s (fastest spawn rate)
- `difficultyIncreaseRate`: 0.97 (multiplier per spawn)
- `applesPerWave`: 12 apples (wave progression)

**Burst Logic:**
```
Wave 1-2: Always 1 apple
Wave 3-5: 70% → 1 apple, 30% → 2 apples
Wave 6+:  70% → 1 apple, 15% → 2 apples, 15% → 3 apples
```

### Basket.kt (Player Controller)
Features:
- **Unified Input System**: Combines keyboard and mobile touch seamlessly
- **Mobile Handlers**: `onMobileLeftDown/Up()` and `onMobileRightDown/Up()`
- **Smooth Movement**: Velocity-based with delta timing
- **Visual Juice**: Lean rotation and squash/stretch on collision
- **Dynamic Texture**: Changes based on apple count (0-8 apples)
- **Screen Clamping**: Prevents basket from leaving screen bounds

**Key Properties:**
- `speed`: 850.0 units/second
- `leanAmount`: 0.15 radians (rotation angle when moving)
- `squashAmount`: 0.35 (vertical compression on catch)
- `stretchAmount`: 0.22 (vertical extension after squash)

**Input System:**
```
Keyboard Input              Mobile Input
(ui_to_left/right)         (isLeftPressed/isRightPressed)
        |                            |
        +----------------------------+
                    |
                    v
          Unified Input (inputX)
                    |
                    v
          Calculate Velocity
                    |
                    v
          Apply Delta Movement
                    |
                    v
          Screen Boundary Clamp
                    |
                    v
          Visual Lean Animation
```

**Squash & Stretch:**
```
Collision → SQUASH (0.10s) → STRETCH (0.12s) → SETTLE (0.18s) → IDLE
            Scale: (4.35,3.65)  (3.78,4.22)     (4.0,4.0)
```

### ScoreController.kt (Score Management)
- **Score Operations**: Add, set, get, reset
- **Dynamic Formatting**: 1,000→"1K", 1,500→"1.5K", 1M→"1M", 1B→"1B"
- **Anti-Cheat**: Displays "--" for scores > 1B

### GameOverScreen.kt (End Screen)
- **Score Display**: Shows final formatted score
- **Animated Button**: Press/release animations using AnimationStyle
- **Double-Click Prevention**: Animation state management

### Apple Hierarchy

```
        RigidBody2D (Godot Physics)
             |
             v
      ┌──────────────┐
      │  BaseApple   │
      │              │
      │ Properties:  │
      │ - velocity   │
      │ - lifetime   │
      │ - type       │
      │ - collision  │
      └──────────────┘
             ^
             |
    ┌────────┼────────┬────────┐
    |        |        |        |
    v        v        v        v
┌────────┐┌────────┐┌────────┐┌────────┐
│RedApple││GreenApp││BadApple││Unique  │
│Points:1││Points:1││Hazard  ││Features│
└────────┘└────────┘└────────┘└────────┘

All apples:
- Start at random top position
- Fall under gravity (RigidBody2D physics)
- Trigger collision event on basket contact
- Auto-delete when off-screen
```

## Display Settings

- **Resolution**: 1920x1080 (Full HD)
- **Rendering**: GL Compatibility mode (Godot 4.4)
- **Stretch Mode**: Canvas items with aspect expansion

## Collision and Scoring System

```
                Apple Spawned
                     |
                     v
            Falls towards ground
            (RigidBody2D physics)
                     |
         ┌───────────┼───────────┐
         |           |           |
         v           v           v
    Good Apple   Good Apple   Bad Apple
    (Red/Green)  (Red/Green)   (Hazard)
         |           |           |
         v           v           v
    Basket near?  Basket near?  Basket near?
         |           |           |
       YES          NO          YES
         |           |           |
         v           v           v
    Award Pts   Delete Apple   Game Event
    (10-15)     (off-screen)   (Hazard trigger)
         |
         v
    Update Score
    (Display label)
         |
         v
    Scale/Rotate Basket
    (Visual feedback)
```

## Build System and Compilation

```
Source Code (.kt files)
    |
    v
Gradle Build Process
    |
    +---> KSP (Kotlin Symbol Processing)
    |     - Scans @RegisterClass annotations
    |     - Scans @RegisterFunction annotations
    |     - Scans @RegisterProperty annotations
    |
    +---> Generate .gdj Files (Script Registration)
    |     - Stored in gdj/ directory
    |     - Used by Godot to load Kotlin scripts
    |
    +---> Compile Kotlin to JVM Bytecode
    |     - Output: build/classes/kotlin/main/
    |
    +---> Package Resources
    |     - Assets copied to build/resources/
    |
    v
JAR File (build/libs/applegame.jar)
    |
    v
Godot Engine Loads JVM Scripts
    |
    v
Game Ready to Run
```

### Annotation System

```
Kotlin Code                 Generated Output
                           
@RegisterClass             Creates .gdj file
class Main : Node2D()      for script registration
    
    @RegisterFunction       Exposes method to Godot
    fun _ready()            editor and runtime
    
    @RegisterProperty       Exposes property in
    var speed = 850.0       Godot inspector
```

## Technical Stack

| Component | Version | Purpose |
|-----------|---------|---------|
| **Godot** | 4.4 | Game engine and editor |
| **Kotlin** | Latest | Programming language |
| **JVM** | 17+ | Runtime platform |
| **godot-kotlin-jvm** | 0.13.1-4.4.1 | Godot bindings for Kotlin |
| **Gradle** | 7.0+ | Build automation |
| **KSP** | Kotlin Symbol Processing | Code generation and annotation processing |

### Technology Flow

```
Kotlin Source Code
        |
        v
Gradle Build System
        |
    ┌───┴────┬────────────┐
    |        |            |
    v        v            v
  KSP    Kotlin      Resource
Processor Compiler   Bundler
    |        |            |
    |        v            |
    | JVM Bytecode        |
    |        |            |
    └────────┼────────────┘
             |
             v
    Generated .gdj Files
    (Script Registration)
             |
             v
    Godot Engine 4.4
    (GL Compatibility)
             |
             v
        Game Runtime
```

## Development Setup

### Prerequisites
- Gradle 7.0 or higher
- JDK 17 or higher
- Godot 4.4 (for editor integration)

### Building

```bash
./gradlew build
```

This generates:
- Compiled Kotlin classes in `build/classes/kotlin/main/`
- Script registration files in `gdj/` directory
- JAR file in `build/libs/`

### Running

Open the project in Godot 4.4 editor and run the Main scene, or execute the compiled JAR with Godot.

## Debugging

The project is configured with JVM debugging support:
- Debug port: 5005
- Listen on all interfaces (*)
- Debugger wait enabled for IDE integration
- Customizable via `godot_kotlin_configuration.json`

### Debug Connection Flow

```
IDE (IntelliJ/VS Code)
        |
        | (Debug Protocol)
        |
        v
JVM Debugger (port 5005)
        |
        | (Breakpoints, variables)
        |
        v
Kotlin Code Execution
        |
        | (Step through)
        |
        v
Godot Runtime
        |
        | (Game state)
        |
        v
Monitor Score, Position, Apple Spawns
```

## Input Configuration

Input mappings defined in `project.godot`:
- `ui_to_left`: A key or left arrow key
- `ui_to_right`: D key or right arrow key

### Input Processing

```
Keyboard Input
    |
    v
Godot Input System
    |
    ├─-> ui_to_left (A key or LEFT arrow)
    |       |
    |       v
    |    Basket._input() method
    |       |
    |       v
    |    velocity.x = -speed
    |
    └─-> ui_to_right (D key or RIGHT arrow)
            |
            v
        Basket._input() method
            |
            v
        velocity.x = +speed
            |
            v
        Update basket position
```

## Performance Optimization

The build system includes KSP (Kotlin Symbol Processing) for:
- Compile-time annotation processing
- Script registration generation
- Type-safe Godot bindings
- Reduced runtime reflection

### Performance Metrics

```
Execution Pipeline

Kotlin Source
    |
    v
Compile-time Processing (KSP)
    |---> Generate .gdj files (one-time cost)
    |---> Type checking (one-time cost)
    |
    v
Runtime Execution

Per Frame (~16ms @ 60 FPS):
├─ Update basket position: <1ms
├─ Spawn apple (if interval passed): <1ms
├─ Physics simulation (RigidBody2D): <2ms
├─ Collision detection: <1ms
├─ Score calculation: <1ms
└─ Render frame: <10ms
    |
    v
Total: ~16ms per frame (60 FPS target)
```

## Game Loop Flow

```
┌─────────────────────────────────────┐
│    GAME INITIALIZATION (_ready)     │
│  - Load red apple scene             │
│  - Load green apple scene           │
│  - Load bad apple scene             │
│  - Initialize game timer (0s)       │
│  - Get screen size                  │
│  - Set spawn interval (1.5s)        │
└────────────────┬────────────────────┘
                 │
                 v
         ┌───────────────────┐
         │ GAME RUNNING      │
         │ (_process delta)  │
         └────────┬──────────┘
                  │
           ┌──────┴──────┐
           |             |
    Time < 60s?      Time >= 60s?
           |             |
          YES            NO
           |             |
           v             v
    Continue         ┌─────────────┐
    Game Loop        │ GAME OVER   │
           |         │ - Stop game │
           |         │ - Show score│
           |         └─────────────┘
           |
    ┌──────┴──────────────┐
    |                     |
    v                     v
Spawn Apple?       Update Basket
    |                     |
  YES       ┌─────────────┘
    |       |
    v       v
  Random   Check Movement Input
  Type     (A/D keys)
    |       |
    |       v
    |    Calculate Velocity
    |       |
    |       v
    |    Apply Easing
    |       |
    |       v
    |    Update Position
    |       |
    |       v
    |    Rotate Basket
    |       |
    v       v
Create   Check Collisions
Apple     |
Node      v
    |    Collect Good Apple?
    |       |
    |      YES
    |       |
    |    +--------+
    |    |        |
    v    v        v
    └────────────────┐
            |        |
            v        v
    Update Score   Scale Basket
            |        |
            v        v
    Loop Back to Top
```

## Assets

All game sprites and textures are stored in `src/main/resources/assets/`. The game dynamically loads these assets during gameplay.

## Quick Reference Guide

### Class Map

| Class | Type | Role | Key Method |
|-------|------|------|------------|
| `Main` | Node2D | Game Controller | `_process(delta)` |
| `Basket` | Area2D | Player | `_input(event)` |
| `BaseApple` | RigidBody2D | Apple Base | `_ready()` |
| `RedApple` | RigidBody2D | Good Apple | Inherits BaseApple |
| `GreenApple` | RigidBody2D | Good Apple | Inherits BaseApple |
| `BadApple` | RigidBody2D | Hazard | Inherits BaseApple |

### Key Variables

| Variable | Type | Default | Effect |
|----------|------|---------|--------|
| `initialSpawnInterval` | Float | 1.5s | Starting spawn rate |
| `minSpawnInterval` | Float | 0.5s | Fastest spawn rate |
| `difficultyIncreaseRate` | Float | 0.98 | Difficulty multiplier |
| `gameDuration` | Float | 60.0s | Game length |
| `speed` | Float | 850.0 | Basket movement speed |

### Code Flow Summary

```
START GAME
    |
    +---> Load Scenes
    |
    +---> Initialize Variables
    |
    +---> Main Game Loop (60 seconds)
    |       |
    |       +---> Decrease spawn interval
    |       +---> Spawn random apple (if time)
    |       +---> Update basket position (from input)
    |       +---> Check collisions
    |       +---> Update score display
    |
    +---> Game Ends
    |
    +---> Show Final Score
    |
END GAME
```

## Key Features Summary

### ✨ New Features (Latest Version)
- **Mobile Touch Controls**: Cross-platform support with configurable opacity
- **Game Over Screen**: Polished end-screen with animated restart button
- **Wave-Based Difficulty**: Progressive difficulty with burst spawning (1-3 apples)
- **Score Formatting**: K/M/B suffix formatting for large scores
- **Modular Architecture**: Separated gameplay logic (TimingModeGameplay) from UI (Main)
- **Smart Spawning**: Anti-clustering algorithm ensures varied apple positions
- **Physics Variations**: Each apple type has unique gravity and movement

### 🎮 Core Features
- **Cross-Platform Input**: Unified keyboard + mobile touch control system
- **Squash & Stretch**: Polished animation feedback on apple catches
- **Dynamic Basket**: Visual states showing 0-8 collected apples
- **Proper Cleanup**: Explicit resource management for smooth restarts
- **Signal Architecture**: Event-driven design with Godot signals
- **Anti-Cheat**: Score validation and display caps

### 🏗️ Technical Features
- **Godot-Kotlin JVM**: Type-safe game development with Kotlin
- **KSP Code Generation**: Compile-time script registration
- **Modular Components**: ScoreController, TimingModeGameplay, GameOverScreen
- **Animation System**: Reusable AnimationStyle presets
- **JVM Debugging**: Full IDE debugging support on port 5005

## Project Statistics

```
Source Code:
├─ Main.kt:               215 lines (Scene root & lifecycle)
├─ TimingModeGameplay.kt: 299 lines (Core gameplay logic)
├─ Basket.kt:             215 lines (Player w/ mobile support)
├─ GameOverScreen.kt:     155 lines (End screen UI)
├─ ScoreController.kt:     83 lines (Score management)
├─ AnimationStyle.kt:      ~60 lines (Animation presets)
├─ BaseApple.kt:          ~100 lines (Apple base class)
└─ Apple Types:            ~50 lines each (3 types)

Total: ~1,300+ lines of Kotlin code

Scenes:
├─ 1 Main scene (with mobile controls)
├─ 1 Game Over screen
├─ 1 Basket scene
├─ 3 Apple scenes
└─ 2 Mobile control button scenes

Assets:
├─ 9 Basket textures (states 0-8)
├─ UI elements (game over, restart, score panel)
├─ Mobile control buttons
└─ Apple sprites
```

## License

This project is part of a personal game development portfolio.

## Author

SoufianoDev

---

For more information about Godot-Kotlin, visit: https://godot-kotl.in/
