package de.skulldrago.multiworld.cmds;

import de.skulldrago.multiworld.main.Multiworld;
import de.skulldrago.multiworld.mysql.MySQL;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class cmd_unlock implements CommandExecutor {
  File lang = new File("plugins/MultiWorld", "lang_de.yml");
  YamlConfiguration cfg2 = YamlConfiguration.loadConfiguration(lang);
  Multiworld service = Multiworld.getPlugin();
  String prefix = service.getPrefix();
  MySQL sql = Multiworld.getPlugin().getMysql();

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (sender instanceof Player) {
      Player p = (Player) sender;
      if (args.length == 0) {
        if (p.hasPermission("Multiworld.lock") || p.hasPermission("Multiworld.all")) {
          World w = p.getWorld();
          sql.withConnection(conn -> {
            try (PreparedStatement st = conn.prepareStatement("SELECT worldname, owner FROM worlds WHERE worldname = '" + w.getName() + "'");
                 ResultSet rs = st.executeQuery();) {
              if (rs.next()) {
                String owner = rs.getString("owner");

                if (owner.equals(p.getName()) || p.hasPermission("Multiworld.all")) {

                  String locked = "false";

                  sql.queryUpdate("UPDATE worlds SET locked = '" + locked + "' WHERE worldname = '" + rs.getString("worldname") + "'");

                  if (cfg2.contains("Commands.Unlock.Finish")) {
                    String msg = cfg2.getString("Commands.Unlock.Finish");
                    msg = msg.replaceAll("&", "§");
                    msg = msg.replaceAll("%prefix%", "" + prefix + "");
                    p.sendMessage(msg);
                  } else {
                    p.sendMessage("§cDeine Welt ist jetzt entsperrt und kann von jedem betreten werden.");
                  }

                } else {
                  if (cfg2.contains("Commands.Unlock.OnlyOwner")) {
                    String msg = cfg2.getString("Commands.Unlock.OnlyOwner");
                    msg = msg.replaceAll("&", "§");
                    msg = msg.replaceAll("%prefix%", "" + prefix + "");
                    p.sendMessage(msg);
                  } else {
                    p.sendMessage(prefix + " §cNur der Owner kann dies tun");
                  }
                }
              }
            } catch (SQLException e) {
              e.printStackTrace();
            }
          });

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
        if (cfg2.contains("Commands.Unlock.WrongSyntax")) {
          String msg = cfg2.getString("Commands.Unlock.WrongSyntax");
          msg = msg.replaceAll("&", "§");
          msg = msg.replaceAll("%prefix%", "" + prefix + "");
          p.sendMessage(msg);
        } else {
          p.sendMessage(prefix + " §cFalsche Syntax. Bitte benutze /unlock.");
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
