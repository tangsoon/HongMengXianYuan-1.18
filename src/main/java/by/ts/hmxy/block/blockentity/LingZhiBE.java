package by.ts.hmxy.block.blockentity;

import by.ts.hmxy.block.LingZhiBlock;
import by.ts.hmxy.item.gene.DNA;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LingZhiBE extends BlockEntity{
	
	public final DNA DNA;
	
	/** 当前生长次数，每个随机刻减一 */
	private int currentGrowTimes = 0;

	/** 药性，每个随机刻药性+=区块灵气*生长速度 */
	private float medicinal = 0.0F;
	private LingZhiBlock lingZhi;

	public LingZhiBE(BlockPos pWorldPosition, BlockState pBlockState) {
		super(HmxyBEs.LING_ZHI.get(), pWorldPosition, pBlockState);
		this.lingZhi = (LingZhiBlock) pBlockState.getBlock();
		DNA=new DNA(lingZhi.GENE_HELPER.getGeneTypes());
	}

	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		this.onDataSave(pTag);
	}

	/**
	 * 同步更新数据
	 * 
	 * @param pTag
	 */
	private void onDataSave(CompoundTag pTag) {
		pTag.putInt("currentGrowTimes", currentGrowTimes);
		pTag.putFloat("medicinal", medicinal);
		pTag.put("genes", this.DNA.serializeNBT());
	}

	public void load(CompoundTag pTag) {
		super.load(pTag);
		this.onDataLoad(pTag);
	}

	/**
	 * 同步更新数据
	 * 
	 * @param pTag
	 */
	private void onDataLoad(CompoundTag pTag) {
		this.currentGrowTimes = pTag.getInt("currentGrowTimes");
		this.medicinal = pTag.getFloat("medicinal");
		this.DNA.deserializeNBT(pTag.getCompound("genes"));
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
