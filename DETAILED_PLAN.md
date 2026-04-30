# IC2 Fresh Port — Updated Porting Plan

Repository: https://github.com/Llemneed/ic2-fresh-port

Target:
- Minecraft 1.21.1
- NeoForge 21.1.227
- Java 21
- Mod ID: `ic2`

This repository may be public temporarily for review/development access. It is not a public release.

---

## Status markers

Use these markers everywhere:

- `[x]` done
- `[~]` partially implemented / registered but not feature-complete
- `[ ]` incomplete / not started

Important: registered content is not the same as feature-complete content.

---

## Main goal

Fully port IndustrialCraft 2 to Minecraft 1.21.1 / NeoForge while preserving:

- EU energy
- tiers and packet size
- overvoltage
- cable loss
- machines
- generators
- storage blocks
- electric tools and armor
- rubber progression
- worldgen
- upgrades
- GUI/menus/screens
- recipes
- sounds
- reactor/nuclear systems
- optional compatibility integrations

Do not rewrite the project from scratch. Improve the existing architecture gradually.

---

## Mandatory rules

1. Do not change `mod_id`; it must stay `ic2`.
2. Do not change existing registry names unless absolutely necessary.
3. Do not delete existing content.
4. Do not use old Forge 1.12.2 APIs directly.
5. Use modern Minecraft 1.21.1 / NeoForge APIs.
6. Internal energy remains EU. Do not replace it with FE.
7. FE bridge may be optional later, not primary.
8. Project must compile after every major step.
9. Run `./gradlew clean build` at the end of every pass.
10. If build fails, fix it before finishing.
11. If a mechanic is too large, implement a minimal working version and leave a clear TODO.
12. Do not start reactor/endgame systems before base EU, machines, cables and storage are stable.
13. Work by milestones.
14. Do not mix unrelated systems in one pass.
15. No log spam every tick.
16. No global world scans every tick.
17. No static `Level`, `Player`, `BlockEntity`, or world references.
18. No client-only imports in common code.
19. Keep client screens/renderers in client packages.
20. Register every new registry class in `ic2.core.IC2`.
21. Use `@Override` where applicable.
22. Use `IC2.LOGGER` / LogUtils, not `System.out.println`.
23. No `Thread.sleep` in tick methods.
24. Call `setChanged()` after state, inventory, energy, progress or mode changes.
25. No file I/O in tick methods.
26. TODO format: `// TODO(milestone-N): description`
27. Every milestone must include build, multiplayer, save/load, localization and test checkpoints where relevant.

---

## Current base

Already present:
- `IC2.java`
- `IC2Blocks`
- `IC2Items`
- `IC2BlockEntities`
- `IC2Menus`
- `IC2ArmorMaterials`
- `IC2CreativeTabs`
- `IC2Sounds`
- `IC2RecipeTypes`
- `IC2RecipeSerializers`
- `IC2DataComponents`
- `AbstractEuBlockEntity`
- `AbstractMachineBlockEntity`
- `AbstractProcessingMachineBlockEntity`
- `AbstractEuInventoryBlockEntity`
- `EnergyConsumer`
- `EnergyTier`
- `EnergyNetHelper`
- partial recipe system
- basic machines
- generators
- storage blocks
- basic electric items
- sounds
- README / ROADMAP

Use this base. Do not start over.

---

## Machine abstraction policy

`AbstractProcessingMachineBlockEntity` already exists. Do not remove it.

Milestone 1–3:
- controlled duplication is allowed
- stable gameplay loop is more important than perfect abstraction

Milestone 4–6:
- extract repeated logic gradually:
  - inventory save/load
  - energy receive/store
  - charge slot
  - upgrade slots
  - progress
  - active state
  - sound trigger

Milestone 7+:
- after 10+ machines are actually working, do deeper cleanup

Avoid creating a god-class that prevents unusual IC2 machines from working.

---

## Data Components vs NBT

BlockEntity state uses regular NBT:
- `saveAdditional`
- `loadAdditional`
- `HolderLookup.Provider`
- inventory NBT
- `energyStored`
- `progress`
- `burnTime`
- modes

ItemStack energy uses Data Components:
- `IC2DataComponents.ENERGY_STORED`
- `DataComponentType<Integer>`

Use components for:
- RE Battery
- Advanced RE Battery
- Energy Crystal
- Lapotron Crystal
- electric tools
- electric armor
- batpacks

