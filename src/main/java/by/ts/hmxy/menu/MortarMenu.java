package by.ts.hmxy.menu;

import javax.annotation.Nonnull;

import by.ts.hmxy.block.LingZhiBlock.LingZhiBE;
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

	public final static int	 LING_ZHI_SLOT=0;
	public final static int	 PESTLE_SLOT=1;
	public final static int	 MEDICINE_BOTTLE_SLOT=2;
	ItemStackHandler ish;
	
	public MortarMenu(int windowId, Inventory playerInventory, Player player, ItemStack mortar) {
		super(MenuTypes.MORTAR.get(), windowId, playerInventory);
		this.mortar = mortar;
		this.data = ((MortarItem) mortar.getItem()).getData(mortar);
		this.addInvToMenu(16, 149, 16, 91);
		ish = new ItemStackHandler(data.stacks) {
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
				Item item = stack.getItem();
				if ((slot == LING_ZHI_SLOT && item instanceof LingZhiItem) || (slot == PESTLE_SLOT && item instanceof PestleItem)
						|| (slot ==MEDICINE_BOTTLE_SLOT && item instanceof MedicineBottleItem)) {
					return super.isItemValid(slot, stack);
				}
				return false;
			}
		};
		this.addSlot(new SlotItemHandler(ish, LING_ZHI_SLOT, 43, 47));
		this.addSlot(new SlotItemHandler(ish, PESTLE_SLOT, 88, 25));
		this.addSlot(new SlotItemHandler(ish, MEDICINE_BOTTLE_SLOT, 133, 47));
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
	
	public void onCraft() {
		ItemStack lingZhiStack=ish.getStackInSlot(LING_ZHI_SLOT);
		ItemStack pestleStack=ish.getStackInSlot(PESTLE_SLOT);
		ItemStack medicineBottleStack=ish.getStackInSlot(MEDICINE_BOTTLE_SLOT);
		if(!lingZhiStack.isEmpty()&&!pestleStack.isEmpty()&&!medicineBottleStack.isEmpty()) {
			LingZhiItem lingZhi=(LingZhiItem) lingZhiStack.getItem();
			LingZhiBE be=lingZhi.getLingZhiBE(lingZhiStack);
			MedicineBottleItem bottle=(MedicineBottleItem) medicineBottleStack.getItem();
			MedicineBottleItem.Data bottleData=bottle.getData(medicineBottleStack);
			if(be.getLingZhi()==bottleData.getLingZhi()&&bottleData.getQuality()<MedicineBottleItem.CAPACITY) {
				float medicinal=(be.getMedicinal()+bottleData.getEffective()*bottleData.getQuality());
				bottleData.setQuality(bottleData.getQuality()+1);
				bottleData.setEffective(medicinal/bottleData.getQuality());
				bottle.saveData(medicineBottleStack, bottleData);
				lingZhiStack.shrink(1);
				pestleStack.shrink(1);
			}
		}
	}
}