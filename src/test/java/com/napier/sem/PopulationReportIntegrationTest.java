package com.napier.sem;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the population lookup (UC5) that use the REAL database.
 * <p>
 * The database needs to be running on localhost:33060 for these to work
 * (start it with docker-compose). They only run when you ask for them with:
 * mvn test -Dgroups=integration -Dtest.excluded=none
 * <p>
 * All the numbers we check against come from db/world_db/world.sql.
 */
@Tag("integration")
public class PopulationReportIntegrationTest {

    /** The world population, which a few tests need. */
    private static final long WORLD = 6078749450L;

    /** The database connection, shared by every test. */
    static DatabaseConnector con;

    /** The lookup we're testing. */
    static PopulationReport report;

    /**
     * Connects to the database once before any of the tests run.
     */
    @BeforeAll
    static void connect() {
        con = new DatabaseConnector();
        con.connect("localhost:33060", 30000);
        report = new PopulationReport(con.getConnection());
    }

    /**
     * Disconnects from the database once all the tests are done.
     */
    @AfterAll
    static void disconnect() {
        con.disconnect();
    }

    /**
     * Does a lookup that should only give one answer, checks there really is just one,
     * and gives that answer back.
     *
     * @param type the type of place
     * @param name the name of the place
     * @return the one answer
     */
    private Population onlyAnswer(String type, String name) {
        List<Population> answers = report.getPopulation(type, name);
        assertEquals(1, answers.size(), "should be one answer for " + type + " " + name);
        return answers.get(0);
    }

    /**
     * Does a lookup that should only give one answer and gives back just the number.
     *
     * @param type the type of place
     * @param name the name of the place
     * @return the number of people
     */
    private long numberFor(String type, String name) {
        return onlyAnswer(type, name).getTotalPopulation();
    }

    // ===== the world =====

    /**
     * The world is every country added together.
     */
    @Test
    void world() {
        Population world = onlyAnswer("world", "");

        assertEquals("World", world.getName());
        assertEquals(WORLD, world.getTotalPopulation());
    }

    /**
     * The name doesn't matter for the world, so a random name gives the same answer.
     */
    @Test
    void worldDoesntCareAboutTheName() {
        assertEquals(WORLD, numberFor("world", "anything at all"));
    }

    // ===== continents =====

    /**
     * Asia and Europe have the right numbers, and Asia comes back with its name.
     */
    @Test
    void asiaAndEurope() {
        Population asia = onlyAnswer("continent", "Asia");

        assertEquals("Asia", asia.getName());
        assertEquals(3705025700L, asia.getTotalPopulation());
        assertEquals(730074600L, numberFor("continent", "Europe"));
    }

    /**
     * Nobody lives in Antarctica, so we should get one answer of 0, not "nothing found".
     */
    @Test
    void antarcticaIsZeroNotNothing() {
        assertEquals(0, numberFor("continent", "Antarctica"));
    }

    /**
     * All 7 continents added together should be the same as the world.
     */
    @Test
    void continentsAddUpToTheWorld() {
        String[] continents = {"Africa", "Antarctica", "Asia", "Europe",
                "North America", "Oceania", "South America"};

        long total = 0;
        for (int i = 0; i < continents.length; i++) {
            total += numberFor("continent", continents[i]);
        }

        assertEquals(WORLD, total);
    }

    // ===== regions and countries =====

    /**
     * The Caribbean region.
     */
    @Test
    void caribbean() {
        assertEquals(38140000L, numberFor("region", "Caribbean"));
    }

    /**
     * France and China.
     */
    @Test
    void franceAndChina() {
        Population france = onlyAnswer("country", "France");

        assertEquals("France", france.getName());
        assertEquals(59225700L, france.getTotalPopulation());
        assertEquals(1277558000L, numberFor("country", "China"));
    }

    // ===== districts =====

