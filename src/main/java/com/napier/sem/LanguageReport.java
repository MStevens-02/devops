package com.napier.sem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles data retrieval and console formatting for the World Language Population Report.
 * Connects to the database to calculate total speakers and global percentages
 * for the five required languages (Chinese, English, Hindi, Spanish, Arabic).
 */
public class LanguageReport {

    /** Active database connection instance. */
    private Connection con;

    /**
     * Constructor that accepts an active database connection.
     *
     * @param con Active SQL database connection
     */
    public LanguageReport(Connection con) {
        this.con = con;
    }

    /**
     * Fetches language statistics and prints the formatted report to the console.
     * Displays an error banner if no data is returned.
     */
    public void displayLanguageReport() {
        List<Language> languages = getLanguageStatistics();

        // Print report headers with fixed column spacing
        System.out.println("\nWorld Language Population Report");
        System.out.printf("%-20s %-20s %-25s\n", "Language", "Total Speakers", "World Population (%)");
        System.out.println("----------------------------------------------------------------------------------");

        // Guard against empty data or missing results
        if (languages == null || languages.isEmpty()) {
            System.out.println("[ERROR] Demographic language data is missing or no records found.");
            return;
        }

        // Iterate through records and print each row using the Language toString() layout
        for (Language lang : languages) {
            System.out.println(lang);
        }
        System.out.println();
    }

    /**
     * Executes the SQL aggregation query to compute speaker totals and global percentage share.
     * Uses CAST for large numbers to prevent overflow and NULLIF to guard against division by zero.
     *
     * @return A list of populated Language domain objects ordered by total speakers descending
     */
    public List<Language> getLanguageStatistics() {
        List<Language> languages = new ArrayList<>();

        if (con == null) {
            System.out.println("Database connection is null.");
            return languages;
        }

        // SQL query filtering for the 5 target languages, summing speakers, and calculating world share percentage
        String strSelect =
                "SELECT cl.Language, " +
                        "SUM(CAST(c.Population AS SIGNED) * (cl.Percentage / 100)) AS TotalSpeakers, " +
                        "(SUM(CAST(c.Population AS SIGNED) * (cl.Percentage / 100)) / " +
                        "NULLIF((SELECT SUM(CAST(Population AS SIGNED)) FROM country), 0)) * 100 AS WorldPercentage " +
                        "FROM countrylanguage cl " +
                        "JOIN country c ON cl.CountryCode = c.Code " +
                        "WHERE cl.Language IN ('Chinese', 'English', 'Hindi', 'Spanish', 'Arabic') " +
                        "GROUP BY cl.Language " +
                        "ORDER BY TotalSpeakers DESC;";

        // Try-with-resources ensures Statement and ResultSet are closed safely to prevent memory/resource leaks
        try (Statement stmt = con.createStatement();
             ResultSet rset = stmt.executeQuery(strSelect)) {

            while (rset.next()) {
                String name = rset.getString("Language");
                long speakers = rset.getLong("TotalSpeakers");
                double percentage = rset.getDouble("WorldPercentage");

                // Map result set row into a Language domain object and add to list
                languages.add(new Language(name, speakers, percentage));
            }
        } catch (Exception e) {
            System.out.println("Failed to get language details: " + e.getMessage());
            e.printStackTrace();
        }

        return languages;
    }
}