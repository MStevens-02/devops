package com.napier.sem;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * More integration tests for CountryReport (edge cases).
 * <p>
 * These tests use the real world database, so it must be running on localhost:33060
 * (start the db service from docker-compose).
 * <p>
 * The "integration" tag means these tests only run when asked for:
 * mvn test -Dgroups=integration -Dtest.excluded=none
 * <p>
 * The expected values come from the world database (db/world_db/world.sql).
 */
@Tag("integration")
public class CountryReportEdgeCaseIntegrationTest {

    static DatabaseConnector con;
    static CountryReport report;

    // Connects to the database once, before all the tests.
    @BeforeAll
    static void init() {
        con = new DatabaseConnector();
        con.connect("localhost:33060", 30000);
        report = new CountryReport(con.getConnection());
    }

    // Closes the database connection after all the tests.
    @AfterAll
    static void cleanUp() {
        con.disconnect();
    }

    // Makes a list of the country codes, so two reports are easy to compare.
    private static String codes(List<Country> countries) {
        StringBuilder text = new StringBuilder();
        for (Country c : countries) {
            text.append(c.getCode()).append(" ");
        }
        return text.toString().trim();
    }

    // N must be 1 or more (all three top N reports)

    // Top N in a continent refuses 0 and negative numbers.
    @Test
    void topNContinentRejectsZeroAndNegativeN() {
        assertThrows(IllegalArgumentException.class, () -> report.getTopCountriesByContinent("Asia", 0));
        assertThrows(IllegalArgumentException.class, () -> report.getTopCountriesByContinent("Asia", -1));
    }

    // Top N in a region refuses 0 and negative numbers.
    @Test
    void topNRegionRejectsZeroAndNegativeN() {
        assertThrows(IllegalArgumentException.class, () -> report.getTopCountriesByRegion("Caribbean", 0));
        assertThrows(IllegalArgumentException.class, () -> report.getTopCountriesByRegion("Caribbean", -1));
    }

    // The error message says what was wrong.
    @Test
    void badNErrorMessageShowsTheNumber() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> report.getTopCountries(-7));

        assertTrue(e.getMessage().contains("-7"));
    }

    // Top 1

    // Asking for the top 1 gives exactly one row in every top N report.
    @Test
    void topOneGivesExactlyOneRow() {
        assertEquals(1, report.getTopCountries(1).size());
        assertEquals(1, report.getTopCountriesByContinent("Asia", 1).size());
        assertEquals(1, report.getTopCountriesByRegion("Western Europe", 1).size());
    }

    // The biggest country in the world is China.
    @Test
    void topOneInTheWorldIsChina() {
        assertEquals("CHN", report.getTopCountries(1).get(0).getCode());
    }

    // Top N is the start of the full report

    // The top 10 in the world are the same countries, in the same order,
    // as the first 10 rows of the full world report.
    @Test
    void topTenIsTheStartOfTheWorldReport() {
        List<Country> all = report.getAllCountries();

        assertEquals(codes(all.subList(0, 10)), codes(report.getTopCountries(10)));
    }

    // The same check for a continent.
    @Test
    void topNContinentIsTheStartOfTheContinentReport() {
        List<Country> all = report.getCountriesByContinent("Asia");

        assertEquals(codes(all.subList(0, 5)), codes(report.getTopCountriesByContinent("Asia", 5)));
    }

    // The same check for a region.
    @Test
    void topNRegionIsTheStartOfTheRegionReport() {
        List<Country> all = report.getCountriesByRegion("Western Europe");

        assertEquals(codes(all.subList(0, 3)), codes(report.getTopCountriesByRegion("Western Europe", 3)));
    }

    // Exact answers

    // The five biggest countries in Asia.
    @Test
    void topFiveInAsiaAreCorrect() {
        assertEquals("CHN IND IDN PAK BGD", codes(report.getTopCountriesByContinent("Asia", 5)));
    }

    // The three biggest countries in Western Europe.
    @Test
    void topThreeInWesternEuropeAreCorrect() {
        assertEquals("DEU FRA NLD", codes(report.getTopCountriesByRegion("Western Europe", 3)));
    }

    // Countries with the same population

    // Seven countries have 0 people. When the population is the same,
    // they are sorted by name, so the end of the world report is always in the same order.
    @Test
    void countriesWithNoPeopleAreSortedByName() {
        List<Country> all = report.getAllCountries();
        List<Country> lastSeven = all.subList(all.size() - 7, all.size());

        String[] expected = {"Antarctica", "Bouvet Island", "British Indian Ocean Territory",
                "French Southern territories", "Heard Island and McDonald Islands",
                "South Georgia and the South Sandwich Islands", "United States Minor Outlying Islands"};

        for (int i = 0; i < expected.length; i++) {
            assertEquals(0, lastSeven.get(i).getPopulation().intValue());
            assertEquals(expected[i], lastSeven.get(i).getName());
        }
    }

    // The report gives the same order every time it is run.
    @Test
    void sameReportTwiceGivesTheSameOrder() {
        assertEquals(codes(report.getAllCountries()), codes(report.getAllCountries()));
    }

    // Every country is in exactly one continent

    // The country counts of the 7 continents add up to the 239 countries in the world.
    @Test
    void continentReportsAddUpToEveryCountry() {
        String[] continents = {"Africa", "Antarctica", "Asia", "Europe",
                "North America", "Oceania", "South America"};

        int sum = 0;
        for (String continent : continents) {
            sum = sum + report.getCountriesByContinent(continent).size();
        }

        assertEquals(239, sum);
    }

    // Check the size of two continents.
    @Test
    void continentSizesAreCorrect() {
        assertEquals(58, report.getCountriesByContinent("Africa").size());
        assertEquals(51, report.getCountriesByContinent("Asia").size());
        assertEquals(5, report.getCountriesByContinent("Antarctica").size());
    }

    // Bad text

    // A null or blank continent gives an empty list, not an error.
    @Test
    void nullOrBlankContinentGivesEmptyList() {
        assertTrue(report.getCountriesByContinent(null).isEmpty());
        assertTrue(report.getCountriesByContinent("").isEmpty());
        assertTrue(report.getCountriesByContinent("   ").isEmpty());
    }

    // A null or blank region gives an empty list, not an error.
    @Test
    void nullOrBlankRegionGivesEmptyList() {
        assertTrue(report.getCountriesByRegion(null).isEmpty());
        assertTrue(report.getCountriesByRegion("").isEmpty());
        assertTrue(report.getCountriesByRegion("   ").isEmpty());
    }

    // The same for the top N reports.
    @Test
    void topNWithUnknownAreaGivesEmptyList() {
        assertTrue(report.getTopCountriesByContinent("Atlantis", 5).isEmpty());
        assertTrue(report.getTopCountriesByRegion("Middle Earth", 5).isEmpty());
    }

    // The ? in the SQL stops SQL injection, so this trick must find nothing.
    @Test
    void sqlInjectionDoesNotWork() {
        assertTrue(report.getCountriesByContinent("Europe' OR '1'='1").isEmpty());
        assertTrue(report.getCountriesByRegion("Caribbean' OR '1'='1").isEmpty());
    }
}
