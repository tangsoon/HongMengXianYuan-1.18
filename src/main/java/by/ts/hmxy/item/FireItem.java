package by.ts.hmxy.item;

import net.minecraft.world.item.Item;

/**
 * 火焰，炼丹或者炼器的时候在将灵气转化为温度
 * @author tangsoon
 */
public class FireItem extends Item{
	/**灵气转化效率*/
	private float efficincy;
	
	public FireItem(Properties pProperties,float efficincy) {
		super(pProperties);
		this.efficincy=efficincy;
	}

	public float getEfficincy() {
		return efficincy;
	}
}
