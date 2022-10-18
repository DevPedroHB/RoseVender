package dev.roseplugins.pedrohb.vender;

import dev.roseplugins.pedrohb.vender.commands.VenderCommand;
import dev.roseplugins.pedrohb.vender.listeners.BukkitListeners;
import dev.roseplugins.pedrohb.vender.rosecore.lite.logger.ConsoleLogger;
import dev.roseplugins.pedrohb.vender.objects.SellItem;
import dev.roseplugins.pedrohb.vender.settings.Settings;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RoseVender extends JavaPlugin {
	public static Economy economy;
	public static Permission permission;

	@Override
	public void onEnable() {
		ConsoleLogger.init(this);
		String c = "§a";
		ConsoleLogger.info(c+" By: PedroHB_ - V " + getDescription().getVersion() + " RELEASE version");
		ConsoleLogger.info("");
		ConsoleLogger.info(c+"Inicializando tarefas para inicialização...");

		try {
			if(!config()) {
				ConsoleLogger.criticalwarning("POR FAVOR, LEIA ANTES DE REPORTAR COMO UM BUG:");
				ConsoleLogger.criticalwarning("Existe um erro em seu arquivo de configuração. Copie e cole a 'config.yml' e verifique o erro no site: https://onlineyamltools.com/validate-yaml");
				ConsoleLogger.criticalwarning("O RoseVender será desativado automaticamente.");
				Thread.sleep(5000);
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
		} catch(Exception e) {
			e.printStackTrace();
			ConsoleLogger.criticalwarning("POR FAVOR, LEIA ANTES DE REPORTAR COMO UM BUG:");
			ConsoleLogger.criticalwarning("Existe um erro em seu arquivo de configuração. Copie e cole a 'config.yml' e verifique o erro no site: https://onlineyamltools.com/validate-yaml");
			ConsoleLogger.criticalwarning("O RoseVender será desativado automaticamente.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		/**
		 * Register commands
		 */
		getCommand("vender").setExecutor(new VenderCommand(this));
		/**
		 * Register listeners
		 */
		getServer().getPluginManager().registerEvents(new BukkitListeners(this), this);

		/**
		 * Vault hook service
		 */
		if(!setupEconomy()) {
			ConsoleLogger.criticalwarning("Não foi possivel dar hook no Vault. Desligando plugin...");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		ConsoleLogger.info("Hooked with Vault (Economy Plugin: " + economy.getName() + ")");
		ConsoleLogger.info(setupPermissions() ? "Hooked with Vault (Permission Plugin : " + permission.getName() + ")" : "Não foi encontrado nenhum plugin de gestão de permissões. Usando multiplicador por permissões do Bukkit...");
	}

	private boolean setupEconomy() {
		PluginManager pm = Bukkit.getPluginManager();
		if(pm.getPlugin("Vault") != null) {
			RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
		    if(economyProvider != null) {
		    	economy = economyProvider.getProvider();
		    }
		    return economy != null;
		}
		return false;
	}

	private boolean setupPermissions() {
		PluginManager pm = Bukkit.getPluginManager();
		if(pm.getPlugin("Vault") != null) {
			RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
		    if(permissionProvider != null) {
		    	permission = permissionProvider.getProvider();
		    }
		    return permission != null;
		}
		return false;
	}

	public boolean config() throws Exception {
		File configFile = new File(getDataFolder(), "config.yml");
		if(!configFile.exists())  {
			saveDefaultConfig();
		}
		reloadConfig();
		FileConfiguration config = getConfig();

		/**
		 * Load settings
		 */
		Settings.reload(config);

		/**
		 * Loads itens & multiplicadores
		 */
		ConsoleLogger.info("Foram carregados " + loadItens() + " item(s) da config.");
		ConsoleLogger.info("Foram carregados " + loadMultiplicadores() + " multiplicador(es) da config.");
		return config.isSet("Config.debug-mode");
	}
	private int loadItens() {
		FileConfiguration config = getConfig();
		ArrayList<ItemStack> itens = new ArrayList<>();
		for(String itemConfig : config.getConfigurationSection("Itens").getKeys(false)) {
			int id = Integer.parseInt(config.getString("Itens." + itemConfig + ".ID").split(":")[0]);
			int data = Integer.parseInt(config.getString("Itens." + itemConfig + ".ID").split(":")[1]);
			double valor = config.getDouble("Itens." + itemConfig + ".Valor");
			int quantidade = config.getInt("Itens." + itemConfig + ".Quantidade");

			SellItem item = new SellItem(id, data, valor, quantidade);
			@SuppressWarnings("deprecation")
			Material material = Material.getMaterial(item.getId());
			ItemStack itemStack = new ItemStack(material);
			itemStack.setDurability((short) item.getData());
			itens.add(itemStack);
		}
		Settings.loadedItens = itens;
		return itens.size();
	}
	private int loadMultiplicadores() {
		FileConfiguration config = getConfig();
		List<String> multiplicadorCache = new ArrayList<>();
		for(String grupo : config.getConfigurationSection("Multiplicador").getKeys(false)) {
			if(!grupo.equalsIgnoreCase("Tipo")) {
				String grupoPex = config.getString("Multiplicador." + grupo + ".Grupo");
				String permissao = config.getString("Multiplicador." + grupo + ".Permissao");
				String multiplicador = config.getString("Multiplicador." + grupo + ".Multiplicador");
				multiplicadorCache.add(grupoPex + "-" + permissao + "-" + multiplicador.split("%")[0]);
			}
		}
		Settings.multiplicadores = multiplicadorCache;
		return multiplicadorCache.size();
	}
}
