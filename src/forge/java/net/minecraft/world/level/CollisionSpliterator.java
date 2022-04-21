package net.minecraft.world.level;

import java.util.Objects;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CollisionSpliterator extends AbstractSpliterator<VoxelShape> {
   @Nullable
   private final Entity source;
   private final AABB box;
   private final CollisionContext context;
   private final Cursor3D cursor;
   private final BlockPos.MutableBlockPos pos;
   private final VoxelShape entityShape;
   private final CollisionGetter collisionGetter;
   private boolean needsBorderCheck;
   private final BiPredicate<BlockState, BlockPos> predicate;

   public CollisionSpliterator(CollisionGetter pGetter, @Nullable Entity pEntity, AABB pCollisionBox) {
      this(pGetter, pEntity, pCollisionBox, (p_45810_, p_45811_) -> {
         return true;
      });
   }

   public CollisionSpliterator(CollisionGetter pCollisionGetter, @Nullable Entity pSource, AABB pBox, BiPredicate<BlockState, BlockPos> pPredicate) {
      super(Long.MAX_VALUE, 1280);
      this.context = pSource == null ? CollisionContext.empty() : CollisionContext.of(pSource);
      this.pos = new BlockPos.MutableBlockPos();
      this.entityShape = Shapes.create(pBox);
      this.collisionGetter = pCollisionGetter;
      this.needsBorderCheck = pSource != null;
      this.source = pSource;
      this.box = pBox;
      this.predicate = pPredicate;
      int i = Mth.floor(pBox.minX - 1.0E-7D) - 1;
      int j = Mth.floor(pBox.maxX + 1.0E-7D) + 1;
      int k = Mth.floor(pBox.minY - 1.0E-7D) - 1;
      int l = Mth.floor(pBox.maxY + 1.0E-7D) + 1;
      int i1 = Mth.floor(pBox.minZ - 1.0E-7D) - 1;
      int j1 = Mth.floor(pBox.maxZ + 1.0E-7D) + 1;
      this.cursor = new Cursor3D(i, k, i1, j, l, j1);
   }

   public boolean tryAdvance(Consumer<? super VoxelShape> pConsumer) {
      return this.needsBorderCheck && this.worldBorderCheck(pConsumer) || this.collisionCheck(pConsumer);
   }

   boolean collisionCheck(Consumer<? super VoxelShape> pConsumer) {
      while(true) {
         if (this.cursor.advance()) {
            int i = this.cursor.nextX();
            int j = this.cursor.nextY();
            int k = this.cursor.nextZ();
            int l = this.cursor.getNextType();
            if (l == 3) {
               continue;
            }

            BlockGetter blockgetter = this.getChunk(i, k);
            if (blockgetter == null) {
               continue;
            }

            this.pos.set(i, j, k);
            BlockState blockstate = blockgetter.getBlockState(this.pos);
            if (!this.predicate.test(blockstate, this.pos) || l == 1 && !blockstate.hasLargeCollisionShape() || l == 2 && !blockstate.is(Blocks.MOVING_PISTON)) {
               continue;
            }

            VoxelShape voxelshape = blockstate.getCollisionShape(this.collisionGetter, this.pos, this.context);
            if (voxelshape == Shapes.block()) {
               if (!this.box.intersects((double)i, (double)j, (double)k, (double)i + 1.0D, (double)j + 1.0D, (double)k + 1.0D)) {
                  continue;
               }

               pConsumer.accept(voxelshape.move((double)i, (double)j, (double)k));
               return true;
            }

            VoxelShape voxelshape1 = voxelshape.move((double)i, (double)j, (double)k);
            if (!Shapes.joinIsNotEmpty(voxelshape1, this.entityShape, BooleanOp.AND)) {
               continue;
            }

            pConsumer.accept(voxelshape1);
            return true;
         }

         return false;
      }
   }

   @Nullable
   private BlockGetter getChunk(int pX, int pZ) {
      int i = SectionPos.blockToSectionCoord(pX);
      int j = SectionPos.blockToSectionCoord(pZ);
      return this.collisionGetter.getChunkForCollisions(i, j);
   }

   boolean worldBorderCheck(Consumer<? super VoxelShape> pConsumer) {
      Objects.requireNonNull(this.source);
      this.needsBorderCheck = false;
      WorldBorder worldborder = this.collisionGetter.getWorldBorder();
      AABB aabb = this.source.getBoundingBox();
      if (!isBoxFullyWithinWorldBorder(worldborder, aabb)) {
         VoxelShape voxelshape = worldborder.getCollisionShape();
         if (!isOutsideBorder(voxelshape, aabb) && isCloseToBorder(voxelshape, aabb)) {
            pConsumer.accept(voxelshape);
            return true;
         }
      }

      return false;
   }

   private static boolean isCloseToBorder(VoxelShape pShape, AABB pCollisionBox) {
      return Shapes.joinIsNotEmpty(pShape, Shapes.create(pCollisionBox.inflate(1.0E-7D)), BooleanOp.AND);
   }

   private static boolean isOutsideBorder(VoxelShape pShape, AABB pCollisionBox) {
      return Shapes.joinIsNotEmpty(pShape, Shapes.create(pCollisionBox.deflate(1.0E-7D)), BooleanOp.AND);
   }

   public static boolean isBoxFullyWithinWorldBorder(WorldBorder pBorder, AABB pCollisionBox) {
      double d0 = (double)Mth.floor(pBorder.getMinX());
      double d1 = (double)Mth.floor(pBorder.getMinZ());
      double d2 = (double)Mth.ceil(pBorder.getMaxX());
      double d3 = (double)Mth.ceil(pBorder.getMaxZ());
      return pCollisionBox.minX > d0 && pCollisionBox.minX < d2 && pCollisionBox.minZ > d1 && pCollisionBox.minZ < d3 && pCollisionBox.maxX > d0 && pCollisionBox.maxX < d2 && pCollisionBox.maxZ > d1 && pCollisionBox.maxZ < d3;
   }
}