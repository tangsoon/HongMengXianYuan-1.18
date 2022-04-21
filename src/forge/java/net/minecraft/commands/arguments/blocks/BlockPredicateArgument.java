package net.minecraft.commands.arguments.blocks;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagContainer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockPredicateArgument implements ArgumentType<BlockPredicateArgument.Result> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}");
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((p_115580_) -> {
      return new TranslatableComponent("arguments.block.tag.unknown", p_115580_);
   });

   public static BlockPredicateArgument blockPredicate() {
      return new BlockPredicateArgument();
   }

   public BlockPredicateArgument.Result parse(StringReader pReader) throws CommandSyntaxException {
      BlockStateParser blockstateparser = (new BlockStateParser(pReader, true)).parse(true);
      if (blockstateparser.getState() != null) {
         BlockPredicateArgument.BlockPredicate blockpredicateargument$blockpredicate = new BlockPredicateArgument.BlockPredicate(blockstateparser.getState(), blockstateparser.getProperties().keySet(), blockstateparser.getNbt());
         return (p_115578_) -> {
            return blockpredicateargument$blockpredicate;
         };
      } else {
         ResourceLocation resourcelocation = blockstateparser.getTag();
         return (p_173736_) -> {
            Tag<Block> tag = p_173736_.getTagOrThrow(Registry.BLOCK_REGISTRY, resourcelocation, (p_173732_) -> {
               return ERROR_UNKNOWN_TAG.create(p_173732_.toString());
            });
            return new BlockPredicateArgument.TagPredicate(tag, blockstateparser.getVagueProperties(), blockstateparser.getNbt());
         };
      }
   }

   public static Predicate<BlockInWorld> getBlockPredicate(CommandContext<CommandSourceStack> pContext, String pName) throws CommandSyntaxException {
      return pContext.getArgument(pName, BlockPredicateArgument.Result.class).create(pContext.getSource().getServer().getTags());
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> pContext, SuggestionsBuilder pBuilder) {
      StringReader stringreader = new StringReader(pBuilder.getInput());
      stringreader.setCursor(pBuilder.getStart());
      BlockStateParser blockstateparser = new BlockStateParser(stringreader, true);

      try {
         blockstateparser.parse(true);
      } catch (CommandSyntaxException commandsyntaxexception) {
      }

      return blockstateparser.fillSuggestions(pBuilder, BlockTags.getAllTags());
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static class BlockPredicate implements Predicate<BlockInWorld> {
      private final BlockState state;
      private final Set<Property<?>> properties;
      @Nullable
      private final CompoundTag nbt;

      public BlockPredicate(BlockState pState, Set<Property<?>> pProperties, @Nullable CompoundTag pNbt) {
         this.state = pState;
         this.properties = pProperties;
         this.nbt = pNbt;
      }

      public boolean test(BlockInWorld pBlock) {
         BlockState blockstate = pBlock.getState();
         if (!blockstate.is(this.state.getBlock())) {
            return false;
         } else {
            for(Property<?> property : this.properties) {
               if (blockstate.getValue(property) != this.state.getValue(property)) {
                  return false;
               }
            }

            if (this.nbt == null) {
               return true;
            } else {
               BlockEntity blockentity = pBlock.getEntity();
               return blockentity != null && NbtUtils.compareNbt(this.nbt, blockentity.save(new CompoundTag()), true);
            }
         }
      }
   }

   public interface Result {
      Predicate<BlockInWorld> create(TagContainer pTagContainer) throws CommandSyntaxException;
   }

   static class TagPredicate implements Predicate<BlockInWorld> {
      private final Tag<Block> tag;
      @Nullable
      private final CompoundTag nbt;
      private final Map<String, String> vagueProperties;

      TagPredicate(Tag<Block> pTag, Map<String, String> pVagueProperties, @Nullable CompoundTag pNbt) {
         this.tag = pTag;
         this.vagueProperties = pVagueProperties;
         this.nbt = pNbt;
      }

      public boolean test(BlockInWorld pBlock) {
         BlockState blockstate = pBlock.getState();
         if (!blockstate.is(this.tag)) {
            return false;
         } else {
            for(Entry<String, String> entry : this.vagueProperties.entrySet()) {
               Property<?> property = blockstate.getBlock().getStateDefinition().getProperty(entry.getKey());
               if (property == null) {
                  return false;
               }

               Comparable<?> comparable = (Comparable)property.getValue(entry.getValue()).orElse(null);
               if (comparable == null) {
                  return false;
               }

               if (blockstate.getValue(property) != comparable) {
                  return false;
               }
            }

            if (this.nbt == null) {
               return true;
            } else {
               BlockEntity blockentity = pBlock.getEntity();
               return blockentity != null && NbtUtils.compareNbt(this.nbt, blockentity.save(new CompoundTag()), true);
            }
         }
      }
   }
}