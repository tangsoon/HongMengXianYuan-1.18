package by.ts.hmxy.block;

import javax.annotation.Nonnull;
import org.apache.logging.log4j.LogManager;
import by.ts.hmxy.block.blockentity.HmxyBEs;
import by.ts.hmxy.block.blockentity.TemperatureBE;
import by.ts.hmxy.item.ElixirRecipe;
import by.ts.hmxy.item.FurnaceRecover;
import by.ts.hmxy.item.MedicineBottleItem;
import by.ts.hmxy.menu.ElixirFurnaceMenu;
import by.ts.hmxy.util.HmxyHelper;
import by.ts.hmxy.util.TransMsg;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemStackHandler;

/**
 * 炼丹炉
 * 
 * @author tangsoon
 *
 */
//TODO 炼丹炉的合成规则: 先用8个方块作为炉壁，材料决定炼丹炉的耐久、炉壁强度；然后用炉壁和一到八个炼药锅合成炼丹炉，炼药锅数量决定炉壁丹仓数量；炼丹炉上色。
public class ElixirFurnaceBlock extends WaterloggedBlockBase implements EntityBlock, HasTemperature {

	public static final float MAX_HARDNESS=100f;
	
	public ElixirFurnaceBlock(Material m) {
		super(Properties.of(m, m.getColor()).strength(2.0F));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		ElixirFurnaceBE be = new ElixirFurnaceBE(pPos, pState);
		return be;
	}

	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState,
			BlockEntityType<T> pBlockEntityType) {
		return (level, pos, state, be) -> {

		};
	}

	@Override
	public void setTemperature(BlockEntity be, float temp) {
		if (be instanceof ElixirFurnaceBE rbe) {
			rbe.setTemperature(temp);
			rbe.setChanged();
		}
	}

	@Override
	public float getTemperature(BlockEntity be) {
		if (be instanceof ElixirFurnaceBE rbe) {
			return rbe.getTemperature();
		} else {
			LogManager.getLogger().error("You can not pass a BlockEntity to a block which dose not have it.");
			return 0F;
		}
	}

	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
			BlockHitResult pHit) {
		if (pLevel.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			BlockEntity blockentity = pLevel.getBlockEntity(pPos);
			HmxyHelper.openGui(pPlayer, (MenuProvider) blockentity, pPos);
			return InteractionResult.CONSUME;
		}
	}

	/**
	 * You should make sure your stack has data before call this method.
	 * 
	 * @param stack
	 * @return
	 */
	public Data getData(ItemStack stack) {
		return new Data(stack.getTagElement("data"));
	}

	public void setData(ItemStack stack, Data data) {
		stack.getOrCreateTag().put("data", data.serializeNBT());
	}

	public static class Data implements INBTSerializable<CompoundTag>, ContainerData {
		/** 同时可以炼化药材的最大数量 */
		private int elixirInvCount;
		/** 药瓶 */
		private ItemStackHandler bottles = new ItemStackHandler() {
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
				return stack.getItem() instanceof MedicineBottleItem;
			}
		};

		/** 已经提炼的灵植 */
		private ItemStackHandler lingZhiHandler=new ItemStackHandler() {
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
				return false;
			}
		};

		/** 炼丹炉的最大耐久 */
		private int maxDuration;
		/** 剩余耐久 */
		private int restDuration;

		/** 炼丹炉的炉壁强度，这个值越高，则在炼化灵植时减少炼丹炉耐久的概率越低 */
		private float wallStrength;

		/** 炉盖 */
		private ItemStackHandler furnaceCover;
		/** 丹方 */
		private ItemStackHandler elixirRecipe;
		/**丹药*/
		private ItemStackHandler elixir;

		@Override
		public int get(int pIndex) {
			int result = switch (pIndex) {
			case 0:
				yield this.elixirInvCount;
			case 1:
				yield this.maxDuration;
			case 2:
				yield this.restDuration;
			case 3:
				yield Float.floatToIntBits(this.wallStrength);
			default:
				throw new IllegalArgumentException("Unexpected value: " + pIndex);
			};
			return result;
		}

		@Override
		public void set(int pIndex, int pValue) {
			switch (pIndex) {
			case 0:
				this.elixirInvCount = pValue;
				break;
			case 1:
				this.maxDuration = pValue;
				break;
			case 2:
				this.restDuration = pValue;
				break;
			case 3:
				this.wallStrength = Float.intBitsToFloat(pValue);
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + pIndex);
			}
		}

		@Override
		public int getCount() {
			return 4;
		}

		public Data(int elixirInvCount, int maxDuration, float wallStrength) {
			this.elixirInvCount = elixirInvCount;
			this.maxDuration = maxDuration;
			this.wallStrength = wallStrength;
			this.bottles.setSize(this.elixirInvCount);
			this.lingZhiHandler.setSize(this.elixirInvCount);

			this.restDuration = maxDuration;

			this.furnaceCover = new ItemStackHandler(1) {
				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
					return stack.getItem() instanceof FurnaceRecover;
				}
			};

			this.elixirRecipe = new ItemStackHandler(1) {
				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
					return stack.getItem() instanceof ElixirRecipe;
				}
			};
			
			this.elixir=new ItemStackHandler(1) {
				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
					return false;
				}
			};
		}
		

		public Data(CompoundTag nbt) {
			this.deserializeNBT(nbt);
		}

		public Data() {

		}

		@Override
		public CompoundTag serializeNBT() {
			CompoundTag tag = new CompoundTag();
			tag.putInt("elixirInvCount", elixirInvCount);
			tag.put("bottles", bottles.serializeNBT());
			tag.put("lingZhiHandler", lingZhiHandler.serializeNBT());
			tag.putInt("maxDuration", maxDuration);
			tag.putInt("restDuration", restDuration);
			tag.putFloat("wallStrength", wallStrength);
			tag.put("furnaceCover", this.furnaceCover.serializeNBT());
			tag.put("elixirRecipe", this.elixirRecipe.serializeNBT());
			tag.put("elixir", this.elixir.serializeNBT());
			return tag;
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			this.elixirInvCount = nbt.getInt("elixirInvCount");
			this.bottles.deserializeNBT(nbt.getCompound("bottles"));
			this.lingZhiHandler.deserializeNBT(nbt.getCompound("lingZhiHandler"));
			this.maxDuration = nbt.getInt("maxDuration");
			this.restDuration = nbt.getInt("restDuration");
			this.wallStrength = nbt.getFloat("wallStrength");
			this.furnaceCover.deserializeNBT(nbt.getCompound("furnaceCover"));
			this.elixirRecipe.deserializeNBT(nbt.getCompound("elixirRecipe"));
			this.elixir.deserializeNBT(nbt.getCompound("elixir"));
		}

		public int getElixirInvCount() {
			return elixirInvCount;
		}

		public void setElixirInvCount(int elixirInvCount) {
			this.elixirInvCount = elixirInvCount;
		}

		public ItemStackHandler getBottles() {
			return bottles;
		}

		public void setBottles(ItemStackHandler bottles) {
			this.bottles = bottles;
		}

		public ItemStackHandler getLingZhiHandler() {
			return lingZhiHandler;
		}

		public void setLingZhiHandler(ItemStackHandler lingZhiHandler) {
			this.lingZhiHandler = lingZhiHandler;
		}

		public int getRestDuration() {
			return restDuration;
		}

		public void setRestDuration(int restDuration) {
			this.restDuration = restDuration;
		}

		public int getMaxDuration() {
			return maxDuration;
		}

		public void setMaxDuration(int maxDuration) {
			this.maxDuration = maxDuration;
		}

		public float getWallStrength() {
			return wallStrength;
		}

		public void setWallStrength(float wallStrength) {
			this.wallStrength = wallStrength;
		}

		public ItemStackHandler getFurnaceCover() {
			return furnaceCover;
		}

		public void setFurnaceCover(ItemStackHandler furnaceCover) {
			this.furnaceCover = furnaceCover;
		}

		public ItemStackHandler getElixirRecipe() {
			return elixirRecipe;
		}

		public void setElixirRecipe(ItemStackHandler elixirRecipe) {
			this.elixirRecipe = elixirRecipe;
		}

		public ItemStackHandler getElixir() {
			return elixir;
		}

		public void setElixir(ItemStackHandler elixir) {
			this.elixir = elixir;
		}
	}

	public static class ElixirFurnaceBE extends TemperatureBE implements MenuProvider {

		private Data data = new Data(1, 128, 10.0F);

		public ElixirFurnaceBE(BlockPos pWorldPosition, BlockState pBlockState) {
			super(HmxyBEs.ELIXIR_FURNACE.get(), pWorldPosition, pBlockState);
		}

		@Override
		protected void saveCustomData(CompoundTag pTag) {
			super.saveCustomData(pTag);
			pTag.put("data", data.serializeNBT());
		}

		@Override
		protected void loadCustomData(CompoundTag pTag) {
			super.loadCustomData(pTag);
			this.data.deserializeNBT(pTag.getCompound("data"));
		}

		public Data getData() {
			return data;
		}

		public void setData(Data data) {
			this.data = data;
		}

		@Override
		public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
			return new ElixirFurnaceMenu(pContainerId, pInventory, worldPosition);
		}

		@Override
		public Component getDisplayName() {
			return TransMsg.CONTAINER_ELIXIR_FURNACE.get();
		}
	}
}
