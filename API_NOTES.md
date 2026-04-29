# IC2 Reference Notes

This file collects the most useful external references already inspected for the
`1.21.1` NeoForge port. It is meant to reduce re-discovery work while porting
machines, energy, upgrades, electric items, and progression.

## Reference Inputs

### 1. IC2 API jar

Reference file:

- `C:\Users\user\Downloads\industrialcraft-2-2.8.188-ex112-api.jar`

Useful confirmed packages/classes:

- Energy:
  - `ic2.api.energy.EnergyNet`
  - `ic2.api.energy.tile.IEnergyTile`
  - `IEnergyAcceptor`
  - `IEnergyEmitter`
  - `IEnergySink`
  - `IEnergySource`
  - `IEnergyConductor`
  - `IOverloadHandler`
  - prefab helpers: `BasicSink`, `BasicSource`, `BasicSinkSource`
- Electric items:
  - `ic2.api.item.IElectricItem`
  - `IElectricItemManager`
  - `ISpecialElectricItem`
  - `ElectricItem`
  - `IMiningDrill`
  - `IMetalArmor`
  - `IHazmatLike`
- Machine recipes:
  - `IBasicMachineRecipeManager`
  - `IMachineRecipeManager`
  - `IRecipeInput`
  - `MachineRecipe`
  - `MachineRecipeResult`
  - `Recipes`
  - `IScrapboxManager`
- Upgrades:
  - `IUpgradableBlock`
  - `IUpgradeItem`
  - `IProcessingUpgrade`
  - `IEnergyStorageUpgrade`
  - `ITransformerUpgrade`
  - `IRedstoneSensitiveUpgrade`
  - `UpgradeRegistry`
  - `UpgradableProperty`

What the API is good for:

- confirms the shape of IC2 systems
- shows which roles belong to energy tiles, electric items, recipes, and upgrades
- helps keep the new port structurally compatible with IC2 concepts

What the API does *not* provide by itself:

- exact machine timings
- exact EU/t values for every machine
- internal formulas for upgrades
- GUI layout/details
- the full original implementation of machine logic

### 2. Full original IC2 jar

Reference file:

- `C:\Users\user\Downloads\industrialcraft-2-2.8.222-ex112.jar`

Confirmed useful classes:

- Machines:
  - `ic2/core/block/machine/tileentity/TileEntityMacerator.class`
  - `TileEntityExtractor.class`
  - `TileEntityElectricFurnace.class`
- Energy storage:
  - `ic2/core/block/wiring/TileEntityElectricBatBox.class`
- Upgrades:
  - `ic2/core/block/invslot/InvSlotUpgrade.class`
  - `ic2/core/item/upgrade/ItemUpgradeModule.class`
  - `ic2/api/upgrade/UpgradeRegistry.class`
- Electric items:
  - `ic2/core/item/ElectricItemManager.class`
  - `BaseElectricItem.class`
  - `GatewayElectricItemManager.class`
- Recipes:
  - `ic2/api/recipe/Recipes.class`

Why this jar matters:

- best source for original IC2 behavior on `1.12.2`
- useful for cross-checking machine timing, slot roles, upgrade behavior, and
  electric item logic
- should be used as the primary behavior reference when API docs are vague

### 3. IC3 1.7.10 client jar

Reference file:

- `C:\Users\user\AppData\Roaming\GravityCraft\updates\HiTech\mods\IndustrialCraft-3-1.7.10-client.jar`

Confirmed useful reference classes:

- Machines:
  - `ic3/common/tile/machine/TileEntityMacerator.class`
  - `TileEntityGenerator.class`
  - `TileEntityElectricFurnace.class`
  - `TileEntityGeoGenerator.class`
- Energy storage:
  - `TileEntityElectricBatBox.class`
  - `TileEntityElectricCESU.class`
  - `TileEntityElectricMFE.class`
  - `TileEntityElectricMFSU.class`
- GUI/container/inventory:
  - `ContainerStandardMachine.class`
  - `InvSlotUpgrade.class`
