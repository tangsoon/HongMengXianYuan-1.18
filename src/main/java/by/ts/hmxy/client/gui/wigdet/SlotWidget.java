package by.ts.hmxy.client.gui.wigdet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.client.gui.BaseSreen;
import by.ts.hmxy.util.TransMsg;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * 渲染物品Slot, 并附带tip
 * @author tangsoon
 *
 */
public class SlotWidget extends HoverWidgetImp{

	private static final ResourceLocation TEXTURE=new ResourceLocation(HmxyMod.MOD_ID, "textures/gui/general.png");
	
	private  AbstractContainerScreen<?> screen;
	private Supplier<List<Component>> componentSupplier;
	
	public SlotWidget(int absX, int absY, AbstractContainerScreen<?> screen,Supplier<List<Component>> componentSupplier) {
		super(absX, absY, 18, 18, TransMsg.EMPTY, TEXTURE, 0, 0);
		this.screen=screen;
		this.componentSupplier=componentSupplier;
	}

	@Override
	public List<Component> getTips() {
		Slot hoveredSlot=screen.hoveredSlot;
		if(hoveredSlot!=null&&hoveredSlot.getItem()!=ItemStack.EMPTY) {
			return new ArrayList<>();
		}
		return componentSupplier.get();
	}
}
