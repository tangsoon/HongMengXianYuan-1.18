package net.minecraft.client.server;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.stats.Stats;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Snooper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class IntegratedServer extends MinecraftServer {
   public static final int CLIENT_VIEW_DISTANCE_OFFSET = -1;
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   private boolean paused;
   private int publishedPort = -1;
   @Nullable
   private GameType publishedGameType;
   private LanServerPinger lanPinger;
   private UUID uuid;

   public IntegratedServer(Thread pServerThread, Minecraft pMinecraft, RegistryAccess.RegistryHolder pRegistryHolder, LevelStorageSource.LevelStorageAccess pStorageSource, PackRepository pPackRepository, ServerResources pResources, WorldData pWorldData, MinecraftSessionService pSessionService, GameProfileRepository pProfileRepository, GameProfileCache pProfileCache, ChunkProgressListenerFactory pProgressListenerFactory) {
      super(pServerThread, pRegistryHolder, pStorageSource, pWorldData, pPackRepository, pMinecraft.getProxy(), pMinecraft.getFixerUpper(), pResources, pSessionService, pProfileRepository, pProfileCache, pProgressListenerFactory);
      this.setSingleplayerName(pMinecraft.getUser().getName());
      this.setDemo(pMinecraft.isDemo());
      this.setPlayerList(new IntegratedPlayerList(this, this.registryHolder, this.playerDataStorage));
      this.minecraft = pMinecraft;
   }

   /**
    * Initialises the server and starts it.
    */
   public boolean initServer() {
      LOGGER.info("Starting integrated minecraft server version {}", (Object)SharedConstants.getCurrentVersion().getName());
      this.setUsesAuthentication(true);
      this.setPvpAllowed(true);
      this.setFlightAllowed(true);
      this.initializeKeyPair();
      if (!net.minecraftforge.fmllegacy.server.ServerLifecycleHooks.handleServerAboutToStart(this)) return false;
      this.loadLevel();
      this.setMotd(this.getSingleplayerName() + " - " + this.getWorldData().getLevelName());
      return net.minecraftforge.fmllegacy.server.ServerLifecycleHooks.handleServerStarting(this);
   }

   /**
    * Main function called by run() every loop.
    */
   public void tickServer(BooleanSupplier pHasTimeLeft) {
      boolean flag = this.paused;
      this.paused = Minecraft.getInstance().getConnection() != null && Minecraft.getInstance().isPaused();
      ProfilerFiller profilerfiller = this.getProfiler();
      if (!flag && this.paused) {
         profilerfiller.push("autoSave");
         LOGGER.info("Saving and pausing game...");
         this.getPlayerList().saveAll();
         this.saveAllChunks(false, false, false);
         profilerfiller.pop();
      }

      if (this.paused) {
         this.tickPaused();
      } else {
         super.tickServer(pHasTimeLeft);
         int i = Math.max(2, this.minecraft.options.renderDistance + -1);
         if (i != this.getPlayerList().getViewDistance()) {
            LOGGER.info("Changing view distance to {}, from {}", i, this.getPlayerList().getViewDistance());
            this.getPlayerList().setViewDistance(i);
         }

      }
   }

   private void tickPaused() {
      for(ServerPlayer serverplayer : this.getPlayerList().getPlayers()) {
         serverplayer.awardStat(Stats.TOTAL_WORLD_TIME);
      }

   }

   public boolean shouldRconBroadcast() {
      return true;
   }

   public boolean shouldInformAdmins() {
      return true;
   }

   public File getServerDirectory() {
      return this.minecraft.gameDirectory;
   }

   public boolean isDedicatedServer() {
      return false;
   }

   public int getRateLimitPacketsPerSecond() {
      return 0;
   }

   /**
    * Get if native transport should be used. Native transport means linux server performance improvements and optimized
    * packet sending/receiving on linux
    */
   public boolean isEpollEnabled() {
      return false;
   }

   /**
    * Called on exit from the main run() loop.
    */
   public void onServerCrash(CrashReport pReport) {
      this.minecraft.delayCrash(pReport);
   }

   public SystemReport fillServerSystemReport(SystemReport pReport) {
      pReport.setDetail("Type", "Integrated Server (map_client.txt)");
      pReport.setDetail("Is Modded", () -> {
         return this.getModdedStatus().orElse("Probably not. Jar signature remains and both client + server brands are untouched.");
      });
      return pReport;
   }

   public Optional<String> getModdedStatus() {
      String s = ClientBrandRetriever.getClientModName();
      if (!s.equals("vanilla")) {
         return Optional.of("Definitely; Client brand changed to '" + s + "'");
      } else {
         s = this.getServerModName();
         if (!"vanilla".equals(s)) {
            return Optional.of("Definitely; Server brand changed to '" + s + "'");
         } else {
            return Minecraft.class.getSigners() == null ? Optional.of("Very likely; Jar signature invalidated") : Optional.empty();
         }
      }
   }

   public void populateSnooper(Snooper pSnooper) {
      super.populateSnooper(pSnooper);
      pSnooper.setDynamicData("snooper_partner", this.minecraft.getSnooper().getToken());
   }

   public boolean isSnooperEnabled() {
      return Minecraft.getInstance().isSnooperEnabled();
   }

   public boolean publishServer(@Nullable GameType pGameMode, boolean pCheats, int pPort) {
      try {
         this.getConnection().startTcpServerListener((InetAddress)null, pPort);
         LOGGER.info("Started serving on {}", (int)pPort);
         this.publishedPort = pPort;
         this.lanPinger = new LanServerPinger(this.getMotd(), "" + pPort);
         this.lanPinger.start();
         this.publishedGameType = pGameMode;
         this.getPlayerList().setAllowCheatsForAllPlayers(pCheats);
         int i = this.getProfilePermissions(this.minecraft.player.getGameProfile());
         this.minecraft.player.setPermissionLevel(i);

         for(ServerPlayer serverplayer : this.getPlayerList().getPlayers()) {
            this.getCommands().sendCommands(serverplayer);
         }

         return true;
      } catch (IOException ioexception) {
         return false;
      }
   }

   /**
    * Saves all necessary data as preparation for stopping the server.
    */
   public void stopServer() {
      super.stopServer();
      if (this.lanPinger != null) {
         this.lanPinger.interrupt();
         this.lanPinger = null;
      }

   }

   /**
    * Sets the serverRunning variable to false, in order to get the server to shut down.
    */
   public void halt(boolean pWaitForServer) {
      if (isRunning())
      this.executeBlocking(() -> {
         for(ServerPlayer serverplayer : Lists.newArrayList(this.getPlayerList().getPlayers())) {
            if (!serverplayer.getUUID().equals(this.uuid)) {
               this.getPlayerList().remove(serverplayer);
            }
         }

      });
      super.halt(pWaitForServer);
      if (this.lanPinger != null) {
         this.lanPinger.interrupt();
         this.lanPinger = null;
      }

   }

   /**
    * Returns true if this integrated server is open to LAN
    */
   public boolean isPublished() {
      return this.publishedPort > -1;
   }

   /**
    * Gets serverPort.
    */
   public int getPort() {
      return this.publishedPort;
   }

   /**
    * Sets the game type for all worlds.
    */
   public void setDefaultGameType(GameType pGameMode) {
      super.setDefaultGameType(pGameMode);
      this.publishedGameType = null;
   }

   /**
    * Return whether command blocks are enabled.
    */
   public boolean isCommandBlockEnabled() {
      return true;
   }

   public int getOperatorUserPermissionLevel() {
      return 2;
   }

   public int getFunctionCompilationLevel() {
      return 2;
   }

   public void setUUID(UUID pUuid) {
      this.uuid = pUuid;
   }

   public boolean isSingleplayerOwner(GameProfile pProfile) {
      return pProfile.getName().equalsIgnoreCase(this.getSingleplayerName());
   }

   public int getScaledTrackingDistance(int p_120056_) {
      return (int)(this.minecraft.options.entityDistanceScaling * (float)p_120056_);
   }

   public boolean forceSynchronousWrites() {
      return this.minecraft.options.syncWrites;
   }

   @Nullable
   public GameType getForcedGameType() {
      return this.isPublished() ? MoreObjects.firstNonNull(this.publishedGameType, this.worldData.getGameType()) : null;
   }
}
