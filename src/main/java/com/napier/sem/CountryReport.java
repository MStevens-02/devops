package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The country reports (UC1).
 * <p>
 * There are 6 reports:
 * all countries in the world, in a continent, or in a region,
 * and the top N countries in the world, in a continent, or in a region.
 * Every report goes from the most people to the least.
 * <p>
 * All the SQL uses a LEFT JOIN onto the city table to get the capital's name.
 * It's a LEFT JOIN so countries with no capital (like Antarctica) still show up.
 * If two countries have the same population we sort them by name so the order never changes.
 */
public class CountryReport {

    /** The link to the database. */
    private Connection con;

    /**
     * Makes a new country report.
     *
     * @param con the database connection (can't be null)
     * @throws NullPointerException if the connection is null
     */
    public CountryReport(Connection con) {
        if (con == null) {
            throw new NullPointerException("Connection argument cannot be null");
        }
        this.con = con;
    }

    /**
     * Report 1: every country in the world.
     *
     * @return the countries, most people first
     */
    public List<Country> getAllCountries() {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT country.Code, country.Name, country.Continent, country.Region, "
                            + "country.Population, country.Capital, city.Name AS CapitalName "
                            + "FROM country LEFT JOIN city ON country.Capital = city.ID "
                            + "ORDER BY country.Population DESC, country.Name ASC");
            return getCountries(stmt);
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return new ArrayList<Country>();
        }
    }

    /**
     * Report 2: every country in a continent.
     *
     * @param continent the continent, like "Europe"
     * @return the countries, most people first
     */
    public List<Country> getCountriesByContinent(String continent) {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT country.Code, country.Name, country.Continent, country.Region, "
                            + "country.Population, country.Capital, city.Name AS CapitalName "
                            + "FROM country LEFT JOIN city ON country.Capital = city.ID "
                            + "WHERE country.Continent = ? "
                            + "ORDER BY country.Population DESC, country.Name ASC");
            stmt.setString(1, continent);
            return getCountries(stmt);
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return new ArrayList<Country>();
        }
    }

    /**
     * Report 3: every country in a region.
     *
     * @param region the region, like "Caribbean"
     * @return the countries, most people first
     */
    public List<Country> getCountriesByRegion(String region) {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT country.Code, country.Name, country.Continent, country.Region, "
                            + "country.Population, country.Capital, city.Name AS CapitalName "
                            + "FROM country LEFT JOIN city ON country.Capital = city.ID "
                            + "WHERE country.Region = ? "
                            + "ORDER BY country.Population DESC, country.Name ASC");
            stmt.setString(1, region);
            return getCountries(stmt);
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return new ArrayList<Country>();
        }
    }

    /**
     * Report 4: the top N countries in the world.
     *
     * @param n how many countries to show (has to be 1 or more)
     * @return the N countries with the most people
     */
    public List<Country> getTopCountries(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("N must be 1 or more but was: " + n);
        }
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT country.Code, country.Name, country.Continent, country.Region, "
                            + "country.Population, country.Capital, city.Name AS CapitalName "
                            + "FROM country LEFT JOIN city ON country.Capital = city.ID "
                            + "ORDER BY country.Population DESC, country.Name ASC "
                            + "LIMIT ?");
            stmt.setInt(1, n);
            return getCountries(stmt);
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return new ArrayList<Country>();
        }
    }

    /**
     * Report 5: the top N countries in a continent.
     *
     * @param continent the continent, like "Asia"
     * @param n         how many countries to show (has to be 1 or more)
     * @return the N countries with the most people in that continent
     */
    public List<Country> getTopCountriesByContinent(String continent, int n) {
        if (n < 1) {
            throw new IllegalArgumentException("N must be 1 or more but was: " + n);
        }
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT country.Code, country.Name, country.Continent, country.Region, "
                            + "country.Population, country.Capital, city.Name AS CapitalName "
                            + "FROM country LEFT JOIN city ON country.Capital = city.ID "
                            + "WHERE country.Continent = ? "
                            + "ORDER BY country.Population DESC, country.Name ASC "
                            + "LIMIT ?");
            stmt.setString(1, continent);
            stmt.setInt(2, n);
            return getCountries(stmt);
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return new ArrayList<Country>();
        }
    }

    /**
     * Report 6: the top N countries in a region.
     *
     * @param region the region, like "Western Europe"
     * @param n      how many countries to show (has to be 1 or more)
     * @return the N countries with the most people in that region
     */
    public List<Country> getTopCountriesByRegion(String region, int n) {
        if (n < 1) {
            throw new IllegalArgumentException("N must be 1 or more but was: " + n);
        }
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT country.Code, country.Name, country.Continent, country.Region, "
                            + "country.Population, country.Capital, city.Name AS CapitalName "
                            + "FROM country LEFT JOIN city ON country.Capital = city.ID "
                            + "WHERE country.Region = ? "
                            + "ORDER BY country.Population DESC, country.Name ASC "
                            + "LIMIT ?");
            stmt.setString(1, region);
            stmt.setInt(2, n);
            return getCountries(stmt);
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return new ArrayList<Country>();
        }
    }

    /**
     * Runs a query that's already been set up and turns every row into a Country.
     *
     * @param stmt the query, with all the ? gaps already filled in
     * @return the countries it found
     * @throws SQLException if something goes wrong with the database
     */
    private List<Country> getCountries(PreparedStatement stmt) throws SQLException {
        List<Country> countries = new ArrayList<Country>();
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            // if a country has no capital, getInt gives 0, and wasNull tells us it was really empty
            Integer capitalId = rs.getInt("Capital");
            if (rs.wasNull()) {
                capitalId = null;
            }

            Country country = new Country(
                    rs.getString("Code"),
                    rs.getString("Name"),
                    rs.getString("Continent"),
                    rs.getString("Region"),
                    rs.getInt("Population"),
                    capitalId,
                    rs.getString("CapitalName"));
            countries.add(country);
        }

        rs.close();
        stmt.close();
        return countries;
    }

    /**
     * Prints a report as a table: the title, the column names, a line, then one row per country.
     * It's static because it only prints the list it's given and never needs the database.
     *
     * @param title     the name of the report
     * @param countries the countries to print (can be null or empty)
     */
    public static void printReport(String title, List<Country> countries) {
        System.out.println();
        System.out.println(title);

        // if there's nothing to show just say so
        if (countries == null || countries.isEmpty()) {
            System.out.println("No countries found.");
            return;
        }

        String header = String.format("%-4s %-45s %-15s %-26s %15s  %s",
                "Code", "Name", "Continent", "Region", "Population", "Capital");
        System.out.println(header);
        System.out.println("-".repeat(header.length()));

        for (Country country : countries) {
            // skip any empty spots in the list so one bad one doesn't break the whole report
            if (country == null) {
                continue;
            }
            System.out.println(country.toReportRow());
        }
    }
}
