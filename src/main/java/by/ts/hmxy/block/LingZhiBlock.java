package by.ts.hmxy.block;

import java.util.Random;
import by.ts.hmxy.block.blockentity.HmxyBEs;
import by.ts.hmxy.block.blockentity.LingZhiBE;
import by.ts.hmxy.capability.Capabilities;
import by.ts.hmxy.item.HmxyItems;
import by.ts.hmxy.item.SeedItem;
import by.ts.hmxy.item.gene.DNA;
import by.ts.hmxy.item.gene.GeneHelper;
import by.ts.hmxy.item.gene.GeneType;
import by.ts.hmxy.util.HmxyHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
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
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;

public class LingZhiBlock extends BushBlock implements EntityBlock, EntityPlace, ItemStackCreator, Break {

	public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
	
	public static final GeneHelper<DNA> GENE_HELPER=new GeneHelper<DNA>();
	private static final GeneType<Integer> MAX_GROW_TIMES=GENE_HELPER.createGeneType("max_grow_times",Integer.class, 0);
	private static final GeneType<Float> GROW_SPEED=GENE_HELPER.createGeneType("grow_speed",Float.class, 0F);;
	static {
		MAX_GROW_TIMES.createGene(Integer.valueOf(10));
		MAX_GROW_TIMES.createGene(Integer.valueOf(20));
		MAX_GROW_TIMES.createGene(Integer.valueOf(30));
		MAX_GROW_TIMES.createGene(Integer.valueOf(40));
		MAX_GROW_TIMES.createGene(Integer.valueOf(50));
		MAX_GROW_TIMES.createGene(Integer.valueOf(60));
		MAX_GROW_TIMES.createGene(Integer.valueOf(70));
		MAX_GROW_TIMES.createGene(Integer.valueOf(80));
		MAX_GROW_TIMES.createGene(Integer.valueOf(90));
		MAX_GROW_TIMES.createGene(Integer.valueOf(100));
		GROW_SPEED.createGene(Float.valueOf(0.01F));
		GROW_SPEED.createGene(Float.valueOf(0.02F));
		GROW_SPEED.createGene(Float.valueOf(0.03F));
		GROW_SPEED.createGene(Float.valueOf(0.04F));
		GROW_SPEED.createGene(Float.valueOf(0.05F));
		GROW_SPEED.createGene(Float.valueOf(0.06F));
		GROW_SPEED.createGene(Float.valueOf(0.07F));
		GROW_SPEED.createGene(Float.valueOf(0.08F));
		GROW_SPEED.createGene(Float.valueOf(0.09F));
		GROW_SPEED.createGene(Float.valueOf(0.01F));
	}
	
	public LingZhiBlock(Properties pro) {
		super(pro);
		this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
	}

	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRandom) {
		if (!pLevel.isClientSide) {
			this.setAge(pState, this.getAge(pState) + 1);
		}
	}

	public int getMaxAge() {
		return 3;
	}
	

	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRandom) {
		if (pLevel.isLoaded(pPos) && pLevel.getRawBrightness(pPos, 0) >= 9) {
			if (pLevel.getBlockEntity(pPos) instanceof LingZhiBE be
					&& be.getCurrentGrowTimes() < this.getMaxGrowTimes(be.DNA)) {
				LevelChunk chunk = pLevel.getChunkAt(pPos);
				chunk.getCapability(Capabilities.CHUNK_INFO).ifPresent(info -> {
					float grow = info.getLingQi() * this.getGrowSpeed(be.DNA);
					info.setLingQi(Math.max(0, info.getLingQi() - grow));
					be.setMedicinal(be.getMedicinal() + grow);
					be.setCurrentGrowTimes(be.getCurrentGrowTimes() + 1);
					int newAge = (int) ((float) be.getCurrentGrowTimes() / this.getMaxGrowTimes(be.DNA) * getMaxAge());
					if (newAge != this.getAge(pState)) {
						if(newAge==this.getMaxAge()) {
							Random ran=pLevel.random;
							BlockPos pos=pPos.offset(ran.nextInt(3)-1, 0, ran.nextInt(3)-1);
							LingZhiBE neighbor= pLevel.getBlockEntity(pos, HmxyBEs.LING_ZHI.get()).orElse(null);
							if(neighbor==null) {
								neighbor=be;
							}
							DNA seedDna=new DNA(GENE_HELPER.getGeneTypes());
							LingZhiBlock.GENE_HELPER.createChild(be.DNA, neighbor.DNA, seedDna);
							SeedItem seed=(SeedItem) HmxyItems.SEED.get();
							ItemStack seedStack=new ItemStack(seed);
							seed.setDna(seedStack, seedDna);
							seed.setLingZhi(seedStack, (LingZhiBlock) pState.getBlock());
							HmxyHelper.dropItem(seedStack, pLevel, pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5);
						}
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
		LingZhiBE be=new LingZhiBE(pPos, pState);
		return be;
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
		// TODO 发布前删除注释
		// event.setCanceled(true);
	}

	public ItemStack createItemStack(BlockGetter blockGetter, BlockPos pPos, BlockState pState) {
		ItemStack lingZhi = new ItemStack(this);
		LingZhiBE lingZhiBe = (LingZhiBE) blockGetter.getBlockEntity(pPos);
		lingZhi.addTagElement("lingZhi", lingZhiBe.serializeNBT());
		return lingZhi;
	}

	/**
	 * 只在服务端调用
	 */
	@Override
	public void onBreak(BreakEvent event) {
		Player player = event.getPlayer();
		ItemStack tool = player.getMainHandItem();
		if (tool.getItem() == HmxyItems.HERB_HOE.get() && event.getWorld() instanceof Level level) {
			if (this.getAge(event.getState()) == 3) {
				ItemStack lingZhi = this.createItemStack(level, event.getPos(), event.getState());
				BlockPos pos = event.getPos();
				HmxyHelper.dropItem(lingZhi, level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			}
		} else {
			event.setCanceled(true);
		}
	}
	
	public int getMaxGrowTimes(DNA dna) {
		return GENE_HELPER.getValue(MAX_GROW_TIMES, dna);
	}

	public float getGrowSpeed(DNA dna) {
		return GENE_HELPER.getValue(GROW_SPEED, dna);
	}
}
