package com.napier.sem;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the country reports (UC1) that use the REAL database.
 * <p>
 * The database needs to be running on localhost:33060 for these to work
 * (start it with docker-compose). They only run when you ask for them with:
 * mvn test -Dgroups=integration -Dtest.excluded=none
 * <p>
 * All the numbers we check against come from db/world_db/world.sql.
 */
@Tag("integration")
public class CountryReportIntegrationTest {

    /** The database connection, shared by every test. */
    static DatabaseConnector con;

    /** The report we're testing. */
    static CountryReport report;

    /**
     * Connects to the database once before any of the tests run.
     */
    @BeforeAll
    static void connect() {
        con = new DatabaseConnector();
        con.connect("localhost:33060", 30000);
        report = new CountryReport(con.getConnection());
    }

    /**
     * Disconnects from the database once all the tests are done.
     */
    @AfterAll
    static void disconnect() {
        con.disconnect();
    }

    /**
     * Checks a list goes from the most people to the least.
     * Each country should have the same or less people than the one before it.
     *
     * @param countries the list to check
     */
    private void checkBiggestFirst(List<Country> countries) {
        for (int i = 1; i < countries.size(); i++) {
            int before = countries.get(i - 1).getPopulation();
            int now = countries.get(i).getPopulation();
            assertTrue(before >= now,
                    "Not sorted: " + countries.get(i - 1).getName() + " before " + countries.get(i).getName());
        }
    }

    // ===== report 1: every country in the world =====

    /**
     * There are 239 countries, China is first, and it's sorted biggest first.
     */
    @Test
    void allCountries() {
        List<Country> countries = report.getAllCountries();

        assertEquals(239, countries.size());
        assertEquals("CHN", countries.get(0).getCode());
        assertEquals("China", countries.get(0).getName());
        assertEquals("Peking", countries.get(0).getCapitalName());
        checkBiggestFirst(countries);
    }

    /**
     * Antarctica has no capital but it should still be in the list.
     */
    @Test
    void antarcticaIsStillThere() {
        Country antarctica = null;
        for (Country c : report.getAllCountries()) {
            if (c.getCode().equals("ATA")) {
                antarctica = c;
            }
        }

        assertNotNull(antarctica);
        assertNull(antarctica.getCapitalName());
    }

    /**
     * The capital name comes from joining the city table, so check it got the right one.
     */
    @Test
    void ukCapitalIsLondon() {
        Country uk = null;
        for (Country c : report.getAllCountries()) {
            if (c.getCode().equals("GBR")) {
                uk = c;
            }
        }

        assertNotNull(uk);
        assertEquals("London", uk.getCapitalName());
        assertEquals(59623400, uk.getPopulation());
    }

    /**
     * 7 countries have 0 people. They have the same population, so they should be
     * sorted by name instead (A to Z), right at the end of the list.
     */
    @Test
    void sameSizeCountriesAreSortedByName() {
        List<Country> countries = report.getAllCountries();
        List<Country> lastSeven = countries.subList(countries.size() - 7, countries.size());

        for (Country c : lastSeven) {
            assertEquals(0, c.getPopulation());
        }
        assertEquals("Antarctica", lastSeven.get(0).getName());
        assertEquals("United States Minor Outlying Islands", lastSeven.get(6).getName());

        // every name should come after the one before it in the alphabet
        for (int i = 1; i < lastSeven.size(); i++) {
            String before = lastSeven.get(i - 1).getName();
            String now = lastSeven.get(i).getName();
            assertTrue(before.compareToIgnoreCase(now) < 0, before + " should be before " + now);
        }
    }

    // ===== report 2: every country in a continent =====

    /**
     * Europe has 46 countries, Russia is first, and every one is actually in Europe.
     */
    @Test
    void europe() {
        List<Country> countries = report.getCountriesByContinent("Europe");

        assertEquals(46, countries.size());
        assertEquals("RUS", countries.get(0).getCode());
        for (Country c : countries) {
            assertEquals("Europe", c.getContinent());
        }
        checkBiggestFirst(countries);
    }

    // ===== report 3: every country in a region =====

    /**
     * The Caribbean has 24 countries, Cuba is first, and every one is actually in the Caribbean.
     */
    @Test
    void caribbean() {
        List<Country> countries = report.getCountriesByRegion("Caribbean");

        assertEquals(24, countries.size());
        assertEquals("CUB", countries.get(0).getCode());
        for (Country c : countries) {
            assertEquals("Caribbean", c.getRegion());
        }
        checkBiggestFirst(countries);
    }

