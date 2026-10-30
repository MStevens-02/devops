package com.napier.sem;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * More unit tests for CountryReport.printReport.
 * <p>
 * The tests in CountryReportTest only check that printReport does not crash.
 * These tests catch what was printed and check the text itself.
 * They do not need the database.
 */
public class CountryReportPrintTest {

    // Makes a normal country that we can use in many tests.
    private Country uk() {
        return new Country("GBR", "United Kingdom", "Europe", "British Islands",
                59623400, 456, "London");
    }

    // Makes a second country so we can check the order.
    private Country france() {
        return new Country("FRA", "France", "Europe", "Western Europe",
                59225700, 2974, "Paris");
    }

    // Runs printReport and gives back everything it printed as one text.
    // System.out is put back to normal afterwards, even if something goes wrong.
    private String printed(String title, List<Country> countries) {
        PrintStream original = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(bytes));
            CountryReport.printReport(title, countries);
        } finally {
            System.setOut(original);
        }
        return bytes.toString();
    }

    // A missing list (null) prints the title and "No countries found."
    @Test
    void nullListSaysNoCountriesFound() {
        // step 1: print a report using a list that is null (no list at all)
        // step 2: check it printed the title and the words "No countries found."
        String text = printed("Null list", null);

        assertTrue(text.contains("Null list"));
        assertTrue(text.contains("No countries found."));
    }

    // An empty list prints "No countries found." and no table header.
    @Test
    void emptyListSaysNoCountriesFound() {
        // step 1: print a report using a list with nothing in it
        // step 2: check it says "No countries found." and does not print the table headings
        String text = printed("Empty list", new ArrayList<Country>());

        assertTrue(text.contains("Empty list"));
        assertTrue(text.contains("No countries found."));
        assertFalse(text.contains("Population"));
    }

    // A normal list prints the title, the column names and the country.
    @Test
    void normalListShowsTitleHeaderAndRow() {
        // step 1: make a list with one country (the UK) and print it
        // step 2: check the title, every column heading, the country name and the capital are in the text
        // step 3: check it does NOT say "No countries found."
        List<Country> countries = new ArrayList<Country>();
        countries.add(uk());

        String text = printed("Normal list", countries);

        assertTrue(text.contains("Normal list"));
        assertTrue(text.contains("Code"));
        assertTrue(text.contains("Continent"));
        assertTrue(text.contains("Region"));
        assertTrue(text.contains("Population"));
        assertTrue(text.contains("Capital"));
        assertTrue(text.contains("United Kingdom"));
        assertTrue(text.contains("London"));
        assertFalse(text.contains("No countries found."));
    }

    // The table has a line of dashes under the header.
    @Test
    void tableHasALineUnderTheHeader() {
        // step 1: print a list with one country
        // step 2: check there is a line of dashes (-----) under the headings
        List<Country> countries = new ArrayList<Country>();
        countries.add(uk());

        assertTrue(printed("Line", countries).contains("-----"));
    }

    // The countries are printed in the same order as the list.
    @Test
    void countriesAreInTheOrderOfTheList() {
        // step 1: make a list with the UK first and France second, then print it
        // step 2: check the UK comes before France in the text, so the order was not changed
        List<Country> countries = new ArrayList<Country>();
        countries.add(uk());
        countries.add(france());

        String text = printed("Order", countries);

        assertTrue(text.indexOf("United Kingdom") < text.indexOf("France"));
    }

    // A missing country in the list is skipped, and the real country is still printed once.
    @Test
    void nullCountryIsSkippedButTheRestIsPrinted() {
        // step 1: make a list with a null country first and the UK second, then print it
        // step 2: check it did not crash and the UK is printed
        // step 3: check the UK is only printed once (the null did not cause a double print)
        List<Country> countries = new ArrayList<Country>();
        countries.add(null);
        countries.add(uk());

        String text = printed("With a null", countries);

        assertTrue(text.contains("United Kingdom"));
        assertEquals(text.indexOf("United Kingdom"), text.lastIndexOf("United Kingdom"));
    }

    // A country without a capital shows N/A instead of "null".
    @Test
    void countryWithoutCapitalShowsNA() {
        // step 1: make Antarctica, which has no capital, and print it
        // step 2: check it shows N/A and does not show the word null
        List<Country> countries = new ArrayList<Country>();
        countries.add(new Country("ATA", "Antarctica", "Antarctica", "Antarctica", 0, null, null));

        String text = printed("No capital", countries);

        assertTrue(text.contains("N/A"));
        assertFalse(text.contains("null"));
    }

    // Each country gets exactly one line, so 2 countries make 2 more lines
    // than the title, the header and the line of dashes.
    @Test
    void oneLinePerCountry() {
        // step 1: print a list with 1 country and count the lines
        // step 2: print a list with 2 countries and count the lines
        // step 3: check the second one has exactly 1 more line (one line per country)
        List<Country> one = new ArrayList<Country>();
        one.add(uk());
        List<Country> two = new ArrayList<Country>();
        two.add(uk());
        two.add(france());

        int linesForOne = printed("Count", one).split("\n").length;
        int linesForTwo = printed("Count", two).split("\n").length;

        assertEquals(linesForOne + 1, linesForTwo);
    }
}
