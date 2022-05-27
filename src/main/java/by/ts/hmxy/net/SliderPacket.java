package by.ts.hmxy.net;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import by.ts.hmxy.menu.ElixirFurnaceRootMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

public class SliderPacket {
	public static final List<Handler> HANDLERS = new ArrayList<>();

 	private Handler handler;
	public double sliderValue;
 	
	public SliderPacket() {

	}

	public SliderPacket(Handler handler,double sliderValue) {
		this.handler = handler;
		this.sliderValue=sliderValue;
	}

	public SliderPacket(FriendlyByteBuf buf) {
		this.handler = HANDLERS.get(buf.readInt());
		this.sliderValue=buf.readDouble();
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeInt(handler.ID);
		buf.writeDouble(sliderValue);
	}

	public boolean handle(Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context ctx = supplier.get();
		ctx.enqueueWork(() -> {
			this.handler.HANDLER.accept(ctx,this);
		});
		return true;
	}
	
	public static class Handler {
		public final int ID;
		public final BiConsumer<NetworkEvent.Context,SliderPacket> HANDLER;
		private Handler(int id, BiConsumer<Context,SliderPacket> handler) {
			ID = id;
			HANDLER = handler;
		}
	}
	
	private static Handler create(BiConsumer<NetworkEvent.Context,SliderPacket> context) {
		Handler handler = new Handler(HANDLERS.size(), context);
		HANDLERS.add(handler);
		return handler;
	}
	
	public static final Handler LING_QI_VALVE=create((ctx,s)->{
		ServerPlayer player= ctx.getSender();
		if(player.containerMenu instanceof ElixirFurnaceRootMenu menu) {
			menu.getBe().setValve((float) s.sliderValue);
		}
	});
}
