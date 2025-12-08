# AutoBuild - Minecraft Forge 1.20.1 Mod

## Overview
AutoBuild is a Minecraft Forge mod for version 1.20.1 that provides auto-building functionality with structure management and hitbox visualization.

## Build System
- **Language**: Java 17
- **Build Tool**: Gradle with Minecraft Forge plugin
- **Minecraft Version**: 1.20.1
- **Forge Version**: 47.2.0

## Project Structure
```
src/main/java/com/autobuild/
├── AutoBuild.java                      # Main mod class
├── client/
│   ├── gui/AutoBuildScreen.java        # GUI screen
│   ├── handler/
│   │   ├── AutoBuildHandler.java       # Build handler
│   │   ├── KeyBindingHandler.java      # Key bindings
│   │   ├── KeyInputHandler.java        # Key input events
│   │   ├── SelectionManager.java       # Selection management
│   │   └── StickInteractionHandler.java
│   └── render/HitboxRenderer.java      # Hitbox rendering
├── common/
│   ├── command/AutoBuildCommand.java   # Commands
│   ├── config/AutoBuildConfig.java     # Configuration
│   ├── data/
│   │   ├── BuildStructure.java
│   │   ├── StructureData.java
│   │   ├── StructureDataManager.java
│   │   └── StructureManager.java
│   └── network/NetworkHandler.java     # Network handling
```

## Building
Run the "Build Mod" workflow or execute:
```bash
./gradlew build
```

The compiled JAR will be in `build/libs/autobuild-1.0.0.jar`

## Installation (for Minecraft)
1. Install Minecraft Forge 1.20.1 (47.2.0+)
2. Copy the JAR from `build/libs/` to your `.minecraft/mods/` folder
3. Launch Minecraft with Forge profile

## Controls
- **H** key: Toggle hitbox visibility
- **Arrow Up/Down**: Move fixed hitbox up/down
- **K** Key: AutoBuild Menu Open
- **J** Key: AutoBuild On/Off
- **Arrow Left-Right** Key: You can move the build left and right with the directional keys. 

## Configuration
Mod settings are stored in `config/autobuild-client.toml`
