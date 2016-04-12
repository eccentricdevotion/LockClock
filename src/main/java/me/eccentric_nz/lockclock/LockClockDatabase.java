/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.lockclock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
            String queryLocks = "CREATE TABLE IF NOT EXISTS locks (id INTEGER PRIMARY KEY NOT NULL, location TEXT, start INTEGER, end INTEGER, message TEXT DEFAULT '', uuid TEXT DEFAULT '', player TEXT DEFAULT '', warn INTEGER DEFAULT '1')";
            statement.executeUpdate(queryLocks);
            statement.close();
            // update locks if there is no uuid column
            String queryUUID = "SELECT sql FROM sqlite_master WHERE tbl_name = 'locks' AND sql LIKE '%uuid TEXT%'";
            ResultSet rsUUID = statement.executeQuery(queryUUID);
            if (!rsUUID.next()) {
                String queryAlterU = "ALTER TABLE locks ADD uuid TEXT DEFAULT ''";
                statement.executeUpdate(queryAlterU);
                System.out.println("[LockClock] Adding UUID field to database!");
            }
            // update locks if there is no warn column
            String queryWarn = "SELECT sql FROM sqlite_master WHERE tbl_name = 'locks' AND sql LIKE '%warn INTEGER%'";
            ResultSet rsWarn = statement.executeQuery(queryWarn);
            if (!rsWarn.next()) {
                String queryAlterW = "ALTER TABLE locks ADD warn INTEGER DEFAULT '1'";
                statement.executeUpdate(queryAlterW);
                System.out.println("[LockClock] Adding warn field to database!");
            }
        } catch (SQLException e) {
            System.err.println("[LockClock] Create table error: " + e);
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Clone is not allowed.");
    }
}
