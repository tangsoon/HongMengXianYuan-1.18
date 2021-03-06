package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.MineShaftPieces;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class MineshaftFeature extends StructureFeature<MineshaftConfiguration> {
   public MineshaftFeature(Codec<MineshaftConfiguration> p_66273_) {
      super(p_66273_);
   }

   protected boolean isFeatureChunk(ChunkGenerator pGenerator, BiomeSource pBiomeSource, long pSeed, WorldgenRandom pRandom, ChunkPos pChunkPos, Biome pBiome, ChunkPos pPotentialPos, MineshaftConfiguration pConfig, LevelHeightAccessor pLevel) {
      pRandom.setLargeFeatureSeed(pSeed, pChunkPos.x, pChunkPos.z);
      double d0 = (double)pConfig.probability;
      return pRandom.nextDouble() < d0;
   }

   public StructureFeature.StructureStartFactory<MineshaftConfiguration> getStartFactory() {
      return MineshaftFeature.MineShaftStart::new;
   }

   public static class MineShaftStart extends StructureStart<MineshaftConfiguration> {
      public MineShaftStart(StructureFeature<MineshaftConfiguration> p_160031_, ChunkPos p_160032_, int p_160033_, long p_160034_) {
         super(p_160031_, p_160032_, p_160033_, p_160034_);
      }

      public void generatePieces(RegistryAccess p_160044_, ChunkGenerator p_160045_, StructureManager p_160046_, ChunkPos p_160047_, Biome p_160048_, MineshaftConfiguration p_160049_, LevelHeightAccessor p_160050_) {
         MineShaftPieces.MineShaftRoom mineshaftpieces$mineshaftroom = new MineShaftPieces.MineShaftRoom(0, this.random, p_160047_.getBlockX(2), p_160047_.getBlockZ(2), p_160049_.type);
         this.addPiece(mineshaftpieces$mineshaftroom);
         mineshaftpieces$mineshaftroom.addChildren(mineshaftpieces$mineshaftroom, this, this.random);
         if (p_160049_.type == MineshaftFeature.Type.MESA) {
            int i = -5;
            BoundingBox boundingbox = this.getBoundingBox();
            int j = p_160045_.getSeaLevel() - boundingbox.maxY() + boundingbox.getYSpan() / 2 - -5;
            this.offsetPiecesVertically(j);
         } else {
            this.moveBelowSeaLevel(p_160045_.getSeaLevel(), p_160045_.getMinY(), this.random, 10);
         }

      }
   }

   public static enum Type implements StringRepresentable {
      NORMAL("normal", Blocks.OAK_LOG, Blocks.OAK_PLANKS, Blocks.OAK_FENCE),
      MESA("mesa", Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_FENCE);

      public static final Codec<MineshaftFeature.Type> CODEC = StringRepresentable.fromEnum(MineshaftFeature.Type::values, MineshaftFeature.Type::byName);
      private static final Map<String, MineshaftFeature.Type> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(MineshaftFeature.Type::getName, (p_66333_) -> {
         return p_66333_;
      }));
      private final String name;
      private final BlockState woodState;
      private final BlockState planksState;
      private final BlockState fenceState;

      private Type(String p_160057_, Block p_160058_, Block p_160059_, Block p_160060_) {
         this.name = p_160057_;
         this.woodState = p_160058_.defaultBlockState();
         this.planksState = p_160059_.defaultBlockState();
         this.fenceState = p_160060_.defaultBlockState();
      }

      public String getName() {
         return this.name;
      }

      private static MineshaftFeature.Type byName(String p_66335_) {
         return BY_NAME.get(p_66335_);
      }

      public static MineshaftFeature.Type byId(int pId) {
         return pId >= 0 && pId < values().length ? values()[pId] : NORMAL;
      }

      public BlockState getWoodState() {
         return this.woodState;
      }

      public BlockState getPlanksState() {
         return this.planksState;
      }

      public BlockState getFenceState() {
         return this.fenceState;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}