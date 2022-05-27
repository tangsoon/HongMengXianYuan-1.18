package by.ts.hmxy.block.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseBlockEntity extends BlockEntity {

	public BaseBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
		super(pType, pWorldPosition, pBlockState);
	}

	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		this.saveCustomData(pTag);
	}

	/**
	 * 同步更新数据
	 * 
	 * @param pTag
	 */
	protected abstract void saveCustomData(CompoundTag pTag);

	public void load(CompoundTag pTag) {
		super.load(pTag);
		this.loadCustomData(pTag);
	}

	/**
	 * 同步更新数据
	 * 
	 * @param pTag
	 */
	protected abstract void loadCustomData(CompoundTag pTag);

	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		this.saveCustomData(tag);
		return tag;
	}
}