    // ===== reports 4, 5 and 6: the top N =====

    /**
     * The top 5 in the world starts with China then India.
     */
    @Test
    void top5InTheWorld() {
        List<Country> countries = report.getTopCountries(5);

        assertEquals(5, countries.size());
        assertEquals("CHN", countries.get(0).getCode());
        assertEquals("IND", countries.get(1).getCode());
        checkBiggestFirst(countries);
    }

    /**
     * The top 5 in the world should be exactly the same as the first 5 of the full list.
     */
    @Test
    void top5MatchesTheFullList() {
        List<Country> top = report.getTopCountries(5);
        List<Country> all = report.getAllCountries();

        for (int i = 0; i < 5; i++) {
            assertEquals(all.get(i).getCode(), top.get(i).getCode());
        }
    }

    /**
     * The top 5 in Asia are all in Asia, in the right order:
     * China, India, Indonesia, Pakistan, Bangladesh.
     */
    @Test
    void top5InAsia() {
        List<Country> countries = report.getTopCountriesByContinent("Asia", 5);

        assertEquals(5, countries.size());
        assertEquals("CHN", countries.get(0).getCode());
        assertEquals("IND", countries.get(1).getCode());
        assertEquals("IDN", countries.get(2).getCode());
        assertEquals("PAK", countries.get(3).getCode());
        assertEquals("BGD", countries.get(4).getCode());
        for (Country c : countries) {
            assertEquals("Asia", c.getContinent());
        }
        checkBiggestFirst(countries);
    }

    /**
     * The top 3 in Western Europe starts with Germany.
     */
    @Test
    void top3InWesternEurope() {
        List<Country> countries = report.getTopCountriesByRegion("Western Europe", 3);

        assertEquals(3, countries.size());
        assertEquals("DEU", countries.get(0).getCode());
        checkBiggestFirst(countries);
    }

    // ===== weird cases =====

    /**
     * The Caribbean only has 24 countries, so asking for 100 just gives 24.
     */
    @Test
    void askingForTooManyGivesWhatThereIs() {
        assertEquals(24, report.getTopCountriesByRegion("Caribbean", 100).size());
    }

    /**
     * A made up continent or region gives an empty list, not an error.
     */
    @Test
    void madeUpPlacesGiveNothing() {
        assertTrue(report.getCountriesByContinent("Atlantis").isEmpty());
        assertTrue(report.getCountriesByRegion("Middle Earth").isEmpty());
    }

    /**
     * A made up continent or region in the top N reports gives an empty list too.
     */
    @Test
    void madeUpPlacesInTopNGiveNothing() {
        assertTrue(report.getTopCountriesByContinent("Atlantis", 5).isEmpty());
        assertTrue(report.getTopCountriesByRegion("Middle Earth", 5).isEmpty());
    }

    /**
     * N has to be 1 or more, so 0 and minus numbers throw an error in all 3 top N reports.
     */
    @Test
    void saysNoToZeroOrMinusN() {
        assertThrows(IllegalArgumentException.class, () -> report.getTopCountries(0));
        assertThrows(IllegalArgumentException.class, () -> report.getTopCountries(-3));
        assertThrows(IllegalArgumentException.class, () -> report.getTopCountriesByContinent("Asia", 0));
        assertThrows(IllegalArgumentException.class, () -> report.getTopCountriesByContinent("Asia", -1));
        assertThrows(IllegalArgumentException.class, () -> report.getTopCountriesByRegion("Caribbean", 0));
        assertThrows(IllegalArgumentException.class, () -> report.getTopCountriesByRegion("Caribbean", -1));
    }

    /**
     * Asking for just 1 gives just China.
     */
    @Test
    void top1IsJustChina() {
        List<Country> countries = report.getTopCountries(1);

        assertEquals(1, countries.size());
        assertEquals("CHN", countries.get(0).getCode());
    }

    /**
     * Printing a real report shouldn't crash.
     */
    @Test
    void printingRealDataWorks() {
        assertDoesNotThrow(() -> CountryReport.printReport("Top 3 countries in the world", report.getTopCountries(3)));
    }
}
