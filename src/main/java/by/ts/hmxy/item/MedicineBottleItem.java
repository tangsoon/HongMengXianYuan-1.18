package by.ts.hmxy.item;

import java.util.List;
import javax.annotation.Nullable;
import by.ts.hmxy.block.HmxyBlocks;
import by.ts.hmxy.block.LingZhiBlock;
import by.ts.hmxy.util.ItemStackData;
import by.ts.hmxy.util.TransMsg;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 药瓶，用于盛放灵植粉末
 * 
 * @author tangsoon
 */
public class MedicineBottleItem extends Item {
	public MedicineBottleItem(Properties pProperties) {
		super(pProperties);
	}

	public static class Data implements INBTSerializable<CompoundTag>, ItemStackData {
		public static final int CAPACITY = 64;

		private LingZhiBlock lingZhi = (LingZhiBlock) HmxyBlocks.DENG_XIN_CAO.get();// 默认
		private float effective = 0.0F;
		private int quality=0;

		private Data(LingZhiBlock lingZhi) {
			this.lingZhi = lingZhi;
		}

		private Data() {

		}

		private Data(ItemStack stack) {
			CompoundTag tag = stack.getTagElement("medicineBottle");
			if (tag != null) {
				this.deserializeNBT(tag);
			}
		}

		@Override
		public CompoundTag serializeNBT() {
			CompoundTag nbt = new CompoundTag();
			nbt.putString("lingZhi", lingZhi.getRegistryName().toString());
			nbt.putFloat("effective", effective);
			nbt.putInt("quality", this.quality);
			return nbt;
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			Block lingZhi = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("lingZhi")));
			if (lingZhi == Blocks.AIR) {
				lingZhi = HmxyBlocks.DENG_XIN_CAO.get();
			}
			this.setLingZhi((LingZhiBlock) lingZhi);
			this.setEffective(nbt.getFloat("lingZhi"));
			this.setQuality(nbt.getInt("quality"));
		}

		public LingZhiBlock getLingZhi() {
			return lingZhi;
		}

		public void setLingZhi(LingZhiBlock lingZhi) {
			this.lingZhi = lingZhi;
		}

		public float getEffective() {
			return effective;
		}

		public void setEffective(float effective) {
			this.effective = effective;
		}
		public int getQuality() {
			return quality;
		}

		public void setQuality(int quality) {
			this.quality = quality;
		}

		@Override
		public void save(ItemStack stack) {
			stack.addTagElement("medicineBottle", this.serializeNBT());
		}
	}

	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
			TooltipFlag pIsAdvanced) {
		super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
		Data data=new Data(pStack);
		pTooltipComponents.add(TransMsg.blockLocallizedName(data.getLingZhi()));
		pTooltipComponents.add(TransMsg.MEDICINE_BOTTLE_QUALITY.create(data.getQuality()));
		pTooltipComponents.add(TransMsg.MEDICINE_BOTTLE_EFFECTIVE.create(data.getEffective()));
	}
}
