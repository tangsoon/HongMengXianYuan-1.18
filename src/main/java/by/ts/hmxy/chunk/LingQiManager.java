package by.ts.hmxy.chunk;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class LingQiManager extends SavedData {

    private final Map<ChunkPos, LingQi> lingQiMap = new HashMap<>();
    private final Random random = new Random();

    private int counter = 0;

    @Nonnull
    public static LingQiManager get(Level level) {
        if (level.isClientSide) {
            throw new RuntimeException("Don't access this client-side!");
        }

        DimensionDataStorage storage = ((ServerLevel)level).getDataStorage();
        return storage.computeIfAbsent(LingQiManager::new, LingQiManager::new, "manamanager");
    }

    @NotNull
    private LingQi getLingQiInternal(BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        return lingQiMap.computeIfAbsent(chunkPos, cp -> new LingQi(random.nextInt(10) +10));
    }

    public int getQingLi(BlockPos pos) {
        LingQi lingQi = getLingQiInternal(pos);
        return lingQi.getLingQi();
    }

    public int extractLingQi(BlockPos pos) {
        LingQi lingQi = getLingQiInternal(pos);
        int present = lingQi.getLingQi();
        if (present > 0) {
        	lingQi.setLingQi(present-1);;
            setDirty();
            return 1;
        } else {
            return 0;
        }
    }

    public void tick(Level level) {
        counter--;
        if (counter <= 0) {
            counter = 10;

            level.players().forEach(player -> {
                if (player instanceof ServerPlayer serverPlayer) {
//                    int playerMana = serverPlayer.getCapability(PlayerManaProvider.PLAYER_MANA)
//                            .map(PlayerMana::getMana)
//                            .orElse(-1);
//                    int chunkMana = getLingQI(serverPlayer.blockPosition());
//                    Messages.sendToPlayer(new PacketSyncManaToClient(playerMana, chunkMana), serverPlayer);
                }
            });

            // todo expansion: here it would be possible to slowly regenerate mana in chunks
        }
    }

    // This constructor is called for a new mana manager
    public LingQiManager() {
    }

    public LingQiManager(CompoundTag tag) {
        ListTag list = tag.getList("lingQi", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag lingQiTag = (CompoundTag) t;
            LingQi lingQi = new LingQi(lingQiTag.getInt("lingQi"));
            ChunkPos pos = new ChunkPos(lingQiTag.getInt("x"), lingQiTag.getInt("z"));
            lingQiMap.put(pos, lingQi);
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        lingQiMap.forEach((chunkPos, lingQi) -> {
            CompoundTag lingQiTag = new CompoundTag();
            lingQiTag.putInt("x", chunkPos.x);
            lingQiTag.putInt("z", chunkPos.z);
            lingQiTag.putInt("lingQi", lingQi.getLingQi());
            list.add(lingQiTag);
        });
        tag.put("lingQi", list);
        return tag;
    }

}