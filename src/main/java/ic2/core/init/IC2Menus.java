package ic2.core.init;

import ic2.core.IC2;
import ic2.core.menu.BatBoxMenu;
import ic2.core.menu.CompressorMenu;
import ic2.core.menu.ElectricFurnaceMenu;
import ic2.core.menu.ExtractorMenu;
import ic2.core.menu.GeneratorMenu;
import ic2.core.menu.IronFurnaceMenu;
import ic2.core.menu.MaceratorMenu;
import ic2.core.menu.MetalFormerMenu;
import ic2.core.menu.RecyclerMenu;
import ic2.core.menu.SolidCannerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class IC2Menus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, IC2.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<MaceratorMenu>> MACERATOR = MENUS.register(
            "macerator",
            () -> IMenuTypeExtension.create(MaceratorMenu::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<ExtractorMenu>> EXTRACTOR = MENUS.register(
            "extractor",
            () -> IMenuTypeExtension.create(ExtractorMenu::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<IronFurnaceMenu>> IRON_FURNACE = MENUS.register(
            "iron_furnace",
            () -> IMenuTypeExtension.create(IronFurnaceMenu::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<ElectricFurnaceMenu>> ELECTRIC_FURNACE = MENUS.register(
            "electric_furnace",
            () -> IMenuTypeExtension.create(ElectricFurnaceMenu::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<CompressorMenu>> COMPRESSOR = MENUS.register(
            "compressor",
            () -> IMenuTypeExtension.create(CompressorMenu::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<MetalFormerMenu>> METAL_FORMER = MENUS.register(
            "metal_former",
            () -> IMenuTypeExtension.create(MetalFormerMenu::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<RecyclerMenu>> RECYCLER = MENUS.register(
            "recycler",
            () -> IMenuTypeExtension.create(RecyclerMenu::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<SolidCannerMenu>> SOLID_CANNER = MENUS.register(
            "solid_canner",
            () -> IMenuTypeExtension.create(SolidCannerMenu::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<GeneratorMenu>> GENERATOR = MENUS.register(
            "generator",
            () -> IMenuTypeExtension.create(GeneratorMenu::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<BatBoxMenu>> BATBOX = MENUS.register(
            "batbox",
            () -> IMenuTypeExtension.create(BatBoxMenu::new)
    );

    private IC2Menus() {
    }
}
