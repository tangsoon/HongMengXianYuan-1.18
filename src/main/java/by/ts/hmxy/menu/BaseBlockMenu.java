package by.ts.hmxy.menu;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * This menu has a position
 * @author tangsoon
 *
 */
public abstract class BaseBlockMenu<E extends BlockEntity> extends BaseMenu {

	@Nullable
	protected BlockPos pos;
	
	protected E be;
	
	@SuppressWarnings("unchecked")
	protected BaseBlockMenu(MenuType<?> pMenuType, int pContainerId, Inventory inv,BlockPos pos) {
		super(pMenuType, pContainerId, inv);
		this.pos=pos;
		be=(E) level.getBlockEntity(pos);
	}

	@Override
	public boolean stillValid(Player pPlayer) {
		return pPlayer.distanceToSqr(pos.getX(), pos.getY(), pos.getZ())<8;
	}

	public BlockPos getPos() {
		return pos;
	}

	public void setPos(BlockPos pos) {
		this.pos = pos;
	}

	public E getBe() {
		return be;
	}

	public void setBe(E be) {
		this.be = be;
	}
}
