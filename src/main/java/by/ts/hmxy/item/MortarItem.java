package by.ts.hmxy.item;

import by.ts.hmxy.menu.MortarMenu;
import by.ts.hmxy.util.HmxyHelper;
import by.ts.hmxy.util.TransMsg;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 臼，用于将灵植捣碎的容器
 * @author tangsoon
 *
 */
public class MortarItem extends Item implements MenuProvider{
	public MortarItem() {
		super(new Properties().tab(Tabs.SUNDRY).stacksTo(64));
	}
	
	public void setData(ItemStack stack,Data data) {
		data.save(stack);
	}
	
	public Data getData(ItemStack stack) {
		return new Data(stack);
	}

	public static class Data {
		
		public NonNullList<ItemStack> stacks;
		public Data(ItemStack mortar) {
			ListTag listTag=mortar.getOrCreateTag().getList("mortar", ListTag.TAG_COMPOUND);
			if(listTag.size()!=0) {
				stacks=HmxyHelper.stacks(listTag);
			}
			else {
				stacks= NonNullList.withSize(3, ItemStack.EMPTY);
			}
		}
		public void save(ItemStack mortar) {
			mortar.addTagElement("mortar", HmxyHelper.listTag(stacks));
		}
	}
	
	 public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
		 ItemStack mainStack=pPlayer.getItemInHand(pUsedHand);
		 if(!pLevel.isClientSide) {
			HmxyHelper.openGui(pPlayer, this);
		 }
		 return InteractionResultHolder.success(mainStack);//success只有客户端执行，consume客户端和服务器都执行
	 }

	@Override
	public MortarMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
		return new MortarMenu(pContainerId, pInventory, pPlayer,pPlayer.getItemInHand(InteractionHand.MAIN_HAND));
	}

	@Override
	public Component getDisplayName() {
		return TransMsg.CONTAINER_MORTAR.get();
	}
}
