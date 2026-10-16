package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * UC5 - the population lookup.
 * Give it a type (world, continent, region, country, district or city)
 * and a name, and it gives back the population.
 */
public class PopulationReport {

    // the connection to the database (we are given this when the class is made)
    private Connection con;

    public PopulationReport(Connection con) {
        this.con = con;
    }

    /**
     * The main method.
     * type = one of: world, continent, region, country, district, city
     * name = the area to look up (it is ignored for the world)
     * Gives back a list of answers. The list is empty if nothing was found.
     * Gives back an error if the type or name is not okay.
     */
    public List<Population> getPopulation(String type, String name) {
        // step 1: clean up the type so "  Country " becomes "country"
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Type must not be blank");
        }
        type = type.trim().toLowerCase();

        // step 2: every type except the world needs a name
        if (!type.equals("world") && (name == null || name.trim().isEmpty())) {
            throw new IllegalArgumentException("Name must not be blank");
        }

        // step 3: if there is no connection we can not ask the database anything
        if (con == null) {
            System.out.println("No database connection");
            return new ArrayList<Population>();
        }

        // step 4: call the method for the type
        if (type.equals("world")) {
            return getWorldPopulation();
        } else if (type.equals("continent")) {
            return getAreaPopulation("SELECT SUM(Population) AS Total FROM country WHERE Continent = ?", name.trim());
        } else if (type.equals("region")) {
            return getAreaPopulation("SELECT SUM(Population) AS Total FROM country WHERE Region = ?", name.trim());
        } else if (type.equals("country")) {
            return getAreaPopulation("SELECT Population AS Total FROM country WHERE Name = ?", name.trim());
        } else if (type.equals("district")) {
            return getAreaPopulation("SELECT SUM(Population) AS Total FROM city WHERE District = ?", name.trim());
        } else if (type.equals("city")) {
            return getCityPopulation(name.trim());
        } else {
            throw new IllegalArgumentException(
                    "Unknown type '" + type + "'. Use world, continent, region, country, district or city");
        }
    }

    // World: add up the population of every country.
    private List<Population> getWorldPopulation() {
        List<Population> answers = new ArrayList<Population>();
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT SUM(Population) AS Total FROM country");
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
     * Used for continent, region, country and district.
     * They all give back ONE number called Total, so one method can do them all.
     * The ? in the query is a gap that is filled with the name the user typed.
     * This is safer than joining the text on, because it stops SQL injection.
     * If nothing was found, SUM gives NULL, so we give back an empty list.
     */
    private List<Population> getAreaPopulation(String sql, String name) {
        List<Population> answers = new ArrayList<Population>();
        try {
            // prepare the query and fill in the ? with the name
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, name);

            // run the query
            ResultSet rs = stmt.executeQuery();

            // there is only one row, so we use if instead of a loop
            if (rs.next()) {
                long total = rs.getLong("Total");

                // wasNull() is true if the database value was NULL (nothing found)
                if (!rs.wasNull()) {
                    answers.add(new Population(name, total));
                }
            }

            // always close things when finished
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Could not get population: " + e.getMessage());
        }
        return answers;
    }

    // City: some cities share a name (for example there are several Birminghams),
    // so this can give back more than one answer. We join the country table
    // so we can show which country each city is in.
    private List<Population> getCityPopulation(String cityName) {
        List<Population> answers = new ArrayList<Population>();
        String sql = "SELECT city.Name, city.District, country.Name AS Country, city.Population "
                + "FROM city JOIN country ON city.CountryCode = country.Code "
                + "WHERE city.Name = ?";
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, cityName);
            ResultSet rs = stmt.executeQuery();

            // go through each row that came back
            while (rs.next()) {
                // build a label like "Edinburgh, Scotland, United Kingdom"
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
