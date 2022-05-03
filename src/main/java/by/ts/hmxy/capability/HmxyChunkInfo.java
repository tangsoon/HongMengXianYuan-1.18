package by.ts.hmxy.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * 存放区块相关的数据。
 */
public class HmxyChunkInfo{

	float lingQi = 0;
	LevelChunk chunk;

	public HmxyChunkInfo(LevelChunk chunk) {
		this.chunk=chunk;
	}
	
	public CompoundTag serializeNBT() {
		CompoundTag result = new CompoundTag();
		result.putFloat("lingQi", lingQi);
		return result;
	}

	public void deserializeNBT(CompoundTag nbt) {
		this.lingQi = nbt.getFloat("lingQi");
	}

	public float getLingQi() {
		return lingQi;
	}

	public void setLingQi(float lingQi) {
		this.lingQi = lingQi;
		this.chunk.setUnsaved(true);
	}
}