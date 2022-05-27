package by.ts.hmxy.client.gui;

import by.ts.hmxy.net.Messages;
import by.ts.hmxy.net.SliderPacket;
import by.ts.hmxy.util.TransMsg;
import net.minecraft.client.gui.components.AbstractSliderButton;

/**
 * this will atomaticly send packet to server when player modify the value of slider
 * @author tangsoon
 */
public class PacketSlider extends AbstractSliderButton{

	private SliderPacket.Handler handler;
	private double valueMulti;
	protected TransMsg msg;
	public PacketSlider(int pX, int pY, int pWidth, int pHeight,TransMsg msg, double pValue,double valueMulti,SliderPacket.Handler handler) {
		super(pX, pY, pWidth, pHeight, msg.create(valueMulti*pValue), pValue);
		this.valueMulti=valueMulti;
		this.handler=handler;
	}

	@Override
	protected void updateMessage() {
		this.setMessage(TransMsg.SLIDER_LING_QI_CONSUME.create(this.value * valueMulti));
	}

	@Override
	protected void applyValue() {
		Messages.sendToServer(new SliderPacket(handler, value));
	}
}
