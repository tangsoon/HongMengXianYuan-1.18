package by.ts.hmxy.client.gui.wigdet;

import by.ts.hmxy.net.Messages;
import by.ts.hmxy.net.SliderPacket;
import by.ts.hmxy.util.TransMsg;
import net.minecraft.resources.ResourceLocation;

/**
 * this will atomaticly send packet to server when player modify the value of
 * slider TODO bug:can not drag
 * 
 * @author tangsoon
 */
public abstract class PacketSlider extends BaseSlider {
	
	private SliderPacket.Handler handler;

	public PacketSlider(int pX, int pY, int pWidth, int pHeight, TransMsg msg, double pValue, double valueMulti,
			ResourceLocation texture,SliderPacket.Handler handler) {
		super(pX, pY, pWidth, pHeight, msg, pValue,valueMulti,texture);
		this.handler = handler;
	}

	@Override
	protected void applyValue() {
		Messages.sendToServer(new SliderPacket(handler, value));	
	}
}
