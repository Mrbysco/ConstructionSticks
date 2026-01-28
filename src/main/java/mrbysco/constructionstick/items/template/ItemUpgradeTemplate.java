package mrbysco.constructionstick.items.template;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.api.IStickTemplate;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ItemUpgradeTemplate extends Item implements IStickTemplate {
	public ItemUpgradeTemplate(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
		tooltipComponents.add(
				Component.translatable(ConstructionStick.MOD_ID + ".option.upgrades." + getRegistryName().toString() + ".desc")
						.withStyle(ChatFormatting.GRAY)
		);
		tooltipComponents.add(
				Component.translatable(ConstructionStick.MOD_ID + ".tooltip.upgrades_tip").withStyle(ChatFormatting.AQUA)
		);
	}
}
