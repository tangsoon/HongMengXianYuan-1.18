//package by.ts.hmxy.util;
//
//import by.ts.hmxy.config.Configs;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraftforge.common.util.INBTSerializable;
//import net.minecraftforge.registries.ForgeRegistryEntry;
//
////TODO 删除
//public class Gene extends ForgeRegistryEntry<Gene> implements INBTSerializable<CompoundTag>{
//
//	/** 最大生长次数，产籽时随机增减并遗传给种子 */
//	private int maxGrowTimes = Configs.lingZhiDefaultMaxGrowTimes.get();
//	/** 生长速度 */
//	private float growSpeed = Configs.lingZhiDefaultGrowSpeed.get();
//	
//	public Gene(int maxGrowTimes, float growSpeed) {
//		this.maxGrowTimes = maxGrowTimes;
//		this.growSpeed = growSpeed;
//	}
//
//	public int getMaxGrowTimes() {
//		return maxGrowTimes;
//	}
//
//	public void setMaxGrowTimes(int maxGrowTimes) {
//		this.maxGrowTimes = maxGrowTimes;
//	}
//
//	public float getGrowSpeed() {
//		return growSpeed;
//	}
//
//	public void setGrowSpeed(float growSpeed) {
//		this.growSpeed = growSpeed;
//	}
//
//	@Override
//	public CompoundTag serializeNBT() {
//		CompoundTag nbt=new CompoundTag();
//		nbt.putInt("maxGrowTimes", maxGrowTimes);
//		nbt.putFloat("growSpeed", growSpeed);
//		return nbt;
//	}
//
//	@Override
//	public void deserializeNBT(CompoundTag nbt) {
//		this.maxGrowTimes=nbt.getInt("maxGrowTimes");
//		this.growSpeed=nbt.getFloat("growSpeed");
//	}
//}