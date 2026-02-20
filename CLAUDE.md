# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

BestBudz is a RuneScape Private Server (RSPS) built on a 317/474 client protocol. It's a cannabis-themed RSPS where standard RS terminology is replaced with custom names (skills = "professions", players = "stoners", attack = "assault", prayer = "resonance", slayer = "mercenary", etc.).

## Build & Run

**Build:** `./gradlew build`
**Run:** `./gradlew run` (binds to port 42000)
**Dev mode:** Pass `true` as first arg to enable dev mode (enables line counter, disables error log file redirection)

Java 17 is required. The project uses Gradle with source in `src/` (not `src/main/java/`).

Entry point: `com.bestbudz.Server` → `GameThread.init()` → loads all game data, binds Netty server, starts game loop.

## Architecture

### Game Loop
`GameThread` runs the main game cycle at 300ms ticks (double the standard 600ms). Each tick: `TaskQueue.process()` → `GroundItemHandler.process()` → `ObjectManager.process()` → `World.process()`.

### Core Entity Model
- **`Entity`** — base class for all game entities (location, movement, attributes)
- **`Stoner`** (extends Entity) — the player class. Decomposed into component managers: `StonerStats`, `StonerInventory`, `StonerSession`, `StonerCombat`, `StonerProfessions`, `StonerPets`, `StonerSocial`, `StonerMinigame`, etc.
- **`Mob`** — NPCs/monsters
- **`World`** — static singleton managing arrays of `Stoner[2048]` and `Mob[8192]`, delegates to `WorldEntityManager`, `WorldUpdateManager`, `WorldMessageManager`

### Networking (Netty)
- `core/network/PipelineFactory` → `ChannelHandler` handles connections
- Incoming packets: `rs2/entity/stoner/net/in/impl/` — each packet type is a separate class (e.g., `CommandPacket`, `InterfaceAction`, `ItemPackets`, `ClickButtonPacket`)
- Outgoing packets: `rs2/entity/stoner/net/out/impl/` — `Send*` classes (e.g., `SendMessage`, `SendConfig`, `SendProfession`)
- Player updating: `StonerUpdating` and `NPCUpdating` handle per-tick sync

### Persistence (SQLite)
- `SQLiteDB` manages a single SQLite connection at `data/database/playerdata.db`
- `StonerSaveUtil` / `StonerLoadUtil` handle player serialization (REPLACE INTO with JSON for complex fields)
- `SaveWorker` processes saves asynchronously via a queue
- `GracefulShutdownHook` saves all players on shutdown
- Pet master data stored separately in `db/petmaster.db`

### Task System
`core/task/Task` is the tick-based scheduler. Tasks have `BreakType` (NEVER, ON_MOVE) and `StackType` (STACK, NEVER_STACK). Queued via `TaskQueue.queue(task)`. Used for delayed/repeating actions (skilling, combat, autosave).

### Professions (Skills)
Located in `rs2/content/profession/`. Each profession has its own package. Renamed RS skills:
- Attack → Assault, Strength → Melee, Prayer → Resonance, Magic → Mage
- Slayer → Mercenary, Herblore → THChempistry, Cooking → Foodie
- Woodcutting → Lumbering, Mining → Quarrying, Firemaking → Pyromaniac
- Fletching → Woodcarving, Crafting → Handiness, Ranged → Sagittarius
- Fishing → Fisher, Farming → Weedsmoking, Runecrafting → Mage
- Summoning → Pet Master, Construction → Bankstanding
- New: Consumer (food/potion consumption skill with allergy system and XP)

Grade system uses `Profession.EXP_FOR_GRADE[]` lookup table. Professions are declared/initialized in `GameDataLoader.load()`.

### Controller System
`rs2/entity/stoner/controllers/Controller` — abstract class governing what actions are allowed in specific contexts (minigames, wilderness, etc.). `ControllerManager` assigns controllers to players. Controllers gate: movement, teleport, combat, eating, trading, logging out, etc.

### Dialogue System
`rs2/content/dialogue/Dialogue` — abstract class with `execute()` (state machine via `next` counter) and `clickButton()`. Implementations in `dialogue/impl/`. `DialogueManager` assigns dialogues to players. `OneLineDialogue` handles quick single-line NPC chat.

### Combat
`rs2/content/combat/` — `Combat` class handles melee/range/magic combat logic. `StonerCombatInterface` manages combat UI. Special attacks in `combat/special/`. Pet combat system in `rs2/entity/pets/PetCombat*`.

### Minigames
`rs2/content/minigames/` — each minigame in its own package (bloodtrial, duelarena, fightpits, godwars, pestcontrol, barrows, plunder, warriorsguild, weapongame). Blood Trial is the newest — wave-based with 25 waves defined in `waves/impl/`.

### Shops
Defined in XML at `data/def/items/ShopDefinitions.xml`. Runtime shop logic in `rs2/content/shopping/`.

### Discord Integration
`core/discord/` — JDA 5-based bot with plugin system. `DiscordServerIntegration` initializes on startup. `ChatBridgeManager` bridges in-game and Discord chat.

## Data Files

- `data/def/npcs/` — NPC definitions, combat stats, drops, spawns (XML)
- `data/def/items/` — item definitions, equipment, food, potions, shops (XML)
- `data/map/` — map region data, clipping
- `data/profession/` — profession-specific data files
- `data/def/professions/` — profession XP/level definitions
- Definitions loaded via `core/util/GameDefinitionLoader` using XStream XML deserialization
- Definition classes in `core/definitions/` (e.g., `ItemDefinition`, `NpcDefinition`, `NpcCombatDefinition`)

## Key Conventions

- **Gitignored secrets:** `DiscordConfig.java` and `PasswordEncryption.java` are gitignored — they contain tokens/keys and must exist locally but never be committed
- **Declare pattern:** Static data is initialized via `declare()` methods called from `GameDataLoader.load()`. When adding new static data, add its `declare()` call there
- **Packet handling:** New incoming packets need registration in `PacketHandler.declare()`
- **"Stoner" = Player** throughout the entire codebase. Methods like `getStoners()`, `StonerSaveUtil`, `StonerUpdating` all refer to player operations