Legacy migration:
- old `CUSTOM_DATA Charge` may be read as fallback
- migrate to `ENERGY_STORED`
- document in `MIGRATION_PLAN.md`

---

# Milestones

## Milestone 0 — Workspace, build, docs, legal

Goal: stable workspace and documentation.

Tasks:
- [x] NeoForge 1.21.1 workspace
- [x] main mod class
- [x] core registries
- [x] README
- [~] ROADMAP
- [ ] `DETAILED_PLAN.md`
- [ ] `TESTING_PLAN.md`
- [ ] `MIGRATION_PLAN.md`
- [ ] verify Java 21
- [ ] verify license / redistribution status
- [ ] add/verify LICENSE
- [ ] add/verify CREDITS.md or NOTICE.md

Checks:
```bash
java -version
./gradlew --version
./gradlew clean build
```

---

## Milestone 1 — Stable Basic IC2 Loop

Required loop:
`Generator → BatBox → Macerator → Dust`

Generator:
- accepts fuel
- produces EU
- stores EU
- saves `burnTime` and `energyStored`
- outputs EU
- returns container items
- has active/lit state
- plays sound only while working

BatBox:
- receives/stores/outputs EU
- saves energy
- has GUI energy bar
- charges/discharges RE Battery

Macerator:
- receives EU
- processes input to output
- shows progress
- saves inventory, energy, progress
- uses JSON recipe if present, fallback if needed
- plays sound only while working

RE Battery:
- uses Data Components
- charges/discharges
- shows energy bar
- saves energy

Manual tests:
1. coal in Generator -> `burnTime > 0`
2. BatBox energy grows
3. ore/raw material in Macerator -> progress grows
4. `/save-all`, exit/reopen -> inventory/energy/progress remain
5. shift-click output -> player inventory
6. shift-click ore -> input
7. shift-click upgrade -> upgrade slot
8. charge RE Battery in BatBox
9. discharge RE Battery in Macerator
10. `./gradlew clean build`

Multiplayer checkpoint:
- server-authoritative GUI
- client does not modify energy/inventory directly
- dedicated server loads without client classes

---

## Milestone 1.5 — Data Components For Electric Items

Moved earlier because RE Battery is required by Milestone 1 and storage charge/discharge.

Tasks:
- [x] `IC2DataComponents`
- [x] `ENERGY_STORED`
- [x] registered in `IC2.java`
- [~] `ElectricItemManager` support
- [~] RE Battery migration
- [ ] audit all electric tools
- [ ] audit armor/batpacks
- [ ] document migration

Criteria:
- RE Battery persists energy
- energy bar works
- tooltip works
- charge/discharge works
- build passes

---

## Milestone 2 — GUI, Menus And Slot Validation

Goal: safe Menu/Screen implementation.

For each core machine/storage:
- Menu opens
- Screen opens
- energy/progress/burn sync works
- slot validation works
- `quickMoveStack` safe
- output slot read-only
- no client-only code in common

Required shift-click tests:
1. output -> player inventory
2. ore -> machine input
3. upgrade -> upgrade slot
4. battery -> charge/discharge slot
5. fuel -> fuel slot
6. invalid item stays in inventory
7. no duplication
8. no deletion
9. no item inserted into output slot

---

## Milestone 3 — Testing Infrastructure

Create `TESTING_PLAN.md`.

Manual tests:
- basic loop
- save/load inventory
- save/load energy
- save/load progress
- battery charge/discharge
- shift-click
- recipes
- overvoltage
- lava bucket fuel
- container item return

GameTest candidates:
- Generator produces EU
- BatBox receives EU
- Macerator produces output
- RE Battery stores component energy
- inventory survives reload
- overvoltage destroys low-tier machine
- cable melts on invalid packet
- recipe JSON loads

Dedicated server checks:
- server starts
- client connects
- GUI opens
- no client-only classes

Performance checks:
- 10-cable line
- 50-cable line
- later Milestone 10 benchmark

---

## Milestone 4 — Data-Driven Machine Recipes

Recipe types:
- `ic2:macerating`
- `ic2:compressing`
- `ic2:extracting`
- `ic2:metal_forming`
- `ic2:recycling`
- `ic2:canning`
- `ic2:electric_smelting` if needed

Rules:
1. machine checks JSON first
2. legacy fallback allowed temporarily
3. migrate fallbacks to JSON
4. use tags where possible
5. recipe execution server-side

Criteria:
- Macerator, Compressor, Extractor read JSON
- fallback shrinks
- build passes

