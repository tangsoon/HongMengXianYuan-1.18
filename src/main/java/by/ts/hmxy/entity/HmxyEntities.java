package by.ts.hmxy.entity;

import java.util.function.Supplier;

import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.data.HmxyLanguageProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HmxyEntities {

	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES,
			HmxyMod.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HmxyMod.MOD_ID);

	public static final RegistryObject<EntityType<MinbusOrb>> MINBUS_ORB = register("minbus_orb", "灵气",
			() -> EntityType.Builder.<MinbusOrb>of(MinbusOrb::new, MobCategory.MISC).sized(0.5F, 0.5F)
					.clientTrackingRange(6).updateInterval(20).build("minbus_orb"));
	public static final RegistryObject<EntityType<ThrownMinbusBottle>> THROWN_MINBUS_BOTTLE = register(
			"thrown_minbus_bottle", "扔出的灵气瓶",
			() -> EntityType.Builder.<ThrownMinbusBottle>of(ThrownMinbusBottle::new, MobCategory.MISC)
					.sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(20).build("thrown_minbus_bottle"));

	public static final <T extends Entity> RegistryObject<EntityType<T>> register(String name, String nameZh,
			Supplier<EntityType<T>> sup) {
		RegistryObject<EntityType<T>> obj = ENTITIES.register(name, sup);
		if(DatagenModLoader.isRunningDataGen()) {
			HmxyLanguageProvider.ENTITY_NAME.put("entity."+HmxyMod.MOD_ID+"."+name, nameZh);	
		}
		return obj;
	}
}