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
│       │       └── game/
│       │           ├── Main.kt              # Game controller and spawn logic
│       │           │   - Game loop management
│       │           │   - Apple spawning system
│       │           │   - Difficulty progression
│       │           │
│       │           ├── Basket.kt            # Player basket with movement and collision
│       │           │   - Input handling (A/D keys)
│       │           │   - Collision detection
│       │           │   - Score tracking
│       │           │
│       │           ├── AnimationStyle.kt    # Animation configuration
│       │           │
│       │           └── apples/
│       │               ├── BaseApple.kt     # Base class for all apple types
│       │               │   - Physics setup
│       │               │   - Collision events
│       │               │   - Lifetime management
│       │               │
│       │               ├── RedApple.kt      # Red apple implementation (10 pts)
│       │               ├── GreenApple.kt    # Green apple implementation (15 pts)
│       │               └── BadApple.kt      # Bad apple implementation (hazard)
│       │
│       └── resources/
│           ├── assets/                      # Game sprites and textures
│           │   - basket.png
│           │   - red_apple.png
│           │   - green_apple.png
│           │   - bad_apple.png
│           │
│           └── META-INF/
│
├── scenes/
│   ├── main.tscn                           # Main game scene
│   │   └── Contains: Main node, Basket, UI
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
│   └── bad_apple.tscn                      # Bad apple scene
│       └── RigidBody2D with Sprite2D
│
├── gdj/
│   └── godot/
│       └── game/                           # Generated script registration files (.gdj)
│           ├── Basket.gdj
│           ├── Main.gdj
│           └── apples/
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
┌─────────────────────────────────────────────┐
│            Main.kt (Game Controller)         │
│  - Manages game timer and duration          │
│  - Spawns apples at calculated intervals    │
│  - Increases difficulty over time           │
└──────────────────┬──────────────────────────┘
                   │
        ┌──────────┼──────────┐
        │          │          │
        v          v          v
    ┌────────┐ ┌────────┐ ┌────────┐
    │RedApple│ │GreenApp│ │BadApple│
    │        │ │        │ │        │
    └────┬───┘ └────┬───┘ └────┬───┘
         │          │          │
         └──────────┼──────────┘
                    │
                    v
          ┌──────────────────┐
          │  BaseApple.kt    │
          │ (Base Class)     │
          │ - Physics (RB2D) │
          │ - Collision      │
          │ - Movement       │
          └──────────────────┘
                    ^
                    │
         ┌──────────┘
         │
         v
    ┌──────────────────┐
    │  Basket.kt       │
    │  (Player)        │
    │ - Input handling │
    │ - Collision      │
    │ - Score display  │
    └──────────────────┘
```

### Main.kt
The game controller that handles:
- Apple scene loading and instantiation
- Spawn timer management and difficulty progression
- Game timer and duration
- Random apple selection for spawning

**Key Properties:**
- `initialSpawnInterval`: Starting time between apple spawns (1.5 seconds)
- `minSpawnInterval`: Fastest spawn rate (0.5 seconds)
- `difficultyIncreaseRate`: Spawn interval multiplier each frame (0.98)
- `gameDuration`: Total game time (60 seconds)

**Spawn Logic:**
```
Current Time: 0s ------> 30s ------> 60s
Spawn Interval: 1.50s -> 0.75s -> 0.50s (minimum)

Formula: interval = max(minSpawnInterval, initialSpawnInterval * difficultyIncreaseRate^frames)
```

### Basket.kt
The player-controlled basket that features:
- Smooth movement using velocity and inertia
- Dynamic rotation based on movement direction
- Scale adjustment when catching apples
- Collision detection for scoring
- Score display and apple counting

**Key Properties:**
- `speed`: Movement speed (850.0 units/second)
- `score`: Current player score
- Dynamic texture loading for different apple types

**Movement System:**
```
Input (A/D keys)
    |
    v
Calculate Target Velocity
    |
    v
Apply Smooth Easing
    |
    v
Update Position with Physics
    |
    v
Rotate Basket Based on Direction
    |
    v
Scale Adjustment on Catch
```

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

## Notes

- The game features progressive difficulty scaling to keep challenge increasing
- Basket uses smooth easing for rotation and scale animations
- Apple spawning is randomized to different screen positions
- Score system tracks both individual catches and total count
- Built with Godot-Kotlin for type-safe JVM game development

## Project Statistics

```
Source Code:
├─ Main.kt:        212 lines (Game controller)
├─ Basket.kt:      305 lines (Player controller)
├─ BaseApple.kt:   ~100 lines (Base class)
├─ Apple Types:    ~50 lines each (Derived classes)
└─ AnimationStyle: (Configuration)

Total: ~700+ lines of Kotlin code

Build Configuration:
├─ Gradle: 50+ lines
├─ Settings: KSP enabled
└─ Godot Config: JVM debugging

Scenes:
├─ 1 Main scene
├─ 1 Basket scene
└─ 3 Apple scenes
```

## License

This project is part of a personal game development portfolio.

## Author

SoufianoDev

---

For more information about Godot-Kotlin, visit: https://godot-kotl.in/
