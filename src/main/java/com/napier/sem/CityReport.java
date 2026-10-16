package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// Create a public class called CityReport to store the city report methods
public class CityReport {
    private final Connection con;

    // Create a base query that joins city and country to get the country name
    private static final String BASE_QUERY =
            "SELECT city.ID, city.Name, city.CountryCode, country.Name AS CountryName, "
                    + "city.District, city.Population "
                    + "FROM city JOIN country ON city.CountryCode = country.Code ";

    // Create an order by so every report goes from largest to smallest population
    private static final String ORDER_BY = "ORDER BY city.Population DESC ";

    // Create a filter for each of the five scopes
    private static final String CONTINENT_FILTER = "WHERE country.Continent = ? ";
    private static final String REGION_FILTER = "WHERE country.Region = ? ";
    private static final String COUNTRY_FILTER = "WHERE country.Name = ? ";
    private static final String DISTRICT_FILTER = "WHERE city.District = ? ";

    // Create a constructor to store the database connection
    public CityReport(Connection con){
        this.con = con;
    }

    // Create a method to return all the cities in the world
    public ArrayList<City> getAllCitiesInWorld(){
        return runQuery(BASE_QUERY + ORDER_BY);
    }

    // Create a method to return all the cities in a continent
    public ArrayList<City> getAllCitiesInContinent(String continent){
        checkLocation(continent);
        return runQuery(BASE_QUERY + CONTINENT_FILTER + ORDER_BY, continent);
    }

    // Create a method to return all the cities in a region
    public ArrayList<City> getAllCitiesInRegion(String region){
        checkLocation(region);
        return runQuery(BASE_QUERY + REGION_FILTER + ORDER_BY, region);
    }

    // Create a method to return all the cities in a country
    public ArrayList<City> getAllCitiesInCountry(String country){
        checkLocation(country);
        return runQuery(BASE_QUERY + COUNTRY_FILTER + ORDER_BY, country);
    }

    // Create a method to return all the cities in a district
    public ArrayList<City> getAllCitiesInDistrict(String district){
        checkLocation(district);
        return runQuery(BASE_QUERY + DISTRICT_FILTER + ORDER_BY, district);
    }

    // Create a method to return the top N cities in the world
    public ArrayList<City> getTopCitiesInWorld(int n) {
        checkN(n);
        return runQuery(BASE_QUERY + ORDER_BY + "LIMIT ?", n);
    }

    // Create a method to return the top N cities in a continent
    public ArrayList<City> getTopCitiesInContinent(String continent, int n) {
        checkLocation(continent);
        checkN(n);
        return runQuery(BASE_QUERY + CONTINENT_FILTER + ORDER_BY + "LIMIT ?", continent, n);
    }

    // Create a method to return the top N cities in a region
    public ArrayList<City> getTopCitiesInRegion(String region, int n) {
        checkLocation(region);
        checkN(n);
        return runQuery(BASE_QUERY + REGION_FILTER + ORDER_BY + "LIMIT ?", region, n);
    }

    // Create a method to return the top N cities in a country
    public ArrayList<City> getTopCitiesInCountry(String country, int n) {
        checkLocation(country);
        checkN(n);
        return runQuery(BASE_QUERY + COUNTRY_FILTER + ORDER_BY + "LIMIT ?", country, n);
    }

    // Create a method to return the top N cities in a district
    public ArrayList<City> getTopCitiesInDistrict(String district, int n) {
        checkLocation(district);
        checkN(n);
        return runQuery(BASE_QUERY + DISTRICT_FILTER + ORDER_BY + "LIMIT ?", district, n);
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

    // Create a method to run a query and turn each row into a City
    private ArrayList<City> runQuery(String sql, Object... params){
        ArrayList<City> cities = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(sql)){
            for (int i = 0; i < params.length; i++){
                stmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = stmt.executeQuery()){
                while (rs.next()) {
                    City city = new City();
                    city.setId(rs.getLong("ID"));
                    city.setName(rs.getString("Name"));
                    city.setCountryCode(rs.getString("CountryCode"));
                    city.setCountryName(rs.getString("CountryName"));
                    city.setDistrict(rs.getString("District"));
                    city.setPopulation(rs.getLong("Population"));
                    cities.add(city);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to get city report: " + e.getMessage());
        }
        return cities;
    }

    // Create a method to print the city report columns
    public void printCities(ArrayList<City> cities) {
        if (cities == null || cities.isEmpty()) {
            System.out.println("No cities found.");
            return;
        }
        System.out.println(String.format("%-35s %-45s %-25s %15s",
                "Name", "Country", "District", "Population"));
        for (City city : cities) {
            System.out.println(String.format("%-35s %-45s %-25s %,15d",
                    city.getName(), city.getCountryName(),
                    city.getDistrict(), city.getPopulation()));
        }
    }
}