package by.ts.hmxy.net;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import by.ts.hmxy.menu.MortarMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

public class ButtonPacket {
	
	public static final List<Handler> HANDLERS = new ArrayList<>();

 	private Handler handler;
	
	public ButtonPacket() {

	}

	public ButtonPacket(Handler handler) {
		this.handler = handler;
	}

	public ButtonPacket(FriendlyByteBuf buf) {
		this.handler = HANDLERS.get(buf.readInt());
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeInt(handler.BUTTON_ID);
	}

	public boolean handle(Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context ctx = supplier.get();
		ctx.enqueueWork(() -> {
			this.handler.HANDLER.accept(ctx);
		});
		return true;
	}
	
	public static class Handler {
		public final int BUTTON_ID;
		public final Consumer<NetworkEvent.Context> HANDLER;
		private Handler(int bUTTON_ID, Consumer<Context> hANDLER) {
			BUTTON_ID = bUTTON_ID;
			HANDLER = hANDLER;
		}
	}
	
	private static Handler createMessage(Consumer<NetworkEvent.Context> context) {
		Handler handler = new Handler(HANDLERS.size(), context);
		HANDLERS.add(handler);
		return handler;
	}
	
	/**研磨*/
	public static final Handler MORTAR_GRIND=createMessage(ctx->{
		ServerPlayer player= ctx.getSender();
		if(player.containerMenu instanceof MortarMenu menu) {
			menu.onCraft();
		}
	});
	
	/**凝丹*/
	public static final Handler NING_DAN=createMessage(ctx->{
		ServerPlayer player= ctx.getSender();
		if(player.containerMenu instanceof MortarMenu menu) {
			menu.onCraft();
		}
	});
}
