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
}
