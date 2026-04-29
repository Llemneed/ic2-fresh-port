package ic2.core.init;

import ic2.core.IC2;
import ic2.core.block.generator.GeneratorBlock;
import ic2.core.block.generator.GeoGeneratorBlock;
import ic2.core.block.generator.SolarGeneratorBlock;
import ic2.core.block.generator.StirlingGeneratorBlock;
import ic2.core.block.generator.WaterGeneratorBlock;
import ic2.core.block.generator.WindGeneratorBlock;
import ic2.core.block.RubberWoodBlock;
import ic2.core.block.machine.ElectricFurnaceBlock;
import ic2.core.block.machine.ExtractorBlock;
import ic2.core.block.machine.IronFurnaceBlock;
import ic2.core.block.machine.MaceratorBlock;
import ic2.core.block.machine.MetalFormerBlock;
import ic2.core.block.machine.CompressorBlock;
import ic2.core.block.machine.RecyclerBlock;
import ic2.core.block.machine.SolidCannerBlock;
import ic2.core.block.storage.BatBoxBlock;
import ic2.core.block.storage.ChargepadBatBoxBlock;
import ic2.core.block.storage.ChargepadCesuBlock;
import ic2.core.block.storage.ChargepadMfeBlock;
import ic2.core.block.storage.ChargepadMfsuBlock;
import ic2.core.block.storage.CesuBlock;
import ic2.core.block.storage.MfeBlock;
import ic2.core.block.storage.MfsuBlock;
import ic2.core.block.wiring.CableBlock;
import ic2.core.block.wiring.CableType;
import ic2.core.block.wiring.TransformerBlock;
import ic2.core.block.wiring.TransformerType;
import java.util.Optional;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class IC2Blocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(IC2.MODID);

    private static final TreeGrower RUBBER_TREE_GROWER = new TreeGrower(
            "ic2_rubber",
            Optional.empty(),
            Optional.of(IC2Worldgen.RUBBER_TREE),
            Optional.empty()
    );

    public static final DeferredBlock<Block> LEAD_ORE = BLOCKS.registerSimpleBlock(
            "lead_ore",
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE).requiresCorrectToolForDrops()
    );
    public static final DeferredBlock<Block> TIN_ORE = BLOCKS.registerSimpleBlock(
            "tin_ore",
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE).requiresCorrectToolForDrops()
    );
    public static final DeferredBlock<Block> URANIUM_ORE = BLOCKS.registerSimpleBlock(
            "uranium_ore",
            BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_DIAMOND_ORE).requiresCorrectToolForDrops()
    );
    public static final DeferredBlock<MaceratorBlock> MACERATOR = BLOCKS.registerBlock(
            "macerator",
            MaceratorBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F)
    );
    public static final DeferredBlock<ExtractorBlock> EXTRACTOR = BLOCKS.registerBlock(
            "extractor",
            ExtractorBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F)
    );
    public static final DeferredBlock<IronFurnaceBlock> IRON_FURNACE = BLOCKS.registerBlock(
            "iron_furnace",
            IronFurnaceBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F)
    );
    public static final DeferredBlock<ElectricFurnaceBlock> ELECTRIC_FURNACE = BLOCKS.registerBlock(
            "electric_furnace",
            ElectricFurnaceBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F)
    );
    public static final DeferredBlock<CompressorBlock> COMPRESSOR = BLOCKS.registerBlock(
            "compressor",
            CompressorBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F)
    );
    public static final DeferredBlock<MetalFormerBlock> METAL_FORMER = BLOCKS.registerBlock(
            "metal_former",
            MetalFormerBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F)
    );
    public static final DeferredBlock<RecyclerBlock> RECYCLER = BLOCKS.registerBlock(
            "recycler",
            RecyclerBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F)
    );
    public static final DeferredBlock<SolidCannerBlock> SOLID_CANNER = BLOCKS.registerBlock(
            "solid_canner",
            SolidCannerBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F)
    );
    public static final DeferredBlock<GeneratorBlock> GENERATOR = BLOCKS.registerBlock(
            "generator",
            GeneratorBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F)
    );
    public static final DeferredBlock<GeoGeneratorBlock> GEO_GENERATOR = BLOCKS.registerBlock(
            "geo_generator",
            GeoGeneratorBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F)
    );
    public static final DeferredBlock<StirlingGeneratorBlock> STIRLING_GENERATOR = BLOCKS.registerBlock(
            "stirling_generator",
            StirlingGeneratorBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F)
    );
    public static final DeferredBlock<SolarGeneratorBlock> SOLAR_GENERATOR = BLOCKS.registerBlock(
            "solar_generator",
            SolarGeneratorBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F)
    );
    public static final DeferredBlock<WaterGeneratorBlock> WATER_GENERATOR = BLOCKS.registerBlock(
            "water_generator",
            WaterGeneratorBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F)
    );
    public static final DeferredBlock<WindGeneratorBlock> WIND_GENERATOR = BLOCKS.registerBlock(
            "wind_generator",
            WindGeneratorBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F)
    );
    public static final DeferredBlock<BatBoxBlock> BATBOX = BLOCKS.registerBlock(
            "batbox",
            BatBoxBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F)
    );
    public static final DeferredBlock<CesuBlock> CESU = BLOCKS.registerBlock(
            "cesu",
            CesuBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F)
    );
    public static final DeferredBlock<MfeBlock> MFE = BLOCKS.registerBlock(
            "mfe",
            MfeBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F)
    );
    public static final DeferredBlock<MfsuBlock> MFSU = BLOCKS.registerBlock(
            "mfsu",
            MfsuBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F)
    );
    public static final DeferredBlock<ChargepadBatBoxBlock> CHARGEPAD_BATBOX = BLOCKS.registerBlock(
            "chargepad_batbox",
            ChargepadBatBoxBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F).noOcclusion()
    );
    public static final DeferredBlock<ChargepadCesuBlock> CHARGEPAD_CESU = BLOCKS.registerBlock(
            "chargepad_cesu",
            ChargepadCesuBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F).noOcclusion()
    );
    public static final DeferredBlock<ChargepadMfeBlock> CHARGEPAD_MFE = BLOCKS.registerBlock(
            "chargepad_mfe",
            ChargepadMfeBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F).noOcclusion()
    );
    public static final DeferredBlock<ChargepadMfsuBlock> CHARGEPAD_MFSU = BLOCKS.registerBlock(
            "chargepad_mfsu",
            ChargepadMfsuBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F).noOcclusion()
    );
    public static final DeferredBlock<CableBlock> TIN_CABLE = BLOCKS.registerBlock(
            "tin_cable",
            properties -> new CableBlock(CableType.TIN, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(0.8F)
    );
    public static final DeferredBlock<CableBlock> COPPER_CABLE = BLOCKS.registerBlock(
            "copper_cable",
            properties -> new CableBlock(CableType.COPPER, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(0.8F)
    );
    public static final DeferredBlock<CableBlock> GOLD_CABLE = BLOCKS.registerBlock(
            "gold_cable",
            properties -> new CableBlock(CableType.GOLD, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(0.8F)
    );
    public static final DeferredBlock<CableBlock> GLASS_FIBRE_CABLE = BLOCKS.registerBlock(
            "glass_fibre_cable",
            properties -> new CableBlock(CableType.GLASS_FIBRE, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).strength(0.5F)
    );
    public static final DeferredBlock<TransformerBlock> LV_TRANSFORMER = BLOCKS.registerBlock(
            "lv_transformer",
            properties -> new TransformerBlock(TransformerType.LV, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F)
    );
    public static final DeferredBlock<TransformerBlock> MV_TRANSFORMER = BLOCKS.registerBlock(
            "mv_transformer",
            properties -> new TransformerBlock(TransformerType.MV, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F)
    );
    public static final DeferredBlock<TransformerBlock> HV_TRANSFORMER = BLOCKS.registerBlock(
            "hv_transformer",
            properties -> new TransformerBlock(TransformerType.HV, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F)
    );
    public static final DeferredBlock<TransformerBlock> EV_TRANSFORMER = BLOCKS.registerBlock(
            "ev_transformer",
            properties -> new TransformerBlock(TransformerType.EV, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3.5F)
    );

    public static final DeferredBlock<RubberWoodBlock> RUBBER_WOOD = BLOCKS.registerBlock(
            "rubber_wood",
            RubberWoodBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG).sound(SoundType.WOOD).strength(2.0F)
    );
    public static final DeferredBlock<LeavesBlock> RUBBER_LEAVES = BLOCKS.registerBlock(
            "rubber_leaves",
            LeavesBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES).strength(0.2F).randomTicks().noOcclusion()
    );
    public static final DeferredBlock<SaplingBlock> RUBBER_SAPLING = BLOCKS.register(
            "rubber_sapling",
            () -> new SaplingBlock(RUBBER_TREE_GROWER, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING))
    );

    private IC2Blocks() {
    }
}
