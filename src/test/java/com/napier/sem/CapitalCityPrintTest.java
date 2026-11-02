package com.napier.sem;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

// Unit tests for CapitalCityReport.printCapitalCities().
// These do not need the database. We catch what is printed and check it.
public class CapitalCityPrintTest {

    // A report with no database (printing does not use the database).
    private final CapitalCityReport report = new CapitalCityReport(null);

    // Makes a capital city so we have something to print.
    private CapitalCity makeCapital(String name, String country, long population) {
        CapitalCity capital = new CapitalCity();
        capital.setId(1);
        capital.setName(name);
        capital.setCountryCode("GBR");
        capital.setCountryName(country);
        capital.setPopulation(population);
        return capital;
    }

    // Prints the list and gives back what was printed.
    private String printed(ArrayList<CapitalCity> capitals) {
        // step 1: swap System.out for a place we can read from
        PrintStream original = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            // step 2: print the list
            report.printCapitalCities(capitals);
        } finally {
            // step 3: always put the normal System.out back
            System.setOut(original);
        }
        return captured.toString();
    }

    // A null list says there are no capital cities.
    @Test
    void nullListSaysNoCapitalCitiesFound() {
        // step 1: print a null list
        // step 2: check it says "No capital cities found"
        assertTrue(printed(null).contains("No capital cities found"));
    }

    // An empty list says there are no capital cities.
    @Test
    void emptyListSaysNoCapitalCitiesFound() {
        // step 1: print a list with nothing in it
        // step 2: check it says "No capital cities found"
        assertTrue(printed(new ArrayList<>()).contains("No capital cities found"));
    }

    // A normal list prints the headings and the capital city (this used to crash).
    @Test
    void normalListShowsHeadingsAndCapital() {
        // step 1: print a list with London
        ArrayList<CapitalCity> capitals = new ArrayList<>();
        capitals.add(makeCapital("London", "United Kingdom", 7285000));
        String text = printed(capitals);
        // step 2: check the headings are there
        assertTrue(text.contains("Name"));
        assertTrue(text.contains("Country"));
        assertTrue(text.contains("Population"));
        // step 3: check the capital city details are there (the population has commas)
        assertTrue(text.contains("London"));
        assertTrue(text.contains("United Kingdom"));
        assertTrue(text.contains("7,285,000"));
    }

    // The capital cities are printed in the order of the list.
    @Test
    void capitalsArePrintedInTheOrderOfTheList() {
        // step 1: print a list with Paris first and London second
        ArrayList<CapitalCity> capitals = new ArrayList<>();
        capitals.add(makeCapital("Paris", "France", 2125246));
        capitals.add(makeCapital("London", "United Kingdom", 7285000));
        String text = printed(capitals);
        // step 2: check Paris comes before London
        assertTrue(text.indexOf("Paris") < text.indexOf("London"));
    }

    // There is one line for the headings and one line for each capital city.
    @Test
    void oneHeadingLineAndOneLinePerCapital() {
        // step 1: print a list with 2 capital cities
        ArrayList<CapitalCity> capitals = new ArrayList<>();
        capitals.add(makeCapital("London", "United Kingdom", 7285000));
        capitals.add(makeCapital("Paris", "France", 2125246));
        String[] lines = printed(capitals).trim().split("\\R");
        // step 2: check there are 3 lines (1 heading + 2 capitals)
        assertEquals(3, lines.length);
    }
}
