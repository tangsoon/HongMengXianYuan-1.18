package by.ts.hmxy.item.gene;

import by.ts.hmxy.block.blockentity.LingZhiBE;

public class LingZhiGeneHelper extends GeneHelper<LingZhiBE> {
	
	private GeneType<Integer> maxGrowTimes;
	private GeneType<Float> growSpeed;

	public static final LingZhiGeneHelper INSTANCE=new LingZhiGeneHelper();
	
	private LingZhiGeneHelper() {
		super();
	}
	
	@Override
	protected void init() {
		maxGrowTimes=this.createGeneType("max_grow_times",Integer.class, 0);
		growSpeed=this.createGeneType("grow_speed",Float.class, 0F);
	}
	
	public int getMaxGrowTimes(LingZhiBE be) {
		return this.getValue(maxGrowTimes, be);
	}

	public float getGrowSpeed(LingZhiBE be) {
		return this.getValue(growSpeed, be);
	}
}