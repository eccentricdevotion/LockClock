/*
 *  Copyright 2016 eccentric_nz.
 */
package me.eccentric_nz.lockclock;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;

/**
 *
 * @author eccentric_nz
 */
public class LockClockDoorCloser implements Runnable {

    private final LockClock plugin;

    public LockClockDoorCloser(LockClock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        LockClockAllLocks locks = new LockClockAllLocks(plugin);
        if (locks.resultSet()) {
            for (LockClockAllLocks.LockData ld : locks.getData()) {
                Material m = ld.getLocation().getBlock().getType();
                if (plugin.getDoors().contains(m)) {
                    long now = ld.getLocation().getWorld().getTime();
                    if (now > ld.getStart() && now < ld.getEnd()) {
                        BlockState state = ld.getLocation().getBlock().getState();
                        Door door = (Door) state.getData();
                        if (!door.isTopHalf() && door.isOpen()) {
                            Openable o = (Openable) state.getData();
                            o.setOpen(false);
                            state.setData((MaterialData) o);
                            state.update();
                        }
                    }
                }
            }
        }
    }
}
