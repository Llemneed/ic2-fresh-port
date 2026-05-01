# Testing Plan

This checklist is focused on keeping the current IC2 Fresh Port stable while the architecture and recipe systems are still moving.

## 1. Manual Test Checklist

### Bootstrap and loading
- [ ] `./gradlew clean build` passes
- [ ] `./gradlew runClient` starts without IC2-specific hard errors
- [ ] Client loads a world with `ic2` content enabled
- [ ] Dedicated server starts without client-only crashes

### Early gameplay loop
- [ ] Craft `RE Battery`
- [ ] Craft `Generator`
- [ ] Craft `BatBox`
- [ ] Craft `Macerator`
- [ ] Burn `coal`
- [ ] Burn `charcoal`
- [ ] Burn `scrap`
- [ ] Burn `lava bucket` and confirm empty bucket return
- [ ] Generator outputs EU into BatBox
- [ ] BatBox stores EU
- [ ] BatBox outputs EU toward a machine
- [ ] Macerator processes `raw_iron`
- [ ] Macerator processes `raw_copper`
- [ ] Macerator processes `raw_gold`
- [ ] Dusts smelt back into ingots

### Machine coverage
- [ ] Extractor basic recipes
- [ ] Compressor basic recipes
- [ ] Electric Furnace smelting
- [ ] Metal Former mode switching and recipe execution
- [ ] Solid Canner dual-input recipes
- [ ] Recycler produces scrap

### Storage and electric items
- [ ] Charge `RE Battery` in BatBox
- [ ] Discharge `RE Battery` into BatBox
- [ ] Test partially charged battery routing with shift-click
- [ ] Test full battery routing with shift-click
- [ ] Verify battery energy bar at `0 EU`
- [ ] Verify battery energy bar at full charge

## 2. GUI / quickMoveStack Checklist

For every machine/storage menu:
- [ ] Input slot rejects invalid items
- [ ] Output slot is read-only
- [ ] Upgrade slots reject non-upgrades
- [ ] Charge slot rejects invalid items
- [ ] Fuel slot rejects invalid items
- [ ] Shift-click from player inventory routes correctly
- [ ] Shift-click from hotbar routes correctly
- [ ] Shift-click from machine slots does not delete items
- [ ] Shift-click does not duplicate items

Priority menus:
- [ ] GeneratorMenu
- [ ] BatBoxMenu
- [ ] MaceratorMenu
- [ ] ExtractorMenu
- [ ] CompressorMenu
- [ ] ElectricFurnaceMenu
- [ ] MetalFormerMenu
- [ ] RecyclerMenu
- [ ] SolidCannerMenu

## 3. Save / Load Regression Tests

### Block entities
- [ ] Place Generator, BatBox, Macerator and save world
- [ ] Reload world and verify inventory contents are preserved
- [ ] Reload world and verify stored energy is preserved
- [ ] Reload world and verify processing progress does not corrupt inventory

### Old saves
- [ ] Open a save made before Data Components item charge migration
- [ ] Verify old charged battery migrates to `IC2DataComponents.ENERGY_STORED`
- [ ] Open a save with pre-fix machine inventories
- [ ] Verify no slot loss in Macerator / Extractor / Compressor / Solid Canner

## 4. GameTest Candidates

These are good targets for future automated tests:

### Core loop
- [ ] Generator burns fuel and increases internal energy
- [ ] BatBox accepts and stores EU
- [ ] BatBox outputs EU only on facing side
- [ ] Macerator consumes energy and outputs correct dust

### Recipe systems
- [ ] `ic2:macerating` recipe resolves for `raw_iron`
- [ ] `ic2:extracting` recipe resolves for `sticky_resin`
- [ ] `ic2:compressing` recipe resolves for `carbon_mesh`
- [ ] `ic2:metal_forming` recipe resolves per mode
- [ ] `ic2:solid_canning` recipe resolves with dual input

### Migration
- [ ] Legacy battery `Charge` migrates to Data Component on first read
- [ ] Machine inventory load preserves all slots after reload

## 5. Dedicated Server Checks

- [ ] Dedicated server starts clean
- [ ] Client joins dedicated server clean
- [ ] Generator / BatBox / Macerator loop works on dedicated server
- [ ] Machine GUIs open without desync
- [ ] Electric item charge changes are server-authoritative
- [ ] No client-only rendering classes referenced from common logic

## 6. Energy Duplication / Loss Tests

- [ ] Energy is not duplicated when charging and discharging batteries repeatedly
- [ ] BatBox does not destroy partial battery charge during shift-click routing
- [ ] Generator only consumes fuel that it actually starts burning
- [ ] Lava bucket returns empty bucket consistently
- [ ] Storage output only subtracts actually sent EU
- [ ] Cable routing does not multiply packets
- [ ] Overvoltage path does not silently accept oversized packets

## 7. Visual / Asset Checks

- [ ] No missing textures in latest logs for core progression content
- [ ] Chargepads look correct in world
- [ ] Chargepads look correct in creative inventory
- [ ] Cables look correct in creative inventory
- [ ] Cables no longer show black background / full-block artifacts in world
- [ ] Transformer item/block textures are present

## 8. Future Cable Network Benchmark

When the cable system is closer to parity:
- [ ] 1 generator -> 1 cable -> 1 batbox sanity check
- [ ] Long cable line with visible loss expectations
- [ ] Multiple consumers on one line
- [ ] Overvoltage / meltdown verification
- [ ] Repeated tick benchmark with several generators, cables and machines
- [ ] Check for recursive routing performance spikes
