package net.minecraft.world.level.newbiome.layer.traits;

/**
 * A no-op offset transformer.
 */
public interface DimensionOffset0Transformer extends DimensionTransformer {
   default int getParentX(int pX) {
      return pX;
   }

   default int getParentY(int pZ) {
      return pZ;
   }
}