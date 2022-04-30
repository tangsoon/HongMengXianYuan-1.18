package net.minecraft.world.level;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedEntry;

public class SpawnData extends WeightedEntry.IntrusiveBase {
   public static final int DEFAULT_WEIGHT = 1;
   public static final String DEFAULT_TYPE = "minecraft:pig";
   private final CompoundTag tag;

   public SpawnData() {
      super(1);
      this.tag = new CompoundTag();
      this.tag.putString("id", "minecraft:pig");
   }

   public SpawnData(CompoundTag pTag) {
      this(pTag.contains("Weight", 99) ? pTag.getInt("Weight") : 1, pTag.getCompound("Entity"));
   }

   public SpawnData(int pWeight, CompoundTag pTag) {
      super(pWeight);
      this.tag = pTag;
      ResourceLocation resourcelocation = ResourceLocation.tryParse(pTag.getString("id"));
      if (resourcelocation != null) {
         pTag.putString("id", resourcelocation.toString());
      } else {
         pTag.putString("id", "minecraft:pig");
      }

   }

   public CompoundTag save() {
      CompoundTag compoundtag = new CompoundTag();
      compoundtag.put("Entity", this.tag);
      compoundtag.putInt("Weight", this.getWeight().asInt());
      return compoundtag;
   }

   public CompoundTag getTag() {
      return this.tag;
   }
}