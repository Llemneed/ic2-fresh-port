package ic2.core.init;

import ic2.core.IC2;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class IC2CreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, IC2.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register(
            "main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.ic2.main"))
                    .icon(() -> new ItemStack(IC2Items.STICKY_RESIN.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(IC2Blocks.LEAD_ORE);
                        output.accept(IC2Blocks.TIN_ORE);
                        output.accept(IC2Blocks.URANIUM_ORE);
                        output.accept(IC2Blocks.MACERATOR);
                        output.accept(IC2Blocks.EXTRACTOR);
                        output.accept(IC2Blocks.IRON_FURNACE);
                        output.accept(IC2Blocks.ELECTRIC_FURNACE);
                        output.accept(IC2Blocks.COMPRESSOR);
                        output.accept(IC2Blocks.METAL_FORMER);
                        output.accept(IC2Blocks.RECYCLER);
                        output.accept(IC2Blocks.SOLID_CANNER);
                        output.accept(IC2Blocks.GENERATOR);
                        output.accept(IC2Blocks.GEO_GENERATOR);
                        output.accept(IC2Blocks.STIRLING_GENERATOR);
                        output.accept(IC2Blocks.SOLAR_GENERATOR);
                        output.accept(IC2Blocks.WATER_GENERATOR);
                        output.accept(IC2Blocks.WIND_GENERATOR);
                        output.accept(IC2Blocks.BATBOX);
                        output.accept(IC2Blocks.CESU);
                        output.accept(IC2Blocks.MFE);
                        output.accept(IC2Blocks.MFSU);
                        output.accept(IC2Blocks.CHARGEPAD_BATBOX);
                        output.accept(IC2Blocks.CHARGEPAD_CESU);
                        output.accept(IC2Blocks.CHARGEPAD_MFE);
                        output.accept(IC2Blocks.CHARGEPAD_MFSU);
                        output.accept(IC2Blocks.RUBBER_WOOD);
                        output.accept(IC2Blocks.RUBBER_LEAVES);
                        output.accept(IC2Blocks.RUBBER_SAPLING);
                        output.accept(IC2Items.TREETAP);
                        output.accept(IC2Items.STICKY_RESIN);
                        output.accept(IC2Items.RUBBER);
                        output.accept(IC2Items.SCRAP);
                        output.accept(IC2Items.SCRAP_BOX);
                        output.accept(IC2Items.MACHINE_CASING);
                        output.accept(IC2Items.ALLOY);
                        output.accept(IC2Items.CIRCUIT);
                        output.accept(IC2Items.ADVANCED_CIRCUIT);
                        output.accept(IC2Items.COAL_BALL);
                        output.accept(IC2Items.COAL_CHUNK);
                        output.accept(IC2Items.CARBON_FIBRE);
                        output.accept(IC2Items.CARBON_MESH);
                        output.accept(IC2Items.CARBON_PLATE);
                        output.accept(IC2Items.COIL);
                        output.accept(IC2Items.ELECTRIC_MOTOR);
                        output.accept(IC2Items.SMALL_POWER_UNIT);
                        output.accept(IC2Items.POWER_UNIT);
                        output.accept(IC2Items.HEAT_CONDUCTOR);
                        output.accept(IC2Items.INDUSTRIAL_DIAMOND);
                        output.accept(IC2Items.PLANT_BALL);
                        output.accept(IC2Items.ADVANCED_MACHINE);
                        output.accept(IC2Items.TIN_CAN);
                        output.accept(IC2Items.FILLED_TIN_CAN);
                        output.accept(IC2Items.LEAD_INGOT);
                        output.accept(IC2Items.TIN_INGOT);
                        output.accept(IC2Items.BRONZE_INGOT);
                        output.accept(IC2Items.URANIUM_INGOT);
                        output.accept(IC2Items.COPPER_DUST);
                        output.accept(IC2Items.IRON_DUST);
                        output.accept(IC2Items.GOLD_DUST);
                        output.accept(IC2Items.LEAD_DUST);
                        output.accept(IC2Items.TIN_DUST);
                        output.accept(IC2Items.BRONZE_DUST);
                        output.accept(IC2Items.COPPER_PLATE);
                        output.accept(IC2Items.IRON_PLATE);
                        output.accept(IC2Items.GOLD_PLATE);
                        output.accept(IC2Items.LEAD_PLATE);
                        output.accept(IC2Items.TIN_PLATE);
                        output.accept(IC2Items.BRONZE_PLATE);
                        output.accept(IC2Items.RE_BATTERY);
                        output.accept(IC2Items.ADVANCED_RE_BATTERY);
                        output.accept(IC2Items.ENERGY_CRYSTAL);
                        output.accept(IC2Items.LAPOTRON_CRYSTAL);
                        output.accept(IC2Items.DRILL);
                        output.accept(IC2Items.DIAMOND_DRILL);
                        output.accept(IC2Items.CHAINSAW);
                        output.accept(IC2Items.NANO_SABER);
                        output.accept(IC2Items.ELECTRIC_WRENCH);
                        output.accept(IC2Items.MINING_LASER);
                        output.accept(IC2Items.SCANNER);
                        output.accept(IC2Items.ADVANCED_SCANNER);
                        output.accept(IC2Items.ELECTRIC_HOE);
                        output.accept(IC2Items.ELECTRIC_TREETAP);
                        output.accept(IC2Items.IRIDIUM_DRILL);
                        output.accept(IC2Items.WIND_METER);
                        output.accept(IC2Items.OBSCURATOR);
                        output.accept(IC2Items.PLASMA_LAUNCHER);
                        output.accept(IC2Items.NANO_HELMET);
                        output.accept(IC2Items.NANO_CHESTPLATE);
                        output.accept(IC2Items.NANO_LEGGINGS);
                        output.accept(IC2Items.NANO_BOOTS);
                        output.accept(IC2Items.BATPACK);
                        output.accept(IC2Items.LAPPACK);
                        output.accept(IC2Items.ADVANCED_BATPACK);
                        output.accept(IC2Items.ENERGY_PACK);
                        output.accept(IC2Items.QUANTUM_HELMET);
                        output.accept(IC2Items.QUANTUM_CHESTPLATE);
                        output.accept(IC2Items.QUANTUM_LEGGINGS);
                        output.accept(IC2Items.QUANTUM_BOOTS);
                        output.accept(IC2Items.OVERCLOCKER_UPGRADE);
                        output.accept(IC2Items.TRANSFORMER_UPGRADE);
                        output.accept(IC2Items.ENERGY_STORAGE_UPGRADE);
                        output.accept(IC2Items.REDSTONE_INVERTER_UPGRADE);
                    })
                    .build()
    );

    private IC2CreativeTabs() {
    }
}
