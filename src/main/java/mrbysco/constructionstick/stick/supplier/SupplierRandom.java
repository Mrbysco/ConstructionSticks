package mrbysco.constructionstick.stick.supplier;

import mrbysco.constructionstick.basics.StickUtil;
import mrbysco.constructionstick.basics.option.StickOptions;
import mrbysco.constructionstick.basics.pool.RandomPool;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

public class SupplierRandom extends SupplierInventory {
	public SupplierRandom(Player player, StickOptions options) {
		super(player, options);
	}

	@Override
	public void getSupply(@Nullable BlockItem target) {
		itemCounts = new LinkedHashMap<>();

		// Random mode -> add all items from hotbar
		itemPool = new RandomPool<>(player.getRandom());

		for (ItemStack stack : StickUtil.getHotbarWithOffhand(player)) {
			if (stack.getItem() instanceof BlockItem) addBlockItem((BlockItem) stack.getItem());
		}
	}
}
