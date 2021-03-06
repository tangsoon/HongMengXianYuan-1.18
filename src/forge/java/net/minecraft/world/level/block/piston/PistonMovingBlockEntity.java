package net.minecraft.world.level.block.piston;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PistonMovingBlockEntity extends BlockEntity {
   private static final int TICKS_TO_EXTEND = 2;
   private static final double PUSH_OFFSET = 0.01D;
   public static final double TICK_MOVEMENT = 0.51D;
   private BlockState movedState;
   private Direction direction;
   /** Whether this piston is extending */
   private boolean extending;
   private boolean isSourcePiston;
   private static final ThreadLocal<Direction> NOCLIP = ThreadLocal.withInitial(() -> {
      return null;
   });
   private float progress;
   /** The extension / retraction progress */
   private float progressO;
   private long lastTicked;
   private int deathTicks;

   public PistonMovingBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      super(BlockEntityType.PISTON, pWorldPosition, pBlockState);
   }

   public PistonMovingBlockEntity(BlockPos pWorldPosition, BlockState pBlockState, BlockState pMovedState, Direction pDirection, boolean pExtending, boolean pIsSourcePiston) {
      this(pWorldPosition, pBlockState);
      this.movedState = pMovedState;
      this.direction = pDirection;
      this.extending = pExtending;
      this.isSourcePiston = pIsSourcePiston;
   }

   /**
    * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
    * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
    */
   public CompoundTag getUpdateTag() {
      return this.save(new CompoundTag());
   }

   /**
    * @return whether this piston is extending
    */
   public boolean isExtending() {
      return this.extending;
   }

   public Direction getDirection() {
      return this.direction;
   }

   public boolean isSourcePiston() {
      return this.isSourcePiston;
   }

   /**
    * @return interpolated progress value (between lastProgress and progress) given the partialTicks
    */
   public float getProgress(float pPartialTicks) {
      if (pPartialTicks > 1.0F) {
         pPartialTicks = 1.0F;
      }

      return Mth.lerp(pPartialTicks, this.progressO, this.progress);
   }

   public float getXOff(float pPartialTicks) {
      return (float)this.direction.getStepX() * this.getExtendedProgress(this.getProgress(pPartialTicks));
   }

   public float getYOff(float pPartialTicks) {
      return (float)this.direction.getStepY() * this.getExtendedProgress(this.getProgress(pPartialTicks));
   }

   public float getZOff(float pPartialTicks) {
      return (float)this.direction.getStepZ() * this.getExtendedProgress(this.getProgress(pPartialTicks));
   }

   private float getExtendedProgress(float pProgress) {
      return this.extending ? pProgress - 1.0F : 1.0F - pProgress;
   }

   private BlockState getCollisionRelatedBlockState() {
      return !this.isExtending() && this.isSourcePiston() && this.movedState.getBlock() instanceof PistonBaseBlock ? Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.SHORT, Boolean.valueOf(this.progress > 0.25F)).setValue(PistonHeadBlock.TYPE, this.movedState.is(Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT).setValue(PistonHeadBlock.FACING, this.movedState.getValue(PistonBaseBlock.FACING)) : this.movedState;
   }

   private static void moveCollidedEntities(Level p_155911_, BlockPos p_155912_, float p_155913_, PistonMovingBlockEntity p_155914_) {
      Direction direction = p_155914_.getMovementDirection();
      double d0 = (double)(p_155913_ - p_155914_.progress);
      VoxelShape voxelshape = p_155914_.getCollisionRelatedBlockState().getCollisionShape(p_155911_, p_155912_);
      if (!voxelshape.isEmpty()) {
         AABB aabb = moveByPositionAndProgress(p_155912_, voxelshape.bounds(), p_155914_);
         List<Entity> list = p_155911_.getEntities((Entity)null, PistonMath.getMovementArea(aabb, direction, d0).minmax(aabb));
         if (!list.isEmpty()) {
            List<AABB> list1 = voxelshape.toAabbs();
            boolean flag = p_155914_.movedState.isSlimeBlock(); //TODO: is this patch really needed the logic of the original seems sound revisit later
            Iterator iterator = list.iterator();

            while(true) {
               Entity entity;
               while(true) {
                  if (!iterator.hasNext()) {
                     return;
                  }

                  entity = (Entity)iterator.next();
                  if (entity.getPistonPushReaction() != PushReaction.IGNORE) {
                     if (!flag) {
                        break;
                     }

                     if (!(entity instanceof ServerPlayer)) {
                        Vec3 vec3 = entity.getDeltaMovement();
                        double d1 = vec3.x;
                        double d2 = vec3.y;
                        double d3 = vec3.z;
                        switch(direction.getAxis()) {
                        case X:
                           d1 = (double)direction.getStepX();
                           break;
                        case Y:
                           d2 = (double)direction.getStepY();
                           break;
                        case Z:
                           d3 = (double)direction.getStepZ();
                        }

                        entity.setDeltaMovement(d1, d2, d3);
                        break;
                     }
                  }
               }

               double d4 = 0.0D;

               for(AABB aabb2 : list1) {
                  AABB aabb1 = PistonMath.getMovementArea(moveByPositionAndProgress(p_155912_, aabb2, p_155914_), direction, d0);
                  AABB aabb3 = entity.getBoundingBox();
                  if (aabb1.intersects(aabb3)) {
                     d4 = Math.max(d4, getMovement(aabb1, direction, aabb3));
                     if (d4 >= d0) {
                        break;
                     }
                  }
               }

               if (!(d4 <= 0.0D)) {
                  d4 = Math.min(d4, d0) + 0.01D;
                  moveEntityByPiston(direction, entity, d4, direction);
                  if (!p_155914_.extending && p_155914_.isSourcePiston) {
                     fixEntityWithinPistonBase(p_155912_, entity, direction, d0);
                  }
               }
            }
         }
      }
   }

   private static void moveEntityByPiston(Direction p_60372_, Entity p_60373_, double p_60374_, Direction p_60375_) {
      NOCLIP.set(p_60372_);
      p_60373_.move(MoverType.PISTON, new Vec3(p_60374_ * (double)p_60375_.getStepX(), p_60374_ * (double)p_60375_.getStepY(), p_60374_ * (double)p_60375_.getStepZ()));
      NOCLIP.set((Direction)null);
   }

   private static void moveStuckEntities(Level p_155932_, BlockPos p_155933_, float p_155934_, PistonMovingBlockEntity p_155935_) {
      if (p_155935_.isStickyForEntities()) {
         Direction direction = p_155935_.getMovementDirection();
         if (direction.getAxis().isHorizontal()) {
            double d0 = p_155935_.movedState.getCollisionShape(p_155932_, p_155933_).max(Direction.Axis.Y);
            AABB aabb = moveByPositionAndProgress(p_155933_, new AABB(0.0D, d0, 0.0D, 1.0D, 1.5000000999999998D, 1.0D), p_155935_);
            double d1 = (double)(p_155934_ - p_155935_.progress);

            for(Entity entity : p_155932_.getEntities((Entity)null, aabb, (p_60384_) -> {
               return matchesStickyCritera(aabb, p_60384_);
            })) {
               moveEntityByPiston(direction, entity, d1, direction);
            }

         }
      }
   }

   private static boolean matchesStickyCritera(AABB pShape, Entity pEntity) {
      return pEntity.getPistonPushReaction() == PushReaction.NORMAL && pEntity.isOnGround() && pEntity.getX() >= pShape.minX && pEntity.getX() <= pShape.maxX && pEntity.getZ() >= pShape.minZ && pEntity.getZ() <= pShape.maxZ;
   }

   private boolean isStickyForEntities() {
      return this.movedState.is(Blocks.HONEY_BLOCK);
   }

   public Direction getMovementDirection() {
      return this.extending ? this.direction : this.direction.getOpposite();
   }

   private static double getMovement(AABB pHeadShape, Direction pDirection, AABB pFacing) {
      switch(pDirection) {
      case EAST:
         return pHeadShape.maxX - pFacing.minX;
      case WEST:
         return pFacing.maxX - pHeadShape.minX;
      case UP:
      default:
         return pHeadShape.maxY - pFacing.minY;
      case DOWN:
         return pFacing.maxY - pHeadShape.minY;
      case SOUTH:
         return pHeadShape.maxZ - pFacing.minZ;
      case NORTH:
         return pFacing.maxZ - pHeadShape.minZ;
      }
   }

   private static AABB moveByPositionAndProgress(BlockPos p_155926_, AABB p_155927_, PistonMovingBlockEntity p_155928_) {
      double d0 = (double)p_155928_.getExtendedProgress(p_155928_.progress);
      return p_155927_.move((double)p_155926_.getX() + d0 * (double)p_155928_.direction.getStepX(), (double)p_155926_.getY() + d0 * (double)p_155928_.direction.getStepY(), (double)p_155926_.getZ() + d0 * (double)p_155928_.direction.getStepZ());
   }

   private static void fixEntityWithinPistonBase(BlockPos p_155921_, Entity p_155922_, Direction p_155923_, double p_155924_) {
      AABB aabb = p_155922_.getBoundingBox();
      AABB aabb1 = Shapes.block().bounds().move(p_155921_);
      if (aabb.intersects(aabb1)) {
         Direction direction = p_155923_.getOpposite();
         double d0 = getMovement(aabb1, direction, aabb) + 0.01D;
         double d1 = getMovement(aabb1, direction, aabb.intersect(aabb1)) + 0.01D;
         if (Math.abs(d0 - d1) < 0.01D) {
            d0 = Math.min(d0, p_155924_) + 0.01D;
            moveEntityByPiston(p_155923_, p_155922_, d0, direction);
         }
      }

   }

   public BlockState getMovedState() {
      return this.movedState;
   }

   /**
    * Removes the piston's BlockEntity and stops any movement
    */
   public void finalTick() {
      if (this.level != null && (this.progressO < 1.0F || this.level.isClientSide)) {
         this.progress = 1.0F;
         this.progressO = this.progress;
         this.level.removeBlockEntity(this.worldPosition);
         this.setRemoved();
         if (this.level.getBlockState(this.worldPosition).is(Blocks.MOVING_PISTON)) {
            BlockState blockstate;
            if (this.isSourcePiston) {
               blockstate = Blocks.AIR.defaultBlockState();
            } else {
               blockstate = Block.updateFromNeighbourShapes(this.movedState, this.level, this.worldPosition);
            }

            this.level.setBlock(this.worldPosition, blockstate, 3);
            this.level.neighborChanged(this.worldPosition, blockstate.getBlock(), this.worldPosition);
         }
      }

   }

   public static void tick(Level pLevel, BlockPos pPos, BlockState pState, PistonMovingBlockEntity pBlockEntity) {
      pBlockEntity.lastTicked = pLevel.getGameTime();
      pBlockEntity.progressO = pBlockEntity.progress;
      if (pBlockEntity.progressO >= 1.0F) {
         if (pLevel.isClientSide && pBlockEntity.deathTicks < 5) {
            ++pBlockEntity.deathTicks;
         } else {
            pLevel.removeBlockEntity(pPos);
            pBlockEntity.setRemoved();
            if (pBlockEntity.movedState != null && pLevel.getBlockState(pPos).is(Blocks.MOVING_PISTON)) {
               BlockState blockstate = Block.updateFromNeighbourShapes(pBlockEntity.movedState, pLevel, pPos);
               if (blockstate.isAir()) {
                  pLevel.setBlock(pPos, pBlockEntity.movedState, 84);
                  Block.updateOrDestroy(pBlockEntity.movedState, blockstate, pLevel, pPos, 3);
               } else {
                  if (blockstate.hasProperty(BlockStateProperties.WATERLOGGED) && blockstate.getValue(BlockStateProperties.WATERLOGGED)) {
                     blockstate = blockstate.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false));
                  }

                  pLevel.setBlock(pPos, blockstate, 67);
                  pLevel.neighborChanged(pPos, blockstate.getBlock(), pPos);
               }
            }

         }
      } else {
         float f = pBlockEntity.progress + 0.5F;
         moveCollidedEntities(pLevel, pPos, f, pBlockEntity);
         moveStuckEntities(pLevel, pPos, f, pBlockEntity);
         pBlockEntity.progress = f;
         if (pBlockEntity.progress >= 1.0F) {
            pBlockEntity.progress = 1.0F;
         }

      }
   }

   public void load(CompoundTag pTag) {
      super.load(pTag);
      this.movedState = NbtUtils.readBlockState(pTag.getCompound("blockState"));
      this.direction = Direction.from3DDataValue(pTag.getInt("facing"));
      this.progress = pTag.getFloat("progress");
      this.progressO = this.progress;
      this.extending = pTag.getBoolean("extending");
      this.isSourcePiston = pTag.getBoolean("source");
   }

   public CompoundTag save(CompoundTag pCompound) {
      super.save(pCompound);
      pCompound.put("blockState", NbtUtils.writeBlockState(this.movedState));
      pCompound.putInt("facing", this.direction.get3DDataValue());
      pCompound.putFloat("progress", this.progressO);
      pCompound.putBoolean("extending", this.extending);
      pCompound.putBoolean("source", this.isSourcePiston);
      return pCompound;
   }

   public VoxelShape getCollisionShape(BlockGetter pLevel, BlockPos pPos) {
      VoxelShape voxelshape;
      if (!this.extending && this.isSourcePiston) {
         voxelshape = this.movedState.setValue(PistonBaseBlock.EXTENDED, Boolean.valueOf(true)).getCollisionShape(pLevel, pPos);
      } else {
         voxelshape = Shapes.empty();
      }

      Direction direction = NOCLIP.get();
      if ((double)this.progress < 1.0D && direction == this.getMovementDirection()) {
         return voxelshape;
      } else {
         BlockState blockstate;
         if (this.isSourcePiston()) {
            blockstate = Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.FACING, this.direction).setValue(PistonHeadBlock.SHORT, Boolean.valueOf(this.extending != 1.0F - this.progress < 0.25F));
         } else {
            blockstate = this.movedState;
         }

         float f = this.getExtendedProgress(this.progress);
         double d0 = (double)((float)this.direction.getStepX() * f);
         double d1 = (double)((float)this.direction.getStepY() * f);
         double d2 = (double)((float)this.direction.getStepZ() * f);
         return Shapes.or(voxelshape, blockstate.getCollisionShape(pLevel, pPos).move(d0, d1, d2));
      }
   }

   public long getLastTicked() {
      return this.lastTicked;
   }
}
