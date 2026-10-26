package com.napier.sem;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for CountryReport (Lab 07 style).
 * <p>
 * These tests use the real world database, so it must be running on localhost:33060
 * (start the db service from docker-compose).
 * <p>
 * The "integration" tag means these tests only run when asked for:
 * mvn test -Dgroups=integration -Dtest.excluded=none
 */
@Tag("integration")
public class CountryReportIntegrationTest {

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

    // Report 1: all countries in the world

    // The world database has 239 countries.
    @Test
    void worldReportHasEveryCountry() {
        assertEquals(239, report.getAllCountries().size());
    }

    // China has the most people, so it comes first.
    @Test
    void worldReportStartsWithChina() {
        Country first = report.getAllCountries().get(0);

        assertEquals("CHN", first.getCode());
        assertEquals("China", first.getName());
        assertEquals("Peking", first.getCapitalName());
    }

    // Every country has the same or fewer people than the one before it.
    @Test
    void worldReportIsSortedLargestFirst() {
        assertSortedByPopulation(report.getAllCountries());
    }

    // Antarctica has no capital, but it must still be in the report.
    @Test
    void countryWithoutCapitalIsStillListed() {
        Country antarctica = report.getAllCountries().stream()
                .filter(c -> c.getCode().equals("ATA"))
                .findFirst()
                .orElse(null);

        assertNotNull(antarctica);
        assertNull(antarctica.getCapitalName());
    }

    // Report 2: all countries in a continent

    // Europe has 46 countries, Russia is first, and every one is in Europe.
    @Test
    void continentReportOnlyHasThatContinent() {
        List<Country> countries = report.getCountriesByContinent("Europe");

        assertEquals(46, countries.size());
        assertEquals("RUS", countries.get(0).getCode());
        for (Country c : countries) {
            assertEquals("Europe", c.getContinent());
        }
        assertSortedByPopulation(countries);
    }

    // Report 3: all countries in a region

    // The Caribbean has 24 countries, Cuba is first, and every one is in the Caribbean.
    @Test
    void regionReportOnlyHasThatRegion() {
        List<Country> countries = report.getCountriesByRegion("Caribbean");

        assertEquals(24, countries.size());
        assertEquals("CUB", countries.get(0).getCode());
        for (Country c : countries) {
            assertEquals("Caribbean", c.getRegion());
        }
        assertSortedByPopulation(countries);
    }

    // Report 4: top N countries in the world

    // Asking for 5 gives exactly 5, starting with China and India.
    @Test
    void topNWorldReturnsExactlyN() {
        List<Country> countries = report.getTopCountries(5);

        assertEquals(5, countries.size());
        assertEquals("CHN", countries.get(0).getCode());
        assertEquals("IND", countries.get(1).getCode());
        assertSortedByPopulation(countries);
    }

    // Report 5: top N countries in a continent

    // Asking for the top 5 in Asia gives 5 Asian countries.
    @Test
    void topNContinentReturnsExactlyN() {
        List<Country> countries = report.getTopCountriesByContinent("Asia", 5);

        assertEquals(5, countries.size());
        for (Country c : countries) {
            assertEquals("Asia", c.getContinent());
        }
        assertSortedByPopulation(countries);
    }

    // Report 6: top N countries in a region

    // The top 3 in Western Europe starts with Germany.
    @Test
    void topNRegionReturnsExactlyN() {
        List<Country> countries = report.getTopCountriesByRegion("Western Europe", 3);

        assertEquals(3, countries.size());
        assertEquals("DEU", countries.get(0).getCode());
        assertSortedByPopulation(countries);
    }

    // Edge cases

    // The Caribbean has 24 countries, so asking for 100 gives 24.
    @Test
    void nLargerThanTotalReturnsAllRows() {
        assertEquals(24, report.getTopCountriesByRegion("Caribbean", 100).size());
    }

    // A continent that does not exist gives an empty list, not an error.
    @Test
    void unknownContinentGivesEmptyList() {
        assertTrue(report.getCountriesByContinent("Atlantis").isEmpty());
    }

    // A region that does not exist gives an empty list, not an error.
    @Test
    void unknownRegionGivesEmptyList() {
        assertTrue(report.getCountriesByRegion("Middle Earth").isEmpty());
    }

    // N of 0 or less is refused.
    @Test
    void zeroOrNegativeNIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> report.getTopCountries(0));
        assertThrows(IllegalArgumentException.class, () -> report.getTopCountries(-3));
    }

    // Printing a real report does not crash.
    @Test
    void printReportWorksWithRealData() {
        CountryReport.printReport("Top 3 countries in the world", report.getTopCountries(3));
    }

    // Helper: checks that every country has the same or fewer people than the one before it.
    private static void assertSortedByPopulation(List<Country> countries) {
        for (int i = 1; i < countries.size(); i++) {
            int before = countries.get(i - 1).getPopulation();
            int now = countries.get(i).getPopulation();
            assertTrue(before >= now,
                    "Not sorted: " + countries.get(i - 1).getName() + " before " + countries.get(i).getName());
        }
    }
}