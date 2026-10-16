package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * UC4 - the population breakdown report.
 * For every continent, region or country it gets:
 *  - the name
 *  - the total population
 *  - the population living in cities (and the %)
 *  - the population not living in cities (and the %)
 */
public class PopulationBreakdownReport {

    // the connection to the database (we are given this when the class is made)
    private Connection con;

    public PopulationBreakdownReport(Connection con) {
        this.con = con;
    }

    // One row for every continent.
    public List<PopulationBreakdown> getContinentBreakdown() {
        return getBreakdown("country.Continent", "country.Continent");
    }

    // One row for every region.
    public List<PopulationBreakdown> getRegionBreakdown() {
        return getBreakdown("country.Region", "country.Region");
    }

    // One row for every country.
    // We group by the code as well as the name so two countries can never get mixed up.
    public List<PopulationBreakdown> getCountryBreakdown() {
        return getBreakdown("country.Name", "country.Code, country.Name");
    }

    /**
     * Does the real work for all three breakdowns.
     * nameColumn   = the column to show as the name (continent, region or country name)
     * groupColumns = the column(s) to group the countries by
     *
     * These two texts are written by us above (the user never types them),
     * so it is safe to join them into the SQL.
     *
     * Why the sub query?
     * If we joined the country table straight to the city table, each country row
     * would be repeated once for every city, and the country total would be counted
     * too many times. So first we add up the cities for each country (the sub query),
     * and then we join that one number to the country.
     */
    private List<PopulationBreakdown> getBreakdown(String nameColumn, String groupColumns) {
        List<PopulationBreakdown> rows = new ArrayList<PopulationBreakdown>();

        // if there is no connection we can not ask the database anything
        if (con == null) {
            System.out.println("No database connection");
            return rows;
        }

        String sql = "SELECT " + nameColumn + " AS Name, "
                + "SUM(country.Population) AS Total, "
                + "COALESCE(SUM(cityTotals.CityPopulation), 0) AS InCities "
                + "FROM country "
                + "LEFT JOIN (SELECT CountryCode, SUM(Population) AS CityPopulation "
                + "           FROM city GROUP BY CountryCode) AS cityTotals "
                + "ON cityTotals.CountryCode = country.Code "
                + "GROUP BY " + groupColumns + " "
                + "ORDER BY Name";

        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // go through each row that came back
            while (rs.next()) {
                String name = rs.getString("Name");
                long total = rs.getLong("Total");
                long inCities = rs.getLong("InCities");
                rows.add(new PopulationBreakdown(name, total, inCities));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Could not get population breakdown: " + e.getMessage());
        }
        return rows;
    }
}
