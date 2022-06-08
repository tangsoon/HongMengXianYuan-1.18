package by.ts.hmxy.client.gui;

import java.util.Arrays;
import java.util.List;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.block.ElixirFurnaceRootBlock.ElixirFurnaceRootBE;
import by.ts.hmxy.client.gui.wigdet.PacketSlider;
import by.ts.hmxy.menu.ElixirFurnaceRootMenu;
import by.ts.hmxy.net.SliderPacket;
import by.ts.hmxy.util.TransMsg;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ElixirFurnaceRootScreen extends BaseSreen<ElixirFurnaceRootMenu> {

	ElixirFurnaceRootBE be;

	public ElixirFurnaceRootScreen(ElixirFurnaceRootMenu menu, Inventory inv, Component name) {
		super(menu, inv, name, new ResourceLocation(HmxyMod.MOD_ID, "textures/gui/elixir_furnace_root.png"));
		this.imageWidth = 193;
		this.imageHeight = 180;
		be = this.getMenu().getBe();
	}

	protected void init() {
		super.init();
		this.addRenderableWidget(new PacketSlider(this.x + 65, this.y + 61, 100, 8, TransMsg.SLIDER_LING_QI_CONSUME,
				be.getValve(), be.getMaxLingQiConsume(), this.texture,SliderPacket.LING_QI_VALVE) {
			@Override
			public List<Component> getTips() {
				return Arrays.asList(TransMsg.DEFAULT.create());
			}
		});
	}
	
	public void customRender(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		RenderSystem.setShaderTexture(0, this.texture);
		this.blit(matrixStack, this.x+66, this.y+34, 0, 246, (int)(96*this.be.getLingQi()/ElixirFurnaceRootBE.MAX_LING_QI_CAPACITY), 10);
	}
}