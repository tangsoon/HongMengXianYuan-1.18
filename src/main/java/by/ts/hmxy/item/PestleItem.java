package by.ts.hmxy.item;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import by.ts.hmxy.util.HmxyTier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.ItemLike;

/**
 * 杵，用于捣碎药物
 * 
 * @author tangsoon
 *
 */
public class PestleItem extends DiggerItem {

	public PestleItem(Tier pTier) {
		super(0F, -3F, pTier, BlockTags.ANVIL, new Properties().tab(Tabs.TOOL).fireResistant());
	}

	public static final ImmutableList<PestleTier> PESTLE_TIERS = new ImmutableList.Builder<PestleTier>()
			.add(new PestleTier(Tiers.STONE, () -> Items.STONE, () -> Items.STONE, "stone", "石"),
					new PestleTier(Tiers.IRON, () -> Items.IRON_BLOCK, () -> Items.IRON_INGOT, "iron", "铁"),
					new PestleTier(Tiers.GOLD, () -> Items.GOLD_BLOCK, () -> Items.GOLD_INGOT, "golden", "金"),
					new PestleTier(Tiers.DIAMOND, () -> Items.DIAMOND_BLOCK, () -> Items.DIAMOND, "diamond", "钻石"),
					new PestleTier(Tiers.NETHERITE, () -> Items.NETHERITE_BLOCK, () -> Items.NETHERITE_INGOT,
							"netherite", "下界合金"))
			.build();

	public static class PestleTier extends HmxyTier {

		public final Supplier<ItemLike> BOTTOM;
		public final Supplier<ItemLike> STICK;
		public final String PREFIX_NAME;
		public final String PREFIX_NAME_ZH;

		@SuppressWarnings("deprecation")
		public PestleTier(Tier tier, Supplier<ItemLike> BOTTOM, Supplier<ItemLike> STICK, String prefixName,
				String prefixNameZh) {
			super(tier.getLevel(), tier.getUses(), tier.getSpeed(), tier.getAttackDamageBonus(),
					tier.getEnchantmentValue(), tier.getRepairIngredient());
			this.BOTTOM = BOTTOM;
			this.STICK = STICK;
			this.PREFIX_NAME = prefixName;
			this.PREFIX_NAME_ZH = prefixNameZh;
		}
	}
}
