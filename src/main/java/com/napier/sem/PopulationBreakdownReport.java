package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The population breakdown report (UC4).
 * <p>
 * For every continent, region or country it gets the total number of people
 * and how many of them live in cities. The PopulationBreakdown class works out the rest.
 * <p>
 * About the SQL: if we joined country straight onto city, each country would show up once
 * for every city it has and its population would get added up loads of times.
 * So we add up the cities for each country first (the bit in brackets) and join that on instead.
 * LEFT JOIN keeps countries with no cities, and COALESCE turns their missing number into 0.
 */
public class PopulationBreakdownReport {

    /** The link to the database. If it's null we can't get anything. */
    private Connection con;

    /**
     * Makes a new breakdown report.
     *
     * @param con the database connection (null means no database)
     */
    public PopulationBreakdownReport(Connection con) {
        this.con = con;
    }

    /**
     * Gets one line for every continent, in A to Z order.
     * <p>
     * Continent is stored as an ENUM which MySQL sorts in its own order, not A to Z.
     * CAST(... AS CHAR) turns it into normal text so it sorts properly.
     *
     * @return the continent lines (empty if there's no database)
     */
    public List<PopulationBreakdown> getContinentBreakdown() {
        String sql = "SELECT CAST(country.Continent AS CHAR) AS PlaceName, "
                + "SUM(country.Population) AS Total, "
                + "COALESCE(SUM(cities.CityPeople), 0) AS InCities "
                + "FROM country "
                + "LEFT JOIN (SELECT CountryCode, SUM(Population) AS CityPeople "
                + "FROM city GROUP BY CountryCode) AS cities "
                + "ON cities.CountryCode = country.Code "
                + "GROUP BY country.Continent "
                + "ORDER BY PlaceName";
        return runReport(sql);
    }

    /**
     * Gets one line for every region, in A to Z order.
     *
     * @return the region lines (empty if there's no database)
     */
    public List<PopulationBreakdown> getRegionBreakdown() {
        String sql = "SELECT country.Region AS PlaceName, "
                + "SUM(country.Population) AS Total, "
                + "COALESCE(SUM(cities.CityPeople), 0) AS InCities "
                + "FROM country "
                + "LEFT JOIN (SELECT CountryCode, SUM(Population) AS CityPeople "
                + "FROM city GROUP BY CountryCode) AS cities "
                + "ON cities.CountryCode = country.Code "
                + "GROUP BY country.Region "
                + "ORDER BY PlaceName";
        return runReport(sql);
    }

    /**
     * Gets one line for every country, in A to Z order.
     * We group by the code as well so two countries can never get mixed up.
     *
     * @return the country lines (empty if there's no database)
     */
    public List<PopulationBreakdown> getCountryBreakdown() {
        String sql = "SELECT country.Name AS PlaceName, "
                + "SUM(country.Population) AS Total, "
                + "COALESCE(SUM(cities.CityPeople), 0) AS InCities "
                + "FROM country "
                + "LEFT JOIN (SELECT CountryCode, SUM(Population) AS CityPeople "
                + "FROM city GROUP BY CountryCode) AS cities "
                + "ON cities.CountryCode = country.Code "
                + "GROUP BY country.Code, country.Name "
                + "ORDER BY PlaceName";
        return runReport(sql);
    }

    /**
     * Runs one of the SQL queries above and turns each row into a PopulationBreakdown.
     *
     * @param sql the SQL to run
     * @return one line for each place
     */
    private List<PopulationBreakdown> runReport(String sql) {
        List<PopulationBreakdown> lines = new ArrayList<PopulationBreakdown>();

        if (con == null) {
            System.out.println("No database connection");
            return lines;
        }

        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("PlaceName");
                long total = rs.getLong("Total");
                long inCities = rs.getLong("InCities");
                lines.add(new PopulationBreakdown(name, total, inCities));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Could not get population breakdown: " + e.getMessage());
        }

        return lines;
    }
}