- Upgrades:
  - `UpgradeRegistry.class`
  - `ItemUpgradeModule$Type.class`
- Recipes:
  - `BasicMachineRecipeManager.class`
  - `Recipes.class`

Why this jar matters:

- useful implementation reference for older IC-style behavior
- helpful when the original IC2 API only exposes interfaces
- especially useful for storage blocks, upgrades, and machine container layout

### 4. IC2 Classic 1.19.2 jar

Reference file:

- `C:\Users\user\Downloads\ic2classic-1.19.2-2.1.3.2.jar`

Confirmed useful modern classes/assets:

- Machines:
  - `ic2/core/block/machines/tiles/lv/MaceratorTileEntity.class`
  - `ExtractorTileEntity.class`
  - `ElectricFurnaceTileEntity.class`
- Generators:
  - `ic2/core/block/generators/tiles/FuelGenTileEntity.class`
  - `GeoGenTileEntity.class`
- Energy:
  - `ic2/api/energy/EnergyNet.class`
  - `ic2/core/energy/EnergyNetGlobal.class`
  - `EnergyNetGrid.class`
  - `EnergyNetLocal.class`
- Storage:
  - `ic2/core/block/storage/tiles/storage/BatBoxTileEntity.class`
- Upgrades:
  - `UpgradeSlot.class`
  - `OverclockerUpgradeItem.class`
  - `TransformerUpgradeItem.class`
  - `EnergyStorageUpgradeItem.class`
  - `RedstoneInverterUpgradeItem.class`
  - `RedstoneSensitivityUpgradeItem.class`
  - `Import/Export` item/fluid upgrades
- Electric gear:
  - `NanoSuit.class`
  - `QuantumSuit.class`
  - `NanoSaber.class`
- Assets:
  - extractor and generator machine sounds
  - batbox textures

Why this jar matters:

- moderner codebase than `1.12.2`, useful for migration patterns
- good reference for how later IC-style projects structured machine tiers,
  generators, energy storage, upgrades, and electric gear
- not a 1:1 behavior authority for classic IC2, but very useful for port design

### 5. IC3.ini config

Reference file:

- `C:\Users\user\AppData\Roaming\GravityCraft\updates\HiTech\config\IC3.ini`

Useful extracted values/settings:

- Worldgen:
  - `rubberTree = true`
  - `copperOre = true`
  - `tinOre = true`
  - `uraniumOre = true`
  - `leadOre = true`
  - `oreDensityFactor = 1.0`
- Protection / explosions:
  - `enableExplosionMachine = false`
  - `nukeExplosionPowerLimit = 60`
  - `reactorExplosionPowerLimit = 45`
- Generator balance multipliers:
  - `generator = 1.0`
  - `geothermal = 1.0`
  - `solar = 1.0`
  - `wind = 1.0`
  - `nuclear = 1.0`
  - `radioisotope = 1.0`
- Misc:
  - `allowBurningScrap = true`
  - `useLinearTransferModel = false`
  - `quantumSpeedOnSprint = true`
  - `smeltToIc2Items = false`
  - `requireIc2Circuits = false`
  - `ignoreWrenchRequirement = false`

Why this config matters:

- confirms enabled worldgen content
- confirms the world/server is using default-ish balance multipliers
- indicates the nonlinear/legacy energy transfer model is preferred
- useful when comparing expected gameplay to current port behavior

### 6. Original IC2 2.8.222 ex112 config

Reference file:

- `C:\Users\user\AppData\Roaming\PrismLauncher\instances\1.12.2\minecraft\config\IC2.ini`

Why this config matters:

- this is a better gameplay authority than `IC3.ini` for the current port
- it reflects the actual `IC2 2.8.222 ex112` setup the user wants to match

Useful extracted values/settings:

- Profile:
  - `selected = Experimental`
- Worldgen globals:
  - `rubberTree = true`
  - `oreDensityFactor = 1.0`
  - `treeDensityFactor = 1.0`
  - `normalizeHeight = true`
  - `retrogenCheckLimit = 0`
  - `retrogenUpdateLimit = 2`
