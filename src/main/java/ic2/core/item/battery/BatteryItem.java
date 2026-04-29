package ic2.core.item.battery;

import ic2.core.item.electric.ElectricItem;
import ic2.core.item.electric.ElectricItemManager;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public final class BatteryItem extends Item implements ElectricItem {
    private final int maxCharge;
    private final int transferLimit;
    private final boolean canProvideEnergy;
    private final Supplier<Component> displayName;

    public BatteryItem(
            int maxCharge,
            int transferLimit,
            boolean canProvideEnergy,
            Supplier<Component> displayName,
            Properties properties
    ) {
        super(properties.stacksTo(1));
        this.maxCharge = maxCharge;
        this.transferLimit = transferLimit;
        this.canProvideEnergy = canProvideEnergy;
        this.displayName = displayName;
    }

    @Override
    public int getMaxCharge() {
        return maxCharge;
    }

    @Override
    public int getTransferLimit() {
        return transferLimit;
    }

    @Override
    public boolean canProvideEnergy() {
        return canProvideEnergy;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int max = getMaxCharge();
        if (max <= 0) {
            return 0;
        }

        return Math.max(1, Math.round(13.0F * ElectricItemManager.getCharge(stack) / (float) max));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float ratio = ElectricItemManager.getMaxCharge(stack) <= 0
                ? 0.0F
                : ElectricItemManager.getCharge(stack) / (float) ElectricItemManager.getMaxCharge(stack);
        return net.minecraft.util.Mth.hsvToRgb(Math.max(0.0F, ratio) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, java.util.List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.literal(
                ElectricItemManager.getCharge(stack) + " / " + maxCharge + " EU"
        ));
        tooltipComponents.add(Component.literal("Transfer: " + transferLimit + " EU/t"));
    }

    @Override
    public Component getName(ItemStack stack) {
        return displayName.get();
    }
}
