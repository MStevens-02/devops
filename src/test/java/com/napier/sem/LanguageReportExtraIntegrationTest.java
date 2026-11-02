package com.napier.sem;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Extra integration tests for the language report (UC6).
// These DO need the database, so start the Docker database first.
@Tag("integration")
public class LanguageReportExtraIntegrationTest {

    // The world population from the database (the sum of every country).
    private static final long WORLD_POPULATION = 6078749450L;

    private static DatabaseConnector db;
    private static LanguageReport report;

    // Connects to the database once, before all the tests.
    @BeforeAll
    static void init() {
        db = new DatabaseConnector();
        db.connect("localhost:33060", 30000);
        assertNotNull(db.getConnection(), "Could not connect to the database");
        report = new LanguageReport(db.getConnection());
    }

    // Closes the database connection after all the tests.
    @AfterAll
    static void cleanUp() {
        db.disconnect();
    }

    // Chinese has the most speakers, so it must be first.
    @Test
    void chineseIsFirst() {
        // step 1: get the language statistics
        List<Language> languages = report.getLanguageStatistics();
        // step 2: check the first language is Chinese
        assertEquals("Chinese", languages.get(0).getLanguageName());
    }

    // Chinese has well over a billion speakers.
    @Test
    void chineseHasOverOneBillionSpeakers() {
        // step 1: get the language statistics
        Language chinese = report.getLanguageStatistics().get(0);
        // step 2: check the number is over 1,000,000,000 (this also proves it did not overflow an int)
        assertTrue(chinese.getTotalSpeakers() > 1_000_000_000L);
    }

    // Each percentage matches its speakers divided by the world population.
    @Test
    void percentagesMatchTheSpeakers() {
        for (Language lang : report.getLanguageStatistics()) {
            // step 1: work out the percentage ourselves
            double expected = lang.getTotalSpeakers() * 100.0 / WORLD_POPULATION;
            // step 2: check the report's percentage is the same (a small difference is okay, speakers are rounded)
            assertEquals(expected, lang.getWorldPercentage(), 0.01,
                    lang.getLanguageName() + " percentage does not match its speakers");
        }
    }

    // No language can have more speakers than there are people in the world.
    @Test
    void noLanguageHasMoreSpeakersThanTheWorld() {
        for (Language lang : report.getLanguageStatistics()) {
            // step 1: look at the speakers of each language
            // step 2: check it is not bigger than the world population
            assertTrue(lang.getTotalSpeakers() <= WORLD_POPULATION,
                    lang.getLanguageName() + " has too many speakers");
        }
    }

    // Each language only appears once.
    @Test
    void noLanguageIsListedTwice() {
        // step 1: get the names and count the different ones
        List<Language> languages = report.getLanguageStatistics();
        long different = languages.stream().map(Language::getLanguageName).distinct().count();
        // step 2: check the count of different names is the same as the number of rows
        assertEquals(languages.size(), different);
    }

    // The percentages are in the same order as the speakers (biggest first).
    @Test
    void percentagesAreInTheSameOrderAsSpeakers() {
        // step 1: get the language statistics
        List<Language> languages = report.getLanguageStatistics();
        // step 2: check each percentage is the same or lower than the one before
        for (int i = 1; i < languages.size(); i++) {
            assertTrue(languages.get(i - 1).getWorldPercentage() >= languages.get(i).getWorldPercentage(),
                    "Percentages are not in order at position " + i);
        }
    }

    // Asking twice gives the same answer.
    @Test
    void askingTwiceGivesTheSameAnswer() {
        // step 1: get the statistics two times
        List<Language> first = report.getLanguageStatistics();
        List<Language> second = report.getLanguageStatistics();
        // step 2: check both lists have the same languages in the same order
        assertEquals(first.size(), second.size());
        for (int i = 0; i < first.size(); i++) {
            assertEquals(first.get(i).getLanguageName(), second.get(i).getLanguageName());
            assertEquals(first.get(i).getTotalSpeakers(), second.get(i).getTotalSpeakers());
        }
    }
}
