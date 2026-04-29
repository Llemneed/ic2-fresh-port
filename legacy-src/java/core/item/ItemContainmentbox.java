// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core.item;

import ic2.core.item.tool.HandHeldContainmentbox;
import ic2.core.IHasGui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.EnumActionResult;
import ic2.core.IC2;
import ic2.core.util.StackUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import ic2.core.ref.ItemName;

public class ItemContainmentbox extends ItemIC2 implements IHandHeldInventory
{
    public ItemContainmentbox() {
        super(ItemName.containment_box);
        this.setMaxStackSize(1);
    }
    
    public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
        final ItemStack stack = StackUtil.get(player, hand);
        if (!world.isRemote) {
            IC2.platform.launchGui(player, this.getInventory(player, stack));
        }
        return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public EnumRarity getRarity(final ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }
    
    @Override
    public IHasGui getInventory(final EntityPlayer player, final ItemStack stack) {
        return new HandHeldContainmentbox(player, stack, 12);
    }
}
