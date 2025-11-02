package mrbysco.constructionstick.client;

import mrbysco.constructionstick.basics.option.IOption;
import mrbysco.constructionstick.basics.option.StickOptions;
import mrbysco.constructionstick.network.PacketStickOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.NotNull;

public class StickScreen extends Screen {
	private final ItemStack stick;
	private final StickOptions stickOptions;

	private static final int BUTTON_WIDTH = 160;
	private static final int BUTTON_HEIGHT = 20;
	private static final int SPACING_WIDTH = 50;
	private static final int SPACING_HEIGHT = 30;
	private static final int N_COLS = 2;
	private static final int N_ROWS = 3;

	private static final int FIELD_WIDTH = N_COLS * (BUTTON_WIDTH + SPACING_WIDTH) - SPACING_WIDTH;
	private static final int FIELD_HEIGHT = N_ROWS * (BUTTON_HEIGHT + SPACING_HEIGHT) - SPACING_HEIGHT;

	public StickScreen(ItemStack stick) {
		super(Component.literal("StickScreen"));
		this.stick = stick;
		stickOptions = new StickOptions(stick);
	}

	@Override
	protected void init() {
		createButton(0, 0, stickOptions.upgrades);
		createButton(0, 1, stickOptions.lock);
		createButton(0, 2, stickOptions.direction);
		createButton(1, 0, stickOptions.replace);
		createButton(1, 1, stickOptions.match);
		createButton(1, 2, stickOptions.random);
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		guiGraphics.drawCenteredString(font, stick.getDisplayName(), width / 2, height / 2 - FIELD_HEIGHT / 2 - SPACING_HEIGHT, 16777215);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (Minecraft.getInstance().options.keyInventory.matches(event)) {
			this.onClose();
			return true;
		} else {
			return super.keyPressed(event);
		}
	}

	private void createButton(int cx, int cy, IOption<?> option) {
		Button button = Button.builder(getButtonLabel(option), bt -> clickButton(bt, option))
				.pos(getX(cx), getY(cy))
				.size(BUTTON_WIDTH, BUTTON_HEIGHT)
				.tooltip(getButtonTooltip(option))
				.build();

		button.active = option.isEnabled();
		addRenderableWidget(button);
	}

	private void clickButton(Button button, IOption<?> option) {
		option.next();
		ClientPacketDistributor.sendToServer(new PacketStickOption(option, false));
		button.setMessage(getButtonLabel(option));
		button.setTooltip(getButtonTooltip(option));
	}

	private int getX(int n) {
		return width / 2 - FIELD_WIDTH / 2 + n * (BUTTON_WIDTH + SPACING_WIDTH);
	}

	private int getY(int n) {
		return height / 2 - FIELD_HEIGHT / 2 + n * (BUTTON_HEIGHT + SPACING_HEIGHT);
	}

	private Component getButtonLabel(IOption<?> option) {
		return Component.translatable(option.getKeyTranslation()).append(Component.translatable(option.getValueTranslation()));
	}

	private Tooltip getButtonTooltip(IOption<?> option) {
		return Tooltip.create(Component.translatable(option.getDescTranslation()));
	}
}
