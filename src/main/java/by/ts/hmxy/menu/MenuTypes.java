package by.ts.hmxy.menu;
import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.item.HmxyItems;
import by.ts.hmxy.item.MortarItem;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuTypes {
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, HmxyMod.MOD_ID);
	public static final RegistryObject<MenuType<MortarMenu>> MORTAR = MENU_TYPES.register("mortar",
            () -> IForgeMenuType.create((windowId, inv, data) -> (((MortarItem)HmxyItems.MORTAR.get()).createMenu(windowId, inv, inv.player))));
	public static final RegistryObject<MenuType<ElixirFurnaceRootMenu>> ELIXIR_FURNACE_ROOT = MENU_TYPES.register("elixir_furnace_root",
            () -> IForgeMenuType.create((windowId,inv,data)->{return new ElixirFurnaceRootMenu(windowId,inv, data.readBlockPos());}));
	
}