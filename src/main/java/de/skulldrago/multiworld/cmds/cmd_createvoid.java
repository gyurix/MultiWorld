package de.skulldrago.multiworld.cmds;

import de.skulldrago.multiworld.main.Multiworld;
import de.skulldrago.multiworld.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static de.skulldrago.multiworld.cmds.cmd_createserver.createWorld;

public class cmd_createvoid implements CommandExecutor {
  File lang = new File("plugins/MultiWorld", "lang_de.yml");
  YamlConfiguration cfg3 = YamlConfiguration.loadConfiguration(lang);
  Multiworld service = Multiworld.getPlugin();
  String prefix = service.getPrefix();
  MySQL sql = Multiworld.getPlugin().getMysql();

  public void copyWorld(File source, File target) {
    try {
      ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat", "session.lock"));
      if (!ignore.contains(source.getName())) {
        if (source.isDirectory()) {
          if (!target.exists()) {
            target.mkdirs();
          }
          String[] files = source.list();
          for (String file : files) {
            File srcFile = new File(source, file);
            File destFile = new File(target, file);
            copyWorld(srcFile, destFile);
          }
        } else {
          InputStream in = new FileInputStream(source);
          OutputStream out = new FileOutputStream(target);
          byte[] buffer = new byte[2048];
          int length;
          while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
          }
          in.close();
          out.close();
        }
      }
    } catch (IOException ignored) {

    }
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (sender instanceof Player) {
      Player p = (Player) sender;
      if (p.hasPermission("Multiworld.void")) {
        if (args.length == 2) {
          String WorldName = args[0];
          String target = args[1];
          Player targetp = Bukkit.getPlayer(target);

          if (target != null) {
            sql.withConnection((conn) -> {
              try (PreparedStatement st = conn.prepareStatement("SELECT worldname FROM worlds WHERE worldname = '" + WorldName + "'");
                   ResultSet rs = st.executeQuery();
                   PreparedStatement st2 = conn.prepareStatement("SELECT vnumbers, vmax FROM worldplayers WHERE name = '" + targetp.getName() + "'");
                   ResultSet rs2 = st2.executeQuery()) {
                if (rs2.next()) {
                  AtomicInteger voids = new AtomicInteger(rs2.getInt("vnumbers"));
                  int maxvoids = rs2.getInt("vmax");

                  if (!(voids.get() == maxvoids) || p.hasPermission("Multiworld.admin")) {

                    if (!(rs.next())) {

                      File sourceFolder = new File("plugins/MultiWorld/backup/void");
                      if (sourceFolder.exists()) {
                        File targetFolder = new File(Bukkit.getWorldContainer(), WorldName);
                        copyWorld(sourceFolder, targetFolder);
                        Bukkit.getServer().createWorld(new WorldCreator(WorldName));
                      } else {
                        if (cfg3.contains("Commands.Createvoid.VoidSourceFail")) {
                          String msg = cfg3.getString("Commands.Createvoid.VoidSourceFail");
                          msg = msg.replaceAll("&", "§");
                          msg = msg.replaceAll("%prefix%", "" + prefix + "");
                          p.sendMessage(msg);
                        } else {
                          p.sendMessage(prefix + " §cVoidquelle fehlt");
                        }
                      }

                      WorldCreator c = WorldCreator.name(WorldName).generator("EmptyWorldGenerator");
                      createWorld(c);
                    } else {
                      if (cfg3.contains("Commands.Createvoid.IsVoid")) {
                        String msg = cfg3.getString("Commands.Createvoid.IsVoid");
                        msg = msg.replaceAll("&", "§");
                        msg = msg.replaceAll("%prefix%", "" + prefix + "");
                        p.sendMessage(msg);
                      } else {
                        p.sendMessage(prefix + " §cVoid existiert schon");
                      }
                    }

                    String typ = "void";
                    boolean locked = true;

                    Bukkit.getScheduler().scheduleSyncDelayedTask(Multiworld.getPlugin(), () -> {
                      Location loc = Bukkit.getWorld(WorldName).getSpawnLocation();

                      double x = loc.getX();
                      double y = loc.getY();
                      double z = loc.getZ();

                      float yaw = loc.getYaw();
                      float pitch = loc.getPitch();

                      String resident = "nobody";
                      voids.incrementAndGet();

                      sql.queryUpdate("INSERT INTO worlds (worldname, owner, locked, type, spawnx, spawny, spawnz, spawnyaw, spawnpitch) VALUES ('" + WorldName + "', '" + targetp.getName() + "','" + locked + "', '" + typ + "', '" + x + "', '" + y + "', '" + z + "', '" + yaw + "', '" + pitch + "')");
                      sql.queryUpdate("INSERT INTO worldresidents (worldname, type, resident) VALUES ('" + WorldName + "', '" + typ + "', '" + resident + "')");
                      sql.queryUpdate("UPDATE worldplayers SET vnumbers = '" + voids + "' WHERE name = '" + targetp.getName() + "'");
                      Bukkit.getScheduler().runTask(Multiworld.getPlugin(), () -> targetp.teleport(Bukkit.getWorld(WorldName).getSpawnLocation()));
                    }, 2);


                    if (cfg3.contains("Commands.Createvoid.Finish")) {
                      String msg = cfg3.getString("Commands.Createvoid.Finish");
                      msg = msg.replaceAll("&", "§");
                      msg = msg.replaceAll("%prefix%", "" + prefix + "");
                      targetp.sendMessage(msg);
                    } else {
                      targetp.sendMessage(prefix + " §3Deine Void wurde angelegt");
                    }

                    if (cfg3.contains("Commands.Createvoid.Teleport")) {
                      String msg = cfg3.getString("Commands.Createvoid.Teleport");
                      msg = msg.replaceAll("&", "§");
                      msg = msg.replaceAll("%prefix%", "" + prefix + "");
                      targetp.sendMessage(msg);
                    } else {
                      targetp.sendMessage(prefix + " §cDu wurdest in deine neue Void teleportiert");
                    }
                  } else {
                    if (cfg3.contains("Commands.Createworld.MaxError")) {
                      String msg = cfg3.getString("Commands.Createworld.MaxError");
                      msg = msg.replaceAll("&", "§");
                      msg = msg.replaceAll("%prefix%", "" + prefix + "");
                      p.sendMessage(msg);
                    } else {
                      p.sendMessage(prefix + "§cError Die maximale Anzahl Welten erreicht");
                    }
                  }
                }
              } catch (SQLException e) {
                e.printStackTrace();
              }
            });
          } else {
            if (cfg3.contains("System.NotOnline")) {
              String msg = cfg3.getString("System.NotOnline");
              msg = msg.replaceAll("&", "§");
              msg = msg.replaceAll("%prefix%", "" + prefix + "");
              p.sendMessage(msg);

            } else {
              p.sendMessage(prefix + " §6Spieler ist nicht online.");
            }
          }
        } else {
          if (cfg3.contains("Commands.Createvoid.WrongSyntax")) {
            String msg = cfg3.getString("Commands.Createvoid.WrongSyntax");
            msg = msg.replaceAll("&", "§");
            msg = msg.replaceAll("%prefix%", "" + prefix + "");
            p.sendMessage(msg);
          } else {
            p.sendMessage(prefix + " §cFalsche Syntax. Bitte benutze /createvoid <Voidname> <Spieler>");
          }
        }
      } else {
        if (cfg3.contains("System.NoPermission")) {
          String msg = cfg3.getString("System.NoPermission");
          msg = msg.replaceAll("&", "§");
          msg = msg.replaceAll("%prefix%", "" + prefix + "");
          p.sendMessage(msg);

        } else {
          p.sendMessage(prefix + " §cDu hast nicht die Permissions um diesen Befehl zu benutzen.");
        }
      }
    } else {
      if (cfg3.contains("System.OnlyPlayers")) {
        String msg = cfg3.getString("System.OnlyPlayers");
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
