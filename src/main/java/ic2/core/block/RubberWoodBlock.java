package ic2.core.block;

import com.mojang.serialization.MapCodec;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public final class RubberWoodBlock extends Block {
    public static final MapCodec<RubberWoodBlock> CODEC = simpleCodec(RubberWoodBlock::new);
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final EnumProperty<ResinState> RESIN = EnumProperty.create("resin", ResinState.class);
    public static final IntegerProperty AMOUNT = IntegerProperty.create("amount", 0, 3);

    public RubberWoodBlock(BlockBehaviour.Properties properties) {
        super(properties.mapColor(MapColor.WOOD).sound(SoundType.WOOD).randomTicks());
        registerDefaultState(stateDefinition.any()
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(RESIN, ResinState.NONE)
                .setValue(AMOUNT, 0));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, RESIN, AMOUNT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis());
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        ResinState resinState = state.getValue(RESIN);

        if (resinState == ResinState.NONE) {
            if (state.getValue(AXIS) == Direction.Axis.Y && hasNearbyLeaves(level, pos)) {
                level.setBlock(
                        pos,
                        state.setValue(RESIN, ResinState.wet(Direction.Plane.HORIZONTAL.getRandomDirection(random)))
                                .setValue(AMOUNT, random.nextInt(3) + 1),
                        Block.UPDATE_CLIENTS
                );
            }
            return;
        }

        if (!resinState.isWet() && random.nextInt(7) == 0) {
            int amount = Math.max(1, state.getValue(AMOUNT));
            level.setBlock(pos, state.setValue(RESIN, resinState.toWet()).setValue(AMOUNT, amount), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return state.getValue(RESIN) == ResinState.NONE ? PushReaction.NORMAL : PushReaction.BLOCK;
    }

    public boolean canExtractResin(BlockState state, Direction side) {
        ResinState resinState = state.getValue(RESIN);
        return resinState.isWet() && resinState.facing() == side;
    }

    public int extractResin(Level level, BlockPos pos, BlockState state, Direction side, RandomSource random) {
        ResinState resinState = state.getValue(RESIN);

        if (!resinState.isWet() || resinState.facing() != side) {
            return 0;
        }

        int amount = Math.max(1, state.getValue(AMOUNT));
        level.setBlock(pos, state.setValue(RESIN, resinState.toDry()).setValue(AMOUNT, amount), Block.UPDATE_CLIENTS);
        return amount;
    }

    private static boolean hasNearbyLeaves(BlockGetter level, BlockPos pos) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int y = -2; y <= 2; y++) {
            for (int z = -2; z <= 2; z++) {
                for (int x = -2; x <= 2; x++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }

                    cursor.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (level.getBlockState(cursor).is(BlockTags.LEAVES)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public enum ResinState implements StringRepresentable {
        NONE(null, false),
        DRY_NORTH(Direction.NORTH, false),
        DRY_SOUTH(Direction.SOUTH, false),
        DRY_WEST(Direction.WEST, false),
        DRY_EAST(Direction.EAST, false),
        WET_NORTH(Direction.NORTH, true),
        WET_SOUTH(Direction.SOUTH, true),
        WET_WEST(Direction.WEST, true),
        WET_EAST(Direction.EAST, true);

        private final Direction facing;
        private final boolean wet;

        ResinState(Direction facing, boolean wet) {
            this.facing = facing;
            this.wet = wet;
        }

        public Direction facing() {
            return facing;
        }

        public boolean isWet() {
            return wet;
        }

        public ResinState toWet() {
            if (this == NONE || wet) {
                return this;
            }

            return wet(facing);
        }

        public ResinState toDry() {
            if (this == NONE || !wet) {
                return this;
            }

            return dry(facing);
        }

        public static ResinState wet(Direction facing) {
            return switch (facing) {
                case NORTH -> WET_NORTH;
                case SOUTH -> WET_SOUTH;
                case WEST -> WET_WEST;
                case EAST -> WET_EAST;
                default -> throw new IllegalArgumentException("Unsupported resin facing: " + facing);
            };
        }

        public static ResinState dry(Direction facing) {
            return switch (facing) {
                case NORTH -> DRY_NORTH;
                case SOUTH -> DRY_SOUTH;
                case WEST -> DRY_WEST;
                case EAST -> DRY_EAST;
                default -> throw new IllegalArgumentException("Unsupported resin facing: " + facing);
            };
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
