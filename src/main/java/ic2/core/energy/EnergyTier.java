package ic2.core.energy;

public enum EnergyTier {
    LV(32),
    MV(128),
    HV(512),
    EV(2048),
    IV(8192);

    private final int packetSize;

    EnergyTier(int packetSize) {
        this.packetSize = packetSize;
    }

    public int packetSize() {
        return packetSize;
    }

    public static EnergyTier forPacket(int packetSize) {
        for (EnergyTier tier : values()) {
            if (packetSize <= tier.packetSize) {
                return tier;
            }
        }
        return IV;
    }
}
