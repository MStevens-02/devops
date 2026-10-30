package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The population lookup (UC5).
 * <p>
 * You tell it what kind of place you want (world, continent, region, country, district or city)
 * and the name of the place, and it tells you how many people live there.
 * Each type of place has its own method with its own SQL in it.
 */
public class PopulationReport {

    /** The link to the database. If it's null we can't look anything up. */
    private Connection con;

    /**
     * Makes a new lookup.
     *
     * @param con the database connection (null means no database)
     */
    public PopulationReport(Connection con) {
        this.con = con;
    }

    /**
     * Looks up how many people live somewhere. This is the main method.
     * <p>
     * Usually you get one answer back, but sometimes you get more.
     * For example there are two cities called Birmingham so you get both of them.
     * If the place doesn't exist you get an empty list.
     *
     * @param type what kind of place: world, continent, region, country, district or city
     * @param name the name of the place, like "France" (not needed for the world)
     * @return a list of answers, empty if nothing was found
     * @throws IllegalArgumentException if the type or name is blank, or the type is made up
     */
    public List<Population> getPopulation(String type, String name) {
        // check we got a type
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Type must not be blank");
        }

        // make the type lower case with no spaces, so "  COUNTRY " turns into "country"
        type = type.trim().toLowerCase();

        // check the type is one we know about
        if (!type.equals("world") && !type.equals("continent") && !type.equals("region")
                && !type.equals("country") && !type.equals("district") && !type.equals("city")) {
            throw new IllegalArgumentException("Unknown type '" + type
                    + "'. Use world, continent, region, country, district or city");
        }

        // everything except the world needs a name
        if (!type.equals("world")) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name must not be blank");
            }
            name = name.trim();
        }

        // if there's no database we can't do anything, so just give back an empty list
        if (con == null) {
            System.out.println("No database connection");
            return new ArrayList<Population>();
        }

        // call the right method for the type
        if (type.equals("world")) {
            return getWorldPopulation();
        } else if (type.equals("continent")) {
            return getContinentPopulation(name);
        } else if (type.equals("region")) {
            return getRegionPopulation(name);
        } else if (type.equals("country")) {
            return getCountryPopulation(name);
        } else if (type.equals("district")) {
            return getDistrictPopulation(name);
        } else {
            return getCityPopulation(name);
        }
    }

    /**
     * Gets the population of the whole world by adding up every country.
     *
     * @return one answer called "World"
     */
    private List<Population> getWorldPopulation() {
        List<Population> answers = new ArrayList<Population>();
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT SUM(Population) AS Total FROM country");
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                answers.add(new Population("World", rs.getLong("Total")));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Could not get world population: " + e.getMessage());
        }
        return answers;
    }

    /**
     * Gets the population of a continent by adding up all the countries in it.
     * <p>
     * The ? in the SQL gets filled in with the name. We do it like this instead of just
     * sticking the name into the SQL, because that would let people do SQL injection.
     * GROUP BY means if the continent doesn't exist we get no rows back at all.
     *
     * @param continent the continent, like "Asia"
     * @return one answer, or an empty list if it wasn't found
     */
    private List<Population> getContinentPopulation(String continent) {
        List<Population> answers = new ArrayList<Population>();
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT Continent, SUM(Population) AS Total FROM country "
                            + "WHERE Continent = ? GROUP BY Continent");
            stmt.setString(1, continent);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                answers.add(new Population(rs.getString("Continent"), rs.getLong("Total")));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Could not get continent population: " + e.getMessage());
        }
        return answers;
    }

    /**
     * Gets the population of a region by adding up all the countries in it.
     *
     * @param region the region, like "Caribbean"
     * @return one answer, or an empty list if it wasn't found
     */
    private List<Population> getRegionPopulation(String region) {
        List<Population> answers = new ArrayList<Population>();
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT Region, SUM(Population) AS Total FROM country "
                            + "WHERE Region = ? GROUP BY Region");
            stmt.setString(1, region);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                answers.add(new Population(rs.getString("Region"), rs.getLong("Total")));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Could not get region population: " + e.getMessage());
        }
        return answers;
    }

    /**
     * Gets the population of a country. The country table already has it so no adding up needed.
     *
     * @param country the country, like "France"
     * @return one answer, or an empty list if it wasn't found
     */
    private List<Population> getCountryPopulation(String country) {
        List<Population> answers = new ArrayList<Population>();
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT Name, Population FROM country WHERE Name = ?");
            stmt.setString(1, country);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                answers.add(new Population(rs.getString("Name"), rs.getLong("Population")));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Could not get country population: " + e.getMessage());
        }
        return answers;
    }

    /**
     * Gets the population of a district by adding up its cities.
     * <p>
     * Some districts have the same name in different countries (there's a "Central" in 7 countries).
     * Adding them all together would be a pointless number, so we give one answer per country,
     * like "Scotland, United Kingdom".
     *
     * @param district the district, like "Scotland"
     * @return one answer for each country that has a district with this name
     */
    private List<Population> getDistrictPopulation(String district) {
        List<Population> answers = new ArrayList<Population>();
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT city.District, country.Name AS Country, SUM(city.Population) AS Total "
                            + "FROM city JOIN country ON city.CountryCode = country.Code "
                            + "WHERE city.District = ? "
                            + "GROUP BY country.Code, country.Name, city.District "
                            + "ORDER BY country.Name");
            stmt.setString(1, district);
            ResultSet rs = stmt.executeQuery();

            // one row for each country
            while (rs.next()) {
                String label = rs.getString("District") + ", " + rs.getString("Country");
                answers.add(new Population(label, rs.getLong("Total")));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Could not get district population: " + e.getMessage());
        }
        return answers;
    }

    /**
     * Gets the population of a city.
     * <p>
     * Some cities have the same name (like Birmingham), so we put the district and country
     * in the name too so you can tell them apart, like "Edinburgh, Scotland, United Kingdom".
     *
     * @param city the city, like "Edinburgh"
     * @return one answer for each city with this name
     */
    private List<Population> getCityPopulation(String city) {
        List<Population> answers = new ArrayList<Population>();
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT city.Name, city.District, country.Name AS Country, city.Population "
                            + "FROM city JOIN country ON city.CountryCode = country.Code "
                            + "WHERE city.Name = ?");
            stmt.setString(1, city);
            ResultSet rs = stmt.executeQuery();

            // one row for each city with this name
            while (rs.next()) {
                String label = rs.getString("Name") + ", " + rs.getString("District")
                        + ", " + rs.getString("Country");
                answers.add(new Population(label, rs.getLong("Population")));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Could not get city population: " + e.getMessage());
        }
        return answers;
    }
}
