package by.ts.hmxy.entity;

import by.ts.hmxy.HmxyMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HmxyEntities {

	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES,
			HmxyMod.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HmxyMod.MOD_ID);

	/**灵气*/
	public static final RegistryObject<EntityType<MinbusOrb>> MINBUS_ORB = ENTITIES.register("minbus_orb",()->
			EntityType.Builder.<MinbusOrb>of(MinbusOrb::new, MobCategory.MISC).sized(0.5F, 0.5F)
					.clientTrackingRange(6).updateInterval(20).build("minbus_orb"));
	/**扔出的灵气瓶*/
	public static final RegistryObject<EntityType<ThrownMinbusBottle>> THROWN_MINBUS_BOTTLE = ENTITIES.register("thrown_minbus_bottle",()->
	EntityType.Builder.<ThrownMinbusBottle>of(ThrownMinbusBottle::new, MobCategory.MISC).sized(0.25F, 0.25F)
			.clientTrackingRange(6).updateInterval(20).build("thrown_minbus_bottle"));
}