package by.ts.hmxy.client.key;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;

public class KeyBindings {

    public static final String KEY_CATEGORIES_HMXY = "key.categories.hmxy";
    public static final String KEY_GATHER_TEST = "key.test";

    public static KeyMapping testKeyMapping;

    public static void init() {
    	testKeyMapping = new KeyMapping(KEY_GATHER_TEST, KeyConflictContext.IN_GAME, InputConstants.getKey("key.keyboard.period"), KEY_CATEGORIES_HMXY);
          ClientRegistry.registerKeyBinding(testKeyMapping);
    }
}