package com.napier.sem;

// Create a public class called CityReport to store the city report methods
public class CityReport {

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

}