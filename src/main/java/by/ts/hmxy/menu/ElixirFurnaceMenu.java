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
		this.addSlots(blockEntityData.getBottles(), this.blockEntityData.getElixirInvCount(), 16, 55,true);
		this.addSlots(blockEntityData.getLingZhiHandler(), this.blockEntityData.getElixirInvCount(), 16, 25,true);
		this.addSlots(blockEntityData.getFurnaceCover(), 1, 45, 80,false);
		this.addSlots(blockEntityData.getElixirRecipe(), 1, 67, 80,false);
		this.addSlots(blockEntityData.getElixir(), 1, 128, 80, false);
		this.addDataSlots(this.blockEntityData);
	}
}
