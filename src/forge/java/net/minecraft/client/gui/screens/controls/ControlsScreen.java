package net.minecraft.client.gui.screens.controls;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.MouseSettingsScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ControlsScreen extends OptionsSubScreen {
   /** The ID of the button that has been pressed. */
   public KeyMapping selectedKey;
   public long lastKeySelection;
   private ControlList controlList;
   private Button resetButton;

   public ControlsScreen(Screen pLastScreen, Options pOptions) {
      super(pLastScreen, pOptions, new TranslatableComponent("controls.title"));
   }

   protected void init() {
      this.addRenderableWidget(new Button(this.width / 2 - 155, 18, 150, 20, new TranslatableComponent("options.mouse_settings"), (p_97540_) -> {
         this.minecraft.setScreen(new MouseSettingsScreen(this, this.options));
      }));
      this.addRenderableWidget(Option.AUTO_JUMP.createButton(this.options, this.width / 2 - 155 + 160, 18, 150));
      this.controlList = new ControlList(this, this.minecraft);
      this.addWidget(this.controlList);
      this.resetButton = this.addRenderableWidget(new Button(this.width / 2 - 155, this.height - 29, 150, 20, new TranslatableComponent("controls.resetAll"), (p_97538_) -> {
         for(KeyMapping keymapping : this.options.keyMappings) {
            keymapping.setToDefault();
         }

         KeyMapping.resetMapping();
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 155 + 160, this.height - 29, 150, 20, CommonComponents.GUI_DONE, (p_97535_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
      if (this.selectedKey != null) {
         this.options.setKey(this.selectedKey, InputConstants.Type.MOUSE.getOrCreate(pButton));
         this.selectedKey = null;
         KeyMapping.resetMapping();
         return true;
      } else {
         return super.mouseClicked(pMouseX, pMouseY, pButton);
      }
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      if (this.selectedKey != null) {
         if (pKeyCode == 256) {
            this.selectedKey.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), InputConstants.UNKNOWN);
            this.options.setKey(this.selectedKey, InputConstants.UNKNOWN);
         } else {
            this.selectedKey.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), InputConstants.getKey(pKeyCode, pScanCode));
            this.options.setKey(this.selectedKey, InputConstants.getKey(pKeyCode, pScanCode));
         }

         if (!net.minecraftforge.client.settings.KeyModifier.isKeyCodeModifier(this.selectedKey.getKey()))
         this.selectedKey = null;
         this.lastKeySelection = Util.getMillis();
         KeyMapping.resetMapping();
         return true;
      } else {
         return super.keyPressed(pKeyCode, pScanCode, pModifiers);
      }
   }

   public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
      this.renderBackground(pPoseStack);
      this.controlList.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
      drawCenteredString(pPoseStack, this.font, this.title, this.width / 2, 8, 16777215);
      boolean flag = false;

      for(KeyMapping keymapping : this.options.keyMappings) {
         if (!keymapping.isDefault()) {
            flag = true;
            break;
         }
      }

      this.resetButton.active = flag;
      super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
   }
}
