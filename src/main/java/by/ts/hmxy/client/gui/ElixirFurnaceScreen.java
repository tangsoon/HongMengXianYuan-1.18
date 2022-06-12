package by.ts.hmxy.client.gui;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import com.mojang.blaze3d.vertex.PoseStack;
import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.block.ElixirFurnaceBlock;
import by.ts.hmxy.client.gui.wigdet.GridProgressBar;
import by.ts.hmxy.client.gui.wigdet.HoverdSlot;
import by.ts.hmxy.client.gui.wigdet.GridWidget;
import by.ts.hmxy.client.gui.wigdet.PacketButton;
import by.ts.hmxy.client.gui.wigdet.ProgressBar;
import by.ts.hmxy.client.gui.wigdet.ProgressBar.Direction;
import by.ts.hmxy.menu.ElixirFurnaceMenu;
import by.ts.hmxy.net.ButtonPacket;
import by.ts.hmxy.util.TransMsg;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ElixirFurnaceScreen extends GridScreen<ElixirFurnaceMenu> {

	private ElixirFurnaceBlock.ElixirFurnaceBE be;
	private ElixirFurnaceBlock.Data furnanceData;

	public static final ResourceLocation FURNACE_TEXTURE = HmxyMod.modLoc("textures/gui/elixir_furnace.png");

	public ElixirFurnaceScreen(ElixirFurnaceMenu menu, Inventory inv, Component name) {
		super(menu, inv, name);
		this.imageWidth = 193;
		this.imageHeight = 180;
		this.be = this.getMenu().getBe();
		this.furnanceData = be.getData();
	}

	protected GridWidget createGridWidget() {
		return super.createGridWidget().setWidth1(11).setWidth2(170).setWidth3(11).setHeight1(10).setHeight2(174)
				.setHeight3(2).setMaxWidth2(233).setMaxHeight2(244).setColor(0xffffff).refreshImageSize();
	}

	protected void init() {
		super.init();
		int tempX = this.leftPos;
		// 温度条
		this.addRenderableOnly(new FurnaceProgressBar(tempX = tempX + 18, this.topPos + 79,
				TransMsg.PROGRESS_BAR_TIP, () -> Float.valueOf(0.5F),0xffcccc));

		// 耐久条		
		this.addRenderableOnly(new FurnaceProgressBar(tempX = tempX + 8, this.topPos + 79,
				TransMsg.PROGRESS_BAR_TIP, () -> Float.valueOf(0.5F),0xccf9ff));

		// 硬度
		this.addRenderableOnly(new FurnaceProgressBar(tempX = tempX + 8, this.topPos + 79,
				TransMsg.PROGRESS_BAR_TIP, () -> Float.valueOf(0.5F),0xccd4ff));
		// 炉盖
		this.addRenderableOnly(new HoverdSlot(tempX += 6 + 4, this.topPos + 79, this,
				() -> Arrays.asList(TransMsg.ELIXIR_FURNACE_COVER.get())));

		// 配方
		this.addRenderableOnly(new HoverdSlot(tempX += 18 + 4, this.topPos + 79, this,
				() -> Arrays.asList(TransMsg.ELIXIR_FURNACE_RECIPE.get())));
		this.addRenderableWidget(new PacketButton(tempX += 18 + 4, this.topPos + 79, 35, 18, 221, 45, FURNACE_TEXTURE,
				ButtonPacket.NING_DAN, () -> null, TransMsg.ELIXIR_FURNACE_NING_DAN.get()));
		// 丹药
		this.addRenderableOnly(new HoverdSlot(tempX += 35 + 4, this.topPos + 79, this,
				() -> Arrays.asList(TransMsg.ELIXIR_FURNACE_RECIPE.get())));

		int offSetX = (9 - this.furnanceData.getElixirInvCount()) / 2;
		for (int i = 0; i < this.furnanceData.getElixirInvCount(); i++) {
			// 灵植
			this.addRenderableOnly(new HoverdSlot(this.leftPos + 15 + 18 * (i + offSetX), this.topPos + 24, this,
					() -> Arrays.asList(TransMsg.ELIXIR_FURNACE_LING_ZHI_TIP.get())));

			// 提炼进度
			this.addRenderableOnly(new ProgressBar(this.leftPos + 19 + 18 * (i + offSetX), this.topPos + 43, 8, 10,
					TransMsg.EMPTY, FURNACE_TEXTURE, 236, 0, this.leftPos + 20 + 18 * (i + offSetX), this.topPos + 44,
					250, 0, 6, 8, Direction.UP) {

				@Override
				public List<Component> getTips() {
					return Arrays.asList(TransMsg.ELIXIR_FURNACE_EXTRACT_PROGRESS.get(1.0F));
				}

				@Override
				public float getValue() {
					// TODO 完善value
					return 0.5f;
				}
			});

			// 药罐
			this.addRenderableOnly(new HoverdSlot(this.leftPos + 15 + 18 * (i + offSetX), this.topPos + 54, this,
					() -> Arrays.asList(TransMsg.ELIXIR_FURNACE_BOTTLE_TIP.get())));
		}
	}

	public void customRender(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {

	}

	private class FurnaceProgressBar extends GridProgressBar {
		public FurnaceProgressBar(int pX, int pY, TransMsg msg,
				Supplier<Float> value,int barColor) {
			super(pX, pY, 6, 18, msg, value, Direction.UP);
			this.background.setColor(0xf4eade);
			this.bar.setColor(barColor);
		}
	}
}