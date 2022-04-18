package com.ts.hmxy.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
//copy from https://forge.gemwire.uk/wiki/Datageneration/Loot_Tables and do a little change.
public abstract class AbstractLootTableProvider<T extends Supplier<ResourceLocation>> extends LootTableProvider {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	protected final Map<T, LootTable.Builder> lootTables = new HashMap<>();
	public static Map<ResourceLocation, LootTable> tables = new HashMap<>();
	protected final DataGenerator generator;

	public AbstractLootTableProvider(DataGenerator pGenerator) {
		super(pGenerator);
		this.generator = pGenerator;
	}

	public void run(HashCache pCache) {
		this.addLootTables();
		lootTables.forEach((supplier, builder) -> {
			tables.put(supplier.get(), builder.build());
		});
		this.writeTables(pCache, tables);
	}

	public abstract void addLootTables();

	public void addLootTable(T supplier, LootTable.Builder builder) {
		lootTables.put(supplier, builder);
	}

	private void writeTables(HashCache cache, Map<ResourceLocation, LootTable> tables) {
		Path outputFolder = this.generator.getOutputFolder();
		tables.forEach((key, lootTable) -> {
			Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
			try {
				DataProvider.save(GSON, cache, LootTables.serialize(lootTable), path);
			} catch (IOException e) {
				LogManager.getLogger().error("Couldn't write loot table {}", path, (Object) e);
			}
		});
	}

	public String getName() {
		return this.getClass().getName();
	}
}
