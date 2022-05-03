package by.ts.hmxy.fluid;

import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import by.ts.hmxy.block.HmxyBlocks;
import by.ts.hmxy.item.HmxyItems;

public abstract class PreviousLifeWater extends ForgeFlowingFluid {
	public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(
			() -> HmxyFluids.PREVIOUS_LIFE_WATER.get(), () -> HmxyFluids.PREVIOUS_LIFE_WATER_FLOWING.get(),
			FluidAttributes.builder(new ResourceLocation("hmxy:block/previous_life_water"),
					new ResourceLocation("hmxy:block/previous_life_water_flowing")))
							.explosionResistance(100f).bucket(() -> HmxyItems.PREVIOUS_LIFE_WATER_BUCKET.get())
							.block(() -> (LiquidBlock) HmxyBlocks.PREVIOUS_LIFE_WATER.get());
	
	private PreviousLifeWater() {
		super(PROPERTIES);
	}
	@Override
	public ParticleOptions getDripParticle() {
		return ParticleTypes.FALLING_WATER;
	}
	public static class Source extends PreviousLifeWater {
		public Source() {
			super();
		}
		public int getAmount(FluidState state) {
			return 8;
		}
		public boolean isSource(FluidState state) {
			return true;
		}
	}

	public static class Flowing extends PreviousLifeWater {
		public Flowing() {
			super();
		}
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}
		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		public boolean isSource(FluidState state) {
			return false;
		}
	}
}
