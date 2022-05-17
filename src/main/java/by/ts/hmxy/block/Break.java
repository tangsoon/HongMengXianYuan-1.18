package by.ts.hmxy.block;

import net.minecraftforge.event.world.BlockEvent.BreakEvent;

/**
 * 在方块被破坏时调用
 * @author tangsoon
 *
 */
public interface Break {
	void onBreak(BreakEvent event);
}
