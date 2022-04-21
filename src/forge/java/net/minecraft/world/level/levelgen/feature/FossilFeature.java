package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.mutable.MutableInt;

public class FossilFeature extends Feature<FossilFeatureConfiguration> {
   public FossilFeature(Codec<FossilFeatureConfiguration> p_65851_) {
      super(p_65851_);
   }

   /**
    * Places the given feature at the given location.
    * During world generation, features are provided with a 3x3 region of chunks, centered on the chunk being generated,
    * that they can safely generate into.
    * @param pContext A context object with a reference to the level and the position the feature is being placed at
    */
   public boolean place(FeaturePlaceContext<FossilFeatureConfiguration> pContext) {
      Random random = pContext.random();
      WorldGenLevel worldgenlevel = pContext.level();
      BlockPos blockpos = pContext.origin();
      Rotation rotation = Rotation.getRandom(random);
      FossilFeatureConfiguration fossilfeatureconfiguration = pContext.config();
      int i = random.nextInt(fossilfeatureconfiguration.fossilStructures.size());
      StructureManager structuremanager = worldgenlevel.getLevel().getServer().getStructureManager();
      StructureTemplate structuretemplate = structuremanager.getOrCreate(fossilfeatureconfiguration.fossilStructures.get(i));
      StructureTemplate structuretemplate1 = structuremanager.getOrCreate(fossilfeatureconfiguration.overlayStructures.get(i));
      ChunkPos chunkpos = new ChunkPos(blockpos);
      BoundingBox boundingbox = new BoundingBox(chunkpos.getMinBlockX(), worldgenlevel.getMinBuildHeight(), chunkpos.getMinBlockZ(), chunkpos.getMaxBlockX(), worldgenlevel.getMaxBuildHeight(), chunkpos.getMaxBlockZ());
      StructurePlaceSettings structureplacesettings = (new StructurePlaceSettings()).setRotation(rotation).setBoundingBox(boundingbox).setRandom(random);
      Vec3i vec3i = structuretemplate.getSize(rotation);
      int j = random.nextInt(16 - vec3i.getX());
      int k = random.nextInt(16 - vec3i.getZ());
      int l = worldgenlevel.getMaxBuildHeight();

      for(int i1 = 0; i1 < vec3i.getX(); ++i1) {
         for(int j1 = 0; j1 < vec3i.getZ(); ++j1) {
            l = Math.min(l, worldgenlevel.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, blockpos.getX() + i1 + j, blockpos.getZ() + j1 + k));
         }
      }

      int k1 = Math.max(l - 15 - random.nextInt(10), worldgenlevel.getMinBuildHeight() + 10);
      BlockPos blockpos1 = structuretemplate.getZeroPositionWithTransform(blockpos.offset(j, 0, k).atY(k1), Mirror.NONE, rotation);
      if (countEmptyCorners(worldgenlevel, structuretemplate.getBoundingBox(structureplacesettings, blockpos1)) > fossilfeatureconfiguration.maxEmptyCornersAllowed) {
         return false;
      } else {
         structureplacesettings.clearProcessors();
         fossilfeatureconfiguration.fossilProcessors.get().list().forEach((p_159795_) -> {
            structureplacesettings.addProcessor(p_159795_);
         });
         structuretemplate.placeInWorld(worldgenlevel, blockpos1, blockpos1, structureplacesettings, random, 4);
         structureplacesettings.clearProcessors();
         fossilfeatureconfiguration.overlayProcessors.get().list().forEach((p_159792_) -> {
            structureplacesettings.addProcessor(p_159792_);
         });
         structuretemplate1.placeInWorld(worldgenlevel, blockpos1, blockpos1, structureplacesettings, random, 4);
         return true;
      }
   }

   private static int countEmptyCorners(WorldGenLevel pLevel, BoundingBox pBoundingBox) {
      MutableInt mutableint = new MutableInt(0);
      pBoundingBox.forAllCorners((p_159787_) -> {
         BlockState blockstate = pLevel.getBlockState(p_159787_);
         if (blockstate.isAir() || blockstate.is(Blocks.LAVA) || blockstate.is(Blocks.WATER)) {
            mutableint.add(1);
         }

      });
      return mutableint.getValue();
   }
}