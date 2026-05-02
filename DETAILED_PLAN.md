# Detailed Development Plan

This file is the working milestone plan for the current IC2 Fresh Port direction. It is intentionally more detailed than [ROADMAP.md](</C:/Users/user/Documents/New project/ic2-fresh-port/ROADMAP.md>).

Status markers:
- `[x] done`
- `[~] partial`
- `[ ] not done`

## Milestone 0 - Workspace, Build, Repo Hygiene
- [x] NeoForge 21.1.227 workspace on Minecraft 1.21.1
- [x] Java 21 baseline
- [x] Gradle wrapper build works
- [x] Main mod bootstrap under `ic2`
- [x] Deferred registers for blocks, items, menus, sounds, block entities
- [x] GitHub Actions build
- [x] Basic project README
- [x] Short roadmap
- [ ] Confirm license / redistribution status before any public release
- [ ] Remove leftover temporary files and one-off probes from the workspace

### Multiplayer checkpoint
- [ ] Verify the mod loads on a dedicated server without client-only crashes

### Save migration notes
- [ ] Record the first release-like baseline for future migration comparisons

## Milestone 1 - First Survival Loop
Goal: a player can start from raw resources and reach `Generator -> BatBox -> Macerator -> Dust`.

- [x] Basic ingots, dusts and machine parts registered
- [x] Generator implemented and producing EU
- [x] BatBox implemented and storing/outputting EU
- [x] Macerator implemented and consuming EU
- [x] Raw ore -> dust loop present
- [x] Dust -> ingot smelting JSONs present
- [~] Manual verification of the exact loop
- [ ] Verify survival-valid recipe progression end to end
- [ ] Verify directionality / placement expectations for BatBox in survival play

### Localization checkpoint
- [~] Core item/block names mostly present
- [ ] Audit early-game tooltip text and remaining English fallbacks

### Multiplayer checkpoint
- [ ] Verify generator, BatBox and Macerator sync correctly between server and client

### Save migration notes
- [x] Inventory loading bugs fixed for multi-slot machines
- [ ] Re-open old worlds containing early machine placements and verify no slot loss

## Milestone 1.5 - Item Energy Foundation
Goal: replace fragile legacy per-item charge storage with a consistent system.

- [x] Shared `ElectricItemManager`
- [x] `IC2DataComponents.ENERGY_STORED`
- [x] Runtime fallback from legacy `CUSTOM_DATA` `Charge`
- [x] Shared charge/discharge paths for battery/tool/armor items
- [~] Existing electric items migrated to the shared manager
- [ ] Add release-facing migration notes for old item stacks
- [ ] Verify energy persistence across save/load and item transfer edge cases

### Localization checkpoint
- [ ] Audit electric item tooltips for consistency

### Multiplayer checkpoint
- [ ] Verify item charge updates stay authoritative on server side

### Save migration notes
- [x] Old `Charge` NBT is migrated lazily on read
- [ ] Decide when the legacy migration path can be retired

## Milestone 2 - Machine Architecture Stabilization
Goal: reduce copy-paste and make core machines safer to extend.

- [x] `AbstractInventoryBlockEntity`
- [x] `AbstractEuBlockEntity`
- [x] `AbstractEuInventoryBlockEntity`
- [x] `AbstractMachineBlockEntity`
- [x] `AbstractProcessingMachineBlockEntity`
- [x] Shared inventory save/load helpers
- [x] Shared processing tick skeleton
- [x] Shared upgrade handling baseline
- [~] Core processing machines moved to shared base
- [ ] Continue unifying non-processing families where it reduces risk
- [ ] Add more small regression-proof helpers only where duplication remains real

### Localization checkpoint
- [ ] Keep UI strings aligned while menus/screens are stabilized

### Multiplayer checkpoint
- [ ] Validate menu/container sync for processing machines on dedicated server

### Save migration notes
- [x] Preserve existing NBT keys where possible
- [ ] Document any machine that changed slot semantics

## Milestone 3 - Data-Driven Machine Recipes
Goal: stop expanding hardcoded machine recipes and move to JSON + recipe manager.

- [x] `ic2:macerating`
- [x] `ic2:extracting`
- [x] `ic2:compressing`
- [x] `ic2:metal_forming`
- [x] `ic2:solid_canning`
- [~] `ic2:recycling`
- [~] `ic2:electric_smelting`
- [x] Recipe type and serializer registration
- [x] Initial JSON coverage for current gameplay paths
- [~] Legacy fallback retained for compatibility
- [ ] Expand coverage until hardcoded paths are only emergency fallback
- [ ] Decide whether to move more special-case outputs into pure JSON data
- [ ] Add datagen later if static JSON maintenance becomes painful

### Localization checkpoint
- [ ] Recipe-view-facing names and categories must stay coherent once viewer support arrives

