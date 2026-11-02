package com.napier.sem;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Integration tests for the CityReport class
// These DO need the database, start the db container first
// They check that the city reports bring back the right data
// Tagged "integration" so it only runs in the integration job (it needs the database)
@Tag("integration")
public class CityReportIntegrationTest {

    // The connection to the database, shared by every test
    static DatabaseConnector con;

    // The report we are testing
    static CityReport report;

    // Connects to the database once before all the tests run
    @BeforeAll
    static void connect() {
        con = new DatabaseConnector();
        con.connect("localhost:33060", 0);
        report = new CityReport(con.getConnection());
    }

    // Disconnects once all the tests have finished
    @AfterAll
    static void disconnect() {
        con.disconnect();
    }

    // ===== good data =====

    // The world report should bring back cities, not an empty list
    @Test
    void worldIsNotEmpty() {
        List<City> cities = report.getAllCitiesInWorld();
        assertFalse(cities.isEmpty());
    }

    // Asia should have cities in it
    @Test
    void asiaIsNotEmpty() {
        List<City> cities = report.getAllCitiesInContinent("Asia");
        assertFalse(cities.isEmpty());
    }

    // The Caribbean should have cities in it
    @Test
    void caribbeanIsNotEmpty() {
        List<City> cities = report.getAllCitiesInRegion("Caribbean");
        assertFalse(cities.isEmpty());
    }

    // France has 40 cities in the database
    @Test
    void franceHasFortyCities() {
        List<City> cities = report.getAllCitiesInCountry("France");
        assertEquals(40, cities.size());
    }

    // Scotland has 4 cities in the database
    @Test
    void scotlandHasFourCities() {
        List<City> cities = report.getAllCitiesInDistrict("Scotland");
        assertEquals(4, cities.size());
    }

    // Asking for the top 5 cities in the world should give back exactly 5
    @Test
    void topCitiesInWorldGivesBackN() {
        List<City> cities = report.getTopCitiesInWorld(5);
        assertEquals(5, cities.size());
    }

    // Asking for the top 1 city in the world should give back exactly 1
    @Test
    void topOneCityGivesBackOne() {
        List<City> cities = report.getTopCitiesInWorld(1);
        assertEquals(1, cities.size());
    }

    // ===== ordering =====

    // Every city should have a population that is the same or bigger than the next one
    @Test
    void worldIsBiggestFirst() {
        List<City> cities = report.getAllCitiesInWorld();

        for (int i = 0; i < cities.size() - 1; i++) {
            assertTrue(cities.get(i).getPopulation() >= cities.get(i + 1).getPopulation());
        }
    }

    // The country report should be biggest first too
    @Test
    void franceIsBiggestFirst() {
        List<City> cities = report.getAllCitiesInCountry("France");

        for (int i = 0; i < cities.size() - 1; i++) {
            assertTrue(cities.get(i).getPopulation() >= cities.get(i + 1).getPopulation());
        }
    }

    // ===== bad data =====

    // A country that doesn't exist should give back an empty list, not crash
    @Test
    void madeUpCountryGivesEmptyList() {
        List<City> cities = report.getAllCitiesInCountry("Atlantis");
        assertTrue(cities.isEmpty());
    }

    // A blank location should be rejected
    @Test
    void saysNoToBlankLocation() {
        assertThrows(IllegalArgumentException.class, () ->
                report.getAllCitiesInCountry("   "));
    }

    // A null location should be rejected
    @Test
    void saysNoToNullLocation() {
        assertThrows(IllegalArgumentException.class, () ->
                report.getAllCitiesInCountry(null));
    }

    // Asking for a minus number of cities should be rejected
    @Test
    void saysNoToMinusN() {
        assertThrows(IllegalArgumentException.class, () ->
                report.getTopCitiesInWorld(-1));
    }
}