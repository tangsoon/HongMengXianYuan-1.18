package net.minecraft.world.level.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction8;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpgradeData {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final UpgradeData EMPTY = new UpgradeData(EmptyBlockGetter.INSTANCE);
   private static final String TAG_INDICES = "Indices";
   private static final Direction8[] DIRECTIONS = Direction8.values();
   private final EnumSet<Direction8> sides = EnumSet.noneOf(Direction8.class);
   private final int[][] index;
   static final Map<Block, UpgradeData.BlockFixer> MAP = new IdentityHashMap<>();
   static final Set<UpgradeData.BlockFixer> CHUNKY_FIXERS = Sets.newHashSet();

   private UpgradeData(LevelHeightAccessor pLevel) {
      this.index = new int[pLevel.getSectionsCount()][];
   }

   public UpgradeData(CompoundTag pTag, LevelHeightAccessor pLevel) {
      this(pLevel);
      if (pTag.contains("Indices", 10)) {
         CompoundTag compoundtag = pTag.getCompound("Indices");

         for(int i = 0; i < this.index.length; ++i) {
            String s = String.valueOf(i);
            if (compoundtag.contains(s, 11)) {
               this.index[i] = compoundtag.getIntArray(s);
            }
         }
      }

      int j = pTag.getInt("Sides");

      for(Direction8 direction8 : Direction8.values()) {
         if ((j & 1 << direction8.ordinal()) != 0) {
            this.sides.add(direction8);
         }
      }

   }

   public void upgrade(LevelChunk pChunk) {
      this.upgradeInside(pChunk);

      for(Direction8 direction8 : DIRECTIONS) {
         upgradeSides(pChunk, direction8);
      }

      Level level = pChunk.getLevel();
      CHUNKY_FIXERS.forEach((p_63334_) -> {
         p_63334_.processChunk(level);
      });
   }

   private static void upgradeSides(LevelChunk pChunk, Direction8 pSide) {
      Level level = pChunk.getLevel();
      if (pChunk.getUpgradeData().sides.remove(pSide)) {
         Set<Direction> set = pSide.getDirections();
         int i = 0;
         int j = 15;
         boolean flag = set.contains(Direction.EAST);
         boolean flag1 = set.contains(Direction.WEST);
         boolean flag2 = set.contains(Direction.SOUTH);
         boolean flag3 = set.contains(Direction.NORTH);
         boolean flag4 = set.size() == 1;
         ChunkPos chunkpos = pChunk.getPos();
         int k = chunkpos.getMinBlockX() + (!flag4 || !flag3 && !flag2 ? (flag1 ? 0 : 15) : 1);
         int l = chunkpos.getMinBlockX() + (!flag4 || !flag3 && !flag2 ? (flag1 ? 0 : 15) : 14);
         int i1 = chunkpos.getMinBlockZ() + (!flag4 || !flag && !flag1 ? (flag3 ? 0 : 15) : 1);
         int j1 = chunkpos.getMinBlockZ() + (!flag4 || !flag && !flag1 ? (flag3 ? 0 : 15) : 14);
         Direction[] adirection = Direction.values();
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(BlockPos blockpos : BlockPos.betweenClosed(k, level.getMinBuildHeight(), i1, l, level.getMaxBuildHeight() - 1, j1)) {
            BlockState blockstate = level.getBlockState(blockpos);
            BlockState blockstate1 = blockstate;

            for(Direction direction : adirection) {
               blockpos$mutableblockpos.setWithOffset(blockpos, direction);
               blockstate1 = updateState(blockstate1, direction, level, blockpos, blockpos$mutableblockpos);
            }

            Block.updateOrDestroy(blockstate, blockstate1, level, blockpos, 18);
         }

      }
   }

   private static BlockState updateState(BlockState pState, Direction pDirection, LevelAccessor pLevel, BlockPos pPos, BlockPos pOffsetPos) {
      return MAP.getOrDefault(pState.getBlock(), UpgradeData.BlockFixers.DEFAULT).updateShape(pState, pDirection, pLevel.getBlockState(pOffsetPos), pLevel, pPos, pOffsetPos);
   }

   private void upgradeInside(LevelChunk pChunk) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();
      ChunkPos chunkpos = pChunk.getPos();
      LevelAccessor levelaccessor = pChunk.getLevel();

      for(int i = 0; i < this.index.length; ++i) {
         LevelChunkSection levelchunksection = pChunk.getSections()[i];
         int[] aint = this.index[i];
         this.index[i] = null;
         if (levelchunksection != null && aint != null && aint.length > 0) {
            Direction[] adirection = Direction.values();
            PalettedContainer<BlockState> palettedcontainer = levelchunksection.getStates();

            for(int j : aint) {
               int k = j & 15;
               int l = j >> 8 & 15;
               int i1 = j >> 4 & 15;
               blockpos$mutableblockpos.set(chunkpos.getMinBlockX() + k, levelchunksection.bottomBlockY() + l, chunkpos.getMinBlockZ() + i1);
               BlockState blockstate = palettedcontainer.get(j);
               BlockState blockstate1 = blockstate;

               for(Direction direction : adirection) {
                  blockpos$mutableblockpos1.setWithOffset(blockpos$mutableblockpos, direction);
                  if (SectionPos.blockToSectionCoord(blockpos$mutableblockpos.getX()) == chunkpos.x && SectionPos.blockToSectionCoord(blockpos$mutableblockpos.getZ()) == chunkpos.z) {
                     blockstate1 = updateState(blockstate1, direction, levelaccessor, blockpos$mutableblockpos, blockpos$mutableblockpos1);
                  }
               }

               Block.updateOrDestroy(blockstate, blockstate1, levelaccessor, blockpos$mutableblockpos, 18);
            }
         }
      }

      for(int j1 = 0; j1 < this.index.length; ++j1) {
         if (this.index[j1] != null) {
            LOGGER.warn("Discarding update data for section {} for chunk ({} {})", levelaccessor.getSectionYFromSectionIndex(j1), chunkpos.x, chunkpos.z);
         }

         this.index[j1] = null;
      }

   }

   public boolean isEmpty() {
      for(int[] aint : this.index) {
         if (aint != null) {
            return false;
         }
      }

      return this.sides.isEmpty();
   }

   public CompoundTag write() {
      CompoundTag compoundtag = new CompoundTag();
      CompoundTag compoundtag1 = new CompoundTag();

      for(int i = 0; i < this.index.length; ++i) {
         String s = String.valueOf(i);
         if (this.index[i] != null && this.index[i].length != 0) {
            compoundtag1.putIntArray(s, this.index[i]);
         }
      }

      if (!compoundtag1.isEmpty()) {
         compoundtag.put("Indices", compoundtag1);
      }

      int j = 0;

      for(Direction8 direction8 : this.sides) {
         j |= 1 << direction8.ordinal();
      }

      compoundtag.putByte("Sides", (byte)j);
      return compoundtag;
   }

   public interface BlockFixer {
      BlockState updateShape(BlockState pState, Direction pDirection, BlockState pOffsetState, LevelAccessor pLevel, BlockPos pPos, BlockPos pOffsetPos);

      default void processChunk(LevelAccessor pLevel) {
      }
   }

   static enum BlockFixers implements UpgradeData.BlockFixer {
      BLACKLIST(Blocks.OBSERVER, Blocks.NETHER_PORTAL, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.DRAGON_EGG, Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN) {
         public BlockState updateShape(BlockState p_63394_, Direction p_63395_, BlockState p_63396_, LevelAccessor p_63397_, BlockPos p_63398_, BlockPos p_63399_) {
            return p_63394_;
         }
      },
      DEFAULT {
         public BlockState updateShape(BlockState p_63405_, Direction p_63406_, BlockState p_63407_, LevelAccessor p_63408_, BlockPos p_63409_, BlockPos p_63410_) {
            return p_63405_.updateShape(p_63406_, p_63408_.getBlockState(p_63410_), p_63408_, p_63409_, p_63410_);
         }
      },
      CHEST(Blocks.CHEST, Blocks.TRAPPED_CHEST) {
         public BlockState updateShape(BlockState p_63416_, Direction p_63417_, BlockState p_63418_, LevelAccessor p_63419_, BlockPos p_63420_, BlockPos p_63421_) {
            if (p_63418_.is(p_63416_.getBlock()) && p_63417_.getAxis().isHorizontal() && p_63416_.getValue(ChestBlock.TYPE) == ChestType.SINGLE && p_63418_.getValue(ChestBlock.TYPE) == ChestType.SINGLE) {
               Direction direction = p_63416_.getValue(ChestBlock.FACING);
               if (p_63417_.getAxis() != direction.getAxis() && direction == p_63418_.getValue(ChestBlock.FACING)) {
                  ChestType chesttype = p_63417_ == direction.getClockWise() ? ChestType.LEFT : ChestType.RIGHT;
                  p_63419_.setBlock(p_63421_, p_63418_.setValue(ChestBlock.TYPE, chesttype.getOpposite()), 18);
                  if (direction == Direction.NORTH || direction == Direction.EAST) {
                     BlockEntity blockentity = p_63419_.getBlockEntity(p_63420_);
                     BlockEntity blockentity1 = p_63419_.getBlockEntity(p_63421_);
                     if (blockentity instanceof ChestBlockEntity && blockentity1 instanceof ChestBlockEntity) {
                        ChestBlockEntity.swapContents((ChestBlockEntity)blockentity, (ChestBlockEntity)blockentity1);
                     }
                  }

                  return p_63416_.setValue(ChestBlock.TYPE, chesttype);
               }
            }

            return p_63416_;
         }
      },
      LEAVES(true, Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES) {
         private final ThreadLocal<List<ObjectSet<BlockPos>>> queue = ThreadLocal.withInitial(() -> {
            return Lists.newArrayListWithCapacity(7);
         });

         public BlockState updateShape(BlockState p_63432_, Direction p_63433_, BlockState p_63434_, LevelAccessor p_63435_, BlockPos p_63436_, BlockPos p_63437_) {
            BlockState blockstate = p_63432_.updateShape(p_63433_, p_63435_.getBlockState(p_63437_), p_63435_, p_63436_, p_63437_);
            if (p_63432_ != blockstate) {
               int i = blockstate.getValue(BlockStateProperties.DISTANCE);
               List<ObjectSet<BlockPos>> list = this.queue.get();
               if (list.isEmpty()) {
                  for(int j = 0; j < 7; ++j) {
                     list.add(new ObjectOpenHashSet<>());
                  }
               }

               list.get(i).add(p_63436_.immutable());
            }

            return p_63432_;
         }

         public void processChunk(LevelAccessor p_63430_) {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            List<ObjectSet<BlockPos>> list = this.queue.get();

            for(int i = 2; i < list.size(); ++i) {
               int j = i - 1;
               ObjectSet<BlockPos> objectset = list.get(j);
               ObjectSet<BlockPos> objectset1 = list.get(i);

               for(BlockPos blockpos : objectset) {
                  BlockState blockstate = p_63430_.getBlockState(blockpos);
                  if (blockstate.getValue(BlockStateProperties.DISTANCE) >= j) {
                     p_63430_.setBlock(blockpos, blockstate.setValue(BlockStateProperties.DISTANCE, Integer.valueOf(j)), 18);
                     if (i != 7) {
                        for(Direction direction : DIRECTIONS) {
                           blockpos$mutableblockpos.setWithOffset(blockpos, direction);
                           BlockState blockstate1 = p_63430_.getBlockState(blockpos$mutableblockpos);
                           if (blockstate1.hasProperty(BlockStateProperties.DISTANCE) && blockstate.getValue(BlockStateProperties.DISTANCE) > i) {
                              objectset1.add(blockpos$mutableblockpos.immutable());
                           }
                        }
                     }
                  }
               }
            }

            list.clear();
         }
      },
      STEM_BLOCK(Blocks.MELON_STEM, Blocks.PUMPKIN_STEM) {
         public BlockState updateShape(BlockState p_63443_, Direction p_63444_, BlockState p_63445_, LevelAccessor p_63446_, BlockPos p_63447_, BlockPos p_63448_) {
            if (p_63443_.getValue(StemBlock.AGE) == 7) {
               StemGrownBlock stemgrownblock = ((StemBlock)p_63443_.getBlock()).getFruit();
               if (p_63445_.is(stemgrownblock)) {
                  return stemgrownblock.getAttachedStem().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, p_63444_);
               }
            }

            return p_63443_;
         }
      };

      public static final Direction[] DIRECTIONS = Direction.values();

      BlockFixers(Block... p_63380_) {
         this(false, p_63380_);
      }

      BlockFixers(boolean p_63369_, Block... p_63370_) {
         for(Block block : p_63370_) {
            UpgradeData.MAP.put(block, this);
         }

         if (p_63369_) {
            UpgradeData.CHUNKY_FIXERS.add(this);
         }

      }
   }
}