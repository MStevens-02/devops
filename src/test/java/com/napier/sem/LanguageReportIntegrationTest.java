package com.napier.sem;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for LanguageReport (UC6).
 * They run against the real world database, so the db container must be running on port 33060.
 */
@Tag("integration")
public class LanguageReportIntegrationTest {

    private static DatabaseConnector db;
    private static LanguageReport report;

    // Connect once before all tests[cite: 1].
    @BeforeAll
    static void init() {
        db = new DatabaseConnector();
        db.connect("localhost:33060", 30000);
        assertNotNull(db.getConnection(), "Could not connect to the database");
        report = new LanguageReport(db.getConnection());
    }

    // Close the connection after all tests[cite: 1].
    @AfterAll
    static void close() {
        db.disconnect();
    }

    // The report has exactly 5 languages[cite: 1].
    @Test
    void reportHasFiveLanguages() {
        List<Language> languages = report.getLanguageStatistics();
        assertNotNull(languages);
        assertEquals(5, languages.size());
    }

    // The 5 languages are Chinese, English, Hindi, Spanish and Arabic[cite: 1].
    @Test
    void reportHasTheFiveTargetLanguages() {
        Set<String> expected = Set.of("Chinese", "English", "Hindi", "Spanish", "Arabic");
        Set<String> actual = report.getLanguageStatistics().stream()
                .map(Language::getLanguageName)
                .collect(Collectors.toSet());
        assertEquals(expected, actual);
    }

    // Every language has more than 0 speakers[cite: 1].
    @Test
    void everyLanguageHasSpeakers() {
        for (Language lang : report.getLanguageStatistics()) {
            assertTrue(lang.getTotalSpeakers() > 0, lang.getLanguageName() + " has no speakers");
        }
    }

    // The list goes from most to fewest speakers[cite: 1].
    @Test
    void languagesAreSortedMostToFewest() {
        List<Language> languages = report.getLanguageStatistics();
        for (int i = 1; i < languages.size(); i++) {
            assertTrue(languages.get(i - 1).getTotalSpeakers() >= languages.get(i).getTotalSpeakers(),
                    "Languages are not in order at position " + i);
        }
    }

    // Each percentage is a valid share of the world population (0 to 100).
    @Test
    void percentagesAreBetween0And100() {
        for (Language lang : report.getLanguageStatistics()) {
            assertTrue(lang.getWorldPercentage() >= 0 && lang.getWorldPercentage() <= 100,
                    lang.getLanguageName() + " has an invalid percentage");
        }
    }

    // Chinese has the most speakers in the world database, so it comes first[cite: 1].
    @Test
    void chineseIsFirst() {
        assertEquals("Chinese", report.getLanguageStatistics().get(0).getLanguageName());
    }

    // Printing the real report does not crash.
    @Test
    void printReportWorksWithRealData() {
        report.displayLanguageReport();
    }
}