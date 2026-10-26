package com.napier.sem;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the CountryReport class.
 * <p>
 * These tests do not need the database.
 * Like in Lab 06, printReport is tested with the four kinds of list it can get:
 * a missing list (null), an empty list, a list with a missing country inside,
 * and a normal list. A test passes if the program does not crash.
 */
public class CountryReportTest {

    // A report cannot be made without a database connection.
    @Test
    void nullConnectionIsRejected() {
        assertThrows(NullPointerException.class, () -> new CountryReport(null));
    }

    // Printing a missing list (null) does not crash.
    // It prints "No countries found." instead.
    @Test
    void printReportTestNull() {
        CountryReport.printReport("Null list", null);
    }

    // Printing an empty list does not crash.
    // It prints "No countries found." instead of an empty table.
    @Test
    void printReportTestEmpty() {
        CountryReport.printReport("Empty list", new ArrayList<>());
    }

    // Printing a list with a missing country inside does not crash.
    // The missing country is skipped.
    @Test
    void printReportTestContainsNull() {
        List<Country> countries = new ArrayList<>();
        countries.add(null);

        CountryReport.printReport("List with a null", countries);
    }

    // Printing a normal list shows the table with one row per country.
    @Test
    void printReportTestNormal() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("GBR", "United Kingdom", "Europe", "British Islands",
                59623400, 456, "London"));

        CountryReport.printReport("Normal list", countries);
    }
}