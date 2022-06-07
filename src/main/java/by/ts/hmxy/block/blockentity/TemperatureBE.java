package by.ts.hmxy.block.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This BlockEntity has temprature.
 * @author tangsoon
 *
 */
public class TemperatureBE extends BaseBlockEntity {

	public static final float MAX_TEMPERATURE=1000f;
	
	protected float temperature=0f;
	
	public TemperatureBE(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
		super(pType, pWorldPosition, pBlockState);
	}

	@Override
	protected void saveCustomData(CompoundTag pTag) {
		pTag.putFloat("temperature", temperature);
	}

	@Override
	protected void loadCustomData(CompoundTag pTag) {
		this.temperature=pTag.getFloat("temperature");
	}

	public float getTemperature() {
		return temperature;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}
}
