package ic2.core.energy;

public interface EnergyConsumer {
    int receiveEnergy(int amount);

    boolean canReceiveEnergy();

    default int maxInputPerTick() {
        return Integer.MAX_VALUE;
    }

    default void onOvervoltage(int amount) {
    }
}
