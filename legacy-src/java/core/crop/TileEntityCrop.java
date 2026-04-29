// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core.crop;

import ic2.core.block.state.UnlistedProperty;
import ic2.core.ref.FluidName;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidRegistry;
import ic2.api.crops.CropSoilType;
import ic2.core.util.Util;
import net.minecraft.util.math.RayTraceResult;
import ic2.core.block.state.Ic2BlockState;
import ic2.core.item.ItemCropSeed;
import net.minecraftforge.oredict.OreDictionary;
import ic2.core.Ic2Player;
import net.minecraft.world.IBlockAccess;
import java.util.Arrays;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.BlockFarmland;
import ic2.core.util.BiomeUtil;
import ic2.api.network.NetworkHelper;
import ic2.api.crops.BaseSeed;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidUtil;
import ic2.core.item.type.CropResItemType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import ic2.core.util.StackUtil;
import ic2.core.ref.ItemName;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import java.util.Iterator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import ic2.core.network.NetworkManager;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.EnumPlantType;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NBTBase;
import ic2.api.crops.Crops;
import ic2.core.util.LogCategory;
import ic2.core.IC2;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraft.nbt.NBTTagCompound;
import ic2.api.crops.CropCard;
import ic2.api.crops.ICropTile;
import ic2.core.block.TileEntityBlock;

public class TileEntityCrop extends TileEntityBlock implements ICropTile
{
    public char ticker;
    public boolean dirty;
    public static int tickRate;
    private CropCard crop;
    private byte statGrowth;
    private byte statGain;
    private byte statResistance;
    private short storageNutrients;
    private short storageWater;
    private short storageWeedEX;
    private byte terrainAirQuality;
    private byte terrainHumidity;
    private byte terrainNutrients;
    private byte currentSize;
    private short growthPoints;
    private byte scanLevel;
    private boolean crossingBase;
    private NBTTagCompound customData;
    public static final IUnlistedProperty<CropRenderState> renderStateProperty;
    private volatile CropRenderState cropRenderState;
    public static final boolean debug;
    public static final boolean debugGrowth;
    public static final boolean debugWeedWork;
    public static final boolean debugCollision;
    public static final boolean debugTerrain;
    
    public TileEntityCrop() {
        this.ticker = (char)IC2.random.nextInt(TileEntityCrop.tickRate);
        this.dirty = true;
        this.crop = null;
        this.growthPoints = 0;
        this.customData = new NBTTagCompound();
        if (TileEntityCrop.debug) {
            IC2.log.info(LogCategory.Block, "Debug mode is running");
        }
    }
    
    @Override
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.crossingBase = nbt.getBoolean("crossingBase");
        if (nbt.hasKey("cropOwner") && nbt.hasKey("cropId")) {
            this.crop = Crops.instance.getCropCard(nbt.getString("cropOwner"), nbt.getString("cropId"));
            this.statGrowth = nbt.getByte("statGrowth");
            this.statGain = nbt.getByte("statGain");
            this.statResistance = nbt.getByte("statResistance");
            this.storageNutrients = nbt.getShort("storageNutrients");
            this.storageWater = nbt.getShort("storageWater");
            this.storageWeedEX = nbt.getShort("storageWeedEX");
            this.terrainHumidity = nbt.getByte("terrainHumidity");
            this.terrainNutrients = nbt.getByte("terrainNutrients");
            this.terrainAirQuality = nbt.getByte("terrainAirQuality");
            this.currentSize = nbt.getByte("currentSize");
            this.growthPoints = nbt.getShort("growthPoints");
            this.scanLevel = nbt.getByte("scanLevel");
            this.customData = nbt.getCompoundTag("customData");
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("crossingBase", this.crossingBase);
        if (this.crop != null) {
            nbt.setString("cropOwner", this.crop.getOwner());
            nbt.setString("cropId", this.crop.getId());
            nbt.setByte("statGrowth", this.statGrowth);
            nbt.setByte("statGain", this.statGain);
            nbt.setByte("statResistance", this.statResistance);
            nbt.setShort("storageNutrients", this.storageNutrients);
            nbt.setShort("storageWater", this.storageWater);
            nbt.setShort("storageWeedEX", this.storageWeedEX);
            nbt.setByte("terrainHumidity", this.terrainHumidity);
            nbt.setByte("terrainNutrients", this.terrainNutrients);
            nbt.setByte("terrainAirQuality", this.terrainAirQuality);
            nbt.setByte("currentSize", this.currentSize);
            nbt.setShort("growthPoints", this.growthPoints);
            nbt.setByte("scanLevel", this.scanLevel);
            nbt.setTag("customData", (NBTBase)this.customData.copy());
        }
        return nbt;
    }
    
