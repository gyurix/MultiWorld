package de.skulldrago.multiworld.files;

import de.skulldrago.multiworld.main.Multiworld;
import de.skulldrago.multiworld.mysql.MySQL;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FileCheckWorld {
  private static Multiworld service = Multiworld.getPlugin();
  private static MySQL sql = service.getMysql();

  File lang = new File("plugins/MultiWorld", "lang_de.yml");
  YamlConfiguration lfg = YamlConfiguration.loadConfiguration(lang);

  public static void checkBackupVoid() {
    File folder = new File("plugins/MultiWorld/backup");
    if (!(folder.exists())) {
      folder.mkdir();
    }
  }

  public static void checkVanillaW() {
    World w = Bukkit.getServer().getWorld("world");
    World n = Bukkit.getServer().getWorld("world_nether");
    World e = Bukkit.getServer().getWorld("world_the_end");
    Multiworld plugin = Multiworld.getPlugin();
    MySQL sql = Multiworld.getPlugin().getMysql();
    sql.withConnection((conn) -> {
      if (w != null) {
        try (PreparedStatement st = conn.prepareStatement("SELECT worldname FROM worlds WHERE worldname = '" + w.getName() + "'");
             ResultSet rs = st.executeQuery();) {
          if (!(rs.isBeforeFirst())) {
            Location loc = w.getSpawnLocation();
            String name = w.getName();
            String owner = "Server";
            WorldType type = w.getWorldType();
            String locked = "false";
            double spawnX = loc.getX();
            double spawnY = loc.getY();
            double spawnZ = loc.getZ();
            float spawnYaw = loc.getYaw();
            float spawnPitch = loc.getPitch();

            sql.queryUpdate("INSERT INTO worlds (worldname, owner, locked, type, spawnx, spawny, spawnz, spawnyaw, spawnpitch) VALUES ('" + name + "', '" + owner + "', '" + locked + "', '" + type + "', '" + spawnX + "', '" + spawnY + "', '" + spawnZ + "', '" + spawnYaw + "', '" + spawnPitch + "')");
          } else {
            Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + " §c" + w.getName() + " befindet sich schon in der Datenbank.");
          }
        } catch (SQLException e1) {
          e1.printStackTrace();
        }

      }
      if (n != null) {
        try (PreparedStatement st = conn.prepareStatement("SELECT worldname FROM worlds WHERE worldname = '" + n.getName() + "'");
             ResultSet rs = st.executeQuery();) {

          if (!(rs.isBeforeFirst())) {
            Location loc = n.getSpawnLocation();
            String name = n.getName();
            String owner = "Server";
            WorldType type = n.getWorldType();
            String locked = "false";
            double spawnX = loc.getX();
            double spawnY = loc.getY();
            double spawnZ = loc.getZ();
            float spawnYaw = loc.getYaw();
            float spawnPitch = loc.getPitch();

            sql.queryUpdate(
                    "INSERT INTO worlds (worldname, owner, locked, type, spawnx, spawny, spawnz, spawnyaw, spawnpitch) VALUES ('" + name + "', '" + owner + "', '" + locked + "', '" + type + "', '" + spawnX + "', '" + spawnY + "', '" + spawnZ + "', '" + spawnYaw + "', '" + spawnPitch + "')");
          } else {
            Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + " §c" + n.getName() + " befindet sich schon in der Datenbank.");
          }
        } catch (SQLException e1) {
          e1.printStackTrace();
        }
      }
      if (e != null) {
        try (PreparedStatement st = conn.prepareStatement("SELECT worldname FROM worlds WHERE worldname = '" + e.getName() + "'");
             ResultSet rs = st.executeQuery();) {
          if (!(rs.isBeforeFirst())) {
            Location loc = e.getSpawnLocation();
            String name = e.getName();
            String owner = "Server";
            WorldType type = e.getWorldType();
            String locked = "false";
            double spawnX = loc.getX();
            double spawnY = loc.getY();
            double spawnZ = loc.getZ();
            float spawnYaw = loc.getYaw();
            float spawnPitch = loc.getPitch();

            sql.queryUpdate(
                    "INSERT INTO worlds (worldname, owner, locked, type, spawnx, spawny, spawnz, spawnyaw, spawnpitch) VALUES ('" + name + "', '" + owner + "', '" + locked + "', '" + type + "', '" + spawnX + "', '" + spawnY + "', '" + spawnZ + "', '" + spawnYaw + "', '" + spawnPitch + "')");
          } else {
            Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + " §c" + e.getName() + " befindet sich schon in der Datenbank.");
          }
        } catch (SQLException e1) {
          e1.printStackTrace();
        }
      }
    });
  }

  public static void checkVoidSource() {
    Multiworld plugin = Multiworld.getPlugin();
    File vfv = new File("plugins/MultiWorld/backup/void");
    World v = Bukkit.getServer().getWorld("void");
    if (v != null) {
      File vd = v.getWorldFolder();
      if (vd.exists()) {
        if (!(vfv.exists())) {
          try {
            vfv.mkdirs();
            FileUtils.copyDirectory(vd, vfv);
            Location loc = v.getSpawnLocation();
            String name = v.getName();
            String owner = "Server";
            WorldType type = v.getWorldType();
            String locked = "false";
            double spawnX = loc.getX();
            double spawnY = loc.getY();
            double spawnZ = loc.getZ();
            float spawnYaw = loc.getYaw();
            float spawnPitch = loc.getPitch();

            sql.queryUpdate("INSERT INTO worlds (worldname, owner, locked, type, spawnx, spawny, spawnz, spawnyaw, spawnpitch) VALUES ('" + name + "', '" + owner + "', '" + locked + "', '" + type + "', '" + spawnX + "', '" + spawnY + "', '" + spawnZ + "', '" + spawnYaw + "', '" + spawnPitch + "')");
          } catch (IOException e) {
            e.printStackTrace();
          }
        } else {
          Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + " §cVoidBackup schon vorhanden");
        }
      } else {
        Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + " §cVoidSource nicht gefunden");
      }
    }

  }

  public static void checkfolder() {
    File folder = new File("plugins/MultiWorld");
    if (!(folder.exists())) {
      folder.mkdir();
    }
  }
}
