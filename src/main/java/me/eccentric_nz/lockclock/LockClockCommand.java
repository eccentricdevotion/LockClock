package me.eccentric_nz.lockclock;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LockClockCommand implements CommandExecutor {

    private final LockClock plugin;

    public LockClockCommand(LockClock plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        if (cmd.getName().equalsIgnoreCase("lockclock")) {
            if (player == null) {
                // console - toggle lock_for_owner setting
                boolean bool = !plugin.getConfig().getBoolean("lock_for_owner");
                plugin.getConfig().set("lock_for_owner", bool);
                sender.sendMessage(plugin.pluginName + "'lock_for_owner' set to " + ((bool) ? "true" : "false"));
                return true;
            } else {
                UUID uuid = player.getUniqueId();
                String name = player.getName();
                if (player.hasPermission("lockclock.lock")) {
                    if (args.length < 2) {
                        return false;
                    }
                    // convert args to long
                    long start = stringToTicks(args[0].toLowerCase());
                    long end = stringToTicks(args[1].toLowerCase());
                    // add a record
                    HashMap<String, Object> set = new HashMap<String, Object>();
                    set.put("start", start);
                    set.put("end", end);
                    set.put("uuid", uuid.toString());
                    set.put("player", name);
                    int key = new LockClockQuery(plugin).doSyncInsert(set);
                    plugin.getAddTracker().put(uuid, key);
                    player.sendMessage(plugin.pluginName + "Click the block you want to time lock!");
                    return true;
                } else {
                    player.sendMessage(plugin.pluginName + "You do not have permission to do that!");
                }
            }
        }
        if (cmd.getName().equalsIgnoreCase("lockmsg")) {
            if (args.length < 1) {
                return false;
            }
            StringBuilder sb = new StringBuilder();
            // build the string
            for (String s : args) {
                sb.append(s).append(" ");
            }
            if (player == null) {
                // console - set default message
                plugin.getConfig().set("default_message", sb.toString().trim());
                sender.sendMessage(plugin.pluginName + "Default time lock message set.");
                return true;
            } else {
                if (player.hasPermission("lockclock.message")) {
                    plugin.getMsgTracker().put(player.getUniqueId(), sb.toString().trim());
                    player.sendMessage(plugin.pluginName + "Click the block you want to set a message for!");
                    return true;
                } else {
                    player.sendMessage(plugin.pluginName + "You do not have permission to do that!");
                }
            }
        }
        if (cmd.getName().equalsIgnoreCase("unlock")) {
            if (player == null) {
                sender.sendMessage(plugin.pluginName + "Command can only be used by a player!");
                return true;
            } else {
                if (player.hasPermission("lockclock.lock")) {
                    plugin.getUnlockTracker().add(player.getUniqueId());
                    player.sendMessage(plugin.pluginName + "Click the block you want to unlock!");
                    return true;
                } else {
                    player.sendMessage(plugin.pluginName + "You do not have permission to do that!");
                }
            }
        }
        if (cmd.getName().equalsIgnoreCase("clock")) {
            if (player == null) {
                sender.sendMessage(plugin.pluginName + "Command can only be used by a player!");
                return true;
            } else {
                if (player.hasPermission("lockclock.clock")) {
                    UUID uuid = player.getUniqueId();
                    if (plugin.getScoreboards().containsKey(uuid)) {
                        player.setScoreboard(plugin.getServer().getScoreboardManager().getMainScoreboard());
                        plugin.getScoreboards().remove(uuid);
                    } else {
                        plugin.getScoreboards().put(uuid, new LockClockScoreboard(plugin, player).getScoreboard());
                    }
                    return true;
                } else {
                    player.sendMessage(plugin.pluginName + "You do not have permission to do that!");
                }
            }
        }
        return false;
    }

    private long stringToTicks(String str) {
        // real world time
        if (str.contains(":")) {
            String[] split = str.split(":");
            long hours = Long.parseLong(split[0]) * 1000 - 6000;
            // add 12 hours if it's pm
            if (hours < 12000 && split[1].contains("pm")) {
                hours += 12000;
            }
            // if it's less than zero add 24 hours
            if (hours < 0) {
                hours += 24000;
            }
            long minutes;
            // strip off the am / pm if necessary
            if (split[1].contains("am") || split[1].contains("pm")) {
                minutes = Long.parseLong(split[1].substring(0, split[1].length() - 2)) / 60 * 1000;
            } else {
                minutes = Long.parseLong(split[1]) / 60 * 1000;
            }
            return hours + minutes;
        } else {
            // ticks
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException e) {
                // who knows
                return 0;
            }
        }
    }
}
