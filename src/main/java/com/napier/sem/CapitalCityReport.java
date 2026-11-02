package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// Create a public class called CapitalCityReport to store the capital city report methods
public class CapitalCityReport {
    private final Connection con;

    // Create a base query that joins capital city and country to get country name
    private static final String BASE_QUERY =
            "SELECT capitalcity.ID, capitalcity.Name, capitalcity.CountryCode, country.Name AS CountryName, "
                    + "capitalcity.Population "
                    + "FROM capitalcity JOIN country ON capitalcity.CountryCode = country.Code ";

    // Create an order by so every report goes from largest to smallest population
    private static final String ORDER_BY = "ORDER BY capitalcity.Population DESC ";

    // Create a filter for each of the three scopes
    private static final String CONTINENT_FILTER = "WHERE country.Continent = ? ";
    private static final String REGION_FILTER = "WHERE country.Region = ? ";
    private static final String COUNTRY_FILTER = "WHERE country.Name = ? ";

    // Create a constructor to store the database connection
    public CapitalCityReport(Connection con) { this.con = con; }

    // Create a method to return all the capital cities in the world
    public ArrayList<CapitalCity> getAllCapitalCitiesInWorld() { return runQuery (BASE_QUERY + ORDER_BY); }

    // Create a method to return all the capital cities in a continent
    public ArrayList<CapitalCity> getAllCapitalCitiesInContinent(String continent) {
        checkLocation(continent);
        return runQuery(BASE_QUERY + CONTINENT_FILTER + ORDER_BY, continent);
    }

    // Create a method to return all the capital cities in a region
    public ArrayList<CapitalCity> getAllCapitalCitiesInRegion(String region) {
        checkLocation(region);
        return runQuery(BASE_QUERY + REGION_FILTER + ORDER_BY, region);
    }

    // Create a method to return all capital cities in a country
    public ArrayList<CapitalCity> getAllCapitalCitiesInCountry(String country) {
        checkLocation(country);
        return runQuery(BASE_QUERY + COUNTRY_FILTER + ORDER_BY, country);
    }

    // Create a method to return the top N capital cities in the world
    public ArrayList<CapitalCity> getTopCapitalCitiesInWorld(int n) {
        checkN(n);
        return runQuery(BASE_QUERY + ORDER_BY + "LIMIT ?", n);
    }

    // Create a method to return the top N capital cities in a continent
    public ArrayList<CapitalCity> getTopCapitalCitiesInContinent(String continent, int n) {
        checkLocation(continent);
        checkN(n);
        return runQuery(BASE_QUERY + CONTINENT_FILTER + ORDER_BY + "LIMIT ?", continent, n);
    }

    // Create a method to return the top N capital cities in a country
    public ArrayList<CapitalCity> getTopCapitalCitiesInCountry(String country, int n) {
        checkLocation(country);
        checkN(n);
        return runQuery(BASE_QUERY + COUNTRY_FILTER + ORDER_BY + "LIMIT ?", country, n);
    }

    // Create a method to validate N
    private void checkN(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("N must be a positive number: " + n);
        }
    }

    // Create a method to validate a location name
    private void checkLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location name must not be null or blank");
        }
    }

    // Create a method to run a query and turn each row into a Capital City
    private ArrayList<CapitalCity> runQuery(String sql, Object... params) {
        ArrayList<CapitalCity> capitalCities = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CapitalCity capitalCity = new CapitalCity();
                    capitalCity.setId(rs.getLong("ID"));
                    capitalCity.setName(rs.getString("Name"));
                    capitalCity.setCountryCode(rs.getString("CountryCode"));
                    capitalCity.setCountryName(rs.getString("CountryName"));
                    capitalCity.setPopulation(rs.getLong("Population"));
                    capitalCities.add(capitalCity);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to get capital city report: " + e.getMessage());
        }
        return capitalCities;
    }

    // Create a method to print the capital city report columns
    public void printCapitalCities(ArrayList<CapitalCity> capitalCities) {
        if (capitalCities == null || capitalCities.isEmpty()) {
            System.out.println("No capital cities found");
            return;
        }
        System.out.println(String.format("%-35s %-45s %-25s %-15s",
                "Name", "Country", "Population"));
        for (CapitalCity capitalcity : capitalCities) {
            System.out.println(String.format("%-35s %-45s %-25s %-15s",
                    capitalcity.getName(), capitalcity.getCountryName(),
                    capitalcity.getPopulation()));
        }
    }
}