---

## Milestone 4.5 — Basic JEI/EMI Integration

Moved earlier because machine recipes need to be visible.

Scope:
- optional JEI or EMI
- Macerator/Compressor/Extractor categories
- catalysts
- input/output/energy/time display

Rules:
- optional dependency
- mod runs without JEI/EMI
- client-only integration separated

---

## Milestone 5 — EU Storage Blocks

Storage:
- BatBox
- CESU
- MFE
- MFSU

Requirements:
- capacity by tier
- max input/output
- source/sink tier
- output side
- rotation
- GUI
- charge/discharge slots
- save/load energy
- receive/output EU
- overvoltage behavior

Criteria:
- Generator charges BatBox
- BatBox powers Macerator
- higher tiers work
- reload preserves energy
- build passes

---

## Milestone 5.5 — Minimal Energy Config

Moved earlier for balancing/testing.

Config:
- generator output
- machine EU/t
- storage capacities
- cable loss
- overvoltage explosions enabled
- debug energy logging

Rules:
- do not over-configure everything
- defaults must be IC2-like
- config must work on dedicated server

---

## Milestone 6 — Materials, Tags, Crafting Progression And Localization

Materials:
- dusts
- plates
- casings
- wires
- insulated wires
- circuits
- machine casings
- carbon materials
- alloy
- industrial diamond
- coils
- motors
- power units

Requirements:
- item models
- lang keys
- creative tab
- tags
- survival recipes
- no circular progression
- localization updated with content

---

## Milestone 7 — Simple EU Cable Transfer

Goal: first cable implementation without a full network architecture.

Explicit constraints:
- direct neighboring blocks only, 6 directions
- no pathfinding
- no network caching
- no EnergyNetGrid
- no EnergyNetGlobal
- no global world scans
- no complex load balancing
- no production-grade EnergyNet

Allowed:
- source/storage -> cable
- cable -> cable
- cable -> machine/storage
- basic packet size
- basic loss
- basic overvoltage
- basic transformer behavior
- cable meltdown

Important:
Long cable chains may work step-by-step and inefficiently. That is acceptable for Milestone 7. Proper network caching belongs to Milestone 10.

Cable types:
- tin
- copper
- insulated copper
- gold
- insulated gold
- HV
- glass fiber

Tests:
- Generator -> Cable -> BatBox
- BatBox -> Cable -> Macerator
- Generator -> 5 Cable -> Macerator
- high-tier source -> low-tier cable melts
- high-tier source -> low-tier machine overvoltage
- valid transformer path survives
- source loses only accepted EU
- no EU duplication
- no EU loss except intended cable loss

---

## Milestone 8 — Rubber Tree And Resin Loop

Requirements:
- rubber wood/leaves/sapling
- rubber tree worldgen
- sapling growth
- sticky resin
- treetap
- electric treetap
- resin regeneration
- sticky resin -> rubber
- rubber used in cables

---

## Milestone 9 — Worldgen

Requirements:
- tin ore
- lead ore
- uranium ore
- vanilla copper integration
- deepslate variants if added
- configured/placed features
- biome modifiers
- dedicated server safe
- balance pass

---

## Milestone 10 — Grid-Based Energy Network Refactor

Start only after Milestone 7 works.

Tasks:
- EnergyNetGlobal
- EnergyNetGrid
- EnergyNode
- source/sink/storage/conductor separation
- recalculate grid on cable changes
- no world scan every tick
- connected-component cache
- packet distribution
- loss
- overvoltage
- multiple sources/sinks

Benchmark:
- 10 cable line
- 50 cable line
- 100 cable line
- place/remove stress test

---

## Milestone 11 — Electric Items And Batteries

Finish:
- RE Battery
- Advanced RE Battery
- Energy Crystal
- Lapotron Crystal
- Drill
- Diamond Drill
- Iridium Drill
- Chainsaw
- Electric Wrench
- Electric Hoe
- Electric Treetap
- Mining Laser
- Scanner
- Advanced Scanner
- Nano Saber

Requirements:
- Data Components
- max energy
- tier
- transfer limit
- charge/discharge
- durability bar
- tooltip
- energy persists

---

## Milestone 12 — Armor And Energy Packs

Armor:
- Nano set
- Quantum set
- BatPack
- Advanced BatPack
- LapPack
- Energy Pack

Requirements:
- Data Components
- energy drain
- durability bar
- tooltips
- special effects
- fall damage/speed/jump behavior as applicable

