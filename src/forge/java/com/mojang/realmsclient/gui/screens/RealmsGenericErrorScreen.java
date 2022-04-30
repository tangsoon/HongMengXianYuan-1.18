package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.exception.RealmsServiceException;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsGenericErrorScreen extends RealmsScreen {
   private final Screen nextScreen;
   private Component line1;
   private Component line2;

   public RealmsGenericErrorScreen(RealmsServiceException pServiceException, Screen pNextScreen) {
      super(NarratorChatListener.NO_TITLE);
      this.nextScreen = pNextScreen;
      this.errorMessage(pServiceException);
   }

   public RealmsGenericErrorScreen(Component pServiceException, Screen pNextScreen) {
      super(NarratorChatListener.NO_TITLE);
      this.nextScreen = pNextScreen;
      this.errorMessage(pServiceException);
   }

   public RealmsGenericErrorScreen(Component pLine1, Component pLine2, Screen pNextScreen) {
      super(NarratorChatListener.NO_TITLE);
      this.nextScreen = pNextScreen;
      this.errorMessage(pLine1, pLine2);
   }

   private void errorMessage(RealmsServiceException pException) {
      if (pException.errorCode == -1) {
         this.line1 = new TextComponent("An error occurred (" + pException.httpResultCode + "):");
         this.line2 = new TextComponent(pException.httpResponseContent);
      } else {
         this.line1 = new TextComponent("Realms (" + pException.errorCode + "):");
         String s = "mco.errorMessage." + pException.errorCode;
         this.line2 = (Component)(I18n.exists(s) ? new TranslatableComponent(s) : Component.nullToEmpty(pException.errorMsg));
      }

   }

   private void errorMessage(Component pLine2) {
      this.line1 = new TextComponent("An error occurred: ");
      this.line2 = pLine2;
   }

   private void errorMessage(Component pLine1, Component pLine2) {
      this.line1 = pLine1;
      this.line2 = pLine2;
   }

   public void init() {
      this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 52, 200, 20, new TextComponent("Ok"), (p_88686_) -> {
         this.minecraft.setScreen(this.nextScreen);
      }));
   }

   public Component getNarrationMessage() {
      return (new TextComponent("")).append(this.line1).append(": ").append(this.line2);
   }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
       if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
          minecraft.setScreen(this.nextScreen);
          return true;
       }
       return super.keyPressed(key, scanCode, modifiers);
    }

   public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
      this.renderBackground(pPoseStack);
      drawCenteredString(pPoseStack, this.font, this.line1, this.width / 2, 80, 16777215);
      drawCenteredString(pPoseStack, this.font, this.line2, this.width / 2, 100, 16711680);
      super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
   }
}
