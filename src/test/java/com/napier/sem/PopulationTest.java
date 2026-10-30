package com.napier.sem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Population class (UC5).
 * <p>
 * These don't need the database. They just check that a Population holds onto
 * good data and says no to bad data.
 */
public class PopulationTest {

    /** A normal Population for France that gets made fresh before every test. */
    private Population france;

    /**
     * Makes a brand new France before each test, so one test can't mess up another one.
     */
    @BeforeEach
    void makeFrance() {
        france = new Population("France", 59225700);
    }

    /**
     * Whatever we put in, we should get back out.
     */
    @Test
    void keepsTheNameAndNumber() {
        assertEquals("France", france.getName());
        assertEquals(59225700, france.getTotalPopulation());
    }

    /**
     * Spaces at the start and end of the name should get chopped off.
     */
    @Test
    void chopsOffSpacesAroundTheName() {
        Population p = new Population("  France  ", 59225700);
        assertEquals("France", p.getName());
    }

    /**
     * 0 people is fine (nobody lives in Antarctica).
     */
    @Test
    void zeroPeopleIsFine() {
        Population p = new Population("Antarctica", 0);
        assertEquals(0, p.getTotalPopulation());
    }

    /**
     * The world has about 6 billion people, which is too big for an int,
     * so this checks the long actually holds it.
     */
    @Test
    void canHoldTheWholeWorld() {
        Population p = new Population("World", 6078749450L);
        assertEquals(6078749450L, p.getTotalPopulation());
    }

    /**
     * Null, empty and just-spaces names should all throw an error.
     */
    @Test
    void saysNoToEmptyNames() {
        assertThrows(IllegalArgumentException.class, () -> new Population(null, 100));
        assertThrows(IllegalArgumentException.class, () -> new Population("", 100));
        assertThrows(IllegalArgumentException.class, () -> new Population("   ", 100));
    }

    /**
     * You can't have minus people.
     */
    @Test
    void saysNoToMinusPeople() {
        assertThrows(IllegalArgumentException.class, () -> new Population("France", -1));
    }

    /**
     * The setters should say no to bad data too, not just the constructor.
     */
    @Test
    void settersSayNoToBadData() {
        assertThrows(IllegalArgumentException.class, () -> france.setName(" "));
        assertThrows(IllegalArgumentException.class, () -> france.setTotalPopulation(-5));
    }

    /**
     * If a setter says no, the old values should still be there afterwards.
     */
    @Test
    void badSetKeepsTheOldValues() {
        assertThrows(IllegalArgumentException.class, () -> france.setTotalPopulation(-5));
        assertThrows(IllegalArgumentException.class, () -> france.setName(""));

        assertEquals(59225700, france.getTotalPopulation());
        assertEquals("France", france.getName());
    }

    /**
     * The setters should actually change things when the data is fine.
     */
    @Test
    void goodSetChangesTheValues() {
        france.setName("Spain");
        france.setTotalPopulation(39441700);

        assertEquals("Spain", france.getName());
        assertEquals(39441700, france.getTotalPopulation());
    }

    /**
     * The printed sentence should have the name and the number with commas in it.
     */
    @Test
    void printsANiceSentence() {
        String expected = "France has a population of " + String.format("%,d", 59225700L);
        assertEquals(expected, france.toString());
    }
}
