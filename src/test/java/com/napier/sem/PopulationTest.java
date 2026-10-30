package com.napier.sem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Population class (UC5).
 * <p>
 * These tests do not need the database.
 * They check that a Population keeps good data and refuses bad data.
 */
public class PopulationTest {

    // Good data

    // A Population made with good data keeps both values.
    @Test
    void validPopulationKeepsAllValues() {
        Population p = new Population("France", 59225700);

        assertEquals("France", p.getName());
        assertEquals(59225700, p.getTotalPopulation());
    }

    // Spaces around the name are removed.
    @Test
    void nameIsTrimmed() {
        Population p = new Population("  France  ", 59225700);

        assertEquals("France", p.getName());
    }

    // A population of 0 is allowed (for example, Antarctica).
    @Test
    void zeroPopulationIsAllowed() {
        Population p = new Population("Antarctica", 0);

        assertEquals(0, p.getTotalPopulation());
    }

    // The world population is bigger than an int can hold, so it must be a long.
    @Test
    void worldSizedNumberIsAllowed() {
        Population p = new Population("World", 6078749450L);

        assertEquals(6078749450L, p.getTotalPopulation());
    }

    // Bad data

    // A null name is not allowed.
    @Test
    void nullNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Population(null, 100));
    }

    // An empty name is not allowed.
    @Test
    void emptyNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Population("", 100));
    }

    // A name with only spaces is not allowed.
    @Test
    void blankNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Population("   ", 100));
    }

    // A negative population is not allowed.
    @Test
    void negativePopulationIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Population("France", -1));
    }

    // Setters

    // The setters also refuse bad data after the object is made.
    @Test
    void settersRejectBadData() {
        Population p = new Population("France", 100);

        assertThrows(IllegalArgumentException.class, () -> p.setName(" "));
        assertThrows(IllegalArgumentException.class, () -> p.setTotalPopulation(-5));
    }

    // A failed setter must not change the value that was already there.
    @Test
    void failedSetterKeepsOldValue() {
        Population p = new Population("France", 100);

        assertThrows(IllegalArgumentException.class, () -> p.setTotalPopulation(-5));

        assertEquals(100, p.getTotalPopulation());
    }

    // Text

    // The text has the name and the population (with separators for big numbers).
    @Test
    void toStringShowsNameAndPopulation() {
        Population p = new Population("France", 59225700);

        String text = p.toString();

        assertTrue(text.startsWith("France has a population of"));
        assertTrue(text.contains(String.format("%,d", 59225700L)));
    }
}
