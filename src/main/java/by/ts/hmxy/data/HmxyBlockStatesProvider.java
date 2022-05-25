package by.ts.hmxy.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.block.LingZhiBlock;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

/**
 * 为物品添加模型的同时为对应的方块添加模型
 * 
 * @author tangsoon
 *
 */
public class HmxyBlockStatesProvider extends BlockStateProvider {

	public static final Map<RegistryObject<Item>, BiConsumer<HmxyBlockStatesProvider, Item>> MODEL_HANDLERS = new HashMap<>();

	public HmxyBlockStatesProvider(DataGenerator gen, ExistingFileHelper helper) {
		super(gen, HmxyMod.MOD_ID, helper);
	}

	@Override
	protected void registerStatesAndModels() {
		for (Map.Entry<RegistryObject<Item>, BiConsumer<HmxyBlockStatesProvider, Item>> entry : MODEL_HANDLERS
				.entrySet()) {
			entry.getValue().accept(this, entry.getKey().get());
		}
		MODEL_HANDLERS.clear();
	}

	/**
	 * 注册的Item对应的Block应该含有Property并且会影响方块模型
	 * 
	 * @param <C>
	 * @param item
	 * @param pro
	 * @param texture 方块材质的地址，会自动包裹上"modId:block/"
	 */
	public <C extends Comparable<C>> void blockWithProperty(Item item, Property<C> pro, String textureKey,
			String texture, ResourceLocation parant) {
		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			VariantBlockStateBuilder varBuilder = HmxyBlockStatesProvider.this.getVariantBuilder(block);
			Iterator<C> it = pro.getPossibleValues().iterator();
			C c = null;
			while (it.hasNext()) {
				c = it.next();
				varBuilder.addModels(varBuilder.partialState().with(pro, c),
						new ConfiguredModel(
								models().withExistingParent(block.getRegistryName().toString() + "_" + c, parant)
										.texture(textureKey, modLoc("block/" + texture + "_" + c))));
			}
			if (c != null) {
				itemModels().withExistingParent(block.getRegistryName().toString(),
						block.getRegistryName().getNamespace() + ":block/" + block.getRegistryName().getPath() + "_"
								+ c);
			}
		}
	}

	public void blockWithDirection(Item item) {
		Property<Direction> pro = HorizontalDirectionalBlock.FACING;
		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			VariantBlockStateBuilder varBuilder = HmxyBlockStatesProvider.this.getVariantBuilder(block);
			String path = block.getRegistryName().getPath();
			ModelFile model=models().getExistingFile(modLoc("block/"+path));
			varBuilder.addModels(varBuilder.partialState().with(pro, Direction.NORTH),
					new ConfiguredModel(model, 0, 0, false));
			varBuilder.addModels(varBuilder.partialState().with(pro, Direction.EAST),
					new ConfiguredModel(model, 0, 90, false));
			varBuilder.addModels(varBuilder.partialState().with(pro, Direction.SOUTH),
					new ConfiguredModel(model, 0, 180, false));
			varBuilder.addModels(varBuilder.partialState().with(pro, Direction.WEST),
					new ConfiguredModel(model, 0, 270, false));
			itemModels().withExistingParent(block.getRegistryName().toString(), modLoc("block/"+path));
		}
	}

	public void lingZhi(Item item) {
		this.blockWithProperty(item, LingZhiBlock.AGE, "cross", item.getRegistryName().getPath(),
				mcLoc("block/tinted_cross"));
		itemModels().withExistingParent(item.getRegistryName().toString() + "_drop",
				item.getRegistryName().getNamespace() + ":item/" + item.getRegistryName().getPath());
	}

	/**
	 * 为item添加模型，这个模型只是一张的材质； 这个方法不会添加item对应的block的模型
	 * 
	 * @param item
	 * @param texture item材质的地址，会自动包裹上"modId:item/"
	 */
	public void item(Item item, String texture) {
		itemModels().getBuilder(item.getRegistryName().toString()).texture("layer0", modLoc("item/" + texture));
		itemModels().withExistingParent(item.getRegistryName().toString(), mcLoc("item/generated"));
	}

	/**
	 * 为item添加模型，这个模型只是一张的材质； 这个方法不会添加item对应的block的模型
	 * 
	 * @param item材质的地址，会自动包裹上"modId:item/"
	 */
	public void item(Item item) {
		item(item, item.getRegistryName().getPath());
	}

	/**
	 * 自动创建Item对应的Block的简单模型，并将其设置为Item的父模型
	 */
	public void block(Item item) {
		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			String path = block.getRegistryName().getPath();
			VariantBlockStateBuilder varBuilder = this.getVariantBuilder(block);
			varBuilder.partialState().addModels(new ConfiguredModel(models().cubeAll(path, modLoc("block/" + path))));
			itemModels().withExistingParent(item.getRegistryName().toString(), modLoc("block/") + path);
		}
	}

	/**
	 * 创建Item和Block的BlockState，模型是已经存在的文件
	 * 
	 * @param item
	 */
	public void blockWithExistingModels(Item item) {
		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			String path = block.getRegistryName().getPath();
			VariantBlockStateBuilder varBuilder = this.getVariantBuilder(block);
			varBuilder.partialState().addModels(new ConfiguredModel(models()
					.getExistingFile(new ResourceLocation(block.getRegistryName().getNamespace(), "block/" + path))));
			itemModels().withExistingParent(item.getRegistryName().toString(), modLoc("block/") + path);
		}
	}

	public static void defaultTexture(HmxyBlockStatesProvider b, Item i) {
		b.item(i, "default");
	}

	/**
	 * 创建流体方块和物品的模型
	 * 
	 * @param liquid
	 */
	public void liquid(Item liquid) {
		if (liquid instanceof BlockItem blockItem && blockItem.getBlock() instanceof LiquidBlock liquidBlock) {
			String path = liquid.getRegistryName().getPath();
			VariantBlockStateBuilder varBuilder = HmxyBlockStatesProvider.this.getVariantBuilder(liquidBlock);
			varBuilder.partialState().addModels(
					new ConfiguredModel(models().getBuilder(path).texture("particle", modLoc("block/" + path))));
			itemModels().getBuilder(liquid.getRegistryName().toString()).texture("layer0", modLoc("block/" + path));
			itemModels().withExistingParent(liquid.getRegistryName().toString(), mcLoc("item/generated"));
		}
	}

	/**
	 * 不会创建任何模型和材质
	 * 
	 * @param item
	 */
	public void noModel(Item item) {

	}

}
