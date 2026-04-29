package ic2.core.item.upgrade;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public final class MachineUpgradeItem extends Item {
    private final UpgradeType type;

    public MachineUpgradeItem(UpgradeType type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public UpgradeType getType() {
        return type;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable(type.tooltipKey() + ".1").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable(type.tooltipKey() + ".2").withStyle(ChatFormatting.DARK_GRAY));
    }

    public enum UpgradeType {
        OVERCLOCKER("tooltip.ic2.overclocker_upgrade"),
        TRANSFORMER("tooltip.ic2.transformer_upgrade"),
        ENERGY_STORAGE("tooltip.ic2.energy_storage_upgrade"),
        REDSTONE_INVERTER("tooltip.ic2.redstone_inverter_upgrade");

        private final String tooltipKey;

        UpgradeType(String tooltipKey) {
            this.tooltipKey = tooltipKey;
        }

        public String tooltipKey() {
            return tooltipKey;
        }
    }
}