    /**
     * Scotland is Glasgow + Edinburgh + Aberdeen + Dundee added up,
     * and the name says which country it's in.
     */
    @Test
    void scotland() {
        Population scotland = onlyAnswer("district", "Scotland");

        assertEquals("Scotland, United Kingdom", scotland.getName());
        assertEquals(1429620L, scotland.getTotalPopulation());
    }

    /**
     * "Distrito Federal" is in 4 countries, so we get 4 answers in country A to Z order,
     * not one big number.
     */
    @Test
    void distritoFederalGivesOneAnswerPerCountry() {
        List<Population> answers = report.getPopulation("district", "Distrito Federal");

        assertEquals(4, answers.size());
        assertEquals("Distrito Federal, Argentina", answers.get(0).getName());
        assertEquals(2982146L, answers.get(0).getTotalPopulation());
        assertEquals("Distrito Federal, Mexico", answers.get(2).getName());
        assertEquals(8591309L, answers.get(2).getTotalPopulation());
    }

    /**
     * "Central" is a district in 7 different countries.
     */
    @Test
    void centralGivesSevenAnswers() {
        assertEquals(7, report.getPopulation("district", "Central").size());
    }

    // ===== cities =====

    /**
     * Edinburgh is only in there once, and the name says where it is.
     */
    @Test
    void edinburgh() {
        Population edinburgh = onlyAnswer("city", "Edinburgh");

        assertEquals("Edinburgh, Scotland, United Kingdom", edinburgh.getName());
        assertEquals(450180L, edinburgh.getTotalPopulation());
    }

    /**
     * There are two Birminghams (one in England and one in Alabama). We should get both,
     * with the right number for each, and be able to tell them apart by the country.
     */
    @Test
    void twoBirminghams() {
        List<Population> answers = report.getPopulation("city", "Birmingham");
        assertEquals(2, answers.size());

        boolean gotEngland = false;
        boolean gotAlabama = false;

        for (Population answer : answers) {
            if (answer.getName().contains("United Kingdom")) {
                gotEngland = true;
                assertEquals(1013000L, answer.getTotalPopulation());
            } else if (answer.getName().contains("United States")) {
                gotAlabama = true;
                assertEquals(242820L, answer.getTotalPopulation());
            }
        }

        assertTrue(gotEngland);
        assertTrue(gotAlabama);
    }

    // ===== what you type in =====

    /**
     * Capitals and extra spaces in the type and name shouldn't matter.
     */
    @Test
    void capitalsAndSpacesDontMatter() {
        assertEquals(59225700L, numberFor("  COUNTRY ", "  France  "));
    }

    /**
     * Places that don't exist (or are spelt wrong) should give an empty list, not an error.
     */
    @Test
    void madeUpPlacesGiveNothing() {
        assertTrue(report.getPopulation("country", "Atlantis").isEmpty());
        assertTrue(report.getPopulation("country", "Frnace").isEmpty());
        assertTrue(report.getPopulation("continent", "Narnia").isEmpty());
        assertTrue(report.getPopulation("region", "Narnia").isEmpty());
        assertTrue(report.getPopulation("district", "Narnia").isEmpty());
        assertTrue(report.getPopulation("city", "Narnia").isEmpty());
    }

    /**
     * A made up type or a blank name should throw an error.
     */
    @Test
    void badInputThrowsAnError() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> report.getPopulation("planet", "Mars"));
        assertTrue(e.getMessage().contains("planet"));

        assertThrows(IllegalArgumentException.class, () -> report.getPopulation("country", "   "));
    }

    // ===== safety =====

    /**
     * Someone trying to sneak SQL in through the name (SQL injection) shouldn't work.
     * If it did work we'd get every country back instead of nothing.
     */
    @Test
    void sqlInjectionDoesntWork() {
        assertTrue(report.getPopulation("country", "France' OR '1'='1").isEmpty());
    }

    /**
     * A name with a ' in it shouldn't break the query.
     */
    @Test
    void quoteInTheNameIsFine() {
        assertDoesNotThrow(() -> report.getPopulation("city", "O'Brien"));
    }
}
