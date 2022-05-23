package by.ts.hmxy.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public abstract class BaseMenu extends AbstractContainerMenu {

	private IItemHandler playerInventory;

	protected BaseMenu(MenuType<?> pMenuType, int pContainerId, Inventory inv) {
		super(pMenuType, pContainerId);
		this.playerInventory = new InvWrapper(inv);
	}

	/**
	 * Add the inventory of player to the menu
	 */
	public void addInvToMenu(int shortcutsX, int shortcutsY, int invX, int invY) {
		for (int i = 0; i < 9; i++) {
			this.addSlot(new SlotItemHandler(this.playerInventory, i, shortcutsX + i * 18, shortcutsY));
		}
		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < 9; i++) {
				this.addSlot(new SlotItemHandler(this.playerInventory, i + j * 9 + 9, invX + i * 18, invY + j * 18));
			}
		}
	}
}
