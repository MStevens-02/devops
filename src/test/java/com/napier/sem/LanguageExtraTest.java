package com.napier.sem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Extra unit tests for the Language class (UC6).
// These do not need the database.
// They check the edges (0, 100, too big, too small) and that bad data is refused.
public class LanguageExtraTest {

    // ===== good data on the edges =====

    // 0 speakers and 0% are allowed.
    @Test
    void zeroSpeakersAndZeroPercentIsAllowed() {
        // step 1: make a language with 0 speakers and 0%
        Language lang = new Language("Latin", 0, 0.0);
        // step 2: check both numbers were kept
        assertEquals(0, lang.getTotalSpeakers());
        assertEquals(0.0, lang.getWorldPercentage(), 0.0001);
    }

    // 100% is the biggest allowed percentage.
    @Test
    void hundredPercentIsAllowed() {
        // step 1: make a language with exactly 100%
        Language lang = new Language("Everyone", 6078749450L, 100.0);
        // step 2: check it was kept
        assertEquals(100.0, lang.getWorldPercentage(), 0.0001);
    }

    // A number bigger than an int can hold must still work (it is a long).
    @Test
    void speakersBiggerThanAnIntAreKept() {
        // step 1: make a language with 6078749450 speakers (too big for an int)
        Language lang = new Language("Chinese", 6078749450L, 50.0);
        // step 2: check the number was not cut short
        assertEquals(6078749450L, lang.getTotalSpeakers());
    }

    // ===== bad data =====

    // A null name is not allowed.
    @Test
    void nullNameIsRejected() {
        // step 1: try to make a language with a null name
        // step 2: check it gives an error
        assertThrows(IllegalArgumentException.class, () -> new Language(null, 10, 1.0));
    }

    // An empty name is not allowed.
    @Test
    void emptyNameIsRejected() {
        // step 1: try to make a language with an empty name
        // step 2: check it gives an error
        assertThrows(IllegalArgumentException.class, () -> new Language("", 10, 1.0));
    }

    // A name that is only spaces is not allowed.
    @Test
    void blankNameIsRejected() {
        // step 1: try to make a language with a name that is only spaces
        // step 2: check it gives an error
        assertThrows(IllegalArgumentException.class, () -> new Language("   ", 10, 1.0));
    }

    // A negative number of speakers is not allowed.
    @Test
    void negativeSpeakersAreRejected() {
        // step 1: try to make a language with -1 speakers
        // step 2: check it gives an error
        assertThrows(IllegalArgumentException.class, () -> new Language("English", -1, 1.0));
    }

    // A percentage under 0 is not allowed.
    @Test
    void percentageBelowZeroIsRejected() {
        // step 1: try to make a language with -0.1%
        // step 2: check it gives an error
        assertThrows(IllegalArgumentException.class, () -> new Language("English", 10, -0.1));
    }

    // A percentage over 100 is not allowed.
    @Test
    void percentageAbove100IsRejected() {
        // step 1: try to make a language with 100.1%
        // step 2: check it gives an error
        assertThrows(IllegalArgumentException.class, () -> new Language("English", 10, 100.1));
    }

    // The error message says what was wrong.
    @Test
    void errorMessagesExplainTheProblem() {
        // step 1: catch the error for each kind of bad data
        Exception nameError = assertThrows(IllegalArgumentException.class,
                () -> new Language("", 10, 1.0));
        Exception speakersError = assertThrows(IllegalArgumentException.class,
                () -> new Language("English", -1, 1.0));
        Exception percentError = assertThrows(IllegalArgumentException.class,
                () -> new Language("English", 10, 101.0));
        // step 2: check each message talks about the right thing
        assertTrue(nameError.getMessage().toLowerCase().contains("name"));
        assertTrue(speakersError.getMessage().toLowerCase().contains("speakers"));
        assertTrue(percentError.getMessage().toLowerCase().contains("percentage"));
    }

    // A failed setter must not change the value that was already there.
    @Test
    void failedSettersKeepTheOldValues() {
        // step 1: make a good language
        Language lang = new Language("English", 100, 5.0);
        // step 2: try three bad setters and check each gives an error
        assertThrows(IllegalArgumentException.class, () -> lang.setLanguageName(" "));
        assertThrows(IllegalArgumentException.class, () -> lang.setTotalSpeakers(-5));
        assertThrows(IllegalArgumentException.class, () -> lang.setWorldPercentage(200));
        // step 3: check nothing changed
        assertEquals("English", lang.getLanguageName());
        assertEquals(100, lang.getTotalSpeakers());
        assertEquals(5.0, lang.getWorldPercentage(), 0.0001);
    }

    // The setters work when the data is good.
    @Test
    void settersChangeTheValues() {
        // step 1: make a language, then change every value
        Language lang = new Language("English", 100, 5.0);
        lang.setLanguageName("Spanish");
        lang.setTotalSpeakers(200);
        lang.setWorldPercentage(10.5);
        // step 2: check the new values are there
        assertEquals("Spanish", lang.getLanguageName());
        assertEquals(200, lang.getTotalSpeakers());
        assertEquals(10.5, lang.getWorldPercentage(), 0.0001);
    }

    // ===== text =====

    // The text shows the speakers with commas and the percentage with 2 decimals and a % sign.
    @Test
    void toStringShowsCommasAndTwoDecimals() {
        // step 1: make a language and turn it into text
        String text = new Language("Hindi", 600000000L, 7.5).toString();
        // step 2: check the number has commas
        assertTrue(text.contains("600,000,000"));
        // step 3: check the percentage has 2 decimals and a % sign
        assertTrue(text.contains("7.50%"));
    }

    // The percentage is rounded to 2 decimals in the text.
    @Test
    void toStringRoundsThePercentage() {
        // step 1: make a language with a long percentage and turn it into text
        String text = new Language("Hindi", 1000, 7.4567).toString();
        // step 2: check it was rounded to 7.46
        assertTrue(text.contains("7.46%"));
        assertFalse(text.contains("7.4567"));
    }

    // A language with nothing still prints without a crash.
    @Test
    void toStringWorksForZeros() {
        // step 1: turn a language with all zeros into text
        String text = new Language("Latin", 0, 0.0).toString();
        // step 2: check the name and 0.00% are in the text
        assertTrue(text.contains("Latin"));
        assertTrue(text.contains("0.00%"));
    }
}
