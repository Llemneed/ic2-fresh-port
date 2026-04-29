package ic2.core.block.generator;

import ic2.core.init.IC2BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class GeoGeneratorBlockEntity extends GeneratorBlockEntity {
    public GeoGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(IC2BlockEntities.GEO_GENERATOR.get(), pos, blockState, 20, 4000, 20, "block.ic2.geo_generator");
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GeoGeneratorBlockEntity blockEntity) {
        blockEntity.serverTickInternal(level, pos, state);
    }

    @Override
    protected int getFuelValue(ItemStack stack) {
        return stack.is(Items.LAVA_BUCKET) ? 1000 : 0;
    }

    @Override
    protected ItemStack getFuelRemainder(ItemStack stack) {
        return stack.is(Items.LAVA_BUCKET) ? new ItemStack(Items.BUCKET) : ItemStack.EMPTY;
    }
}
