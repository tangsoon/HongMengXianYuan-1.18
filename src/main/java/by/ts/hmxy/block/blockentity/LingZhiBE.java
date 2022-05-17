package by.ts.hmxy.block.blockentity;

import java.util.Random;

import by.ts.hmxy.block.LingZhiBlock;
import by.ts.hmxy.item.gene.GeneItem;
import by.ts.hmxy.item.gene.GeneType;
import by.ts.hmxy.item.gene.IGeneContanier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LingZhiBE extends BlockEntity implements IGeneContanier {
	/** 当前生长次数，每个随机刻减一 */
	private int currentGrowTimes = 0;
	private GeneItem<?>[] geneA = null;
	private GeneItem<?>[] geneB = null;

	/** 药性，每个随机刻药性+=区块灵气*生长速度 */
	private float medicinal = 0.0F;
	private LingZhiBlock lingZhi;

	public LingZhiBE(BlockPos pWorldPosition, BlockState pBlockState) {
		super(HmxyBEs.LING_ZHI.get(), pWorldPosition, pBlockState);
		this.lingZhi = (LingZhiBlock) pBlockState.getBlock();
		int lenth=LingZhiBlock.GENE_HELPER.getGeneTypes().size();
		GeneItem<?>[] genesA = new GeneItem<?>[lenth];
		GeneItem<?>[] genesB = new GeneItem<?>[lenth];
		Random ran = new Random();
		for (int i = 0; i < lenth; i++) {
			GeneType<?> geneType = LingZhiBlock.GENE_HELPER.getGeneTypes().get(i);
			int size = geneType.size();
			if (size > 0) {
				genesA[i] = geneType.getGene(ran.nextInt(size)).get();
				genesB[i] = geneType.getGene(ran.nextInt(size)).get();
			}
		}
		this.setGenesA(genesA);
		this.setGenesB(genesB);
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
		pTag.put("genes", LingZhiBlock.GENE_HELPER.serialize(this));
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
		LingZhiBlock.GENE_HELPER.deserialize(pTag.getCompound("genes"), this);
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

	public GeneItem<?>[] getGeneA() {
		return geneA;
	}

	public GeneItem<?>[] getGeneB() {
		return geneB;
	}

	public LingZhiBlock getLingZhi() {
		return lingZhi;
	}

	@Override
	public GeneItem<?>[] getGenesA() {
		return this.geneA;
	}

	@Override
	public GeneItem<?>[] getGenesB() {
		return this.geneB;
	}

	@Override
	public void setGenesA(GeneItem<?>[] genes) {
		this.geneA = genes;
	}

	@Override
	public void setGenesB(GeneItem<?>[] genes) {
		this.geneB = genes;
	}
}
