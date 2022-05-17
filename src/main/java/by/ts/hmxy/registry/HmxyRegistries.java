package by.ts.hmxy.registry;

import java.util.function.Supplier;
import by.ts.hmxy.util.Gene;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

public class HmxyRegistries {
	private static final int MAX_VARINT = Integer.MAX_VALUE - 1;
	public static Supplier<IForgeRegistry<Gene>> GENES;
	public static final ResourceKey<Registry<Gene>> GENES_KEY = key("genes");

	@SubscribeEvent
	public void handle(NewRegistryEvent event) {
		HmxyRegistries.GENES = event.create(HmxyRegistries.makeRegistry(HmxyRegistries.GENES_KEY, Gene.class, "null"));
	}
	
	private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(
			ResourceKey<? extends Registry<T>> key, Class<T> type, String _default) {
		return new RegistryBuilder<T>().setName(key.location()).setType(type).setMaxID(MAX_VARINT)
				.setDefaultKey(new ResourceLocation(_default));
	}

	private static <T> ResourceKey<Registry<T>> key(String name) {
		return ResourceKey.createRegistryKey(new ResourceLocation(name));
	}
}
