package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates the Country Reports (Use Case 1).
 * <p>
 * There are 6 reports:
 * 1. All countries in the world
 * 2. All countries in a continent
 * 3. All countries in a region
 * 4. Top N countries in the world
 * 5. Top N countries in a continent
 * 6. Top N countries in a region
 * <p>
 * Every report is sorted from the largest population to the smallest.
 */
public class CountryReport {

    // The SQL that every country report starts with.
    // It joins the city table to get the capital's name.
    // LEFT JOIN keeps countries that have no capital (e.g. Antarctica).
    private static final String BASE_QUERY =
            "SELECT country.Code, country.Name, country.Continent, country.Region, "
                    + "country.Population, country.Capital, city.Name AS CapitalName "
                    + "FROM country "
                    + "LEFT JOIN city ON country.Capital = city.ID ";

    // Sort by population, largest first.
    // If two countries have the same population, sort them by name,
    // so the order is always the same.
    private static final String ORDER_BY =
            "ORDER BY country.Population DESC, country.Name ASC ";

    // Column layout for the header. It matches Country.toReportRow().
    private static final String HEADER_FORMAT = "%-4s %-45s %-15s %-26s %15s  %s";

    // The database connection from DatabaseConnector.
    private final Connection con;

    /**
     * Creates a CountryReport that uses the given database connection.
     *
     * @param con an open connection to the world database
     * @throws NullPointerException if the connection is null
     */
    public CountryReport(Connection con) {
        if (con == null) {
            throw new NullPointerException("Connection argument cannot be null");
        }
        this.con = con;
    }

    /**
     * Report 1: all countries in the world.
     *
     * @return the countries, largest population first
     */
    public List<Country> getAllCountries() {
        String sql = BASE_QUERY + ORDER_BY;
        return runQuery(sql);
    }

    /**
     * Report 2: all countries in a continent.
     *
     * @param continent the continent, e.g. "Europe"
     * @return the countries, largest population first
     */
    public List<Country> getCountriesByContinent(String continent) {
        String sql = BASE_QUERY + "WHERE country.Continent = ? " + ORDER_BY;
        return runQuery(sql, continent);
    }

    /**
     * Report 3: all countries in a region.
     *
     * @param region the region, e.g. "Caribbean"
     * @return the countries, largest population first
     */
    public List<Country> getCountriesByRegion(String region) {
        String sql = BASE_QUERY + "WHERE country.Region = ? " + ORDER_BY;
        return runQuery(sql, region);
    }

    /**
     * Report 4: the top N countries in the world.
     *
     * @param n how many countries to show (1 or more)
     * @return the N countries with the most people
     */
    public List<Country> getTopCountries(int n) {
        checkN(n);
        String sql = BASE_QUERY + ORDER_BY + "LIMIT ?";
        return runQuery(sql, n);
    }

    /**
     * Report 5: the top N countries in a continent.
     *
     * @param continent the continent, e.g. "Asia"
     * @param n         how many countries to show (1 or more)
     * @return the N countries with the most people in that continent
     */
    public List<Country> getTopCountriesByContinent(String continent, int n) {
        checkN(n);
        String sql = BASE_QUERY + "WHERE country.Continent = ? " + ORDER_BY + "LIMIT ?";
        return runQuery(sql, continent, n);
    }

    /**
     * Report 6: the top N countries in a region.
     *
     * @param region the region, e.g. "Western Europe"
     * @param n      how many countries to show (1 or more)
     * @return the N countries with the most people in that region
     */
    public List<Country> getTopCountriesByRegion(String region, int n) {
        checkN(n);
        String sql = BASE_QUERY + "WHERE country.Region = ? " + ORDER_BY + "LIMIT ?";
        return runQuery(sql, region, n);
    }

    /**
     * Prints a report as a table.
     * It shows the title, the column names, a line, and then one row per country.
     *
     * @param title     the name of the report, shown at the top
     * @param countries the countries to print
     */
    public void printReport(String title, List<Country> countries) {
        String header = String.format(HEADER_FORMAT,
                "Code", "Name", "Continent", "Region", "Population", "Capital");

        System.out.println();
        System.out.println(title);
        System.out.println(header);
        System.out.println("-".repeat(header.length()));

        // If there are no results, say so instead of printing an empty table.
        if (countries.isEmpty()) {
            System.out.println("No countries found.");
            return;
        }

        for (Country country : countries) {
            System.out.println(country.toReportRow());
        }
    }

    /**
     * Checks that N is 1 or more. A top 0 or top -5 report makes no sense.
     *
     * @param n the number to check
     * @throws IllegalArgumentException if N is less than 1
     */
    private void checkN(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("N must be 1 or more but was: " + n);
        }
    }

    /**
     * Runs a SQL query and turns each row into a Country.
     * <p>
     * Each "?" in the query is replaced by a value, in order.
     * This is called a PreparedStatement. It keeps the query safe,
     * because the values can never change the SQL itself.
     *
     * @param sql    the SQL query, with a "?" for each value
     * @param values the values for the "?" places (text or numbers)
     * @return the countries found, or an empty list if something went wrong
     */
    private List<Country> runQuery(String sql, Object... values) {
        List<Country> countries = new ArrayList<>();

        // "try (...)" closes the statement and the results for us when we are done.
        try (PreparedStatement stmt = con.prepareStatement(sql)) {

            // Put each value into its "?". Numbering starts at 1, not 0.
            for (int i = 0; i < values.length; i++) {
                if (values[i] instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) values[i]);
                } else {
                    stmt.setString(i + 1, (String) values[i]);
                }
            }

            try (ResultSet rset = stmt.executeQuery()) {
                // Go through the results one row at a time.
                while (rset.next()) {
                    countries.add(Country.fromResultSet(rset));
                }
            }
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }

        return countries;
    }
}