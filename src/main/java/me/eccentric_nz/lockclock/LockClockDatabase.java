/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.lockclock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author eccentric_nz
 */
public class LockClockDatabase {

    private static final LockClockDatabase instance = new LockClockDatabase();
    public Connection connection = null;
    public Statement statement;

    public static synchronized LockClockDatabase getInstance() {
        return instance;
    }

    public void setConnection(String path) throws Exception {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
    }

    public Connection getConnection() {
        return connection;
    }

    public void createTable() {
        try {
            statement = connection.createStatement();
            String queryLocks = "CREATE TABLE IF NOT EXISTS locks (id INTEGER PRIMARY KEY NOT NULL, location TEXT, start INTEGER, end INTEGER, message TEXT DEFAULT '', player TEXT)";
            statement.executeUpdate(queryLocks);
            statement.close();
        } catch (SQLException e) {
            System.err.println("[LockClock] Create table error: " + e);
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Clone is not allowed.");
    }
}
