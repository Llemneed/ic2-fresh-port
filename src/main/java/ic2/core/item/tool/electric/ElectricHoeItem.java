package ic2.core.item.tool.electric;

import ic2.core.item.electric.ElectricItem;
import ic2.core.item.electric.ElectricItemManager;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

public final class ElectricHoeItem extends HoeItem implements ElectricItem {
    private static final int ENERGY_PER_USE = 50;

    private final int maxCharge;
    private final int transferLimit;
    private final int tier;

    public ElectricHoeItem(Tier toolTier, int maxCharge, int transferLimit, int tier, Properties properties) {
        super(toolTier, properties.stacksTo(1));
        this.maxCharge = maxCharge;
        this.transferLimit = transferLimit;
        this.tier = tier;
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
        return tier;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult result = super.useOn(context);
        if (result.consumesAction() && !context.getLevel().isClientSide()) {
            ElectricItemManager.discharge(context.getItemInHand(), ENERGY_PER_USE, false);
        }

        return result;
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.literal(ElectricItemManager.getCharge(stack) + " / " + maxCharge + " EU"));
        tooltipComponents.add(Component.literal("Transfer: " + transferLimit + " EU/t"));
    }
}
