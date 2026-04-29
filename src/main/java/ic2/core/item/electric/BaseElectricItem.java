package ic2.core.item.electric;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class BaseElectricItem extends Item implements ElectricItem {
    private final int maxCharge;
    private final int transferLimit;
    private final int energyTier;

    public BaseElectricItem(int maxCharge, int transferLimit, int energyTier, Properties properties) {
        super(properties.stacksTo(1));
        this.maxCharge = maxCharge;
        this.transferLimit = transferLimit;
        this.energyTier = energyTier;
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
    public int getEnergyTier() {
        return energyTier;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (maxCharge <= 0) {
            return 0;
        }

        return Math.max(1, Math.round(13.0F * ElectricItemManager.getCharge(stack) / (float) maxCharge));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float ratio = maxCharge <= 0 ? 0.0F : ElectricItemManager.getCharge(stack) / (float) maxCharge;
        return Mth.hsvToRgb(Math.max(0.0F, ratio) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.literal(ElectricItemManager.getCharge(stack) + " / " + maxCharge + " EU"));
        tooltipComponents.add(Component.literal("Transfer: " + transferLimit + " EU/t"));
    }
}
