package by.ts.hmxy.client.key;

import net.minecraftforge.client.event.InputEvent;

public class KeyInputHandler {

    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeyBindings.testKeyMapping.consumeClick()) {
        	System.out.println("客户端按键");
        }
    }
}