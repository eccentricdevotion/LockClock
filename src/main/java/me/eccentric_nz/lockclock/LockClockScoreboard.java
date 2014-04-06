/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.lockclock;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

/**
 *
 * @author eccentric_nz
 */
public class LockClockScoreboard {

    private final LockClock plugin;
    private final Player player;
    private final ScoreboardManager manager;

    public LockClockScoreboard(LockClock plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.manager = plugin.getServer().getScoreboardManager();
    }

    public Scoreboard getScoreboard() {

        Scoreboard board = manager.getNewScoreboard();
        String obj = (player.getName().length() > 11) ? player.getName().substring(0, 10) : player.getName();
        Objective objective = board.registerNewObjective(obj + "Clock", "Time");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("Time");
        Score hm = objective.getScore(plugin.getServer().getOfflinePlayer(ChatColor.GREEN + "Hrs:"));
        hm.setScore(plugin.getTime(player.getLocation().getWorld().getName()));
        player.setScoreboard(board);
        return board;
    }
}
