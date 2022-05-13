package by.ts.hmxy.block.blockentity;

import by.ts.hmxy.capability.Capabilities;
import by.ts.hmxy.config.Configs;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public class LingZhiBE extends BlockEntity implements BlockEntityTicker<LingZhiBE> {
	/** 生长次数上限 */
	public static final int GROW_TIMES_LIMIT = Configs.lingZhiGrowTimesLimit.get();
	/** 生长速度上限 */
	public static final float GROW_SPEED_LIMIT = Configs.lingZhiGrowSpeedLimit.get();

	/** 最大生长次数，产籽时随机增减并遗传给种子 */
	private int maxGrowTimes = Configs.lingZhiDefaultMaxGrowTimes.get();
	/** 当前生长次数，每个随机刻减一 */
	private int currentGrowTimes = 0;
	/** 生长速度 */
	private float growSpeed = Configs.lingZhiDefaultGrowSpeed.get();
	/** 药性，每个随机刻药性+=区块灵气*生长速度 */
	private float medicinal = 0.0F;

	public LingZhiBE(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
		super(pType, pWorldPosition, pBlockState);
	}

	public LingZhiBE(BlockPos pWorldPosition, BlockState pBlockState) {
		super(HmxyBEs.LING_ZHI.get(), pWorldPosition, pBlockState);
	}

	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		this.onDataSave(pTag);
	}

	private void onDataSave(CompoundTag pTag) {
		pTag.putInt("maxGrowTimes", maxGrowTimes);
		pTag.putInt("currentGrowTimes", currentGrowTimes);
		pTag.putFloat("growSpeed", growSpeed);
		pTag.putFloat("medicinal", medicinal);
	}

	public void load(CompoundTag pTag) {
		super.load(pTag);
		this.onDataLoad(pTag);
	}

	private void onDataLoad(CompoundTag pTag) {
		this.maxGrowTimes = pTag.getInt("maxGrowTimes");
		this.currentGrowTimes = pTag.getInt("currentGrowTimes");
		this.growSpeed = pTag.getFloat("growSpeed");
		this.medicinal = pTag.getFloat("medicinal");
	}

	@Override
	public void tick(Level pLevel, BlockPos pPos, BlockState pState, LingZhiBE be) {
		if (pLevel instanceof ServerLevel level && pLevel.getGameTime() % 20 == 18) {
			LevelChunk chunk = pLevel.getChunkAt(pPos);
			chunk.getCapability(Capabilities.CHUNK_INFO).ifPresent(info -> {
				float chunkLingQi = info.getLingQi();
				float grow = chunkLingQi * be.growSpeed;
				info.setLingQi(Math.max(chunkLingQi - grow, 0.0F));
				be.medicinal += grow;
				be.setChanged();
				level.sendBlockUpdated(pPos, pState, pState, 0b11);//发送到客户端
			});
		}
	}

	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		this.onDataSave(tag);
		return tag;
	}

	public void handleUpdateTag(CompoundTag tag) {
		super.handleUpdateTag(tag);
		this.onDataLoad(tag);
	}

	public int getMaxGrowTimes() {
		return maxGrowTimes;
	}

	public void setMaxGrowTimes(int maxGrowTimes) {
		this.maxGrowTimes = maxGrowTimes;
	}

	public int getCurrentGrowTimes() {
		return currentGrowTimes;
	}

	public void setCurrentGrowTimes(int currentGrowTimes) {
		this.currentGrowTimes = currentGrowTimes;
	}

	public float getGrowSpeed() {
		return growSpeed;
	}

	public void setGrowSpeed(float growSpeed) {
		this.growSpeed = growSpeed;
	}

	public float getMedicinal() {
		return medicinal;
	}

	public void setMedicinal(float medicinal) {
		this.medicinal = medicinal;
	}
}
