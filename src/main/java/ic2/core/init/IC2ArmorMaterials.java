package ic2.core.init;

import ic2.core.IC2;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class IC2ArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, IC2.MODID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> NANO = ARMOR_MATERIALS.register(
            "nano",
            () -> new ArmorMaterial(
                    createDefenseMap(3, 8, 6, 3),
                    18,
                    SoundEvents.ARMOR_EQUIP_DIAMOND,
                    () -> Ingredient.of(IC2Items.RE_BATTERY.get()),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(IC2.MODID, "nano"))),
                    2.0F,
                    0.1F
            )
    );
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> BATPACK = ARMOR_MATERIALS.register(
            "batpack",
            () -> new ArmorMaterial(
                    createDefenseMap(0, 1, 0, 0),
                    8,
                    SoundEvents.ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.of(IC2Items.RE_BATTERY.get()),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(IC2.MODID, "batpack"))),
                    0.0F,
                    0.0F
            )
    );
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> LAPPACK = ARMOR_MATERIALS.register(
            "lappack",
            () -> new ArmorMaterial(
                    createDefenseMap(0, 2, 0, 0),
                    10,
                    SoundEvents.ARMOR_EQUIP_CHAIN,
                    () -> Ingredient.of(IC2Items.RE_BATTERY.get()),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(IC2.MODID, "lappack"))),
                    0.0F,
                    0.0F
            )
    );
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ADVANCED_BATPACK = ARMOR_MATERIALS.register(
            "advanced_batpack",
            () -> new ArmorMaterial(
                    createDefenseMap(0, 2, 0, 0),
                    10,
                    SoundEvents.ARMOR_EQUIP_CHAIN,
                    () -> Ingredient.of(IC2Items.RE_BATTERY.get()),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(IC2.MODID, "advanced_batpack"))),
                    0.0F,
                    0.0F
            )
    );
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ENERGY_PACK = ARMOR_MATERIALS.register(
            "energy_pack",
            () -> new ArmorMaterial(
                    createDefenseMap(0, 3, 0, 0),
                    12,
                    SoundEvents.ARMOR_EQUIP_IRON,
                    () -> Ingredient.of(IC2Items.RE_BATTERY.get()),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(IC2.MODID, "energy_pack"))),
                    0.0F,
                    0.0F
            )
    );
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> QUANTUM = ARMOR_MATERIALS.register(
            "quantum",
            () -> new ArmorMaterial(
                    createDefenseMap(4, 10, 8, 4),
                    22,
                    SoundEvents.ARMOR_EQUIP_NETHERITE,
                    () -> Ingredient.of(IC2Items.NANO_CHESTPLATE.get()),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(IC2.MODID, "quantum"))),
                    3.0F,
                    0.2F
            )
    );

    private IC2ArmorMaterials() {
    }

    private static Map<ArmorItem.Type, Integer> createDefenseMap(int boots, int chestplate, int leggings, int helmet) {
        Map<ArmorItem.Type, Integer> map = new EnumMap<>(ArmorItem.Type.class);
        map.put(ArmorItem.Type.BOOTS, boots);
        map.put(ArmorItem.Type.CHESTPLATE, chestplate);
        map.put(ArmorItem.Type.LEGGINGS, leggings);
        map.put(ArmorItem.Type.HELMET, helmet);
        map.put(ArmorItem.Type.BODY, chestplate);
        return map;
    }
}
