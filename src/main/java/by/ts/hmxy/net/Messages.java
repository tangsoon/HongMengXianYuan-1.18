package by.ts.hmxy.net;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import by.ts.hmxy.HmxyMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.simple.SimpleChannel.MessageBuilder.ToBooleanBiFunction;

public class Messages {

	private static SimpleChannel INSTANCE;
	private static int packetId = 0;

	public static void register() {
		INSTANCE = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(HmxyMod.MOD_ID, "messages"))
				.networkProtocolVersion(() -> "1.0").clientAcceptedVersions(s -> true).serverAcceptedVersions(s -> true)
				.simpleChannel();
		registerPacket(ButtonPacket.class, NetworkDirection.PLAY_TO_SERVER, ButtonPacket::new, ButtonPacket::toBytes,
				ButtonPacket::handle);
	}

	public static <MSG> void sendToServer(MSG message) {
		INSTANCE.sendToServer(message);
	}

	public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
		INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
	}

	public static <MSG> void registerPacket(Class<MSG> clazz, NetworkDirection dir,
			Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, FriendlyByteBuf> encoder,
			ToBooleanBiFunction<MSG, Supplier<NetworkEvent.Context>> handler) {
		INSTANCE.messageBuilder(clazz,packetId++, dir).decoder(decoder).encoder(encoder).consumer(handler).add();
	}
}