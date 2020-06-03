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

public class cmd_delete implements CommandExecutor {
  File lang = new File("plugins/MultiWorld", "lang_de.yml");
  YamlConfiguration cfg2 = YamlConfiguration.loadConfiguration(lang);
  Multiworld service = Multiworld.getPlugin();
  String prefix = service.getPrefix();
  MySQL sql = Multiworld.getPlugin().getMysql();

  private boolean deleteWorld(File path) {
    if (path.exists()) {
      File[] files = path.listFiles();
      for (File file : files) {
        if (file.isDirectory()) {
          deleteWorld(file);
        } else {
          file.delete();
        }
      }
    }
    return (path.delete());
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (args.length == 1) {
      if (sender instanceof Player) {
        Player p = (Player) sender;
        if (p.hasPermission("Multiworld.delete") || p.hasPermission("Multiworld.all")) {
          String worlddefault = service.getConfig().getString("Multiworld.Defaultworld");
          World defaultw = Bukkit.getWorld(worlddefault);
          World delete = Bukkit.getWorld(args[0]);
          if (delete != null) {
            if (delete != defaultw) {
              File deleteFolder = delete.getWorldFolder();
              for (Player target : Bukkit.getServer().getOnlinePlayers()) {
                if (target.getWorld().getName().equalsIgnoreCase(args[0])) {
                  Bukkit.getScheduler().runTask(Multiworld.getPlugin(),
                          () -> target.teleport(defaultw.getSpawnLocation()));

                }
              }
              Bukkit.getServer().unloadWorld(args[0], true);
              deleteWorld(deleteFolder);

              sql.withConnection(conn -> {

                try (PreparedStatement st = conn.prepareStatement("SELECT owner, type FROM worlds WHERE worldname = '" + delete.getName() + "'");
                     ResultSet rs = st.executeQuery()) {
                  if (rs.next()) {
                    String typ = rs.getString("type");
                    String owner = rs.getString("owner");

                    if (!(owner.equals("Server"))) {

                      try (PreparedStatement st2 = conn.prepareStatement(
                              "SELECT numbers, vnumbers FROM worldplayers WHERE name = '" + owner
                                      + "'");
                           ResultSet rs2 = st2.executeQuery()) {
                        if (rs2.next()) {
                          int welten = rs2.getInt("numbers");
                          int voids = rs2.getInt("vnumbers");

                          if (typ.equals("void")) {
                            voids = voids - 1;
                          } else {
                            welten = welten - 1;
                          }

                          sql.queryUpdate("UPDATE worldplayers SET numbers = '" + welten
                                  + "', vnumbers = '" + voids + "' WHERE name = '" + owner + "'");
                        }
                      }
                    }
                  }
                } catch (SQLException e) {
                  e.printStackTrace();
                }
              });

              sql.queryUpdate("DELETE FROM worlds WHERE worldname = '" + delete.getName() + "'");
              sql.queryUpdate("DELETE FROM worldresidents WHERE worldname = '" + delete.getName() + "'");

              if (cfg2.contains("Commands.Delworld.Finish")) {
                String msg = cfg2.getString("Commands.Delworld.Finish");
                msg = msg.replaceAll("&", "§");
                msg = msg.replaceAll("%prefix%", "" + prefix + "");
                msg = msg.replaceAll("%world%", "" + args[0] + "");
                p.sendMessage(msg);
              } else {
                p.sendMessage(prefix + " §cDie Welt §5" + args[0] + " §awurde erfolgreich entfernt!");
              }
            } else {
              if (cfg2.contains("Commands.Delworld.Defaultworld")) {
                String msg = cfg2.getString("Commands.Delworld.Defaultworld");
                msg = msg.replaceAll("&", "§");
                msg = msg.replaceAll("%prefix%", "" + prefix + "");
                p.sendMessage(msg);
              } else {
                p.sendMessage(prefix + " §cWelt existiert nicht.");
              }
            }
          } else {
            if (cfg2.contains("Commands.Delworld.NoWorld")) {
              String msg = cfg2.getString("Commands.Delworld.NoWorld");
              msg = msg.replaceAll("&", "§");
              msg = msg.replaceAll("%prefix%", "" + prefix + "");
              p.sendMessage(msg);
            } else {
              p.sendMessage(prefix + " §cWelt existiert nicht.");
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
      if (cfg2.contains("Commands.Delworld.WrongSyntax")) {
        String msg = cfg2.getString("Commands.Delworld.WrongSyntax");
        msg = msg.replaceAll("&", "§");
        msg = msg.replaceAll("%prefix%", "" + prefix + "");
        sender.sendMessage(msg);
      } else {
        sender.sendMessage(prefix + " §cFalsche Syntax. Bitte benutze /deleteworld <Weltname>");
      }
    }
    return true;
  }
}
