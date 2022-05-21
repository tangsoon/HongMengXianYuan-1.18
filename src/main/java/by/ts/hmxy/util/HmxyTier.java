package by.ts.hmxy.util;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class HmxyTier implements Tier {

	private final int level;
	private final int uses;
	private final float speed;
	private final float damage;
	private final int enchantmentValue;
	private final Ingredient repairIngredient;

	@SuppressWarnings("deprecation")
	public HmxyTier(Tier tier) {
		this(tier.getLevel(), tier.getUses(),tier.getSpeed(), tier.getAttackDamageBonus(), tier.getEnchantmentValue(),
				tier.getRepairIngredient());
	}

	public HmxyTier(int level, int uses, float speed, float damage, int enchantmentValue, Ingredient repairIngredient) {
		this.level = level;
		this.uses = uses;
		this.speed = speed;
		this.damage = damage;
		this.enchantmentValue = enchantmentValue;
		this.repairIngredient = repairIngredient;
	}

	public int getLevel() {
		return level;
	}

	public int getUses() {
		return uses;
	}

	public float getSpeed() {
		return speed;
	}

	public int getEnchantmentValue() {
		return enchantmentValue;
	}

	public Ingredient getRepairIngredient() {
		return repairIngredient;
	}

	@Override
	public float getAttackDamageBonus() {
		return damage;
	}

}