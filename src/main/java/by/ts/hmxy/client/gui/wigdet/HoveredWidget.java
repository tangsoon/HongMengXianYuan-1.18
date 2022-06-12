package by.ts.hmxy.client.gui.wigdet;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;

/**
 * 实现这个接口的widget被hover时可以rendertooltip
 * @author tangsoon
 *
 */
public interface HoveredWidget {
	@Nullable
	List<Component> getTips();
}
