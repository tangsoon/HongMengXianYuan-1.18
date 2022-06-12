package by.ts.hmxy.client.gui;

import java.util.List;
import java.util.function.Supplier;

import by.ts.hmxy.client.gui.wigdet.PacketButton;
import by.ts.hmxy.net.ButtonPacket.Handler;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class GridButton extends PacketButton{

	public GridButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pYDiffTex,
			ResourceLocation pResourceLocation, int pTextureWidth, int pTextureHeight, Handler handler,
			Supplier<List<Component>> componentSup, Component message) {
		super(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffTex, pResourceLocation, pTextureWidth, pTextureHeight,
				handler, componentSup, message);
		// TODO Auto-generated constructor stub
	}

}
