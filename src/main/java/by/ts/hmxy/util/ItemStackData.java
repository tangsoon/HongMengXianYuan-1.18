package by.ts.hmxy.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * 一个数据存取的类实现这个接口用于存取ItemStack中的数据
 * @author tangsoon
 */
public interface ItemStackData extends INBTSerializable<CompoundTag>{
	void save(ItemStack stack);
}
