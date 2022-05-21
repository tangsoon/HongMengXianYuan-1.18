package by.ts.hmxy.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class CapaProBase<C extends INBTSerializable<CompoundTag>> implements ICapabilitySerializable<CompoundTag>{

	private final C INSTANCE;
	private final Capability<C> CAPABILITY;
	
	
	public CapaProBase(C c,Capability<C> CAPABILITY) {
		this.INSTANCE=c;
		this.CAPABILITY=CAPABILITY;
	}

	@Override
	public CompoundTag serializeNBT() {
		return this.INSTANCE.serializeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		this.INSTANCE.deserializeNBT(nbt);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return CAPABILITY.orEmpty(cap, LazyOptional.of(()->this.INSTANCE));
	}
}
