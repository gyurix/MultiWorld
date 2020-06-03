package de.skulldrago.multiworld.mysql;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MySQL {

  private Connection conn;
  private String database;
  private ExecutorService exec = Executors.newSingleThreadExecutor();
  private String host;
  private String password;
  private int port;
  private String user;

  public MySQL() {

    File file = new File("plugins/MultiWorldCore/", "mysql.yml");
    FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

    String db = "database.";
    cfg.addDefault(db + "host", "localhost");
    cfg.addDefault(db + "port", 3306);
    cfg.addDefault(db + "user", "user");
    cfg.addDefault(db + "password", "password");
    cfg.addDefault(db + "database", "database");
    cfg.options().copyDefaults(true);
    try {
      cfg.save(file);
    } catch (IOException e) {
      e.printStackTrace();
    }

    this.host = cfg.getString(db + "host");
    this.port = cfg.getInt(db + "port");
    this.user = cfg.getString(db + "user");
    this.password = cfg.getString(db + "password");
    this.database = cfg.getString(db + "database");
  }

  public void closeConnection() {
    try {
      this.conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      this.conn = null;
    }
  }

  private boolean hasConnection() {
    try {
      return this.conn != null && this.conn.isValid(5000);
    } catch (SQLException e) {
      return false;
    }
  }

  public void openConnection() throws Exception {
    Class.forName("com.mysql.jdbc.Driver");
    this.conn = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.user, this.password);
  }

  public void queryUpdate(String query) {
    exec.submit(() -> {
      if (!hasConnection()) {
        try {
          openConnection();
        } catch (Throwable e) {
          e.printStackTrace();
        }
      }
      try (PreparedStatement st = conn.prepareStatement(query)) {
        st.executeUpdate();
      } catch (SQLException e) {
        Bukkit.getConsoleSender().sendMessage("§c[MySQL] Error at executing querry '" + query + "':");
        e.printStackTrace();
      }
    });
  }

  public void withConnection(Consumer<Connection> action) {
    exec.submit(() -> {
      if (!hasConnection()) {
        try {
          openConnection();
        } catch (Throwable e) {
          e.printStackTrace();
        }
      }
      try {
        action.accept(conn);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    });
  }
}
