package by.ts.hmxy.client.gui.wigdet;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import com.mojang.blaze3d.vertex.PoseStack;
import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.util.TransMsg;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

/**
 * 进度条
 * 
 * @author tangsoon
 *
 */
public class GridProgressBar extends AbstractWidget implements HoveredWidget {

	protected Supplier<List<Component>> tips;
	protected Supplier<Float> value;
	private Direction dir;
	protected GridWidget background;
	protected GridWidget bar;
	
	/**传入一个TransMsg 而不是一个Supplier<List<Component>>*/
	public GridProgressBar(int pX, int pY, int pWidth, int pHeight,TransMsg msg,
			Supplier<Float> value, Direction dir) {
		this(pX, pY, pWidth, pHeight, ()->{
			return Arrays.asList(msg.get(value.get()));
		}, value, dir);
	}
	
	public GridProgressBar(int pX, int pY, int pWidth, int pHeight, Supplier<List<Component>> tips,
			Supplier<Float> value, Direction dir) {
		super(pX, pY, pWidth, pHeight, TransMsg.EMPTY);
		this.tips = tips;
		this.value = value;
		this.dir = dir;
		this.createGridWidget();
	}

	public static final ResourceLocation GRID_PROGRESS_BAR_BACKGROUND = HmxyMod
			.modLoc("textures/gui/grid_progress_bar_background.png");

	public static final ResourceLocation GRID_PROGRESS_BAR = HmxyMod.modLoc("textures/gui/grid_progress_bar.png");

	@Override
	public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
		pNarrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
		if (this.active) {
			if (this.isFocused()) {
				pNarrationElementOutput.add(NarratedElementType.USAGE,
						new TranslatableComponent("narration.checkbox.usage.focused"));
			} else {
				pNarrationElementOutput.add(NarratedElementType.USAGE,
						new TranslatableComponent("narration.checkbox.usage.hovered"));
			}
		}
	}

	public int getBarHeight() {
		return (int) (Math.ceil((value.get()) * (this.height - 2)));
	}

	public int getBarWidth() {
		return (int) (Math.ceil((value.get()) * (this.width - 2)));
	}

	public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {

		this.background.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
		if (dir == Direction.UP || dir == Direction.DOWN) {
			int barHeight = this.getBarHeight();
			bar.setHeight1(0).setHeight2(0).setHeight3(0);
			if (barHeight >= 1)
				bar.setHeight1(1);
			if (barHeight >= 2)
				bar.setHeight3(1);
			if (barHeight > 2)
				bar.setHeight2(barHeight - 2);
		} else if (dir == Direction.RIGHT || dir == Direction.LEFT) {
			int barWidth = this.getBarWidth();
			bar.setWidth1(0).setWidth2(0).setWidth3(0);
			if (barWidth >= 1)
				bar.setWidth1(1);
			if (barWidth >= 2)
				bar.setWidth3(1);
			if (barWidth > 2)
				bar.setWidth2(barWidth - 2);
		}
		this.bar.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
	}

	public float getValue() {
		return value.get();
	};

	/**
	 * 在这里设置{@link GridWidget}
	 */
	protected void createGridWidget() {
		
		this.background = new GridWidget(GRID_PROGRESS_BAR_BACKGROUND, () -> this.x, () -> this.y, this).setWidth1(2)
				.setWidth3(2).setWidth2(this.width - 4).setHeight1(2).setHeight3(2).setHeight2(this.height - 4)
				.setMaxHeight2(252).setMaxWidth2(252)
				.refreshImageSize();
		this.bar = new GridWidget(GRID_PROGRESS_BAR, () -> {
			if (this.dir == Direction.LEFT) {
				return this.x + 1 + this.width - 2 - this.getBarWidth();
			}
			return this.x + 1;
		}, () -> {
			if (this.dir == Direction.UP) {
				return this.y + 1 + this.height - 2 - this.getBarHeight();
			}
				return this.y + 1;

		}, this).setWidth1(1).setWidth3(1).setWidth2(this.width - 4).setHeight1(1).setHeight3(1)
				.setHeight2(this.height - 4).setMaxWidth2(254).setMaxHeight2(254)
				.refreshImageSize();
	};

	public static enum Direction {
		UP, DOWN, LEFT, RIGHT
	}

	@Override
	public List<Component> getTips() {
		return tips.get();
	}
}
