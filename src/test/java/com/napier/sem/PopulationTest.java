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
        // step 1: make a Population for France with 59225700 people
        // step 2: check the name and the number are the same as we put in
        Population p = new Population("France", 59225700);

        assertEquals("France", p.getName());
        assertEquals(59225700, p.getTotalPopulation());
    }

    // Spaces around the name are removed.
    @Test
    void nameIsTrimmed() {
        // step 1: make a Population with spaces around the name
        // step 2: check the spaces were removed
        Population p = new Population("  France  ", 59225700);

        assertEquals("France", p.getName());
    }

    // A population of 0 is allowed (for example, Antarctica).
    @Test
    void zeroPopulationIsAllowed() {
        // step 1: make a Population with 0 people
        // step 2: check it was accepted and still says 0
        Population p = new Population("Antarctica", 0);

        assertEquals(0, p.getTotalPopulation());
    }

    // The world population is bigger than an int can hold, so it must be a long.
    @Test
    void worldSizedNumberIsAllowed() {
        // step 1: make a Population with the whole world number (6078749450)
        // step 2: check the number was kept (it only fits in a long, not an int)
        Population p = new Population("World", 6078749450L);

        assertEquals(6078749450L, p.getTotalPopulation());
    }

    // Bad data

    // A null name is not allowed.
    @Test
    void nullNameIsRejected() {
        // step 1: try to make a Population with a null name
        // step 2: check it gives an error (IllegalArgumentException)
        assertThrows(IllegalArgumentException.class, () -> new Population(null, 100));
    }

    // An empty name is not allowed.
    @Test
    void emptyNameIsRejected() {
        // step 1: try to make a Population with an empty name
        // step 2: check it gives an error
        assertThrows(IllegalArgumentException.class, () -> new Population("", 100));
    }

    // A name with only spaces is not allowed.
    @Test
    void blankNameIsRejected() {
        // step 1: try to make a Population with a name that is only spaces
        // step 2: check it gives an error
        assertThrows(IllegalArgumentException.class, () -> new Population("   ", 100));
    }

    // A negative population is not allowed.
    @Test
    void negativePopulationIsRejected() {
        // step 1: try to make a Population with -1 people
        // step 2: check it gives an error
        assertThrows(IllegalArgumentException.class, () -> new Population("France", -1));
    }

    // Setters

    // The setters also refuse bad data after the object is made.
    @Test
    void settersRejectBadData() {
        // step 1: make a good Population
        // step 2: try to set a blank name and check it gives an error
        // step 3: try to set a negative population and check it gives an error
        Population p = new Population("France", 100);

        assertThrows(IllegalArgumentException.class, () -> p.setName(" "));
        assertThrows(IllegalArgumentException.class, () -> p.setTotalPopulation(-5));
    }

    // A failed setter must not change the value that was already there.
    @Test
    void failedSetterKeepsOldValue() {
        // step 1: make a Population with 100 people
        // step 2: try to set -5 people and check it gives an error
        // step 3: check the population is still 100
        Population p = new Population("France", 100);

        assertThrows(IllegalArgumentException.class, () -> p.setTotalPopulation(-5));

        assertEquals(100, p.getTotalPopulation());
    }

    // Text

    // The text has the name and the population (with separators for big numbers).
    @Test
    void toStringShowsNameAndPopulation() {
        // step 1: make a Population and turn it into text
        // step 2: check the text starts with the name and the words "has a population of"
        // step 3: check the number is in the text with commas
        Population p = new Population("France", 59225700);

        String text = p.toString();

        assertTrue(text.startsWith("France has a population of"));
        assertTrue(text.contains(String.format("%,d", 59225700L)));
    }
}
