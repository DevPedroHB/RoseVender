package dev.roseplugins.pedrohb.vender.manager;

import dev.roseplugins.pedrohb.vender.rosecore.lite.itemstack.Item;
import dev.roseplugins.pedrohb.vender.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class VendaMenu {
	private enum Type {

		AUTO_VENDA("§7Auto-Venda"), VENDA_SHIFT("§7Venda Shift"), VENDA("§7Vender");

		private final String displayName;

		Type(String displayName) {
			this.displayName = displayName;
		}

		public String getDisplayName() {
			return displayName;
		}

		public boolean getStatus(Player player) {
			switch(this) {
				case AUTO_VENDA:
					return Settings.autoVenda.contains(player.getName());
				case VENDA_SHIFT:
					return Settings.vendaShift.contains(player.getName());
			}
			return false;
		}

	}

	private static ItemStack getItemStack(Player player, Type vendaType) {
		Item itemBuilder = Item.builder()
				.material(vendaType == Type.AUTO_VENDA || (vendaType == Type.VENDA_SHIFT) ? Material.INK_SACK : Material.NAME_TAG)
				.displayName(vendaType.getDisplayName())
				.build();

		if(vendaType != Type.VENDA) {
			itemBuilder.setDurability(vendaType.getStatus(player) ? 10 : 8);
		}
		return itemBuilder.createItem();
	}

	public static void openMenu(Player player) {
		Inventory inventory = Bukkit.createInventory(null, 27, "§8Opções de venda");
		inventory.setItem(11, getItemStack(player, Type.AUTO_VENDA));
		inventory.setItem(13, getItemStack(player, Type.VENDA));
		inventory.setItem(15, getItemStack(player, Type.VENDA_SHIFT));
		player.openInventory(inventory);
	}
}
