package com.napier.sem;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

// Unit tests for CityReport. They do not need the database.
// 1. The input checks: bad names and bad N are refused BEFORE the database is used,
//    so we can give the report a null connection (no database) and still test them.
// 2. The printing: we catch what is printed to the screen and check it.
public class CityReportCheckTest {

    // A report with no database connection.
    private final CityReport report = new CityReport(null);

    // Makes a normal city so we have something to print.
    private City makeCity(String name, String country, String district, long population) {
        City city = new City();
        city.setId(1);
        city.setName(name);
        city.setCountryCode("GBR");
        city.setCountryName(country);
        city.setDistrict(district);
        city.setPopulation(population);
        return city;
    }

    // Prints the cities with printCities() and gives back what was printed.
    private String printed(ArrayList<City> cities) {
        // step 1: swap System.out for a place we can read from
        PrintStream original = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            // step 2: print the cities
            report.printCities(cities);
        } finally {
            // step 3: always put the normal System.out back
            System.setOut(original);
        }
        return captured.toString();
    }

    // ===== bad names are refused =====

    // A null continent, region, country or district is not allowed in any "all cities" report.
    @Test
    void nullNamesAreRejectedForAllCityReports() {
        // step 1: ask for each report with a null name
        // step 2: check every one gives an error
        assertThrows(IllegalArgumentException.class, () -> report.getAllCitiesInContinent(null));
        assertThrows(IllegalArgumentException.class, () -> report.getAllCitiesInRegion(null));
        assertThrows(IllegalArgumentException.class, () -> report.getAllCitiesInCountry(null));
        assertThrows(IllegalArgumentException.class, () -> report.getAllCitiesInDistrict(null));
    }

    // An empty or blank name is not allowed in any "all cities" report.
    @Test
    void blankNamesAreRejectedForAllCityReports() {
        // step 1: ask for each report with a name of only spaces
        // step 2: check every one gives an error
        assertThrows(IllegalArgumentException.class, () -> report.getAllCitiesInContinent("   "));
        assertThrows(IllegalArgumentException.class, () -> report.getAllCitiesInRegion(""));
        assertThrows(IllegalArgumentException.class, () -> report.getAllCitiesInCountry("  "));
        assertThrows(IllegalArgumentException.class, () -> report.getAllCitiesInDistrict(""));
    }

    // The same name check is used by the "top N" reports.
    @Test
    void badNamesAreRejectedForTopCityReports() {
        // step 1: ask for each top N report with a null or blank name (N is fine)
        // step 2: check every one gives an error
        assertThrows(IllegalArgumentException.class, () -> report.getTopCitiesInContinent(null, 5));
        assertThrows(IllegalArgumentException.class, () -> report.getTopCitiesInRegion(" ", 5));
        assertThrows(IllegalArgumentException.class, () -> report.getTopCitiesInCountry("", 5));
        assertThrows(IllegalArgumentException.class, () -> report.getTopCitiesInDistrict(null, 5));
    }

    // ===== bad N is refused =====

    // N must be 1 or more in every top N report. 0 is not allowed.
    @Test
    void zeroIsRejectedForEveryTopN() {
        // step 1: ask for each top N report with N = 0 (the name is fine)
        // step 2: check every one gives an error
        assertThrows(IllegalArgumentException.class, () -> report.getTopCitiesInWorld(0));
        assertThrows(IllegalArgumentException.class, () -> report.getTopCitiesInContinent("Asia", 0));
        assertThrows(IllegalArgumentException.class, () -> report.getTopCitiesInRegion("Caribbean", 0));
        assertThrows(IllegalArgumentException.class, () -> report.getTopCitiesInCountry("France", 0));
        assertThrows(IllegalArgumentException.class, () -> report.getTopCitiesInDistrict("Scotland", 0));
    }

    // A negative N is not allowed either.
    @Test
    void negativeNIsRejectedForEveryTopN() {
        // step 1: ask for each top N report with N = -1 (the name is fine)
        // step 2: check every one gives an error
        assertThrows(IllegalArgumentException.class, () -> report.getTopCitiesInWorld(-1));
        assertThrows(IllegalArgumentException.class, () -> report.getTopCitiesInContinent("Asia", -1));
        assertThrows(IllegalArgumentException.class, () -> report.getTopCitiesInRegion("Caribbean", -1));
        assertThrows(IllegalArgumentException.class, () -> report.getTopCitiesInCountry("France", -1));
        assertThrows(IllegalArgumentException.class, () -> report.getTopCitiesInDistrict("Scotland", -1));
    }

    // The error message shows the bad number so the user can see what they typed.
    @Test
    void badNErrorMessageShowsTheNumber() {
        // step 1: ask for the top -7 cities and catch the error
        Exception error = assertThrows(IllegalArgumentException.class,
                () -> report.getTopCitiesInWorld(-7));
        // step 2: check the message has -7 in it
        assertTrue(error.getMessage().contains("-7"));
    }

    // ===== printing =====

    // A null list says there are no cities.
    @Test
    void nullListSaysNoCitiesFound() {
        // step 1: print a null list
        String text = printed(null);
        // step 2: check it says "No cities found."
        assertTrue(text.contains("No cities found."));
    }

    // An empty list says there are no cities and prints no table.
    @Test
    void emptyListSaysNoCitiesFound() {
        // step 1: print a list with nothing in it
        String text = printed(new ArrayList<>());
        // step 2: check it says "No cities found." and has no headings
        assertTrue(text.contains("No cities found."));
        assertFalse(text.contains("Population"));
    }

    // A normal list shows the headings and the city details.
    @Test
    void normalListShowsHeadingsAndCity() {
        // step 1: make a list with Edinburgh and print it
        ArrayList<City> cities = new ArrayList<>();
        cities.add(makeCity("Edinburgh", "United Kingdom", "Scotland", 450180));
        String text = printed(cities);
        // step 2: check the headings are there
        assertTrue(text.contains("Name"));
        assertTrue(text.contains("Country"));
        assertTrue(text.contains("District"));
        assertTrue(text.contains("Population"));
        // step 3: check the city details are there (the population has a comma)
        assertTrue(text.contains("Edinburgh"));
        assertTrue(text.contains("United Kingdom"));
        assertTrue(text.contains("Scotland"));
        assertTrue(text.contains("450,180"));
        // step 4: check it does not say "No cities found."
        assertFalse(text.contains("No cities found."));
    }

    // The cities are printed in the order of the list (the report does not re-order them).
    @Test
    void citiesArePrintedInTheOrderOfTheList() {
        // step 1: make a list with London first and Edinburgh second, then print it
        ArrayList<City> cities = new ArrayList<>();
        cities.add(makeCity("London", "United Kingdom", "England", 7285000));
        cities.add(makeCity("Edinburgh", "United Kingdom", "Scotland", 450180));
        String text = printed(cities);
        // step 2: check London comes before Edinburgh
        assertTrue(text.indexOf("London") < text.indexOf("Edinburgh"));
    }

    // There is one line for the headings and one line for each city.
    @Test
    void oneHeadingLineAndOneLinePerCity() {
        // step 1: print a list with 3 cities
        ArrayList<City> cities = new ArrayList<>();
        cities.add(makeCity("London", "United Kingdom", "England", 7285000));
        cities.add(makeCity("Glasgow", "United Kingdom", "Scotland", 619680));
        cities.add(makeCity("Edinburgh", "United Kingdom", "Scotland", 450180));
        String[] lines = printed(cities).trim().split("\\R");
        // step 2: check there are 4 lines (1 heading + 3 cities)
        assertEquals(4, lines.length);
    }

    // ===== no database =====

    // With no database every report gives back an empty list (not null, and no crash).
    @Test
    void noConnectionGivesEmptyLists() {
        // step 1: ask for a few reports with no database connection
        // step 2: check each one gives back an empty list
        assertTrue(report.getAllCitiesInWorld().isEmpty());
        assertTrue(report.getAllCitiesInCountry("France").isEmpty());
        assertTrue(report.getTopCitiesInWorld(5).isEmpty());
    }
}
