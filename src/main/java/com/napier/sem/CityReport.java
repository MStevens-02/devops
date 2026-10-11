package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// Create a public class called CityReport to store the city report methods
public class CityReport {
    private final Connection con;

    // Create a base query that joins city and country to get the city name
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
        return runQuery(BASE_QUERY + CONTINENT_FILTER + ORDER_BY, continent);
    }

    // Create a method to return all the cities in a region
    public ArrayList<City> getAllCitiesInRegion(String region){
        return runQuery(BASE_QUERY + REGION_FILTER + ORDER_BY, region);
    }

    // Create a method to return all the cities in a country
    public ArrayList<City> getAllCitiesInCountry(String country){
        return runQuery(BASE_QUERY + COUNTRY_FILTER + ORDER_BY, country);
    }

    // Create a method to return all the cities in a district
    public ArrayList<City> getAllCitiesInDistrict(String district){
        return runQuery(BASE_QUERY + DISTRICT_FILTER + ORDER_BY, district);
    }

    // Create a method to run a query and turn each row into a City
    private  ArrayList<City> runQuery(String sql, Object... params){
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


}