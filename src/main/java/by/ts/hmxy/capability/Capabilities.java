package by.ts.hmxy.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class Capabilities {
	public static final Capability<HmxyChunkInfo> CHUNK_INFO = CapabilityManager.get(new CapabilityToken<>() {
	});
}
