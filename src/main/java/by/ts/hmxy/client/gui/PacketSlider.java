package by.ts.hmxy.client.gui;

import by.ts.hmxy.net.Messages;
import by.ts.hmxy.net.SliderPacket;
import by.ts.hmxy.util.TransMsg;
import net.minecraft.client.gui.components.AbstractSliderButton;

/**
 * this will atomaticly send packet to server when player modify the value of slider
 * @author tangsoon
 */
public abstract class PacketSlider extends AbstractSliderButton{

	private SliderPacket.Handler handler;
	protected TransMsg msg;
	public PacketSlider(int pX, int pY, int pWidth, int pHeight,TransMsg msg, double pValue,double displayValue,SliderPacket.Handler handler) {
		super(pX, pY, pWidth, pHeight, msg.create(displayValue), pValue);
	}

	@Override
	protected abstract void updateMessage();

	@Override
	protected void applyValue() {
		Messages.sendToServer(new SliderPacket(handler, value));
	}
}