    @Override
    public List<String> getNetworkedFields() {
        final List<String> ret = new ArrayList<String>();
        ret.add("crop");
        ret.add("currentSize");
        ret.add("statGrowth");
        ret.add("statGain");
        ret.add("statResistance");
        ret.add("storageNutrients");
        ret.add("storageWater");
        ret.add("storageWeedEX");
        ret.add("terrainHumidity");
        ret.add("terrainNutrients");
        ret.add("terrainAirQuality");
        ret.add("currentSize");
        ret.add("growthPoints");
        ret.add("scanLevel");
        ret.add("crossingBase");
        ret.add("customData");
        ret.addAll(super.getNetworkedFields());
        return ret;
    }
    
    @Override
    public void onNetworkUpdate(final String field) {
        this.updateRenderState();
        this.rerender();
        super.onNetworkUpdate(field);
    }
    
    @Override
    protected EnumPlantType getPlantType() {
        return EnumPlantType.Crop;
    }
    
    @Override
    protected void onLoaded() {
        super.onLoaded();
        if (this.getWorld().isRemote) {
            this.updateRenderState();
        }
    }
    
    public void updateEntityServer() {
        final char ticker = this.ticker;
        this.ticker = (char)(ticker + '\u0001');
        if (ticker % TileEntityCrop.tickRate == 0) {
            this.performTick();
        }
        if (this.dirty) {
            this.dirty = false;
            final World world = this.getWorld();
            final IBlockState state = world.getBlockState(this.pos);
            world.notifyBlockUpdate(this.pos, state, state, 3);
            world.checkLightFor(EnumSkyBlock.BLOCK, this.pos);
            if (!world.isRemote) {
                for (final String field : this.getNetworkedFields()) {
                    IC2.network.get(true).updateTileEntityField(this, field);
                }
            }
        }
    }
    
    public void performTick() {
        assert !this.getWorld().isRemote;
        if (this.ticker % (TileEntityCrop.tickRate << 2) == 0) {
            this.updateTerrainHumidity();
            if (TileEntityCrop.debug) {
                IC2.log.info(LogCategory.Block, "Crop at %s - terrain humidity: %s", this.pos, this.terrainHumidity);
            }
        }
        if ((this.ticker + TileEntityCrop.tickRate) % (TileEntityCrop.tickRate << 2) == 0) {
            this.updateTerrainNutrients();
            if (TileEntityCrop.debug) {
                IC2.log.info(LogCategory.Block, "Crop at %s - terrain nutrients: %s", this.pos, this.terrainNutrients);
            }
        }
        if ((this.ticker + TileEntityCrop.tickRate * 2) % (TileEntityCrop.tickRate << 2) == 0) {
            this.updateTerrainAirQuality();
            if (TileEntityCrop.debug) {
                IC2.log.info(LogCategory.Block, "Crop at %s - terrain air quality: %s", this.pos, this.terrainAirQuality);
            }
        }
        if (this.crop == null && (!this.isCrossingBase() || !this.attemptCrossing())) {
            if (IC2.random.nextInt(100) != 0 || this.getStorageWeedEX() > 0) {
                if (this.getStorageWeedEX() > 0 && IC2.random.nextInt(10) == 0) {
                    --this.storageWeedEX;
                }
                return;
            }
            this.reset();
            this.crop = IC2Crops.weed;
            this.setCurrentSize(1);
        }
        this.crop.tick(this);
        if (TileEntityCrop.debug) {
            System.out.println("Plant: " + this.getCrop().getUnlocalizedName());
        }
        if (this.crop.canGrow(this)) {
            this.performGrowthTick();
            if (this.crop == null) {
                return;
            }
            if (this.growthPoints >= this.crop.getGrowthDuration(this)) {
                this.growthPoints = 0;
                this.setCurrentSize(this.getCurrentSize() + 1);
                this.dirty = true;
            }
        }
        if (this.storageNutrients > 0) {
            --this.storageNutrients;
        }
        if (this.storageWater > 0) {
            --this.storageWater;
        }
        if (this.crop.isWeed(this) && IC2.random.nextInt(50) - this.getStatGrowth() <= 2) {
            this.performWeedWork();
        }
    }
    
