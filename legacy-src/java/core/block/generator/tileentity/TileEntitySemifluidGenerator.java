// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core.block.generator.tileentity;

import ic2.api.recipe.ISemiFluidFuelManager;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import ic2.core.util.ConfigUtil;
import ic2.core.init.MainConfig;
import ic2.core.SemiFluidFuelManager;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotConsumableLiquidByManager;
import ic2.api.recipe.ILiquidAcceptManager;
import ic2.api.recipe.Recipes;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.Fluids;
import ic2.core.network.GuiSynced;
import net.minecraftforge.fluids.FluidTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.profile.NotClassic;

@NotClassic
public class TileEntitySemifluidGenerator extends TileEntityBaseGenerator
{
    public final InvSlotConsumableLiquid fluidSlot;
    public final InvSlotOutput outputSlot;
    @GuiSynced
    protected final FluidTank fluidTank;
    protected final Fluids fluids;
    
    public TileEntitySemifluidGenerator() {
        super(32.0, 1, 32000);
        this.fluids = this.addComponent(new Fluids(this));
        this.fluidTank = this.fluids.addTankInsert("fluid", 10000, Fluids.fluidPredicate(Recipes.semiFluidGenerator));
        this.fluidSlot = new InvSlotConsumableLiquidByManager(this, "fluidSlot", 1, Recipes.semiFluidGenerator);
        this.outputSlot = new InvSlotOutput(this, "output", 1);
    }
    
    public static void init() {
        Recipes.semiFluidGenerator = new SemiFluidFuelManager();
        if (ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidOil") > 0.0f) {
            addFuel("oil", 10, Math.round(8.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidOil")));
        }
        if (ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidFuel") > 0.0f) {
            addFuel("fuel", 5, Math.round(32.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidFuel")));
        }
        if (ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidBiomass") > 0.0f) {
            addFuel("biomass", 20, Math.round(8.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidBiomass")));
        }
        if (ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidBioethanol") > 0.0f) {
            addFuel("bio.ethanol", 10, Math.round(16.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidBioethanol")));
        }
        if (ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidBiogas") > 0.0f) {
            addFuel("ic2biogas", 10, Math.round(16.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidBiogas")));
        }
    }
    
    public static void addFuel(final String fluidName, final int amount, final int eu) {
        Recipes.semiFluidGenerator.addFluid(fluidName, amount, eu);
    }
    
    public void updateEntityServer() {
        super.updateEntityServer();
        if (this.fluidSlot.processIntoTank((IFluidTank)this.fluidTank, this.outputSlot)) {
            this.markDirty();
        }
    }
    
    @Override
    public boolean gainFuel() {
        boolean dirty = false;
        final FluidStack ret = this.fluidTank.drain(Integer.MAX_VALUE, false);
        if (ret != null) {
            final ISemiFluidFuelManager.BurnProperty property = Recipes.semiFluidGenerator.getBurnProperty(ret.getFluid());
            if (property != null && ret.amount >= property.amount) {
                this.fluidTank.drainInternal(property.amount, true);
                this.production = property.power;
                this.fuel += property.amount;
                dirty = true;
            }
        }
        return dirty;
    }
    
    @Override
    public String getOperationSoundFile() {
        return "Generators/GeothermalLoop.ogg";
    }
}
