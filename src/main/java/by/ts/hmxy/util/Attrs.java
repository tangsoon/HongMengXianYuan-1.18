package by.ts.hmxy.util;

import by.ts.hmxy.HmxyMod;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Attrs {

	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, HmxyMod.MOD_ID);
	
	/** 灵力上限 */
	public static final RegistryObject<Attribute> MAX_LING_LI = ATTRIBUTES.register("max_ling_li",
			() -> new RangedAttribute("attribute.name.generic.max_ling_li", 20.0D, 1.0D, 1024.0D).setSyncable(true));
	
}
