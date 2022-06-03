package by.ts.hmxy.item;

import net.minecraft.world.item.Item;

/**
 * 火焰，炼丹或者炼器的时候在将灵气转化为温度
 * @author tangsoon
 */
public class FireItem extends Item implements FireOringin{

	private float conversion;
	
	public FireItem(Properties pProperties,float conversion) {
		super(pProperties);
		this.conversion=conversion;
	}

	@Override
	public float conversionRate() {
		return this.conversion;
	}
}
