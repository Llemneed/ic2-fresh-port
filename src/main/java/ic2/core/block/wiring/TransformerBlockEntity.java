package ic2.core.block.wiring;

import ic2.core.energy.EnergyConsumer;
import ic2.core.energy.EnergyNetHelper;
import ic2.core.energy.EnergyTier;
import ic2.core.init.IC2BlockEntities;
import ic2.core.init.IC2Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public final class TransformerBlockEntity extends BlockEntity implements EnergyConsumer {
    private final TransformerType transformerType;
    private final int maxEnergy;
    private int energyStored;

    public TransformerBlockEntity(BlockPos pos, BlockState state) {
        this(resolveType(state), pos, state, resolveTransformerType(state));
    }

    private TransformerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, TransformerType transformerType) {
        super(type, pos, state);
        this.transformerType = transformerType;
        this.maxEnergy = transformerType.highTier().packetSize() * 8;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TransformerBlockEntity blockEntity) {
        blockEntity.pushEnergy();
        boolean active = blockEntity.energyStored > 0;
        if (state.getValue(TransformerBlock.ACTIVE) != active) {
            level.setBlock(pos, state.setValue(TransformerBlock.ACTIVE, active), 3);
        }
        setChanged(level, pos, state);
    }

    public boolean isStepUp() {
        return getBlockState().getValue(TransformerBlock.STEP_UP);
    }

    public EnergyTier getInputTier() {
        return isStepUp() ? transformerType.lowTier() : transformerType.highTier();
    }

    public EnergyTier getOutputTier() {
        return isStepUp() ? transformerType.highTier() : transformerType.lowTier();
    }

    @Override
    public int receiveEnergy(int amount) {
        int accepted = Math.min(Math.min(amount, maxInputPerTick()), maxEnergy - energyStored);
        energyStored += accepted;
        if (accepted > 0) {
            setChanged();
        }
        return accepted;
    }

    @Override
    public boolean canReceiveEnergy() {
        return energyStored < maxEnergy;
    }

    @Override
    public int maxInputPerTick() {
        return getInputTier().packetSize();
    }

    @Override
    public EnergyTier getSinkTier() {
        return getInputTier();
    }

    @Override
    public void onOvervoltage(int amount) {
        if (level == null || level.isClientSide) {
            return;
        }

        BlockPos pos = getBlockPos();
        level.removeBlock(pos, false);
        level.explode(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 2.5F, Level.ExplosionInteraction.BLOCK);
    }

    private void pushEnergy() {
        if (level == null || energyStored <= 0) {
            return;
        }

        Direction outputSide = getBlockState().getValue(TransformerBlock.FACING);
        int packet = Math.min(getOutputTier().packetSize(), energyStored);
        int sent = EnergyNetHelper.sendEnergy(level, worldPosition, outputSide, packet, getOutputTier());
        energyStored -= sent;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("energyStored", energyStored);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energyStored = tag.getInt("energyStored");
    }

    public static BlockEntityType<TransformerBlockEntity> resolveType(BlockState state) {
        if (state.is(IC2Blocks.LV_TRANSFORMER.get())) {
            return IC2BlockEntities.LV_TRANSFORMER.get();
        }
        if (state.is(IC2Blocks.MV_TRANSFORMER.get())) {
            return IC2BlockEntities.MV_TRANSFORMER.get();
        }
        if (state.is(IC2Blocks.HV_TRANSFORMER.get())) {
            return IC2BlockEntities.HV_TRANSFORMER.get();
        }
        return IC2BlockEntities.EV_TRANSFORMER.get();
    }

    private static TransformerType resolveTransformerType(BlockState state) {
        if (state.is(IC2Blocks.LV_TRANSFORMER.get())) {
            return TransformerType.LV;
        }
        if (state.is(IC2Blocks.MV_TRANSFORMER.get())) {
            return TransformerType.MV;
        }
        if (state.is(IC2Blocks.HV_TRANSFORMER.get())) {
            return TransformerType.HV;
        }
        return TransformerType.EV;
    }
}
