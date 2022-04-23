package by.ts.hmxy.world.item.level.block;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.BlockPos;
import java.util.Random;
import by.ts.hmxy.world.dimension.TheMortalTeleporter;


public class TheMortalPortalBlock extends LiquidBlock {
	public TheMortalPortalBlock() {
		super(() -> {
			return Fluids.LAVA;
		}, BlockBehaviour.Properties.of(Material.WATER).noCollission().randomTicks().strength(-1.0F)
				.sound(SoundType.GLASS).lightLevel(s -> 15).noDrops());
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
	}

	public static void portalSpawn(Level world, BlockPos pos) {
//		Optional<TheMortalPortalShape> optional = TheMortalPortalShape.findEmptyPortalShape(world, pos, Direction.Axis.X);
//		if (optional.isPresent()) {
//			optional.get().createPortalBlocks();
//		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {
		for (int i = 0; i < 4; i++) {
			double px = pos.getX() + random.nextFloat();
			double py = pos.getY() + random.nextFloat();
			double pz = pos.getZ() + random.nextFloat();
			double vx = (random.nextFloat() - 0.5) / 2.;
			double vy = (random.nextFloat() - 0.5) / 2.;
			double vz = (random.nextFloat() - 0.5) / 2.;
			int j = random.nextInt(4) - 1;
			if (world.getBlockState(pos.west()).getBlock() != this
					&& world.getBlockState(pos.east()).getBlock() != this) {
				px = pos.getX() + 0.5 + 0.25 * j;
				vx = random.nextFloat() * 2 * j;
			} else {
				pz = pos.getZ() + 0.5 + 0.25 * j;
				vz = random.nextFloat() * 2 * j;
			}
			world.addParticle(ParticleTypes.PORTAL, px, py, pz, vx, vy, vz);
		}
		if (random.nextInt(110) == 0)
			world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
					ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(("block.portal.ambient"))),
					SoundSource.BLOCKS, 0.5f, random.nextFloat() * 0.4f + 0.8f);
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		if (!entity.isPassenger() && !entity.isVehicle() && entity.canChangeDimensions() && !entity.level.isClientSide()
				&& true) {
			if (entity.isOnPortalCooldown()) {
				entity.setPortalCooldown();
			} else if (entity.level.dimension() != ResourceKey.create(Registry.DIMENSION_REGISTRY,
					new ResourceLocation("hmxy:the_mortal"))) {
				entity.setPortalCooldown();
				teleportToDimension(entity, pos,
						ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("hmxy:the_mortal")));
			} else {
				entity.setPortalCooldown();
				teleportToDimension(entity, pos, Level.OVERWORLD);
			}
		}
	}

	private void teleportToDimension(Entity entity, BlockPos pos, ResourceKey<Level> destinationType) {
		entity.changeDimension(entity.getServer().getLevel(destinationType),
				new TheMortalTeleporter(entity.getServer().getLevel(destinationType), pos));
	}
}
