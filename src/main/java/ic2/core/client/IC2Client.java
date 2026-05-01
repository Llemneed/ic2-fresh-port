package ic2.core.client;

import ic2.core.client.screen.BatBoxScreen;
import ic2.core.client.screen.CompressorScreen;
import ic2.core.client.screen.ElectricFurnaceScreen;
import ic2.core.client.screen.ExtractorScreen;
import ic2.core.client.screen.GeneratorScreen;
import ic2.core.client.screen.IronFurnaceScreen;
import ic2.core.client.screen.MaceratorScreen;
import ic2.core.client.screen.MetalFormerScreen;
import ic2.core.client.screen.RecyclerScreen;
import ic2.core.client.screen.SolidCannerScreen;
import ic2.core.init.IC2Blocks;
import ic2.core.init.IC2Menus;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class IC2Client {
    private IC2Client() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(IC2Client::registerScreens);
        modEventBus.addListener(IC2Client::onClientSetup);
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(IC2Menus.MACERATOR.get(), MaceratorScreen::new);
        event.register(IC2Menus.EXTRACTOR.get(), ExtractorScreen::new);
        event.register(IC2Menus.IRON_FURNACE.get(), IronFurnaceScreen::new);
        event.register(IC2Menus.ELECTRIC_FURNACE.get(), ElectricFurnaceScreen::new);
        event.register(IC2Menus.COMPRESSOR.get(), CompressorScreen::new);
        event.register(IC2Menus.METAL_FORMER.get(), MetalFormerScreen::new);
        event.register(IC2Menus.RECYCLER.get(), RecyclerScreen::new);
        event.register(IC2Menus.SOLID_CANNER.get(), SolidCannerScreen::new);
        event.register(IC2Menus.GENERATOR.get(), GeneratorScreen::new);
        event.register(IC2Menus.BATBOX.get(), BatBoxScreen::new);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(IC2Blocks.TIN_CABLE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(IC2Blocks.COPPER_CABLE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(IC2Blocks.GOLD_CABLE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(IC2Blocks.GLASS_FIBRE_CABLE.get(), RenderType.cutout());
        });
    }
}