- Copper ore:
  - `enabled = true`
  - `count = 15`
  - `size = 10`
  - `minHeight = 0`
  - `maxHeight = 68`
  - `distribution = smooth`
- Lead ore:
  - `enabled = true`
  - `count = 8`
  - `size = 4`
  - `minHeight = 0`
  - `maxHeight = 64`
  - `distribution = uniform`
- Tin ore:
  - `enabled = true`
  - `count = 25`
  - `size = 6`
  - `minHeight = 0`
  - `maxHeight = 40`
  - `distribution = uniform`
- Uranium ore:
  - `enabled = true`
  - `count = 20`
  - `size = 3`
  - `minHeight = 0`
  - `maxHeight = 64`
  - `distribution = uniform`
- Protection:
  - `enableNuke = true`
  - `nukeExplosionPowerLimit = 60`
  - `reactorExplosionPowerLimit = 45`
- Balance:
  - `energyRetainedInStorageBlockDrops = 0.8`
  - `massFabricatorTier = 3`
  - `matterFabricatorTier = 3`
  - `uuEnergyFactor = 1.0`
  - `watermillAutomation = false`
  - `euPerChunk = 1.0`
- Generator multipliers:
  - `generator = 1.0`
  - `geothermal = 1.0`
  - `water = 1.0`
  - `solar = 1.0`
  - `wind = 1.0`
  - `nuclear = 1.0`
  - `semiFluidOil = 1.0`
  - `semiFluidFuel = 1.0`
  - `semiFluidBiomass = 1.0`
  - `semiFluidBioethanol = 1.0`
  - `semiFluidBiogas = 1.0`
  - `Stirling = 1.0`
  - `Kinetic = 1.0`
  - `radioisotope = 1.0`
- Recipes / progression:
  - `requireIc2Circuits = false`
  - `smeltToIc2Items = false`
  - `allowCoinCrafting = true`
- Audio:
  - `enabled = true`
  - `volume = 1.0`
  - `fadeDistance = 16`
  - `maxSourceCount = 32`
- Misc:
  - `hideSecretRecipes = true`
  - `quantumSpeedOnSprint = true`
  - `allowBurningScrap = true`
  - `useLinearTransferModel = false`
  - `roundEnetLoss = true`
  - `enableEnetExplosions = true`
  - `enableEnetCableMeltdown = true`

Direct gameplay implications for the port:

- keep the port aligned to the `Experimental` profile, not Classic
- ore worldgen should eventually mirror these exact counts, sizes, heights, and
  distributions
- rubber tree generation should stay enabled
- the nonlinear/legacy-style energy network remains the correct target
- machine overvoltage and cable meltdown should stay meaningful, not be removed
- burning scrap in generators is canon for this setup

## Porting Guidance Derived From References

### Energy

- Prefer an IC2-like packet/tier model over a generic RF-style buffer model.
- `Transformer Upgrade` should only become fully meaningful once packet limits,
  tier checks, and overvoltage are wired consistently.
- Charge/discharge behavior must stay server-driven.

### Machines

- Recipes should live in a dedicated machine-recipe layer rather than being
  spread across vanilla logic.
- `Macerator`, `Extractor`, `Electric Furnace`, and `BatBox` are the next core
  canonical machines to keep aligned with reference behavior.

### Upgrades

- Real upgrade items should be preferred over temporary placeholder mechanics.
- Useful baseline upgrade set:
  - overclocker
  - transformer
  - energy storage
  - redstone inverter
- Later extensions can follow IC2 Classic/IC3 references for import/export,
  fluid transport, and redstone sensitivity.

### Electric tools and armor

- When implementing drills, chainsaw, nanosaber, nanosuit, and quantumsuit,
  model them around:
  - `IElectricItem`
  - `IElectricItemManager`
  - item-side transfer limit
  - server-side charge handling

## Immediate Next Priorities

1. Finish `Extractor` as a real machine rather than a placeholder.
2. Implement `BatBox`.
3. Build item charging around a reusable electric-item layer.
4. Port electric tools and electric armor using that shared charge layer.
