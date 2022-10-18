package dev.roseplugins.pedrohb.vender.commands;

import dev.roseplugins.pedrohb.vender.RoseVender;
import dev.roseplugins.pedrohb.vender.manager.VendaMenu;
import dev.roseplugins.pedrohb.vender.settings.Settings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VenderCommand implements CommandExecutor {
	private final RoseVender rosevender;

	public VenderCommand(RoseVender rosevender) {
		this.rosevender = rosevender;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lb, String[] args) {
		try {
			if(!sender.hasPermission(Settings.PERMISSION_VENDER.getString())) {
				sender.sendMessage(Settings.Messages.NO_PERMISSION.getMessage());
				return true;
			}

			if(!(sender instanceof Player)) {
				reload(sender);
				return true;
			}

			Player player = (Player) sender;
			if(args.length == 0) {
				VendaMenu.openMenu(player);
				return true;
			}

			String subcmd = args[0];
			if(subcmd.equalsIgnoreCase("reload") || subcmd.equalsIgnoreCase("r")) {
				if(!sender.hasPermission("rosevender.admin")) {
					sender.sendMessage(Settings.Messages.NO_PERMISSION.getMessage());
					return true;
				}
				reload(sender);
				return true;
			}
			sender.sendMessage("§7Sub-comando §cinexistente§7.");
		} catch(Exception e) {
			e.printStackTrace();
			sender.sendMessage("§7Falha ao executar este comando §c:c");
		}
		return true;
	}

	private void reload(CommandSender sender) throws Exception {
		rosevender.config();
		sender.sendMessage(Settings.Messages.CONFIG_RELOADED.getMessage());
	}
}
