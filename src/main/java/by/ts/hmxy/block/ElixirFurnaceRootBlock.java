package by.ts.hmxy.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import by.ts.hmxy.block.blockentity.BaseBlockEntity;
import by.ts.hmxy.block.blockentity.HmxyBEs;
import by.ts.hmxy.item.FireOringin;
import by.ts.hmxy.menu.ElixirFurnaceRootMenu;
import by.ts.hmxy.util.ContainLingQi;
import by.ts.hmxy.util.HmxyHelper;
import by.ts.hmxy.util.TransMsg;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemStackHandler;

/**
 * 炼丹炉基，用于放置燃料，不断消耗燃料为上面的炼丹炉提供热量
 * 
 * @author tangsoon
 *
 */
public class ElixirFurnaceRootBlock extends WaterloggedBlockBase implements EntityBlock {

	protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D);
	/** the default lingQi conservation of this */
	protected static final float DEFAULT_LING_QI_CONSERVATION = 0.1f;

	public ElixirFurnaceRootBlock(Material m) {
		super(Properties.of(m, m.getColor()).strength(2.0F));
	}

	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return SHAPE;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new ElixirFurnaceRootBE(pPos, pState);
	}

	/**
	 * This will be called once at sometime.
	 */
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState,
			BlockEntityType<T> pBlockEntityType) {
		return (level, pos, state, be) -> {
			if (level instanceof ServerLevel sLevel && sLevel.getGameTime() % 20 == 11
					&& be instanceof ElixirFurnaceRootBE rbe) {

				// 补充燃料
				float restCapacity = ElixirFurnaceRootBE.MAX_LING_QI_CAPACITY - rbe.lingQi;
				ItemStack fuelStack = rbe.getFuel();
				if (fuelStack.getItem() instanceof ContainLingQi item && item.getLingQi() <= restCapacity) {
					rbe.lingQi += item.getLingQi();
					fuelStack.shrink(1);
					rbe.setChanged();
				}

				// 消耗燃料
				float consume = rbe.valve * ElixirFurnaceRootBE.MAX_LING_QI_CONSUME;
				consume = Math.min(rbe.lingQi, consume);
				if (consume > 0) {
					rbe.lingQi -= consume;
					rbe.setChanged();
				}

				// 上面的方块温度增加
				BlockPos posUp=pos.above();
				BlockState upState = level.getBlockState(posUp);
				BlockEntity beUp=level.getBlockEntity(posUp);
				if (upState.getBlock() instanceof HasTemperature tempBlock&&beUp!=null) {
					
					float conversion = DEFAULT_LING_QI_CONSERVATION;
					if (rbe.getFire().getItem() instanceof FireOringin fire) {
						conversion = Math.max(1F, fire.conversionRate() + conversion);
					}
					float temperature = tempBlock.getTemperature(beUp);
					tempBlock.setTemperature(beUp, temperature + consume * Math.min(1, conversion));
				}
			}
		};
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

	public static class ElixirFurnaceRootBE extends BaseBlockEntity implements MenuProvider, ContainerData {
		/** 每秒钟最多消耗多少灵气 */
		public static final float MAX_LING_QI_CONSUME = 10;
		/** The max lingQi of this BlockEntity */
		public static final float MAX_LING_QI_CAPACITY = 20000;

		public static final int FIRE_SLOT_INDEX = 0;
		public static final int FUEL_SLOT_INDEX = 1;

		ItemStackHandler stacks;
		/** 存储的灵气 */
		float lingQi;
		/** 灵气阀门 */
		float valve;

		public ElixirFurnaceRootBE(BlockPos pWorldPosition, BlockState pBlockState) {
			super(HmxyBEs.ELIXIR_FURNACE_ROOT.get(), pWorldPosition, pBlockState);
			lingQi = 0F;
			valve = 0F;
			stacks = new ItemStackHandler(2) {
				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
					if (FIRE_SLOT_INDEX == slot && stack.getItem() instanceof FireOringin fire) {
						return true;
					} else if (FUEL_SLOT_INDEX == slot && stack.getItem() instanceof ContainLingQi item) {
						return true;
					}
					return false;
				}
			};
		}

		@Override
		public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
			return new ElixirFurnaceRootMenu(pContainerId, pInventory, worldPosition);
		}

		@Override
		public Component getDisplayName() {
			return TransMsg.CONTAINER_ELIXIR_FURNACE_ROOT.get();
		}

		@Override
		protected void saveCustomData(CompoundTag tag) {
			tag.put("stacks", stacks.serializeNBT());
			tag.putFloat("lingQi", this.lingQi);
			tag.putFloat("valve", valve);
		}

		@Override
		protected void loadCustomData(CompoundTag tag) {
			this.stacks.deserializeNBT(tag.getCompound("stacks"));
			this.lingQi = tag.getFloat("lingQi");
			this.valve = tag.getFloat("valve");
		}

		public ItemStack getFuel() {
			return stacks.getStackInSlot(FUEL_SLOT_INDEX);
		}

		public void setFuel(ItemStack fuel) {
			stacks.setStackInSlot(FUEL_SLOT_INDEX, fuel);
		}

		public ItemStack getFire() {
			return stacks.getStackInSlot(FIRE_SLOT_INDEX);
		}

		public void setFire(ItemStack fire) {
			stacks.setStackInSlot(FIRE_SLOT_INDEX, fire);
		}

		public float getLingQi() {
			return lingQi;
		}

		public void setLingQi(float lingQi) {
			this.lingQi = lingQi;
		}

		public float getValve() {
			return valve;
		}

		public void setValve(float valve) {
			this.valve = valve;
		}

		public float getMaxLingQiConsume() {
			return MAX_LING_QI_CONSUME;
		}

		public ItemStackHandler getStacks() {
			return stacks;
		}

		@Override
		public int get(int pIndex) {
			if (pIndex == 0) {
				return Float.floatToIntBits(this.valve);
			} else if (1 == pIndex) {
				return Float.floatToIntBits(this.lingQi);
			}
			return 0;
		}

		@Override
		public void set(int pIndex, int pValue) {
			if (0 == pIndex) {
				this.valve = Float.intBitsToFloat(pValue);
			} else if (1 == pIndex) {
				this.lingQi = Float.intBitsToFloat(pValue);
			}
		}

		@Override
		public int getCount() {
			return 2;
		}
	}
}
