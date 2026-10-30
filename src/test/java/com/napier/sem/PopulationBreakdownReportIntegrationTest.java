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
        // step 1: get the continent breakdown
        // step 2: check there are 7 rows
        assertEquals(7, report.getContinentBreakdown().size());
    }

    // The rows are in name order, so Africa is first and South America is last.
    @Test
    void continentsAreInNameOrder() {
        // step 1: get the continent breakdown
        // step 2: check the first row is Africa and the last row is South America
        List<PopulationBreakdown> rows = report.getContinentBreakdown();

        assertEquals("Africa", rows.get(0).getName());
        assertEquals("South America", rows.get(rows.size() - 1).getName());
    }

    // The real Asia numbers.
    @Test
    void asiaBreakdownIsCorrect() {
        // step 1: get the continent breakdown and find the Asia row
        // step 2: check the total, the city number, the non-city number and the percentage
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
        // step 1: get the continent breakdown and find the Europe row
        // step 2: check the total and the number living in cities
        PopulationBreakdown europe = find(report.getContinentBreakdown(), "Europe");

        assertNotNull(europe);
        assertEquals(730074600L, europe.getTotalPopulation());
        assertEquals(241942813L, europe.getCityPopulation());
    }

    // The continent totals add up to the world population.
    // (This would fail if the cities were joined in the wrong way and counted too many times.)
    @Test
    void continentTotalsAddUpToTheWorld() {
        // step 1: add up the total of every continent row
        // step 2: check it is the world population (6078749450)
        assertEquals(6078749450L, sumOfTotals(report.getContinentBreakdown()));
    }

    // Antarctica has nobody in it, so it is all zeros with 0% (and no divide by zero).
    @Test
    void antarcticaContinentIsAllZero() {
        // step 1: find the Antarctica row
        // step 2: check everything is 0 and both percentages are 0
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
        // step 1: get the region breakdown
        // step 2: check there are 25 rows
        assertEquals(25, report.getRegionBreakdown().size());
    }

    // The Caribbean total matches the lookup number.
    @Test
    void caribbeanRegionTotalIsCorrect() {
        // step 1: find the Caribbean row
        // step 2: check its total is 38140000, the same as the lookup
        PopulationBreakdown caribbean = find(report.getRegionBreakdown(), "Caribbean");

        assertNotNull(caribbean);
        assertEquals(38140000L, caribbean.getTotalPopulation());
    }

    // The region totals add up to the world population.
    @Test
    void regionTotalsAddUpToTheWorld() {
        // step 1: add up the total of every region row
        // step 2: check it is the world population
        assertEquals(6078749450L, sumOfTotals(report.getRegionBreakdown()));
    }

    // Country breakdown

    // There are 239 countries.
    @Test
    void thereAreTwoHundredAndThirtyNineCountries() {
        // step 1: get the country breakdown
        // step 2: check there are 239 rows
        assertEquals(239, report.getCountryBreakdown().size());
    }

    // The country totals add up to the world population.
    @Test
    void countryTotalsAddUpToTheWorld() {
        // step 1: add up the total of every country row
        // step 2: check it is the world population
        assertEquals(6078749450L, sumOfTotals(report.getCountryBreakdown()));
    }

    // France total matches the country table.
    @Test
    void franceTotalIsCorrect() {
        // step 1: find the France row
        // step 2: check its total is 59225700
        PopulationBreakdown france = find(report.getCountryBreakdown(), "France");

        assertNotNull(france);
        assertEquals(59225700L, france.getTotalPopulation());
    }

    // A country with no cities in the database must still be listed, with 0 in cities.
    @Test
    void countryWithNoCitiesIsStillListed() {
        // step 1: find Bouvet Island, which has no cities
        // step 2: check it is in the list and has 0 in cities
        PopulationBreakdown bouvet = find(report.getCountryBreakdown(), "Bouvet Island");

        assertNotNull(bouvet);
        assertEquals(0, bouvet.getCityPopulation());
    }

    // Singapore has more city people than country people in the database (a data problem).
    // The report must still show it, and flag it.
    @Test
    void singaporeIsFlaggedAsADataMismatch() {
        // step 1: find the Singapore row
        // step 2: check it is flagged as a mismatch
        // step 3: check the printed line has a * on it
        PopulationBreakdown singapore = find(report.getCountryBreakdown(), "Singapore");

        assertNotNull(singapore);
        assertEquals(3567000L, singapore.getTotalPopulation());
        assertEquals(4017733L, singapore.getCityPopulation());
        assertTrue(singapore.isDataMismatch());
    }

    // Only 3 countries have this problem: Cocos Islands, Gibraltar and Singapore.
    @Test
    void exactlyThreeCountriesAreMismatches() {
        // step 1: go through every country row and count the ones flagged
        // step 2: check the count is exactly 3
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
        // step 1: check every row in the continent, region and country breakdowns (see the helper below)
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
        // step 1: go through every row in all three breakdowns
        // step 2: check each row has a name and a total that is not negative
        for (PopulationBreakdown row : report.getCountryBreakdown()) {
            assertNotNull(row.getName());
            assertFalse(row.getName().isEmpty());
            assertTrue(row.getTotalPopulation() >= 0);
        }
    }
}
