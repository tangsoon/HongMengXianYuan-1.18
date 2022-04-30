package net.minecraft.world.level.chunk;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class LevelChunkSection {
   public static final int SECTION_WIDTH = 16;
   public static final int SECTION_HEIGHT = 16;
   public static final int SECTION_SIZE = 4096;
   private static final Palette<BlockState> GLOBAL_BLOCKSTATE_PALETTE = new GlobalPalette<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState());
   private final int bottomBlockY;
   private short nonEmptyBlockCount;
   private short tickingBlockCount;
   private short tickingFluidCount;
   private final PalettedContainer<BlockState> states;

   public LevelChunkSection(int pSectionY) {
      this(pSectionY, (short)0, (short)0, (short)0);
   }

   public LevelChunkSection(int pSectionY, short pNonEmptyBlockCount, short pTickingBlockCount, short pTickingFluidCount) {
      this.bottomBlockY = getBottomBlockY(pSectionY);
      this.nonEmptyBlockCount = pNonEmptyBlockCount;
      this.tickingBlockCount = pTickingBlockCount;
      this.tickingFluidCount = pTickingFluidCount;
      this.states = new PalettedContainer<>(GLOBAL_BLOCKSTATE_PALETTE, Block.BLOCK_STATE_REGISTRY, NbtUtils::readBlockState, NbtUtils::writeBlockState, Blocks.AIR.defaultBlockState());
   }

   public static int getBottomBlockY(int pSectionY) {
      return pSectionY << 4;
   }

   public BlockState getBlockState(int pX, int pY, int pZ) {
      return this.states.get(pX, pY, pZ);
   }

   public FluidState getFluidState(int pX, int pY, int pZ) {
      return this.states.get(pX, pY, pZ).getFluidState();
   }

   public void acquire() {
      this.states.acquire();
   }

   public void release() {
      this.states.release();
   }

   public BlockState setBlockState(int pX, int pY, int pZ, BlockState pState) {
      return this.setBlockState(pX, pY, pZ, pState, true);
   }

   public BlockState setBlockState(int pX, int pY, int pZ, BlockState pState, boolean pUseLocks) {
      BlockState blockstate;
      if (pUseLocks) {
         blockstate = this.states.getAndSet(pX, pY, pZ, pState);
      } else {
         blockstate = this.states.getAndSetUnchecked(pX, pY, pZ, pState);
      }

      FluidState fluidstate = blockstate.getFluidState();
      FluidState fluidstate1 = pState.getFluidState();
      if (!blockstate.isAir()) {
         --this.nonEmptyBlockCount;
         if (blockstate.isRandomlyTicking()) {
            --this.tickingBlockCount;
         }
      }

      if (!fluidstate.isEmpty()) {
         --this.tickingFluidCount;
      }

      if (!pState.isAir()) {
         ++this.nonEmptyBlockCount;
         if (pState.isRandomlyTicking()) {
            ++this.tickingBlockCount;
         }
      }

      if (!fluidstate1.isEmpty()) {
         ++this.tickingFluidCount;
      }

      return blockstate;
   }

   /**
    * @return {@code true} if this section is empty (consisting only of air-like blocks).
    */
   public boolean isEmpty() {
      return this.nonEmptyBlockCount == 0;
   }

   public static boolean isEmpty(@Nullable LevelChunkSection pSection) {
      return pSection == LevelChunk.EMPTY_SECTION || pSection.isEmpty();
   }

   public boolean isRandomlyTicking() {
      return this.isRandomlyTickingBlocks() || this.isRandomlyTickingFluids();
   }

   /**
    * @return {@code true} if this section has any blocks that require random ticks.
    */
   public boolean isRandomlyTickingBlocks() {
      return this.tickingBlockCount > 0;
   }

   /**
    * @return {@code true} if this section has any fluids that require random ticks.
    */
   public boolean isRandomlyTickingFluids() {
      return this.tickingFluidCount > 0;
   }

   /**
    * @return The lowest y coordinate in this section.
    */
   public int bottomBlockY() {
      return this.bottomBlockY;
   }

   public void recalcBlockCounts() {
      this.nonEmptyBlockCount = 0;
      this.tickingBlockCount = 0;
      this.tickingFluidCount = 0;
      this.states.count((p_62998_, p_62999_) -> {
         FluidState fluidstate = p_62998_.getFluidState();
         if (!p_62998_.isAir()) {
            this.nonEmptyBlockCount = (short)(this.nonEmptyBlockCount + p_62999_);
            if (p_62998_.isRandomlyTicking()) {
               this.tickingBlockCount = (short)(this.tickingBlockCount + p_62999_);
            }
         }

         if (!fluidstate.isEmpty()) {
            this.nonEmptyBlockCount = (short)(this.nonEmptyBlockCount + p_62999_);
            if (fluidstate.isRandomlyTicking()) {
               this.tickingFluidCount = (short)(this.tickingFluidCount + p_62999_);
            }
         }

      });
   }

   public PalettedContainer<BlockState> getStates() {
      return this.states;
   }

   public void read(FriendlyByteBuf pBuffer) {
      this.nonEmptyBlockCount = pBuffer.readShort();
      this.states.read(pBuffer);
   }

   public void write(FriendlyByteBuf pBuffer) {
      pBuffer.writeShort(this.nonEmptyBlockCount);
      this.states.write(pBuffer);
   }

   public int getSerializedSize() {
      return 2 + this.states.getSerializedSize();
   }

   /**
    * @return {@code true} if this section has any states matching the given predicate. As the internal representation
    * uses a {@link net.minecraft.world.level.chunk.Palette}, this is more efficient than looping through every position
    * in the section, or indeed the chunk.
    */
   public boolean maybeHas(Predicate<BlockState> pPredicate) {
      return this.states.maybeHas(pPredicate);
   }
}