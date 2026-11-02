package com.napier.sem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the CapitalCity class (UC3).
 * These don't need the database, they just check that a capital city
 * holds onto good data and says no to bad data
 */

public class CapitalCityTest {

    // A normal capital city (Edinburgh) that gets made fresh before every test
    private CapitalCity edinburgh;

    // Makes a brand new capital city before each test, so one test can't cause issues for another
    @BeforeEach
    void makeCapitalCity() {
        edinburgh = new CapitalCity();
        edinburgh.setId(459);
        edinburgh.setName("Edinburgh");
        edinburgh.setCountryCode("GBR");
        edinburgh.setCountryName("United Kingdom");
        edinburgh.setPopulation(450180);
    }

    // ===== all fields =====

    // Whatever we put in, we should get back out
    @Test
    void keepsAllTheValues() {
        assertEquals(459, edinburgh.getId());
        assertEquals("Edinburgh", edinburgh.getName());
        assertEquals("GBR", edinburgh.getCountryCode());
        assertEquals("United Kingdom", edinburgh.getCountryName());
        assertEquals(450180, edinburgh.getPopulation());
    }

    // Spaces around the name, country code, and country name get chopped off
    @Test
    void spacesGetChoppedOff() {
        CapitalCity cc = new CapitalCity();
        cc.setName("  Glasgow  ");
        cc.setCountryCode("  GBR  ");
        cc.setCountryName(" United Kingdom ");

        assertEquals("Glasgow", cc.getName());
        assertEquals("GBR", cc.getCountryCode());
        assertEquals("United Kingdom", cc.getCountryName());
    }

    // ===== id =====

    // The ID has to be above 0, so 0 and negative numbers should throw an error
    @Test
    void saysNoToBadIds() {
        assertThrows(IllegalArgumentException.class, () -> edinburgh.setId(0));
        assertThrows(IllegalArgumentException.class, () -> edinburgh.setId(-1));
    }

    // ===== name =====

    // The name can't be null or just spaces
    @Test
    void saysNoToBadNames() {
        assertThrows(IllegalArgumentException.class, () -> edinburgh.setName(null));
        assertThrows(IllegalArgumentException.class, () -> edinburgh.setName("   "));
    }

    // ===== country code =====

    // The country code can't be null, blank, or anything other than 3 characters
    @Test
    void saysNoToBadCountryCodes() {
        assertThrows(IllegalArgumentException.class, () -> edinburgh.setCountryCode(null));
        assertThrows(IllegalArgumentException.class, () -> edinburgh.setCountryCode("   "));
        assertThrows(IllegalArgumentException.class, () -> edinburgh.setCountryCode("GB"));
        assertThrows(IllegalArgumentException.class, () -> edinburgh.setCountryCode("GBRR"));
    }

    // ===== country name =====

    // The country name can't be null or just spaces
    @Test
    void saysNoToBadCountryNames() {
        assertThrows(IllegalArgumentException.class, () -> edinburgh.setName(null));
        assertThrows(IllegalArgumentException.class, () -> edinburgh.setName(""));
    }

    // ===== population =====

    // A population of 0 is allowed
    @Test
    void zeroPopulationIsFine() {
        edinburgh.setPopulation(0);
        assertEquals(0, edinburgh.getPopulation());
    }

    // The population can't be below 0
    @Test
    void saysNoToNegativePopulation() {
        assertThrows(IllegalArgumentException.class, () -> edinburgh.setPopulation(-1));
    }

    // ===== bad setters =====

    /**
     * If a setter says no, the old value should still be there afterwards
     * This goes through every setter that can say no
     */
    @Test
    void badSetKeepsTheOldValue() {
        assertThrows(IllegalArgumentException.class, () -> edinburgh.setId(0));
        assertThrows(IllegalArgumentException.class, () -> edinburgh.setName(""));
        assertThrows(IllegalArgumentException.class, () -> edinburgh.setCountryCode("XX"));
        assertThrows(IllegalArgumentException.class, () -> edinburgh.setCountryName(" "));
        assertThrows(IllegalArgumentException.class, () -> edinburgh.setPopulation(-5));

        assertEquals(459, edinburgh.getId());
        assertEquals("Edinburgh", edinburgh.getName());
        assertEquals("GBR", edinburgh.getCountryCode());
        assertEquals("United Kingdom", edinburgh.getCountryName());
        assertEquals(450180, edinburgh.getPopulation());
    }

    // ===== printing =====

    // toString should have the capital city's name and population in it
    @Test
    void toStringHasTheDetails() {
        String text = edinburgh.toString();

        assertTrue(text.contains("Edinburgh"));
        assertTrue(text.contains("450180"));
    }
}
