package by.ts.hmxy.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import by.ts.hmxy.HmxyMod;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
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

	public static final Map<RegistryObject<Item>, BiConsumer<HmxyBlockStatesProvider,Item>> MODEL_HANDLERS = new HashMap<>();

	public HmxyBlockStatesProvider(DataGenerator gen, ExistingFileHelper helper) {
		super(gen, HmxyMod.MOD_ID, helper);
	}

	@Override
	protected void registerStatesAndModels() {
		for (Map.Entry<RegistryObject<Item>, BiConsumer<HmxyBlockStatesProvider,Item>> entry : MODEL_HANDLERS.entrySet()) {
			entry.getValue().accept(this, entry.getKey().get());
		}
		MODEL_HANDLERS.clear();
//		this.simpleBlockAndItem(HmxyBlocks.REIKI_STONE_ORE.get());
//		this.simpleBlockAndItem(HmxyBlocks.REIKI_STONE_ORE_FLICKER.get());
//		lingZhi(HmxyBlocks.DENG_XIN_CAO.get(),LingZhiBlock.AGE,"deng_xin_cao");
	}

//	private void simpleBlockAndItem(Block block) {
//		super.simpleBlock(block, cubeAll(block));
//		this.itemModels().withExistingParent(block.getRegistryName().toString(),
//				block.getRegistryName().getNamespace() + ":block/" + block.getRegistryName().getPath());
//	}

	/**
	 * 注册的Item对应的Block应该含有Property并且会影响方块模型
	 * 
	 * @param <C>  Property的属性类型
	 * @param item
	 * @param pro
	 */
	public <C extends Comparable<C>> void itemWithProperty(Item item, Property<C> pro) {
		this.itemWithProperty(item, pro, item.getRegistryName().getPath());
	}

	/**
	 * 注册的Item对应的Block应该含有Property并且会影响方块模型
	 * 
	 * @param <C>
	 * @param item
	 * @param pro
	 * @param texture 方块材质的地址，会自动包裹上"modId:block/"
	 */
	public <C extends Comparable<C>> void itemWithProperty(Item item, Property<C> pro, String texture) {
		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			VariantBlockStateBuilder varBuilder = HmxyBlockStatesProvider.this.getVariantBuilder(block);
			Iterator<C> it = pro.getPossibleValues().iterator();
			C c = null;
			if (it.hasNext()) {
				c = it.next();
				varBuilder.addModels(varBuilder.partialState().with(pro, c), new ConfiguredModel(models()
						.cross(block.getRegistryName().toString() + "_" + c, modLoc("block/" + texture + "_" + c))));
			}
			if (c != null) {
				itemModels().withExistingParent(block.getRegistryName().toString(),
						block.getRegistryName().getNamespace() + ":block/" + block.getRegistryName().getPath() + c);
			}
		}
	}

	/**
	 * 为item添加模型，这个模型只是一张的材质； 这个方法不会添加item对应的block的模型
	 * 
	 * @param item
	 * @param texture item的材质地址，会自动
	 */
	public void item(Item item, String texture) {
		itemModels().getBuilder(item.getRegistryName().toString()).texture("layer0",modLoc("item/"+texture));
	}

	/**
	 * 为item添加模型，这个模型只是一张的材质； 这个方法不会添加item对应的block的模型
	 * 
	 * @param item
	 */
	public void item(Item item) {
		item(item,item.getRegistryName().getPath());
	}
	
	public static void defaultTexture(HmxyBlockStatesProvider b,Item i) {
		b.item(i,"default");
	}
	public static void selfTexture(HmxyBlockStatesProvider b,Item i) {
		b.item(i,"default");
	}
}
