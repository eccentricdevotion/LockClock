package me.eccentric_nz.lockclock;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

public class LockClock extends JavaPlugin {

    public String pluginName;
    LockClockDatabase service;
    private final HashMap<String, Scoreboard> scoreboards = new HashMap<String, Scoreboard>();
    private final HashMap<String, Integer> addTracker = new HashMap<String, Integer>();
    private final HashMap<String, String> msgTracker = new HashMap<String, String>();
    private final HashMap<String, String> doubleChestTracker = new HashMap<String, String>();
    private final List<String> unlockTracker = new ArrayList<String>();
    private final List<Material> lockables = new ArrayList<Material>();

    @Override
    public void onDisable() {
        // TODO: Place any custom disable code here.
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        PluginManager pm = getServer().getPluginManager();
        String plug = getConfig().getString("plugin_name");
        pluginName = ChatColor.GOLD + "[" + plug + "]" + ChatColor.RESET + " ";
        pm.registerEvents(new LockClockListener(this), this);
        getCommand("lockclock").setExecutor(new LockClockCommand(this));
        getCommand("lockmsg").setExecutor(new LockClockCommand(this));
        getCommand("unlock").setExecutor(new LockClockCommand(this));
        getCommand("clock").setExecutor(new LockClockCommand(this));
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new LockClockRunnable(this), 10L, 8L);
        service = LockClockDatabase.getInstance();
        try {
            String path = getDataFolder() + File.separator + "Locks.db";
            service.setConnection(path);
            service.createTable();
        } catch (Exception e) {
            debug("Connection and Table Error: " + e);
        }
        for (String s : getConfig().getStringList("lockables")) {
            try {
                lockables.add(Material.valueOf(s));
            } catch (IllegalArgumentException e) {
                getServer().getConsoleSender().sendMessage(pluginName + ChatColor.RED + s + " is not a valid lockable block!");
            }
        }
    }

    public int getTime(String world) {
        Long ticks = getServer().getWorld(world).getTime();
        int hours = (int) (ticks / 1000 + 6) * 100;
        if (hours > 2300) {
            hours -= 2400;
        }
        int minutes = (int) ((ticks % 1000) * 60 / 1000);
        return hours + minutes;
    }

    public String getTime(long ticks) {
        int hours = (int) (ticks / 1000 + 6) * 100;
        if (hours > 2300) {
            hours -= 2400;
        }
        int minutes = (int) ((ticks % 1000) * 60 / 1000);
        return String.format("%d", hours + minutes);
    }

    public HashMap<String, Scoreboard> getScoreboards() {
        return scoreboards;
    }

    public HashMap<String, Integer> getAddTracker() {
        return addTracker;
    }

    public HashMap<String, String> getMsgTracker() {
        return msgTracker;
    }

    public HashMap<String, String> getDoubleChestTracker() {
        return doubleChestTracker;
    }

    public List<String> getUnlockTracker() {
        return unlockTracker;
    }

    public List<Material> getLockables() {
        return lockables;
    }

    public void debug(Object o) {
        if (getConfig().getBoolean("debug") == true) {
            System.out.println("[LockClock Debug] " + o);
        }
    }
}
