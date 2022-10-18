package dev.roseplugins.pedrohb.vender.manager;

import dev.roseplugins.pedrohb.vender.RoseVender;
import dev.roseplugins.pedrohb.vender.objects.SellItem;
import dev.roseplugins.pedrohb.vender.settings.Settings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class VendaCore {
	private Player player;
	private Type vendaType;
	private RoseVender rosevender;
	@Getter private int quantidade;
	private double ganhos;

	public VendaCore(Player player, Type vendaType, RoseVender rosevender) {
		try {
			this.player = player;
			this.vendaType = vendaType;
			this.rosevender = rosevender;
			if(Settings.RUN_SELL_ASYNC.getBoolean()) {
				rosevender.getServer().getScheduler().runTaskAsynchronously(rosevender, this::performSell);
			} else {
				rosevender.getServer().getScheduler().runTask(rosevender, this::performSell);
			}
		} catch(Exception e) {
			e.printStackTrace();
			player.sendMessage("§cUm erro desconhecido ocorreu...");
		}
	}

	public void performSell() {
		if(checkInventoryIsEmpty()) {
			if(vendaType == Type.VENDA_NORMAL) player.sendMessage(Settings.Messages.INVENTORY_EMPTY.getMessage());
			return;
		}
		if(Settings.loadedItens == null || Settings.loadedItens.size() == 0) {
			player.sendMessage("§cAinda não existem itens configurados para serem vendidos.");
			return;
		}
		for(ItemStack item : getItensInInventory()) {
			int amount = item.getAmount();
			for(ItemStack itemStackConfig : Settings.loadedItens) {
				if(item.getType() == itemStackConfig.getType() && (item.getDurability() == itemStackConfig.getDurability())) {
					SellItem rosevenderitem = SellItem.valueOf(rosevender, item);
					double itemValue = rosevenderitem.getPrice();
					double dinheiroAdicionado = itemValue * amount;
					dinheiroAdicionado = dinheiroAdicionado/rosevenderitem.getQuantidade();
					quantidade = quantidade + amount;
					ganhos = ganhos + processMultiplicador(dinheiroAdicionado);
					player.updateInventory();
					RoseVender.economy.depositPlayer(player, processMultiplicador(dinheiroAdicionado));
				}
			}
		}
		for(ItemStack item : getItensInInventory()) {
			for(ItemStack itemStack : Settings.loadedItens) {
				if(item.getType() == itemStack.getType() && (item.getDurability() == itemStack.getDurability())) {
					for(int i = 0; i < quantidade; i++) {
						player.getInventory().removeItem(item);
					}
					player.updateInventory();
				}
			}
		}
		if(quantidade == 0) {
			if(vendaType == Type.VENDA_NORMAL) {
				player.sendMessage(Settings.Messages.NO_ITENS.getMessage());
			}
			return;
		}
		player.sendMessage(Settings.Messages.VENDIDO.getMessage().replace("%itens%", String.valueOf(quantidade)).replace("%dinheiro%", String.valueOf(ganhos)));
	}

	private boolean checkInventoryIsEmpty() {
		PlayerInventory inv = player.getInventory();
		for(ItemStack i : inv.getContents()) {
			if(i != null && !(i.getType() == Material.AIR)) {
				return false;
			}
		}
		return true;
	}

	private double processMultiplicador(double valorOriginal) {
		switch(Settings.getMultiplicadorType()) {

			case DETECCAO_GRUPO:
				for(String multiplicador : Settings.multiplicadores) {
					boolean inGroup = RoseVender.permission.playerInGroup(player, multiplicador.split("-")[0]);
					if(inGroup) {
						double calc = Double.parseDouble(multiplicador.split("-")[2])/100;
						return valorOriginal*calc;
					}
				}
				break;
			case DETECCAO_PERMISSAO:
				for(String multiplicador : Settings.multiplicadores) {
					boolean hasperm = player.hasPermission(multiplicador.split("-")[1]);
					if(hasperm) {
						double calc = Double.parseDouble(multiplicador.split("-")[2])/100;
						return valorOriginal*calc;
					}
				}
				break;
		}
		return valorOriginal;
	}

	private ArrayList<ItemStack> getItensInInventory() {
		ItemStack[] contents = player.getInventory().getContents();
		ArrayList<ItemStack> itens = new ArrayList<>();
		for(ItemStack item : contents) {
			if(item != null) {
				itens.add(item);
			}
		}
		return itens;
	}

	public String getGanhos() {
		return new DecimalFormat("#,##0").format(ganhos);
	}

	@AllArgsConstructor @Getter
	public enum Type {
		AUTO_VENDA, VENDA_SHIFT, VENDA_NORMAL
	}
	@AllArgsConstructor @Getter
	public enum MultiplicadorType {
		DETECCAO_GRUPO("Detecção por Grupo", "grupo"), DETECCAO_PERMISSAO("Detecção por Permissão", "permissao"), NENHUM("Nenhum", "nenhum");

		private final String nome;
		private final String configNome;

		public static MultiplicadorType getByConfig(String config) {
			if(config == null || RoseVender.permission == null) return DETECCAO_PERMISSAO;

			for(MultiplicadorType multiplicadorMethodType : MultiplicadorType.values()) {
				if(multiplicadorMethodType.configNome.equals(config.toLowerCase())) return multiplicadorMethodType;
			}
			return RoseVender.permission != null && RoseVender.permission.isEnabled() ?  MultiplicadorType.DETECCAO_GRUPO :  MultiplicadorType.DETECCAO_PERMISSAO;
		}
	}
}
