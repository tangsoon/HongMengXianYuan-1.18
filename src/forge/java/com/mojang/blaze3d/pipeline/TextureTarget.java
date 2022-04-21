package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextureTarget extends RenderTarget {
   public TextureTarget(int pWidth, int pHeight, boolean pUseDepth, boolean pClearError) {
      super(pUseDepth);
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      this.resize(pWidth, pHeight, pClearError);
   }
}