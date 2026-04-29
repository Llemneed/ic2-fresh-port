package ic2.core.energy;

public interface EnergyConsumer {
    int receiveEnergy(int amount);

    boolean canReceiveEnergy();

    default int receiveEu(int amount, EnergyTier sourceTier, boolean simulate) {
        if (amount <= 0 || !canReceiveEnergy()) {
            return 0;
        }

        int packetLimit = maxInputPerTick();
        if (amount > packetLimit || sourceTier.packetSize() > packetLimit) {
            if (!simulate) {
                onOvervoltage(amount);
            }
            return 0;
        }

        int accepted = Math.min(amount, packetLimit);
        if (simulate) {
            return accepted;
        }

        return receiveEnergy(accepted);
    }

    default int maxInputPerTick() {
        return getSinkTier().packetSize();
    }

    default EnergyTier getSinkTier() {
        return EnergyTier.forPacket(maxInputPerTick());
    }

    default void onOvervoltage(int amount) {
    }
}
