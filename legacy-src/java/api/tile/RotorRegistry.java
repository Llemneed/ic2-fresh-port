// 
// Decompiled by Procyon v0.5.30
// 

package ic2.api.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.ModContainer;
import ic2.api.info.Info;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RotorRegistry
{
    private static IRotorRegistry INSTANCE;
    
    public static <T extends TileEntity> void registerRotorProvider(final Class<T> clazz) {
        if (RotorRegistry.INSTANCE != null) {
            RotorRegistry.INSTANCE.registerRotorProvider(clazz);
        }
    }
    
    public static void setInstance(final IRotorRegistry i) {
        final ModContainer mc = Loader.instance().activeModContainer();
        if (mc == null || !Info.MOD_ID.equals(mc.getModId())) {
            throw new IllegalAccessError("Only IC2 can set the instance");
        }
        RotorRegistry.INSTANCE = i;
    }
    
    public interface IRotorRegistry
    {
         <T extends net.minecraft.tileentity.TileEntity> void registerRotorProvider(final Class<T> p0);
    }
}
