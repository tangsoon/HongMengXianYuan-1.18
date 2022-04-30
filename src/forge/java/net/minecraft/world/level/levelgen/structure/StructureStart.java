package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class StructureStart<C extends FeatureConfiguration> implements StructurePieceAccessor {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final String INVALID_START_ID = "INVALID";
   public static final StructureStart<?> INVALID_START = new StructureStart<MineshaftConfiguration>((StructureFeature)null, new ChunkPos(0, 0), 0, 0L) {
      public void generatePieces(RegistryAccess p_163644_, ChunkGenerator p_163645_, StructureManager p_163646_, ChunkPos p_163647_, Biome p_163648_, MineshaftConfiguration p_163649_, LevelHeightAccessor p_163650_) {
      }

      public boolean isValid() {
         return false;
      }
   };
   private final StructureFeature<C> feature;
   protected final List<StructurePiece> pieces = Lists.newArrayList();
   private final ChunkPos chunkPos;
   private int references;
   protected final WorldgenRandom random;
   @Nullable
   private BoundingBox cachedBoundingBox;

   public StructureStart(StructureFeature<C> pFeature, ChunkPos pChunkPos, int pReferences, long pSeed) {
      this.feature = pFeature;
      this.chunkPos = pChunkPos;
      this.references = pReferences;
      this.random = new WorldgenRandom();
      this.random.setLargeFeatureSeed(pSeed, pChunkPos.x, pChunkPos.z);
   }

   public abstract void generatePieces(RegistryAccess pRegistryAccess, ChunkGenerator pChunkGenerator, StructureManager pStructureManager, ChunkPos pChunkPos, Biome pBiome, C pConfig, LevelHeightAccessor pLevel);

   public final BoundingBox getBoundingBox() {
      if (this.cachedBoundingBox == null) {
         this.cachedBoundingBox = this.createBoundingBox();
      }

      return this.cachedBoundingBox;
   }

   protected BoundingBox createBoundingBox() {
      synchronized(this.pieces) {
         return BoundingBox.encapsulatingBoxes(this.pieces.stream().map(StructurePiece::getBoundingBox)::iterator).orElseThrow(() -> {
            return new IllegalStateException("Unable to calculate boundingbox without pieces");
         });
      }
   }

   public List<StructurePiece> getPieces() {
      return this.pieces;
   }

   public void placeInChunk(WorldGenLevel pLevel, StructureFeatureManager pStructureManager, ChunkGenerator pChunkGenerator, Random pRandom, BoundingBox pBox, ChunkPos pChunkPos) {
      synchronized(this.pieces) {
         if (!this.pieces.isEmpty()) {
            BoundingBox boundingbox = (this.pieces.get(0)).boundingBox;
            BlockPos blockpos = boundingbox.getCenter();
            BlockPos blockpos1 = new BlockPos(blockpos.getX(), boundingbox.minY(), blockpos.getZ());
            Iterator<StructurePiece> iterator = this.pieces.iterator();

            while(iterator.hasNext()) {
               StructurePiece structurepiece = iterator.next();
               if (structurepiece.getBoundingBox().intersects(pBox) && !structurepiece.postProcess(pLevel, pStructureManager, pChunkGenerator, pRandom, pBox, pChunkPos, blockpos1)) {
                  iterator.remove();
               }
            }

         }
      }
   }

   public CompoundTag createTag(ServerLevel pLevel, ChunkPos pChunkPos) {
      CompoundTag compoundtag = new CompoundTag();
      if (this.isValid()) {
         if (Registry.STRUCTURE_FEATURE.getKey(this.getFeature()) == null) { // FORGE: This is just a more friendly error instead of the 'Null String' below
            throw new RuntimeException("StructureStart \"" + this.getClass().getName() + "\": \"" + this.getFeature() + "\" missing ID Mapping, Modder see MapGenStructureIO");
         }
         compoundtag.putString("id", Registry.STRUCTURE_FEATURE.getKey(this.getFeature()).toString());
         compoundtag.putInt("ChunkX", pChunkPos.x);
         compoundtag.putInt("ChunkZ", pChunkPos.z);
         compoundtag.putInt("references", this.references);
         ListTag listtag = new ListTag();
         synchronized(this.pieces) {
            for(StructurePiece structurepiece : this.pieces) {
               listtag.add(structurepiece.createTag(pLevel));
            }
         }

         compoundtag.put("Children", listtag);
         return compoundtag;
      } else {
         compoundtag.putString("id", "INVALID");
         return compoundtag;
      }
   }

   protected void moveBelowSeaLevel(int p_163602_, int p_163603_, Random p_163604_, int p_163605_) {
      int i = p_163602_ - p_163605_;
      BoundingBox boundingbox = this.getBoundingBox();
      int j = boundingbox.getYSpan() + p_163603_ + 1;
      if (j < i) {
         j += p_163604_.nextInt(i - j);
      }

      int k = j - boundingbox.maxY();
      this.offsetPiecesVertically(k);
   }

   protected void moveInsideHeights(Random p_73598_, int p_73599_, int p_73600_) {
      BoundingBox boundingbox = this.getBoundingBox();
      int i = p_73600_ - p_73599_ + 1 - boundingbox.getYSpan();
      int j;
      if (i > 1) {
         j = p_73599_ + p_73598_.nextInt(i);
      } else {
         j = p_73599_;
      }

      int k = j - boundingbox.minY();
      this.offsetPiecesVertically(k);
   }

   protected void offsetPiecesVertically(int p_163600_) {
      for(StructurePiece structurepiece : this.pieces) {
         structurepiece.move(0, p_163600_, 0);
      }

      this.invalidateCache();
   }

   private void invalidateCache() {
      this.cachedBoundingBox = null;
   }

   public boolean isValid() {
      return !this.pieces.isEmpty();
   }

   public ChunkPos getChunkPos() {
      return this.chunkPos;
   }

   public BlockPos getLocatePos() {
      return new BlockPos(this.chunkPos.getMinBlockX(), 0, this.chunkPos.getMinBlockZ());
   }

   public boolean canBeReferenced() {
      return this.references < this.getMaxReferences();
   }

   public void addReference() {
      ++this.references;
   }

   public int getReferences() {
      return this.references;
   }

   protected int getMaxReferences() {
      return 1;
   }

   public StructureFeature<?> getFeature() {
      return this.feature;
   }

   public void addPiece(StructurePiece pPiece) {
      this.pieces.add(pPiece);
      this.invalidateCache();
   }

   @Nullable
   public StructurePiece findCollisionPiece(BoundingBox pBox) {
      return findCollisionPiece(this.pieces, pBox);
   }

   public void clearPieces() {
      this.pieces.clear();
      this.invalidateCache();
   }

   public boolean hasNoPieces() {
      return this.pieces.isEmpty();
   }

   @Nullable
   public static StructurePiece findCollisionPiece(List<StructurePiece> pPieces, BoundingBox pBox) {
      for(StructurePiece structurepiece : pPieces) {
         if (structurepiece.getBoundingBox().intersects(pBox)) {
            return structurepiece;
         }
      }

      return null;
   }

   protected boolean isInsidePiece(BlockPos pPos) {
      for(StructurePiece structurepiece : this.pieces) {
         if (structurepiece.getBoundingBox().isInside(pPos)) {
            return true;
         }
      }

      return false;
   }
}