### Multiplayer checkpoint
- [ ] Verify recipe resolution is identical on integrated and dedicated server

### Save migration notes
- [x] Recipe migration is runtime-safe because it does not change stored machine inventories
- [ ] Document when fallback logic can safely be removed

## Milestone 4 - EU Network, Storage, Transformers
Goal: solid baseline EU flow beyond adjacent single-block setups.

- [x] Basic `EnergyTier` model
- [x] `receiveEu(...)` overvoltage path
- [x] Generator/storage output helpers
- [x] BatBox / CESU / MFE / MFSU baseline
- [x] Chargepads baseline
- [~] Cable blocks and transformer blocks present
- [~] Basic send/receive routing works
- [ ] Proper cable visual parity
- [ ] Proper cable connection geometry / state parity
- [ ] Finalize transformer behavior and tier expectations
- [ ] Finalize meltdown / overvoltage / machine explosion parity

### Milestone 4.5 - Basic JEI / EMI Integration
Goal: expose the current machine recipes in a discoverable way.

- [ ] Register basic recipe categories
- [ ] Show Macerator / Extractor / Compressor / Metal Former / Solid Canner recipes
- [ ] Show input/output plus machine timing or EU hints where practical
- [ ] Keep this layer minimal until recipe shapes settle

### Localization checkpoint
- [ ] Recipe category names
- [ ] Viewer text / translated labels

### Multiplayer checkpoint
- [ ] Verify cable and storage behavior under server authority
- [ ] Verify no client-only energy desync assumptions remain

### Save migration notes
- [ ] Record any blockstate/model/network changes that may affect placed energy blocks

## Milestone 5 - Worldgen, Materials, Early Balancing
Goal: bring world resources and progression inputs closer to expected IC2 gameplay.

- [x] Tin / lead / uranium values from `IC2.ini`
- [x] Vanilla copper integration
- [x] Rubber tree blocks and sapling
- [~] Worldgen present in-game
- [ ] Verify real-world density and progression pacing
- [ ] Tune ore exposure and height bands only after manual survival runs

### Milestone 5.5 - Minimal Energy Config
Goal: make a few critical energy values configurable without destabilizing the port.

- [ ] Config entries for selected machine/storage values
- [ ] Config entries for conservative balancing toggles
- [ ] Document default values and migration expectations
- [ ] Keep scope intentionally small to avoid config sprawl

### Localization checkpoint
- [ ] Translate any config-facing text if exposed in-game later

### Multiplayer checkpoint
- [ ] Verify config-driven values stay authoritative on dedicated server

### Save migration notes
- [ ] Document compatibility rules if config changes affect placed blocks or stored energy

## Milestone 6 - Assets, UI, Localization, UX
Goal: make the current playable content readable and presentable without pretending feature parity is done.

- [x] Basic machine GUI layer
- [x] Basic machine sounds
- [~] Wiring / storage asset cleanup
- [~] Chargepad visuals improved
- [~] Cable visuals improved, but not final
- [~] Russian localization partially refreshed
- [ ] Finish flat / placeholder item model cleanup
- [ ] Finish cable world visuals
- [ ] Audit all core machines for screen usability
- [ ] Full tooltip pass for batteries, tools, machines and upgrades

### Localization checkpoint
- [~] Basic Russian and English coverage exists
- [ ] Remove remaining fallback English where content is considered playable

### Multiplayer checkpoint
- [ ] Verify screen sync, sounds and state visuals in multiplayer

### Save migration notes
- [ ] None expected for asset-only changes, but test old placed blocks visually

## Milestone 7 - Multiplayer And Release Safety
Goal: avoid shipping a singleplayer-only dev port by accident.

- [ ] Dedicated server startup clean
- [ ] Client connects to dedicated server clean
- [ ] Core loop works in multiplayer
- [ ] Machine GUIs sync correctly
- [ ] Electric item charge sync verified
- [ ] No obvious duplication / desync regressions

### Localization checkpoint
- [ ] Server/client text consistency for menus and messages

### Multiplayer checkpoint
- [x] This whole milestone is the multiplayer checkpoint

### Save migration notes
- [ ] Open older saves on both integrated and dedicated server and compare outcomes

## Milestone 8 - Release Candidate Stabilization
Goal: move from "working dev port" to "carefully testable build".

- [ ] Clean regression checklist
- [ ] Save/load verification on representative worlds
- [ ] Migration notes updated
- [ ] Final issue pass on cable visuals and machine UX
- [ ] Basic documentation for testers
- [ ] Tag a candidate build only after manual and dedicated-server checks

### Localization checkpoint
- [ ] Verify all user-facing strings for implemented content

### Multiplayer checkpoint
- [ ] Final smoke test on dedicated server before any public testing build

### Save migration notes
- [ ] Freeze migration notes for the release candidate branch
