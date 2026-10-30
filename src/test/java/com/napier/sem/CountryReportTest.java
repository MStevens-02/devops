package com.napier.sem;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the CountryReport class that DON'T need the database.
 * <p>
 * We try printing the 4 kinds of list printReport could get: null, empty,
 * a list with a null in it, and a normal list. We check it doesn't crash
 * and that it actually prints the right stuff.
 */
public class CountryReportTest {

    /**
     * Runs printReport and grabs everything it prints, so we can check it.
     * It swaps System.out for a fake one for a bit, then puts the real one back.
     *
     * @param title     the report title
     * @param countries the list to print
     * @return everything that got printed
     */
    private String printAndGrab(String title, List<Country> countries) {
        PrintStream realOut = System.out;
        ByteArrayOutputStream grabbed = new ByteArrayOutputStream();
        System.setOut(new PrintStream(grabbed));

        try {
            CountryReport.printReport(title, countries);
        } finally {
            System.setOut(realOut);
        }

        return grabbed.toString();
    }

    /**
     * You can't make a report without a database connection.
     */
    @Test
    void saysNoToNullConnection() {
        assertThrows(NullPointerException.class, () -> new CountryReport(null));
    }

    /**
     * A null list should print the title and "No countries found." and not crash.
     */
    @Test
    void printNullList() {
        String output = printAndGrab("Null list", null);

        assertTrue(output.contains("Null list"));
        assertTrue(output.contains("No countries found."));
    }

    /**
     * An empty list should print "No countries found." instead of an empty table.
     */
    @Test
    void printEmptyList() {
        String output = printAndGrab("Empty list", new ArrayList<Country>());

        assertTrue(output.contains("No countries found."));
        assertFalse(output.contains("Population"));
    }

    /**
     * A list with a null inside should skip the null and not crash.
     * It still prints the column names, just with no rows.
     */
    @Test
    void printListWithANullInIt() {
        List<Country> countries = new ArrayList<Country>();
        countries.add(null);

        String output = printAndGrab("List with a gap", countries);

        assertTrue(output.contains("Population"));
        assertFalse(output.contains("null"));
    }

    /**
     * A normal list should print the column names and a row for each country.
     * A null mixed in with real countries just gets skipped.
     */
    @Test
    void printNormalList() {
        List<Country> countries = new ArrayList<Country>();
        countries.add(new Country("GBR", "United Kingdom", "Europe", "British Islands", 59623400, 456, "London"));
        countries.add(null);
        countries.add(new Country("ATA", "Antarctica", "Antarctica", "Antarctica", 0, null, null));

        String output = printAndGrab("Normal list", countries);

        assertTrue(output.contains("Code"));
        assertTrue(output.contains("Capital"));
        assertTrue(output.contains("United Kingdom"));
        assertTrue(output.contains("London"));
        assertTrue(output.contains("Antarctica"));
        assertTrue(output.contains("N/A"));
        assertFalse(output.contains("No countries found."));
    }
}
