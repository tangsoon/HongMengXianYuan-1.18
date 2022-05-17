package by.ts.hmxy.block.blockentity;

import java.util.Random;

import by.ts.hmxy.config.Configs;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LingZhiBE extends BlockEntity {
	/** 生长次数上限 */
	public static final int GROW_TIMES_LIMIT = Configs.lingZhiGrowTimesLimit.get();
	/** 生长速度上限 */
	public static final float GROW_SPEED_LIMIT = Configs.lingZhiGrowSpeedLimit.get();
	/** 最大生长次数，产籽时随机增减并遗传给种子 */
	// private int maxGrowTimes = Configs.lingZhiDefaultMaxGrowTimes.get();
	/** 当前生长次数，每个随机刻减一 */
	private int currentGrowTimes = 0;
	/** 生长速度 */
	// private float growSpeed = Configs.lingZhiDefaultGrowSpeed.get();
	/** 药性，每个随机刻药性+=区块灵气*生长速度 */
	private float medicinal = 0.0F;

	private Gene[] genes;
	private int geneExpress = 0;

//	public LingZhiBE(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
//		super(pType, pWorldPosition, pBlockState);
//	}

	public LingZhiBE(BlockPos pWorldPosition, BlockState pBlockState) {
		super(HmxyBEs.LING_ZHI.get(), pWorldPosition, pBlockState);
		Random ran = new Random();
		genes = new Gene[] { new Gene(ran.nextInt(GROW_TIMES_LIMIT / 2), ran.nextFloat(GROW_SPEED_LIMIT / 2)),
				new Gene(ran.nextInt(GROW_TIMES_LIMIT / 2), ran.nextFloat(GROW_SPEED_LIMIT / 2)) };
	}

	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		this.onDataSave(pTag);
	}

	private void onDataSave(CompoundTag pTag) {
		pTag.putInt("currentGrowTimes", currentGrowTimes);
		pTag.putFloat("medicinal", medicinal);
		ListTag geneTag = new ListTag();
		for (Gene gene : genes) {
			geneTag.add(gene.serializeNBT());
		}
		pTag.put("genes", geneTag);
		pTag.putInt("geneExpress", geneExpress);
	}

	public void load(CompoundTag pTag) {
		super.load(pTag);
		this.onDataLoad(pTag);
	}

	private void onDataLoad(CompoundTag pTag) {
		this.currentGrowTimes = pTag.getInt("currentGrowTimes");
		this.medicinal = pTag.getFloat("medicinal");
		ListTag geneTag = pTag.getList("genes", 10);
		for (int i = 0; i < geneTag.size(); i++) {
			genes[i].deserializeNBT((CompoundTag) geneTag.get(i));
		}
		this.geneExpress = pTag.getInt("geneExpress");
	}

	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		this.onDataSave(tag);
		return tag;
	}

	public void handleUpdateTag(CompoundTag tag) {
		super.handleUpdateTag(tag);
		this.onDataLoad(tag);
	}

	public int getMaxGrowTimes() {
		return genes[geneExpress].getMaxGrowTimes();
	}

	public int getCurrentGrowTimes() {
		return currentGrowTimes;
	}

	public void setCurrentGrowTimes(int currentGrowTimes) {
		this.currentGrowTimes = currentGrowTimes;
	}

	public float getGrowSpeed() {
		return genes[geneExpress].getGrowSpeed();
	}

	public float getMedicinal() {
		return medicinal;
	}

	public void setMedicinal(float medicinal) {
		this.medicinal = medicinal;
	}
}
