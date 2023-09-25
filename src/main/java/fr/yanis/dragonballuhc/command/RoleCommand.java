package fr.yanis.dragonballuhc.command;

import fr.yanis.dragonballuhc.DBUHCMain;
import fr.yanis.dragonballuhc.manager.PlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RoleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player && DBUHCMain.getInstance().getGameManager().isStarted() && DBUHCMain.getInstance().getGameManager().isRoleGiven()) {
            Player player = (Player) sender;
            PlayerManager.getPlayer(player).getRole().getInstance().onUseRoleCommand(args);
            return true;
        } else {
            sender.sendMessage("§cVous ne pouvez pas utiliser cette commande pour le moment !");
        }
        return false;
    }
}