    public void performGrowthTick() {
        if (this.crop == null) {
            return;
        }
        if (TileEntityCrop.debugGrowth) {
            IC2.log.info(LogCategory.Block, "Crop at %s - growth points (before): %s", this.pos, this.growthPoints);
        }
        int totalGrowth = 0;
        final int baseGrowth = 3 + IC2.random.nextInt(7) + this.getStatGrowth();
        int minimumQuality = (this.crop.getProperties().getTier() - 1) * 4 + this.getStatGrowth() + this.statGain + this.statResistance;
        minimumQuality = ((minimumQuality < 0) ? 0 : minimumQuality);
        final int providedQuality = 75;
        if (providedQuality >= minimumQuality) {
            totalGrowth = baseGrowth * (100 + (providedQuality - minimumQuality)) / 100;
        }
        else {
            final int aux = (minimumQuality - providedQuality) * 4;
            if (aux > 100 && IC2.random.nextInt(32) > this.statResistance) {
                this.reset();
                totalGrowth = 0;
            }
            else {
                totalGrowth = baseGrowth * (100 - aux) / 100;
                totalGrowth = ((totalGrowth < 0) ? 0 : totalGrowth);
            }
        }
        this.growthPoints += (short)totalGrowth;
        if (TileEntityCrop.debugGrowth) {
            IC2.log.info(LogCategory.Block, "Crop at %s - base growth: %s", this.pos, baseGrowth);
            IC2.log.info(LogCategory.Block, "Crop at %s - minimum quality: %s", this.pos, minimumQuality);
            IC2.log.info(LogCategory.Block, "Crop at %s - provided quality: %s", this.pos, providedQuality);
            IC2.log.info(LogCategory.Block, "Crop at %s - total growth: %s", this.pos, totalGrowth);
            IC2.log.info(LogCategory.Block, "Crop at %s - growth points (after): %s", this.pos, this.growthPoints);
        }
    }
    
    public void performWeedWork() {
        final World world = this.getWorld();
        final BlockPos dstPos = this.pos.offset(EnumFacing.HORIZONTALS[IC2.random.nextInt(4)]);
        final TileEntity dstRaw = world.getTileEntity(dstPos);
        if (dstRaw instanceof TileEntityCrop) {
            if (TileEntityCrop.debugWeedWork) {
                IC2.log.info(LogCategory.Block, "Crop at %s - trying to generate weed", dstPos);
            }
            final TileEntityCrop tileEntityCrop = (TileEntityCrop)dstRaw;
            final CropCard neighborCrop = tileEntityCrop.getCrop();
            if (neighborCrop == null || (!neighborCrop.isWeed(tileEntityCrop) && IC2.random.nextInt(32) >= tileEntityCrop.getStatResistance() && !tileEntityCrop.hasWeedEX())) {
                if (TileEntityCrop.debugWeedWork) {
                    IC2.log.info(LogCategory.Block, "Crop at %s - weed generated", dstPos);
                }
                int newGrowth = Math.max(this.getStatGrowth(), tileEntityCrop.getStatGrowth());
                if (newGrowth < 31 && IC2.random.nextBoolean()) {
                    ++newGrowth;
                }
                tileEntityCrop.reset();
                tileEntityCrop.crop = Crops.weed;
                tileEntityCrop.setCurrentSize(1);
                tileEntityCrop.setStatGrowth(newGrowth);
            }
        }
        else if (world.isAirBlock(dstPos)) {
            if (TileEntityCrop.debugWeedWork) {
                IC2.log.info(LogCategory.Block, "Block at %s - trying to generate grass", dstPos);
            }
            final BlockPos soilPos = dstPos.down();
            final Block block = world.getBlockState(soilPos).getBlock();
            if (block == Blocks.DIRT || block == Blocks.GRASS || block == Blocks.FARMLAND) {
                world.setBlockState(soilPos, Blocks.GRASS.getDefaultState(), 7);
                world.setBlockState(dstPos, Blocks.TALLGRASS.getStateFromMeta(1), 7);
            }
        }
    }
    
    public boolean hasWeedEX() {
        if (this.storageWeedEX > 0) {
            this.storageWeedEX -= 5;
            return true;
        }
        return false;
    }
    
    @Override
    protected boolean onActivated(final EntityPlayer player, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        return this.getWorld().isRemote || this.rightClick(player, hand);
    }
    
    @Override
    protected void onClicked(final EntityPlayer player) {
        if (this.crop != null) {
            this.crop.onLeftClick(this, player);
        }
        else if (this.crossingBase && !this.getWorld().isRemote) {
            this.crossingBase = false;
            this.dirty = true;
            StackUtil.dropAsEntity(this.getWorld(), this.pos, ItemName.crop_stick.getItemStack());
        }
    }
    
    @Override
    protected SoundType getBlockSound(final Entity entity) {
        return SoundType.PLANT;
    }
    
    @Override
    protected void onBlockBreak() {
        if (!this.getWorld().isRemote) {
            this.pick();
        }
    }
    
