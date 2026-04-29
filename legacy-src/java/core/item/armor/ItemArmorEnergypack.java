// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core.item.armor;

import net.minecraft.item.ItemStack;
import net.minecraft.inventory.EntityEquipmentSlot;
import ic2.core.ref.ItemName;

public class ItemArmorEnergypack extends ItemArmorElectric
{
    public ItemArmorEnergypack() {
        super(ItemName.energy_pack, "energypack", EntityEquipmentSlot.CHEST, 2000000.0, 1000.0, 3);
    }
    
    @Override
    public boolean canProvideEnergy(final ItemStack stack) {
        return true;
    }
    
    @Override
    public double getDamageAbsorptionRatio() {
        return 0.0;
    }
    
    @Override
    public int getEnergyPerDamage() {
        return 0;
    }
}
