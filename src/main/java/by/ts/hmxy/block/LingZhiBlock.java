package by.ts.hmxy.block;

import java.util.Random;

import by.ts.hmxy.block.blockentity.BaseBlockEntity;
import by.ts.hmxy.block.blockentity.HmxyBEs;
import by.ts.hmxy.capability.Capabilities;
import by.ts.hmxy.item.HmxyItems;
import by.ts.hmxy.item.LingZhiItem;
import by.ts.hmxy.item.SeedItem;
import by.ts.hmxy.item.gene.DNA;
import by.ts.hmxy.item.gene.GeneHelper;
import by.ts.hmxy.item.gene.GeneType;
import by.ts.hmxy.util.HmxyHelper;
import by.ts.hmxy.util.PlantTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.PlantType;

public class LingZhiBlock extends BushBlock implements EntityBlock {

	public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
	public static final GeneHelper<DNA> GENE_HELPER = new GeneHelper<DNA>();
	private static final GeneType<Integer> MAX_GROW_TIMES = GENE_HELPER.createGeneType("max_grow_times", "生长次数",
			Integer.class, Integer.valueOf(0));
	private static final GeneType<Float> GROW_SPEED = GENE_HELPER.createGeneType("grow_speed", "生长速度", Float.class,
			Float.valueOf(0F));
	private static final GeneType<Integer> SEED_COUNT = GENE_HELPER.createGeneType("seed_count", "产籽数量", Integer.class,
			Integer.valueOf(0));
	static {
		for (int i = 0; i < 10; i++) {
			MAX_GROW_TIMES.createGene(Integer.valueOf(10 + i), "" + i);
			GROW_SPEED.createGene(Float.valueOf(i / 100F), "" + i);
		}
		SEED_COUNT.createGene(Integer.valueOf(1), "1");
	}

	private LingZhiItem item;

	public LingZhiBlock(Properties pro) {
		super(pro);
		this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
	}

	protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return pState.is(Blocks.FARMLAND);
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
						if (newAge == this.getMaxAge()) {
							Random ran = pLevel.random;
							int seedCount = getSeedCount(be.DNA);
							for (int i = 0; i < seedCount; i++) {
								BlockPos pos = pPos.offset(ran.nextInt(3) - 1, 0, ran.nextInt(3) - 1);
								LingZhiBE neighbor = pLevel.getBlockEntity(pos, HmxyBEs.LING_ZHI.get()).orElse(null);
								if (neighbor == null) {
									neighbor = be;
								}
								DNA seedDna = new DNA(GENE_HELPER.getGeneTypes());
								LingZhiBlock.GENE_HELPER.createChild(be.DNA, neighbor.DNA, seedDna);
								SeedItem seed = (SeedItem) HmxyItems.SEED.get();
								ItemStack seedStack = new ItemStack(seed);
								seed.setDna(seedStack, seedDna);
								seed.setLingZhi(seedStack, (LingZhiBlock) pState.getBlock());
								HmxyHelper.dropItem(seedStack, pLevel, pPos.getX() + 0.5, pPos.getY() + 0.5,
										pPos.getZ() + 0.5);
							}
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
		LingZhiBE be = new LingZhiBE(pPos, pState);
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

	public int getMaxGrowTimes(DNA dna) {
		return GENE_HELPER.getValue(MAX_GROW_TIMES, dna);
	}

	public float getGrowSpeed(DNA dna) {
		return GENE_HELPER.getValue(GROW_SPEED, dna);
	}

	public int getSeedCount(DNA dna) {
		return GENE_HELPER.getValue(SEED_COUNT, dna);
	}

	public LingZhiItem getItem() {
		return item;
	}

	public void setItem(LingZhiItem item) {
		this.item = item;
	}

	public PlantType getPlantType(BlockGetter level, BlockPos pos) {
		return PlantTypes.LING_ZHI;
	}
	
	public static class LingZhiBE extends BaseBlockEntity{

		public final DNA DNA;
		/** 当前生长次数，每个随机刻减一 */
		private int currentGrowTimes = 0;

		/** 药性，每个随机刻药性+=区块灵气*生长速度 */
		private float medicinal = 0.0F;
		private LingZhiBlock lingZhi;

		public LingZhiBE(BlockPos pWorldPosition, BlockState pBlockState) {
			super(HmxyBEs.LING_ZHI.get(), pWorldPosition, pBlockState);
			this.lingZhi = (LingZhiBlock) pBlockState.getBlock();
			DNA = new DNA(LingZhiBlock.GENE_HELPER.getGeneTypes());
		}

		/**
		 * 同步更新数据
		 * 
		 * @param pTag
		 */
		protected void saveCustomData(CompoundTag pTag) {
			pTag.putInt("currentGrowTimes", currentGrowTimes);
			pTag.putFloat("medicinal", medicinal);
			pTag.put("genes", this.DNA.serializeNBT());
		}

		protected void loadCustomData(CompoundTag pTag) {
			this.currentGrowTimes = pTag.getInt("currentGrowTimes");
			this.medicinal = pTag.getFloat("medicinal");
			this.DNA.deserializeNBT(pTag.getCompound("genes"));
		}

		public int getCurrentGrowTimes() {
			return currentGrowTimes;
		}

		public void setCurrentGrowTimes(int currentGrowTimes) {
			this.currentGrowTimes = currentGrowTimes;
		}

		public float getMedicinal() {
			return medicinal;
		}

		public void setMedicinal(float medicinal) {
			this.medicinal = medicinal;
		}

		public LingZhiBlock getLingZhi() {
			return lingZhi;
		}
	}
}
