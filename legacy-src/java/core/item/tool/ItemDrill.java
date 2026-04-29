// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core.item.tool;

import java.util.Iterator;
import net.minecraftforge.fml.common.FMLCommonHandler;
import ic2.core.IC2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.block.material.Material;
import ic2.api.item.ElectricItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.client.entity.EntityPlayerSP;
import java.util.Set;
import java.util.EnumSet;
import ic2.core.ref.ItemName;
import ic2.core.IHitSoundOverride;

public class ItemDrill extends ItemElectricTool implements IHitSoundOverride
{
    public ItemDrill(final ItemName name, final int operationEnergyCost, final HarvestLevel harvestLevel, final int maxCharge, final int transferLimit, final int tier, final float efficiency) {
        super(name, operationEnergyCost, harvestLevel, EnumSet.of(ToolClass.Pickaxe, ToolClass.Shovel));
        this.maxCharge = maxCharge;
        this.transferLimit = transferLimit;
        this.tier = tier;
        this.efficiency = efficiency;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public String getHitSoundForBlock(final EntityPlayerSP player, final World world, final BlockPos pos, final ItemStack stack) {
        final IBlockState state = world.getBlockState(pos);
        final float hardness = state.getBlockHardness(world, pos);
        if (hardness > 1.0f || hardness < 0.0f) {
            return "Tools/Drill/DrillHard.ogg";
        }
        return "Tools/Drill/DrillSoft.ogg";
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public String getBreakSoundForBlock(final EntityPlayerSP player, final World world, final BlockPos pos, final ItemStack stack) {
        if (player.capabilities.isCreativeMode) {
            return null;
        }
        final IBlockState state = world.getBlockState(pos);
        final float hardness = state.getBlockHardness(world, pos);
        if (hardness > 0.5 || !ElectricItem.manager.canUse(stack, this.operationEnergyCost)) {
            return null;
        }
        return "Tools/Drill/DrillSoft.ogg";
    }
    
    @Override
    public float getDestroySpeed(final ItemStack stack, final IBlockState state) {
        float speed = super.getDestroySpeed(stack, state);
        final EntityPlayer player = getPlayerHoldingItem(stack);
        if (player != null) {
            if (player.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier((EntityLivingBase)player)) {
                speed *= 5.0f;
            }
            if (!player.onGround) {
                speed *= 5.0f;
            }
        }
        return speed;
    }
    
    private static EntityPlayer getPlayerHoldingItem(final ItemStack stack) {
        if (IC2.platform.isRendering()) {
            final EntityPlayer player = IC2.platform.getPlayerInstance();
            if (player != null && player.inventory.getCurrentItem() == stack) {
                return player;
            }
        }
        else {
            for (final EntityPlayer player2 : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
                if (player2.inventory.getCurrentItem() == stack) {
                    return player2;
                }
            }
        }
        return null;
    }
}
