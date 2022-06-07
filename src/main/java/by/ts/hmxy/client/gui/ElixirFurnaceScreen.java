package by.ts.hmxy.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.block.ElixirFurnaceBlock;
import by.ts.hmxy.block.blockentity.TemperatureBE;
import by.ts.hmxy.client.gui.wigdet.HoverWidgetImp;
import by.ts.hmxy.client.gui.wigdet.ProgressBar;
import by.ts.hmxy.client.gui.wigdet.ProgressBar.Direction;
import by.ts.hmxy.client.gui.wigdet.SlotWidget;
import by.ts.hmxy.menu.ElixirFurnaceMenu;
import by.ts.hmxy.util.HmxyHelper;
import by.ts.hmxy.util.TransMsg;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

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
		// 温度条
		this.addRenderableWidget(new ProgressBar(this.x + 20, this.y + 73, 6, 19, TransMsg.PROGRESS_BAR.create(),
				texture, 20, 73, this.x + 21, this.y + 74, 252, 9, 4, 17, Direction.UP) {
			@Override
			public List<Component> getTips() {
				return Arrays.asList(TransMsg.ELIXIR_FURNACE_PROGRESS_TEMPERATURE
						.create(ElixirFurnaceScreen.this.be.getTemperature()));
			}

			@Override
			public float getValue() {
				return Math.min(ElixirFurnaceScreen.this.be.getTemperature() / TemperatureBE.MAX_TEMPERATURE, 1F);
			}
		});

		// 耐久条
		this.addRenderableWidget(new ProgressBar(this.x + 38, this.y + 73, 6, 19, TransMsg.PROGRESS_BAR.create(),
				texture, 38, 73, this.x + 39, this.y + 74, 248, 9, 4, 17, Direction.UP) {
			@Override
			public List<Component> getTips() {
				return Arrays.asList(TransMsg.ELIXIR_FURNACE_PROGRESS_DURATION.create(furnanceData.getRestDuration()));
			}

			@Override
			public float getValue() {
				return Math.min(furnanceData.getRestDuration() / furnanceData.getMaxDuration(), 1.0F);
			}
		});

		// 硬度
		this.addRenderableWidget(new ProgressBar(this.x + 56, this.y + 73, 6, 19, TransMsg.PROGRESS_BAR.create(),
				texture, 56, 73, this.x + 57, this.y + 74, 244, 9, 4, 17, Direction.UP) {
			@Override
			public List<Component> getTips() {
				return Arrays.asList(TransMsg.ELIXIR_FURNACE_PROGRESS_HARDNESS.create(furnanceData.getWallStrength()));
			}

			@Override
			public float getValue() {
				return Math.min(furnanceData.getWallStrength() / ElixirFurnaceBlock.MAX_HARDNESS, 1.0F);
			}
		});

		int offSetX = (9 - this.furnanceData.getElixirInvCount()) / 2;
		for (int i = 0; i < this.furnanceData.getElixirInvCount(); i++) {
			// 灵植
			this.addRenderableOnly(new SlotWidget(this.x + 15 + 18 * (i + offSetX), this.y + 24, 18, 18,
					TransMsg.emptyComponent(), texture, 238, 26, this,
					() -> Arrays.asList(TransMsg.ELIXIR_FURNACE_LING_ZHI_TIP.create())));

			// 提炼进度
			this.addRenderableOnly(new ProgressBar(this.x + 19 + 18 * (i + offSetX), this.y + 43, 8, 10,
					TransMsg.emptyComponent(), texture, 236, 0, this.x + 20 + 18 * (i + offSetX), this.y + 44, 250, 0,
					6, 8, Direction.UP) {

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
			this.addRenderableOnly(
					new SlotWidget(this.x + 15 + 18 * (i + offSetX), this.y + 54, 18, 18, TransMsg.emptyComponent(),
							texture, 238, 26, this, () -> Arrays.asList(TransMsg.ELIXIR_FURNACE_BOTTLE_TIP.create())));
		}
	}

	public void customRender(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		RenderSystem.setShaderTexture(0, this.texture);

		HmxyHelper.drawCenterString(matrixStack, mouseX, mouseY, partialTicks, 0.5F, this.x + 16, this.y + 94, 16, 9,
				font, TransMsg.ELIXIR_FURNACE_TEMPERATURE.create(), 0xffffff, true);
		HmxyHelper.drawCenterString(matrixStack, mouseX, mouseY, partialTicks, 0.5F, this.x + 34, this.y + 94, 16, 9,
				font, TransMsg.ELIXIR_FURNACE_DURATION.create(), 0xffffff, true);
		HmxyHelper.drawCenterString(matrixStack, mouseX, mouseY, partialTicks, 0.5F, this.x + 52, this.y + 94, 16, 9,
				font, TransMsg.ELIXIR_FURNACE_HARDNESS.create(), 0xffffff, true);
	}
}