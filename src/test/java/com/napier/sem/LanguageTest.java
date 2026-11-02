package com.napier.sem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for the Language domain model.
 * Tests constructor initialization, getter and setter operations,
 * input validation boundary conditions, and string formatting.
 */
public class LanguageTest {

    /**
     * Verifies that the parameterized constructor correctly assigns all fields
     * and that the getter methods return the exact initialized values.
     */
    @Test
    public void testLanguageConstructorAndGetters() {
        // Arrange & Act: Instantiate a new Language object with test data
        Language lang = new Language("Spanish", 500000000L, 6.25);

        // Assert: Ensure getters return the correct field values
        assertEquals("Spanish", lang.getLanguageName(), "Language name should match constructor input.");
        assertEquals(500000000L, lang.getTotalSpeakers(), "Total speakers should match constructor input.");
        assertEquals(6.25, lang.getWorldPercentage(), 0.001, "World percentage should match constructor input.");
    }

    /**
     * Verifies that setter methods update object attributes correctly
     * and that invalid inputs throw an IllegalArgumentException as intended.
     */
    @Test
    public void testLanguageSettersAndValidation() {
        // Arrange: Instantiate with valid initial state
        Language lang = new Language("English", 1000000000L, 12.5);

        // Act: Update properties using setters
        lang.setLanguageName("Mandarin");
        lang.setTotalSpeakers(1100000000L);
        lang.setWorldPercentage(14.2);

        // Assert: Verify that updated values are saved
        assertEquals("Mandarin", lang.getLanguageName(), "Language name should be updated.");
        assertEquals(1100000000L, lang.getTotalSpeakers(), "Total speakers count should be updated.");
        assertEquals(14.2, lang.getWorldPercentage(), 0.001, "World percentage should be updated.");

        // Assert Validation Rules: Verify IllegalArgumentException is thrown for invalid language names
        assertThrows(IllegalArgumentException.class, () -> lang.setLanguageName(null),
                "Setting language name to null should throw an exception.");
        assertThrows(IllegalArgumentException.class, () -> lang.setLanguageName("   "),
                "Setting language name to blank should throw an exception.");

        // Assert Validation Rules: Verify IllegalArgumentException is thrown for negative speaker counts
        assertThrows(IllegalArgumentException.class, () -> lang.setTotalSpeakers(-1L),
                "Setting total speakers to a negative number should throw an exception.");

        // Assert Validation Rules: Verify IllegalArgumentException is thrown for out-of-range percentage values
        assertThrows(IllegalArgumentException.class, () -> lang.setWorldPercentage(-0.1),
                "Setting world percentage below 0 should throw an exception.");
        assertThrows(IllegalArgumentException.class, () -> lang.setWorldPercentage(100.1),
                "Setting world percentage above 100 should throw an exception.");
    }


    /**
     * Verifies that the toString method or formatting produces exact 2-decimal outputs
     * for known test figures.
     */
    @Test
    public void testTwoDecimalRounding() {
        // Arrange with a figure that has many decimal places (e.g., 12.34567)
        Language lang = new Language("Spanish", 1000000L, 12.34567);

        // Act: Get string representation containing the percentage
        String result = lang.toString();

        // Assert: Verify it rounds 12.34567 to "12.35" or matches expected 2-decimal format
        assertTrue(result.contains("12.35"), "Percentage should be rounded and formatted to 2 decimal places.");
    }

    /**
     * Verifies that the custom toString() method returns a non-null,
     * non-empty string that contains the language data formatted correctly.
     */
    @Test
    public void testToStringFormat() {
        // Arrange: Create a Language instance
        Language lang = new Language("Hindi", 600000000L, 7.5);

        // Act: Invoke the formatted string output
        String result = lang.toString();

        // Assert: Verify string representation is valid and contains expected language data
        assertNotNull(result, "Formatted output should not be null.");
        assertTrue(result.contains("Hindi"), "Formatted string output should include the language name.");
    }
}