package by.ts.hmxy.block;

import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * The block which implements this interface has temperature and can be heated by {@link ElixirFurnaceRootBlock}
 * @author tangsoon
 *
 */
public interface HasTemperature {
	void setTemperature(BlockEntity be,float temp);
	float getTemperature(BlockEntity be);
}
