// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core.block.machine.tileentity;

import java.util.EnumSet;
import ic2.api.upgrade.UpgradableProperty;
import java.util.Set;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ic2.core.block.machine.gui.GuiCropHavester;
import net.minecraft.client.gui.GuiScreen;
import ic2.core.block.machine.container.ContainerCropHavester;
import ic2.core.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.item.ItemStack;
import ic2.api.crops.ICropTile;
import ic2.core.util.StackUtil;
import ic2.core.crop.TileEntityCrop;
import ic2.core.ref.ItemName;
import net.minecraft.item.Item;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotConsumableId;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.invslot.InvSlot;
import ic2.core.profile.NotClassic;
import ic2.api.upgrade.IUpgradableBlock;
import ic2.core.IHasGui;

@NotClassic
public class TileEntityCropHarvester extends TileEntityElectricMachine implements IHasGui, IUpgradableBlock
{
    public final InvSlot contentSlot;
    public final InvSlotUpgrade upgradeSlot;
    public final InvSlotConsumableId cropnalyzerSlot;
    public int scanX;
    public int scanY;
    public int scanZ;
    
    public TileEntityCropHarvester() {
        super(10000, 1, false);
        this.scanX = -5;
        this.scanY = -1;
        this.scanZ = -5;
        this.contentSlot = new InvSlot(this, "content", InvSlot.Access.IO, 15);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 1);
        this.cropnalyzerSlot = new InvSlotConsumableId(this, "cropnalyzer", 7, new Item[] { ItemName.cropnalyzer.getInstance() });
    }
    
    protected void updateEntityServer() {
        super.updateEntityServer();
        this.upgradeSlot.tick();
        if (this.energy.getEnergy() >= 201.0) {
            this.scan();
        }
    }
    
    public void scan() {
        final ItemStack cropnalyzer = this.cropnalyzerSlot.get(0);
        ++this.scanX;
        if (this.scanX > 5) {
            this.scanX = -5;
            ++this.scanZ;
            if (this.scanZ > 5) {
                this.scanZ = -5;
                ++this.scanY;
                if (this.scanY > 1) {
                    this.scanY = -1;
                }
            }
        }
        this.energy.useEnergy(1.0);
        final World world = this.getWorld();
        final TileEntity te = world.getTileEntity(this.pos.add(this.scanX, this.scanY, this.scanZ));
        if (te instanceof TileEntityCrop && !this.isInvFull()) {
            final TileEntityCrop crop = (TileEntityCrop)te;
            if (crop.getCrop() != null) {
                List<ItemStack> drops = null;
                if (!StackUtil.isEmpty(cropnalyzer) && crop.getCurrentSize() == crop.getCrop().getOptimalHarvestSize(crop)) {
                    drops = crop.performHarvest();
                }
                else if (crop.getCurrentSize() == crop.getCrop().getMaxSize()) {
                    drops = crop.performHarvest();
                }
                if (drops != null) {
                    for (final ItemStack drop : drops) {
                        if (StackUtil.putInInventory(this, EnumFacing.WEST, drop, true) == 0) {
                            StackUtil.dropAsEntity(world, this.pos, drop);
                        }
                        else {
                            StackUtil.putInInventory(this, EnumFacing.WEST, drop, false);
                        }
                        this.energy.useEnergy(100.0);
                        if (!StackUtil.isEmpty(cropnalyzer)) {
                            this.energy.useEnergy(100.0);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public ContainerBase<TileEntityCropHarvester> getGuiContainer(final EntityPlayer player) {
        return new ContainerCropHavester(player, this);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getGui(final EntityPlayer player, final boolean isAdmin) {
        return (GuiScreen)new GuiCropHavester(new ContainerCropHavester(player, this));
    }
    
    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.ItemProducing);
    }
    
    @Override
    public double getEnergy() {
        return this.energy.getEnergy();
    }
    
    @Override
    public boolean useEnergy(final double amount) {
        return this.energy.useEnergy(amount);
    }
    
    @Override
    public void onGuiClosed(final EntityPlayer player) {
    }
    
    private boolean isInvFull() {
        for (int i = 0; i < this.contentSlot.size(); ++i) {
            final ItemStack stack = this.contentSlot.get(i);
            if (StackUtil.isEmpty(stack) || StackUtil.getSize(stack) < Math.min(stack.getMaxStackSize(), this.contentSlot.getStackSizeLimit())) {
                return false;
            }
        }
        return true;
    }
}
