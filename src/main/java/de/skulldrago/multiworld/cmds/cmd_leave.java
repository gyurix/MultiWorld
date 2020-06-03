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

public class cmd_leave implements CommandExecutor {
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
    if (sender instanceof Player) {
      Player p = (Player) sender;
      if (p.hasPermission("Multiworld.leave")) {
        World w = p.getWorld();

        sql.withConnection(conn -> {
          try (PreparedStatement st = conn.prepareStatement(
                  "SELECT worldname, owner, type FROM worlds WHERE worldname='" + w.getName() + "'");
               ResultSet rs = st.executeQuery();
               PreparedStatement st2 = conn.prepareStatement(
                       "SELECT anumbers, vnumbers FROM worldplayers WHERE name = '" + p.getName() + "'");
               ResultSet rs2 = st2.executeQuery()) {

            if (rs2.next()) {

              int welten = rs2.getInt("numbers");
              int voids = rs2.getInt("vnumbers");

              if (rs.next()) {
                String owner = rs.getString("owner");
                String typ = rs.getString("type");

                if (owner.equals(p.getName())) {
                  for (Player target : Bukkit.getServer().getOnlinePlayers()) {
                    if (target.getWorld().getName().equalsIgnoreCase(w.getName())) {
                      Bukkit.getScheduler().runTask(Multiworld.getPlugin(), () ->
                              target.teleport(Bukkit.getWorld("world").getSpawnLocation()));
                    }
                  }

                  if (!(typ.equals("void"))) {
                    welten = welten - 1;
                  } else {
                    voids = voids - 1;
                  }
                  sql.queryUpdate("DELETE FROM worlds WHERE worldname = '" + w.getName() + "'");
                  sql.queryUpdate(
                          "UPDATE worldplayers SET numbers = '" + welten + "', vnumbers = '" + voids + "'");
                  File deletefolder = w.getWorldFolder();
                  Bukkit.getServer().unloadWorld(w, true);
                  deleteWorld(deletefolder);

                  if (cfg2.contains("Commands.Leaveworld.Finish")) {
                    String msg = cfg2.getString("Commands.Leaveworld.Finish");
                    msg = msg.replaceAll("&", "§");
                    msg = msg.replaceAll("%prefix%", "" + prefix + "");
                    p.sendMessage(msg);
                  } else {
                    p.sendMessage(prefix + " §cDu hast deine Welt erfolgreich verlassen.");
                  }

                } else {
                  if (cfg2.contains("Commands.Leaveworld.OnlyOwner")) {
                    String msg = cfg2.getString("Commands.Leaveworld.OnlyOwner");
                    msg = msg.replaceAll("&", "§");
                    msg = msg.replaceAll("%prefix%", "" + prefix + "");
                    p.sendMessage(msg);
                  } else {
                    p.sendMessage(prefix + " §cNur der Owner der Welt darf dies tun.");
                  }
                }
              }
            } else {
              p.sendMessage("§cError!");
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
