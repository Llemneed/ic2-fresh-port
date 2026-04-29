// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core.crop;

import java.util.HashMap;
import net.minecraft.client.Minecraft;
import ic2.core.model.BasicBakedBlockModel;
import java.util.Collections;
import java.util.Set;
import ic2.core.model.VdUtil;
import java.util.EnumSet;
import java.util.ArrayList;
import net.minecraftforge.common.property.IUnlistedProperty;
import ic2.core.block.state.Ic2BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import java.util.List;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraftforge.common.model.IModelState;
import java.util.Iterator;
import ic2.api.crops.CropCard;
import net.minecraftforge.fml.common.eventhandler.Event;
import ic2.api.crops.Crops;
import net.minecraftforge.common.MinecraftForge;
import java.util.Collection;
import com.google.common.cache.CacheLoader;
import java.util.concurrent.TimeUnit;
import com.google.common.cache.CacheBuilder;
import net.minecraft.client.renderer.block.model.IBakedModel;
import com.google.common.cache.LoadingCache;
import java.util.Map;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import java.util.function.Function;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ic2.core.model.AbstractModel;

@SideOnly(Side.CLIENT)
public class CropModel extends AbstractModel
{
    private static final ResourceLocation STICK;
    private static final Function<ResourceLocation, TextureAtlasSprite> MISSING;
    static Map<ResourceLocation, TextureAtlasSprite> textures;
    private final LoadingCache<TileEntityCrop.CropRenderState, IBakedModel> modelCache;
    
    public CropModel() {
        this.modelCache = (LoadingCache<TileEntityCrop.CropRenderState, IBakedModel>)CacheBuilder.newBuilder().maximumSize(256L).expireAfterAccess(5L, TimeUnit.MINUTES).build((CacheLoader)new CacheLoader<TileEntityCrop.CropRenderState, IBakedModel>() {
            public IBakedModel load(final TileEntityCrop.CropRenderState key) throws Exception {
                if (key.crop == null || key.size <= 0) {
                    return CropModel.this.generateStickModel(key.crosscrop);
                }
                return CropModel.this.generateModel(key);
            }
        });
    }
    
    @Override
    public Collection<ResourceLocation> getTextures() {
        if (CropModel.textures.isEmpty()) {
            IC2Crops.needsToPost = false;
            MinecraftForge.EVENT_BUS.post((Event)new Crops.CropRegisterEvent());
            for (final CropCard crop : Crops.instance.getCrops()) {
                for (final ResourceLocation aux : crop.getTexturesLocation()) {
                    CropModel.textures.put(aux, null);
                }
            }
            CropModel.textures.put(CropModel.STICK, null);
        }
        return CropModel.textures.keySet();
    }
    
    @Override
    public IBakedModel bake(final IModelState state, final VertexFormat format, final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        for (final Map.Entry<ResourceLocation, TextureAtlasSprite> entry : CropModel.textures.entrySet()) {
            entry.setValue(bakedTextureGetter.apply(entry.getKey()));
        }
        return (IBakedModel)this;
    }
    
    private static ResourceLocation getTextureLocation(final CropCard crop, final int currentSize) {
        return crop.getTexturesLocation().get(currentSize - 1);
    }
    
