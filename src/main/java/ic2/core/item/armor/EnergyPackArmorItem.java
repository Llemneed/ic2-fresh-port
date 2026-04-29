package ic2.core.item.armor;

import ic2.core.item.electric.ElectricItem;
import ic2.core.item.electric.ElectricItemManager;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public final class EnergyPackArmorItem extends ArmorItem implements ElectricItem {
    private final int maxCharge;
    private final int transferLimit;
    private final int energyTier;

    public EnergyPackArmorItem(
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
    public boolean canProvideEnergy() {
        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (level.isClientSide || !(entity instanceof Player player)) {
            return;
        }

        if (player.getItemBySlot(EquipmentSlot.CHEST) != stack) {
            return;
        }

        chargeInventory(stack, player);
    }

    private void chargeInventory(ItemStack source, Player player) {
        int budget = Math.min(getTransferLimit(), ElectricItemManager.getCharge(source));
        if (budget <= 0) {
            return;
        }

        for (ItemStack target : player.getInventory().items) {
            if (budget <= 0) {
                break;
            }
            budget = transferEnergy(source, target, budget);
        }

        for (ItemStack target : player.getInventory().offhand) {
            if (budget <= 0) {
                break;
            }
            budget = transferEnergy(source, target, budget);
        }
    }

    private int transferEnergy(ItemStack source, ItemStack target, int budget) {
        if (target.isEmpty() || target == source || !ElectricItemManager.isElectricItem(target)) {
            return budget;
        }

        int extracted = ElectricItemManager.discharge(source, budget, true);
        if (extracted <= 0) {
            return 0;
        }

        int accepted = ElectricItemManager.charge(target, extracted);
        if (accepted < extracted) {
            ElectricItemManager.charge(source, extracted - accepted);
        }

        return budget - accepted;
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
