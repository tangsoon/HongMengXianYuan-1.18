package by.ts.hmxy.util;

import by.ts.hmxy.HmxyMod;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Attrs {

	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, HmxyMod.MOD_ID);
	/** 灵力上限 */
	public static final RegistryObject<Attribute> MAX_LING_LI = ATTRIBUTES.register("max_ling_li",
			() -> new RangedAttribute("attribute.name.generic.max_ling_li", 20.0D, 1.0D, 1024.0D).setSyncable(true));
	/**冲刺时每tick灵力消耗*/
	public static final RegistryObject<Attribute> LING_LI_CONSUME_WHEN_PRINTING = ATTRIBUTES.register("ling_li_consume_when_printing",
			() -> new RangedAttribute("attribute.name.generic.ling_li_consume_when_printing", 0.05D, 0.0D, 1024.0D).setSyncable(true));
	
	/**每tick灵力恢复*/
	public static final RegistryObject<Attribute> LING_LI_RESUME = ATTRIBUTES.register("ling_li_resume",
			() -> new RangedAttribute("attribute.name.generic.ling_li_resume", 0.0025D, 0.0D, 1024.0D).setSyncable(true));
}
