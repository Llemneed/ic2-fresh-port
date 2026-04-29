package ic2.core.client;

import ic2.core.IC2;
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
import ic2.core.init.IC2Menus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = IC2.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class IC2Client {
    private IC2Client() {
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
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
}
