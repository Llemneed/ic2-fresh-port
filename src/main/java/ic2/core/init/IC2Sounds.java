package ic2.core.init;

import ic2.core.IC2;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class IC2Sounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, IC2.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> GENERATOR_OPERATING = register("generator_operating");
    public static final DeferredHolder<SoundEvent, SoundEvent> GEOTHERMAL_OPERATING = register("geothermal_operating");
    public static final DeferredHolder<SoundEvent, SoundEvent> WATERMILL_OPERATING = register("watermill_operating");
    public static final DeferredHolder<SoundEvent, SoundEvent> WINDMILL_OPERATING = register("windmill_operating");
    public static final DeferredHolder<SoundEvent, SoundEvent> MACERATOR_OPERATING = register("macerator_operating");
    public static final DeferredHolder<SoundEvent, SoundEvent> EXTRACTOR_OPERATING = register("extractor_operating");
    public static final DeferredHolder<SoundEvent, SoundEvent> ELECTRIC_FURNACE_OPERATING = register("electric_furnace_operating");
    public static final DeferredHolder<SoundEvent, SoundEvent> COMPRESSOR_OPERATING = register("compressor_operating");
    public static final DeferredHolder<SoundEvent, SoundEvent> RECYCLER_OPERATING = register("recycler_operating");

    private IC2Sounds() {
    }

    private static DeferredHolder<SoundEvent, SoundEvent> register(String id) {
        return SOUND_EVENTS.register(id, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(IC2.MODID, id)));
    }
}
