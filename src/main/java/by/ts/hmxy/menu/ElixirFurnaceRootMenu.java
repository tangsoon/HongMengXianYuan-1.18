package by.ts.hmxy.menu;

import javax.annotation.Nullable;

import by.ts.hmxy.block.ElixirFurnaceRootBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.SlotItemHandler;

public class ElixirFurnaceRootMenu extends BaseBlockMenu<ElixirFurnaceRootBlock.ElixirFurnaceRootBE> {
	public ElixirFurnaceRootMenu(int pContainerId, Inventory inv,@Nullable BlockPos pos) {
		super(MenuTypes.ELIXIR_FURNACE_ROOT.get(), pContainerId, inv,pos);
		this.addInvToMenu(16, 149, 16, 91);	
		this.addSlot(new SlotItemHandler(be.getStacks(), 0, 30, 31 ) {
			
		});//fire
		this.addSlot(new SlotItemHandler(be.getStacks(), 1, 30, 57));//fuel
		this.addDataSlots(be);
	}
}
   