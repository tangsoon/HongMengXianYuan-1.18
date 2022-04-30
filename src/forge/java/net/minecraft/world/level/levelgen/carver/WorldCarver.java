package net.minecraft.world.level.levelgen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.BaseStoneSource;
import net.minecraft.world.level.levelgen.SingleBaseStoneSource;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.apache.commons.lang3.mutable.MutableBoolean;

public abstract class WorldCarver<C extends CarverConfiguration> extends net.minecraftforge.registries.ForgeRegistryEntry<WorldCarver<?>> {
   public static final WorldCarver<CaveCarverConfiguration> CAVE = register("cave", new CaveWorldCarver(CaveCarverConfiguration.CODEC));
   public static final WorldCarver<CaveCarverConfiguration> NETHER_CAVE = register("nether_cave", new NetherWorldCarver(CaveCarverConfiguration.CODEC));
   public static final WorldCarver<CanyonCarverConfiguration> CANYON = register("canyon", new CanyonWorldCarver(CanyonCarverConfiguration.CODEC));
   public static final WorldCarver<CanyonCarverConfiguration> UNDERWATER_CANYON = register("underwater_canyon", new UnderwaterCanyonWorldCarver(CanyonCarverConfiguration.CODEC));
   public static final WorldCarver<CaveCarverConfiguration> UNDERWATER_CAVE = register("underwater_cave", new UnderwaterCaveWorldCarver(CaveCarverConfiguration.CODEC));
   protected static final BaseStoneSource STONE_SOURCE = new SingleBaseStoneSource(Blocks.STONE.defaultBlockState());
   protected static final BlockState AIR = Blocks.AIR.defaultBlockState();
   protected static final BlockState CAVE_AIR = Blocks.CAVE_AIR.defaultBlockState();
   protected static final FluidState WATER = Fluids.WATER.defaultFluidState();
   protected static final FluidState LAVA = Fluids.LAVA.defaultFluidState();
   public Set<Block> replaceableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.PACKED_ICE, Blocks.DEEPSLATE, Blocks.TUFF, Blocks.GRANITE, Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.RAW_IRON_BLOCK, Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.RAW_COPPER_BLOCK);
   protected Set<Fluid> liquids = ImmutableSet.of(Fluids.WATER);
   private final Codec<ConfiguredWorldCarver<C>> configuredCodec;

   private static <C extends CarverConfiguration, F extends WorldCarver<C>> F register(String pKey, F pCarver) {
      return Registry.register(Registry.CARVER, pKey, pCarver);
   }

   public WorldCarver(Codec<C> pCodec) {
      this.configuredCodec = pCodec.fieldOf("config").xmap(this::configured, ConfiguredWorldCarver::config).codec();
   }

   public ConfiguredWorldCarver<C> configured(C p_65064_) {
      return new ConfiguredWorldCarver<>(this, p_65064_);
   }

   public Codec<ConfiguredWorldCarver<C>> configuredCodec() {
      return this.configuredCodec;
   }

   public int getRange() {
      return 4;
   }

   /**
    * Carves blocks in an ellipsoid (more accurately a spheroid), defined by a center (x, y, z) position, with a
    * horizontal and vertical radius (the semi-axies)
    * @param pSkipChecker Used to skip certain blocks within the carved region.
    */
   protected boolean carveEllipsoid(CarvingContext pContext, C pConfig, ChunkAccess pChunk, Function<BlockPos, Biome> pBiomeAccessor, long pSeed, Aquifer pAquifer, double pX, double pY, double pZ, double pHorizontalRadius, double pVerticalRadius, BitSet pCarvingMask, WorldCarver.CarveSkipChecker pSkipChecker) {
      ChunkPos chunkpos = pChunk.getPos();
      int i = chunkpos.x;
      int j = chunkpos.z;
      Random random = new Random(pSeed + (long)i + (long)j);
      double d0 = (double)chunkpos.getMiddleBlockX();
      double d1 = (double)chunkpos.getMiddleBlockZ();
      double d2 = 16.0D + pHorizontalRadius * 2.0D;
      if (!(Math.abs(pX - d0) > d2) && !(Math.abs(pZ - d1) > d2)) {
         int k = chunkpos.getMinBlockX();
         int l = chunkpos.getMinBlockZ();
         int i1 = Math.max(Mth.floor(pX - pHorizontalRadius) - k - 1, 0);
         int j1 = Math.min(Mth.floor(pX + pHorizontalRadius) - k, 15);
         int k1 = Math.max(Mth.floor(pY - pVerticalRadius) - 1, pContext.getMinGenY() + 1);
         int l1 = Math.min(Mth.floor(pY + pVerticalRadius) + 1, pContext.getMinGenY() + pContext.getGenDepth() - 8);
         int i2 = Math.max(Mth.floor(pZ - pHorizontalRadius) - l - 1, 0);
         int j2 = Math.min(Mth.floor(pZ + pHorizontalRadius) - l, 15);
         if (!pConfig.aquifersEnabled && this.hasDisallowedLiquid(pChunk, i1, j1, k1, l1, i2, j2)) {
            return false;
         } else {
            boolean flag = false;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();

            for(int k2 = i1; k2 <= j1; ++k2) {
               int l2 = chunkpos.getBlockX(k2);
               double d3 = ((double)l2 + 0.5D - pX) / pHorizontalRadius;

               for(int i3 = i2; i3 <= j2; ++i3) {
                  int j3 = chunkpos.getBlockZ(i3);
                  double d4 = ((double)j3 + 0.5D - pZ) / pHorizontalRadius;
                  if (!(d3 * d3 + d4 * d4 >= 1.0D)) {
                     MutableBoolean mutableboolean = new MutableBoolean(false);

                     for(int k3 = l1; k3 > k1; --k3) {
                        double d5 = ((double)k3 - 0.5D - pY) / pVerticalRadius;
                        if (!pSkipChecker.shouldSkip(pContext, d3, d5, d4, k3)) {
                           int l3 = k3 - pContext.getMinGenY();
                           int i4 = k2 | i3 << 4 | l3 << 8;
                           if (!pCarvingMask.get(i4) || isDebugEnabled(pConfig)) {
                              pCarvingMask.set(i4);
                              blockpos$mutableblockpos.set(l2, k3, j3);
                              flag |= this.carveBlock(pContext, pConfig, pChunk, pBiomeAccessor, pCarvingMask, random, blockpos$mutableblockpos, blockpos$mutableblockpos1, pAquifer, mutableboolean);
                           }
                        }
                     }
                  }
               }
            }

            return flag;
         }
      } else {
         return false;
      }
   }

   /**
    * Carves a single block, replacing it with the appropiate state if possible, and handles replacing exposed dirt with
    * grass.
    * @param pPos The position to carve at. The method does not mutate this position.
    * @param pCheckPos An additional mutable block position object to be used and modified by the method
    * @param pReachedSurface Set to true if the block carved was the surface, which is checked as being either grass or
    * mycelium
    */
   protected boolean carveBlock(CarvingContext pContext, C pConfig, ChunkAccess pChunk, Function<BlockPos, Biome> pBiomeAccessor, BitSet pCarvingMask, Random pRandom, BlockPos.MutableBlockPos pPos, BlockPos.MutableBlockPos pCheckPos, Aquifer pAquifer, MutableBoolean pReachedSurface) {
      BlockState blockstate = pChunk.getBlockState(pPos);
      BlockState blockstate1 = pChunk.getBlockState(pCheckPos.setWithOffset(pPos, Direction.UP));
      if (blockstate.is(Blocks.GRASS_BLOCK) || blockstate.is(Blocks.MYCELIUM)) {
         pReachedSurface.setTrue();
      }

      if (!this.canReplaceBlock(blockstate, blockstate1) && !isDebugEnabled(pConfig)) {
         return false;
      } else {
         BlockState blockstate2 = this.getCarveState(pContext, pConfig, pPos, pAquifer);
         if (blockstate2 == null) {
            return false;
         } else {
            pChunk.setBlockState(pPos, blockstate2, false);
            if (pReachedSurface.isTrue()) {
               pCheckPos.setWithOffset(pPos, Direction.DOWN);
               if (pChunk.getBlockState(pCheckPos).is(Blocks.DIRT)) {
                  pChunk.setBlockState(pCheckPos, pBiomeAccessor.apply(pPos).getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial(), false);
               }
            }

            return true;
         }
      }
   }

   @Nullable
   private BlockState getCarveState(CarvingContext pContext, C pConfig, BlockPos pPos, Aquifer pAquifer) {
      if (pPos.getY() <= pConfig.lavaLevel.resolveY(pContext)) {
         return LAVA.createLegacyBlock();
      } else if (!pConfig.aquifersEnabled) {
         return isDebugEnabled(pConfig) ? getDebugState(pConfig, AIR) : AIR;
      } else {
         BlockState blockstate = pAquifer.computeState(STONE_SOURCE, pPos.getX(), pPos.getY(), pPos.getZ(), 0.0D);
         if (blockstate == Blocks.STONE.defaultBlockState()) {
            return isDebugEnabled(pConfig) ? pConfig.debugSettings.getBarrierState() : null;
         } else {
            return isDebugEnabled(pConfig) ? getDebugState(pConfig, blockstate) : blockstate;
         }
      }
   }

   private static BlockState getDebugState(CarverConfiguration pConfig, BlockState pState) {
      if (pState.is(Blocks.AIR)) {
         return pConfig.debugSettings.getAirState();
      } else if (pState.is(Blocks.WATER)) {
         BlockState blockstate = pConfig.debugSettings.getWaterState();
         return blockstate.hasProperty(BlockStateProperties.WATERLOGGED) ? blockstate.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true)) : blockstate;
      } else {
         return pState.is(Blocks.LAVA) ? pConfig.debugSettings.getLavaState() : pState;
      }
   }

   /**
    * Carves the given chunk with caves that originate from the given {@code chunkPos}.
    * This method is invoked 289 times in order to generate each chunk (once for every position in an 8 chunk radius, or
    * 17x17 chunk area, centered around the target chunk).
    * 
    * @see net.minecraft.world.level.chunk.ChunkGenerator#applyCarvers
    * @param pChunk The chunk to be carved
    * @param pChunkPos The chunk position this carver is being called from
    */
   public abstract boolean carve(CarvingContext pContext, C pConfig, ChunkAccess pChunk, Function<BlockPos, Biome> pBiomeAccessor, Random pRandom, Aquifer pAquifer, ChunkPos pChunkPos, BitSet pCarvingMask);

   public abstract boolean isStartChunk(C pConfig, Random pRandom);

   protected boolean canReplaceBlock(BlockState pState) {
      return this.replaceableBlocks.contains(pState.getBlock());
   }

   protected boolean canReplaceBlock(BlockState pState, BlockState pAboveState) {
      return this.canReplaceBlock(pState) || (pState.is(Blocks.SAND) || pState.is(Blocks.GRAVEL)) && !pAboveState.getFluidState().is(FluidTags.WATER);
   }

   protected boolean hasDisallowedLiquid(ChunkAccess pChunk, int pMinX, int pMaxX, int pMinY, int pMaxY, int pMinZ, int pMaxZ) {
      ChunkPos chunkpos = pChunk.getPos();
      int i = chunkpos.getMinBlockX();
      int j = chunkpos.getMinBlockZ();
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int k = pMinX; k <= pMaxX; ++k) {
         for(int l = pMinZ; l <= pMaxZ; ++l) {
            for(int i1 = pMinY - 1; i1 <= pMaxY + 1; ++i1) {
               blockpos$mutableblockpos.set(i + k, i1, j + l);
               if (this.liquids.contains(pChunk.getFluidState(blockpos$mutableblockpos).getType())) {
                  return true;
               }

               if (i1 != pMaxY + 1 && !isEdge(k, l, pMinX, pMaxX, pMinZ, pMaxZ)) {
                  i1 = pMaxY;
               }
            }
         }
      }

      return false;
   }

   private static boolean isEdge(int pX, int pZ, int pMinX, int pMaxX, int pMinZ, int pMaxZ) {
      return pX == pMinX || pX == pMaxX || pZ == pMinZ || pZ == pMaxZ;
   }

   protected static boolean canReach(ChunkPos pChunkPos, double pX, double pZ, int pBranchIndex, int pBranchCount, float pWidth) {
      double d0 = (double)pChunkPos.getMiddleBlockX();
      double d1 = (double)pChunkPos.getMiddleBlockZ();
      double d2 = pX - d0;
      double d3 = pZ - d1;
      double d4 = (double)(pBranchCount - pBranchIndex);
      double d5 = (double)(pWidth + 2.0F + 16.0F);
      return d2 * d2 + d3 * d3 - d4 * d4 <= d5 * d5;
   }

   private static boolean isDebugEnabled(CarverConfiguration pConfig) {
      return pConfig.debugSettings.isDebugMode();
   }

   /**
    * Used to define certain positions to skip or ignore when carving.
    */
   public interface CarveSkipChecker {
      boolean shouldSkip(CarvingContext pContext, double pRelativeX, double pRelativeY, double pRelativeZ, int pY);
   }
}
