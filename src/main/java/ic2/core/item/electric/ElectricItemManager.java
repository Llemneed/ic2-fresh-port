package ic2.core.item.electric;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class ElectricItemManager {
    private static final String CHARGE_KEY = "Charge";

    private ElectricItemManager() {
    }

    public static boolean isElectricItem(ItemStack stack) {
        return stack.getItem() instanceof ElectricItem;
    }

    public static boolean canProvideEnergy(ItemStack stack) {
        return stack.getItem() instanceof ElectricItem electricItem && electricItem.canProvideEnergy();
    }

    public static int getCharge(ItemStack stack) {
        if (!(stack.getItem() instanceof ElectricItem)) {
            return 0;
        }

        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return Math.max(0, customData.copyTag().getInt(CHARGE_KEY));
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

    public static void setCharge(ItemStack stack, int charge) {
        if (!(stack.getItem() instanceof ElectricItem electricItem)) {
            return;
        }

        int clampedCharge = Math.max(0, Math.min(charge, electricItem.getMaxCharge()));
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putInt(CHARGE_KEY, clampedCharge);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}
