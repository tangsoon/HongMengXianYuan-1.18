package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;

public class InteractWithDoor extends Behavior<LivingEntity> {
   private static final int COOLDOWN_BEFORE_RERUNNING_IN_SAME_NODE = 20;
   private static final double SKIP_CLOSING_DOOR_IF_FURTHER_AWAY_THAN = 2.0D;
   private static final double MAX_DISTANCE_TO_HOLD_DOOR_OPEN_FOR_OTHER_MOBS = 2.0D;
   @Nullable
   private Node lastCheckedNode;
   private int remainingCooldown;

   public InteractWithDoor() {
      super(ImmutableMap.of(MemoryModuleType.PATH, MemoryStatus.VALUE_PRESENT, MemoryModuleType.DOORS_TO_CLOSE, MemoryStatus.REGISTERED));
   }

   protected boolean checkExtraStartConditions(ServerLevel pLevel, LivingEntity pOwner) {
      Path path = pOwner.getBrain().getMemory(MemoryModuleType.PATH).get();
      if (!path.notStarted() && !path.isDone()) {
         if (!Objects.equals(this.lastCheckedNode, path.getNextNode())) {
            this.remainingCooldown = 20;
            return true;
         } else {
            if (this.remainingCooldown > 0) {
               --this.remainingCooldown;
            }

            return this.remainingCooldown == 0;
         }
      } else {
         return false;
      }
   }

   protected void start(ServerLevel pLevel, LivingEntity pEntity, long pGameTime) {
      Path path = pEntity.getBrain().getMemory(MemoryModuleType.PATH).get();
      this.lastCheckedNode = path.getNextNode();
      Node node = path.getPreviousNode();
      Node node1 = path.getNextNode();
      BlockPos blockpos = node.asBlockPos();
      BlockState blockstate = pLevel.getBlockState(blockpos);
      if (blockstate.is(BlockTags.WOODEN_DOORS)) {
         DoorBlock doorblock = (DoorBlock)blockstate.getBlock();
         if (!doorblock.isOpen(blockstate)) {
            doorblock.setOpen(pEntity, pLevel, blockstate, blockpos, true);
         }

         this.rememberDoorToClose(pLevel, pEntity, blockpos);
      }

      BlockPos blockpos1 = node1.asBlockPos();
      BlockState blockstate1 = pLevel.getBlockState(blockpos1);
      if (blockstate1.is(BlockTags.WOODEN_DOORS)) {
         DoorBlock doorblock1 = (DoorBlock)blockstate1.getBlock();
         if (!doorblock1.isOpen(blockstate1)) {
            doorblock1.setOpen(pEntity, pLevel, blockstate1, blockpos1, true);
            this.rememberDoorToClose(pLevel, pEntity, blockpos1);
         }
      }

      closeDoorsThatIHaveOpenedOrPassedThrough(pLevel, pEntity, node, node1);
   }

   public static void closeDoorsThatIHaveOpenedOrPassedThrough(ServerLevel p_23299_, LivingEntity p_23300_, @Nullable Node p_23301_, @Nullable Node p_23302_) {
      Brain<?> brain = p_23300_.getBrain();
      if (brain.hasMemoryValue(MemoryModuleType.DOORS_TO_CLOSE)) {
         Iterator<GlobalPos> iterator = brain.getMemory(MemoryModuleType.DOORS_TO_CLOSE).get().iterator();

         while(iterator.hasNext()) {
            GlobalPos globalpos = iterator.next();
            BlockPos blockpos = globalpos.pos();
            if ((p_23301_ == null || !p_23301_.asBlockPos().equals(blockpos)) && (p_23302_ == null || !p_23302_.asBlockPos().equals(blockpos))) {
               if (isDoorTooFarAway(p_23299_, p_23300_, globalpos)) {
                  iterator.remove();
               } else {
                  BlockState blockstate = p_23299_.getBlockState(blockpos);
                  if (!blockstate.is(BlockTags.WOODEN_DOORS)) {
                     iterator.remove();
                  } else {
                     DoorBlock doorblock = (DoorBlock)blockstate.getBlock();
                     if (!doorblock.isOpen(blockstate)) {
                        iterator.remove();
                     } else if (areOtherMobsComingThroughDoor(p_23299_, p_23300_, blockpos)) {
                        iterator.remove();
                     } else {
                        doorblock.setOpen(p_23300_, p_23299_, blockstate, blockpos, false);
                        iterator.remove();
                     }
                  }
               }
            }
         }
      }

   }

   private static boolean areOtherMobsComingThroughDoor(ServerLevel p_23304_, LivingEntity p_23305_, BlockPos p_23306_) {
      Brain<?> brain = p_23305_.getBrain();
      return !brain.hasMemoryValue(MemoryModuleType.NEAREST_LIVING_ENTITIES) ? false : brain.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).get().stream().filter((p_23317_) -> {
         return p_23317_.getType() == p_23305_.getType();
      }).filter((p_23320_) -> {
         return p_23306_.closerThan(p_23320_.position(), 2.0D);
      }).anyMatch((p_23314_) -> {
         return isMobComingThroughDoor(p_23304_, p_23314_, p_23306_);
      });
   }

   private static boolean isMobComingThroughDoor(ServerLevel p_23322_, LivingEntity p_23323_, BlockPos p_23324_) {
      if (!p_23323_.getBrain().hasMemoryValue(MemoryModuleType.PATH)) {
         return false;
      } else {
         Path path = p_23323_.getBrain().getMemory(MemoryModuleType.PATH).get();
         if (path.isDone()) {
            return false;
         } else {
            Node node = path.getPreviousNode();
            if (node == null) {
               return false;
            } else {
               Node node1 = path.getNextNode();
               return p_23324_.equals(node.asBlockPos()) || p_23324_.equals(node1.asBlockPos());
            }
         }
      }
   }

   private static boolean isDoorTooFarAway(ServerLevel p_23308_, LivingEntity p_23309_, GlobalPos p_23310_) {
      return p_23310_.dimension() != p_23308_.dimension() || !p_23310_.pos().closerThan(p_23309_.position(), 2.0D);
   }

   private void rememberDoorToClose(ServerLevel p_23326_, LivingEntity p_23327_, BlockPos p_23328_) {
      Brain<?> brain = p_23327_.getBrain();
      GlobalPos globalpos = GlobalPos.of(p_23326_.dimension(), p_23328_);
      if (brain.getMemory(MemoryModuleType.DOORS_TO_CLOSE).isPresent()) {
         brain.getMemory(MemoryModuleType.DOORS_TO_CLOSE).get().add(globalpos);
      } else {
         brain.setMemory(MemoryModuleType.DOORS_TO_CLOSE, Sets.newHashSet(globalpos));
      }

   }
}