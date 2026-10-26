package com.napier.sem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Country class.
 * <p>
 * These tests do not need the database.
 * They check that a Country keeps good data and refuses bad data.
 */
public class CountryTest {

    // Makes a normal country that we can use in many tests.
    private Country validCountry() {
        return new Country("GBR", "United Kingdom", "Europe", "British Islands",
                59623400, 456, "London");
    }

    // Good data

    // A country made with good data keeps every value.
    @Test
    void validCountryKeepsAllValues() {
        Country c = validCountry();

        assertEquals("GBR", c.getCode());
        assertEquals("United Kingdom", c.getName());
        assertEquals("Europe", c.getContinent());
        assertEquals("British Islands", c.getRegion());
        assertEquals(59623400, c.getPopulation());
        assertEquals(456, c.getCapitalId());
        assertEquals("London", c.getCapitalName());
    }

    // The code is cleaned: spaces are removed and letters become capitals.
    @Test
    void codeIsChangedToUpperCaseAndTrimmed() {
        Country c = new Country(" gbr ", "United Kingdom", "Europe", "British Islands",
                59623400, 456, "London");

        assertEquals("GBR", c.getCode());
    }

    // A population of 0 is allowed (for example, Antarctica).
    @Test
    void zeroPopulationIsAllowed() {
        Country c = new Country("ATA", "Antarctica", "Antarctica", "Antarctica", 0, null, null);

        assertEquals(0, c.getPopulation());
    }

    // A country with no capital is allowed.
    @Test
    void countryWithoutCapitalIsAllowed() {
        Country c = new Country("ATA", "Antarctica", "Antarctica", "Antarctica", 0, null, null);

        assertNull(c.getCapitalId());
        assertNull(c.getCapitalName());
    }

    // An empty capital name is saved as null, which means "no capital".
    @Test
    void blankCapitalNameIsSavedAsNull() {
        Country c = new Country("ATA", "Antarctica", "Antarctica", "Antarctica", 0, null, "   ");

        assertNull(c.getCapitalName());
    }

    // Bad code

    // The code must have exactly 3 letters.
    @Test
    void codeWithWrongLengthIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new Country("GB", "United Kingdom", "Europe", "British Islands", 1, null, null));
    }

    // The code can only have letters, not numbers.
    @Test
    void codeWithNumbersIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new Country("G8R", "United Kingdom", "Europe", "British Islands", 1, null, null));
    }

    // Bad name

    // The name cannot be missing.
    @Test
    void nullNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new Country("GBR", null, "Europe", "British Islands", 1, null, null));
    }

    // The name cannot be only spaces.
    @Test
    void blankNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new Country("GBR", "   ", "Europe", "British Islands", 1, null, null));
    }

    // The name cannot be longer than 100 letters.
    @Test
    void nameThatIsTooLongIsRejected() {
        String longName = "A".repeat(101);

        assertThrows(IllegalArgumentException.class, () ->
                new Country("GBR", longName, "Europe", "British Islands", 1, null, null));
    }

    // Bad continent and region

    // The continent must be a real one from the database.
    @Test
    void unknownContinentIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new Country("GBR", "United Kingdom", "Atlantis", "British Islands", 1, null, null));
    }

    // The region cannot be empty.
    @Test
    void blankRegionIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new Country("GBR", "United Kingdom", "Europe", "", 1, null, null));
    }

    // Bad population

    // The population cannot be below 0.
    @Test
    void negativePopulationIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new Country("GBR", "United Kingdom", "Europe", "British Islands", -1, null, null));
    }

    // The population cannot be missing.
    @Test
    void nullPopulationIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new Country("GBR", "United Kingdom", "Europe", "British Islands", null, null, null));
    }

    // Setters also check the data, and a bad value does not change the old one.
    @Test
    void setterRejectsBadValueAfterCreation() {
        Country c = validCountry();

        assertThrows(IllegalArgumentException.class, () -> c.setPopulation(-5));
        assertEquals(59623400, c.getPopulation());
    }

    // Bad capital

    // A capital city ID must be above 0.
    @Test
    void capitalIdOfZeroIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new Country("GBR", "United Kingdom", "Europe", "British Islands", 1, 0, "London"));
    }

    // Output

    // One report row shows all six columns.
    @Test
    void reportRowShowsAllColumns() {
        String row = validCountry().toReportRow();

        assertTrue(row.contains("GBR"));
        assertTrue(row.contains("United Kingdom"));
        assertTrue(row.contains("Europe"));
        assertTrue(row.contains("British Islands"));
        assertTrue(row.contains("London"));
        // The population has separators, e.g. 59,623,400.
        // String.format makes this work on any computer language setting.
        assertTrue(row.contains(String.format("%,d", 59623400)));
    }

    // A country with no capital shows "N/A" at the end of its row.
    @Test
    void reportRowShowsNAWhenThereIsNoCapital() {
        Country c = new Country("ATA", "Antarctica", "Antarctica", "Antarctica", 0, null, null);

        assertTrue(c.toReportRow().endsWith("N/A"));
    }

    // toString() includes the country code.
    @Test
    void toStringContainsTheCode() {
        assertTrue(validCountry().toString().contains("GBR"));
    }
}