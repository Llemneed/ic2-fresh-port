# Migration Plan

This document tracks runtime compatibility strategy for saves and item data while the port is still evolving.

## 1. Item Energy Migration

### Current direction
- Old storage: `CUSTOM_DATA` -> `Charge`
- New storage: `IC2DataComponents.ENERGY_STORED`

### Current strategy
- On charge read, `ElectricItemManager` first checks `IC2DataComponents.ENERGY_STORED`
- If absent, it checks legacy `CUSTOM_DATA.Charge`
- If legacy charge exists, it migrates the value into `IC2DataComponents.ENERGY_STORED`
- After successful migration, legacy `Charge` is removed from custom data

### Why this is safe
- Old item stacks remain readable
- New item stacks are written only in the new format
- Migration is lazy and does not require a one-shot world upgrader

### Before release
- Verify migration on old batteries, tools and armor created before the Data Components change
- Verify migrated items survive save/load, slot transfer and GUI interaction

## 2. BlockEntity NBT Migration Strategy

### Rules
- Preserve existing NBT keys whenever possible
- Avoid changing inventory key names without a compatibility path
- Keep energy/progress field names stable unless absolutely necessary

### Current stabilization work
- Multi-slot inventory loading bugs were fixed by deserializing into the existing `ItemStackHandler`
- `loadAdditional(...)` now checks `tag.contains(\"inventory\")` before reading inventory
- Shared base classes reduce the chance of future slot-count desync bugs

### Before release
- Re-open old worlds containing:
  - Generator
  - BatBox / CESU / MFE / MFSU
  - Macerator
  - Extractor
  - Compressor
  - Electric Furnace
  - Metal Former
  - Recycler
  - Solid Canner
- Confirm no slot loss, no dropped upgrades, no broken progress state

## 3. Inventory Format Safety

### Requirements
- Slot count must never silently shrink for an existing machine
- Inventory save/load helpers must deserialize into the real machine handler
- Dropping contents must extract from the handler, not copy stale stacks

### Current safety notes
- Shared inventory base classes are now responsible for safe drop/save/load behavior
- This should be kept centralized to avoid regressions when new machines are added

### Future safety checks
- Add GameTests for representative machine inventory persistence
- Document any machine whose slot layout changes across versions

## 4. Versioned Migration Notes

This section should be updated whenever a storage format meaningfully changes.

### Pre-versioned state
- Early port builds used mixed ad-hoc item charge storage
- Some machine classes had unsafe inventory load implementations

### Current migration-relevant state
- Electric items: migrating toward `IC2DataComponents.ENERGY_STORED`
- Machines: inventory deserialization now standardized
- Recipes: moving from hardcoded fallback toward data-driven recipe lookup

### Future note format
For each release candidate or public test build, record:
- save version label
- item data format changes
- block entity NBT changes
- removed fallbacks
- required manual regression tests

## 5. What Must Be Tested Before Releases

### Item energy
- [ ] Old electric items migrate correctly
- [ ] New electric items do not write legacy `Charge`
- [ ] Battery charge bars remain correct after migration

### Machine persistence
- [ ] Machine inventories survive save/load
- [ ] Stored energy survives save/load
- [ ] Processing progress does not corrupt inventory on reload

### Recipe compatibility
- [ ] Data-driven recipes resolve correctly after reload
- [ ] Legacy fallback still protects older gameplay paths where intended

### Multiplayer
- [ ] Server-authoritative energy/item state remains correct after reconnect

### Removal of old compatibility paths
- [ ] Do not remove legacy item charge migration until old stacks are tested
- [ ] Do not remove recipe fallbacks until JSON coverage is confirmed for real gameplay
