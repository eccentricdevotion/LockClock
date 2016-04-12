/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.lockclock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author eccentric_nz
 */
public class LockClockQuery {

    private final LockClockDatabase service = LockClockDatabase.getInstance();
    private final Connection connection = service.getConnection();
    private final LockClock plugin;

    public LockClockQuery(LockClock plugin) {
        this.plugin = plugin;
    }

    public int doSyncInsert(HashMap<String, Object> data) {
        PreparedStatement ps = null;
        ResultSet idRS = null;
        String fields;
        String questions;
        StringBuilder sbf = new StringBuilder();
        StringBuilder sbq = new StringBuilder();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            sbf.append(entry.getKey()).append(",");
            sbq.append("?,");
        }
        fields = sbf.toString().substring(0, sbf.length() - 1);
        questions = sbq.toString().substring(0, sbq.length() - 1);
        try {
            ps = connection.prepareStatement("INSERT INTO locks (" + fields + ") VALUES (" + questions + ")", PreparedStatement.RETURN_GENERATED_KEYS);
            int i = 1;
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (entry.getValue().getClass().equals(String.class)) {
                    ps.setString(i, entry.getValue().toString());
                } else {
                    ps.setLong(i, Long.parseLong(entry.getValue().toString()));
                }
                i++;
            }
            data.clear();
            ps.executeUpdate();
            idRS = ps.getGeneratedKeys();
            return (idRS.next()) ? idRS.getInt(1) : -1;
        } catch (SQLException e) {
            plugin.debug("Insert error for locks! " + e.getMessage());
            return -1;
        } finally {
            try {
                if (idRS != null) {
                    idRS.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                plugin.debug("Insert close error for locks! " + e.getMessage());
            }
        }
    }

    public void updateLockLocation(String data, int id) {
        Statement statement = null;
        String query = "UPDATE locks SET location = '" + data + "' WHERE id = " + id;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            plugin.debug("Location update error for locks [location]! " + e.getMessage());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                plugin.debug("Location close error for locks [location]! " + e.getMessage());
            }
        }
    }

    public void updateLockMessage(String data, String location) {
        Statement statement = null;
        String query = "UPDATE locks SET message = '" + data + "' WHERE location = '" + location + "'";
        try {
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            plugin.debug("Mesage update error for locks [message]! " + e.getMessage());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                plugin.debug("Message close error for locks [message]! " + e.getMessage());
            }
        }
    }

    public void updateWarning(int data, int id) {
        Statement statement = null;
        String query = "UPDATE locks SET warn = " + data + " WHERE id = " + id;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            plugin.debug("Location update error for locks [warn]! " + e.getMessage());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                plugin.debug("Location close error for locks [warn]! " + e.getMessage());
            }
        }
    }

    public void deleteLock(int id) {
        Statement statement = null;
        String query = "DELETE FROM locks WHERE id = " + id;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            plugin.debug("Delete error for locks! " + e.getMessage());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                plugin.debug("Delete close error for locks! " + e.getMessage());
            }
        }
    }

    public void deleteLock(String loc) {
        Statement statement = null;
        String query = "DELETE FROM locks WHERE location = " + loc;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            plugin.debug("Delete error for locks! " + e.getMessage());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                plugin.debug("Delete close error for locks! " + e.getMessage());
            }
        }
    }
}
