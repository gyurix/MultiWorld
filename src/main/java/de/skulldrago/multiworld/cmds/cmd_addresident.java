package de.skulldrago.multiworld.cmds;

import de.skulldrago.multiworld.main.Multiworld;
import de.skulldrago.multiworld.mysql.MySQL;
import org.bukkit.Bukkit;
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

public class cmd_addresident implements CommandExecutor {
  File lang = new File("plugins/MultiWorld", "lang_de.yml");
  YamlConfiguration cfg2 = YamlConfiguration.loadConfiguration(lang);
  Multiworld service = Multiworld.getPlugin();
  String prefix = service.getPrefix();
  MySQL sql = Multiworld.getPlugin().getMysql();

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (sender instanceof Player) {
      Player p = (Player) sender;
      if (args.length == 1) {
        if (p.hasPermission("Multiworld.residents") || p.hasPermission("Multiworld.all")) {
          World w = p.getWorld();
          String name = w.getName();
          sql.withConnection(conn -> {
            try (PreparedStatement st = conn.prepareStatement("SELECT owner, type FROM worlds WHERE worldname='" + name + "'");
                 ResultSet rs = st.executeQuery()) {
              if (rs.next()) {
                String typ = rs.getString("type");
                String owner = rs.getString("owner");

                if (owner.equals(p.getName())) {
                  Player target = Bukkit.getPlayer(args[0]);
                  if (target != null) {

                    ResultSet rs2 = null;
                    PreparedStatement st2 = null;

                    st2 = conn.prepareStatement("SELECT worldname, type, resident FROM worldresidents WHERE worldname='" + name + "'");
                    rs2 = st2.executeQuery();
                    if (rs2.next()) {
                      rs2.last();
                      int anzahl = rs2.getRow();
                      rs2.first();

                      if (!(anzahl > 1)) {
                        if (rs2.getString("resident").equals("nobody")) {
                          sql.queryUpdate("UPDATE worldresidents SET resident='" + target.getName() + "' WHERE worldname = '" + name + "'");

                          if (cfg2.contains("Commands.Addresident.Finish")) {
                            String msg = cfg2.getString("Commands.Addresident.Finish");
                            msg = msg.replaceAll("&", "§");
                            msg = msg.replaceAll("%prefix%", "" + prefix + "");
                            msg = msg.replaceAll("%player%", "" + target.getName() + "");
                            p.sendMessage(msg);
                          } else {
                            p.sendMessage(prefix + " §c" + target.getName() + " ist nun Mitbewohner in deiner Welt");
                          }
                        } else {
                          sql.queryUpdate("INSERT INTO worldresidents (worldname, type, resident) VALUES ('" + name + "', '" + typ + "', '" + target.getName() + "')");

                          if (cfg2.contains("Commands.Addresident.Finish")) {
                            String msg = cfg2.getString("Commands.Addresident.Finish");
                            msg = msg.replaceAll("&", "§");
                            msg = msg.replaceAll("%prefix%", "" + prefix + "");
                            msg = msg.replaceAll("%player%", "" + target.getName() + "");
                            p.sendMessage(msg);
                          } else {
                            p.sendMessage(prefix + " §c" + target.getName() + " ist nun Mitbewohner in deiner Welt");
                          }
                        }
                      } else {
                        sql.queryUpdate("INSERT INTO worldresidents (worldname, type, resident) VALUES ('" + name + "', '" + typ + "', '" + target.getName() + "')");

                        if (cfg2.contains("Commands.Addresident.Finish")) {
                          String msg = cfg2.getString("Commands.Addresident.Finish");
                          msg = msg.replaceAll("&", "§");
                          msg = msg.replaceAll("%prefix%", "" + prefix + "");
                          msg = msg.replaceAll("%player%", "" + target.getName() + "");
                          p.sendMessage(msg);
                        } else {
                          p.sendMessage(prefix + " §c" + target.getName() + " ist nun Mitbewohner in deiner Welt");
                        }
                      }
                    }
                  } else {
                    if (cfg2.contains("System.NotOnline")) {
                      String msg = cfg2.getString("System.NotOnline");
                      msg = msg.replaceAll("&", "§");
                      msg = msg.replaceAll("%prefix%", "" + prefix + "");
                      p.sendMessage(msg);

                    } else {
                      p.sendMessage(prefix + " §6Spieler ist nicht online.");
                    }
                  }
                } else {
                  if (cfg2.contains("Commands.Addresident.OnlyOwner")) {
                    String msg = cfg2.getString("Commands.Addresident.OnlyOwner");
                    msg = msg.replaceAll("&", "§");
                    msg = msg.replaceAll("%prefix%", "" + prefix + "");
                    p.sendMessage(msg);
                  } else {
                    p.sendMessage(prefix + " §cNur der Owner kann dies tun.");
                  }
                }
              }
            } catch (SQLException e) {
              if (cfg2.contains("Commands.Addresident.Error")) {
                String msg = cfg2.getString("Commands.Addresident.Error");
                msg = msg.replaceAll("&", "§");
                msg = msg.replaceAll("%prefix%", "" + prefix + "");
                p.sendMessage(msg);
              } else {
                p.sendMessage(prefix + " §cVorgang wurde wegen Fehler abgebrochen.");
              }
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
        if (cfg2.contains("Commands.Addresident.WrongSyntax")) {
          String msg = cfg2.getString("Commands.Addresident.WrongSyntax");
          msg = msg.replaceAll("&", "§");
          msg = msg.replaceAll("%prefix%", "" + prefix + "");
          p.sendMessage(msg);
        } else {
          p.sendMessage(prefix + " §cFalsche Syntax. Bitte benutze /addresident <Spielername>.");
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
