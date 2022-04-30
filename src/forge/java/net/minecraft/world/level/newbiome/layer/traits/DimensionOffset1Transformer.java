package net.minecraft.world.level.newbiome.layer.traits;

/**
 * A transformer which offsets the parent center position by (-1, -1).
 */
public interface DimensionOffset1Transformer extends DimensionTransformer {
   default int getParentX(int pX) {
      return pX - 1;
   }

   default int getParentY(int pZ) {
      return pZ - 1;
   }
}