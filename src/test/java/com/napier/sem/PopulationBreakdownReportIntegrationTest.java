package com.napier.sem;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for PopulationBreakdownReport (UC4, the population breakdown).
 * <p>
 * These tests use the real world database, so it must be running on localhost:33060
 * (start the db service from docker-compose).
 * <p>
 * The "integration" tag means these tests only run when asked for:
 * mvn test -Dgroups=integration -Dtest.excluded=none
 * <p>
 * The expected numbers come from the world database (db/world_db/world.sql).
 */
@Tag("integration")
public class PopulationBreakdownReportIntegrationTest {

    // Percentages are doubles, so we allow a tiny difference when comparing them.
    private static final double DELTA = 0.001;

    static DatabaseConnector con;
    static PopulationBreakdownReport report;

    // Connects to the database once, before all the tests.
    @BeforeAll
    static void init() {
        con = new DatabaseConnector();
        con.connect("localhost:33060", 30000);
        report = new PopulationBreakdownReport(con.getConnection());
    }

    // Closes the database connection after all the tests.
    @AfterAll
    static void cleanUp() {
        con.disconnect();
    }

    // Finds the row with this name, or gives back null if there is no such row.
    private PopulationBreakdown find(List<PopulationBreakdown> rows, String name) {
        for (PopulationBreakdown row : rows) {
            if (row.getName().equals(name)) {
                return row;
            }
        }
        return null;
    }

    // Adds up the total population of every row.
    private long sumOfTotals(List<PopulationBreakdown> rows) {
        long sum = 0;
        for (PopulationBreakdown row : rows) {
            sum = sum + row.getTotalPopulation();
        }
        return sum;
    }

    // Continent breakdown

    // There are 7 continents.
    @Test
    void thereAreSevenContinents() {
        assertEquals(7, report.getContinentBreakdown().size());
    }

    // The rows are in name order, so Africa is first and South America is last.
    @Test
    void continentsAreInNameOrder() {
        List<PopulationBreakdown> rows = report.getContinentBreakdown();

        assertEquals("Africa", rows.get(0).getName());
        assertEquals("South America", rows.get(rows.size() - 1).getName());
    }

    // The real Asia numbers.
    @Test
    void asiaBreakdownIsCorrect() {
        PopulationBreakdown asia = find(report.getContinentBreakdown(), "Asia");

        assertNotNull(asia);
        assertEquals(3705025700L, asia.getTotalPopulation());
        assertEquals(697604103L, asia.getCityPopulation());
        assertEquals(3007421597L, asia.getNonCityPopulation());
        assertEquals(18.83, asia.getCityPercentage(), 0.01);
        assertEquals(81.17, asia.getNonCityPercentage(), 0.01);
    }

    // The real Europe numbers.
    @Test
    void europeBreakdownIsCorrect() {
        PopulationBreakdown europe = find(report.getContinentBreakdown(), "Europe");

        assertNotNull(europe);
        assertEquals(730074600L, europe.getTotalPopulation());
        assertEquals(241942813L, europe.getCityPopulation());
    }

    // The continent totals add up to the world population.
    // (This would fail if the cities were joined in the wrong way and counted too many times.)
    @Test
    void continentTotalsAddUpToTheWorld() {
        assertEquals(6078749450L, sumOfTotals(report.getContinentBreakdown()));
    }

    // Antarctica has nobody in it, so it is all zeros with 0% (and no divide by zero).
    @Test
    void antarcticaContinentIsAllZero() {
        PopulationBreakdown antarctica = find(report.getContinentBreakdown(), "Antarctica");

        assertNotNull(antarctica);
        assertEquals(0, antarctica.getTotalPopulation());
        assertEquals(0, antarctica.getCityPopulation());
        assertEquals(0.0, antarctica.getCityPercentage(), DELTA);
        assertEquals(0.0, antarctica.getNonCityPercentage(), DELTA);
    }

    // Region breakdown

    // There are 25 regions.
    @Test
    void thereAreTwentyFiveRegions() {
        assertEquals(25, report.getRegionBreakdown().size());
    }

    // The Caribbean total matches the lookup number.
    @Test
    void caribbeanRegionTotalIsCorrect() {
        PopulationBreakdown caribbean = find(report.getRegionBreakdown(), "Caribbean");

        assertNotNull(caribbean);
        assertEquals(38140000L, caribbean.getTotalPopulation());
    }

    // The region totals add up to the world population.
    @Test
    void regionTotalsAddUpToTheWorld() {
        assertEquals(6078749450L, sumOfTotals(report.getRegionBreakdown()));
    }

    // Country breakdown

    // There are 239 countries.
    @Test
    void thereAreTwoHundredAndThirtyNineCountries() {
        assertEquals(239, report.getCountryBreakdown().size());
    }

    // The country totals add up to the world population.
    @Test
    void countryTotalsAddUpToTheWorld() {
        assertEquals(6078749450L, sumOfTotals(report.getCountryBreakdown()));
    }

    // France total matches the country table.
    @Test
    void franceTotalIsCorrect() {
        PopulationBreakdown france = find(report.getCountryBreakdown(), "France");

        assertNotNull(france);
        assertEquals(59225700L, france.getTotalPopulation());
    }

    // A country with no cities in the database must still be listed, with 0 in cities.
    @Test
    void countryWithNoCitiesIsStillListed() {
        PopulationBreakdown bouvet = find(report.getCountryBreakdown(), "Bouvet Island");

        assertNotNull(bouvet);
        assertEquals(0, bouvet.getCityPopulation());
    }

    // Singapore has more city people than country people in the database (a data problem).
    // The report must still show it, and flag it.
    @Test
    void singaporeIsFlaggedAsADataMismatch() {
        PopulationBreakdown singapore = find(report.getCountryBreakdown(), "Singapore");

        assertNotNull(singapore);
        assertEquals(3567000L, singapore.getTotalPopulation());
        assertEquals(4017733L, singapore.getCityPopulation());
        assertTrue(singapore.isDataMismatch());
    }

    // Only 3 countries have this problem: Cocos Islands, Gibraltar and Singapore.
    @Test
    void exactlyThreeCountriesAreMismatches() {
        int count = 0;
        for (PopulationBreakdown row : report.getCountryBreakdown()) {
            if (row.isDataMismatch()) {
                count++;
            }
        }

        assertEquals(3, count);
    }

    // Checks that apply to every row of every report

    // For every row: city people + non-city people = total, and the percentages add up to 100
    // (a total of 0 gives 0% and 0%).
    @Test
    void everyRowAddsUp() {
        checkEveryRow(report.getContinentBreakdown());
        checkEveryRow(report.getRegionBreakdown());
        checkEveryRow(report.getCountryBreakdown());
    }

    // Does the checks for one list of rows.
    private void checkEveryRow(List<PopulationBreakdown> rows) {
        for (PopulationBreakdown row : rows) {
            assertEquals(row.getTotalPopulation(),
                    row.getCityPopulation() + row.getNonCityPopulation(),
                    row.getName() + " should add up");

            double sum = row.getCityPercentage() + row.getNonCityPercentage();
            if (row.getTotalPopulation() == 0) {
                assertEquals(0.0, sum, DELTA, row.getName());
            } else {
                assertEquals(100.0, sum, DELTA, row.getName());
            }
        }
    }

    // Every row has a name and no row has a negative total.
    @Test
    void everyRowHasANameAndAValidTotal() {
        for (PopulationBreakdown row : report.getCountryBreakdown()) {
            assertNotNull(row.getName());
            assertFalse(row.getName().isEmpty());
            assertTrue(row.getTotalPopulation() >= 0);
        }
    }
}
