package com.napier.sem;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the CapitalCityReport class
 * These do require the database, start the db container first
 * They check that the capital city reports bring back the right data
 */

// Tagged "integration" so it only runs in the integration job (it needs the database)
@Tag("integration")
public class CapitalCityReportIntegrationTest {

    // The connection to the database, shared by every test
    static DatabaseConnector con;

    // The report we are testing
    static CapitalCityReport report;

    // Connects to the database once before all the tests run
    @BeforeAll
    static void connect() {
        con = new DatabaseConnector();
        con.connect("localhost:33060", 0);
        report = new CapitalCityReport(con.getConnection());
    }

    // Disconnects once all the tests have finished
    @AfterAll
    static void disconnect() {
        con.disconnect();
    }

    // ===== good data =====

    // The world report should bring back capital cities, not an empty list
    @Test
    void worldIsNotEmpty() {
        List<CapitalCity> capitalcities = report.getAllCapitalCitiesInWorld();
        assertFalse(capitalcities.isEmpty());
    }

    // Asia should have capital cities in it
    @Test
    void asiaIsNotEmpty() {
        List<CapitalCity> capitalcities = report.getAllCapitalCitiesInContinent("Asia");
        assertFalse(capitalcities.isEmpty());
    }

    // The Caribbean should have capital cities in it
    @Test
    void caribbeanIsNotEmpty() {
        List<CapitalCity> capitalcities = report.getAllCapitalCitiesInRegion("Caribbean");
        assertFalse(capitalcities.isEmpty());
    }

    // Asking for the top 5 capital cities in the world should give back exactly 5
    @Test
    void topCapitalCitiesInWorldGivesBackN() {
        List<CapitalCity> capitalcities = report.getTopCapitalCitiesInWorld(5);
        assertEquals(5, capitalcities.size());
    }

    // Asking for the top 1 capital city in the world should give back exactly 1
    @Test
    void topOneCapitalCityGivesBackOne() {
        List<CapitalCity> capitalcities = report.getTopCapitalCitiesInWorld(1);
        assertEquals(1, capitalcities.size());
    }

    // ===== ordering =====

    // Every capital city should have a population that is the same or bigger than the next one
    @Test
    void worldIsBiggestFirst() {
        List<CapitalCity> capitalcities = report.getAllCapitalCitiesInWorld();

        for (int i = 0; i < capitalcities.size() - 1; i++) {
            assertTrue(capitalcities.get(i).getPopulation() >= capitalcities.get(i + 1).getPopulation());
        }
    }

    // ===== bad data =====

    // A country that doesn't exist should give back an empty list, not crash
    @Test
    void madeUpCountryGivesEmptyList() {
        List<CapitalCity> capitalcities = report.getAllCapitalCitiesInCountry("Atlantis");
        assertTrue(capitalcities.isEmpty());
    }

    // A blank location should be rejected
    @Test
    void saysNoToBlankLocation() {
        assertThrows(IllegalArgumentException.class, () -> report.getAllCapitalCitiesInCountry("    "));
    }

    // A null location should be rejected
    @Test
    void saysNoToNullLocation() {
        assertThrows(IllegalArgumentException.class, () -> report.getAllCapitalCitiesInCountry(null));
    }

    // Asking for a negative number of capital cities should be rejected
    @Test
    void saysNoToNegativeN() {
        assertThrows(IllegalArgumentException.class, () -> report.getTopCapitalCitiesInWorld(-1));
    }
}
