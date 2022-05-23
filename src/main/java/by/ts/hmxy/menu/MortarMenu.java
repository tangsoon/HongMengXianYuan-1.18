package by.ts.hmxy.menu;

import javax.annotation.Nonnull;

import by.ts.hmxy.item.LingZhiItem;
import by.ts.hmxy.item.MedicineBottleItem;
import by.ts.hmxy.item.MortarItem;
import by.ts.hmxy.item.PestleItem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MortarMenu extends BaseMenu {

	ItemStack mortar;
	MortarItem.Data data;

	public MortarMenu(int windowId, Inventory playerInventory, Player player, ItemStack mortar) {
		super(MenuTypes.MORTAR.get(), windowId, playerInventory);
		this.mortar = mortar;
		this.data = ((MortarItem) mortar.getItem()).getData(mortar);
		this.addInvToMenu(16, 149, 16, 91);
		ItemStackHandler ish = new ItemStackHandler(data.stacks) {
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
				Item item = stack.getItem();
				if ((slot == 0 && item instanceof LingZhiItem) || (slot == 1 && item instanceof PestleItem)
						|| (slot == 2 && item instanceof MedicineBottleItem)) {
					return super.isItemValid(slot, stack);
				}
				return false;
			}
		};
		this.addSlot(new SlotItemHandler(ish, 0, 43, 47));
		this.addSlot(new SlotItemHandler(ish, 1, 88, 25));
		this.addSlot(new SlotItemHandler(ish, 2, 133, 47));
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return true;
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		// TODO 快速移动
		return ItemStack.EMPTY;
	}

	public void removed(Player pPlayer) {
		if (!pPlayer.level.isClientSide) {
			this.data.save(mortar);
		}
	}
}