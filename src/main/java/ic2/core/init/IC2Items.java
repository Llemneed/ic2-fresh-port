package ic2.core.init;

import ic2.core.IC2;
import ic2.core.item.battery.BatteryItem;
import ic2.core.item.armor.NanoArmorItem;
import ic2.core.item.armor.EnergyPackArmorItem;
import ic2.core.item.crafting.ScrapBoxItem;
import ic2.core.item.food.FilledTinCanItem;
import ic2.core.item.tool.electric.ChainsawItem;
import ic2.core.item.tool.electric.DrillItem;
import ic2.core.item.tool.electric.ElectricWrenchItem;
import ic2.core.item.tool.electric.ElectricHoeItem;
import ic2.core.item.tool.electric.ElectricTreetapItem;
import ic2.core.item.tool.electric.MiningLaserItem;
import ic2.core.item.tool.electric.NanoSaberItem;
import ic2.core.item.tool.electric.PlasmaLauncherItem;
import ic2.core.item.tool.electric.ScannerItem;
import ic2.core.item.tool.ObscuratorItem;
import ic2.core.item.tool.TreetapItem;
import ic2.core.item.tool.WindMeterItem;
import ic2.core.item.upgrade.MachineUpgradeItem;
import ic2.core.item.upgrade.MachineUpgradeItem.UpgradeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class IC2Items {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(IC2.MODID);

    public static final DeferredItem<BlockItem> LEAD_ORE = ITEMS.registerSimpleBlockItem(IC2Blocks.LEAD_ORE);
    public static final DeferredItem<BlockItem> TIN_ORE = ITEMS.registerSimpleBlockItem(IC2Blocks.TIN_ORE);
    public static final DeferredItem<BlockItem> URANIUM_ORE = ITEMS.registerSimpleBlockItem(IC2Blocks.URANIUM_ORE);
    public static final DeferredItem<BlockItem> MACERATOR = ITEMS.registerSimpleBlockItem(IC2Blocks.MACERATOR);
    public static final DeferredItem<BlockItem> EXTRACTOR = ITEMS.registerSimpleBlockItem(IC2Blocks.EXTRACTOR);
    public static final DeferredItem<BlockItem> IRON_FURNACE = ITEMS.registerSimpleBlockItem(IC2Blocks.IRON_FURNACE);
    public static final DeferredItem<BlockItem> ELECTRIC_FURNACE = ITEMS.registerSimpleBlockItem(IC2Blocks.ELECTRIC_FURNACE);
    public static final DeferredItem<BlockItem> COMPRESSOR = ITEMS.registerSimpleBlockItem(IC2Blocks.COMPRESSOR);
    public static final DeferredItem<BlockItem> METAL_FORMER = ITEMS.registerSimpleBlockItem(IC2Blocks.METAL_FORMER);
    public static final DeferredItem<BlockItem> RECYCLER = ITEMS.registerSimpleBlockItem(IC2Blocks.RECYCLER);
    public static final DeferredItem<BlockItem> SOLID_CANNER = ITEMS.registerSimpleBlockItem(IC2Blocks.SOLID_CANNER);
    public static final DeferredItem<BlockItem> GENERATOR = ITEMS.registerSimpleBlockItem(IC2Blocks.GENERATOR);
    public static final DeferredItem<BlockItem> GEO_GENERATOR = ITEMS.registerSimpleBlockItem(IC2Blocks.GEO_GENERATOR);
    public static final DeferredItem<BlockItem> STIRLING_GENERATOR = ITEMS.registerSimpleBlockItem(IC2Blocks.STIRLING_GENERATOR);
    public static final DeferredItem<BlockItem> SOLAR_GENERATOR = ITEMS.registerSimpleBlockItem(IC2Blocks.SOLAR_GENERATOR);
    public static final DeferredItem<BlockItem> WATER_GENERATOR = ITEMS.registerSimpleBlockItem(IC2Blocks.WATER_GENERATOR);
    public static final DeferredItem<BlockItem> WIND_GENERATOR = ITEMS.registerSimpleBlockItem(IC2Blocks.WIND_GENERATOR);
    public static final DeferredItem<BlockItem> BATBOX = ITEMS.registerSimpleBlockItem(IC2Blocks.BATBOX);
    public static final DeferredItem<BlockItem> CESU = ITEMS.registerSimpleBlockItem(IC2Blocks.CESU);
    public static final DeferredItem<BlockItem> MFE = ITEMS.registerSimpleBlockItem(IC2Blocks.MFE);
    public static final DeferredItem<BlockItem> MFSU = ITEMS.registerSimpleBlockItem(IC2Blocks.MFSU);
    public static final DeferredItem<BlockItem> CHARGEPAD_BATBOX = ITEMS.registerSimpleBlockItem(IC2Blocks.CHARGEPAD_BATBOX);
    public static final DeferredItem<BlockItem> CHARGEPAD_CESU = ITEMS.registerSimpleBlockItem(IC2Blocks.CHARGEPAD_CESU);
    public static final DeferredItem<BlockItem> CHARGEPAD_MFE = ITEMS.registerSimpleBlockItem(IC2Blocks.CHARGEPAD_MFE);
    public static final DeferredItem<BlockItem> CHARGEPAD_MFSU = ITEMS.registerSimpleBlockItem(IC2Blocks.CHARGEPAD_MFSU);
    public static final DeferredItem<BlockItem> RUBBER_WOOD = ITEMS.registerSimpleBlockItem(IC2Blocks.RUBBER_WOOD);
    public static final DeferredItem<BlockItem> RUBBER_LEAVES = ITEMS.registerSimpleBlockItem(IC2Blocks.RUBBER_LEAVES);
    public static final DeferredItem<BlockItem> RUBBER_SAPLING = ITEMS.registerSimpleBlockItem(IC2Blocks.RUBBER_SAPLING);

    public static final DeferredItem<Item> TREETAP = ITEMS.register(
            "treetap",
            () -> new TreetapItem(new Item.Properties().durability(16))
    );
    public static final DeferredItem<Item> STICKY_RESIN = ITEMS.registerSimpleItem("sticky_resin");
    public static final DeferredItem<Item> RUBBER = ITEMS.registerSimpleItem("rubber");
    public static final DeferredItem<Item> SCRAP = ITEMS.registerSimpleItem("scrap");
    public static final DeferredItem<Item> SCRAP_BOX = ITEMS.register(
            "scrap_box",
            () -> new ScrapBoxItem(new Item.Properties())
    );
    public static final DeferredItem<Item> MACHINE_CASING = ITEMS.registerSimpleItem("machine_casing");
    public static final DeferredItem<Item> ALLOY = ITEMS.registerSimpleItem("alloy");
    public static final DeferredItem<Item> CIRCUIT = ITEMS.registerSimpleItem("circuit");
    public static final DeferredItem<Item> ADVANCED_CIRCUIT = ITEMS.registerSimpleItem("advanced_circuit");
    public static final DeferredItem<Item> COAL_BALL = ITEMS.registerSimpleItem("coal_ball");
    public static final DeferredItem<Item> COAL_CHUNK = ITEMS.registerSimpleItem("coal_chunk");
    public static final DeferredItem<Item> CARBON_FIBRE = ITEMS.registerSimpleItem("carbon_fibre");
    public static final DeferredItem<Item> CARBON_MESH = ITEMS.registerSimpleItem("carbon_mesh");
    public static final DeferredItem<Item> CARBON_PLATE = ITEMS.registerSimpleItem("carbon_plate");
    public static final DeferredItem<Item> COIL = ITEMS.registerSimpleItem("coil");
    public static final DeferredItem<Item> ELECTRIC_MOTOR = ITEMS.registerSimpleItem("electric_motor");
    public static final DeferredItem<Item> SMALL_POWER_UNIT = ITEMS.registerSimpleItem("small_power_unit");
    public static final DeferredItem<Item> POWER_UNIT = ITEMS.registerSimpleItem("power_unit");
    public static final DeferredItem<Item> HEAT_CONDUCTOR = ITEMS.registerSimpleItem("heat_conductor");
    public static final DeferredItem<Item> INDUSTRIAL_DIAMOND = ITEMS.registerSimpleItem("industrial_diamond");
    public static final DeferredItem<Item> PLANT_BALL = ITEMS.registerSimpleItem("plant_ball");
    public static final DeferredItem<Item> ADVANCED_MACHINE = ITEMS.registerSimpleItem("advanced_machine");
    public static final DeferredItem<Item> TIN_CAN = ITEMS.registerSimpleItem("tin_can");
    public static final DeferredItem<Item> FILLED_TIN_CAN = ITEMS.register(
            "filled_tin_can",
            () -> new FilledTinCanItem(new Item.Properties())
    );
    public static final DeferredItem<Item> LEAD_INGOT = ITEMS.registerSimpleItem("lead_ingot");
    public static final DeferredItem<Item> TIN_INGOT = ITEMS.registerSimpleItem("tin_ingot");
    public static final DeferredItem<Item> BRONZE_INGOT = ITEMS.registerSimpleItem("bronze_ingot");
    public static final DeferredItem<Item> URANIUM_INGOT = ITEMS.registerSimpleItem("uranium_ingot");
    public static final DeferredItem<Item> COPPER_DUST = ITEMS.registerSimpleItem("copper_dust");
    public static final DeferredItem<Item> IRON_DUST = ITEMS.registerSimpleItem("iron_dust");
    public static final DeferredItem<Item> GOLD_DUST = ITEMS.registerSimpleItem("gold_dust");
    public static final DeferredItem<Item> LEAD_DUST = ITEMS.registerSimpleItem("lead_dust");
    public static final DeferredItem<Item> TIN_DUST = ITEMS.registerSimpleItem("tin_dust");
    public static final DeferredItem<Item> BRONZE_DUST = ITEMS.registerSimpleItem("bronze_dust");
    public static final DeferredItem<Item> COPPER_PLATE = ITEMS.registerSimpleItem("copper_plate");
    public static final DeferredItem<Item> IRON_PLATE = ITEMS.registerSimpleItem("iron_plate");
    public static final DeferredItem<Item> GOLD_PLATE = ITEMS.registerSimpleItem("gold_plate");
    public static final DeferredItem<Item> LEAD_PLATE = ITEMS.registerSimpleItem("lead_plate");
    public static final DeferredItem<Item> TIN_PLATE = ITEMS.registerSimpleItem("tin_plate");
    public static final DeferredItem<Item> BRONZE_PLATE = ITEMS.registerSimpleItem("bronze_plate");
    public static final DeferredItem<Item> RE_BATTERY = ITEMS.register(
            "re_battery",
            () -> new BatteryItem(
                    10000,
                    32,
                    true,
                    () -> Component.translatable("item.ic2.re_battery"),
                    new Item.Properties()
            )
    );
    public static final DeferredItem<Item> ADVANCED_RE_BATTERY = ITEMS.register(
            "advanced_re_battery",
            () -> new BatteryItem(
                    100000,
                    128,
                    true,
                    () -> Component.translatable("item.ic2.advanced_re_battery"),
                    new Item.Properties()
            )
    );
    public static final DeferredItem<Item> ENERGY_CRYSTAL = ITEMS.register(
            "energy_crystal",
            () -> new BatteryItem(
                    1000000,
                    256,
                    true,
                    () -> Component.translatable("item.ic2.energy_crystal"),
                    new Item.Properties()
            )
    );
    public static final DeferredItem<Item> LAPOTRON_CRYSTAL = ITEMS.register(
            "lapotron_crystal",
            () -> new BatteryItem(
                    10000000,
                    512,
                    true,
                    () -> Component.translatable("item.ic2.lapotron_crystal"),
                    new Item.Properties()
            )
    );
    public static final DeferredItem<Item> DRILL = ITEMS.register(
            "drill",
            () -> new DrillItem(Tiers.IRON, 10000, 32, 1, 12.0F, new Item.Properties())
    );
    public static final DeferredItem<Item> DIAMOND_DRILL = ITEMS.register(
            "diamond_drill",
            () -> new DrillItem(Tiers.DIAMOND, 30000, 128, 2, 20.0F, new Item.Properties())
    );
    public static final DeferredItem<Item> CHAINSAW = ITEMS.register(
            "chainsaw",
            () -> new ChainsawItem(Tiers.IRON, 10000, 32, 1, 12.0F, new Item.Properties())
    );
    public static final DeferredItem<Item> NANO_SABER = ITEMS.register(
            "nano_saber",
            () -> new NanoSaberItem(Tiers.DIAMOND, new Item.Properties())
    );
    public static final DeferredItem<Item> ELECTRIC_WRENCH = ITEMS.register(
            "electric_wrench",
            () -> new ElectricWrenchItem(12000, 32, 1, new Item.Properties())
    );
    public static final DeferredItem<Item> MINING_LASER = ITEMS.register(
            "mining_laser",
            () -> new MiningLaserItem(30000, 128, 2, 800, 20.0D, new Item.Properties())
    );
    public static final DeferredItem<Item> SCANNER = ITEMS.register(
            "scanner",
            () -> new ScannerItem(12000, 32, 1, 200, 12.0D, false, new Item.Properties())
    );
    public static final DeferredItem<Item> ADVANCED_SCANNER = ITEMS.register(
            "advanced_scanner",
            () -> new ScannerItem(60000, 128, 2, 400, 24.0D, true, new Item.Properties())
    );
    public static final DeferredItem<Item> ELECTRIC_HOE = ITEMS.register(
            "electric_hoe",
            () -> new ElectricHoeItem(Tiers.IRON, 10000, 32, 1, new Item.Properties())
    );
    public static final DeferredItem<Item> ELECTRIC_TREETAP = ITEMS.register(
            "electric_treetap",
            () -> new ElectricTreetapItem(10000, 32, 1, new Item.Properties())
    );
    public static final DeferredItem<Item> IRIDIUM_DRILL = ITEMS.register(
            "iridium_drill",
            () -> new DrillItem(Tiers.NETHERITE, 100000, 512, 3, 28.0F, new Item.Properties())
    );
    public static final DeferredItem<Item> WIND_METER = ITEMS.register(
            "wind_meter",
            () -> new WindMeterItem(new Item.Properties())
    );
    public static final DeferredItem<Item> OBSCURATOR = ITEMS.register(
            "obscurator",
            () -> new ObscuratorItem(new Item.Properties())
    );
    public static final DeferredItem<Item> PLASMA_LAUNCHER = ITEMS.register(
            "plasma_launcher",
            () -> new PlasmaLauncherItem(60000, 256, 3, 2000, new Item.Properties())
    );
    public static final DeferredItem<Item> NANO_HELMET = ITEMS.register(
            "nano_helmet",
            () -> new NanoArmorItem(IC2ArmorMaterials.NANO, net.minecraft.world.item.ArmorItem.Type.HELMET, 120000, 128, 2, new Item.Properties())
    );
    public static final DeferredItem<Item> NANO_CHESTPLATE = ITEMS.register(
            "nano_chestplate",
            () -> new NanoArmorItem(IC2ArmorMaterials.NANO, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, 160000, 128, 2, new Item.Properties())
    );
    public static final DeferredItem<Item> NANO_LEGGINGS = ITEMS.register(
            "nano_leggings",
            () -> new NanoArmorItem(IC2ArmorMaterials.NANO, net.minecraft.world.item.ArmorItem.Type.LEGGINGS, 140000, 128, 2, new Item.Properties())
    );
    public static final DeferredItem<Item> NANO_BOOTS = ITEMS.register(
            "nano_boots",
            () -> new NanoArmorItem(IC2ArmorMaterials.NANO, net.minecraft.world.item.ArmorItem.Type.BOOTS, 120000, 128, 2, new Item.Properties())
    );
    public static final DeferredItem<Item> BATPACK = ITEMS.register(
            "batpack",
            () -> new EnergyPackArmorItem(IC2ArmorMaterials.BATPACK, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, 60000, 32, 1, new Item.Properties())
    );
    public static final DeferredItem<Item> LAPPACK = ITEMS.register(
            "lappack",
            () -> new EnergyPackArmorItem(IC2ArmorMaterials.LAPPACK, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, 300000, 128, 2, new Item.Properties())
    );
    public static final DeferredItem<Item> ADVANCED_BATPACK = ITEMS.register(
            "advanced_batpack",
            () -> new EnergyPackArmorItem(IC2ArmorMaterials.ADVANCED_BATPACK, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, 120000, 128, 2, new Item.Properties())
    );
    public static final DeferredItem<Item> ENERGY_PACK = ITEMS.register(
            "energy_pack",
            () -> new EnergyPackArmorItem(IC2ArmorMaterials.ENERGY_PACK, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, 600000, 256, 3, new Item.Properties())
    );
    public static final DeferredItem<Item> QUANTUM_HELMET = ITEMS.register(
            "quantum_helmet",
            () -> new NanoArmorItem(IC2ArmorMaterials.QUANTUM, net.minecraft.world.item.ArmorItem.Type.HELMET, 1000000, 512, 3, new Item.Properties())
    );
    public static final DeferredItem<Item> QUANTUM_CHESTPLATE = ITEMS.register(
            "quantum_chestplate",
            () -> new NanoArmorItem(IC2ArmorMaterials.QUANTUM, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, 1000000, 512, 3, new Item.Properties())
    );
    public static final DeferredItem<Item> QUANTUM_LEGGINGS = ITEMS.register(
            "quantum_leggings",
            () -> new NanoArmorItem(IC2ArmorMaterials.QUANTUM, net.minecraft.world.item.ArmorItem.Type.LEGGINGS, 1000000, 512, 3, new Item.Properties())
    );
    public static final DeferredItem<Item> QUANTUM_BOOTS = ITEMS.register(
            "quantum_boots",
            () -> new NanoArmorItem(IC2ArmorMaterials.QUANTUM, net.minecraft.world.item.ArmorItem.Type.BOOTS, 1000000, 512, 3, new Item.Properties())
    );
    public static final DeferredItem<Item> OVERCLOCKER_UPGRADE = ITEMS.register(
            "overclocker_upgrade",
            () -> new MachineUpgradeItem(UpgradeType.OVERCLOCKER, new Item.Properties().stacksTo(1))
    );
    public static final DeferredItem<Item> TRANSFORMER_UPGRADE = ITEMS.register(
            "transformer_upgrade",
            () -> new MachineUpgradeItem(UpgradeType.TRANSFORMER, new Item.Properties().stacksTo(1))
    );
    public static final DeferredItem<Item> ENERGY_STORAGE_UPGRADE = ITEMS.register(
            "energy_storage_upgrade",
            () -> new MachineUpgradeItem(UpgradeType.ENERGY_STORAGE, new Item.Properties().stacksTo(1))
    );
    public static final DeferredItem<Item> REDSTONE_INVERTER_UPGRADE = ITEMS.register(
            "redstone_inverter_upgrade",
            () -> new MachineUpgradeItem(UpgradeType.REDSTONE_INVERTER, new Item.Properties().stacksTo(1))
    );

    private IC2Items() {
    }
}
