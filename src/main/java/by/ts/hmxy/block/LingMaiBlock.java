package by.ts.hmxy.block;

import java.util.Random;

import by.ts.hmxy.capability.ChunkInfoProvider;
import by.ts.hmxy.capability.HmxyChunkInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.chunk.LevelChunk;

public class LingMaiBlock extends Block {

	public LingMaiBlock(Properties pro) {
		super(pro);
		StateDefinition.Builder<Block, BlockState> builder = new StateDefinition.Builder<>(this);
		this.createBlockStateDefinition(builder);
		
		this.registerDefaultState(builder.create(Block::defaultBlockState,(o,m1,m2)->{
			return  new BlockState(o,m1,m2) {
				@Override
			      public void randomTick(ServerLevel pLevel, BlockPos pPos, Random pRandom) {
 			    	  LevelChunk chunk= pLevel.getChunkAt(pPos);
 			    	  HmxyChunkInfo chunkInfo=chunk.getCapability(ChunkInfoProvider.CAPABILITY).orElseGet(()->new HmxyChunkInfo());
 			    	  chunkInfo.setLingQi(chunkInfo.getLingQi()+1);
 			    	  System.out.println("lalalla");
			      }
			};
		}).any());
	}


}
