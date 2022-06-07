package by.ts.hmxy.client.gui.wigdet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import by.ts.hmxy.client.gui.BaseSreen;
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

	private  BaseSreen<?> screen;
	private Supplier<List<Component>> componentSupplier;
	
	public SlotWidget(int absX, int absY, int pWidth, int pHeight, Component pMessage, ResourceLocation texture,
			int texBgU, int texBgV,BaseSreen<?> screen,Supplier<List<Component>> componentSupplier) {
		super(absX, absY, pWidth, pHeight, pMessage, texture, texBgU, texBgV);
		this.screen=screen;
		this.componentSupplier=componentSupplier;
	}

	@Override
	public List<Component> getTips() {
		Slot hoveredSlot=screen.getHoverdSlot();
		if(hoveredSlot!=null&&hoveredSlot.getItem()==ItemStack.EMPTY) {
			return new ArrayList<>();
		}
		return componentSupplier.get();
	}
}
