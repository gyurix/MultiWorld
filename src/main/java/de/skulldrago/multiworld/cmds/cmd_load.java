package de.skulldrago.multiworld.cmds;

import de.skulldrago.multiworld.main.Multiworld;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class cmd_load implements CommandExecutor {
  File lang = new File("plugins/MultiWorld", "lang_de.yml");
  YamlConfiguration cfg2 = YamlConfiguration.loadConfiguration(lang);
  Multiworld service = Multiworld.getPlugin();
  String prefix = service.getPrefix();

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (args.length == 1) {
      if (sender instanceof Player) {
        Player p = (Player) sender;
        if (p.hasPermission("Multiworld.load")) {
          Bukkit.getServer().createWorld(new WorldCreator(args[0]));
          if (cfg2.contains("Commands.Load.Finish")) {
            String msg = cfg2.getString("Commands.Load.Finish");
            msg = msg.replaceAll("&", "§");
            msg = msg.replaceAll("%prefix%", "" + prefix + "");
            msg = msg.replaceAll("%world%", "" + args[0] + "");
            p.sendMessage(msg);
          } else {
            p.sendMessage(prefix + " §2Du hast die Welt §6" + args[0] + " §2erfolgreich geladen.");
          }
        } else {
          if (cfg2.contains("System.NoPermission")) {
            String msg = cfg2.getString("System.NoPermission");
            msg = msg.replaceAll("&", "§");
            msg = msg.replaceAll("%prefix%", "" + prefix + "");
            p.sendMessage(msg);

          } else {
            p.sendMessage(prefix + " §cDu hast nicht die Permissions um diesen Befehl zu benutzen.");
          }
        }
      } else {
        if (cfg2.contains("System.OnlyPlayers")) {
          String msg = cfg2.getString("System.OnlyPlayers");
          msg = msg.replaceAll("&", "§");
          msg = msg.replaceAll("%prefix%", "" + prefix + "");
          sender.sendMessage(msg);

        } else {
          sender.sendMessage(prefix + " §cNur Spieler duerfen diesen Befehl benutzen!");
        }
      }
    } else {
      if (cfg2.contains("Commands.Load.WrongSyntax")) {
        String msg = cfg2.getString("Commands.Load.WrongSyntax");
        msg = msg.replaceAll("&", "§");
        msg = msg.replaceAll("%prefix%", "" + prefix + "");
        sender.sendMessage(msg);
      } else {
        sender.sendMessage(prefix + " §cFalsche Syntax. Bitte benutze /load <Weltname>");
      }
    }
    return true;
  }
}
