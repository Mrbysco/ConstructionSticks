package mrbysco.constructionstick.items.template;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.api.IStickTemplate;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public abstract class ItemUpgradeTemplate extends Item implements IStickTemplate {
	public ItemUpgradeTemplate(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay,
	                            Consumer<Component> tooltips, TooltipFlag flag) {
		tooltips.accept(
				Component.translatable(ConstructionStick.MOD_ID + ".option.upgrades." + getRegistryName().toString() + ".desc")
						.withStyle(ChatFormatting.GRAY)
		);
		tooltips.accept(
				Component.translatable(ConstructionStick.MOD_ID + ".tooltip.upgrades_tip").withStyle(ChatFormatting.AQUA)
		);
	}
}
