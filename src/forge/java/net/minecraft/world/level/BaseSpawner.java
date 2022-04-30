package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseSpawner {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int EVENT_SPAWN = 1;
   private static WeightedRandomList<SpawnData> EMPTY_POTENTIALS = WeightedRandomList.create();
   private int spawnDelay = 20;
   private WeightedRandomList<SpawnData> spawnPotentials = EMPTY_POTENTIALS;
   private SpawnData nextSpawnData = new SpawnData();
   private double spin;
   private double oSpin;
   private int minSpawnDelay = 200;
   private int maxSpawnDelay = 800;
   private int spawnCount = 4;
   /** Cached instance of the entity to render inside the spawner. */
   @Nullable
   private Entity displayEntity;
   private int maxNearbyEntities = 6;
   private int requiredPlayerRange = 16;
   private int spawnRange = 4;
   private final Random random = new Random();

   @Nullable
   private ResourceLocation getEntityId(@Nullable Level pLevel, BlockPos pPos) {
      String s = this.nextSpawnData.getTag().getString("id");

      try {
         return StringUtil.isNullOrEmpty(s) ? null : new ResourceLocation(s);
      } catch (ResourceLocationException resourcelocationexception) {
         LOGGER.warn("Invalid entity id '{}' at spawner {}:[{},{},{}]", s, pLevel != null ? pLevel.dimension().location() : "<null>", pPos.getX(), pPos.getY(), pPos.getZ());
         return null;
      }
   }

   public void setEntityId(EntityType<?> pType) {
      this.nextSpawnData.getTag().putString("id", Registry.ENTITY_TYPE.getKey(pType).toString());
   }

   private boolean isNearPlayer(Level pLevel, BlockPos pPos) {
      return pLevel.hasNearbyAlivePlayer((double)pPos.getX() + 0.5D, (double)pPos.getY() + 0.5D, (double)pPos.getZ() + 0.5D, (double)this.requiredPlayerRange);
   }

   public void clientTick(Level pLevel, BlockPos pPos) {
      if (!this.isNearPlayer(pLevel, pPos)) {
         this.oSpin = this.spin;
      } else {
         double d0 = (double)pPos.getX() + pLevel.random.nextDouble();
         double d1 = (double)pPos.getY() + pLevel.random.nextDouble();
         double d2 = (double)pPos.getZ() + pLevel.random.nextDouble();
         pLevel.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
         pLevel.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
         if (this.spawnDelay > 0) {
            --this.spawnDelay;
         }

         this.oSpin = this.spin;
         this.spin = (this.spin + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0D;
      }

   }

   public void serverTick(ServerLevel pServerLevel, BlockPos pPos) {
      if (this.isNearPlayer(pServerLevel, pPos)) {
         if (this.spawnDelay == -1) {
            this.delay(pServerLevel, pPos);
         }

         if (this.spawnDelay > 0) {
            --this.spawnDelay;
         } else {
            boolean flag = false;

            for(int i = 0; i < this.spawnCount; ++i) {
               CompoundTag compoundtag = this.nextSpawnData.getTag();
               Optional<EntityType<?>> optional = EntityType.by(compoundtag);
               if (!optional.isPresent()) {
                  this.delay(pServerLevel, pPos);
                  return;
               }

               ListTag listtag = compoundtag.getList("Pos", 6);
               int j = listtag.size();
               double d0 = j >= 1 ? listtag.getDouble(0) : (double)pPos.getX() + (pServerLevel.random.nextDouble() - pServerLevel.random.nextDouble()) * (double)this.spawnRange + 0.5D;
               double d1 = j >= 2 ? listtag.getDouble(1) : (double)(pPos.getY() + pServerLevel.random.nextInt(3) - 1);
               double d2 = j >= 3 ? listtag.getDouble(2) : (double)pPos.getZ() + (pServerLevel.random.nextDouble() - pServerLevel.random.nextDouble()) * (double)this.spawnRange + 0.5D;
               if (pServerLevel.noCollision(optional.get().getAABB(d0, d1, d2)) && SpawnPlacements.checkSpawnRules(optional.get(), pServerLevel, MobSpawnType.SPAWNER, new BlockPos(d0, d1, d2), pServerLevel.getRandom())) {
                  Entity entity = EntityType.loadEntityRecursive(compoundtag, pServerLevel, (p_151310_) -> {
                     p_151310_.moveTo(d0, d1, d2, p_151310_.getYRot(), p_151310_.getXRot());
                     return p_151310_;
                  });
                  if (entity == null) {
                     this.delay(pServerLevel, pPos);
                     return;
                  }

                  int k = pServerLevel.getEntitiesOfClass(entity.getClass(), (new AABB((double)pPos.getX(), (double)pPos.getY(), (double)pPos.getZ(), (double)(pPos.getX() + 1), (double)(pPos.getY() + 1), (double)(pPos.getZ() + 1))).inflate((double)this.spawnRange)).size();
                  if (k >= this.maxNearbyEntities) {
                     this.delay(pServerLevel, pPos);
                     return;
                  }

                  entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), pServerLevel.random.nextFloat() * 360.0F, 0.0F);
                  if (entity instanceof Mob) {
                     Mob mob = (Mob)entity;
                     if (!net.minecraftforge.event.ForgeEventFactory.canEntitySpawnSpawner(mob, pServerLevel, (float)entity.getX(), (float)entity.getY(), (float)entity.getZ(), this)) {
                        continue;
                     }

                     if (this.nextSpawnData.getTag().size() == 1 && this.nextSpawnData.getTag().contains("id", 8)) {
                        if (!net.minecraftforge.event.ForgeEventFactory.doSpecialSpawn(mob, pServerLevel, (float)entity.getX(), (float)entity.getY(), (float)entity.getZ(), this, MobSpawnType.SPAWNER))
                        ((Mob)entity).finalizeSpawn(pServerLevel, pServerLevel.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.SPAWNER, (SpawnGroupData)null, (CompoundTag)null);
                     }
                  }

                  if (!pServerLevel.tryAddFreshEntityWithPassengers(entity)) {
                     this.delay(pServerLevel, pPos);
                     return;
                  }

                  pServerLevel.levelEvent(2004, pPos, 0);
                  if (entity instanceof Mob) {
                     ((Mob)entity).spawnAnim();
                  }

                  flag = true;
               }
            }

            if (flag) {
               this.delay(pServerLevel, pPos);
            }

         }
      }
   }

   private void delay(Level pLevel, BlockPos pPos) {
      if (this.maxSpawnDelay <= this.minSpawnDelay) {
         this.spawnDelay = this.minSpawnDelay;
      } else {
         this.spawnDelay = this.minSpawnDelay + this.random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
      }

      this.spawnPotentials.getRandom(this.random).ifPresent((p_151349_) -> {
         this.setNextSpawnData(pLevel, pPos, p_151349_);
      });
      this.broadcastEvent(pLevel, pPos, 1);
   }

   public void load(@Nullable Level pLevel, BlockPos pPos, CompoundTag pTag) {
      this.spawnDelay = pTag.getShort("Delay");
      List<SpawnData> list = Lists.newArrayList();
      if (pTag.contains("SpawnPotentials", 9)) {
         ListTag listtag = pTag.getList("SpawnPotentials", 10);

         for(int i = 0; i < listtag.size(); ++i) {
            list.add(new SpawnData(listtag.getCompound(i)));
         }
      }

      this.spawnPotentials = WeightedRandomList.create(list);
      if (pTag.contains("SpawnData", 10)) {
         this.setNextSpawnData(pLevel, pPos, new SpawnData(1, pTag.getCompound("SpawnData")));
      } else if (!list.isEmpty()) {
         this.spawnPotentials.getRandom(this.random).ifPresent((p_151338_) -> {
            this.setNextSpawnData(pLevel, pPos, p_151338_);
         });
      }

      if (pTag.contains("MinSpawnDelay", 99)) {
         this.minSpawnDelay = pTag.getShort("MinSpawnDelay");
         this.maxSpawnDelay = pTag.getShort("MaxSpawnDelay");
         this.spawnCount = pTag.getShort("SpawnCount");
      }

      if (pTag.contains("MaxNearbyEntities", 99)) {
         this.maxNearbyEntities = pTag.getShort("MaxNearbyEntities");
         this.requiredPlayerRange = pTag.getShort("RequiredPlayerRange");
      }

      if (pTag.contains("SpawnRange", 99)) {
         this.spawnRange = pTag.getShort("SpawnRange");
      }

      this.displayEntity = null;
   }

   public CompoundTag save(@Nullable Level pLevel, BlockPos pPos, CompoundTag pTag) {
      ResourceLocation resourcelocation = this.getEntityId(pLevel, pPos);
      if (resourcelocation == null) {
         return pTag;
      } else {
         pTag.putShort("Delay", (short)this.spawnDelay);
         pTag.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
         pTag.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
         pTag.putShort("SpawnCount", (short)this.spawnCount);
         pTag.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
         pTag.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
         pTag.putShort("SpawnRange", (short)this.spawnRange);
         pTag.put("SpawnData", this.nextSpawnData.getTag().copy());
         ListTag listtag = new ListTag();
         if (this.spawnPotentials.isEmpty()) {
            listtag.add(this.nextSpawnData.save());
         } else {
            for(SpawnData spawndata : this.spawnPotentials.unwrap()) {
               listtag.add(spawndata.save());
            }
         }

         pTag.put("SpawnPotentials", listtag);
         return pTag;
      }
   }

   @Nullable
   public Entity getOrCreateDisplayEntity(Level pLevel) {
      if (this.displayEntity == null) {
         this.displayEntity = EntityType.loadEntityRecursive(this.nextSpawnData.getTag(), pLevel, Function.identity());
         if (this.nextSpawnData.getTag().size() == 1 && this.nextSpawnData.getTag().contains("id", 8) && this.displayEntity instanceof Mob) {
         }
      }

      return this.displayEntity;
   }

   public boolean onEventTriggered(Level p_151317_, int p_151318_) {
      if (p_151318_ == 1) {
         if (p_151317_.isClientSide) {
            this.spawnDelay = this.minSpawnDelay;
         }

         return true;
      } else {
         return false;
      }
   }

   public void setNextSpawnData(@Nullable Level pLevel, BlockPos pPos, SpawnData pNextSpawnData) {
      this.nextSpawnData = pNextSpawnData;
   }

   public abstract void broadcastEvent(Level p_151322_, BlockPos p_151323_, int p_151324_);

   public double getSpin() {
      return this.spin;
   }

   public double getoSpin() {
      return this.oSpin;
   }

   @Nullable
   public Entity getSpawnerEntity() {
      return null;
   }

   @Nullable
   public net.minecraft.world.level.block.entity.BlockEntity getSpawnerBlockEntity(){ return null; }
}
