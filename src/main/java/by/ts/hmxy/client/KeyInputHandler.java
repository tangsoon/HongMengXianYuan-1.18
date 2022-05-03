package by.ts.hmxy.client;

import by.ts.hmxy.key.KeyBindings;
import net.minecraftforge.client.event.InputEvent;

public class KeyInputHandler {

    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeyBindings.testKeyMapping.consumeClick()) {
            //Messages.sendToServer(new PacketGatherMana());
        	System.out.println();
        }
    }
}