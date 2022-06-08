package by.ts.hmxy.client.gui;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import com.mojang.blaze3d.vertex.PoseStack;
import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.block.ElixirFurnaceBlock;
import by.ts.hmxy.block.blockentity.TemperatureBE;
import by.ts.hmxy.client.gui.wigdet.PacketButton;
import by.ts.hmxy.client.gui.wigdet.ProgressBar;
import by.ts.hmxy.client.gui.wigdet.ProgressBar.Direction;
import by.ts.hmxy.client.gui.wigdet.SlotWidget;
import by.ts.hmxy.menu.ElixirFurnaceMenu;
import by.ts.hmxy.net.ButtonPacket;
import by.ts.hmxy.util.TransMsg;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ElixirFurnaceScreen extends BaseSreen<ElixirFurnaceMenu> {

	private ElixirFurnaceBlock.ElixirFurnaceBE be;
	private ElixirFurnaceBlock.Data furnanceData;

	public ElixirFurnaceScreen(ElixirFurnaceMenu menu, Inventory inv, Component name) {
		super(menu, inv, name, new ResourceLocation(HmxyMod.MOD_ID, "textures/gui/elixir_furnace.png"));
		this.imageWidth = 193;
		this.imageHeight = 180;
		this.be = this.getMenu().getBe();
		this.furnanceData = be.getData();
	}

	protected void init() {
		super.init();
		int tempX = this.x;
		// 温度条
		this.addRenderableOnly(new FurnaceProgressBar(tempX = tempX + 18, this.y + 79, 252, 9,
				() -> Arrays.asList(TransMsg.ELIXIR_FURNACE_PROGRESS_TEMPERATURE
						.create(ElixirFurnaceScreen.this.be.getTemperature())),
				() -> Math.min(ElixirFurnaceScreen.this.be.getTemperature() / TemperatureBE.MAX_TEMPERATURE, 1F)));

		// 耐久条

		this.addRenderableOnly(new FurnaceProgressBar(tempX += 8, this.y + 79, 248, 9,
				() -> Arrays.asList(
						TransMsg.ELIXIR_FURNACE_PROGRESS_DURATION.create(ElixirFurnaceScreen.this.be.getTemperature())),
				() -> Math.min(furnanceData.getRestDuration() / furnanceData.getMaxDuration(), 1.0F)));

		// 硬度

		this.addRenderableOnly(new FurnaceProgressBar(tempX += 8, this.y + 79, 244, 9,
				() -> Arrays.asList(
						TransMsg.ELIXIR_FURNACE_PROGRESS_HARDNESS.create(ElixirFurnaceScreen.this.be.getTemperature())),
				() -> Math.min(furnanceData.getWallStrength() / ElixirFurnaceBlock.MAX_HARDNESS, 1.0F)));

		// 炉盖
		this.addRenderableOnly(new SlotWidget(tempX += 6 + 4, this.y + 79, this,
				() -> Arrays.asList(TransMsg.ELIXIR_FURNACE_COVER.create())));
		// 配方
		this.addRenderableOnly(new SlotWidget(tempX += 18 + 4, this.y + 79, this,
				() -> Arrays.asList(TransMsg.ELIXIR_FURNACE_RECIPE.create())));
		// 按钮
		// this.addRenderableWidget(new PacketButton(tempX, tempX, tempX, tempX, tempX,
		// tempX, tempX, BACKGROUND_LOCATION, tempX, tempX, null));
		this.addRenderableWidget(new PacketButton(tempX += 18 + 4, this.y + 79, 35, 18, 221, 45, this.texture,
				ButtonPacket.NING_DAN, null, title));

		int offSetX = (9 - this.furnanceData.getElixirInvCount()) / 2;
		for (int i = 0; i < this.furnanceData.getElixirInvCount(); i++) {
			// 灵植
			this.addRenderableOnly(new SlotWidget(this.x + 15 + 18 * (i + offSetX), this.y + 24, this,
					() -> Arrays.asList(TransMsg.ELIXIR_FURNACE_LING_ZHI_TIP.create())));

			// 提炼进度
			this.addRenderableOnly(new ProgressBar(this.x + 19 + 18 * (i + offSetX), this.y + 43, 8, 10, TransMsg.EMPTY,
					texture, 236, 0, this.x + 20 + 18 * (i + offSetX), this.y + 44, 250, 0, 6, 8, Direction.UP) {

				@Override
				public List<Component> getTips() {
					return Arrays.asList(TransMsg.ELIXIR_FURNACE_EXTRACT_PROGRESS.create(1.0F));
				}

				@Override
				public float getValue() {
					// TODO 完善value
					return 0.5f;
				}
			});

			// 药罐
			this.addRenderableOnly(new SlotWidget(this.x + 15 + 18 * (i + offSetX), this.y + 54, this,
					() -> Arrays.asList(TransMsg.ELIXIR_FURNACE_BOTTLE_TIP.create())));
		}

	}

	public void customRender(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {

	}

	private class FurnaceProgressBar extends ProgressBar {

		private Supplier<List<Component>> components;
		private Supplier<Float> valueSupplier;

		public FurnaceProgressBar(int absX, int absY, int barU, int barV, Supplier<List<Component>> components,
				Supplier<Float> valueSupplier) {
			super(absX, absY, 6, 18, TransMsg.EMPTY, ElixirFurnaceScreen.this.texture, 230, 0, absX + 1, absY + 1, barU,
					barV, 4, 16, Direction.UP);
			this.components = components;
			this.valueSupplier = valueSupplier;
		}

		@Override
		public List<Component> getTips() {
			return components.get();
		}

		@Override
		public float getValue() {
			return valueSupplier.get();
		}
	}
}