package net.minecraft.world.level.levelgen.surfacebuilders;

import net.minecraft.world.level.block.state.BlockState;

public interface SurfaceBuilderConfiguration {
   /**
    * The state to be placed as the top level of surface, above water. Typically grass or sand.
    */
   BlockState getTopMaterial();

   /**
    * The state to be placed underneath the surface, above water. Typically dirt under grass, or more sand under sand.
    */
   BlockState getUnderMaterial();

   /**
    * The state to be placed under water (below sea level). Typically gravel.
    */
   BlockState getUnderwaterMaterial();
}