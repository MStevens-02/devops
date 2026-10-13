package com.napier.sem;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Create unit tests for the City class
public class CityTest {

    // Test that a valid name is stored and trimmed
    @Test
    void setNameStoresTrimmedName() {
        City city = new City();
        city.setName("  Edinburgh  ");
        assertEquals("Edinburgh", city.getName());
    }

    // Test that a blank name is rejected
    @Test
    void setNameRejectsBlank() {
        City city = new City();
        assertThrows(IllegalArgumentException.class, () -> city.setName("   "));
    }

    // Test that a null name is rejected
    @Test
    void setNameRejectsNull() {
        City city = new City();
        assertThrows(IllegalArgumentException.class, () -> city.setName(null));
    }

    // Test that a country code must be 3 characters
    @Test
    void setCountryCodeRejectsWrongLength() {
        City city = new City();
        assertThrows(IllegalArgumentException.class, () -> city.setCountryCode("GB"));
    }

    // Test that a negative population is rejected
    @Test
    void setPopulationRejectsNegative() {
        City city = new City();
        assertThrows(IllegalArgumentException.class, () -> city.setPopulation(-1));
    }

    // Test that zero population is allowed (boundary)
    @Test
    void setPopulationAllowsZero() {
        City city = new City();
        city.setPopulation(0);
        assertEquals(0, city.getPopulation());
    }

    // Test that an id of zero is rejected (boundary)
    @Test
    void setIdRejectsZero() {
        City city = new City();
        assertThrows(IllegalArgumentException.class, () -> city.setId(0));
    }
}