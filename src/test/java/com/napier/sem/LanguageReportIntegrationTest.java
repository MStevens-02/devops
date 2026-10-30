package com.napier.sem;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test class for LanguageReport.
 * Executes live query against the Docker database container and asserts
 * that the 5 target languages are present with non-zero counts and sorted correctly.
 */
public class LanguageReportIntegrationTest {

    private static Connection con;

    /**
     * Connects to the local Docker database instance prior to test execution.
     */
    @BeforeAll
    public static void init() {
        DatabaseConnector dbConnector = new DatabaseConnector();
        dbConnector.connect("localhost:33060", 30000);
        con = dbConnector.getConnection();
    }

    /**
     * Asserts that the report returns exactly the 5 target languages
     * (Chinese, English, Hindi, Spanish, Arabic), that they have non-zero counts,
     * and that they are sorted in descending order.
     */
    @Test
    public void testLanguageReportIntegrationData() {
        assertNotNull(con, "Database connection must be active for integration tests.");

        LanguageReport report = new LanguageReport(con);
        List<Language> languages = report.getLanguageStatistics();

        // 1. Verify list is not null and contains exactly 5 items
        assertNotNull(languages, "Language statistics list should not be null.");
        assertEquals(5, languages.size(), "Report must include exactly 5 target languages.");

        // 2. Explicitly verify all 5 target languages (Chinese, English, Hindi, Spanish, Arabic) are present
        Set<String> expectedLanguages = Set.of("Chinese", "English", "Hindi", "Spanish", "Arabic");
        Set<String> actualLanguages = languages.stream()
                .map(Language::getLanguageName)
                .collect(Collectors.toSet());

        assertEquals(expectedLanguages, actualLanguages, "The 5 mandatory languages must be present in the report.");

        // 3. Verify non-zero counts and descending order
        long previousSpeakers = Long.MAX_VALUE;
        for (Language lang : languages) {
            assertTrue(lang.getTotalSpeakers() > 0,
                    "Language " + lang.getLanguageName() + " should have non-zero speakers.");

            assertTrue(lang.getTotalSpeakers() <= previousSpeakers,
                    "Languages must be ordered from most to fewest speakers.");

            previousSpeakers = lang.getTotalSpeakers();
        }
    }
}