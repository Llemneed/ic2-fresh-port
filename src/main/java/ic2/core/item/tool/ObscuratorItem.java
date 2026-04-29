package ic2.core.item.tool;

import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class ObscuratorItem extends Item {
    private static final String BLOCK_ID_KEY = "SampledBlock";

    public ObscuratorItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (key == null) {
            return InteractionResult.PASS;
        }

        CompoundTag tag = context.getItemInHand().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putString(BLOCK_ID_KEY, key.toString());
        context.getItemInHand().set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        if (!level.isClientSide() && context.getPlayer() != null) {
            context.getPlayer().displayClientMessage(Component.literal("Sampled block: " + key), true);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.contains(BLOCK_ID_KEY)) {
            tooltipComponents.add(Component.literal("Sampled: " + tag.getString(BLOCK_ID_KEY)));
        } else {
            tooltipComponents.add(Component.literal("Sampled: none"));
        }
    }
}
