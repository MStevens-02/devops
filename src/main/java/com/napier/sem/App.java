package com.napier.sem;

import java.sql.ResultSet;
import java.sql.Statement;

public class App {
    public static void main(String[] args) {
        // Create new Database Connector
        DatabaseConnector con = new DatabaseConnector();
        // Connect to database
        con.connect();
        try {
            Statement stmt = con.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT Name, Population FROM country ORDER BY Population DESC LIMIT 5"
            );

            System.out.println("\n--- Top 5 Most Populous Countries ---");
            while (rs.next()) {
                String name = rs.getString("Name");
                int population = rs.getInt("Population");
                System.out.println(name + ": " + population);
            }

        } catch (Exception e) {
            System.out.println("Query failed: " + e.getMessage());
        }
        // Disconnect from database




        System.out.println("Bob");
        con.disconnect();
    }
}