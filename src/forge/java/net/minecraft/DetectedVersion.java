package net.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.bridge.game.GameVersion;
import com.mojang.bridge.game.PackType;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DetectedVersion implements GameVersion {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final GameVersion BUILT_IN = new DetectedVersion();
   private final String id;
   private final String name;
   private final boolean stable;
   private final int worldVersion;
   private final int protocolVersion;
   private final int resourcePackVersion;
   private final int dataPackVersion;
   private final Date buildTime;
   private final String releaseTarget;

   private DetectedVersion() {
      this.id = UUID.randomUUID().toString().replaceAll("-", "");
      this.name = "1.17.1";
      this.stable = true;
      this.worldVersion = 2730;
      this.protocolVersion = SharedConstants.getProtocolVersion();
      this.resourcePackVersion = 7;
      this.dataPackVersion = 7;
      this.buildTime = new Date();
      this.releaseTarget = "1.17.1";
   }

   private DetectedVersion(JsonObject pJson) {
      this.id = GsonHelper.getAsString(pJson, "id");
      this.name = GsonHelper.getAsString(pJson, "name");
      this.releaseTarget = GsonHelper.getAsString(pJson, "release_target");
      this.stable = GsonHelper.getAsBoolean(pJson, "stable");
      this.worldVersion = GsonHelper.getAsInt(pJson, "world_version");
      this.protocolVersion = GsonHelper.getAsInt(pJson, "protocol_version");
      JsonObject jsonobject = GsonHelper.getAsJsonObject(pJson, "pack_version");
      this.resourcePackVersion = GsonHelper.getAsInt(jsonobject, "resource");
      this.dataPackVersion = GsonHelper.getAsInt(jsonobject, "data");
      this.buildTime = Date.from(ZonedDateTime.parse(GsonHelper.getAsString(pJson, "build_time")).toInstant());
   }

   /**
    * Creates a new instance containing game version data from version.json (or fallback data if necessary).
    * 
    * For getting data, use {@link SharedConstants#getVersion} instead, as that is cached.
    */
   public static GameVersion tryDetectVersion() {
      try {
         InputStream inputstream = DetectedVersion.class.getResourceAsStream("/version.json");

         GameVersion gameversion;
         label63: {
            DetectedVersion detectedversion;
            try {
               if (inputstream == null) {
                  LOGGER.warn("Missing version information!");
                  gameversion = BUILT_IN;
                  break label63;
               }

               InputStreamReader inputstreamreader = new InputStreamReader(inputstream);

               try {
                  detectedversion = new DetectedVersion(GsonHelper.parse(inputstreamreader));
               } catch (Throwable throwable2) {
                  try {
                     inputstreamreader.close();
                  } catch (Throwable throwable1) {
                     throwable2.addSuppressed(throwable1);
                  }

                  throw throwable2;
               }

               inputstreamreader.close();
            } catch (Throwable throwable3) {
               if (inputstream != null) {
                  try {
                     inputstream.close();
                  } catch (Throwable throwable) {
                     throwable3.addSuppressed(throwable);
                  }
               }

               throw throwable3;
            }

            if (inputstream != null) {
               inputstream.close();
            }

            return detectedversion;
         }

         if (inputstream != null) {
            inputstream.close();
         }

         return gameversion;
      } catch (JsonParseException | IOException ioexception) {
         throw new IllegalStateException("Game version information is corrupt", ioexception);
      }
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getReleaseTarget() {
      return this.releaseTarget;
   }

   public int getWorldVersion() {
      return this.worldVersion;
   }

   public int getProtocolVersion() {
      return this.protocolVersion;
   }

   public int getPackVersion(PackType pType) {
      return pType == PackType.DATA ? this.dataPackVersion : this.resourcePackVersion;
   }

   public Date getBuildTime() {
      return this.buildTime;
   }

   public boolean isStable() {
      return this.stable;
   }
}