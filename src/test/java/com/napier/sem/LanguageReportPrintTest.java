package com.napier.sem;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Unit tests for what LanguageReport prints.
// These do not need the database: we give the report a null connection (no database)
// and catch everything it prints to the screen so we can check it.
public class LanguageReportPrintTest {

    // Runs displayLanguageReport() with no database and gives back what it printed.
    private String printedWithNoDatabase() {
        // step 1: swap System.out for a place we can read from
        PrintStream original = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            // step 2: run the report
            new LanguageReport(null).displayLanguageReport();
        } finally {
            // step 3: always put the normal System.out back
            System.setOut(original);
        }
        return captured.toString();
    }

    // The report always starts with its title.
    @Test
    void reportShowsItsTitle() {
        // step 1: run the report with no database
        String text = printedWithNoDatabase();
        // step 2: check the title is there
        assertTrue(text.contains("World Language Population Report"));
    }

    // The column headings are printed even if there is no data.
    @Test
    void reportShowsTheColumnHeadings() {
        // step 1: run the report with no database
        String text = printedWithNoDatabase();
        // step 2: check all three headings are there
        assertTrue(text.contains("Language"));
        assertTrue(text.contains("Total Speakers"));
        assertTrue(text.contains("World Population (%)"));
    }

    // With no data the report says so, instead of printing an empty table.
    @Test
    void emptyDataShowsAnErrorMessage() {
        // step 1: run the report with no database
        String text = printedWithNoDatabase();
        // step 2: check the error message is there
        assertTrue(text.contains("[ERROR]"));
        assertTrue(text.contains("missing or no records found"));
    }

    // With no database we get an empty list, never null.
    @Test
    void noDatabaseGivesAnEmptyList() {
        // step 1: ask for the statistics with no database
        List<Language> languages = new LanguageReport(null).getLanguageStatistics();
        // step 2: check the list is not null and is empty
        assertNotNull(languages);
        assertTrue(languages.isEmpty());
    }

    // Running the report twice gives the same output both times.
    @Test
    void reportCanBeRunTwice() {
        // step 1: run the report two times
        String first = printedWithNoDatabase();
        String second = printedWithNoDatabase();
        // step 2: check both times printed the same thing
        assertEquals(first, second);
    }
}
