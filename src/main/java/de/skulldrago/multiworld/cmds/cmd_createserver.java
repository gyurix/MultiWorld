package de.skulldrago.multiworld.cmds;

import de.skulldrago.multiworld.main.Multiworld;
import de.skulldrago.multiworld.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class cmd_createserver implements CommandExecutor {
  File lang = new File("plugins/MultiWorld", "lang_de.yml");
  YamlConfiguration cfg2 = YamlConfiguration.loadConfiguration(lang);
  Multiworld service = Multiworld.getPlugin();
  String prefix = service.getPrefix();
  MySQL sql = Multiworld.getPlugin().getMysql();

  public static void createWorld(WorldCreator wc) {
    Bukkit.getScheduler().scheduleSyncDelayedTask(Multiworld.getPlugin(), () -> Bukkit.createWorld(wc));
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (sender instanceof Player) {
      Player p = (Player) sender;
      if (p.hasPermission("Multiworld.serverworld")) {
        if (args.length == 1) {
          WorldCreator c = WorldCreator.name(args[0]);
          createWorld(c);
        } else if (args.length == 3) {
          String worldName = args[0];
          String worldEnv = args[1];
          String worldType = args[2];

          sql.withConnection(conn -> {
            try (PreparedStatement st = conn.prepareStatement("SELECT worldname FROM worlds WHERE worldname= '" + worldName + "'");
                 ResultSet rs = st.executeQuery()) {
              if (!(rs.next())) {
                if (cfg2.contains("Commands.Createworld.Create")) {
                  String msg = cfg2.getString("Commands.Createworld.Create");
                  msg = msg.replaceAll("&", "§");
                  msg = msg.replaceAll("%world%", "" + worldName + "");
                  msg = msg.replaceAll("%prefix%", "" + prefix + "");
                  p.sendMessage(msg);
                } else {
                  p.sendMessage(prefix + " §4Welt §e" + worldName + " §4wird erstellt...");
                }

                if (worldEnv.equalsIgnoreCase("normal")) {
                  if (worldType.equalsIgnoreCase("normal")) {
                    WorldCreator c = WorldCreator.name(worldName).environment(Environment.NORMAL).type(WorldType.NORMAL);
                    createWorld(c);
                  } else if (worldType.equalsIgnoreCase("flat")) {
                    WorldCreator c = WorldCreator.name(worldName).environment(Environment.NORMAL).type(WorldType.FLAT);
                    createWorld(c);
                  } else if (worldType.equalsIgnoreCase("amplified")) {
                    WorldCreator c = WorldCreator.name(worldName).environment(Environment.NORMAL).type(WorldType.AMPLIFIED);
                    createWorld(c);
                  } else if (worldType.equalsIgnoreCase("bigbiome")) {
                    WorldCreator c = WorldCreator.name(worldName).environment(Environment.NORMAL).type(WorldType.LARGE_BIOMES);
                    createWorld(c);
                  } else {
                    if (cfg2.contains("Commands.Createserver.WrongSyntaxEnvironment")) {
                      String msg = cfg2.getString("Commands.Createworld.WrongSyntaxEnvironment");
                      msg = msg.replaceAll("&", "§");
                      msg = msg.replaceAll("%prefix%", "" + prefix + "");
                      p.sendMessage(msg);
                    } else {
                      p.sendMessage(prefix + " §c/createserverworld <Name> <Environment (normal | nether | end)> <Type (normal | flat | amplified | bigbiome)>");
                    }
                  }
                } else if (worldEnv.equalsIgnoreCase("nether")) {
                  if (worldType.equalsIgnoreCase("normal")) {
                    WorldCreator c = WorldCreator.name(worldName).environment(Environment.NETHER).type(WorldType.NORMAL);
                    createWorld(c);
                  } else if (worldType.equalsIgnoreCase("flat")) {
                    WorldCreator c = WorldCreator.name(worldName).environment(Environment.NETHER).type(WorldType.FLAT);
                    createWorld(c);
                  } else if (worldType.equalsIgnoreCase("amplified")) {
                    WorldCreator c = WorldCreator.name(worldName).environment(Environment.NETHER).type(WorldType.AMPLIFIED);
                    createWorld(c);
                  } else if (worldType.equalsIgnoreCase("bigbiome")) {
                    WorldCreator c = WorldCreator.name(worldName).environment(Environment.NETHER).type(WorldType.LARGE_BIOMES);
                    createWorld(c);
                  } else {
                    if (cfg2.contains("Commands.Createserver.WrongSyntaxEnvironment")) {
                      String msg = cfg2.getString("Commands.Createworld.WrongSyntaxEnvironment");
                      msg = msg.replaceAll("&", "§");
                      msg = msg.replaceAll("%prefix%", "" + prefix + "");
                      p.sendMessage(msg);
                    } else {
                      p.sendMessage(prefix + " §c/createserverworld <Name> <Environment (normal | nether | end)> <Type (normal | flat | amplified | bigbiome)>");
                    }
                  }
                } else if (worldEnv.equalsIgnoreCase("end")) {
                  if (worldType.equalsIgnoreCase("normal")) {
                    WorldCreator c = WorldCreator.name(worldName).environment(Environment.THE_END).type(WorldType.NORMAL);
                    createWorld(c);
                  } else if (worldType.equalsIgnoreCase("flat")) {
                    WorldCreator c = WorldCreator.name(worldName).environment(Environment.THE_END).type(WorldType.FLAT);
                    createWorld(c);
                  } else if (worldType.equalsIgnoreCase("amplified")) {
                    WorldCreator c = WorldCreator.name(worldName).environment(Environment.THE_END).type(WorldType.AMPLIFIED);
                    createWorld(c);
                  } else if (worldType.equalsIgnoreCase("bigbiome")) {
                    WorldCreator c = WorldCreator.name(worldName).environment(Environment.THE_END).type(WorldType.LARGE_BIOMES);
                    createWorld(c);
                  } else {
                    if (cfg2.contains("Commands.Createserver.WrongSyntaxEnvironment")) {
                      String msg = cfg2.getString("Commands.Createworld.WrongSyntaxEnvironment");
                      msg = msg.replaceAll("&", "§");
                      msg = msg.replaceAll("%prefix%", "" + prefix + "");
                      p.sendMessage(msg);
                    } else {
                      p.sendMessage(prefix + " §c/createserverworld <Name> <Environment (normal | nether | end)> <Type (normal | flat | amplified | bigbiome)>");
                    }
                  }
                } else {
                  if (cfg2.contains("Commands.Createserver.WrongSyntaxEnvironment")) {
                    String msg = cfg2.getString("Commands.Createworld.WrongSyntaxEnvironment");
                    msg = msg.replaceAll("&", "§");
                    msg = msg.replaceAll("%prefix%", "" + prefix + "");
                    p.sendMessage(msg);
                  } else {
                    p.sendMessage(prefix + " §c/createserverworld <Name> <Environment (normal | nether | end)> <Type (normal | flat | amplified | bigbiome)>");
                  }
                }

                String owner = "Server";
                String locked = "false";
                Bukkit.getScheduler().scheduleSyncDelayedTask(Multiworld.getPlugin(), () -> {
                  Location loc = Bukkit.getWorld(worldName).getSpawnLocation();

                  double x = loc.getX();
                  double y = loc.getY();
                  double z = loc.getZ();

                  float yaw = loc.getYaw();
                  float pitch = loc.getPitch();

                  String resident = "nobody";

                  sql.queryUpdate("INSERT INTO worlds (worldname, owner, locked, type, spawnx, spawny, spawnz, spawnyaw, spawnpitch) VALUES ('" + worldName + "', '" + owner + "', '" + locked + "', '" + worldType + "', '" + x + "', '" + y + "', '" + z + "', '" + yaw + "', '" + pitch + "')");
                  sql.queryUpdate("INSERT INTO worldresidents (worldname, type, resident) VALUES ('" + worldName + "', '" + worldType + "', '" + resident + "')");

                  if (cfg2.contains("Commands.Createserver.Finish")) {
                    String msg = cfg2.getString("Commands.Createworld.Finish");
                    msg = msg.replaceAll("&", "§");
                    msg = msg.replaceAll("%world%", "" + worldName + "");
                    msg = msg.replaceAll("%prefix%", "" + prefix + "");
                    p.sendMessage(msg);
                  } else {
                    p.sendMessage(prefix + " §cWelt §e" + worldName + " §cerfolgreich erstellt.");
                  }
                  Bukkit.getScheduler().runTask(Multiworld.getPlugin(), () ->
                          p.teleport(Bukkit.getWorld(worldName).getSpawnLocation()));
                }, 2);
              } else {
                if (cfg2.contains("Commands.Createserver.IsWorld")) {
                  String msg = cfg2.getString("Commands.Createworld.IsWorld");
                  msg = msg.replaceAll("&", "§");
                  msg = msg.replaceAll("%prefix%", "" + prefix + "");
                  p.sendMessage(msg);
                } else {
                  p.sendMessage(prefix + " §cError Welt existiert schon");
                }
              }
            } catch (SQLException e) {
              e.printStackTrace();
            }
          });

        } else {
          if (cfg2.contains("Commands.Createserver.WrongSyntax")) {
            String msg = cfg2.getString("Commands.Createworld.WrongSyntax");
            msg = msg.replaceAll("&", "§");
            msg = msg.replaceAll("%prefix%", "" + prefix + "");
            p.sendMessage(msg);
          } else {
            p.sendMessage(prefix + " §cFalsche Syntax. Bitte benutze /createserverworld <Weltname> <Environment (normal | nether | end)> <Type (normal | flat | amplified | bigbiome)>");
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
