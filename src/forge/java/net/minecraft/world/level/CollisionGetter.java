package net.minecraft.world.level;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface CollisionGetter extends BlockGetter {
   WorldBorder getWorldBorder();

   @Nullable
   BlockGetter getChunkForCollisions(int pChunkX, int pChunkZ);

   default boolean isUnobstructed(@Nullable Entity pEntity, VoxelShape pShape) {
      return true;
   }

   default boolean isUnobstructed(BlockState pState, BlockPos pPos, CollisionContext pContext) {
      VoxelShape voxelshape = pState.getCollisionShape(this, pPos, pContext);
      return voxelshape.isEmpty() || this.isUnobstructed((Entity)null, voxelshape.move((double)pPos.getX(), (double)pPos.getY(), (double)pPos.getZ()));
   }

   default boolean isUnobstructed(Entity pEntity) {
      return this.isUnobstructed(pEntity, Shapes.create(pEntity.getBoundingBox()));
   }

   default boolean noCollision(AABB pCollisionBox) {
      return this.noCollision((Entity)null, pCollisionBox, (p_45780_) -> {
         return true;
      });
   }

   default boolean noCollision(Entity pEntity) {
      return this.noCollision(pEntity, pEntity.getBoundingBox(), (p_45760_) -> {
         return true;
      });
   }

   default boolean noCollision(Entity pEntity, AABB pCollisionBox) {
      return this.noCollision(pEntity, pCollisionBox, (p_45745_) -> {
         return true;
      });
   }

   default boolean noCollision(@Nullable Entity pEntity, AABB pCollisionBox, Predicate<Entity> pEntityPredicate) {
      return this.getCollisions(pEntity, pCollisionBox, pEntityPredicate).allMatch(VoxelShape::isEmpty);
   }

   Stream<VoxelShape> getEntityCollisions(@Nullable Entity pEntity, AABB pCollisionBox, Predicate<Entity> pFilter);

   default Stream<VoxelShape> getCollisions(@Nullable Entity pEntity, AABB pCollisionBox, Predicate<Entity> pFilter) {
      return Stream.concat(this.getBlockCollisions(pEntity, pCollisionBox), this.getEntityCollisions(pEntity, pCollisionBox, pFilter));
   }

   default Stream<VoxelShape> getBlockCollisions(@Nullable Entity pEntity, AABB pCollisionBox) {
      return StreamSupport.stream(new CollisionSpliterator(this, pEntity, pCollisionBox), false);
   }

   default boolean hasBlockCollision(@Nullable Entity pEntity, AABB pCollisionBox, BiPredicate<BlockState, BlockPos> pFilter) {
      return !this.getBlockCollisions(pEntity, pCollisionBox, pFilter).allMatch(VoxelShape::isEmpty);
   }

   default Stream<VoxelShape> getBlockCollisions(@Nullable Entity pEntity, AABB pCollisionBox, BiPredicate<BlockState, BlockPos> pFilter) {
      return StreamSupport.stream(new CollisionSpliterator(this, pEntity, pCollisionBox, pFilter), false);
   }

   default Optional<Vec3> findFreePosition(@Nullable Entity pEntity, VoxelShape pShape, Vec3 pPos, double pX, double pY, double pZ) {
      if (pShape.isEmpty()) {
         return Optional.empty();
      } else {
         AABB aabb = pShape.bounds().inflate(pX, pY, pZ);
         VoxelShape voxelshape = this.getBlockCollisions(pEntity, aabb).flatMap((p_151426_) -> {
            return p_151426_.toAabbs().stream();
         }).map((p_151413_) -> {
            return p_151413_.inflate(pX / 2.0D, pY / 2.0D, pZ / 2.0D);
         }).map(Shapes::create).reduce(Shapes.empty(), Shapes::or);
         VoxelShape voxelshape1 = Shapes.join(pShape, voxelshape, BooleanOp.ONLY_FIRST);
         return voxelshape1.closestPointTo(pPos);
      }
   }
}