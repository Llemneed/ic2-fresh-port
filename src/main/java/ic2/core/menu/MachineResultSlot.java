package ic2.core.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public final class MachineResultSlot extends SlotItemHandler {
    private final Runnable onTakeCallback;

    public MachineResultSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Runnable onTakeCallback) {
        super(itemHandler, index, xPosition, yPosition);
        this.onTakeCallback = onTakeCallback;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        super.onTake(player, stack);
        onTakeCallback.run();
    }
}
