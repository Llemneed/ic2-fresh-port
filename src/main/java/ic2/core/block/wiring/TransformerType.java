package ic2.core.block.wiring;

import ic2.core.energy.EnergyTier;

public enum TransformerType {
    LV("lv_transformer", EnergyTier.LV, EnergyTier.MV),
    MV("mv_transformer", EnergyTier.MV, EnergyTier.HV),
    HV("hv_transformer", EnergyTier.HV, EnergyTier.EV),
    EV("ev_transformer", EnergyTier.EV, EnergyTier.IV);

    private final String id;
    private final EnergyTier lowTier;
    private final EnergyTier highTier;

    TransformerType(String id, EnergyTier lowTier, EnergyTier highTier) {
        this.id = id;
        this.lowTier = lowTier;
        this.highTier = highTier;
    }

    public String id() {
        return id;
    }

    public EnergyTier lowTier() {
        return lowTier;
    }

    public EnergyTier highTier() {
        return highTier;
    }
}
