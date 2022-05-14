package by.ts.hmxy.client.bakedmodel;

import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

public class LingZhiBakedModel implements IDynamicBakedModel{

	@Override
	public boolean useAmbientOcclusion() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isGui3d() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean usesBlockLight() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCustomRenderer() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemOverrides getOverrides() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
		// TODO Auto-generated method stub
		return null;
	}

}
