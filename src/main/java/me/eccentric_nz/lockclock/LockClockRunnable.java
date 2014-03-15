/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.lockclock;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 *
 * @author eccentric_nz
 */
public class LockClockRunnable implements Runnable {

    private final LockClock plugin;

    public LockClockRunnable(LockClock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (p.hasPermission("lockclock.clock")) {
                if (plugin.getScoreboards().containsKey(p.getName())) {
                    Scoreboard board = plugin.getScoreboards().get(p.getName());
                    if (board != null) {
                        Objective objective = board.getObjective(DisplaySlot.SIDEBAR);
                        objective.getScore(plugin.getServer().getOfflinePlayer(ChatColor.GREEN + "Hrs:")).setScore(plugin.getTime(p.getLocation().getWorld().getName()));
                        p.setScoreboard(board);
                    }
                }
            }
        }
    }
}
