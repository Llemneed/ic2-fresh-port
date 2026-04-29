// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core.crop;

import ic2.core.crop.cropcard.CropBeetroot;
import ic2.core.crop.cropcard.CropEating;
import ic2.core.crop.cropcard.CropPotato;
import ic2.core.crop.cropcard.CropCarrots;
import ic2.core.crop.cropcard.CropHops;
import ic2.core.crop.cropcard.CropCoffee;
import ic2.core.crop.cropcard.CropRedWheat;
import ic2.core.crop.cropcard.CropBaseMetalUncommon;
import ic2.core.crop.cropcard.CropBaseMetalCommon;
import ic2.core.item.type.DustResourceType;
import ic2.core.crop.cropcard.CropTerraWart;
import ic2.core.crop.cropcard.CropNetherWart;
import ic2.core.crop.cropcard.CropBaseMushroom;
import ic2.core.crop.cropcard.CropCocoa;
import ic2.core.crop.cropcard.CropStickreed;
import ic2.core.crop.cropcard.CropReed;
import ic2.core.crop.cropcard.CropVenomilia;
import ic2.core.crop.cropcard.CropColorFlower;
import ic2.core.crop.cropcard.CropMelon;
import ic2.core.crop.cropcard.CropPumpkin;
import ic2.core.crop.cropcard.CropWheat;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.function.BiConsumer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.AbstractCollection;
import java.util.Collection;
import net.minecraft.nbt.NBTTagCompound;
import java.util.Iterator;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import ic2.core.item.type.CropResItemType;
import ic2.core.ref.ItemName;
import net.minecraft.init.Items;
import ic2.core.crop.cropcard.CropWeed;
import java.util.HashMap;
import java.util.IdentityHashMap;
import ic2.api.crops.CropCard;
import ic2.api.crops.BaseSeed;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.BiomeDictionary;
import java.util.Map;
import ic2.api.crops.Crops;

public class IC2Crops extends Crops
{
    private final Map<BiomeDictionary.Type, Integer> humidityBiomeTypeBonus;
    private final Map<BiomeDictionary.Type, Integer> nutrientBiomeTypeBonus;
    private final Map<ItemStack, BaseSeed> baseSeeds;
    public static CropCard cropWheat;
    public static CropCard cropPumpkin;
    public static CropCard cropMelon;
    public static CropCard cropYellowFlower;
    public static CropCard cropRedFlower;
    public static CropCard cropBlackFlower;
    public static CropCard cropPurpleFlower;
    public static CropCard cropBlueFlower;
    public static CropCard cropVenomilia;
    public static CropCard cropReed;
    public static CropCard cropStickReed;
    public static CropCard cropCocoa;
    public static CropCard cropRedMushroom;
    public static CropCard cropBrownMushroom;
    public static CropCard cropNetherWart;
    public static CropCard cropTerraWart;
    public static CropCard cropFerru;
    public static CropCard cropCyprium;
    public static CropCard cropStagnium;
    public static CropCard cropPlumbiscus;
    public static CropCard cropAurelia;
    public static CropCard cropShining;
    public static CropCard cropRedwheat;
    public static CropCard cropCoffee;
    public static CropCard cropHops;
    public static CropCard cropCarrots;
    public static CropCard cropPotato;
    public static CropCard cropEatingPlant;
    public static CropCard cropBeetroot;
    static boolean needsToPost;
    private final Map<String, Map<String, CropCard>> cropMap;
    
    public IC2Crops() {
        this.humidityBiomeTypeBonus = new IdentityHashMap<BiomeDictionary.Type, Integer>();
        this.nutrientBiomeTypeBonus = new IdentityHashMap<BiomeDictionary.Type, Integer>();
        this.baseSeeds = new HashMap<ItemStack, BaseSeed>();
        this.cropMap = new HashMap<String, Map<String, CropCard>>();
    }
    
