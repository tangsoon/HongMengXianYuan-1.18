package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class EffectRenderingInventoryScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
   /** True if there is some potion effect to display */
   protected boolean doRenderEffects;

   public EffectRenderingInventoryScreen(T p_98701_, Inventory p_98702_, Component p_98703_) {
      super(p_98701_, p_98702_, p_98703_);
   }

   protected void init() {
      super.init();
      this.checkEffectRendering();
   }

   protected void checkEffectRendering() {
      if (this.minecraft.player.getActiveEffects().isEmpty()) {
         this.leftPos = (this.width - this.imageWidth) / 2;
         this.doRenderEffects = false;
      } else {
         if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.PotionShiftEvent(this)))
            this.leftPos = (this.width - this.imageWidth) / 2;
         else
         this.leftPos = 160 + (this.width - this.imageWidth - 200) / 2;
         this.doRenderEffects = true;
      }

   }

   public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
      super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
      if (this.doRenderEffects) {
         this.renderEffects(pPoseStack);
      }

   }

   private void renderEffects(PoseStack pPoseStack) {
      int i = this.leftPos - 124;
      Collection<MobEffectInstance> collection = this.minecraft.player.getActiveEffects();
      if (!collection.isEmpty()) {
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         int j = 33;
         if (collection.size() > 5) {
            j = 132 / (collection.size() - 1);
         }

         Iterable<MobEffectInstance> iterable = collection.stream().filter(net.minecraftforge.client.ForgeHooksClient::shouldRender).sorted().collect(java.util.stream.Collectors.toList());
         this.renderBackgrounds(pPoseStack, i, j, iterable);
         this.renderIcons(pPoseStack, i, j, iterable);
         this.renderLabels(pPoseStack, i, j, iterable);
      }
   }

   private void renderBackgrounds(PoseStack pPoseStack, int pRenderX, int pYOffset, Iterable<MobEffectInstance> pEffects) {
      RenderSystem.setShaderTexture(0, INVENTORY_LOCATION);
      int i = this.topPos;

      for(MobEffectInstance mobeffectinstance : pEffects) {
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         this.blit(pPoseStack, pRenderX, i, 0, 166, 140, 32);
         i += pYOffset;
      }

   }

   private void renderIcons(PoseStack pPoseStack, int pRenderX, int pYOffset, Iterable<MobEffectInstance> pEffects) {
      MobEffectTextureManager mobeffecttexturemanager = this.minecraft.getMobEffectTextures();
      int i = this.topPos;

      for(MobEffectInstance mobeffectinstance : pEffects) {
         MobEffect mobeffect = mobeffectinstance.getEffect();
         TextureAtlasSprite textureatlassprite = mobeffecttexturemanager.get(mobeffect);
         RenderSystem.setShaderTexture(0, textureatlassprite.atlas().location());
         blit(pPoseStack, pRenderX + 6, i + 7, this.getBlitOffset(), 18, 18, textureatlassprite);
         i += pYOffset;
      }

   }

   private void renderLabels(PoseStack pPoseStack, int pRenderX, int pYOffset, Iterable<MobEffectInstance> pEffects) {
      int i = this.topPos;

      for(MobEffectInstance mobeffectinstance : pEffects) {
         net.minecraftforge.client.EffectRenderer renderer = net.minecraftforge.client.RenderProperties.getEffectRenderer(mobeffectinstance);
         renderer.renderInventoryEffect(mobeffectinstance, this, pPoseStack, pRenderX, i, this.getBlitOffset());
         if (!renderer.shouldRenderInvText(mobeffectinstance)) {
            i += pYOffset;
            continue;
         }
         String s = I18n.get(mobeffectinstance.getEffect().getDescriptionId());
         if (mobeffectinstance.getAmplifier() >= 1 && mobeffectinstance.getAmplifier() <= 9) {
            s = s + " " + I18n.get("enchantment.level." + (mobeffectinstance.getAmplifier() + 1));
         }

         this.font.drawShadow(pPoseStack, s, (float)(pRenderX + 10 + 18), (float)(i + 6), 16777215);
         String s1 = MobEffectUtil.formatDuration(mobeffectinstance, 1.0F);
         this.font.drawShadow(pPoseStack, s1, (float)(pRenderX + 10 + 18), (float)(i + 6 + 10), 8355711);
         i += pYOffset;
      }

   }
}
