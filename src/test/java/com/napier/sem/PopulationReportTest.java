package com.napier.sem;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PopulationReport and PopulationBreakdownReport.
 * <p>
 * These tests do not need the database. We give the reports a null connection
 * (no database) and check that they check the input and do not crash.
 * The tests that use the real database are in the two integration test classes.
 */
public class PopulationReportTest {

    // A report with no database connection.
    private PopulationReport lookup = new PopulationReport(null);
    private PopulationBreakdownReport breakdown = new PopulationBreakdownReport(null);

    // Population lookup (UC5) - bad input

    // A null type is not allowed.
    @Test
    void nullTypeIsRejected() {
        // step 1: ask for a population with a null type
        // step 2: check it gives an error
        assertThrows(IllegalArgumentException.class, () -> lookup.getPopulation(null, "France"));
    }

    // An empty type is not allowed.
    @Test
    void emptyTypeIsRejected() {
        // step 1: ask for a population with an empty type
        // step 2: check it gives an error
        assertThrows(IllegalArgumentException.class, () -> lookup.getPopulation("", "France"));
    }

    // A type with only spaces is not allowed.
    @Test
    void blankTypeIsRejected() {
        // step 1: ask for a population with a type of only spaces
        // step 2: check it gives an error
        assertThrows(IllegalArgumentException.class, () -> lookup.getPopulation("   ", "France"));
    }

    // Every type except the world needs a name: null name.
    @Test
    void nullNameIsRejectedForCountry() {
        // step 1: ask for a country population with a null name
        // step 2: check it gives an error
        assertThrows(IllegalArgumentException.class, () -> lookup.getPopulation("country", null));
    }

    // Every type except the world needs a name: blank name.
    @Test
    void blankNameIsRejectedForEveryTypeExceptWorld() {
        // step 1: make a list of the types that need a name
        // step 2: loop through them and ask for each one with a blank name
        // step 3: check every single one gives an error
        String[] types = {"continent", "region", "country", "district", "city"};

        for (String type : types) {
            assertThrows(IllegalArgumentException.class,
                    () -> lookup.getPopulation(type, "   "),
                    "type " + type + " should need a name");
        }
    }

    // Population lookup (UC5) - good input but no database

    // The world does not need a name, so a null name must not cause an error.
    @Test
    void worldDoesNotNeedAName() {
        // step 1: ask for the world population with a null name
        // step 2: check there is no error (we only get an empty list because there is no database)
        List<Population> answers = lookup.getPopulation("world", null);

        assertTrue(answers.isEmpty());
    }

    // The type is cleaned (spaces removed, any capitals) so this must not throw.
    @Test
    void typeIsCleanedBeforeUse() {
        // step 1: ask using "  WORLD " (spaces and capitals)
        // step 2: ask using " Country " with a name
        // step 3: check neither one throws an error
        assertDoesNotThrow(() -> lookup.getPopulation("  WORLD ", ""));
        assertDoesNotThrow(() -> lookup.getPopulation(" Country ", "France"));
    }

    // With no database the lookup gives back an empty list, not null and not a crash.
    @Test
    void noConnectionGivesEmptyList() {
        // step 1: ask for France with no database connection
        // step 2: check we get an empty list, not null
        List<Population> answers = lookup.getPopulation("country", "France");

        assertNotNull(answers);
        assertTrue(answers.isEmpty());
    }

    // Population breakdown (UC4) - no database

    // With no database every breakdown gives back an empty list, not null and not a crash.
    @Test
    void noConnectionGivesEmptyBreakdowns() {
        // step 1: ask for the continent, region and country breakdowns with no database
        // step 2: check all three give an empty list
        assertTrue(breakdown.getContinentBreakdown().isEmpty());
        assertTrue(breakdown.getRegionBreakdown().isEmpty());
        assertTrue(breakdown.getCountryBreakdown().isEmpty());
    }
}
