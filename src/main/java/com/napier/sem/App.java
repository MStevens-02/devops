package com.napier.sem;

public class App {
    public static void main(String[] args) {
        // Create new Database Connector
        DatabaseConnector con = new DatabaseConnector();
        // Connect to database
        con.connect();

        // ... your application logic goes here ...
        CountryReport country =  new CountryReport(con.getConnection());
        country.generatereport();

        // Disconnect from database
        System.out.println("Test");
        con.disconnect();
    }
}