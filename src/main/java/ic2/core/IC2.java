package ic2.core;

import com.mojang.logging.LogUtils;
import ic2.core.init.IC2Blocks;
import ic2.core.init.IC2CreativeTabs;
import ic2.core.init.IC2BlockEntities;
import ic2.core.init.IC2ArmorMaterials;
import ic2.core.init.IC2Items;
import ic2.core.init.IC2Menus;
import ic2.core.init.IC2Sounds;
import ic2.core.recipe.IC2RecipeSerializers;
import ic2.core.recipe.IC2RecipeTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
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
        IC2Sounds.SOUND_EVENTS.register(modEventBus);
        IC2RecipeTypes.RECIPE_TYPES.register(modEventBus);
        IC2RecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        IC2CreativeTabs.TABS.register(modEventBus);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ic2.core.client.IC2Client.register(modEventBus);
        }

        LOGGER.info("Bootstrapping IndustrialCraft 2 fresh port for Minecraft 1.21.1 / NeoForge 21.1.227");
    }
}
