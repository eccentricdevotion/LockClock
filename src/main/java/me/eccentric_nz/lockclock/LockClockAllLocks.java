/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.lockclock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author eccentric_nz
 */
public class LockClockAllLocks {

    private final LockClockDatabase service = LockClockDatabase.getInstance();
    private final Connection connection = service.getConnection();
    private final LockClock plugin;
    private final List<LockData> data = new ArrayList<LockData>();

    public LockClockAllLocks(LockClock plugin) {
        this.plugin = plugin;
    }

    public boolean resultSet() {
        PreparedStatement statement = null;
        ResultSet rs = null;
        String query = "SELECT location, start, end, warn FROM locks";
        try {
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            if (rs.isBeforeFirst()) {
                while (rs.next()) {
                    String l = rs.getString("location");
                    if (!rs.wasNull() && l != null) {
                        LockData ld = new LockData(l, rs.getLong("start"), rs.getLong("end"), rs.getBoolean("warn"));
                        data.add(ld);
                    }
                }
            } else {
                return false;
            }
        } catch (SQLException e) {
            plugin.debug("ResultSet error for locks table! " + e.getMessage());
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                plugin.debug("Error closing locks table! " + e.getMessage());
            }
        }
        return true;
    }

    public List<LockData> getData() {
        return data;
    }

    public class LockData {

        private final Location location;
        private final long start;
        private final long end;
        private final boolean warn;

        public LockData(String location, long start, long end, boolean warn) {
            this.location = getLocationFromBukkitString(location);
            this.start = start;
            this.end = end;
            this.warn = warn;
        }

        public Location getLocation() {
            return location;
        }

        public long getStart() {
            return start;
        }

        public long getEnd() {
            return end;
        }

        public boolean shouldWarn() {
            return warn;
        }
    }

    public Location getLocationFromBukkitString(String string) {
        //Location{world=CraftWorld{name=world},x=0.0,y=0.0,z=0.0,pitch=0.0,yaw=0.0}
        String[] loc_data = string.split(",");
        // w, x, y, z - 0, 1, 2, 3
        String[] wStr = loc_data[0].split("=");
        String[] xStr = loc_data[1].split("=");
        String[] yStr = loc_data[2].split("=");
        String[] zStr = loc_data[3].split("=");
        World w = plugin.getServer().getWorld(wStr[2].substring(0, (wStr[2].length() - 1)));
        if (w == null) {
            return null;
        }
        // Location{world=CraftWorld{name=world},x=1.0000021E7,y=67.0,z=1824.0,pitch=0.0,yaw=0.0}
        double x = (xStr[1].contains("E")) ? Double.valueOf(xStr[1]) : Double.parseDouble(xStr[1]);
        double y = Double.parseDouble(yStr[1]);
        double z = (zStr[1].contains("E")) ? Double.valueOf(zStr[1]) : Double.parseDouble(zStr[1]);
        return new Location(w, x, y, z);
    }
}
