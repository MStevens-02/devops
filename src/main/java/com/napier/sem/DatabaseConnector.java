package com.napier.sem;

import java.sql.*;

public class DatabaseConnector {

    private Connection con = null;

    /**
     * Connects using the Docker settings (used by App inside Docker).
     */
    public void connect() {
        connect("db:3306", 30000);
    }

    /**
     * Connects to the database at the given location.
     * location = where the database is, e.g. "db:3306" or "localhost:33060"
     * delay = how long to wait (in milliseconds) before each attempt
     */
    public void connect(String location, int delay) {
        try {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                // Wait a bit for db to start
                Thread.sleep(delay);
                // Connect to database
                con = DriverManager.getConnection(
                        "jdbc:mysql://" + location + "/world?allowPublicKeyRetrieval=true&useSSL=false",
                        "root", "livelongandprosper"
                );
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " + i);
                System.out.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    public void disconnect() {
        if (con != null) {
            try {
                con.close();
            } catch (Exception e) {
                System.out.println("Error closing connection to database");
            }
        }
    }

    public Connection getConnection() {
        return con;
    }
}