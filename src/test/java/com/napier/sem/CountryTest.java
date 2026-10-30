package com.napier.sem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Country class (UC1).
 * <p>
 * These don't need the database. They just check that a Country holds onto
 * good data and says no to bad data.
 */
public class CountryTest {

    /** A normal country (the UK) that gets made fresh before every test. */
    private Country uk;

    /** Antarctica, which has nobody living there and no capital. */
    private Country antarctica;

    /**
     * Makes brand new countries before each test, so one test can't mess up another one.
     */
    @BeforeEach
    void makeCountries() {
        uk = new Country("GBR", "United Kingdom", "Europe", "British Islands", 59623400, 456, "London");
        antarctica = new Country("ATA", "Antarctica", "Antarctica", "Antarctica", 0, null, null);
    }

    // ===== good data =====

    /**
     * Whatever we put in, we should get back out.
     */
    @Test
    void keepsAllTheValues() {
        assertEquals("GBR", uk.getCode());
        assertEquals("United Kingdom", uk.getName());
        assertEquals("Europe", uk.getContinent());
        assertEquals("British Islands", uk.getRegion());
        assertEquals(59623400, uk.getPopulation());
        assertEquals(456, uk.getCapitalId());
        assertEquals("London", uk.getCapitalName());
    }

    /**
     * The code gets tidied up: spaces chopped off and small letters turned into capitals.
     */
    @Test
    void codeGetsTidiedUp() {
        Country c = new Country(" gbr ", "United Kingdom", "Europe", "British Islands", 59623400, 456, "London");
        assertEquals("GBR", c.getCode());
    }

    /**
     * Antarctica has 0 people and no capital, and that's all fine.
     */
    @Test
    void zeroPeopleAndNoCapitalIsFine() {
        assertEquals(0, antarctica.getPopulation());
        assertNull(antarctica.getCapitalId());
        assertNull(antarctica.getCapitalName());
    }

    /**
     * A capital name that's just spaces gets saved as null, which means "no capital".
     */
    @Test
    void blankCapitalNameBecomesNull() {
        Country c = new Country("ATA", "Antarctica", "Antarctica", "Antarctica", 0, null, "   ");
        assertNull(c.getCapitalName());
    }

    // ===== bad data =====

    /**
     * The code has to be exactly 3 letters, so 2 letters or a number in it should throw an error.
     */
    @Test
    void saysNoToBadCodes() {
        assertThrows(IllegalArgumentException.class, () ->
                new Country("GB", "United Kingdom", "Europe", "British Islands", 1, null, null));
        assertThrows(IllegalArgumentException.class, () ->
                new Country("G8R", "United Kingdom", "Europe", "British Islands", 1, null, null));
    }

    /**
     * The name can't be null, just spaces, or longer than 100 characters.
     */
    @Test
    void saysNoToBadNames() {
        String longName = "A".repeat(101);

        assertThrows(IllegalArgumentException.class, () ->
                new Country("GBR", null, "Europe", "British Islands", 1, null, null));
        assertThrows(IllegalArgumentException.class, () ->
                new Country("GBR", "   ", "Europe", "British Islands", 1, null, null));
        assertThrows(IllegalArgumentException.class, () ->
                new Country("GBR", longName, "Europe", "British Islands", 1, null, null));
    }

    /**
     * The continent has to be a real one, and the region can't be empty.
     */
    @Test
    void saysNoToBadContinentsAndRegions() {
        assertThrows(IllegalArgumentException.class, () ->
                new Country("GBR", "United Kingdom", "Atlantis", "British Islands", 1, null, null));
        assertThrows(IllegalArgumentException.class, () ->
                new Country("GBR", "United Kingdom", "Europe", "", 1, null, null));
    }

    /**
     * The population can't be below 0 and it can't be missing.
     */
    @Test
    void saysNoToBadPopulations() {
        assertThrows(IllegalArgumentException.class, () ->
                new Country("GBR", "United Kingdom", "Europe", "British Islands", -1, null, null));
        assertThrows(IllegalArgumentException.class, () ->
                new Country("GBR", "United Kingdom", "Europe", "British Islands", null, null, null));
    }

    /**
     * The capital ID has to be above 0 if there is one.
     */
    @Test
    void saysNoToCapitalIdOfZero() {
        assertThrows(IllegalArgumentException.class, () ->
                new Country("GBR", "United Kingdom", "Europe", "British Islands", 1, 0, "London"));
    }

