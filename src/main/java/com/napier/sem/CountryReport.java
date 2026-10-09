package com.napier.sem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class CountryReport {

    private static final String BASE_QUERY =
            "SELECT country.Code, country.Name, country.Continent, country.Region, " +
                    "country.Population, country.Capital, city.Name AS CapitalName " +
                    "FROM country " +
                    "LEFT JOIN city ON country.Capital = city.ID " +
                    "LIMIT 10;";

    private final Connection con;

    public CountryReport(Connection con) {
        if (con == null){
            throw new java.lang.NullPointerException("Con argument cannot be null");
        }
        this.con = con;
    }

    public void generatereport() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rset = stmt.executeQuery(BASE_QUERY);
            while (rset.next()) {
                String code = rset.getString("Code");
                String name = rset.getString("Name");
                String continent = rset.getString("Continent");
                String region = rset.getString("Region");
                Integer population = rset.getInt("Population");
                Integer capital = rset.getInt("Capital");
                String CapitalName = rset.getString("CapitalName");

                System.out.println(code);
                System.out.println(name);
                System.out.println(continent);
                System.out.println(region);
                System.out.println(population);
                System.out.println(capital);
                System.out.println(CapitalName);

            }
        } catch (Exception e) {
            System.out.println("Query failed: " + e.getMessage());
        }

    }
}
