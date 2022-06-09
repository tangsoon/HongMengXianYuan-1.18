package by.ts.hmxy.client.gui;

import by.ts.hmxy.menu.MenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class Screens {
	public static void init(FMLClientSetupEvent event) {
		event.enqueueWork(()->{
			MenuScreens.register(MenuTypes.MORTAR.get(), MortarScreen::new);
			MenuScreens.register(MenuTypes.ELIXIR_FURNACE_ROOT.get(),ElixirFurnaceRootScreen::new);
			MenuScreens.register(MenuTypes.ELIXIR_FURNACE.get(),ImageScreen::new);
		});
	}
}
