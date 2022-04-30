package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class Ingredient implements Predicate<ItemStack> {
   //Because Mojang caches things... we need to invalidate them.. so... here we go..
   private static final java.util.Set<Ingredient> INSTANCES = java.util.Collections.newSetFromMap(new java.util.WeakHashMap<Ingredient, Boolean>());
   public static void invalidateAll() {
      INSTANCES.stream().filter(e -> e != null).forEach(i -> i.invalidate());
   }

   public static final Ingredient EMPTY = new Ingredient(Stream.empty());
   private final Ingredient.Value[] values;
   private ItemStack[] itemStacks;
   private IntList stackingIds;
   private final boolean isSimple;

   protected Ingredient(Stream<? extends Ingredient.Value> pValues) {
      this.values = pValues.toArray((p_43933_) -> {
         return new Ingredient.Value[p_43933_];
      });
      this.isSimple = !net.minecraftforge.fmllegacy.DatagenModLoader.isRunningDataGen() && !Arrays.stream(values).anyMatch(list -> list.getItems().stream().anyMatch(stack -> stack.getItem().isDamageable(stack)));
      Ingredient.INSTANCES.add(this);
   }

   public ItemStack[] getItems() {
      this.dissolve();
      return this.itemStacks;
   }

   private void dissolve() {
      if (this.itemStacks == null) {
         this.itemStacks = Arrays.stream(this.values).flatMap((p_43916_) -> {
            return p_43916_.getItems().stream();
         }).distinct().toArray((p_43910_) -> {
            return new ItemStack[p_43910_];
         });
      }

   }

   public boolean test(@Nullable ItemStack p_43914_) {
      if (p_43914_ == null) {
         return false;
      } else {
         this.dissolve();
         if (this.itemStacks.length == 0) {
            return p_43914_.isEmpty();
         } else {
            for(ItemStack itemstack : this.itemStacks) {
               if (itemstack.is(p_43914_.getItem())) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public IntList getStackingIds() {
      if (this.stackingIds == null) {
         this.dissolve();
         this.stackingIds = new IntArrayList(this.itemStacks.length);

         for(ItemStack itemstack : this.itemStacks) {
            this.stackingIds.add(StackedContents.getStackingIndex(itemstack));
         }

         this.stackingIds.sort(IntComparators.NATURAL_COMPARATOR);
      }

      return this.stackingIds;
   }

   public final void toNetwork(FriendlyByteBuf pBuffer) {
      this.dissolve();
      if (!this.isVanilla()) {
         net.minecraftforge.common.crafting.CraftingHelper.write(pBuffer, this);
         return;
      }
      pBuffer.writeCollection(Arrays.asList(this.itemStacks), FriendlyByteBuf::writeItem);
   }

   public JsonElement toJson() {
      if (this.values.length == 1) {
         return this.values[0].serialize();
      } else {
         JsonArray jsonarray = new JsonArray();

         for(Ingredient.Value ingredient$value : this.values) {
            jsonarray.add(ingredient$value.serialize());
         }

         return jsonarray;
      }
   }

   public boolean isEmpty() {
      return this.values.length == 0 && (this.itemStacks == null || this.itemStacks.length == 0) && (this.stackingIds == null || this.stackingIds.isEmpty());
   }

   protected void invalidate() {
      this.itemStacks = null;
      this.stackingIds = null;
   }

   public boolean isSimple() {
      return isSimple || this == EMPTY;
   }

   private final boolean isVanilla = this.getClass() == Ingredient.class;
   public final boolean isVanilla() {
       return isVanilla;
   }

   public net.minecraftforge.common.crafting.IIngredientSerializer<? extends Ingredient> getSerializer() {
      if (!isVanilla()) throw new IllegalStateException("Modders must implement Ingredient.getSerializer in their custom Ingredients: " + this);
      return net.minecraftforge.common.crafting.VanillaIngredientSerializer.INSTANCE;
   }

   public static Ingredient fromValues(Stream<? extends Ingredient.Value> pStream) {
      Ingredient ingredient = new Ingredient(pStream);
      return ingredient.values.length == 0 ? EMPTY : ingredient;
   }

   public static Ingredient of() {
      return EMPTY;
   }

   public static Ingredient of(ItemLike... pItems) {
      return of(Arrays.stream(pItems).map(ItemStack::new));
   }

   public static Ingredient of(ItemStack... pStacks) {
      return of(Arrays.stream(pStacks));
   }

   public static Ingredient of(Stream<ItemStack> pStacks) {
      return fromValues(pStacks.filter((p_43944_) -> {
         return !p_43944_.isEmpty();
      }).map(Ingredient.ItemValue::new));
   }

   public static Ingredient of(Tag<Item> pTag) {
      return fromValues(Stream.of(new Ingredient.TagValue(pTag)));
   }

   public static Ingredient fromNetwork(FriendlyByteBuf pBuffer) {
      var size = pBuffer.readVarInt();
      if (size == -1) return net.minecraftforge.common.crafting.CraftingHelper.getIngredient(pBuffer.readResourceLocation(), pBuffer);
      return fromValues(Stream.generate(() -> new Ingredient.ItemValue(pBuffer.readItem())).limit(size));
   }

   public static Ingredient fromJson(@Nullable JsonElement pJson) {
      if (pJson != null && !pJson.isJsonNull()) {
         Ingredient ret = net.minecraftforge.common.crafting.CraftingHelper.getIngredient(pJson);
         if (ret != null) return ret;
         if (pJson.isJsonObject()) {
            return fromValues(Stream.of(valueFromJson(pJson.getAsJsonObject())));
         } else if (pJson.isJsonArray()) {
            JsonArray jsonarray = pJson.getAsJsonArray();
            if (jsonarray.size() == 0) {
               throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
            } else {
               return fromValues(StreamSupport.stream(jsonarray.spliterator(), false).map((p_151264_) -> {
                  return valueFromJson(GsonHelper.convertToJsonObject(p_151264_, "item"));
               }));
            }
         } else {
            throw new JsonSyntaxException("Expected item to be object or array of objects");
         }
      } else {
         throw new JsonSyntaxException("Item cannot be null");
      }
   }

   public static Ingredient.Value valueFromJson(JsonObject pJson) {
      if (pJson.has("item") && pJson.has("tag")) {
         throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
      } else if (pJson.has("item")) {
         Item item = ShapedRecipe.itemFromJson(pJson);
         return new Ingredient.ItemValue(new ItemStack(item));
      } else if (pJson.has("tag")) {
         ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(pJson, "tag"));
         Tag<Item> tag = SerializationTags.getInstance().getTagOrThrow(Registry.ITEM_REGISTRY, resourcelocation, (p_151262_) -> {
            return new JsonSyntaxException("Unknown item tag '" + p_151262_ + "'");
         });
         return new Ingredient.TagValue(tag);
      } else {
         throw new JsonParseException("An ingredient entry needs either a tag or an item");
      }
   }

   //Merges several vanilla Ingredients together. As a quirk of how the json is structured, we can't tell if its a single Ingredient type or multiple so we split per item and re-merge here.
   //Only public for internal use, so we can access a private field in here.
   public static Ingredient merge(Collection<Ingredient> parts) {
      return fromValues(parts.stream().flatMap(i -> Arrays.stream(i.values)));
   }

   public static class ItemValue implements Ingredient.Value {
      private final ItemStack item;

      public ItemValue(ItemStack pItem) {
         this.item = pItem;
      }

      public Collection<ItemStack> getItems() {
         return Collections.singleton(this.item);
      }

      public JsonObject serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("item", Registry.ITEM.getKey(this.item.getItem()).toString());
         return jsonobject;
      }
   }

   public static class TagValue implements Ingredient.Value {
      private final Tag<Item> tag;

      public TagValue(Tag<Item> pTag) {
         this.tag = pTag;
      }

      public Collection<ItemStack> getItems() {
         List<ItemStack> list = Lists.newArrayList();

         for(Item item : this.tag.getValues()) {
            list.add(new ItemStack(item));
         }

         if (list.size() == 0 && !net.minecraftforge.common.ForgeConfig.SERVER.treatEmptyTagsAsAir.get()) {
            list.add(new ItemStack(net.minecraft.world.level.block.Blocks.BARRIER).setHoverName(new net.minecraft.network.chat.TextComponent("Empty Tag: " + SerializationTags.getInstance().getIdOrThrow(Registry.ITEM_REGISTRY, this.tag, () -> new IllegalStateException("Unrecognized tag")))));
         }
         return list;
      }

      public JsonObject serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("tag", SerializationTags.getInstance().getIdOrThrow(Registry.ITEM_REGISTRY, this.tag, () -> {
            return new IllegalStateException("Unknown item tag");
         }).toString());
         return jsonobject;
      }
   }

   public interface Value {
      Collection<ItemStack> getItems();

      JsonObject serialize();
   }
}
