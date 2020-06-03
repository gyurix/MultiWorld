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
import java.util.ArrayList;
import java.util.List;

public class cmd_setowner implements CommandExecutor {
  File lang = new File("plugins/MultiWorld", "lang_de.yml");
  YamlConfiguration cfg2 = YamlConfiguration.loadConfiguration(lang);
  Multiworld service = Multiworld.getPlugin();
  String prefix = service.getPrefix();
  MySQL sql = Multiworld.getPlugin().getMysql();

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (args.length == 1) {
      if (sender instanceof Player) {
        Player p = (Player) sender;
        if (p.hasPermission("Multiworld.setowner")) {
          World w = p.getWorld();
          Player target = Bukkit.getPlayer(args[0]);
          if (target != null) {
            sql.withConnection(conn -> {
              try (PreparedStatement st = conn.prepareStatement(
                      "SELECT worldname, owner, type FROM worlds WHERE worldname='" + w.getName() + "'");
                   ResultSet rs = st.executeQuery();

                   PreparedStatement st2 = conn.prepareStatement(
                           "SELECT resident FROM worldresidents WHERE worldname='" + w.getName() + "'");
                   ResultSet rs2 = st2.executeQuery()) {

                if (rs.next()) {
                  String owner = rs.getString("owner");
                  String typ = rs.getString("type");

                  if (owner.equals(p.getName()) || p.hasPermission("Multiworld.all")) {

                    List<String> residents = new ArrayList<>();
                    if (rs2.next()) {
                      while (rs2.next()) {
                        residents.add(rs2.getString("resident"));
                      }
                    }

                    PreparedStatement st3 = conn.prepareStatement(
                            "SELECT numbers, max, vnumbers, vmax FROM worldplayers WHERE name = '" + target.getName() + "'");
                    ResultSet rs3 = st3.executeQuery();

                    if (rs3.next()) {
                      int welten = rs3.getInt("numbers");
                      int maxwelten = rs3.getInt("max");
                      int voids = rs3.getInt("vnumbers");
                      int maxvoids = rs3.getInt("vmax");
                      boolean wrong = typ.equals("void") && welten == maxwelten;
                      boolean vwrong = !typ.equals("void") || (voids == maxvoids);

                      if (!(wrong) || (vwrong)) {

                        if (residents.contains("nobody")) {
                          residents.remove("nobody");
                          String resident = "nobody";
                          sql.queryUpdate("DELETE FROM worldresidents WHERE resident ='" + resident + "' AND worldname = '" + w.getName() + "'");
                        }

                        if (residents.contains(target.getName())) {
                          residents.remove(target.getName());
                          sql.queryUpdate("DELETE FROM worldresidents WHERE resident='" + target.getName() + "' AND worldname='" + w.getName() + "'");
                        }
                        residents.add(p.getName());

                        sql.queryUpdate("UPDATE worlds SET owner='" + target.getName() + "' WHERE worldname='" + w.getName() + "'");
                        sql.queryUpdate("INSERT INTO worldresidents (worldname, type, resident) VALUES ('" + w.getName() + "', '" + rs.getString("type") + "', '" + p.getName() + "')");

                        if (typ.equals("void")) {
                          voids = voids + 1;
                          sql.queryUpdate("UPDATE worldplayers SET vnumbers = '" + voids + "' WHERE name = '" + target.getName() + "'");

                        } else {
                          welten = welten + 1;
                          sql.queryUpdate("UPDATE worldplayers SET numbers = '" + welten + "' WHERE name = '" + target.getName() + "'");
                        }

                        if (cfg2.contains("Commands.Setowner.Finish")) {
                          String msg = cfg2.getString("Commands.Setowner.Finish");
                          msg = msg.replaceAll("&", "§");
                          msg = msg.replaceAll("%prefix%", "" + prefix + "");
                          msg = msg.replaceAll("%player%", "" + target.getName() + "");
                          p.sendMessage(msg);
                        } else {
                          p.sendMessage(prefix + " §c" + target.getName()
                                  + " ist nun der Owner dieser Welt");
                        }
                        if (cfg2.contains("Commands.Setowner.FinishPlayer")) {
                          String msg = cfg2.getString("Commands.Setowner.FinishPlayer");
                          msg = msg.replaceAll("&", "§");
                          msg = msg.replaceAll("%prefix%", "" + prefix + "");
                          target.sendMessage(msg);
                        } else {
                          target.sendMessage(prefix + " §cDu bist nun Owner dieser Welt");
                        }
                      } else {
                        if (cfg2.contains("Commands.Setowner.MaxError")) {
                          String msg = cfg2.getString("Commands.Setowner.MaxError");
                          msg = msg.replaceAll("&", "§");
                          msg = msg.replaceAll("%player%", "" + target.getName() + "");
                          msg = msg.replaceAll("%prefix%", "" + prefix + "");
                          p.sendMessage(msg);
                        } else {
                          p.sendMessage(prefix + "§cDer Spieler " + target.getName()
                                  + " hat schon seine maximale Anzahl Welten erreicht.");
                        }
                      }
                    }
                  } else if (owner.equals(p.getName())) {
                    List<String> residents = new ArrayList<>();
                    if (rs2.next()) {
                      rs2.beforeFirst();
                      while (rs2.next()) {
                        residents.add(rs.getString("Resident"));
                      }
                    }
                    PreparedStatement st3 = conn.prepareStatement(
                            "SELECT numbers, max, vnumbers, vmax FROM worldplayers WHERE name = '" + target.getName() + "'");
                    ResultSet rs3 = st3.executeQuery();

                    if (rs3.isBeforeFirst()) {
                      rs3.next();
                      int welten = rs3.getInt("numbers");
                      int maxwelten = rs3.getInt("max");
                      int voids = rs3.getInt("vnumbers");
                      int maxvoids = rs3.getInt("vmax");

                      boolean vwrong = !typ.equals("void") || (voids == maxvoids);

                      boolean wrong = typ.equals("void") && welten == maxwelten;

                      if (!(wrong) || (vwrong)) {

                        if (residents.contains(target.getName())) {
                          residents.remove(target.getName());
                          sql.queryUpdate("DELETE FROM worldresidents WHERE resident='" + target.getName() + "' AND worldname='" + w.getName() + "'");
                        }
                        sql.queryUpdate("UPDATE worlds SET Owner='" + target.getName() + "' WHERE worldname='" + w.getName() + "'");

                        if (typ.equals("void")) {

                          voids = voids + 1;
                          sql.queryUpdate("UPDATE worldplayers SET vnumbers = '" + voids + "' WHERE name = '" + target.getName() + "'");

                        } else if (!(typ.equals("void"))) {

                          welten = welten + 1;
                          sql.queryUpdate("UPDATE worldplayers SET anzahl = '" + welten + "' WHERE Name = '" + target.getName() + "'");

                        }

                        if (cfg2.contains("Commands.Setowner.Finish")) {
                          String msg = cfg2.getString("Commands.Setowner.Finish");
                          msg = msg.replaceAll("&", "§");
                          msg = msg.replaceAll("%prefix%", "" + prefix + "");
                          msg = msg.replaceAll("%player%", "" + target.getName() + "");
                          p.sendMessage(msg);
                        } else {
                          p.sendMessage(prefix + " §c" + target.getName() + " ist nun der Owner dieser Welt");
                        }
                        if (cfg2.contains("Commands.Setowner.FinishPlayer")) {
                          String msg = cfg2.getString("Commands.Setowner.FinishPlayer");
                          msg = msg.replaceAll("&", "§");
                          msg = msg.replaceAll("%prefix%", "" + prefix + "");
                          target.sendMessage(msg);
                        } else {
                          target.sendMessage(prefix + " §cDu bist nun Owner dieser Welt");
                        }
                      } else {
                        if (cfg2.contains("Commands.Setowner.MaxError")) {
                          String msg = cfg2.getString("Commands.Setowner.MaxError");
                          msg = msg.replaceAll("&", "§");
                          msg = msg.replaceAll("%player%", "" + target.getName() + "");
                          msg = msg.replaceAll("%prefix%", "" + prefix + "");
                          p.sendMessage(msg);
                        } else {
                          p.sendMessage(prefix + "§cDer Spieler " + target.getName() + " hat schon seine maximale Anzahl Welten erreicht.");
                        }
                      }
                    }
                  } else {
                    if (cfg2.contains("Commands.Setowner.OnlyOwner")) {
                      String msg = cfg2.getString("Commands.Setowner.OnlyOwner");
                      msg = msg.replaceAll("&", "§");
                      msg = msg.replaceAll("%prefix%", "" + prefix + "");
                      p.sendMessage(msg);
                    } else {
                      p.sendMessage(prefix + " §cNur der Owner kann dies tun.");
                    }
                  }
                }
              } catch (SQLException e) {
                if (cfg2.contains("Commands.Setowner.Error")) {
                  String msg = cfg2.getString("Commands.Setowner.Error");
                  msg = msg.replaceAll("&", "§");
                  msg = msg.replaceAll("%prefix%", "" + prefix + "");
                  p.sendMessage(msg);
                } else {
                  p.sendMessage(prefix + " §cVorgang konnte nicht durchgef§hrt werden.");
                }
              }
            });
          } else {
            if (cfg2.contains("Commands.Setowner.NotOnline")) {
              String msg = cfg2.getString("Commands.Setowner.NotOnline");
              msg = msg.replaceAll("&", "§");
              msg = msg.replaceAll("%prefix%", "" + prefix + "");
              p.sendMessage(msg);
            } else {
              p.sendMessage(prefix + " §cSpieler ist nicht online");
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
      if (cfg2.contains("Commands.Setowner.Wrongsyntax")) {
        String msg = cfg2.getString("Commands.Setowner.WrongSyntax");
        msg = msg.replaceAll("&", "§");
        msg = msg.replaceAll("%prefix%", "" + prefix + "");
        sender.sendMessage(msg);
      } else {
        sender.sendMessage(prefix + " &cFalsche Syntax. Bitte benutze /setowner <Spielername>");
      }
    }
    return true;
  }
}
