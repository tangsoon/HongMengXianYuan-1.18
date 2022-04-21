package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagCollection;
import net.minecraft.world.item.Item;

public class ItemParser {
   public static final SimpleCommandExceptionType ERROR_NO_TAGS_ALLOWED = new SimpleCommandExceptionType(new TranslatableComponent("argument.item.tag.disallowed"));
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_ITEM = new DynamicCommandExceptionType((p_121013_) -> {
      return new TranslatableComponent("argument.item.id.invalid", p_121013_);
   });
   private static final char SYNTAX_START_NBT = '{';
   private static final char SYNTAX_TAG = '#';
   private static final BiFunction<SuggestionsBuilder, TagCollection<Item>, CompletableFuture<Suggestions>> SUGGEST_NOTHING = (p_121028_, p_121029_) -> {
      return p_121028_.buildFuture();
   };
   private final StringReader reader;
   private final boolean forTesting;
   private Item item;
   @Nullable
   private CompoundTag nbt;
   private ResourceLocation tag = new ResourceLocation("");
   private int tagCursor;
   /** Builder to be used when creating a list of suggestions */
   private BiFunction<SuggestionsBuilder, TagCollection<Item>, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;

   public ItemParser(StringReader pReader, boolean pForTesting) {
      this.reader = pReader;
      this.forTesting = pForTesting;
   }

   public Item getItem() {
      return this.item;
   }

   @Nullable
   public CompoundTag getNbt() {
      return this.nbt;
   }

   public ResourceLocation getTag() {
      return this.tag;
   }

   public void readItem() throws CommandSyntaxException {
      int i = this.reader.getCursor();
      ResourceLocation resourcelocation = ResourceLocation.read(this.reader);
      this.item = Registry.ITEM.getOptional(resourcelocation).orElseThrow(() -> {
         this.reader.setCursor(i);
         return ERROR_UNKNOWN_ITEM.createWithContext(this.reader, resourcelocation.toString());
      });
   }

   public void readTag() throws CommandSyntaxException {
      if (!this.forTesting) {
         throw ERROR_NO_TAGS_ALLOWED.create();
      } else {
         this.suggestions = this::suggestTag;
         this.reader.expect('#');
         this.tagCursor = this.reader.getCursor();
         this.tag = ResourceLocation.read(this.reader);
      }
   }

   public void readNbt() throws CommandSyntaxException {
      this.nbt = (new TagParser(this.reader)).readStruct();
   }

   public ItemParser parse() throws CommandSyntaxException {
      this.suggestions = this::suggestItemIdOrTag;
      if (this.reader.canRead() && this.reader.peek() == '#') {
         this.readTag();
      } else {
         this.readItem();
         this.suggestions = this::suggestOpenNbt;
      }

      if (this.reader.canRead() && this.reader.peek() == '{') {
         this.suggestions = SUGGEST_NOTHING;
         this.readNbt();
      }

      return this;
   }

   /**
    * Builds a list of suggestions based on item registry names.
    */
   private CompletableFuture<Suggestions> suggestOpenNbt(SuggestionsBuilder p_121016_, TagCollection<Item> p_121017_) {
      if (p_121016_.getRemaining().isEmpty()) {
         p_121016_.suggest(String.valueOf('{'));
      }

      return p_121016_.buildFuture();
   }

   /**
    * Builds a list of suggestions based on item tags.
    */
   private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder p_121020_, TagCollection<Item> p_121021_) {
      return SharedSuggestionProvider.suggestResource(p_121021_.getAvailableTags(), p_121020_.createOffset(this.tagCursor));
   }

   /**
    * Builds a list of suggestions based on item tags (if the parser is set to allows tags) and item registry names.
    */
   private CompletableFuture<Suggestions> suggestItemIdOrTag(SuggestionsBuilder p_121024_, TagCollection<Item> p_121025_) {
      if (this.forTesting) {
         SharedSuggestionProvider.suggestResource(p_121025_.getAvailableTags(), p_121024_, String.valueOf('#'));
      }

      return SharedSuggestionProvider.suggestResource(Registry.ITEM.keySet(), p_121024_);
   }

   /**
    * Create a list of suggestions for the specified builder.
    */
   public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder pBuilder, TagCollection<Item> pTags) {
      return this.suggestions.apply(pBuilder.createOffset(this.reader.getCursor()), pTags);
   }
}