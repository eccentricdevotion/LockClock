/*
 *  Copyright 2016 eccentric_nz.
 */
package me.eccentric_nz.lockclock;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 *
 * @author eccentric_nz
 */
public class LockClockDoorWarner implements Runnable {

    private final LockClock plugin;

    public LockClockDoorWarner(LockClock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        LockClockAllLocks locks = new LockClockAllLocks(plugin);
        if (locks.resultSet()) {
            for (LockClockAllLocks.LockData ld : locks.getData()) {
                if (ld.shouldWarn()) {
                    Material m = ld.getLocation().getBlock().getType();
                    if (plugin.getDoors().contains(m)) {
                        long now = ld.getLocation().getWorld().getTime();
                        long time = plugin.getConfig().getLong("warn.time") * 20;
                        String message = plugin.getPluginName() + plugin.getConfig().getString("warn.message");
                        if (now > ld.getStart() - time && now < ld.getStart()) {
                            for (Entity e : ld.getLocation().getWorld().getNearbyEntities(ld.getLocation(), 16.0d, 16.0d, 16.0d)) {
                                if (e.getType().equals(EntityType.PLAYER)) {
                                    ((Player) e).sendMessage(message);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