    /**
     * The code, continent and region can't be null either.
     */
    @Test
    void saysNoToNullCodeContinentAndRegion() {
        assertThrows(IllegalArgumentException.class, () ->
                new Country(null, "United Kingdom", "Europe", "British Islands", 1, null, null));
        assertThrows(IllegalArgumentException.class, () ->
                new Country("GBR", "United Kingdom", null, "British Islands", 1, null, null));
        assertThrows(IllegalArgumentException.class, () ->
                new Country("GBR", "United Kingdom", "Europe", null, 1, null, null));
    }

    /**
     * A minus capital ID is no good either, not just 0.
     */
    @Test
    void saysNoToMinusCapitalId() {
        assertThrows(IllegalArgumentException.class, () ->
                new Country("GBR", "United Kingdom", "Europe", "British Islands", 1, -1, "London"));
    }

    /**
     * The region and capital name can't be longer than 100 characters.
     */
    @Test
    void saysNoToLongRegionAndCapital() {
        String tooLong = "A".repeat(101);

        assertThrows(IllegalArgumentException.class, () ->
                new Country("GBR", "United Kingdom", "Europe", tooLong, 1, null, null));
        assertThrows(IllegalArgumentException.class, () ->
                new Country("GBR", "United Kingdom", "Europe", "British Islands", 1, 456, tooLong));
    }

    /**
     * Exactly 100 characters is right on the limit, so that should be fine.
     */
    @Test
    void exactly100CharactersIsFine() {
        String hundred = "A".repeat(100);
        Country c = new Country("GBR", hundred, "Europe", hundred, 1, 456, hundred);

        assertEquals(hundred, c.getName());
        assertEquals(hundred, c.getRegion());
        assertEquals(hundred, c.getCapitalName());
    }

    /**
     * Spaces around the name, continent, region and capital get chopped off.
     */
    @Test
    void spacesGetChoppedOff() {
        Country c = new Country("GBR", " United Kingdom ", "  Europe ", " British Islands ", 1, 456, " London ");

        assertEquals("United Kingdom", c.getName());
        assertEquals("Europe", c.getContinent());
        assertEquals("British Islands", c.getRegion());
        assertEquals("London", c.getCapitalName());
    }

    /**
     * The setters should actually change things when the data is fine.
     */
    @Test
    void goodSetChangesTheValues() {
        uk.setCode("fra");
        uk.setName("France");
        uk.setContinent("Europe");
        uk.setRegion("Western Europe");
        uk.setPopulation(59225700);
        uk.setCapitalId(2974);
        uk.setCapitalName("Paris");

        assertEquals("FRA", uk.getCode());
        assertEquals("France", uk.getName());
        assertEquals("Western Europe", uk.getRegion());
        assertEquals(59225700, uk.getPopulation());
        assertEquals(2974, uk.getCapitalId());
        assertEquals("Paris", uk.getCapitalName());
    }

    /**
     * If a setter says no, the old value should still be there afterwards.
     * This goes through every setter.
     */
    @Test
    void badSetKeepsTheOldValue() {
        assertThrows(IllegalArgumentException.class, () -> uk.setCode("XX"));
        assertThrows(IllegalArgumentException.class, () -> uk.setName(""));
        assertThrows(IllegalArgumentException.class, () -> uk.setContinent("Atlantis"));
        assertThrows(IllegalArgumentException.class, () -> uk.setRegion(" "));
        assertThrows(IllegalArgumentException.class, () -> uk.setPopulation(-5));
        assertThrows(IllegalArgumentException.class, () -> uk.setCapitalId(0));

        assertEquals("GBR", uk.getCode());
        assertEquals("United Kingdom", uk.getName());
        assertEquals("Europe", uk.getContinent());
        assertEquals("British Islands", uk.getRegion());
        assertEquals(59623400, uk.getPopulation());
        assertEquals(456, uk.getCapitalId());
    }

    // ===== printing =====

    /**
     * One line of the report should have all six columns in it,
     * and the population should have commas in it.
     */
    @Test
    void reportRowHasEverything() {
        String row = uk.toReportRow();

        assertTrue(row.contains("GBR"));
        assertTrue(row.contains("United Kingdom"));
        assertTrue(row.contains("Europe"));
        assertTrue(row.contains("British Islands"));
        assertTrue(row.contains("London"));
        assertTrue(row.contains(String.format("%,d", 59623400)));
    }

    /**
     * A country with no capital shows "N/A" at the end of its line.
     */
    @Test
    void noCapitalShowsNA() {
        assertTrue(antarctica.toReportRow().endsWith("N/A"));
    }

    /**
     * toString should have the code in it.
     */
    @Test
    void toStringHasTheCode() {
        assertTrue(uk.toString().contains("GBR"));
    }
}
