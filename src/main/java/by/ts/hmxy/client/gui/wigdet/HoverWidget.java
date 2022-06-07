package by.ts.hmxy.client.gui.wigdet;

import java.util.List;

import net.minecraft.network.chat.Component;

/**
 * 实现这个接口的widget被hover时可以rendertooltip
 * @author tangsoon
 *
 */
public interface HoverWidget {
	List<Component> getTips();
}
