package by.ts.hmxy.block;

import java.util.Random;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
/**
 * 灵石矿
 * @author tangsoon
 *
 */
public class ReikiStoneOreBlock extends OreBlock {
	public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;
	public final ReikiType type;

	public ReikiStoneOreBlock(BlockBehaviour.Properties p, ReikiType type) {
		super(p);
		this.type = type;
		this.registerDefaultState(this.defaultBlockState().setValue(LIT, Boolean.valueOf(false)));
	}

	public ReikiStoneOreBlock(BlockBehaviour.Properties p, UniformInt u, ReikiType type) {
		super(p, u);
		this.type = type;
	}

	public void attack(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
		this.interact(pState, pLevel, pPos);
	}

	public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
		this.interact(pState, pLevel, pPos);
		super.stepOn(pLevel, pPos, pState, pEntity);
	}

	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
			BlockHitResult pHit) {
		if (pLevel.isClientSide) {
			spawnParticles(pLevel, pPos);
		} else {
			interact(pState, pLevel, pPos);
		}

		ItemStack itemstack = pPlayer.getItemInHand(pHand);
		return itemstack.getItem() instanceof BlockItem
				&& (new BlockPlaceContext(pPlayer, pHand, itemstack, pHit)).canPlace() ? InteractionResult.PASS
						: InteractionResult.SUCCESS;
	}

	private void interact(BlockState pState, Level pLevel, BlockPos pPos) {
		spawnParticles(pLevel, pPos);
		if (!pState.getValue(LIT)) {
			pLevel.setBlock(pPos, pState.setValue(LIT, Boolean.valueOf(true)), 3);
		}
	}

	public boolean isRandomlyTicking(BlockState pState) {
		return pState.getValue(LIT);
	}

	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRandom) {
		if (pState.getValue(LIT)) {
			pLevel.setBlock(pPos, pState.setValue(LIT, Boolean.valueOf(false)), 3);
		}
	}

	@Override
	public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader world, BlockPos pos, int fortune,
			int silktouch) {
		int result = (silktouch == 0 ? 1 + RANDOM.nextInt(5) : 0);
		return type == Type.FLICKER.type ? result * 10 : result;
	}

	public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, Random pRand) {
		if (pState.getValue(LIT)) {
			spawnParticles(pLevel, pPos);
		}
	}

	private void spawnParticles(Level pLevel, BlockPos pPos) {
		Random random = pLevel.random;
		for (Direction direction : Direction.values()) {
			BlockPos blockpos = pPos.relative(direction);
			if (!pLevel.getBlockState(blockpos).isSolidRender(pLevel, blockpos)) {
				Direction.Axis direction$axis = direction.getAxis();
				double d1 = direction$axis == Direction.Axis.X ? 0.5D + 0.5625D * (double) direction.getStepX()
						: (double) random.nextFloat();
				double d2 = direction$axis == Direction.Axis.Y ? 0.5D + 0.5625D * (double) direction.getStepY()
						: (double) random.nextFloat();
				double d3 = direction$axis == Direction.Axis.Z ? 0.5D + 0.5625D * (double) direction.getStepZ()
						: (double) random.nextFloat();
				pLevel.addParticle(this.type.particle, (double) pPos.getX() + d1, (double) pPos.getY() + d2,
						(double) pPos.getZ() + d3, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(LIT);
	}

	public static class ReikiType {
		public final Vector3f color;
		public final DustParticleOptions particle;

		public ReikiType(int rgb) {
			this.color = new Vector3f(Vec3.fromRGB24(rgb));
			this.particle = new DustParticleOptions(color, 1.0F);
		}
	}

	public enum Type {
		ORDINARY(new ReikiType(0xa1eeff)), FLICKER(new ReikiType(0xb9d2f1));

		ReikiType type;

		private Type(ReikiType type) {
			this.type = type;
		}
	}
}
