package net.minecraft.world.level.levelgen.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;

public abstract class ScatteredFeaturePiece extends StructurePiece {
   protected final int width;
   protected final int height;
   protected final int depth;
   protected int heightPosition = -1;

   protected ScatteredFeaturePiece(StructurePieceType pType, int pX, int pY, int pZ, int pWidth, int pHeight, int pDepth, Direction pOrientation) {
      super(pType, 0, StructurePiece.makeBoundingBox(pX, pY, pZ, pOrientation, pWidth, pHeight, pDepth));
      this.width = pWidth;
      this.height = pHeight;
      this.depth = pDepth;
      this.setOrientation(pOrientation);
   }

   protected ScatteredFeaturePiece(StructurePieceType p_72801_, CompoundTag p_72802_) {
      super(p_72801_, p_72802_);
      this.width = p_72802_.getInt("Width");
      this.height = p_72802_.getInt("Height");
      this.depth = p_72802_.getInt("Depth");
      this.heightPosition = p_72802_.getInt("HPos");
   }

   protected void addAdditionalSaveData(ServerLevel pLevel, CompoundTag pTag) {
      pTag.putInt("Width", this.width);
      pTag.putInt("Height", this.height);
      pTag.putInt("Depth", this.depth);
      pTag.putInt("HPos", this.heightPosition);
   }

   protected boolean updateAverageGroundHeight(LevelAccessor pLevel, BoundingBox pBounds, int pHeight) {
      if (this.heightPosition >= 0) {
         return true;
      } else {
         int i = 0;
         int j = 0;
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int k = this.boundingBox.minZ(); k <= this.boundingBox.maxZ(); ++k) {
            for(int l = this.boundingBox.minX(); l <= this.boundingBox.maxX(); ++l) {
               blockpos$mutableblockpos.set(l, 64, k);
               if (pBounds.isInside(blockpos$mutableblockpos)) {
                  i += pLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutableblockpos).getY();
                  ++j;
               }
            }
         }

         if (j == 0) {
            return false;
         } else {
            this.heightPosition = i / j;
            this.boundingBox.move(0, this.heightPosition - this.boundingBox.minY() + pHeight, 0);
            return true;
         }
      }
   }
}