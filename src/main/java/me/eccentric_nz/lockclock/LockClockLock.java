/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.lockclock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 *
 * @author eccentric_nz
 */
public class LockClockLock {

    private final LockClockDatabase service = LockClockDatabase.getInstance();
    private final Connection connection = service.getConnection();
    private final LockClock plugin;
    private final String loc;
    private int id;
    private String location;
    private long start;
    private long end;
    private UUID uuid;
    private String player;
    private String message;

    public LockClockLock(LockClock plugin, String loc) {
        this.plugin = plugin;
        this.loc = loc;
    }

    public boolean resultSet() {
        PreparedStatement statement = null;
        ResultSet rs = null;
        String query = "SELECT * FROM locks WHERE location ='" + loc + "'";
        try {
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            if (rs.isBeforeFirst()) {
                while (rs.next()) {
                    this.id = rs.getInt("id");
                    this.location = rs.getString("location");
                    this.start = rs.getLong("start");
                    this.end = rs.getLong("end");
                    this.uuid = UUID.fromString(rs.getString("uuid"));
                    this.player = rs.getString("player");
                    this.message = rs.getString("message");
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

    public int getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
    }
}
