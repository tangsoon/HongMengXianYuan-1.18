package by.ts.hmxy.block;

import javax.annotation.Nullable;
import by.ts.hmxy.block.blockentity.BaseBlockEntity;
import by.ts.hmxy.block.blockentity.HmxyBEs;
import by.ts.hmxy.menu.ElixirFurnaceRootMenu;
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

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState,
			BlockEntityType<T> pBlockEntityType) {
		return (level,pos,state,be)->{
			
		};
	}

	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
			BlockHitResult pHit) {
		if (pLevel.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			this.openContainer(pLevel, pPos, pPlayer);
			return InteractionResult.CONSUME;
		}
	}

	protected void openContainer(Level pLevel, BlockPos pPos, Player pPlayer) {
		BlockEntity blockentity = pLevel.getBlockEntity(pPos);
		HmxyHelper.openGui(pPlayer,(MenuProvider) blockentity,pPos);
	}

	public static class ElixirFurnaceRootBE extends BaseBlockEntity implements MenuProvider,ContainerData {
		/**每秒钟最多消耗多少灵气*/
		public static final float MAX_LING_QI_CONSUME=10;
		ItemStackHandler stacks;
		/**存储的灵气*/
		float lingQi;
		/**灵气阀门*/
		float valve;
		
		public ElixirFurnaceRootBE(BlockPos pWorldPosition, BlockState pBlockState) {
			super(HmxyBEs.ELIXIR_FURNACE_ROOT.get(), pWorldPosition, pBlockState);
			lingQi=0F;
			valve=0F;
			stacks=new ItemStackHandler(2);
		}

		@Override
		public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
			return new ElixirFurnaceRootMenu(pContainerId, pInventory, worldPosition);
		}

		@Override
		public Component getDisplayName() {
			return TransMsg.CONTAINER_ELIXIR_FURNACE_ROOT.create();
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
			this.lingQi=tag.getFloat("lingQi");
			this.valve=tag.getFloat("valve");
		}

		public ItemStack getFuel() {
			return stacks.getStackInSlot(1);
		}

		public void setFuel(ItemStack fuel) {
			stacks.setStackInSlot(1, fuel);
		}

		public ItemStack getFire() {
			return stacks.getStackInSlot(0);
		}

		public void setFire(ItemStack fire) {
			stacks.setStackInSlot(0, fire);
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
			if(pIndex==0) {
				return Float.floatToIntBits(this.valve);
			}
			else if(1==pIndex) {
				return Float.floatToIntBits(this.lingQi);
			}
			return 0;
		}

		@Override
		public void set(int pIndex, int pValue) {
			if(0==pIndex) {
				this.valve=Float.intBitsToFloat(pValue);
			}
			else if(1==pIndex) {
				this.lingQi=Float.intBitsToFloat(pValue);
			}
		}

		@Override
		public int getCount() {
			return 1;
		}
	}
}
