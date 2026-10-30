package com.napier.sem;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for PopulationReport (UC5, the population lookup).
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
public class PopulationReportIntegrationTest {

    static DatabaseConnector con;
    static PopulationReport report;

    // Connects to the database once, before all the tests.
    @BeforeAll
    static void init() {
        con = new DatabaseConnector();
        con.connect("localhost:33060", 30000);
        report = new PopulationReport(con.getConnection());
    }

    // Closes the database connection after all the tests.
    @AfterAll
    static void cleanUp() {
        con.disconnect();
    }

    // Gets the one answer for a lookup that should give exactly one answer.
    private Population single(String type, String name) {
        List<Population> answers = report.getPopulation(type, name);

        assertEquals(1, answers.size(), "expected exactly one answer for " + type + " " + name);
        return answers.get(0);
    }

    // World

    // The world population is the sum of every country.
    @Test
    void worldPopulationIsCorrect() {
        Population world = single("world", "");

        assertEquals("World", world.getName());
        assertEquals(6078749450L, world.getTotalPopulation());
    }

    // The name is ignored for the world, so any name gives the same answer.
    @Test
    void worldIgnoresTheName() {
        assertEquals(6078749450L, single("world", "anything at all").getTotalPopulation());
    }

    // Continent

    // Asia is the biggest continent.
    @Test
    void asiaPopulationIsCorrect() {
        Population asia = single("continent", "Asia");

        assertEquals("Asia", asia.getName());
        assertEquals(3705025700L, asia.getTotalPopulation());
    }

    // Check one more continent.
    @Test
    void europePopulationIsCorrect() {
        assertEquals(730074600L, single("continent", "Europe").getTotalPopulation());
    }

    // Antarctica has countries but nobody lives there, so the answer is 0 (not "not found").
    @Test
    void antarcticaHasZeroPopulation() {
        assertEquals(0, single("continent", "Antarctica").getTotalPopulation());
    }

    // The 7 continents add up to the world population.
    @Test
    void continentsAddUpToTheWorld() {
        String[] continents = {"Africa", "Antarctica", "Asia", "Europe",
                "North America", "Oceania", "South America"};

        long sum = 0;
        for (String continent : continents) {
            sum = sum + single("continent", continent).getTotalPopulation();
        }

        assertEquals(single("world", "").getTotalPopulation(), sum);
    }

    // Region

    // The Caribbean region.
    @Test
    void caribbeanPopulationIsCorrect() {
        assertEquals(38140000L, single("region", "Caribbean").getTotalPopulation());
    }

    // Country

    // France has its own population stored in the country table.
    @Test
    void francePopulationIsCorrect() {
        Population france = single("country", "France");

        assertEquals("France", france.getName());
        assertEquals(59225700L, france.getTotalPopulation());
    }

    // China is the biggest country.
    @Test
    void chinaPopulationIsCorrect() {
        assertEquals(1277558000L, single("country", "China").getTotalPopulation());
    }

    // District

    // A district is the total of its cities. Scotland has 4 cities in the database.
    @Test
    void scotlandDistrictPopulationIsCorrect() {
        // Glasgow + Edinburgh + Aberdeen + Dundee
        Population scotland = single("district", "Scotland");

        assertEquals("Scotland, United Kingdom", scotland.getName());
        assertEquals(1429620L, scotland.getTotalPopulation());
    }

    // "Distrito Federal" is a district in 4 different countries, so we get 4 answers
    // (one for each country) and not one number that adds them all together.
    @Test
    void districtNameUsedInManyCountriesGivesOneAnswerForEach() {
        List<Population> answers = report.getPopulation("district", "Distrito Federal");

        assertEquals(4, answers.size());

        // the answers are in country name order: Argentina, Brazil, Mexico, Venezuela
        assertEquals("Distrito Federal, Argentina", answers.get(0).getName());
        assertEquals(2982146L, answers.get(0).getTotalPopulation());
        assertEquals("Distrito Federal, Mexico", answers.get(2).getName());
        assertEquals(8591309L, answers.get(2).getTotalPopulation());
    }

    // The district "Central" is in 7 countries.
    @Test
    void centralDistrictGivesSevenAnswers() {
        assertEquals(7, report.getPopulation("district", "Central").size());
    }

    // City

    // Edinburgh is only in the database once, and the label shows where it is.
    @Test
    void edinburghPopulationIsCorrect() {
        Population edinburgh = single("city", "Edinburgh");

        assertEquals("Edinburgh, Scotland, United Kingdom", edinburgh.getName());
        assertEquals(450180L, edinburgh.getTotalPopulation());
    }

    // Two cities are called Birmingham (England and Alabama), so we get two answers.
    @Test
    void cityNameUsedTwiceGivesTwoAnswers() {
        List<Population> answers = report.getPopulation("city", "Birmingham");

        assertEquals(2, answers.size());
    }

    // The two Birminghams are in different countries.
    @Test
    void duplicateCitiesShowTheirCountry() {
        List<Population> answers = report.getPopulation("city", "Birmingham");

        boolean foundEngland = false;
        boolean foundAlabama = false;
        for (Population answer : answers) {
            if (answer.getName().contains("United Kingdom")) {
                foundEngland = true;
                assertEquals(1013000L, answer.getTotalPopulation());
            }
            if (answer.getName().contains("United States")) {
                foundAlabama = true;
                assertEquals(242820L, answer.getTotalPopulation());
            }
        }

        assertTrue(foundEngland);
        assertTrue(foundAlabama);
    }

    // Input is cleaned

    // Capitals and spaces in the type and the name do not matter.
    @Test
    void typeAndNameAreCleaned() {
        assertEquals(59225700L, single("  COUNTRY ", "  France  ").getTotalPopulation());
    }

    // Not found

    // An area that does not exist gives an empty list (and not an error).
    @Test
    void unknownCountryGivesEmptyList() {
        assertTrue(report.getPopulation("country", "Atlantis").isEmpty());
    }

    // The same for the other types.
    @Test
    void unknownAreasGiveEmptyLists() {
        assertTrue(report.getPopulation("continent", "Narnia").isEmpty());
        assertTrue(report.getPopulation("region", "Narnia").isEmpty());
        assertTrue(report.getPopulation("district", "Narnia").isEmpty());
        assertTrue(report.getPopulation("city", "Narnia").isEmpty());
    }

    // Names are case insensitive in MySQL, but spelling must still be right.
    @Test
    void misspeltNameGivesEmptyList() {
        assertTrue(report.getPopulation("country", "Frnace").isEmpty());
    }

    // Bad input

    // A type that is not one of the 6 allowed types is refused.
    @Test
    void unknownTypeIsRejected() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> report.getPopulation("planet", "Mars"));

        assertTrue(e.getMessage().contains("planet"));
    }

    // A blank name is refused before the database is used.
    @Test
    void blankNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> report.getPopulation("country", "   "));
    }

    // Safety

    // The ? in the SQL stops SQL injection, so this trick must find nothing and must not crash.
    @Test
    void sqlInjectionDoesNotWork() {
        List<Population> answers = report.getPopulation("country", "France' OR '1'='1");

        assertTrue(answers.isEmpty());
    }

    // A name with a quote in it must not break the query either.
    @Test
    void nameWithAQuoteDoesNotCrash() {
        assertDoesNotThrow(() -> report.getPopulation("city", "O'Brien"));
    }
}