---

## Milestone 13 — Machine Upgrades

Upgrades:
- Overclocker
- Transformer
- Energy Storage
- Redstone Inverter
- Ejector
- Pulling
- Fluid Ejector later

Criteria:
- overclocker changes speed and EU/t
- transformer affects input tier
- storage upgrade increases capacity only
- overvoltage respects transformer upgrades

---

## Milestone 14 — Sound System

Rules:
- machine sound only while active
- generator sound only while burning/producing
- no every-tick sound spam
- cable meltdown sound
- custom explosion sound optional
- `sounds.json` must match registry
- `.ogg` files in correct paths

---

## Milestone 15 — Datagen Expansion

Providers:
- ItemModelProvider
- BlockStateProvider
- RecipeProvider
- LootTableProvider
- LanguageProvider
- BlockTagsProvider
- ItemTagsProvider
- machine recipe provider
- worldgen provider if useful

Commands:
```bash
./gradlew runData
./gradlew clean build
```

---

## Milestone 16 — Full Compatibility Pass

Integrations:
- expanded JEI/EMI
- Jade/The One Probe
- optional FE bridge
- optional Curios for batpacks

Rules:
- all optional
- mod runs without them
- strict client/common separation

---

## Milestone 17 — Debug And Testing Tools

Commands, admin/debug only:
- `/ic2debug energy get`
- `/ic2debug energy set <amount>`
- `/ic2debug charge held <amount>`
- `/ic2debug drain held <amount>`
- `/ic2debug network scan`
- `/ic2debug machine reset`
- `/ic2debug give_basic_loop`

---

## Milestone 18 — Save Migration Audit

Create/maintain `MIGRATION_PLAN.md`.

Areas:
- `CUSTOM_DATA Charge` -> `ENERGY_STORED`
- inventory tag changes
- slot count changes
- old `energy` -> `energyStored`
- capacity clamps
- renamed/removed IDs
- recipe type changes

Rules:
- do not silently delete player items
- clamp impossible values
- document incompatible changes

---

## Milestone 19 — Nuclear / Reactor System

Start only after stable:
- EU network
- storage
- cables
- machines
- GUI
- recipes
- item energy

Sub-milestones:
- 19.1 reactor block and chambers
- 19.2 reactor inventory and GUI
- 19.3 deterministic heat simulation
- 19.4 reactor components
- 19.5 EU output
- 19.6 meltdown and explosion
- 19.7 reactor polish

---

## Milestone 20 — Final Parity And Release Readiness

Check:
- survival progression
- machines
- generators
- storage
- cables
- batteries
- tools
- armor
- upgrades
- worldgen
- rubber
- reactor
- GUI
- sounds
- recipes
- multiplayer
- dedicated server
- datagen
- missing textures/sounds/lang
- license/credits
- config defaults
- migration notes
- known issues

Criteria:
- `./gradlew clean build` passes
- `runClient` launches
- dedicated server does not crash
- basic survival progression playable
- no known data-loss bugs
- release notes prepared

---

## Error Priority Order

1. Compile errors
2. Startup crash
3. Dedicated server crash
4. World load crash
5. NBT/inventory loss
6. Energy duplication/loss
7. GUI crashes
8. Recipe loading errors
9. Missing textures
10. Missing sounds
11. Balance issues

Do not move to new features while the project does not compile.

---

## Working With Legacy IC2 1.12.2 Logic

Adapt, do not blindly copy:

- `TileEntity` -> `BlockEntity`
- `IInventory` -> `ItemStackHandler` / Menu / capabilities
- metadata blocks -> blockstates/properties
- old registries -> `DeferredRegister`
- old recipes -> JSON recipe types
- old worldgen events -> configured/placed features / biome modifiers
- old packets -> modern networking
- old item energy NBT -> Data Components
- old GUI/container -> Menu/Screen
- old models -> modern blockstates/models

---

## Recommended Next Pass

1. Put this file in the repo as `DETAILED_PLAN.md`.
2. Update `ROADMAP.md` to match `[x] / [~] / [ ]`.
3. Create `TESTING_PLAN.md`.
4. Create `MIGRATION_PLAN.md`.
5. Continue with:
   - Basic Loop verification
   - Menu/slot validation
   - data-driven recipes
   - basic JEI/EMI
   - simple neighbor-only cable transfer

Do not start reactor, nuclear systems, advanced armor or grid EnergyNet yet.