    @Override
    public List<BakedQuad> getQuads(final IBlockState rawState, final EnumFacing side, final long rand) {
        final Ic2BlockState.Ic2BlockStateInstance state;
        TileEntityCrop.CropRenderState prop;
        if (rawState instanceof Ic2BlockState.Ic2BlockStateInstance && (state = (Ic2BlockState.Ic2BlockStateInstance)rawState).hasValue(TileEntityCrop.renderStateProperty)) {
            prop = state.getValue(TileEntityCrop.renderStateProperty);
        }
        else {
            prop = new TileEntityCrop.CropRenderState(null, 0, false);
        }
        try {
            return this.modelCache.get(prop).getQuads(rawState, side, rand);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    IBakedModel generateModel(final TileEntityCrop.CropRenderState prop) {
        List<BakedQuad>[] faceQuads = (List<BakedQuad>[])new List[EnumFacing.VALUES.length];
        for (int i = 0; i < faceQuads.length; ++i) {
            faceQuads[i] = new ArrayList<BakedQuad>();
        }
        List<BakedQuad> generalQuads = new ArrayList<BakedQuad>();
        final TextureAtlasSprite sprite_stick = CropModel.textures.get(CropModel.STICK);
        final TextureAtlasSprite sprite_crop = CropModel.textures.computeIfAbsent(getTextureLocation(prop.crop, prop.size), CropModel.MISSING);
        for (final EnumFacing facing : EnumFacing.VALUES) {
            VdUtil.addCuboid(0.2f, 0.0f, 0.2f, 0.25f, 0.85f, 0.25f, EnumSet.of(facing), sprite_stick, faceQuads, generalQuads);
            VdUtil.addCuboid(0.75f, 0.0f, 0.2f, 0.8f, 0.85f, 0.25f, EnumSet.of(facing), sprite_stick, faceQuads, generalQuads);
            VdUtil.addCuboid(0.2f, 0.0f, 0.75f, 0.25f, 0.85f, 0.8f, EnumSet.of(facing), sprite_stick, faceQuads, generalQuads);
            VdUtil.addCuboid(0.75f, 0.0f, 0.75f, 0.8f, 0.85f, 0.8f, EnumSet.of(facing), sprite_stick, faceQuads, generalQuads);
            if (prop.crosscrop) {
                VdUtil.addCuboid(0.05f, 0.65f, 0.2f, 0.95f, 0.7f, 0.25f, EnumSet.of(facing), sprite_stick, faceQuads, generalQuads);
                VdUtil.addCuboid(0.2f, 0.65f, 0.05f, 0.25f, 0.7f, 0.95f, EnumSet.of(facing), sprite_stick, faceQuads, generalQuads);
                VdUtil.addCuboid(0.05f, 0.65f, 0.75f, 0.95f, 0.7f, 0.8f, EnumSet.of(facing), sprite_stick, faceQuads, generalQuads);
                VdUtil.addCuboid(0.75f, 0.65f, 0.05f, 0.8f, 0.7f, 0.95f, EnumSet.of(facing), sprite_stick, faceQuads, generalQuads);
            }
            if (prop.crop != null) {
                VdUtil.addFlippedCuboid(0.0f, 0.0f, 0.225f, 1.0f, 1.0f, 0.225f, EnumSet.of(facing), sprite_crop, faceQuads, generalQuads);
                VdUtil.addFlippedCuboid(0.225f, 0.0f, 0.0f, 0.225f, 1.0f, 1.0f, EnumSet.of(facing), sprite_crop, faceQuads, generalQuads);
                VdUtil.addFlippedCuboid(0.0f, 0.0f, 0.775f, 1.0f, 1.0f, 0.775f, EnumSet.of(facing), sprite_crop, faceQuads, generalQuads);
                VdUtil.addFlippedCuboid(0.775f, 0.0f, 0.0f, 0.775f, 1.0f, 1.0f, EnumSet.of(facing), sprite_crop, faceQuads, generalQuads);
            }
        }
        int used = 0;
        for (int j = 0; j < faceQuads.length; ++j) {
            if (faceQuads[j].isEmpty()) {
                faceQuads[j] = Collections.emptyList();
            }
            else {
                ++used;
            }
        }
        if (used == 0) {
            faceQuads = null;
        }
        if (generalQuads.isEmpty()) {
            generalQuads = Collections.emptyList();
        }
        return (IBakedModel)new BasicBakedBlockModel(faceQuads, generalQuads, sprite_stick);
    }
    
    IBakedModel generateStickModel(final boolean crosscrop) {
        List<BakedQuad>[] faceQuads = (List<BakedQuad>[])new List[EnumFacing.VALUES.length];
        for (int i = 0; i < faceQuads.length; ++i) {
            faceQuads[i] = new ArrayList<BakedQuad>();
        }
        List<BakedQuad> generalQuads = new ArrayList<BakedQuad>();
        final TextureAtlasSprite sprite_stick = CropModel.textures.get(CropModel.STICK);
        for (final EnumFacing facing : EnumFacing.VALUES) {
            VdUtil.addCuboid(0.2f, 0.0f, 0.2f, 0.25f, 0.85f, 0.25f, EnumSet.of(facing), sprite_stick, faceQuads, generalQuads);
            VdUtil.addCuboid(0.75f, 0.0f, 0.2f, 0.8f, 0.85f, 0.25f, EnumSet.of(facing), sprite_stick, faceQuads, generalQuads);
            VdUtil.addCuboid(0.2f, 0.0f, 0.75f, 0.25f, 0.85f, 0.8f, EnumSet.of(facing), sprite_stick, faceQuads, generalQuads);
            VdUtil.addCuboid(0.75f, 0.0f, 0.75f, 0.8f, 0.85f, 0.8f, EnumSet.of(facing), sprite_stick, faceQuads, generalQuads);
            if (crosscrop) {
                VdUtil.addCuboid(0.05f, 0.65f, 0.2f, 0.95f, 0.7f, 0.25f, EnumSet.of(facing), sprite_stick, faceQuads, generalQuads);
                VdUtil.addCuboid(0.2f, 0.65f, 0.05f, 0.25f, 0.7f, 0.95f, EnumSet.of(facing), sprite_stick, faceQuads, generalQuads);
                VdUtil.addCuboid(0.05f, 0.65f, 0.75f, 0.95f, 0.7f, 0.8f, EnumSet.of(facing), sprite_stick, faceQuads, generalQuads);
                VdUtil.addCuboid(0.75f, 0.65f, 0.05f, 0.8f, 0.7f, 0.95f, EnumSet.of(facing), sprite_stick, faceQuads, generalQuads);
            }
        }
        int used = 0;
        for (int j = 0; j < faceQuads.length; ++j) {
            if (faceQuads[j].isEmpty()) {
                faceQuads[j] = Collections.emptyList();
            }
            else {
                ++used;
            }
        }
        if (used == 0) {
            faceQuads = null;
        }
        if (generalQuads.isEmpty()) {
            generalQuads = Collections.emptyList();
        }
        return (IBakedModel)new BasicBakedBlockModel(faceQuads, generalQuads, sprite_stick);
    }
    
    @Override
    public TextureAtlasSprite getParticleTexture() {
        return CropModel.textures.get(CropModel.STICK);
    }
    
    @Override
    public void onReload() {
        this.modelCache.invalidateAll();
    }
    
    static {
        STICK = new ResourceLocation("ic2", "blocks/crop/stick2");
        MISSING = (loc -> Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite());
        CropModel.textures = new HashMap<ResourceLocation, TextureAtlasSprite>();
    }
}
