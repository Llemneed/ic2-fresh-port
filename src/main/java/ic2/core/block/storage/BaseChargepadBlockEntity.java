package ic2.core.block.storage;

import ic2.core.item.electric.ElectricItemManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public abstract class BaseChargepadBlockEntity extends BaseEnergyStorageBlockEntity {
    private final int playerTransferLimit;

    protected BaseChargepadBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState blockState,
            int maxEnergy,
            int inputPerTick,
            int outputPerTick,
            int playerTransferLimit,
            String displayKey
    ) {
        super(type, pos, blockState, maxEnergy, inputPerTick, outputPerTick, displayKey);
        this.playerTransferLimit = playerTransferLimit;
    }

    @Override
    protected void afterBaseTick() {
        boolean active = chargeStandingPlayers();
        if (level != null && !level.isClientSide && getBlockState().getBlock() instanceof ChargepadBlock<?>) {
            boolean current = getBlockState().getValue(ChargepadBlock.ACTIVE);
            if (current != active) {
                level.setBlock(getBlockPos(), getBlockState().setValue(ChargepadBlock.ACTIVE, active), 3);
            }
        }
    }

    private boolean chargeStandingPlayers() {
        if (level == null || level.isClientSide || energyStored <= 0) {
            return false;
        }

        boolean chargedAny = false;
        AABB aabb = new AABB(worldPosition).inflate(0.05D, 0.0D, 0.05D).move(0.0D, 1.0D, 0.0D);
        for (Player player : level.getEntitiesOfClass(Player.class, aabb)) {
            if (chargePlayer(player)) {
                chargedAny = true;
            }
            if (energyStored <= 0) {
                break;
            }
        }

        return chargedAny;
    }

    private boolean chargePlayer(Player player) {
        int budget = Math.min(playerTransferLimit, energyStored);
        int before = budget;
        for (ItemStack target : player.getInventory().items) {
            if (budget <= 0) {
                break;
            }
            budget = transferEnergy(target, budget);
        }
        for (ItemStack target : player.getInventory().offhand) {
            if (budget <= 0) {
                break;
            }
            budget = transferEnergy(target, budget);
        }
        for (ItemStack target : player.getInventory().armor) {
            if (budget <= 0) {
                break;
            }
            budget = transferEnergy(target, budget);
        }

        int used = before - budget;
        if (used > 0) {
            energyStored -= used;
            setChanged();
            return true;
        }

        return false;
    }

    private int transferEnergy(ItemStack target, int budget) {
        if (target.isEmpty() || !ElectricItemManager.isElectricItem(target)) {
            return budget;
        }

        int accepted = ElectricItemManager.charge(target, budget);
        return budget - accepted;
    }
}
