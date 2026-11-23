package com.napier.sem;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for the CapitalCityReport class.
 * This doesn't require a connection to the database.
 */

public class CapitalCityReportUnitTest {

    /**
     * Verifies that the CapitalCityReport constructor can handle a null database connection
    */
    @Test
    public void testCapitalCityReport() {
        // Initialise report with a null database connection
        CapitalCityReport report = new CapitalCityReport(null);

        // Attempt to retrieve capital city data
        List<CapitalCity> result = report.getAllCapitalCitiesInWorld();

        // Ensure the returned list is instantiated (not null) and empty
        assertNotNull(result, "Result list should not be null even if connection is null.");
        assertTrue(result.isEmpty(), "Result list should be empty when connection is null.");
    }

    // Test that printCapitalCities can handle a list with a null capital city in it
    @Test
    public void printCapitalCitiesTest(){
        // Create a capital city report with no database connection, because printing doesn't need one
        CapitalCityReport report = new CapitalCityReport(null);

        // Create an empty list to hold the capital cities
        ArrayList<CapitalCity> CapitalCities = new ArrayList<>();

        // Create a capital city and only give it a name
        CapitalCity london = new CapitalCity();
        london.setName("London");

        // Add London to the list
        CapitalCities.add(london);

        // Add a null to the list to check a missing capital city doesn't break the report
        CapitalCities.add(null);

        // Print the list, which should not crash
        report.printCapitalCities(CapitalCities);

    }
}
