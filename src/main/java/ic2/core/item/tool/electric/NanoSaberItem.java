package ic2.core.item.tool.electric;

import ic2.core.item.electric.ElectricItem;
import ic2.core.item.electric.ElectricItemManager;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public final class NanoSaberItem extends SwordItem implements ElectricItem {
    private static final String ACTIVE_KEY = "Active";
    private static final int ENERGY_PER_HIT = 400;
    private static final int BASE_MAX_CHARGE = 160000;
    private static final int BASE_TRANSFER = 512;

    public NanoSaberItem(Tier toolTier, Item.Properties properties) {
        super(toolTier, properties.stacksTo(1));
    }

    @Override
    public int getMaxCharge() {
        return BASE_MAX_CHARGE;
    }

    @Override
    public int getTransferLimit() {
        return BASE_TRANSFER;
    }

    @Override
    public int getEnergyTier() {
        return 3;
    }

    public static boolean isActive(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return customData.copyTag().getBoolean(ACTIVE_KEY);
    }

    private static void setActive(ItemStack stack, boolean active) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putBoolean(ACTIVE_KEY, active);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide) {
            boolean nextState = !isActive(stack);
            if (nextState && ElectricItemManager.getCharge(stack) < ENERGY_PER_HIT) {
                nextState = false;
            }

            setActive(stack, nextState);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (isActive(stack) && ElectricItemManager.discharge(stack, ENERGY_PER_HIT, false) > 0) {
            target.hurt(attacker.damageSources().mobAttack(attacker), 12.0F);
        } else if (isActive(stack)) {
            setActive(stack, false);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!level.isClientSide && isActive(stack) && ElectricItemManager.getCharge(stack) < ENERGY_PER_HIT) {
            setActive(stack, false);
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return ElectricItemManager.getBarWidth(stack, getMaxCharge());
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float ratio = ElectricItemManager.getCharge(stack) / (float) getMaxCharge();
        return Mth.hsvToRgb(Math.max(0.0F, ratio) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.literal(ElectricItemManager.getCharge(stack) + " / " + getMaxCharge() + " EU"));
        tooltipComponents.add(Component.literal("Transfer: " + getTransferLimit() + " EU/t"));
        tooltipComponents.add(Component.literal("State: " + (isActive(stack) ? "Active" : "Idle")));
    }
}
