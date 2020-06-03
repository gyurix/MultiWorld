package de.skulldrago.multiworld.cmds;

import de.skulldrago.multiworld.main.Multiworld;
import de.skulldrago.multiworld.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class cmd_tpahere implements CommandExecutor {
  File lang = new File("plugins/MultiWorld", "lang_de.yml");
  YamlConfiguration cfg2 = YamlConfiguration.loadConfiguration(lang);
  Multiworld service = Multiworld.getPlugin();
  String prefix = service.getPrefix();
  MySQL sql = Multiworld.getPlugin().getMysql();

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (sender instanceof Player) {
      Player p = (Player) sender;

      if (p.hasPermission("Multiworld.tpahere")) {
        if (args.length == 1) {
          Player target = Bukkit.getPlayer(args[0]);
          if (target != null) {
            if (cfg2.contains("Commands.Tpahere.Message")) {
              String msg = cfg2.getString("Commands.Tpahere.Message");
              msg = msg.replaceAll("&", "§");
              msg = msg.replaceAll("%prefix%", "" + prefix + "");
              msg = msg.replaceAll("%player%", "" + p.getName() + "");
              target.sendMessage(msg);
            } else {
              target.sendMessage("§c" + p.getName() + " hat dir eine Teleportanfrage geschickt. Um zu akzeptieren benutze /tpahereaccept. Um ab zu lehnen benutze /tpaheredeny.");
            }
            if (cfg2.contains("Commands.Tpahere.MessagePlayer")) {
              String msg = cfg2.getString("Commands.Tpahere.MessagePlayer");
              msg = msg.replaceAll("&", "§");
              msg = msg.replaceAll("%prefix%", "" + prefix + "");
              p.sendMessage(msg);
            } else {
              p.sendMessage("§cTeleportanfrage gesendet.");
            }

            sql.queryUpdate("INSERT INTO tphererequests (requestname, name) VALUES ('" + p.getName() + "', '" + target.getName() + "')");

          } else {
            if (cfg2.contains("Commands.Tpahere.NotOnline")) {
              String msg = cfg2.getString("Commands.Tpahere.NotOnline");
              msg = msg.replaceAll("&", "§");
              msg = msg.replaceAll("%prefix%", "" + prefix + "");
              p.sendMessage(msg);
            } else {
              p.sendMessage(prefix + " §cSpieler ist nicht online.");
            }
          }
        } else {
          if (cfg2.contains("Commands.Tpahere.WrongSyntax")) {
            String msg = cfg2.getString("Commands.Tpahere.WrongSyntax");
            msg = msg.replaceAll("&", "§");
            msg = msg.replaceAll("%prefix%", "" + prefix + "");
            p.sendMessage(msg);
          } else {
            p.sendMessage(prefix + " §cFalsche Syntax. Bitte benutze /tpahere <Spielername>");
          }
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
    return true;
  }
}
