package ic2.core.item.electric;

public interface ElectricItem {
    int getMaxCharge();

    int getTransferLimit();

    default int getEnergyTier() {
        return 1;
    }

    default boolean canProvideEnergy() {
        return false;
    }
}
