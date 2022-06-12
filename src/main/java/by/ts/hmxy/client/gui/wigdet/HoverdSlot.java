package by.ts.hmxy.client.gui.wigdet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import by.ts.hmxy.util.Textures;
import by.ts.hmxy.util.TransMsg;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * 渲染物品Slot, 并附带tip
 * @author tangsoon
 *
 */
public class HoverdSlot extends HoveredWidgetImp {
	
	private  AbstractContainerScreen<?> screen;
	private Supplier<List<Component>> componentSupplier;
	
	public HoverdSlot(int absX, int absY, AbstractContainerScreen<?> screen,Supplier<List<Component>> componentSupplier) {
		super(absX, absY, 18, 18, TransMsg.EMPTY, Textures.GENERAL_UI, 0, 0);
		this.setColor(0xe6cfb3);
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
