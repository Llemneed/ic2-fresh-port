package ic2.core.block.generator;

import com.mojang.serialization.MapCodec;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public abstract class PassiveGeneratorBlock<T extends BasePassiveGeneratorBlockEntity> extends BaseEntityBlock implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private final Supplier<BlockEntityType<T>> blockEntityTypeSupplier;
    private final BiFunction<BlockPos, BlockState, T> blockEntityFactory;

    protected PassiveGeneratorBlock(
            BlockBehaviour.Properties properties,
            Supplier<BlockEntityType<T>> blockEntityTypeSupplier,
            BiFunction<BlockPos, BlockState, T> blockEntityFactory
    ) {
        super(properties);
        this.blockEntityTypeSupplier = blockEntityTypeSupplier;
        this.blockEntityFactory = blockEntityFactory;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected abstract MapCodec<? extends BaseEntityBlock> codec();

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BasePassiveGeneratorBlockEntity generator) {
                generator.dropContents();
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityFactory.apply(pos, state);
    }

    @Override
    public <K extends BlockEntity> BlockEntityTicker<K> getTicker(Level level, BlockState state, BlockEntityType<K> blockEntityType) {
        return null;
    }

    protected Supplier<BlockEntityType<T>> getBlockEntityTypeSupplier() {
        return blockEntityTypeSupplier;
    }
}
