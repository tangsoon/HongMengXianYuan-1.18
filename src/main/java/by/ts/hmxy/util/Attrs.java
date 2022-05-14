package by.ts.hmxy.util;

import by.ts.hmxy.HmxyMod;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Attrs {
	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES,
			HmxyMod.MOD_ID);
	//----------------------------------------拥有境界的生物的属性----------------------------------------
	/** 灵力上限 */
	public static final RegistryObject<Attribute> MAX_LING_LI = ATTRIBUTES.register("max_ling_li",
			() -> new RangedAttribute("attribute.name.generic.max_ling_li", 20.0D, 1.0D, 1024.0D).setSyncable(true));

	/** 每tick灵力恢复 */
	public static final RegistryObject<Attribute> LING_LI_RESUME = ATTRIBUTES.register("ling_li_resume",
			() -> new RangedAttribute("attribute.name.generic.ling_li_resume", 0.0025D, 0.0D, 1024.0D)
					.setSyncable(true));

	/** 冲刺、游泳、滑翔时每tick耐力消耗 */
	public static final RegistryObject<Attribute> STAMINA_CONSUME = ATTRIBUTES.register("stamina_consume",
			() -> new RangedAttribute("attribute.name.generic.stamina_consume", 0.05D, 0.0D, 1024.0D)
					.setSyncable(true));

	/** 耐力上限 */
	public static final RegistryObject<Attribute> MAX_STAMINA = ATTRIBUTES.register("max_stamina",
			() -> new RangedAttribute("attribute.name.generic.ling_li_resume", 20.0D, 1.0D, 1024.0D).setSyncable(true));

	/** 每tick灵力耐力 */
	public static final RegistryObject<Attribute> STAMINA_RESUME = ATTRIBUTES.register("stamina_resume",
			() -> new RangedAttribute("attribute.name.generic.stamina_resume", 0.05, 0.0D, 1024.0D)
					.setSyncable(true));
	
	
}
