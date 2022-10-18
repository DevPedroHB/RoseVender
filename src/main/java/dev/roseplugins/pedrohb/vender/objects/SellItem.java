package dev.roseplugins.pedrohb.vender.objects;

import dev.roseplugins.pedrohb.vender.RoseVender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor @Getter
public class SellItem {
	private int id;
	private int data;
	private double price = 1.0;
	private int quantidade = 1;

	@SuppressWarnings("deprecation")
	public SellItem(RoseVender rosevender, ItemStack itemStack) {
		for(String item : rosevender.getConfig().getConfigurationSection("Itens").getKeys(false)) {
			this.id = Integer.parseInt(rosevender.getConfig().getString("Itens." + item + ".ID").split(":")[0]);
			this.data = Integer.parseInt(rosevender.getConfig().getString("Itens." + item + ".ID").split(":")[1]);
			if(id == itemStack.getType().getId() && data == itemStack.getDurability()) {
				this.price = rosevender.getConfig().getDouble("Itens." + item + ".Valor");
				this.quantidade = rosevender.getConfig().getInt("Itens." + item + ".Quantidade");
			}
		}
	}

	public static SellItem valueOf(RoseVender rosevender, ItemStack item) {
		return new SellItem(rosevender, item);
	}

	@SuppressWarnings("deprecation")
	public Material getMaterial() {
		return Material.getMaterial(id);
	}
}
