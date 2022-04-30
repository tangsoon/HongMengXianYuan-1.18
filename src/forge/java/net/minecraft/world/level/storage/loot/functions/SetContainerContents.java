package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * LootItemFunction that sets the contents of a container such as a chest by setting the {@code BlocKEntityTag} of the
 * stacks.
 * The contents are based on a list of loot pools.
 */
public class SetContainerContents extends LootItemConditionalFunction {
   final List<LootPoolEntryContainer> entries;

   SetContainerContents(LootItemCondition[] p_80904_, List<LootPoolEntryContainer> p_80905_) {
      super(p_80904_);
      this.entries = ImmutableList.copyOf(p_80905_);
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_CONTENTS;
   }

   /**
    * Called to perform the actual action of this function, after conditions have been checked.
    */
   public ItemStack run(ItemStack pStack, LootContext pContext) {
      if (pStack.isEmpty()) {
         return pStack;
      } else {
         NonNullList<ItemStack> nonnulllist = NonNullList.create();
         this.entries.forEach((p_80916_) -> {
            p_80916_.expand(pContext, (p_165321_) -> {
               p_165321_.createItemStack(LootTable.createStackSplitter(nonnulllist::add), pContext);
            });
         });
         CompoundTag compoundtag = new CompoundTag();
         ContainerHelper.saveAllItems(compoundtag, nonnulllist);
         CompoundTag compoundtag1 = pStack.getOrCreateTag();
         compoundtag1.put("BlockEntityTag", compoundtag.merge(compoundtag1.getCompound("BlockEntityTag")));
         return pStack;
      }
   }

   /**
    * Validate that this object is used correctly according to the given ValidationContext.
    */
   public void validate(ValidationContext pContext) {
      super.validate(pContext);

      for(int i = 0; i < this.entries.size(); ++i) {
         this.entries.get(i).validate(pContext.forChild(".entry[" + i + "]"));
      }

   }

   public static SetContainerContents.Builder setContents() {
      return new SetContainerContents.Builder();
   }

   public static class Builder extends LootItemConditionalFunction.Builder<SetContainerContents.Builder> {
      private final List<LootPoolEntryContainer> entries = Lists.newArrayList();

      protected SetContainerContents.Builder getThis() {
         return this;
      }

      public SetContainerContents.Builder withEntry(LootPoolEntryContainer.Builder<?> pLootEntryBuilder) {
         this.entries.add(pLootEntryBuilder.build());
         return this;
      }

      public LootItemFunction build() {
         return new SetContainerContents(this.getConditions(), this.entries);
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetContainerContents> {
      /**
       * Serialize the value by putting its data into the JsonObject.
       */
      public void serialize(JsonObject pJson, SetContainerContents pValue, JsonSerializationContext pSerializationContext) {
         super.serialize(pJson, pValue, pSerializationContext);
         pJson.add("entries", pSerializationContext.serialize(pValue.entries));
      }

      public SetContainerContents deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
         LootPoolEntryContainer[] alootpoolentrycontainer = GsonHelper.getAsObject(pObject, "entries", pDeserializationContext, LootPoolEntryContainer[].class);
         return new SetContainerContents(pConditions, Arrays.asList(alootpoolentrycontainer));
      }
   }
}