package com.napier.sem;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for LanguageReport.
 * Tests edge cases and exception handling for report operations
 * in isolation without requiring a live database connection.
 */
public class LanguageReportUnitTest {

    /**
     * Verifies that getLanguageStatistics() handles a null database connection
     * gracefully by returning an empty list instead of throwing an unhandled exception.
     */
    @Test
    public void testGetLanguageStatisticsNullConnection() {
        // Arrange: Initialize report with a null database connection
        LanguageReport report = new LanguageReport(null);

        // Act: Attempt to retrieve language statistics
        List<Language> result = report.getLanguageStatistics();

        // Assert: Ensure the returned list is instantiated (not null) and empty
        assertNotNull(result, "Result list should not be null even if connection is null.");
        assertTrue(result.isEmpty(), "Result list should be empty when connection is null.");
    }

    /**
     * Verifies that displayLanguageReport() handles a null database connection
     * gracefully without crashing the application (e.g., preventing NullPointerException).
     */
    @Test
    public void testDisplayLanguageReportNullConnection() {
        // Arrange: Initialize report with a null database connection
        LanguageReport report = new LanguageReport(null);

        // Act & Assert: Ensure calling display does not throw any exceptions
        assertDoesNotThrow(() -> report.displayLanguageReport(),
                "Displaying the report with a null connection should handle errors gracefully.");
    }
}