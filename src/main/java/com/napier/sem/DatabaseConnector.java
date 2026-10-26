package com.napier.sem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Opens and closes the connection to the world database.
 * <p>
 * The database location can be given when connecting (Lab 07), so the same code works:
 * - in Docker:            connect()  (the same as connect("db:3306", 30000))
 * - on your computer:     connect("localhost:33060", 30000)
 * - in integration tests: connect("localhost:33060", 30000)
 */
public class DatabaseConnector {

    // How many times we try to connect before giving up.
    private static final int RETRIES = 10;

    private Connection con = null;

    /**
     * Connects to the database inside Docker.
     * App.java uses this, so it keeps working without any change.
     */
    public void connect() {
        connect("db:3306", 30000);
    }

    /**
     * Connects to the world database.
     * If the database is not ready yet, it waits and tries again.
     *
     * @param location host and port of the database, e.g. "localhost:33060" or "db:3306"
     * @param delay    how long to wait (in milliseconds) after a failed try
     */
    public void connect(String location, int delay) {
        try {
            // Load the MySQL database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        for (int i = 0; i < RETRIES; ++i) {
            System.out.println("Connecting to database...");
            try {
                con = DriverManager.getConnection(
                        "jdbc:mysql://" + location
                                + "/world?allowPublicKeyRetrieval=true&useSSL=false",
                        "root", "livelongandprosper");
                System.out.println("Successfully connected");
                return;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " + i);
                System.out.println(sqle.getMessage());
            }

            // Only wait when the try failed, so a database that is
            // already running connects straight away.
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Closes the connection to the database, if it is open.
     */
    public void disconnect() {
        if (con != null) {
            try {
                con.close();
            } catch (Exception e) {
                System.out.println("Error closing connection to database");
            }
        }
    }

    /**
     * Gives the open connection, or null if we are not connected.
     */
    public Connection getConnection() {
        return con;
    }
}