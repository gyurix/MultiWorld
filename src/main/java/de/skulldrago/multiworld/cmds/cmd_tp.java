package de.skulldrago.multiworld.cmds;

import de.skulldrago.multiworld.main.Multiworld;
import de.skulldrago.multiworld.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
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

public class cmd_tp implements CommandExecutor {
  File lang = new File("plugins/MultiWorld", "lang_de.yml");
  YamlConfiguration cfg4 = YamlConfiguration.loadConfiguration(lang);
  Multiworld service = Multiworld.getPlugin();
  String prefix = service.getPrefix();
  MySQL sql = Multiworld.getPlugin().getMysql();

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (sender instanceof Player) {
      Player p = (Player) sender;
      if (p.hasPermission("Multiworld.tp")) {
        if (args.length == 0) {
          sql.withConnection(conn -> {
            try (PreparedStatement st = conn.prepareStatement("SELECT * FROM worlds WHERE owner = '" + p.getName() + "'");
                 ResultSet rs = st.executeQuery()) {
              if (rs.next()) {
                World playerworld = Bukkit.getWorld(rs.getString("worldname"));

                if (playerworld != null) {
                  rs.next();
                  double x = rs.getDouble("spawnx");
                  double y = rs.getDouble("spawny");
                  double z = rs.getDouble("spawnz");

                  float yaw = rs.getFloat("spawnyaw");
                  float pitch = rs.getFloat("spawnpitch");

                  Location loc = new Location(playerworld, x, y, z, yaw, pitch);
                  Bukkit.getScheduler().runTask(Multiworld.getPlugin(), () -> p.teleport(loc));

                } else {
                  Bukkit.getServer().createWorld(new WorldCreator(rs.getString("worldname")));
                  double x = rs.getDouble("spawnx");
                  double y = rs.getDouble("spawny");
                  double z = rs.getDouble("spawnz");

                  float yaw = rs.getFloat("spawnyaw");
                  float pitch = rs.getFloat("spawnpitch");

                  Location loc = new Location(playerworld, x, y, z, yaw, pitch);

                  Bukkit.getScheduler().runTask(Multiworld.getPlugin(), () -> p.teleport(loc));
                }
              } else {
                if (cfg4.contains("Commands.Tpworld.Error")) {
                  String msg = cfg4.getString("Commands.Tpworld.Error");
                  msg = msg.replaceAll("&", "§");
                  msg = msg.replaceAll("%prefix%", "" + prefix + "");
                  p.sendMessage(msg);
                } else {
                  p.sendMessage(prefix + " §cFehler: Welt existiert nicht");
                }
              }
            } catch (SQLException e) {
              e.printStackTrace();
            }
          });
        } else if (args.length == 1) {
          String target = args[0];

          sql.withConnection(conn -> {
            try (PreparedStatement st = conn.prepareStatement("SELECT * FROM worlds WHERE worldname = '" + target + "'");
                 ResultSet rs = st.executeQuery()) {
              if (rs.next()) {
                boolean locked = rs.getBoolean("locked");

                World Playerworld = Bukkit.getWorld(rs.getString("worldname"));
                if (Playerworld == null) {
                  p.chat("/load " + target);
                }
                if ((locked) && (!(p.hasPermission("Multiworld.admin")))) {
                  String owner = rs.getString("owner");

                  try (PreparedStatement st2 = conn.prepareStatement("SELECT * FROM worldresidents WHERE owner = '" + owner + "'");
                       ResultSet rs2 = st2.executeQuery()) {

                    List<String> residents = new ArrayList<String>();
                    if (rs2.next()) {
                      while (rs2.next()) {
                        residents.add(rs2.getString("resident"));
                      }
                    }

                    if (owner.equals(p.getName()) || residents.contains(p.getName())) {

                      if (Playerworld != null) {
                        double x = rs.getDouble("spawnx");
                        double y = rs.getDouble("spawny");
                        double z = rs.getDouble("spawnz");
                        float yaw = rs.getFloat("spawnyaw");
                        float pitch = rs.getFloat("spawnpitch");

                        Location loc2 = new Location(Playerworld, x, y, z, yaw, pitch);
                        Bukkit.getScheduler().runTask(Multiworld.getPlugin(), () -> p.teleport(loc2));

                      } else {
                        Bukkit.getServer().createWorld(new WorldCreator(rs.getString("worldname")));
                        double x = rs.getDouble("spawnx");
                        double y = rs.getDouble("spawny");
                        double z = rs.getDouble("spawnz");

                        float yaw = rs.getFloat("spawnyaw");
                        float pitch = rs.getFloat("spawnpitch");

                        Location loc = new Location(Playerworld, x, y, z, yaw, pitch);

                        Bukkit.getScheduler().runTask(Multiworld.getPlugin(), () -> p.teleport(loc));
                      }
                    } else {
                      if (cfg4.contains("System.NoPermission")) {
                        String msg = cfg4.getString("System.NoPermission");
                        msg = msg.replaceAll("&", "§");
                        msg = msg.replaceAll("%prefix%", "" + prefix + "");
                        p.sendMessage(msg);

                      } else {
                        p.sendMessage(prefix + " §cDu hast nicht die Permissions um diesen Befehl zu benutzen.");
                      }
                    }
                  }
                } else {
                  String owner = rs.getString("owner");

                  if (owner != null && !owner.equals(" ")) {

                    if (Playerworld != null) {
                      double X = rs.getDouble("spawnx");
                      double Y = rs.getDouble("spawny");
                      double Z = rs.getDouble("spawnz");
                      float Yaw = rs.getFloat("spawnyaw");
                      float Pitch = rs.getFloat("spawnpitch");
                      Location loc3 = new Location(Playerworld, X, Y, Z, Yaw, Pitch);
                      Bukkit.getScheduler().runTask(Multiworld.getPlugin(), () -> p.teleport(loc3));
                    } else {
                      if (cfg4.contains("Commands.Tpworld.Error")) {
                        String msg = cfg4.getString("Commands.Tpworld.Error");
                        msg = msg.replaceAll("&", "§");
                        msg = msg.replaceAll("%prefix%", "" + prefix + "");
                        p.sendMessage(msg);
                      } else {
                        p.sendMessage(prefix + " §cFehler: Welt existiert nicht");
                      }

                    }

                  } else if (target.equalsIgnoreCase("world") || target.equalsIgnoreCase("world_nether") || target.equalsIgnoreCase("world_the_end")) {
                    World w = Bukkit.getWorld(target);
                    Location loc = w.getSpawnLocation();
                    Bukkit.getScheduler().runTask(Multiworld.getPlugin(), () -> p.teleport(loc));
                  } else {
                    if (cfg4.contains("Commands.Tpworld.NotExists")) {
                      String msg = cfg4.getString("Commands.Tpworld.NotExists");
                      msg = msg.replaceAll("&", "§");
                      msg = msg.replaceAll("%prefix%", "" + prefix + "");
                      p.sendMessage(msg);
                    } else {
                      p.sendMessage(prefix + " §cWelt existiert nicht oder du bist nicht berechtigt");
                    }
                  }
                }
              } else {
                if (cfg4.contains("Commands.Tpworld.Error")) {
                  String msg = cfg4.getString("Commands.Tpworld.Error");
                  msg = msg.replaceAll("&", "§");
                  msg = msg.replaceAll("%prefix%", "" + prefix + "");
                  p.sendMessage(msg);
                } else {
                  p.sendMessage(prefix + " §cFehler: Welt existiert nicht");
                }
              }

            } catch (SQLException e) {
              e.printStackTrace();
            }
          });
        } else {
          if (cfg4.contains("Commands.Tpworld.WrongSyntax")) {
            String msg = cfg4.getString("Commands.Tpworld.WrongSyntax");
            msg = msg.replaceAll("&", "§");
            msg = msg.replaceAll("%prefix%", "" + prefix + "");
            p.sendMessage(msg);
          } else {
            p.sendMessage(prefix + " §cFalsche Syntax. Benutze bitte /tpworld oder /tpworld <Weltname>");
          }
        }
      } else {
        if (cfg4.contains("System.NoPermission")) {
          String msg = cfg4.getString("System.NoPermission");
          msg = msg.replaceAll("&", "§");
          msg = msg.replaceAll("%prefix%", "" + prefix + "");
          p.sendMessage(msg);

        } else {
          p.sendMessage(prefix + " §cDu hast nicht die Permissions um diesen Befehl zu benutzen.");
        }
      }
    } else {
      if (cfg4.contains("System.OnlyPlayers")) {
        String msg = cfg4.getString("System.OnlyPlayers");
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
