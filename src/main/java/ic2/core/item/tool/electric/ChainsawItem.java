package ic2.core.item.tool.electric;

import ic2.core.item.electric.ElectricItem;
import ic2.core.item.electric.ElectricItemManager;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class ChainsawItem extends AxeItem implements ElectricItem {
    private static final int ENERGY_PER_BLOCK = 50;
    private static final int ENERGY_PER_HIT = 100;

    private final int maxCharge;
    private final int transferLimit;
    private final int tier;
    private final float poweredSpeed;

    public ChainsawItem(
            Tier toolTier,
            int maxCharge,
            int transferLimit,
            int tier,
            float poweredSpeed,
            Item.Properties properties
    ) {
        super(toolTier, properties.stacksTo(1));
        this.maxCharge = maxCharge;
        this.transferLimit = transferLimit;
        this.tier = tier;
        this.poweredSpeed = poweredSpeed;
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
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return ElectricItemManager.getCharge(stack) >= ENERGY_PER_BLOCK ? poweredSpeed : 1.0F;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, net.minecraft.core.BlockPos pos, LivingEntity miningEntity) {
        if (!level.isClientSide && !state.isAir()) {
            ElectricItemManager.discharge(stack, ENERGY_PER_BLOCK, false);
        }

        return true;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return ElectricItemManager.getCharge(stack) >= ENERGY_PER_HIT;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        ElectricItemManager.discharge(stack, ENERGY_PER_HIT, false);
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
