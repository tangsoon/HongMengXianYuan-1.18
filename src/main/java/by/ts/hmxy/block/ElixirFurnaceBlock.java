package by.ts.hmxy.block;

import org.apache.logging.log4j.LogManager;

import by.ts.hmxy.block.blockentity.HmxyBEs;
import by.ts.hmxy.block.blockentity.TemperatureBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

/**
 * 炼丹炉
 * @author tangsoon
 *
 */
public class ElixirFurnaceBlock extends WaterloggedBlockBase implements EntityBlock,HasTemperature {
	
	public ElixirFurnaceBlock(Material m) {
		super(Properties.of(m, m.getColor()).strength(2.0F));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		ElixirFurnaceBE be=new ElixirFurnaceBE(pPos, pState);
		return be;
	}
	
	public static class ElixirFurnaceBE extends TemperatureBE{
		public ElixirFurnaceBE(BlockPos pWorldPosition, BlockState pBlockState) {
			super(HmxyBEs.ELIXIR_FURNACE.get(), pWorldPosition, pBlockState);
		}
	}

	@Override
	public void setTemperature(BlockEntity be, float temp) {
		if(be instanceof ElixirFurnaceBE rbe) {
			rbe.setTemperature(temp);
			rbe.setChanged();
		}
	}

	@Override
	public float getTemperature(BlockEntity be) {
		if(be instanceof ElixirFurnaceBE rbe) {
			return rbe.getTemperature();	
		}
		else {
			LogManager.getLogger().error("You can not pass a BlockEntity to a block which dose not have it.");
			return 0F;	
		}
	}
}
