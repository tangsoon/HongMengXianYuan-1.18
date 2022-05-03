package by.ts.hmxy.capability;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class ChunkInfoProvider extends CapabilityProvider<ChunkInfoProvider> implements INBTSerializable<CompoundTag> {

	public static final Capability<HmxyChunkInfo> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});
	private final LazyOptional<HmxyChunkInfo> capability;

	protected ChunkInfoProvider(LevelChunk chunk) {
		super(ChunkInfoProvider.class);
		capability = LazyOptional.of(() -> new HmxyChunkInfo(chunk));
	}
	
	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return CAPABILITY.orEmpty(cap, capability);
	}

	@Override
	public CompoundTag serializeNBT() {
		return capability.resolve().isPresent() ? capability.resolve().get().serializeNBT() : new CompoundTag();
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		capability.ifPresent(cap -> cap.deserializeNBT(nbt));
	}
}