package net.minecraft.world.level;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface CommonLevelAccessor extends EntityGetter, LevelReader, LevelSimulatedRW {
   default <T extends BlockEntity> Optional<T> getBlockEntity(BlockPos pPos, BlockEntityType<T> pBlockEntityType) {
      return LevelReader.super.getBlockEntity(pPos, pBlockEntityType);
   }

   default Stream<VoxelShape> getEntityCollisions(@Nullable Entity pEntity, AABB pArea, Predicate<Entity> pFilter) {
      return EntityGetter.super.getEntityCollisions(pEntity, pArea, pFilter);
   }

   default boolean isUnobstructed(@Nullable Entity pEntity, VoxelShape pShape) {
      return EntityGetter.super.isUnobstructed(pEntity, pShape);
   }

   default BlockPos getHeightmapPos(Heightmap.Types pHeightmapType, BlockPos pPos) {
      return LevelReader.super.getHeightmapPos(pHeightmapType, pPos);
   }

   RegistryAccess registryAccess();

   default Optional<ResourceKey<Biome>> getBiomeName(BlockPos pPos) {
      return this.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getResourceKey(this.getBiome(pPos));
   }
}