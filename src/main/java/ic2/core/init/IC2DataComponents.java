package ic2.core.init;

import com.mojang.serialization.Codec;
import ic2.core.IC2;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class IC2DataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, IC2.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY_STORED =
            DATA_COMPONENTS.registerComponentType(
                    "energy_stored",
                    builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT)
            );

    private IC2DataComponents() {
    }
}
