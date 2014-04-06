package me.eccentric_nz.lockclock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LockClockListener implements Listener {

    private final LockClock plugin;
    private final List<BlockFace> faces = Arrays.asList(new BlockFace[]{BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.EAST});

    public LockClockListener(LockClock plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (player.hasPermission("lockclock.clock")) {
            UUID uuid = player.getUniqueId();
            if (!plugin.getScoreboards().containsKey(uuid)) {
                plugin.getScoreboards().put(uuid, new LockClockScoreboard(plugin, player).getScoreboard());
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLeave(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (plugin.getScoreboards().containsKey(uuid)) {
            plugin.getScoreboards().remove(uuid);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        Block b = event.getClickedBlock();
        if (b != null) {
            // is the block a door?
            if (b.getType().equals(Material.IRON_DOOR_BLOCK) || b.getType().equals(Material.WOODEN_DOOR)) {
                // is it the top or bottom of the door?
                if (b.getData() >= 8) {
                    b = b.getRelative(BlockFace.DOWN);
                }
            }
            UUID uuid = event.getPlayer().getUniqueId();
            if (plugin.getAddTracker().containsKey(uuid)) {
                // is it an allowed block?
                if (!plugin.getLockables().contains(b.getType())) {
                    event.getPlayer().sendMessage(plugin.pluginName + "You are not allowed to lock that kind of block!");
                    plugin.getAddTracker().remove(uuid);
                    return;
                }
                // add clicked block location to database
                new LockClockQuery(plugin).updateLockLocation(b.getLocation().toString(), plugin.getAddTracker().get(uuid));
                // check for double chests
                if (b.getType().equals(Material.CHEST) || b.getType().equals(Material.TRAPPED_CHEST)) {
                    String l = isDoubleChest(b);
                    if (!l.isEmpty()) {
                        // get the original blocks times
                        LockClockLock rs = new LockClockLock(plugin, b.getLocation().toString());
                        // is there a lock?
                        if (rs.resultSet()) {
                            // add a record
                            HashMap<String, Object> set = new HashMap<String, Object>();
                            set.put("location", l);
                            set.put("start", rs.getStart());
                            set.put("end", rs.getEnd());
                            set.put("uuid", uuid.toString());
                            set.put("player", event.getPlayer().getName());
                            new LockClockQuery(plugin).doSyncInsert(set);
                            plugin.getDoubleChestTracker().put(uuid, l);
                        }
                    }
                }
                plugin.getAddTracker().remove(uuid);
                event.getPlayer().sendMessage(plugin.pluginName + "Time lock location saved succesfully.");
                return;
            }
            if (plugin.getMsgTracker().containsKey(uuid)) {
                // add clicked block message to database
                new LockClockQuery(plugin).updateLockMessage(plugin.getMsgTracker().get(uuid), b.getLocation().toString());
                // check for double chest
                if (plugin.getDoubleChestTracker().containsKey(uuid)) {
                    new LockClockQuery(plugin).updateLockMessage(plugin.getMsgTracker().get(uuid), plugin.getDoubleChestTracker().get(uuid));
                    plugin.getDoubleChestTracker().remove(uuid);
                }
                plugin.getMsgTracker().remove(uuid);
                event.getPlayer().sendMessage(plugin.pluginName + "Time lock message saved succesfully.");
                return;
            }
            LockClockLock rs = new LockClockLock(plugin, b.getLocation().toString());
            // is there a lock?
            if (rs.resultSet()) {
                if (plugin.getUnlockTracker().contains(uuid)) {
                    if (rs.getUuid().equals(uuid) || event.getPlayer().isOp()) {
                        plugin.getUnlockTracker().remove(uuid);
                        LockClockQuery lcq = new LockClockQuery(plugin);
                        lcq.deleteLock(rs.getId());
                        // check for double chests
                        if (b.getType().equals(Material.CHEST) || b.getType().equals(Material.TRAPPED_CHEST)) {
                            String l = isDoubleChest(b);
                            if (!l.isEmpty()) {
                                lcq.deleteLock(l);
                            }
                        }
                        event.getPlayer().sendMessage(plugin.pluginName + "Time lock removed succesfully.");
                        plugin.getUnlockTracker().remove(uuid);
                        return;
                    } else {
                        event.getPlayer().sendMessage(plugin.pluginName + "You can only remove your own time locks.");
                        plugin.getUnlockTracker().remove(uuid);
                    }
                }
                // only if not the player whose lock it is
                if (!uuid.equals(rs.getPlayer()) || plugin.getConfig().getBoolean("lock_for_owner")) {
                    // get the time of the event
                    long now = b.getLocation().getWorld().getTime();
                    // get start and end times
                    long start = rs.getStart();
                    long end = rs.getEnd();
                    // is the block locked?
                    if (now >= start && now <= end) {
                        event.setCancelled(true);
                        // if no message set, use the default message
                        String message = (rs.getMessage().isEmpty()) ? String.format(plugin.getConfig().getString("default_message"), b.getType().toString(), plugin.getTime(end)) : rs.getMessage();
                        event.getPlayer().sendMessage(plugin.pluginName + ChatColor.translateAlternateColorCodes('&', message));
                    }
                }
            }
        }
    }

    private String isDoubleChest(Block b) {
        String location = "";
        for (BlockFace f : faces) {
            if (b.getRelative(f).getType().equals(b.getType())) {
                return b.getRelative(f).getLocation().toString();
            }
        }
        return location;
    }
}