    public static void init() {
        Crops.instance = new IC2Crops();
        Crops.weed = new CropWeed();
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.JUNGLE, 10);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.SWAMP, 10);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.MUSHROOM, 5);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.FOREST, 5);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.RIVER, 2);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.PLAINS, 0);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.SAVANNA, -2);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.HILLS, -5);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.MOUNTAIN, -5);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.WASTELAND, -8);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.END, -10);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.NETHER, -10);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.DEAD, -10);
        registerCrops();
        registerBaseSeeds();
    }
    
    public static void registerCrops() {
        Crops.instance.registerCrop(IC2Crops.weed);
        Crops.instance.registerCrop(IC2Crops.cropWheat);
        Crops.instance.registerCrop(IC2Crops.cropPumpkin);
        Crops.instance.registerCrop(IC2Crops.cropMelon);
        Crops.instance.registerCrop(IC2Crops.cropYellowFlower);
        Crops.instance.registerCrop(IC2Crops.cropRedFlower);
        Crops.instance.registerCrop(IC2Crops.cropBlackFlower);
        Crops.instance.registerCrop(IC2Crops.cropPurpleFlower);
        Crops.instance.registerCrop(IC2Crops.cropBlueFlower);
        Crops.instance.registerCrop(IC2Crops.cropVenomilia);
        Crops.instance.registerCrop(IC2Crops.cropReed);
        Crops.instance.registerCrop(IC2Crops.cropStickReed);
        Crops.instance.registerCrop(IC2Crops.cropCocoa);
        Crops.instance.registerCrop(IC2Crops.cropFerru);
        Crops.instance.registerCrop(IC2Crops.cropAurelia);
        Crops.instance.registerCrop(IC2Crops.cropRedwheat);
        Crops.instance.registerCrop(IC2Crops.cropNetherWart);
        Crops.instance.registerCrop(IC2Crops.cropTerraWart);
        Crops.instance.registerCrop(IC2Crops.cropCoffee);
        Crops.instance.registerCrop(IC2Crops.cropHops);
        Crops.instance.registerCrop(IC2Crops.cropCarrots);
        Crops.instance.registerCrop(IC2Crops.cropPotato);
        Crops.instance.registerCrop(IC2Crops.cropRedMushroom);
        Crops.instance.registerCrop(IC2Crops.cropBrownMushroom);
        Crops.instance.registerCrop(IC2Crops.cropEatingPlant);
        Crops.instance.registerCrop(IC2Crops.cropCyprium);
        Crops.instance.registerCrop(IC2Crops.cropStagnium);
        Crops.instance.registerCrop(IC2Crops.cropPlumbiscus);
        Crops.instance.registerCrop(IC2Crops.cropShining);
        Crops.instance.registerCrop(IC2Crops.cropBeetroot);
    }
    
    public static void registerBaseSeeds() {
        Crops.instance.registerBaseSeed(new ItemStack(Items.WHEAT_SEEDS, 1, 32767), IC2Crops.cropWheat, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack(Items.PUMPKIN_SEEDS, 1, 32767), IC2Crops.cropPumpkin, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack(Items.MELON_SEEDS, 1, 32767), IC2Crops.cropMelon, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack(Items.NETHER_WART, 1, 32767), IC2Crops.cropNetherWart, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(ItemName.terra_wart.getItemStack(), IC2Crops.cropTerraWart, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(ItemName.crop_res.getItemStack(CropResItemType.coffee_beans), IC2Crops.cropCoffee, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack(Items.REEDS, 1, 32767), IC2Crops.cropReed, 1, 3, 0, 2);
        Crops.instance.registerBaseSeed(new ItemStack(Items.DYE, 1, 3), IC2Crops.cropCocoa, 1, 0, 0, 0);
        Crops.instance.registerBaseSeed(new ItemStack((Block)Blocks.RED_FLOWER, 4, 32767), IC2Crops.cropRedFlower, 4, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack((Block)Blocks.YELLOW_FLOWER, 4, 32767), IC2Crops.cropYellowFlower, 4, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack(Items.CARROT, 1, 32767), IC2Crops.cropCarrots, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack(Items.POTATO, 1, 32767), IC2Crops.cropPotato, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack((Block)Blocks.BROWN_MUSHROOM, 4, 32767), IC2Crops.cropBrownMushroom, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack((Block)Blocks.RED_MUSHROOM, 4, 32767), IC2Crops.cropRedMushroom, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack((Block)Blocks.CACTUS, 1, 32767), IC2Crops.cropEatingPlant, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack(Items.BEETROOT_SEEDS, 1, 32767), IC2Crops.cropBeetroot, 1, 1, 1, 1);
    }
    
    public static void ensureInit() {
        if (IC2Crops.needsToPost) {
            MinecraftForge.EVENT_BUS.post((Event)new CropRegisterEvent());
        }
    }
    
    @Override
    public void addBiomenutrientsBonus(final BiomeDictionary.Type type, final int nutrientsBonus) {
        this.nutrientBiomeTypeBonus.put(type, nutrientsBonus);
    }
    
    @Override
    public void addBiomehumidityBonus(final BiomeDictionary.Type type, final int humidityBonus) {
        this.humidityBiomeTypeBonus.put(type, humidityBonus);
    }
    
    @Override
    public int getHumidityBiomeBonus(final Biome biome) {
        Integer ret = 0;
        for (final BiomeDictionary.Type type : BiomeDictionary.getTypes(biome)) {
            final Integer val = this.humidityBiomeTypeBonus.get(type);
            if (val != null && val > ret) {
                ret = val;
            }
        }
        return ret;
    }
    
    @Override
    public int getNutrientBiomeBonus(final Biome biome) {
        Integer ret = 0;
        for (final BiomeDictionary.Type type : BiomeDictionary.getTypes(biome)) {
            final Integer val = this.nutrientBiomeTypeBonus.get(type);
            if (val != null && val > ret) {
                ret = val;
            }
        }
        return ret;
    }
    
    @Override
    public CropCard getCropCard(final String owner, final String name) {
        final Map<String, CropCard> map = this.cropMap.get(owner);
        if (map == null) {
            return null;
        }
        return map.get(name);
    }
    
    @Override
    public CropCard getCropCard(final ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return null;
        }
        final NBTTagCompound nbt = stack.getTagCompound();
        if (nbt.hasKey("owner") && nbt.hasKey("id")) {
            return this.getCropCard(nbt.getString("owner"), nbt.getString("id"));
        }
        return null;
    }
    
    @Override
    public Collection<CropCard> getCrops() {
        return new AbstractCollection<CropCard>() {
            @Override
            public Iterator<CropCard> iterator() {
                return new Iterator<CropCard>() {
                    private final Iterator<Map<String, CropCard>> mapIterator = IC2Crops.this.cropMap.values().iterator();
                    private Iterator<CropCard> iterator = this.getNextIterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iterator != null && this.iterator.hasNext();
                    }
                    
                    @Override
                    public CropCard next() {
                        if (this.iterator == null) {
                            throw new NoSuchElementException("no more elements");
                        }
                        final CropCard ret = this.iterator.next();
                        if (!this.iterator.hasNext()) {
                            this.iterator = this.getNextIterator();
                        }
                        return ret;
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("This iterator is read-only.");
                    }
                    
                    private Iterator<CropCard> getNextIterator() {
                        Iterator<CropCard> ret;
                        for (ret = null; this.mapIterator.hasNext() && ret == null; ret = null) {
                            ret = this.mapIterator.next().values().iterator();
                            if (!ret.hasNext()) {}
                        }
                        return ret;
                    }
                };
            }
            
            @Override
            public int size() {
                int ret = 0;
                for (final Map<String, CropCard> map : IC2Crops.this.cropMap.values()) {
                    ret += map.size();
                }
                return ret;
            }
        };
    }
    
    @Override
    public void registerCrop(final CropCard crop) {
        final String owner = crop.getOwner();
        final String id = crop.getId();
        if (!owner.equals(owner.toLowerCase(Locale.ENGLISH))) {
            throw new IllegalArgumentException("The crop owner=" + owner + " id=" + id + " uses a non-lower case owner");
        }
        Map<String, CropCard> map = this.cropMap.get(owner);
        if (map == null) {
            map = new HashMap<String, CropCard>();
            this.cropMap.put(owner, map);
        }
        final CropCard prev = map.put(id, crop);
        if (prev != null) {
            throw new IllegalArgumentException("The crop owner=" + owner + " id=" + id + " uses a non-unique owner+id pair");
        }
    }
    
    @Override
    public boolean registerBaseSeed(final ItemStack stack, final CropCard crop, final int size, final int growth, final int gain, final int resistance) {
        for (final ItemStack key : this.baseSeeds.keySet()) {
            if (key.getItem() == stack.getItem() && key.getItemDamage() == stack.getItemDamage()) {
                return false;
            }
        }
        this.baseSeeds.put(stack, new BaseSeed(crop, size, growth, gain, resistance));
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerCropTextures(final Map<ResourceLocation, TextureAtlasSprite> extraTextures) {
        extraTextures.forEach(CropModel.textures::putIfAbsent);
    }
    
    @Override
    public BaseSeed getBaseSeed(final ItemStack stack) {
        if (stack == null) {
            return null;
        }
        for (final Map.Entry<ItemStack, BaseSeed> entry : this.baseSeeds.entrySet()) {
            final ItemStack key = entry.getKey();
            if (key.getItem() == stack.getItem() && (key.getItemDamage() == 32767 || key.getItemDamage() == stack.getItemDamage())) {
                return this.baseSeeds.get(key);
            }
        }
        return null;
    }
    
    static {
        IC2Crops.cropWheat = new CropWheat();
        IC2Crops.cropPumpkin = new CropPumpkin();
        IC2Crops.cropMelon = new CropMelon();
        IC2Crops.cropYellowFlower = new CropColorFlower("dandelion", new String[] { "Yellow", "Flower" }, 11);
        IC2Crops.cropRedFlower = new CropColorFlower("rose", new String[] { "Red", "Flower", "Rose" }, 1);
        IC2Crops.cropBlackFlower = new CropColorFlower("blackthorn", new String[] { "Black", "Flower", "Rose" }, 0);
        IC2Crops.cropPurpleFlower = new CropColorFlower("tulip", new String[] { "Purple", "Flower", "Tulip" }, 5);
        IC2Crops.cropBlueFlower = new CropColorFlower("cyazint", new String[] { "Blue", "Flower" }, 6);
        IC2Crops.cropVenomilia = new CropVenomilia();
        IC2Crops.cropReed = new CropReed();
        IC2Crops.cropStickReed = new CropStickreed();
        IC2Crops.cropCocoa = new CropCocoa();
        IC2Crops.cropRedMushroom = new CropBaseMushroom("red_mushroom", new String[] { "Red", "Food", "Mushroom" }, new ItemStack((Block)Blocks.RED_MUSHROOM));
        IC2Crops.cropBrownMushroom = new CropBaseMushroom("brown_mushroom", new String[] { "Brown", "Food", "Mushroom" }, new ItemStack((Block)Blocks.BROWN_MUSHROOM));
        IC2Crops.cropNetherWart = new CropNetherWart();
        IC2Crops.cropTerraWart = new CropTerraWart();
        IC2Crops.cropFerru = new CropBaseMetalCommon("ferru", new String[] { "Gray", "Leaves", "Metal" }, new String[] { "oreIron", "blockIron" }, ItemName.dust.getItemStack(DustResourceType.small_iron));
        IC2Crops.cropCyprium = new CropBaseMetalCommon("cyprium", new String[] { "Orange", "Leaves", "Metal" }, new String[] { "oreCopper", "blockCopper" }, ItemName.dust.getItemStack(DustResourceType.small_copper));
        IC2Crops.cropStagnium = new CropBaseMetalCommon("stagnium", new String[] { "Shiny", "Leaves", "Metal" }, new String[] { "oreTin", "blockTin" }, ItemName.dust.getItemStack(DustResourceType.small_tin));
        IC2Crops.cropPlumbiscus = new CropBaseMetalCommon("plumbiscus", new String[] { "Dense", "Leaves", "Metal" }, new String[] { "oreLead", "blockLead" }, ItemName.dust.getItemStack(DustResourceType.small_lead));
        IC2Crops.cropAurelia = new CropBaseMetalUncommon("aurelia", new String[] { "Gold", "Leaves", "Metal" }, new String[] { "oreGold", "blockGold" }, ItemName.dust.getItemStack(DustResourceType.small_gold));
        IC2Crops.cropShining = new CropBaseMetalUncommon("shining", new String[] { "Silver", "Leaves", "Metal" }, new String[] { "oreSilver", "blockSilver" }, ItemName.dust.getItemStack(DustResourceType.small_silver));
        IC2Crops.cropRedwheat = new CropRedWheat();
        IC2Crops.cropCoffee = new CropCoffee();
        IC2Crops.cropHops = new CropHops();
        IC2Crops.cropCarrots = new CropCarrots();
        IC2Crops.cropPotato = new CropPotato();
        IC2Crops.cropEatingPlant = new CropEating();
        IC2Crops.cropBeetroot = new CropBeetroot();
        IC2Crops.needsToPost = true;
    }
}
