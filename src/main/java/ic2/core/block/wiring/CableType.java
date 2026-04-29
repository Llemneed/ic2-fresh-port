package ic2.core.block.wiring;

public enum CableType {
    TIN("tin_cable", "tin_cable_0", 32, 1),
    COPPER("copper_cable", "copper_cable_0", 128, 1),
    GOLD("gold_cable", "gold_cable_0", 512, 2),
    GLASS_FIBRE("glass_fibre_cable", "glass_cable", 2048, 0);

    private final String id;
    private final String modelName;
    private final int packetLimit;
    private final int lossPerBlock;

    CableType(String id, String modelName, int packetLimit, int lossPerBlock) {
        this.id = id;
        this.modelName = modelName;
        this.packetLimit = packetLimit;
        this.lossPerBlock = lossPerBlock;
    }

    public String id() {
        return id;
    }

    public String modelName() {
        return modelName;
    }

    public int packetLimit() {
        return packetLimit;
    }

    public int lossPerBlock() {
        return lossPerBlock;
    }
}
