package com.napier.sem;

public class App {
    public static void main(String[] args) {
        // Create new Database Connector
        DatabaseConnector con = new DatabaseConnector();
        // Connect to database
        con.connect();

        // ... your application logic goes here ...

        // Disconnect from database
        System.out.println("Test");
        con.disconnect();
    }
}