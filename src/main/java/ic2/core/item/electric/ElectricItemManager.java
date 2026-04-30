package ic2.core.item.electric;

import ic2.core.init.IC2DataComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class ElectricItemManager {
    private static final String LEGACY_CHARGE_KEY = "Charge";

    private ElectricItemManager() {
    }

    public static boolean isElectricItem(ItemStack stack) {
        return stack.getItem() instanceof ElectricItem;
    }

    public static boolean canProvideEnergy(ItemStack stack) {
        return stack.getItem() instanceof ElectricItem electricItem && electricItem.canProvideEnergy();
    }

    public static boolean canAcceptEnergy(ItemStack stack) {
        return isElectricItem(stack) && getCharge(stack) < getMaxCharge(stack);
    }

    public static boolean hasEnergy(ItemStack stack) {
        return isElectricItem(stack) && getCharge(stack) > 0;
    }

    public static int getCharge(ItemStack stack) {
        if (!(stack.getItem() instanceof ElectricItem electricItem)) {
            return 0;
        }

        Integer stored = stack.get(IC2DataComponents.ENERGY_STORED.get());
        if (stored != null) {
            return clampCharge(stored, electricItem.getMaxCharge());
        }

        return readLegacyCharge(stack, electricItem);
    }

    public static int getMaxCharge(ItemStack stack) {
        if (!(stack.getItem() instanceof ElectricItem electricItem)) {
            return 0;
        }

        return electricItem.getMaxCharge();
    }

    public static int getTransferLimit(ItemStack stack) {
        if (!(stack.getItem() instanceof ElectricItem electricItem)) {
            return 0;
        }

        return electricItem.getTransferLimit();
    }

    public static int getBarWidth(ItemStack stack, int maxCharge) {
        if (maxCharge <= 0) {
            return 0;
        }

        int charge = getCharge(stack);
        if (charge <= 0) {
            return 0;
        }

        if (charge >= maxCharge) {
            return 13;
        }

        return Math.min(13, Math.round(13.0F * charge / (float) maxCharge));
    }

    public static int charge(ItemStack stack, int amount) {
        if (!(stack.getItem() instanceof ElectricItem electricItem) || amount <= 0) {
            return 0;
        }

        int stored = getCharge(stack);
        int accepted = Math.min(amount, Math.min(electricItem.getTransferLimit(), electricItem.getMaxCharge() - stored));
        if (accepted <= 0) {
            return 0;
        }

        setCharge(stack, stored + accepted);
        return accepted;
    }

    public static int discharge(ItemStack stack, int amount, boolean requireProvider) {
        if (!(stack.getItem() instanceof ElectricItem electricItem) || amount <= 0) {
            return 0;
        }

        if (requireProvider && !electricItem.canProvideEnergy()) {
            return 0;
        }

        int stored = getCharge(stack);
        int extracted = Math.min(amount, Math.min(electricItem.getTransferLimit(), stored));
        if (extracted <= 0) {
            return 0;
        }

        setCharge(stack, stored - extracted);
        return extracted;
    }

    public static int transfer(ItemStack source, ItemStack target, int amount, boolean requireProvider) {
        if (amount <= 0 || source.isEmpty() || target.isEmpty() || source == target || !isElectricItem(target)) {
            return 0;
        }

        int extracted = discharge(source, amount, requireProvider);
        if (extracted <= 0) {
            return 0;
        }

        int accepted = charge(target, extracted);
        if (accepted < extracted) {
            charge(source, extracted - accepted);
        }

        return accepted;
    }

    public static void setCharge(ItemStack stack, int charge) {
        if (!(stack.getItem() instanceof ElectricItem electricItem)) {
            return;
        }

        int clampedCharge = clampCharge(charge, electricItem.getMaxCharge());
        stack.set(IC2DataComponents.ENERGY_STORED.get(), clampedCharge);
        clearLegacyCharge(stack);
    }

    private static int readLegacyCharge(ItemStack stack, ElectricItem electricItem) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        int legacyCharge = clampCharge(customData.copyTag().getInt(LEGACY_CHARGE_KEY), electricItem.getMaxCharge());
        if (legacyCharge > 0) {
            // TODO(milestone-3): Remove legacy CUSTOM_DATA charge migration after old stacks are no longer relevant.
            stack.set(IC2DataComponents.ENERGY_STORED.get(), legacyCharge);
            clearLegacyCharge(stack);
        }
        return legacyCharge;
    }

    private static void clearLegacyCharge(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        if (!tag.contains(LEGACY_CHARGE_KEY)) {
            return;
        }

        tag.remove(LEGACY_CHARGE_KEY);
        if (tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    private static int clampCharge(int charge, int maxCharge) {
        return Math.max(0, Math.min(charge, maxCharge));
    }
}
