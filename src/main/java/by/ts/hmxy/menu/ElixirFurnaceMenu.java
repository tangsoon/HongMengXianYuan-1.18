package by.ts.hmxy.menu;

import javax.annotation.Nullable;
import by.ts.hmxy.block.ElixirFurnaceBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;

public class ElixirFurnaceMenu extends BaseBlockMenu<ElixirFurnaceBlock.ElixirFurnaceBE> {
	private ElixirFurnaceBlock.Data blockEntityData;

	public ElixirFurnaceMenu(int pContainerId, Inventory inv, @Nullable BlockPos pos) {
		super(MenuTypes.ELIXIR_FURNACE.get(), pContainerId, inv, pos);
		this.addInvToMenu(16, 167, 16, 109);
		this.blockEntityData = this.getBe().getData();
		this.addSlots(blockEntityData.getBottles(), this.blockEntityData.getElixirInvCount(), 16, 55);
		this.addSlots(blockEntityData.getLingZhiHandler(), this.blockEntityData.getElixirInvCount(), 16, 25);
		this.addSlots(blockEntityData.getFurnaceCover(), 1, 74, 80);
		this.addSlots(blockEntityData.getElixirRecipe(), 1, 96, 80);		
		this.addDataSlots(this.blockEntityData);
	}
}
