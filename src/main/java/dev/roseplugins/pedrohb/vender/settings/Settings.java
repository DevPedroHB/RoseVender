package dev.roseplugins.pedrohb.vender.settings;

import dev.roseplugins.pedrohb.vender.manager.VendaCore;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;

public enum Settings {
    PERMISSION_SHIFT("permissao-shift", "rosevender.shift"),
	PERMISSION_AUTOMATICO("permissao-automatico", "rosevender.automatico"),
	PERMISSION_VENDER("permissao-vender", "rosevender.usar"),
    MULTIPLICADOR_TYPE("Multiplicador.Tipo", "grupo"),
    RUN_SELL_ASYNC("Config.run-sell-async", true),
    ;

    Settings(String key, Object defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    private static VendaCore.MultiplicadorType multiplicadorType;
    public static ArrayList<String> vendaShift = new ArrayList<>();
    public static ArrayList<String> autoVenda = new ArrayList<>();
    public static List<ItemStack> loadedItens = new ArrayList<>();
    public static List<String> multiplicadores = new ArrayList<>();
    private final String key;
    private final Object defaultValue;
    private static FileConfiguration config;

    public String getString() {
        return config.getString(key, (String) defaultValue);
    }

    public boolean getBoolean() {
        return config.getBoolean(key, (Boolean) defaultValue);
    }

    public static void reload(FileConfiguration config) throws Exception {
        Settings.config = config;
        multiplicadorType = VendaCore.MultiplicadorType.getByConfig(Settings.MULTIPLICADOR_TYPE.getString().toLowerCase());
    }

    public static VendaCore.MultiplicadorType getMultiplicadorType() {
        return multiplicadorType;
    }

    public enum Messages {
        NO_PERMISSION("Mensagens.Sem-Permissao", "§7Você não tem §cpermissão §7para executar este comando."),
        CONFIG_RELOADED("Mensagens.Configuracao-Recarregada", "§cConfiguração §7e §carquivos §7de linguagem foram §crecarregados§7."),
        INVALID_CAPTCHA("Mensagens.Captcha-Invalido", "§7O captcha inserido é §cinválido§7."),
        VENDIDO("Mensagens.Vendido-Sucesso","§7Você vendeu §c%itens% §7itens por §c$%dinheiro%§7."),
        NO_ITENS("Mensagens.Sem-Itens", "§7Você não possui §citens §7para serem §cvendidos§7."),
        INVENTORY_EMPTY("Mensagens.Inventario-Vazio", "§7Você não pode estar com um §cinventário vazio§7."),
        ;

        private final String key;
        private final String defaultValue;

        Messages(String key, String defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public String getMessage() {
            return ChatColor.translateAlternateColorCodes('&', config.getString(key, defaultValue));
        }
    }
}
