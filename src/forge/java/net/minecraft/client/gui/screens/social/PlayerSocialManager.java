package net.minecraft.client.gui.screens.social;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.SocialInteractionsService;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerSocialManager {
   private final Minecraft minecraft;
   private final Set<UUID> hiddenPlayers = Sets.newHashSet();
   private final SocialInteractionsService service;
   private final Map<String, UUID> discoveredNamesToUUID = Maps.newHashMap();

   public PlayerSocialManager(Minecraft pMinecraft, SocialInteractionsService pService) {
      this.minecraft = pMinecraft;
      this.service = pService;
   }

   public void hidePlayer(UUID pId) {
      this.hiddenPlayers.add(pId);
   }

   public void showPlayer(UUID pId) {
      this.hiddenPlayers.remove(pId);
   }

   public boolean shouldHideMessageFrom(UUID pId) {
      return this.isHidden(pId) || this.isBlocked(pId);
   }

   public boolean isHidden(UUID pId) {
      return this.hiddenPlayers.contains(pId);
   }

   public boolean isBlocked(UUID pId) {
      return this.service.isBlockedPlayer(pId);
   }

   public Set<UUID> getHiddenPlayers() {
      return this.hiddenPlayers;
   }

   public UUID getDiscoveredUUID(String p_100679_) {
      return this.discoveredNamesToUUID.getOrDefault(p_100679_, Util.NIL_UUID);
   }

   public void addPlayer(PlayerInfo pPlayerInfo) {
      GameProfile gameprofile = pPlayerInfo.getProfile();
      if (gameprofile.isComplete()) {
         this.discoveredNamesToUUID.put(gameprofile.getName(), gameprofile.getId());
      }

      Screen screen = this.minecraft.screen;
      if (screen instanceof SocialInteractionsScreen) {
         SocialInteractionsScreen socialinteractionsscreen = (SocialInteractionsScreen)screen;
         socialinteractionsscreen.onAddPlayer(pPlayerInfo);
      }

   }

   public void removePlayer(UUID pId) {
      Screen screen = this.minecraft.screen;
      if (screen instanceof SocialInteractionsScreen) {
         SocialInteractionsScreen socialinteractionsscreen = (SocialInteractionsScreen)screen;
         socialinteractionsscreen.onRemovePlayer(pId);
      }

   }
}