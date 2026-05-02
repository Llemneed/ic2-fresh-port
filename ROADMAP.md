# Roadmap

## Milestone 0 - Workspace And Bootstrap
- [x] NeoForge 1.21.1 workspace
- [x] Main mod bootstrap and deferred registries
- [x] Basic build pipeline and GitHub Actions
- [~] Project documentation cleanup
- [ ] License and redistribution verification

## Milestone 1 - First Playable Loop
- [x] Generator produces EU
- [x] BatBox stores and outputs EU
- [x] Macerator consumes EU
- [x] Dust processing works
- [~] Extractor -> rubber -> cable early-game loop
- [ ] Full manual verification of the survival start path

## Milestone 1.5 - Item Energy Foundation
- [x] ElectricItemManager shared layer
- [x] Item energy stored through Data Components
- [~] Legacy item charge migration fallback
- [ ] Release-grade migration verification on old saves

## Milestone 2 - Core Machine Architecture
- [x] Shared machine inventory base classes
- [x] Shared processing-machine tick skeleton
- [~] Menu container data validation across machine menus
- [~] Core LV machines moved onto shared processing base
- [ ] Finish architecture cleanup for all remaining machine families

## Milestone 3 - Data-Driven Recipes
- [x] Macerator recipe type and serializer
- [x] Extractor / Compressor / Metal Former / Solid Canner recipe foundations
- [~] Recycler recipe foundation with starter JSON coverage
- [~] Electric Furnace recipe foundation with dust smelting coverage
- [~] Electric Furnace recipe foundation with core dust, ore and resin coverage
- [~] Current gameplay recipes covered by JSON with legacy fallback
- [ ] Remove most hardcoded compatibility fallbacks after parity checks

## Milestone 4 - EU Network And Storage
- [x] Basic EU tiers
- [x] Generator / storage / machine packet flow
- [x] BatBox / CESU / MFE / MFSU baseline
- [~] Cable transport and transformer baseline
- [ ] Proper cable visuals, routing parity and meltdown behavior

## Milestone 4.5 - JEI / EMI Basics
- [ ] Show machine recipes in recipe viewer
- [ ] Show recipe categories for core processing machines
- [ ] Basic energy-related item context

## Milestone 5 - Worldgen And Materials
- [x] Tin / lead / uranium values from IC2.ini
- [x] Vanilla copper integration
- [~] Rubber tree and ore generation available in-game
- [ ] Final balancing pass in survival worlds

## Milestone 5.5 - Minimal Energy Config
- [ ] Config for basic EU rates and storage values
- [ ] Config for enabling conservative balance adjustments
- [ ] Config migration notes

## Milestone 6 - Polish And Release Safety
- [x] Basic machine sounds
- [~] Menus and screens in usable state
- [~] Localization pass in progress
- [ ] Multiplayer verification
- [ ] Save migration verification
- [ ] Final GUI, assets and balance pass
