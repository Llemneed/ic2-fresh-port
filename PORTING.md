## IC2 Fresh Port Baseline

Source of truth for the legacy mod:

- Original source archive: `C:\Users\user\Documents\New project\ic2-from-zip-2\industrialcraft2-master`
- Legacy Java code copied for reference: `legacy-src/java`
- Legacy old-format metadata copied for reference: `legacy-src/resources`

Current target:

- Minecraft `1.21.1`
- NeoForge `21.1.227`
- Java `21`

What was verified:

- The imported archive is the real IC2 Java source, not just assets.
- The original mod targets `Minecraft 1.12.2` with `ForgeGradle 2.3`.
- The legacy source tree contains roughly `927` Java files and `3045` resources.

First real compile pass against NeoForge 1.21.1:

- The modern compile fails immediately on the old `Forge 1.12.2` / old Minecraft namespace.
- Typical failures:
  - `net.minecraftforge.fml.relauncher.*` no longer exists.
  - `net.minecraft.world.World`, `net.minecraft.util.math.BlockPos`, `NBTTagCompound`, `TileEntity`, old item/entity imports, and many old Forge events no longer exist under those names.
  - There are also API-shape conflicts inside old IC2 interfaces once modern types are applied.

Migration approach from here:

1. Keep the modern NeoForge project compiling at all times.
2. Port subsystems from `legacy-src/java` into `src/main/java` incrementally.
3. Start with thin compatibility layers and data/registry bootstrap.
4. Port machines, energy, recipes, GUI, worldgen, and items in isolated slices instead of trying to compile the whole 1.12.2 tree at once.

Current migrated baseline:

- Replaced the temporary `IC2NeoForge` bootstrap with a real modern `ic2.core.IC2` entrypoint.
- Added modern NeoForge registries for:
  - base ores: `lead_ore`, `tin_ore`, `uranium_ore`
  - rubber tree content: `rubber_wood`, `rubber_leaves`, `rubber_sapling`
  - starter items: `treetap`, `sticky_resin`, `rubber`, ingots, and dusts
- Added a dedicated IC2 creative tab.
- Added first-pass assets, translations, recipes, loot tables, tool tags, and worldgen JSON for ores and rubber trees.
- Ported the first live rubber extraction slice:
  - modern `RubberWoodBlock` with wet/dry resin states
  - resin amount is now stored per tree spot as `1..3`
  - regrowth logic for resin-bearing rubber wood
  - modern `TreetapItem` that extracts sticky resin from the correct face
  - updated rubber wood blockstates and treetap item model/texture wiring
- Ported the first machine baseline:
  - modern `MaceratorBlock` with facing and active states
  - server-side `MaceratorBlockEntity` with input/output inventory, progress, and simple adjacent energy intake
  - menu + client screen wiring for the first openable IC2 machine
  - starter macerator processing map for IC2 ores and a few vanilla materials
  - modern `IronFurnaceBlock` and `IronFurnaceBlockEntity` as a faster fuel-based furnace baseline
  - modern `GeneratorBlock` and `GeneratorBlockEntity` with coal fuel, stored energy, and simple adjacent energy output
- The project currently compiles and packages successfully:
  - `compileJava` green
  - `build` green
  - output jar: `build/libs/ic2-2.8.109-port1211.jar`

Next migration layer:

1. Replace temporary starter recipes/material flow with fuller IC2 processing chains.
2. Introduce the first real machine slice:
   - electric furnace
3. Start rebuilding the energy/item charge layer that those machines depend on.
4. Revisit survival recipes and progression balance for newly ported tools/items.