    @Override
    protected List<AxisAlignedBB> getAabbs(final boolean forCollision) {
        final List<AxisAlignedBB> ret = new ArrayList<AxisAlignedBB>();
        if (forCollision) {
            ret.add(new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
        }
        else {
            ret.add(new AxisAlignedBB(0.20000000298023224, 0.0, 0.20000000298023224, 0.800000011920929, 0.8500000238418579, 0.800000011920929));
        }
        return ret;
    }
    
    public boolean rightClick(final EntityPlayer player, final EnumHand hand) {
        final ItemStack heldItem = StackUtil.get(player, hand);
        final boolean creative = player.capabilities.isCreativeMode;
        if (!StackUtil.isEmpty(heldItem)) {
            if (this.crop == null && !this.crossingBase && heldItem.getItem() == ItemName.crop_stick.getInstance()) {
                if (!creative) {
                    StackUtil.consumeOrError(player, hand, 1);
                }
                this.crossingBase = true;
                return this.dirty = true;
            }
            if (this.crop != null && StackUtil.checkItemEquality(heldItem, ItemName.crop_res.getItemStack(CropResItemType.fertilizer))) {
                if (this.applyFertilizer(true)) {
                    this.dirty = true;
                }
                if (!creative) {
                    StackUtil.consumeOrError(player, hand, 1);
                }
                return true;
            }
            final IFluidHandler handler = (IFluidHandler)FluidUtil.getFluidHandler(heldItem);
            if (handler != null) {
                if (this.applyHydration(handler) || this.applyWeedEx(handler, true)) {
                    this.dirty = true;
                }
                return true;
            }
            if (this.crop == null && !this.crossingBase && Crops.instance.getBaseSeed(heldItem) != null) {
                this.reset();
                final BaseSeed bs = Crops.instance.getBaseSeed(heldItem);
                this.crop = bs.crop;
                this.currentSize = (byte)bs.size;
                this.statGain = (byte)bs.statGain;
                this.statGrowth = (byte)bs.statGrowth;
                this.statResistance = (byte)bs.statResistance;
                if (!creative) {
                    StackUtil.consumeOrError(player, hand, 1);
                }
                return true;
            }
        }
        return this.crop != null && this.crop.onRightClick(this, player);
    }
    
    public boolean tryPlantIn(final CropCard crop, final int size, final int statGr, final int statGa, final int statRe, final int scan) {
        if (crop == null || crop == IC2Crops.weed || this.isCrossingBase()) {
            return false;
        }
        if (!crop.canGrow(this)) {
            return false;
        }
        this.reset();
        this.crop = crop;
        this.setCurrentSize(size);
        this.setStatGain(statGa);
        this.setStatGrowth(statGr);
        this.setStatResistance(statRe);
        this.setScanLevel(scan);
        NetworkHelper.sendInitialData(this);
        return true;
    }
    
    public void onEntityCollision(final Entity entity) {
        if (this.crop == null) {
            return;
        }
        if (this.crop.onEntityCollision(this, entity)) {
            final World world = this.getWorld();
            if (world.isRemote) {
                return;
            }
            if (IC2.random.nextInt(100) == 0 && IC2.random.nextInt(40) > this.statResistance) {
                this.reset();
                world.setBlockState(this.pos.down(), Blocks.DIRT.getDefaultState(), 7);
                if (TileEntityCrop.debugCollision) {
                    IC2.log.info(LogCategory.Block, "Crop at %s - crop was trampled", this.pos);
                }
            }
        }
    }
    
    public void updateTerrainAirQuality() {
        final World world = this.getWorld();
        int value = 0;
        int height = (this.pos.getY() - 64) / 15;
        if (height > 4) {
            height = 4;
        }
        if (height < 0) {
            height = 0;
        }
        value += height;
        int fresh = 9;
        for (int x = this.pos.getX() - 1; x < this.pos.getX() + 1 && fresh > 0; ++x) {
            for (int z = this.pos.getZ() - 1; z < this.pos.getZ() + 1 && fresh > 0; ++z) {
                if (world.isBlockNormalCube(new BlockPos(x, this.pos.getY(), z), false) || world.getTileEntity(new BlockPos(x, this.pos.getY(), z)) instanceof TileEntityCrop) {
                    --fresh;
                }
            }
        }
        value += fresh / 2;
        if (world.canSeeSky(this.pos.up())) {
            value += 2;
        }
        this.setTerrainAirQuality(value);
    }
    
    public void updateTerrainHumidity() {
        final World world = this.getWorld();
        int humidity = Crops.instance.getHumidityBiomeBonus(BiomeUtil.getBiome(world, this.pos));
        if ((int)world.getBlockState(this.pos.down()).getValue((IProperty)BlockFarmland.MOISTURE) >= 7) {
            humidity += 2;
        }
        if (this.getStorageWater() >= 5) {
            humidity += 2;
        }
        humidity += (this.getStorageWater() + 24) / 25;
        this.setTerrainHumidity(humidity);
    }
    
    public void updateTerrainNutrients() {
        final World world = this.getWorld();
        int nutrients = Crops.instance.getNutrientBiomeBonus(BiomeUtil.getBiome(world, this.pos));
        for (int i = 1; i < 5 && world.getBlockState(this.pos.down(i)).getBlock() == Blocks.DIRT; ++i) {
            ++nutrients;
        }
        nutrients += (this.getStorageNutrients() + 19) / 20;
        this.setTerrainNutrients(nutrients);
    }
    
    @Override
    public CropCard getCrop() {
        return this.crop;
    }
    
    @Override
    public void setCrop(final CropCard crop) {
        this.crop = crop;
    }
    
    @Override
    public int getCurrentSize() {
        return this.currentSize;
    }
    
    @Override
    public void setCurrentSize(final int size) {
        this.currentSize = (byte)size;
    }
    
    @Override
    public int getStatGrowth() {
        return this.statGrowth;
    }
    
    @Override
    public void setStatGrowth(final int growth) {
        this.statGrowth = (byte)growth;
    }
    
    @Override
    public int getStatGain() {
        return this.statGain;
    }
    
    @Override
    public void setStatGain(final int gain) {
        this.statGain = (byte)gain;
    }
    
    @Override
    public int getStatResistance() {
        return this.statResistance;
    }
    
    @Override
    public void setStatResistance(final int resistance) {
        this.statResistance = (byte)resistance;
    }
    
    @Override
    public int getStorageNutrients() {
        return this.storageNutrients;
    }
    
    @Override
    public void setStorageNutrients(final int nutrients) {
        this.storageNutrients = (short)nutrients;
    }
    
    @Override
    public int getStorageWater() {
        return this.storageWater;
    }
    
    @Override
    public void setStorageWater(final int water) {
        this.storageWater = (short)water;
    }
    
    @Override
    public int getStorageWeedEX() {
        return this.storageWeedEX;
    }
    
    @Override
    public void setStorageWeedEX(final int weedEX) {
        this.storageWeedEX = (short)weedEX;
    }
    
    @Override
    public int getTerrainAirQuality() {
        return this.terrainAirQuality;
    }
    
    public void setTerrainAirQuality(final int value) {
        this.terrainAirQuality = (byte)value;
    }
    
    @Override
    public int getTerrainHumidity() {
        return this.terrainHumidity;
    }
    
    public void setTerrainHumidity(final int humidity) {
        this.terrainHumidity = (byte)humidity;
    }
    
    @Override
    public int getTerrainNutrients() {
        return this.terrainNutrients;
    }
    
    public void setTerrainNutrients(final int nutrients) {
        this.terrainNutrients = (byte)nutrients;
    }
    
    @Override
    public int getScanLevel() {
        return this.scanLevel;
    }
    
    @Override
    public void setScanLevel(final int scanLevel) {
        this.scanLevel = (byte)scanLevel;
    }
    
    @Override
    public int getGrowthPoints() {
        return this.growthPoints;
    }
    
    @Override
    public void setGrowthPoints(final int growthPoints) {
        this.growthPoints = (short)growthPoints;
    }
    
    @Override
    public boolean isCrossingBase() {
        return this.crossingBase;
    }
    
    @Override
    public void setCrossingBase(final boolean crossingBase) {
        this.crossingBase = crossingBase;
    }
    
    @Override
    public NBTTagCompound getCustomData() {
        return this.customData;
    }
    
    public BlockPos getPosition() {
        return this.pos;
    }
    
    public World getWorldObj() {
        return this.getWorld();
    }
    
    @Deprecated
    @Override
    public BlockPos getLocation() {
        return this.pos;
    }
    
    @Override
    public int getLightLevel() {
        return this.getWorld().getLight(this.pos);
    }
    
    public int getLightValue() {
        return (this.crop == null) ? 0 : this.crop.getEmittedLight(this);
    }
    
    @Override
    public boolean pick() {
        if (this.crop == null) {
            return false;
        }
        final World world = this.getWorld();
        final boolean bonus = this.crop.canBeHarvested(this);
        float firstchance = this.crop.dropSeedChance(this);
        firstchance *= (float)Math.pow(1.1, this.statResistance);
        int dropCount = 0;
        if (bonus) {
            if (world.rand.nextFloat() <= (firstchance + 1.0f) * 0.8f) {
                ++dropCount;
            }
            float chance = this.crop.dropSeedChance(this) + this.getStatGrowth() / 100.0f;
            for (int i = 23; i < this.statGain; ++i) {
                chance *= 0.95f;
            }
            if (world.rand.nextFloat() <= chance) {
                ++dropCount;
            }
        }
        else if (world.rand.nextFloat() <= firstchance * 1.5f) {
            ++dropCount;
        }
        final ItemStack[] drops = new ItemStack[dropCount];
        for (int i = 0; i < dropCount; ++i) {
            drops[i] = this.crop.getSeeds(this);
        }
        this.reset();
        if (!world.isRemote && drops.length > 0) {
            for (final ItemStack drop : drops) {
                if (drop.getItem() != ItemName.crop_seed_bag.getInstance()) {
                    drop.setTagCompound((NBTTagCompound)null);
                }
                StackUtil.dropAsEntity(world, this.pos, drop);
            }
        }
        return true;
    }
    
    @Override
    public boolean performManualHarvest() {
        final List<ItemStack> dropItems = this.performHarvest();
        if (dropItems != null && !dropItems.isEmpty()) {
            final World world = this.getWorld();
            for (final ItemStack stack : dropItems) {
                StackUtil.dropAsEntity(world, this.pos, stack);
            }
            return true;
        }
        return false;
    }
    
    @Override
    public List<ItemStack> performHarvest() {
        if (this.crop == null || !this.crop.canBeHarvested(this)) {
            return null;
        }
        double chance = this.crop.dropGainChance();
        chance *= Math.pow(1.03, this.getStatGain());
        if (TileEntityCrop.debug) {
            System.out.println("chance: " + chance);
            final int simCount = 200;
            int sum = 0;
            for (int i = 0; i < 200; ++i) {
                final int dropCount = (int)Math.max(0L, Math.round(IC2.random.nextGaussian() * chance * 0.6827 + chance));
                sum += dropCount;
                System.out.print(dropCount + " ");
            }
            System.out.println();
            System.out.println("sum: " + sum + ", avg: " + sum / 200.0);
        }
        final int dropCount2 = (int)Math.max(0L, Math.round(IC2.random.nextGaussian() * chance * 0.6827 + chance));
        final ItemStack[] ret = new ItemStack[dropCount2];
        for (int i = 0; i < dropCount2; ++i) {
            ret[i] = this.crop.getGain(this);
            if (ret[i] != null && IC2.random.nextInt(100) <= this.getStatGain()) {
                ret[i] = StackUtil.incSize(ret[i]);
            }
        }
        this.setCurrentSize(this.crop.getSizeAfterHarvest(this));
        this.dirty = true;
        return Arrays.asList(ret);
    }
    
    @Override
    public void reset() {
        this.crop = null;
        this.customData = new NBTTagCompound();
        this.statGain = 0;
        this.statResistance = 0;
        this.statGrowth = 0;
        this.terrainAirQuality = -1;
        this.terrainHumidity = -1;
        this.terrainNutrients = -1;
        this.growthPoints = 0;
        this.scanLevel = 0;
        this.currentSize = 1;
        this.dirty = true;
    }
    
    @Override
    public void updateState() {
        this.getWorld().markBlockRangeForRenderUpdate(this.pos, this.pos);
    }
    
    @Override
    public boolean isBlockBelow(final Block reqBlock) {
        if (this.crop == null) {
            return false;
        }
        final World world = this.getWorld();
        for (int i = 1; i < this.crop.getRootsLength(this); ++i) {
            final BlockPos blockPos = this.pos.down(i);
            final IBlockState state = world.getBlockState(blockPos);
            final Block block = state.getBlock();
            if (block.isAir(state, (IBlockAccess)world, blockPos)) {
                return false;
            }
            if (block == reqBlock) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isBlockBelow(final String oreDictionaryEntry) {
        if (this.crop == null) {
            return false;
        }
        final World world = this.getWorld();
        for (int i = 1; i < this.crop.getRootsLength(this); ++i) {
            final BlockPos blockPos = this.pos.down(i);
            final IBlockState state = world.getBlockState(blockPos);
            final Block block = state.getBlock();
            if (block.isAir(state, (IBlockAccess)world, blockPos)) {
                return false;
            }
            final ItemStack stackBelow = StackUtil.getPickStack(world, blockPos, state, Ic2Player.get(world));
            for (final ItemStack stack : OreDictionary.getOres(oreDictionaryEntry)) {
                if (StackUtil.checkItemEquality(stackBelow, stack)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public ItemStack generateSeeds(final CropCard crop, final int growth, final int gain, final int resistance, final int scan) {
        return ItemCropSeed.generateItemStackFromValues(crop, growth, gain, resistance, scan);
    }
    
    @Override
    protected int getLightOpacity() {
        return 0;
    }
    
    public Ic2BlockState.Ic2BlockStateInstance getExtendedState(Ic2BlockState.Ic2BlockStateInstance state) {
        state = super.getExtendedState(state);
        final CropRenderState renderState = this.cropRenderState;
        if (renderState != null) {
            state = state.withProperties(TileEntityCrop.renderStateProperty, renderState);
        }
        return state;
    }
    
    private void updateRenderState() {
        this.cropRenderState = new CropRenderState(this.crop, this.getCurrentSize(), this.crossingBase);
    }
    
    public boolean wrenchCanRemove(final EntityPlayer player) {
        return false;
    }
    
    @Override
    protected ItemStack getPickBlock(final EntityPlayer player, final RayTraceResult target) {
        if (this.crop == null) {
            return ItemName.crop_stick.getItemStack();
        }
        return this.generateSeeds(this.crop, this.statGrowth, this.statGain, this.statResistance, this.scanLevel);
    }
    
    private boolean attemptCrossing() {
        if (IC2.random.nextInt(3) != 0) {
            return false;
        }
        final List<TileEntityCrop> cropTes = new ArrayList<TileEntityCrop>(4);
        this.askCropJoinCross(this.pos.north(), cropTes);
        this.askCropJoinCross(this.pos.south(), cropTes);
        this.askCropJoinCross(this.pos.east(), cropTes);
        this.askCropJoinCross(this.pos.west(), cropTes);
        if (TileEntityCrop.debug) {
            System.out.print("Attempted cross with " + cropTes.size() + " plants: ");
            for (int i = 0; i < cropTes.size(); ++i) {
                System.out.print(cropTes.get(i).getCrop().getUnlocalizedName() + " ");
            }
            System.out.println();
        }
        if (cropTes.size() < 2) {
            return false;
        }
        final CropCard[] crops = Crops.instance.getCrops().toArray(new CropCard[0]);
        if (crops.length == 0) {
            return false;
        }
        final int[] ratios = new int[crops.length];
        int total = 0;
        for (int j = 0; j < ratios.length; ++j) {
            final CropCard crop = crops[j];
            if (crop.canGrow(this)) {
                for (final TileEntityCrop te : cropTes) {
                    total += this.calculateRatioFor(crop, te.getCrop());
                }
            }
            ratios[j] = total;
        }
        if (TileEntityCrop.debug) {
            int lastChance = 0;
            for (int k = 0; k < crops.length; ++k) {
                final int currentChance = ratios[k];
                System.out.printf("%s: %.1f%% %d%n", crops[k].getUnlocalizedName(), (currentChance - lastChance) * 100.0 / total, ratios[k]);
                lastChance = currentChance;
            }
        }
        final int search = IC2.random.nextInt(total);
        if (TileEntityCrop.debug) {
            System.out.printf("rnd: %d / %d%n", search, total);
        }
        int min = 0;
        int max = ratios.length - 1;
        while (min < max) {
            final int cur = (min + max) / 2;
            final int value = ratios[cur];
            if (TileEntityCrop.debug) {
                System.out.printf("min: %d, max: %d, cur: %d, value: %d%n", min, max, cur, value);
            }
            if (search < value) {
                max = cur;
            }
            else {
                min = cur + 1;
            }
        }
        if (TileEntityCrop.debug) {
            System.out.printf("result: %s (%d %d)%n", crops[min].getUnlocalizedName(), min, max);
        }
        assert min == max;
        assert min >= 0 && min < ratios.length;
        assert ratios[min] > search;
        assert ratios[min - 1] <= search;
        this.setCrossingBase(false);
        this.crop = crops[min];
        this.dirty = true;
        this.setCurrentSize(1);
        this.statGrowth = 0;
        this.statResistance = 0;
        this.statGain = 0;
        for (final TileEntityCrop te2 : cropTes) {
            this.statGrowth += te2.statGrowth;
            this.statResistance += te2.statResistance;
            this.statGain += te2.statGain;
        }
        final int count = cropTes.size();
        this.statGrowth /= (byte)count;
        this.statResistance /= (byte)count;
        this.statGain /= (byte)count;
        this.statGrowth += (byte)(IC2.random.nextInt(1 + 2 * count) - count);
        this.statGain += (byte)(IC2.random.nextInt(1 + 2 * count) - count);
        this.statResistance += (byte)(IC2.random.nextInt(1 + 2 * count) - count);
        this.statGrowth = (byte)Util.limit(this.statGrowth, 0, 31);
        this.statGain = (byte)Util.limit(this.statGain, 0, 31);
        this.statResistance = (byte)Util.limit(this.statResistance, 0, 31);
        return true;
    }
    
    private int calculateRatioFor(final CropCard newCrop, final CropCard oldCrop) {
        if (newCrop == oldCrop) {
            return 500;
        }
        int value = 0;
        final int[] propOld = oldCrop.getProperties().getAllProperties();
        final int[] propNew = newCrop.getProperties().getAllProperties();
        assert propOld.length == propNew.length;
        for (int i = 0; i < 5; ++i) {
            final int delta = Math.abs(propOld[i] - propNew[i]);
            value += -delta + 2;
        }
        for (final String attributeNew : newCrop.getAttributes()) {
            for (final String attributeOld : oldCrop.getAttributes()) {
                if (attributeNew.equalsIgnoreCase(attributeOld)) {
                    value += 5;
                }
            }
        }
        final int diff = newCrop.getProperties().getTier() - oldCrop.getProperties().getTier();
        if (diff > 1) {
            value -= 2 * diff;
        }
        if (diff < -3) {
            value -= -diff;
        }
        return Math.max(value, 0);
    }
    
    private void askCropJoinCross(final BlockPos pos, final List<TileEntityCrop> crops) {
        final TileEntity te = this.getWorld().getTileEntity(pos);
        if (!(te instanceof TileEntityCrop)) {
            return;
        }
        final TileEntityCrop sideCrop = (TileEntityCrop)te;
        final CropCard neighborCrop = sideCrop.getCrop();
        if (neighborCrop == null) {
            return;
        }
        if (!neighborCrop.canGrow(this) || !neighborCrop.canCross(sideCrop)) {
            return;
        }
        int base = 4;
        if (sideCrop.statGrowth >= 16) {
            ++base;
        }
        if (sideCrop.statGrowth >= 30) {
            ++base;
        }
        if (sideCrop.statResistance >= 28) {
            base += 27 - sideCrop.statResistance;
        }
        if (base >= IC2.random.nextInt(20)) {
            crops.add(sideCrop);
        }
    }
    
    @Override
    protected void onNeighborChange(final Block neighbor, final BlockPos neighborPos) {
        super.onNeighborChange(neighbor, neighborPos);
        final World world = this.getWorld();
        if (!CropSoilType.contais(world.getBlockState(this.pos.down()).getBlock())) {
            this.pick();
            world.setBlockToAir(this.pos);
        }
    }
    
    public boolean applyHydration(final IFluidHandler handler) {
        final int limit = 200;
        if (this.storageWater >= limit) {
            return false;
        }
        final FluidStack stack = handler.drain(new FluidStack(FluidRegistry.WATER, limit - this.storageWater), true);
        if (stack == null || stack.amount <= 0) {
            return false;
        }
        this.storageWater += (short)stack.amount;
        return true;
    }
    
    public boolean applyWeedEx(final IFluidHandler handler, final boolean manual) {
        final int limit = manual ? 100 : 150;
        if (this.storageWeedEX >= limit) {
            return false;
        }
        final FluidStack stack = handler.drain(new FluidStack(FluidName.weed_ex.getInstance(), limit - this.storageWeedEX), true);
        if (stack == null || stack.amount <= 0) {
            return false;
        }
        this.storageWeedEX += (short)stack.amount;
        return true;
    }
    
    public boolean applyFertilizer(final boolean manual) {
        if (this.storageNutrients >= 100) {
            return false;
        }
        this.storageNutrients += (short)(manual ? 100 : 90);
        return true;
    }
    
    static {
        TileEntityCrop.tickRate = 256;
        renderStateProperty = (IUnlistedProperty)new UnlistedProperty("renderstate", CropRenderState.class);
        debug = (System.getProperty("ic2.crops.debug") != null);
        debugGrowth = (TileEntityCrop.debug && System.getProperty("ic2.crops.debug").contains("growth"));
        debugWeedWork = (TileEntityCrop.debug && System.getProperty("ic2.crops.debug").contains("weedwork"));
        debugCollision = (TileEntityCrop.debug && System.getProperty("ic2.crops.debug").contains("collision"));
        debugTerrain = (TileEntityCrop.debug && System.getProperty("ic2.crops.debug").contains("terrain"));
    }
    
    public static class CropRenderState
    {
        public final CropCard crop;
        public final int size;
        public final boolean crosscrop;
        
        public CropRenderState(final CropCard crop, final int size, final boolean crosscrop) {
            this.crop = crop;
            this.size = size;
            this.crosscrop = crosscrop;
        }
        
        @Override
        public int hashCode() {
            int ret = (this.crop != null) ? this.crop.hashCode() : 1;
            ret = ret * 31 + (this.size + 1) * 5;
            ret = ret * 31 + (this.crosscrop ? 1 : 0);
            return ret;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof CropRenderState)) {
                return false;
            }
            final CropRenderState crs = (CropRenderState)obj;
            return crs.crop == this.crop && crs.size == this.size && crs.crosscrop == this.crosscrop;
        }
        
        @Override
        public String toString() {
            return "CropState<" + this.crop + ", " + this.size + ", " + this.crosscrop + '>';
        }
    }
}
