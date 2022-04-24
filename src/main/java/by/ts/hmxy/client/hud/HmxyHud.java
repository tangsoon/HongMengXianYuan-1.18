package by.ts.hmxy.client.hud;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import by.ts.hmxy.HmxyMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

public class HmxyHud extends Gui{

	public HmxyHud(Minecraft pMinecraft) {
		super(pMinecraft);
	}

	public static void init() {
		MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, RenderGameOverlayEvent.PreLayer.class, HmxyHud::render);
	}
	
	public static void render(final RenderGameOverlayEvent.PreLayer event) {
		if(!event.isCanceled()) {
			bind(GuiComponent.GUI_ICONS_LOCATION);
	        RenderSystem.defaultBlendFunc();
	        Minecraft.getInstance().getProfiler().push("bossHealth");
	        this.bossOverlay.render(mStack);
	        new HmxyHud(null).bossOverlay.render(null);
	        minecraft.getProfiler().pop();
		}
	}
    private static void bind(ResourceLocation res)
    {
        RenderSystem.setShaderTexture(0, res);
    }
}
