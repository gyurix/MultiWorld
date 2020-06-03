package de.skulldrago.multiworld.cmds;

import de.skulldrago.multiworld.main.Multiworld;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

public class cmd_unload implements CommandExecutor {
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
          if (p.getWorld().getName().equals(args[0])) {
            List<Player> players = p.getWorld().getPlayers();
            for (Player s : players) {
              if (s.isOnline()) {
                Bukkit.getScheduler().runTask(Multiworld.getPlugin(), () ->
                        s.teleport(Bukkit.getServer().getWorld("world").getSpawnLocation()));
              }
            }

            Bukkit.getServer().unloadWorld(args[0], true);
            if (cfg2.contains("Commands.Unload.Finish")) {
              String msg = cfg2.getString("Commands.Unload.Finish");
              msg = msg.replaceAll("&", "§");
              msg = msg.replaceAll("%prefix%", "" + prefix + "");
              msg = msg.replaceAll("%world%", "" + args[0] + "");
              p.sendMessage(msg);
            } else {
              p.sendMessage(prefix + " §2Du hast die Welt §6" + args[0] + " §2erfolgreich entladen.");
            }

          } else {
            if (cfg2.contains("Commands.Unload.NoWorld")) {
              String msg = cfg2.getString("Commands.Unload.NoWorld");
              msg = msg.replaceAll("&", "§");
              msg = msg.replaceAll("%prefix%", "" + prefix + "");
              p.sendMessage(msg);
            } else {
              p.sendMessage(prefix + " §cDiese Welt existiert nicht und kann nicht entladen werden.");
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
    } else {
      if (cfg2.contains("Commands.Unload.WrongSyntax")) {
        String msg = cfg2.getString("Commands.Unload.WrongSyntax");
        msg = msg.replaceAll("&", "§");
        msg = msg.replaceAll("%prefix%", "" + prefix + "");
        sender.sendMessage(msg);
      } else {
        sender.sendMessage(prefix + " §cFalsche Syntax. Bitte benutze /unload <Weltname>");
      }
    }
    return true;
  }

}
