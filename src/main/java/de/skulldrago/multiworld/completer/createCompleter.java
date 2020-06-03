package de.skulldrago.multiworld.completer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class createCompleter implements TabCompleter {

  @Override
  public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

    List<String> worldenv = new ArrayList<>();
    worldenv.add("normal");
    worldenv.add("nether");
    worldenv.add("end");

    List<String> worldtype = new ArrayList<>();
    worldtype.add("normal");
    worldtype.add("flat");
    worldtype.add("amplified");
    worldtype.add("bigbiome");

    if (args.length == 1 && args[0].equalsIgnoreCase("create")) {
      return worldenv;
    } else if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
      return worldtype;
    } else {
      return null;
    }
  }
}
