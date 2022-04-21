package net.minecraft.client.resources.sounds;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoundEventRegistration {
   private final List<Sound> sounds;
   /** if true it will override all the sounds from the resourcepacks loaded before */
   private final boolean replace;
   private final String subtitle;

   public SoundEventRegistration(List<Sound> pSounds, boolean pReplace, String pSubtitle) {
      this.sounds = pSounds;
      this.replace = pReplace;
      this.subtitle = pSubtitle;
   }

   public List<Sound> getSounds() {
      return this.sounds;
   }

   public boolean isReplace() {
      return this.replace;
   }

   @Nullable
   public String getSubtitle() {
      return this.subtitle;
   }
}