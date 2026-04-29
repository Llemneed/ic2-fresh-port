package ic2.core.init;

import ic2.core.IC2;
import ic2.core.block.generator.GeneratorBlockEntity;
import ic2.core.block.generator.GeoGeneratorBlockEntity;
import ic2.core.block.generator.SolarGeneratorBlockEntity;
import ic2.core.block.generator.StirlingGeneratorBlockEntity;
import ic2.core.block.generator.WaterGeneratorBlockEntity;
import ic2.core.block.generator.WindGeneratorBlockEntity;
import ic2.core.block.machine.ElectricFurnaceBlockEntity;
import ic2.core.block.machine.ExtractorBlockEntity;
import ic2.core.block.machine.IronFurnaceBlockEntity;
import ic2.core.block.machine.MaceratorBlockEntity;
import ic2.core.block.machine.MetalFormerBlockEntity;
import ic2.core.block.machine.CompressorBlockEntity;
import ic2.core.block.machine.RecyclerBlockEntity;
import ic2.core.block.machine.SolidCannerBlockEntity;
import ic2.core.block.storage.BatBoxBlockEntity;
import ic2.core.block.storage.ChargepadBatBoxBlockEntity;
import ic2.core.block.storage.ChargepadCesuBlockEntity;
import ic2.core.block.storage.ChargepadMfeBlockEntity;
import ic2.core.block.storage.ChargepadMfsuBlockEntity;
import ic2.core.block.storage.CesuBlockEntity;
import ic2.core.block.storage.MfeBlockEntity;
import ic2.core.block.storage.MfsuBlockEntity;
import ic2.core.block.wiring.CableBlockEntity;
import ic2.core.block.wiring.TransformerBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class IC2BlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, IC2.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MaceratorBlockEntity>> MACERATOR = BLOCK_ENTITY_TYPES.register(
            "macerator",
            () -> BlockEntityType.Builder.of(MaceratorBlockEntity::new, IC2Blocks.MACERATOR.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExtractorBlockEntity>> EXTRACTOR = BLOCK_ENTITY_TYPES.register(
            "extractor",
            () -> BlockEntityType.Builder.of(ExtractorBlockEntity::new, IC2Blocks.EXTRACTOR.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IronFurnaceBlockEntity>> IRON_FURNACE = BLOCK_ENTITY_TYPES.register(
            "iron_furnace",
            () -> BlockEntityType.Builder.of(IronFurnaceBlockEntity::new, IC2Blocks.IRON_FURNACE.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ElectricFurnaceBlockEntity>> ELECTRIC_FURNACE = BLOCK_ENTITY_TYPES.register(
            "electric_furnace",
            () -> BlockEntityType.Builder.of(ElectricFurnaceBlockEntity::new, IC2Blocks.ELECTRIC_FURNACE.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CompressorBlockEntity>> COMPRESSOR = BLOCK_ENTITY_TYPES.register(
            "compressor",
            () -> BlockEntityType.Builder.of(CompressorBlockEntity::new, IC2Blocks.COMPRESSOR.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MetalFormerBlockEntity>> METAL_FORMER = BLOCK_ENTITY_TYPES.register(
            "metal_former",
            () -> BlockEntityType.Builder.of(MetalFormerBlockEntity::new, IC2Blocks.METAL_FORMER.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RecyclerBlockEntity>> RECYCLER = BLOCK_ENTITY_TYPES.register(
            "recycler",
            () -> BlockEntityType.Builder.of(RecyclerBlockEntity::new, IC2Blocks.RECYCLER.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolidCannerBlockEntity>> SOLID_CANNER = BLOCK_ENTITY_TYPES.register(
            "solid_canner",
            () -> BlockEntityType.Builder.of(SolidCannerBlockEntity::new, IC2Blocks.SOLID_CANNER.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeneratorBlockEntity>> GENERATOR = BLOCK_ENTITY_TYPES.register(
            "generator",
            () -> BlockEntityType.Builder.of(GeneratorBlockEntity::new, IC2Blocks.GENERATOR.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeoGeneratorBlockEntity>> GEO_GENERATOR = BLOCK_ENTITY_TYPES.register(
            "geo_generator",
            () -> BlockEntityType.Builder.of(GeoGeneratorBlockEntity::new, IC2Blocks.GEO_GENERATOR.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StirlingGeneratorBlockEntity>> STIRLING_GENERATOR = BLOCK_ENTITY_TYPES.register(
            "stirling_generator",
            () -> BlockEntityType.Builder.of(StirlingGeneratorBlockEntity::new, IC2Blocks.STIRLING_GENERATOR.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarGeneratorBlockEntity>> SOLAR_GENERATOR = BLOCK_ENTITY_TYPES.register(
            "solar_generator",
            () -> BlockEntityType.Builder.of(SolarGeneratorBlockEntity::new, IC2Blocks.SOLAR_GENERATOR.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WaterGeneratorBlockEntity>> WATER_GENERATOR = BLOCK_ENTITY_TYPES.register(
            "water_generator",
            () -> BlockEntityType.Builder.of(WaterGeneratorBlockEntity::new, IC2Blocks.WATER_GENERATOR.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WindGeneratorBlockEntity>> WIND_GENERATOR = BLOCK_ENTITY_TYPES.register(
            "wind_generator",
            () -> BlockEntityType.Builder.of(WindGeneratorBlockEntity::new, IC2Blocks.WIND_GENERATOR.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BatBoxBlockEntity>> BATBOX = BLOCK_ENTITY_TYPES.register(
            "batbox",
            () -> BlockEntityType.Builder.of(BatBoxBlockEntity::new, IC2Blocks.BATBOX.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CesuBlockEntity>> CESU = BLOCK_ENTITY_TYPES.register(
            "cesu",
            () -> BlockEntityType.Builder.of(CesuBlockEntity::new, IC2Blocks.CESU.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MfeBlockEntity>> MFE = BLOCK_ENTITY_TYPES.register(
            "mfe",
            () -> BlockEntityType.Builder.of(MfeBlockEntity::new, IC2Blocks.MFE.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MfsuBlockEntity>> MFSU = BLOCK_ENTITY_TYPES.register(
            "mfsu",
            () -> BlockEntityType.Builder.of(MfsuBlockEntity::new, IC2Blocks.MFSU.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChargepadBatBoxBlockEntity>> CHARGEPAD_BATBOX = BLOCK_ENTITY_TYPES.register(
            "chargepad_batbox",
            () -> BlockEntityType.Builder.of(ChargepadBatBoxBlockEntity::new, IC2Blocks.CHARGEPAD_BATBOX.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChargepadCesuBlockEntity>> CHARGEPAD_CESU = BLOCK_ENTITY_TYPES.register(
            "chargepad_cesu",
            () -> BlockEntityType.Builder.of(ChargepadCesuBlockEntity::new, IC2Blocks.CHARGEPAD_CESU.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChargepadMfeBlockEntity>> CHARGEPAD_MFE = BLOCK_ENTITY_TYPES.register(
            "chargepad_mfe",
            () -> BlockEntityType.Builder.of(ChargepadMfeBlockEntity::new, IC2Blocks.CHARGEPAD_MFE.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChargepadMfsuBlockEntity>> CHARGEPAD_MFSU = BLOCK_ENTITY_TYPES.register(
            "chargepad_mfsu",
            () -> BlockEntityType.Builder.of(ChargepadMfsuBlockEntity::new, IC2Blocks.CHARGEPAD_MFSU.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableBlockEntity>> TIN_CABLE = BLOCK_ENTITY_TYPES.register(
            "tin_cable",
            () -> BlockEntityType.Builder.of(CableBlockEntity::new, IC2Blocks.TIN_CABLE.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableBlockEntity>> COPPER_CABLE = BLOCK_ENTITY_TYPES.register(
            "copper_cable",
            () -> BlockEntityType.Builder.of(CableBlockEntity::new, IC2Blocks.COPPER_CABLE.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableBlockEntity>> GOLD_CABLE = BLOCK_ENTITY_TYPES.register(
            "gold_cable",
            () -> BlockEntityType.Builder.of(CableBlockEntity::new, IC2Blocks.GOLD_CABLE.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableBlockEntity>> GLASS_FIBRE_CABLE = BLOCK_ENTITY_TYPES.register(
            "glass_fibre_cable",
            () -> BlockEntityType.Builder.of(CableBlockEntity::new, IC2Blocks.GLASS_FIBRE_CABLE.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TransformerBlockEntity>> LV_TRANSFORMER = BLOCK_ENTITY_TYPES.register(
            "lv_transformer",
            () -> BlockEntityType.Builder.of(TransformerBlockEntity::new, IC2Blocks.LV_TRANSFORMER.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TransformerBlockEntity>> MV_TRANSFORMER = BLOCK_ENTITY_TYPES.register(
            "mv_transformer",
            () -> BlockEntityType.Builder.of(TransformerBlockEntity::new, IC2Blocks.MV_TRANSFORMER.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TransformerBlockEntity>> HV_TRANSFORMER = BLOCK_ENTITY_TYPES.register(
            "hv_transformer",
            () -> BlockEntityType.Builder.of(TransformerBlockEntity::new, IC2Blocks.HV_TRANSFORMER.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TransformerBlockEntity>> EV_TRANSFORMER = BLOCK_ENTITY_TYPES.register(
            "ev_transformer",
            () -> BlockEntityType.Builder.of(TransformerBlockEntity::new, IC2Blocks.EV_TRANSFORMER.get()).build(null)
    );

    private IC2BlockEntities() {
    }
}
