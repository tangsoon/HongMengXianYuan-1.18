package by.ts.hmxy.data;

import java.util.function.Supplier;

import by.ts.hmxy.block.HmxyBlocks;
import by.ts.hmxy.item.HmxyItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class HmxyLootTableProvider extends AbstractLootTableProvider<Supplier<ResourceLocation>> {

	public HmxyLootTableProvider(DataGenerator pGenerator) {
		super(pGenerator);
	}

	@Override
	public void addLootTables() {
//		this.addLootTable(() -> HmxyBlocks.REIKI_STONE_ORE.get().getLootTable(), LootTable.lootTable().withPool(LootPool
//				.lootPool().setRolls(ConstantValue.exactly(1.0F)).setBonusRolls(ConstantValue.exactly(0.0F)).add(LootItem.lootTableItem(HmxyItems.NATURE_REIKI_STONE.get()))));
	}

}
