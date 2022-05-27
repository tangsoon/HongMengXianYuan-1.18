package by.ts.hmxy.menu;

import javax.annotation.Nullable;

import by.ts.hmxy.block.ElixirFurnaceRootBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;

public class ElixirFurnaceRootMenu extends BaseBlockMenu<ElixirFurnaceRootBlock.ElixirFurnaceRootBE> {
	public ElixirFurnaceRootMenu(int pContainerId, Inventory inv,@Nullable BlockPos pos) {
		super(MenuTypes.ELIXIR_FURNACE_ROOT.get(), pContainerId, inv,pos);
		this.addInvToMenu(0, 0, 0, 24);	
	}
}
