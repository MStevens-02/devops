package com.napier.sem;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the population breakdown (UC4) that use the REAL database.
 * <p>
 * The database needs to be running on localhost:33060 for these to work
 * (start it with docker-compose). They only run when you ask for them with:
 * mvn test -Dgroups=integration -Dtest.excluded=none
 * <p>
 * All the numbers we check against come from db/world_db/world.sql.
 */
@Tag("integration")
public class PopulationBreakdownReportIntegrationTest {

    /** Percentages are decimals, so we let them be off by a tiny tiny bit. */
    private static final double CLOSE_ENOUGH = 0.001;

    /** The world population. Every breakdown should add up to this. */
    private static final long WORLD = 6078749450L;

    /** The database connection, shared by every test. */
    static DatabaseConnector con;

    /** The breakdown report we're testing. */
    static PopulationBreakdownReport report;

    /** The three breakdowns, grabbed once at the start so we don't keep asking the database. */
    static List<PopulationBreakdown> continents;
    static List<PopulationBreakdown> regions;
    static List<PopulationBreakdown> countries;

    /**
     * Connects to the database and gets all three breakdowns once before any tests run.
     */
    @BeforeAll
    static void connect() {
        con = new DatabaseConnector();
        con.connect("localhost:33060", 30000);
        report = new PopulationBreakdownReport(con.getConnection());

        continents = report.getContinentBreakdown();
        regions = report.getRegionBreakdown();
        countries = report.getCountryBreakdown();
    }

    /**
     * Disconnects from the database once all the tests are done.
     */
    @AfterAll
    static void disconnect() {
        con.disconnect();
    }

