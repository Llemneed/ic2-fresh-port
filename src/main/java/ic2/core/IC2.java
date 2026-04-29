package ic2.core;

import com.mojang.logging.LogUtils;
import ic2.core.init.IC2Blocks;
import ic2.core.init.IC2CreativeTabs;
import ic2.core.init.IC2BlockEntities;
import ic2.core.init.IC2ArmorMaterials;
import ic2.core.init.IC2Items;
import ic2.core.init.IC2Menus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(IC2.MODID)
public final class IC2 {
    public static final String MODID = "ic2";
    public static final Logger LOGGER = LogUtils.getLogger();

    public IC2(IEventBus modEventBus) {
        IC2Blocks.BLOCKS.register(modEventBus);
        IC2BlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        IC2ArmorMaterials.ARMOR_MATERIALS.register(modEventBus);
        IC2Items.ITEMS.register(modEventBus);
        IC2Menus.MENUS.register(modEventBus);
        IC2CreativeTabs.TABS.register(modEventBus);

        LOGGER.info("Bootstrapping IndustrialCraft 2 fresh port for Minecraft 1.21.1 / NeoForge 21.1.227");
    }
}
