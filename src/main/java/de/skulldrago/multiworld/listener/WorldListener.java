package de.skulldrago.multiworld.listener;

import de.skulldrago.multiworld.main.Multiworld;
import de.skulldrago.multiworld.mysql.MySQL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WorldListener implements Listener {
  Multiworld plugin = Multiworld.getPlugin();
  MySQL sql = plugin.getMysql();

  @EventHandler
  public void onchangeWorld(PlayerChangedWorldEvent e) {
    Player p = e.getPlayer();
    String worldname = p.getWorld().getName();
    p.sendMessage(plugin.getPrefix() + "§6Willkommen auf der Welt " + worldname + " !");
    sql.withConnection(conn -> {
      ResultSet rs = null;
      PreparedStatement st = null;
      ResultSet rs2 = null;
      PreparedStatement st2 = null;

      try {
        st2 = conn.prepareStatement("SELECT owner FROM worlds WHERE worldname = '" + worldname + "'");
        rs2 = st2.executeQuery();

        if (rs2.isBeforeFirst()) {
          rs2.next();
          String Owner = rs2.getString("owner");
          p.sendMessage(plugin.getPrefix() + "§6Dies ist die Welt vom Spieler " + Owner + ".");
        }
      } catch (SQLException e2) {
        e2.printStackTrace();
      }

      try {
        st = conn.prepareStatement("SELECT locked, owner FROM worlds WHERE owner = '" + p.getName() + "'");
        rs = st.executeQuery();

        if (rs.isBeforeFirst()) {
          rs.next();
          String locked = rs.getString("locked");

          if (locked.equals("false")) {
            locked = "true";
            sql.queryUpdate("UPDATE worlds SET locked = '" + locked + "' WHERE owner = '" + p.getName() + "'");
          }
        }
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    });
  }

  @EventHandler
  public void onlogin(PlayerJoinEvent e) {
    Player p = e.getPlayer();
    sql.withConnection(conn -> {

      try (PreparedStatement st = conn.prepareStatement("SELECT max, vmax FROM worldplayers WHERE name = '" + p.getName() + "'");
           ResultSet rs = st.executeQuery()) {

        int maxworld = 0;
        int maxvworld = 0;

        if (rs.isBeforeFirst()) {
          rs.next();
          maxworld = rs.getInt("max");
          maxvworld = rs.getInt("vmax");

          if (p.hasPermission("Multiworld.g1")) {

            int option = plugin.getConfig().getInt("Multiworld.g1");
            int voption = plugin.getConfig().getInt("Multiworld.v1");

            if (!(maxworld == option)) {
              sql.queryUpdate("UPDATE worldplayers SET max = '" + option + "' WHERE name = '" + p.getName() + "'");
            }

            if (!(maxvworld == voption)) {
              sql.queryUpdate("UPDATE worldplayers SET vmax = '" + voption + "' WHERE name = '" + p.getName() + "'");
            }
          } else if (p.hasPermission("Multiworld.g2")) {

            int option = plugin.getConfig().getInt("Multiworld.g2");
            int voption = plugin.getConfig().getInt("Multiworld.v2");

            if (!(maxworld == option)) {
              sql.queryUpdate("UPDATE worldplayers SET max = '" + option + "' WHERE name = '" + p.getName() + "'");
            }

            if (!(maxvworld == voption)) {
              sql.queryUpdate("UPDATE worldplayers SET vmax = '" + voption + "' WHERE name = '" + p.getName() + "'");
            }
          } else if (p.hasPermission("Multiworld.g3")) {

            int option = plugin.getConfig().getInt("Multiworld.g3");
            int voption = plugin.getConfig().getInt("Multiworld.v3");

            if (!(maxworld == option)) {
              sql.queryUpdate("UPDATE worldplayers SET max = '" + option + "' WHERE name = '" + p.getName() + "'");
            }

            if (!(maxvworld == voption)) {
              sql.queryUpdate("UPDATE worldplayers SET vmax = '" + voption + "' WHERE name = '" + p.getName() + "'");
            }
          }
        } else {
          int option = 0;
          int voption = 0;
          int anzahl = 0;
          int vanzahl = 0;
          if (p.hasPermission("Multiworld.g1")) {
            option = plugin.getConfig().getInt("Multiworld.g1");
            voption = plugin.getConfig().getInt("Multiworld.v1");

          } else if (p.hasPermission("Multiworld.g2")) {
            option = plugin.getConfig().getInt("Multiworld.g2");
            voption = plugin.getConfig().getInt("Multiworld.v3");

          } else if (p.hasPermission("Multiworld.g3")) {
            option = plugin.getConfig().getInt("Multiworld.g3");
            voption = plugin.getConfig().getInt("Multiworld.v3");
          }
          sql.queryUpdate("INSERT INTO worldplayers (name, max, numbers, vmax, vnumbers) VALUES ('" + p.getName() + "', '" + option + "', '" + anzahl + "', '" + voption + "', '" + vanzahl + "')");
        }
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    });
  }
}
