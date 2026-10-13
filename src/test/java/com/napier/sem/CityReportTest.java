package com.napier.sem;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

// Create unit tests for the CityReport class that do not need a database
public class CityReportTest {

    // The checks run before the database is used, so no connection is needed
    private final CityReport report = new CityReport(null);

    // Test that N of zero is rejected (boundary)
    @Test
    void topNRejectsZero() {
        assertThrows(IllegalArgumentException.class, () -> report.getTopCitiesInWorld(0));
    }

    // Test that a negative N is rejected
    @Test
    void topNRejectsNegative() {
        assertThrows(IllegalArgumentException.class, () -> report.getTopCitiesInWorld(-5));
    }

    // Test that a blank continent is rejected
    @Test
    void continentRejectsBlank() {
        assertThrows(IllegalArgumentException.class, () -> report.getAllCitiesInContinent(""));
    }

    // Test that a null district is rejected
    @Test
    void districtRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> report.getAllCitiesInDistrict(null));
    }

    // Test that printing an empty list does not crash
    @Test
    void printCitiesHandlesEmptyList() {
        assertDoesNotThrow(() -> report.printCities(new ArrayList<>()));
    }

    // Test that printing null does not crash
    @Test
    void printCitiesHandlesNull() {
        assertDoesNotThrow(() -> report.printCities(null));
    }
}