package by.ts.hmxy.item.gene;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IDNA extends INBTSerializable<CompoundTag>{
	GeneItem<?>[] getGenesA();
	GeneItem<?>[] getGenesB();
	void setGenesA(GeneItem<?>[] genes);
	void setGenesB(GeneItem<?>[] genes);
}
