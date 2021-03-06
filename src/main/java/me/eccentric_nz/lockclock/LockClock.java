package me.eccentric_nz.lockclock;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

public class LockClock extends JavaPlugin {

    private String pluginName;
    LockClockDatabase service;
    private final HashMap<UUID, Scoreboard> scoreboards = new HashMap<UUID, Scoreboard>();
    private final HashMap<UUID, Integer> addTracker = new HashMap<UUID, Integer>();
    private final HashMap<UUID, String> msgTracker = new HashMap<UUID, String>();
    private final HashMap<UUID, String> doubleChestTracker = new HashMap<UUID, String>();
    private final List<UUID> unlockTracker = new ArrayList<UUID>();
    private final List<UUID> warnToggleTracker = new ArrayList<UUID>();
    private final List<Material> lockables = new ArrayList<Material>();
    private final List<Material> doors = Arrays.asList(Material.ACACIA_DOOR, Material.BIRCH_DOOR, Material.DARK_OAK_DOOR, Material.IRON_DOOR_BLOCK, Material.JUNGLE_DOOR, Material.SPRUCE_DOOR, Material.WOODEN_DOOR);

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        try {
            service.connection.close();
        } catch (SQLException e) {
            System.err.println("[LockClock] Could not close database connection: " + e);
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        new LockClockConfig(this).checkConfig();
        PluginManager pm = getServer().getPluginManager();
        service = LockClockDatabase.getInstance();
        try {
            String path = getDataFolder() + File.separator + "Locks.db";
            service.setConnection(path);
            service.createTable();
        } catch (Exception e) {
            debug("Connection and Table Error: " + e);
        }
        // update database add and populate uuid fields
        if (!getConfig().getBoolean("uuid_conversion_done")) {
            LockClockUUIDConverter uc = new LockClockUUIDConverter(this);
            if (!uc.convert()) {
                // conversion failed
                System.err.println("[LockClock]" + ChatColor.RED + "UUID conversion failed, disabling...");
                pm.disablePlugin(this);
                return;
            } else {
                getConfig().set("uuid_conversion_done", true);
                saveConfig();
                System.out.println("[LockClock] UUID conversion successful :)");
            }
        }
        String plug = getConfig().getString("plugin_name");
        pluginName = ChatColor.GOLD + "[" + plug + "]" + ChatColor.RESET + " ";
        pm.registerEvents(new LockClockListener(this), this);
        LockClockCommand lockClockCommand = new LockClockCommand(this);
        getCommand("lockclock").setExecutor(lockClockCommand);
        getCommand("lockmsg").setExecutor(lockClockCommand);
        getCommand("unlock").setExecutor(lockClockCommand);
        getCommand("clock").setExecutor(lockClockCommand);
        getCommand("warn_toggle").setExecutor(lockClockCommand);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new LockClockRunnable(this), 10L, 8L);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new LockClockDoorCloser(this), 20L, 60L);
        if (getConfig().getInt("warn.time") > 0) {
            getServer().getScheduler().scheduleSyncRepeatingTask(this, new LockClockDoorWarner(this), 20L, 600L);
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

    public String getPluginName() {
        return pluginName;
    }

    public HashMap<UUID, Scoreboard> getScoreboards() {
        return scoreboards;
    }

    public HashMap<UUID, Integer> getAddTracker() {
        return addTracker;
    }

    public HashMap<UUID, String> getMsgTracker() {
        return msgTracker;
    }

    public HashMap<UUID, String> getDoubleChestTracker() {
        return doubleChestTracker;
    }

    public List<UUID> getUnlockTracker() {
        return unlockTracker;
    }

    public List<UUID> getWarnToggleTracker() {
        return warnToggleTracker;
    }

    public List<Material> getLockables() {
        return lockables;
    }

    public List<Material> getDoors() {
        return doors;
    }

    public void debug(Object o) {
        if (getConfig().getBoolean("debug") == true) {
            System.out.println("[LockClock Debug] " + o);
        }
    }
}
