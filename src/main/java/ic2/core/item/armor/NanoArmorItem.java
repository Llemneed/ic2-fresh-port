package ic2.core.item.armor;

import ic2.core.item.electric.ElectricItem;
import ic2.core.item.electric.ElectricItemManager;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public final class NanoArmorItem extends ArmorItem implements ElectricItem {
    private final int maxCharge;
    private final int transferLimit;
    private final int energyTier;

    public NanoArmorItem(
            Holder<ArmorMaterial> material,
            ArmorItem.Type type,
            int maxCharge,
            int transferLimit,
            int energyTier,
            Item.Properties properties
    ) {
        super(material, type, properties.stacksTo(1));
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
        return ElectricItemManager.getBarWidth(stack, maxCharge);
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
