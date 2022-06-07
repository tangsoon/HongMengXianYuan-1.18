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
	 * 
	 * @param pMenuType
	 * @param pContainerId
	 * @param inv
	 */
	protected BaseMenu(MenuType<?> pMenuType, int pContainerId, Inventory inv) {
		super(pMenuType, pContainerId);
		this.playerInventory = new InvWrapper(inv);
		this.player = inv.player;
		this.level = player.level;
	}

	/**
	 * Add the inventory of player to the menu, you should call this in your
	 * constructor
	 */
	public void addInvToMenu(int shortcutsX, int shortcutsY, int invX, int invY) {
		this.addSlots(playerInventory, 0, 9, shortcutsX, shortcutsY);
		this.addSlots(playerInventory, 9, 27, invX, invY);
	}

	public void addSlots(IItemHandler handler, int count, int startPosX, int startPosY) {
		this.addSlots(handler, 0, count, startPosX, startPosY);
	}

	public void addSlots(IItemHandler handler, int startIndex, int count, int startPosX, int startPosY) {
		this.addSlots(handler, startIndex, count, 9, startPosX, startPosY);
	}

	public void addSlots(IItemHandler handler, int startIndex, int count, int colCount, int startPosX, int startPosY) {
		this.addSlots(handler, startIndex, count, colCount, startPosX, startPosY, 0, 0);
	}

	/**
	 * Add items in the handler to slots of menu.
	 * 
	 * @param handler
	 * @param startIndex handler中物品的起始位置
	 * @param count      How many itemstack will be add to the menu.
	 * @param colCount   一行有多少个slot
	 * @param startPosX  slot开始位置x
	 * @param startPosY  slot开始位置y
	 * @param colSpace   两个slot的列距
	 * @param rowSpace   两个slot的行距
	 */
	public void addSlots(IItemHandler handler, int startIndex, int count, int colCount, int startPosX, int startPosY,
			int colSpace, int rowSpace) {
		for (int i = 0; i < count; i++) {
			int stackIndex = i + startIndex;
			int colPosition = startPosX + i % colCount * (18 + colSpace);
			int rowPosition = startPosY + i / colCount * (18 + rowSpace);
			this.addSlot(new SlotItemHandler(handler, stackIndex, colPosition, rowPosition));
		}
	}

}
