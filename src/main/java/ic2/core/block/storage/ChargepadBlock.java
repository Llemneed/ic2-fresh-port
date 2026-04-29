package ic2.core.block.storage;

import com.mojang.serialization.MapCodec;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntityType;

public abstract class ChargepadBlock<T extends BaseChargepadBlockEntity> extends EnergyStorageBlock<T> {
    public static final BooleanProperty ACTIVE = BlockStateProperties.LIT;

    protected ChargepadBlock(
            BlockBehaviour.Properties properties,
            Supplier<BlockEntityType<T>> blockEntityTypeSupplier,
            BiFunction<BlockPos, BlockState, T> blockEntityFactory
    ) {
        super(properties, blockEntityTypeSupplier, blockEntityFactory);
        registerDefaultState(defaultBlockState().setValue(FACING, net.minecraft.core.Direction.NORTH).setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Override
    protected abstract MapCodec<? extends BaseEntityBlock> codec();
}
