// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core.recipe;

import net.minecraftforge.oredict.OreDictionary;
import net.minecraft.item.ItemStack;
import ic2.core.item.type.OreResourceType;
import ic2.core.item.type.PlateResourceType;
import ic2.core.item.type.IngotResourceType;
import ic2.core.item.type.CraftingItemType;
import ic2.core.item.type.MiscResourceType;
import ic2.core.item.type.DustResourceType;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import ic2.core.block.type.ResourceBlock;
import ic2.core.ref.BlockName;

public class OreDictionaryEntries
{
    public static void load() {
        add("oreCopper", BlockName.resource.getItemStack(ResourceBlock.copper_ore));
        add("oreLead", BlockName.resource.getItemStack(ResourceBlock.lead_ore));
        add("oreTin", BlockName.resource.getItemStack(ResourceBlock.tin_ore));
        add("oreUranium", BlockName.resource.getItemStack(ResourceBlock.uranium_ore));
        add("treeLeaves", StackUtil.copyWithWildCard(BlockName.leaves.getItemStack()));
        add("treeSapling", StackUtil.copyWithWildCard(BlockName.sapling.getItemStack()));
        add("dustStone", ItemName.dust.getItemStack(DustResourceType.stone));
        add("dustBronze", ItemName.dust.getItemStack(DustResourceType.bronze));
        add("dustClay", ItemName.dust.getItemStack(DustResourceType.clay));
        add("dustCoal", ItemName.dust.getItemStack(DustResourceType.coal));
        add("dustCopper", ItemName.dust.getItemStack(DustResourceType.copper));
        add("dustGold", ItemName.dust.getItemStack(DustResourceType.gold));
        add("dustIron", ItemName.dust.getItemStack(DustResourceType.iron));
        add("dustSilver", ItemName.dust.getItemStack(DustResourceType.silver));
        add("dustTin", ItemName.dust.getItemStack(DustResourceType.tin));
        add("dustLead", ItemName.dust.getItemStack(DustResourceType.lead));
        add("dustObsidian", ItemName.dust.getItemStack(DustResourceType.obsidian));
        add("dustLapis", ItemName.dust.getItemStack(DustResourceType.lapis));
        add("dustSulfur", ItemName.dust.getItemStack(DustResourceType.sulfur));
        add("dustLithium", ItemName.dust.getItemStack(DustResourceType.lithium));
        add("dustDiamond", ItemName.dust.getItemStack(DustResourceType.diamond));
        add("dustSiliconDioxide", ItemName.dust.getItemStack(DustResourceType.silicon_dioxide));
        add("dustHydratedCoal", ItemName.dust.getItemStack(DustResourceType.coal_fuel));
        add("dustAshes", ItemName.misc_resource.getItemStack(MiscResourceType.ashes));
        add("itemSlag", ItemName.misc_resource.getItemStack(MiscResourceType.slag));
        add("dustTinyCopper", ItemName.dust.getItemStack(DustResourceType.small_copper));
        add("dustTinyGold", ItemName.dust.getItemStack(DustResourceType.small_gold));
        add("dustTinyIron", ItemName.dust.getItemStack(DustResourceType.small_iron));
        add("dustTinySilver", ItemName.dust.getItemStack(DustResourceType.small_silver));
        add("dustTinyTin", ItemName.dust.getItemStack(DustResourceType.small_tin));
        add("dustTinyLead", ItemName.dust.getItemStack(DustResourceType.small_lead));
        add("dustTinySulfur", ItemName.dust.getItemStack(DustResourceType.small_sulfur));
        add("dustTinyLithium", ItemName.dust.getItemStack(DustResourceType.small_lithium));
        add("dustTinyBronze", ItemName.dust.getItemStack(DustResourceType.small_bronze));
        add("dustTinyLapis", ItemName.dust.getItemStack(DustResourceType.small_lapis));
        add("dustTinyObsidian", ItemName.dust.getItemStack(DustResourceType.small_obsidian));
        add("itemRubber", ItemName.crafting.getItemStack(CraftingItemType.rubber));
        add("ingotBronze", ItemName.ingot.getItemStack(IngotResourceType.bronze));
        add("ingotCopper", ItemName.ingot.getItemStack(IngotResourceType.copper));
        add("ingotSteel", ItemName.ingot.getItemStack(IngotResourceType.steel));
        add("ingotLead", ItemName.ingot.getItemStack(IngotResourceType.lead));
        add("ingotTin", ItemName.ingot.getItemStack(IngotResourceType.tin));
        add("ingotSilver", ItemName.ingot.getItemStack(IngotResourceType.silver));
        add("ingotRefinedIron", ItemName.ingot.getItemStack(IngotResourceType.refined_iron));
        add("ingotUranium", ItemName.ingot.getItemStack(IngotResourceType.uranium));
        add("plateIron", ItemName.plate.getItemStack(PlateResourceType.iron));
        add("plateGold", ItemName.plate.getItemStack(PlateResourceType.gold));
        add("plateCopper", ItemName.plate.getItemStack(PlateResourceType.copper));
        add("plateTin", ItemName.plate.getItemStack(PlateResourceType.tin));
        add("plateLead", ItemName.plate.getItemStack(PlateResourceType.lead));
        add("plateLapis", ItemName.plate.getItemStack(PlateResourceType.lapis));
        add("plateObsidian", ItemName.plate.getItemStack(PlateResourceType.obsidian));
        add("plateBronze", ItemName.plate.getItemStack(PlateResourceType.bronze));
        add("plateSteel", ItemName.plate.getItemStack(PlateResourceType.steel));
        add("plateDenseSteel", ItemName.plate.getItemStack(PlateResourceType.dense_steel));
        add("plateDenseIron", ItemName.plate.getItemStack(PlateResourceType.dense_iron));
        add("plateDenseGold", ItemName.plate.getItemStack(PlateResourceType.dense_gold));
        add("plateDenseCopper", ItemName.plate.getItemStack(PlateResourceType.dense_copper));
        add("plateDenseTin", ItemName.plate.getItemStack(PlateResourceType.dense_tin));
        add("plateDenseLead", ItemName.plate.getItemStack(PlateResourceType.dense_lead));
        add("plateDenseLapis", ItemName.plate.getItemStack(PlateResourceType.dense_lapis));
        add("plateDenseObsidian", ItemName.plate.getItemStack(PlateResourceType.dense_obsidian));
        add("plateDenseBronze", ItemName.plate.getItemStack(PlateResourceType.dense_bronze));
        add("crushedIron", ItemName.crushed.getItemStack(OreResourceType.iron));
        add("crushedGold", ItemName.crushed.getItemStack(OreResourceType.gold));
        add("crushedSilver", ItemName.crushed.getItemStack(OreResourceType.silver));
        add("crushedLead", ItemName.crushed.getItemStack(OreResourceType.lead));
        add("crushedCopper", ItemName.crushed.getItemStack(OreResourceType.copper));
        add("crushedTin", ItemName.crushed.getItemStack(OreResourceType.tin));
        add("crushedUranium", ItemName.crushed.getItemStack(OreResourceType.uranium));
        add("crushedPurifiedIron", ItemName.purified.getItemStack(OreResourceType.iron));
        add("crushedPurifiedGold", ItemName.purified.getItemStack(OreResourceType.gold));
        add("crushedPurifiedSilver", ItemName.purified.getItemStack(OreResourceType.silver));
        add("crushedPurifiedLead", ItemName.purified.getItemStack(OreResourceType.lead));
        add("crushedPurifiedCopper", ItemName.purified.getItemStack(OreResourceType.copper));
        add("crushedPurifiedTin", ItemName.purified.getItemStack(OreResourceType.tin));
        add("crushedPurifiedUranium", ItemName.purified.getItemStack(OreResourceType.uranium));
        add("blockBronze", BlockName.resource.getItemStack(ResourceBlock.bronze_block));
        add("blockCopper", BlockName.resource.getItemStack(ResourceBlock.copper_block));
        add("blockTin", BlockName.resource.getItemStack(ResourceBlock.tin_block));
        add("blockUranium", BlockName.resource.getItemStack(ResourceBlock.uranium_block));
        add("blockLead", BlockName.resource.getItemStack(ResourceBlock.lead_block));
        add("blockSilver", BlockName.resource.getItemStack(ResourceBlock.silver_block));
        add("blockSteel", BlockName.resource.getItemStack(ResourceBlock.steel_block));
        add("circuitBasic", ItemName.crafting.getItemStack(CraftingItemType.circuit));
        add("circuitAdvanced", ItemName.crafting.getItemStack(CraftingItemType.advanced_circuit));
        add("gemDiamond", ItemName.crafting.getItemStack(CraftingItemType.industrial_diamond));
        add("craftingToolForgeHammer", StackUtil.copyWithWildCard(ItemName.forge_hammer.getItemStack()));
        add("craftingToolWireCutter", StackUtil.copyWithWildCard(ItemName.cutter.getItemStack()));
    }
    
    private static void add(final String name, final ItemStack stack) {
        if (name == null) {
            throw new NullPointerException("null name for stack " + StackUtil.toStringSafe(stack));
        }
        if (StackUtil.isEmpty(stack)) {
            throw new IllegalArgumentException("invalid stack for " + name + ": " + StackUtil.toStringSafe(stack));
        }
        OreDictionary.registerOre(name, stack);
    }
}
