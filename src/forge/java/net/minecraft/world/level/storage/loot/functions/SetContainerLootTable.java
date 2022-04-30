package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * LootItemFunction that sets the LootTable and optionally the loot table seed on the stack's {@code BlockEntityTag}.
 * The effect of this is that containers such as chests will receive the given LootTable when placed.
 */
public class SetContainerLootTable extends LootItemConditionalFunction {
   final ResourceLocation name;
   final long seed;

   SetContainerLootTable(LootItemCondition[] pConditions, ResourceLocation pLootTableId, long pLootTableSeed) {
      super(pConditions);
      this.name = pLootTableId;
      this.seed = pLootTableSeed;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_LOOT_TABLE;
   }

   /**
    * Called to perform the actual action of this function, after conditions have been checked.
    */
   public ItemStack run(ItemStack pStack, LootContext pContext) {
      if (pStack.isEmpty()) {
         return pStack;
      } else {
         CompoundTag compoundtag = new CompoundTag();
         compoundtag.putString("LootTable", this.name.toString());
         if (this.seed != 0L) {
            compoundtag.putLong("LootTableSeed", this.seed);
         }

         pStack.getOrCreateTag().put("BlockEntityTag", compoundtag);
         return pStack;
      }
   }

   /**
    * Validate that this object is used correctly according to the given ValidationContext.
    */
   public void validate(ValidationContext pContext) {
      if (pContext.hasVisitedTable(this.name)) {
         pContext.reportProblem("Table " + this.name + " is recursively called");
      } else {
         super.validate(pContext);
         LootTable loottable = pContext.resolveLootTable(this.name);
         if (loottable == null) {
            pContext.reportProblem("Unknown loot table called " + this.name);
         } else {
            loottable.validate(pContext.enterTable("->{" + this.name + "}", this.name));
         }

      }
   }

   public static LootItemConditionalFunction.Builder<?> withLootTable(ResourceLocation pLootTableId) {
      return simpleBuilder((p_165333_) -> {
         return new SetContainerLootTable(p_165333_, pLootTableId, 0L);
      });
   }

   public static LootItemConditionalFunction.Builder<?> withLootTable(ResourceLocation pLootTableId, long pLootTableSeed) {
      return simpleBuilder((p_165330_) -> {
         return new SetContainerLootTable(p_165330_, pLootTableId, pLootTableSeed);
      });
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetContainerLootTable> {
      /**
       * Serialize the value by putting its data into the JsonObject.
       */
      public void serialize(JsonObject pJson, SetContainerLootTable pValue, JsonSerializationContext pSerializationContext) {
         super.serialize(pJson, pValue, pSerializationContext);
         pJson.addProperty("name", pValue.name.toString());
         if (pValue.seed != 0L) {
            pJson.addProperty("seed", pValue.seed);
         }

      }

      public SetContainerLootTable deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
         ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(pObject, "name"));
         long i = GsonHelper.getAsLong(pObject, "seed", 0L);
         return new SetContainerLootTable(pConditions, resourcelocation, i);
      }
   }
}