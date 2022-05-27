package by.ts.hmxy.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public abstract class BaseMenu extends AbstractContainerMenu {

	protected IItemHandler playerInventory;
	protected Player player;
	protected Level level;
	/**
	 * 仅在注册的时候调用
	 * @param pMenuType
	 * @param pContainerId
	 * @param inv
	 */
	protected BaseMenu(MenuType<?> pMenuType, int pContainerId, Inventory inv) {
		super(pMenuType, pContainerId);
		this.playerInventory = new InvWrapper(inv);
		this.player=inv.player;
		this.level=player.level;
	}

	/**
	 * Add the inventory of player to the menu, you should call this in your constructor
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
