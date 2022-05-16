package by.ts.hmxy.block;

import java.util.Random;
import by.ts.hmxy.block.blockentity.LingZhiBE;
import by.ts.hmxy.capability.Capabilities;
import by.ts.hmxy.config.Configs;
import by.ts.hmxy.item.HmxyItems;
import by.ts.hmxy.util.HmxyHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;

public class LingZhiBlock extends BushBlock implements EntityBlock, EntityPlace ,ItemStackCreator,Break{

	public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

	public LingZhiBlock(Properties pro) {
		super(pro);
		this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
	}

	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRandom) {
		if (!pLevel.isClientSide) {
			this.setAge(pState, this.getAge(pState) + 1);
		}
	}

	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRandom) {
		if (pLevel.isLoaded(pPos) && pLevel.getRawBrightness(pPos, 0) >= 9) {
			if (pLevel.getBlockEntity(pPos) instanceof LingZhiBE be
					&& be.getCurrentGrowTimes() < be.getMaxGrowTimes()) {
				LevelChunk chunk = pLevel.getChunkAt(pPos);
				chunk.getCapability(Capabilities.CHUNK_INFO).ifPresent(info -> {
					float grow = info.getLingQi() * be.getGrowSpeed();
					info.setLingQi(Math.max(0, info.getLingQi() - grow));
					be.setMedicinal(be.getMedicinal() + grow);
					be.setCurrentGrowTimes(be.getCurrentGrowTimes() + 1);
					int newAge = (int) ((float) be.getCurrentGrowTimes() / be.getMaxGrowTimes() * 4 - 1);
					if (newAge != this.getAge(pState)) {
						BlockState newState = this.getStateForAge(newAge);
						pLevel.setBlock(pPos, newState, 0b11);
					}
					be.setChanged();
					chunk.setUnsaved(true);
				});
			}
		}
	}

	public BlockState getStateForAge(int pAge) {
		return this.defaultBlockState().setValue(AGE, Integer.valueOf(pAge));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new LingZhiBE(pPos, pState);
	}

	public int getAge(BlockState state) {
		return state.getValue(AGE);
	}

	public void setAge(BlockState state, int value) {
		state.setValue(AGE, value);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(AGE);
	}

	@Override
	public void onEntityPlace(EntityPlaceEvent event) {
		//TODO 发布前删除注释
		//event.setCanceled(true);
	}

	public ItemStack createItemStack(BlockGetter blockGetter, BlockPos pPos, BlockState pState) {
		ItemStack lingZhi=new ItemStack(this);
		LingZhiBE lingZhiBe=(LingZhiBE) blockGetter.getBlockEntity(pPos);
		lingZhi.addTagElement("lingZhi", lingZhiBe.serializeNBT());
		return lingZhi;
	}

	/**
	 * 只在服务端调用
	 */
	@Override
	public void onBreak(BreakEvent event) {
		Player player=event.getPlayer();
		ItemStack tool=player.getMainHandItem();
		if(tool.getItem()==HmxyItems.HERB_HOE.get()&&event.getWorld() instanceof Level level) {
			if(this.getAge(event.getState())==3) {
				ItemStack lingZhi=this.createItemStack(level, event.getPos(), event.getState());
				BlockPos pos=event.getPos();
				HmxyHelper.dropItem(lingZhi, level,pos.getX()+0.5 , pos.getY()+0.5, pos.getZ()+0.5);	
				
			}
		}
		else {
			event.setCanceled(true);
		}
	}
	
	public class Gene implements INBTSerializable<CompoundTag>{

		/** 最大生长次数，产籽时随机增减并遗传给种子 */
		private int maxGrowTimes = Configs.lingZhiDefaultMaxGrowTimes.get();
		/** 生长速度 */
		private float growSpeed = Configs.lingZhiDefaultGrowSpeed.get();
		
		public int getMaxGrowTimes() {
			return maxGrowTimes;
		}

		public void setMaxGrowTimes(int maxGrowTimes) {
			this.maxGrowTimes = maxGrowTimes;
		}

		public float getGrowSpeed() {
			return growSpeed;
		}

		public void setGrowSpeed(float growSpeed) {
			this.growSpeed = growSpeed;
		}

		@Override
		public CompoundTag serializeNBT() {
			CompoundTag nbt=new CompoundTag();
			nbt.putInt("maxGrowTimes", maxGrowTimes);
			nbt.putFloat("growSpeed", growSpeed);
			return nbt;
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			this.maxGrowTimes=nbt.getInt("maxGrowTimes");
			this.growSpeed=nbt.getFloat("growSpeed");
		}
	}
	
//	/**
//	 * 记录灵植种子的数据
//	 * @author tangsoon
//	 *
//	 */
//	public static class  Seed implements INBTSerializable<CompoundTag>{
//		
//		private int maxGrowTimes=0;
//		private float growSpeed=0;
//		
//		public Seed(int maxGrowTimes, float growSpeed2) {
//			this.maxGrowTimes = maxGrowTimes;
//			this.growSpeed = growSpeed2;
//		}
//
//		@Override
//		public CompoundTag serializeNBT() {
//			CompoundTag result=new CompoundTag();
//			result.putInt("maxGrowTimes", maxGrowTimes);
//			result.putFloat("growSpeed", growSpeed);
//			return result;
//		}
//		
//		@Override
//		public void deserializeNBT(CompoundTag nbt) {
//			this.setMaxGrowTimes(nbt.getInt("maxGrowTimes"));
//			this.setGrowSpeed(nbt.getInt("growSpeed"));
//		}
//
//		public int getMaxGrowTimes() {
//			return maxGrowTimes;
//		}
//
//		public void setMaxGrowTimes(int maxGrowTimes) {
//			this.maxGrowTimes = maxGrowTimes;
//		}
//
//		public float getGrowSpeed() {
//			return growSpeed;
//		}
//
//		public void setGrowSpeed(int growSpeed) {
//			this.growSpeed = growSpeed;
//		}
//	}
	
//	/**
//	 * 灵植种子
//	 * @author tangsoon
//	 *
//	 */
//	public class SeedItem extends Item{
//		public SeedItem(Properties pProperties) {
//			super(pProperties);
//		}
//	}
}
