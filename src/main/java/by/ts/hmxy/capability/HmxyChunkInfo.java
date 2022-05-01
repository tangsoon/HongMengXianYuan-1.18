package by.ts.hmxy.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * 存放区块相关的数据。
 */
public class HmxyChunkInfo implements INBTSerializable<CompoundTag> {

	float lingQi = 0;

	public HmxyChunkInfo() {

	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag result = new CompoundTag();
		result.putFloat("lingQi", lingQi);
		return result;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		this.lingQi = nbt.getFloat("lingQi");
	}

	public float getLingQi() {
		return lingQi;
	}

	public void setLingQi(float lingQi) {
		this.lingQi = lingQi;
	}
}