    /**
     * Looks through a list for the line with this name. Fails the test if it isn't there.
     *
     * @param lines the list to look through
     * @param name  the name we want
     * @return the line with that name
     */
    private PopulationBreakdown lineFor(List<PopulationBreakdown> lines, String name) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).getName().equals(name)) {
                return lines.get(i);
            }
        }
        fail(name + " wasn't in the list");
        return null;
    }

    /**
     * Adds up the totals of every line in a list.
     *
     * @param lines the list to add up
     * @return all the totals added together
     */
    private long addUpTotals(List<PopulationBreakdown> lines) {
        long total = 0;
        for (PopulationBreakdown line : lines) {
            total += line.getTotalPopulation();
        }
        return total;
    }

    // ===== continents =====

    /**
     * There are 7 continents and they're in A to Z order.
     */
    @Test
    void sevenContinentsInOrder() {
        assertEquals(7, continents.size());
        assertEquals("Africa", continents.get(0).getName());
        assertEquals("South America", continents.get(6).getName());
    }

    /**
     * The real Asia numbers.
     */
    @Test
    void asia() {
        PopulationBreakdown asia = lineFor(continents, "Asia");

        assertEquals(3705025700L, asia.getTotalPopulation());
        assertEquals(697604103L, asia.getCityPopulation());
        assertEquals(3007421597L, asia.getNonCityPopulation());
        assertEquals(18.83, asia.getCityPercentage(), 0.01);
        assertEquals(81.17, asia.getNonCityPercentage(), 0.01);
    }

    /**
     * The real Europe numbers.
     */
    @Test
    void europe() {
        PopulationBreakdown europe = lineFor(continents, "Europe");

        assertEquals(730074600L, europe.getTotalPopulation());
        assertEquals(241942813L, europe.getCityPopulation());
    }

    /**
     * Nobody lives in Antarctica, so it's all zeros and 0% (and nothing crashes from dividing by 0).
     */
    @Test
    void antarcticaIsAllZeros() {
        PopulationBreakdown antarctica = lineFor(continents, "Antarctica");

        assertEquals(0, antarctica.getTotalPopulation());
        assertEquals(0, antarctica.getCityPopulation());
        assertEquals(0.0, antarctica.getCityPercentage(), CLOSE_ENOUGH);
        assertEquals(0.0, antarctica.getNonCityPercentage(), CLOSE_ENOUGH);
    }

    // ===== regions =====

    /**
     * There are 25 regions, and the Caribbean matches what the lookup says.
     */
    @Test
    void regionsAndCaribbean() {
        assertEquals(25, regions.size());
        assertEquals(38140000L, lineFor(regions, "Caribbean").getTotalPopulation());
    }

    // ===== countries =====

    /**
     * There are 239 countries, and France has the right number.
     */
    @Test
    void countriesAndFrance() {
        assertEquals(239, countries.size());
        assertEquals(59225700L, lineFor(countries, "France").getTotalPopulation());
    }

    /**
     * Bouvet Island has no cities but it should still be in the list, with 0 in cities.
     */
    @Test
    void countryWithNoCitiesStillShowsUp() {
        assertEquals(0, lineFor(countries, "Bouvet Island").getCityPopulation());
    }

    /**
     * Singapore has more city people than total people in the database (a data mistake).
     * It should still be in the list, get flagged, and have a star when it's printed.
     */
    @Test
    void singaporeGetsFlagged() {
        PopulationBreakdown singapore = lineFor(countries, "Singapore");

        assertEquals(3567000L, singapore.getTotalPopulation());
        assertEquals(4017733L, singapore.getCityPopulation());
        assertTrue(singapore.isDataMismatch());
        assertTrue(singapore.toString().contains("*"));
    }

    /**
     * Only 3 countries have that mistake: Cocos Islands, Gibraltar and Singapore.
     */
    @Test
    void onlyThreeDodgyCountries() {
        int dodgy = 0;
        for (PopulationBreakdown country : countries) {
            if (country.isDataMismatch()) {
                dodgy++;
            }
        }
        assertEquals(3, dodgy);
    }

    /**
     * The UK's real numbers: 59,623,400 people and 22,436,673 of them in cities.
     */
    @Test
    void unitedKingdom() {
        PopulationBreakdown uk = lineFor(countries, "United Kingdom");

        assertEquals(59623400L, uk.getTotalPopulation());
        assertEquals(22436673L, uk.getCityPopulation());
        assertEquals(37186727L, uk.getNonCityPopulation());
    }

    /**
     * 7 countries have no cities in the database, and every one of them should still be listed with 0.
     */
    @Test
    void sevenCountriesHaveNoCities() {
        int noCities = 0;
        for (PopulationBreakdown country : countries) {
            if (country.getCityPopulation() == 0) {
                noCities++;
            }
        }
        assertEquals(7, noCities);
    }

    /**
     * The regions Antarctica and Micronesia/Caribbean have no cities, so they should show 0 in cities.
     */
    @Test
    void regionsWithNoCitiesShowZero() {
        assertEquals(0, lineFor(regions, "Antarctica").getCityPopulation());
        assertEquals(0, lineFor(regions, "Micronesia/Caribbean").getCityPopulation());
    }

    // ===== order =====

    /**
     * All 7 continents, in A to Z order.
     */
    @Test
    void continentsAreInOrder() {
        String[] expected = {"Africa", "Antarctica", "Asia", "Europe",
                "North America", "Oceania", "South America"};

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], continents.get(i).getName());
        }
    }

    /**
     * Regions are in A to Z order, from "Antarctica" to "Western Europe".
     */
    @Test
    void regionsAreInOrder() {
        assertEquals("Antarctica", regions.get(0).getName());
        assertEquals("Western Europe", regions.get(regions.size() - 1).getName());

        for (int i = 1; i < regions.size(); i++) {
            String before = regions.get(i - 1).getName();
            String now = regions.get(i).getName();
            assertTrue(before.compareToIgnoreCase(now) < 0, before + " should be before " + now);
        }
    }

    /**
     * Countries are in A to Z order, so Afghanistan is first and Zimbabwe is last.
     */
    @Test
    void countriesAreInOrder() {
        assertEquals("Afghanistan", countries.get(0).getName());
        assertEquals("Zimbabwe", countries.get(countries.size() - 1).getName());
    }

    // ===== checks on everything =====

    /**
     * Adds up the city people of every line in a list.
     *
     * @param lines the list to add up
     * @return all the city people added together
     */
    private long addUpCities(List<PopulationBreakdown> lines) {
        long total = 0;
        for (PopulationBreakdown line : lines) {
            total += line.getCityPopulation();
        }
        return total;
    }

    /**
     * Everyone living in a city in the whole database is 1,429,559,884.
     * Continents, regions and countries should each add up to exactly that.
     * If a city got counted twice (or missed) this would fail.
     */
    @Test
    void cityPeopleAddUpForEverything() {
        long allCityPeople = 1429559884L;

        assertEquals(allCityPeople, addUpCities(continents));
        assertEquals(allCityPeople, addUpCities(regions));
        assertEquals(allCityPeople, addUpCities(countries));
    }

    /**
     * The breakdown and the population lookup (UC5) should agree on every continent's total.
     */
    @Test
    void breakdownAgreesWithTheLookup() {
        PopulationReport lookup = new PopulationReport(con.getConnection());

        for (PopulationBreakdown continent : continents) {
            List<Population> answers = lookup.getPopulation("continent", continent.getName());

            assertEquals(1, answers.size(), continent.getName());
            assertEquals(answers.get(0).getTotalPopulation(), continent.getTotalPopulation(), continent.getName());
        }
    }

    /**
     * Continents, regions and countries should each add up to the world.
     * (If the cities were joined on wrong, people would get counted twice and this would fail.)
     */
    @Test
    void everythingAddsUpToTheWorld() {
        assertEquals(WORLD, addUpTotals(continents));
        assertEquals(WORLD, addUpTotals(regions));
        assertEquals(WORLD, addUpTotals(countries));
    }

    /**
     * Checks every single line in all three breakdowns makes sense.
     */
    @Test
    void everyLineMakesSense() {
        checkLines(continents);
        checkLines(regions);
        checkLines(countries);
    }

    /**
     * Checks every line in a list: it has a name, the total isn't minus,
     * city + non-city gives the total, and the percentages add up to 100
     * (or 0 if nobody lives there).
     *
     * @param lines the list to check
     */
    private void checkLines(List<PopulationBreakdown> lines) {
        for (PopulationBreakdown line : lines) {
            String name = line.getName();

            assertNotNull(name);
            assertFalse(name.isEmpty());
            assertTrue(line.getTotalPopulation() >= 0, name);

            assertEquals(line.getTotalPopulation(),
                    line.getCityPopulation() + line.getNonCityPopulation(),
                    name + " should add up");

            double percentages = line.getCityPercentage() + line.getNonCityPercentage();
            if (line.getTotalPopulation() == 0) {
                assertEquals(0.0, percentages, CLOSE_ENOUGH, name);
            } else {
                assertEquals(100.0, percentages, CLOSE_ENOUGH, name);
            }
        }
    }
}
