package com.napier.sem;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PopulationReport and PopulationBreakdownReport that DON'T need the database.
 * <p>
 * We give both reports a null connection (so no database) and check that they still
 * check what you type in properly, and don't crash. The tests that use the real
 * database are in the two integration test classes.
 */
public class PopulationReportTest {

    /** The lookup with no database. */
    private PopulationReport lookup = new PopulationReport(null);

    /** The breakdown report with no database. */
    private PopulationBreakdownReport breakdown = new PopulationBreakdownReport(null);

    // ===== the lookup (UC5) =====

    /**
     * A null, empty or just-spaces type should throw an error.
     */
    @Test
    void saysNoToEmptyTypes() {
        assertThrows(IllegalArgumentException.class, () -> lookup.getPopulation(null, "France"));
        assertThrows(IllegalArgumentException.class, () -> lookup.getPopulation("", "France"));
        assertThrows(IllegalArgumentException.class, () -> lookup.getPopulation("   ", "France"));
    }

    /**
     * A made up type like "planet" should throw an error, and the message should say what was wrong.
     * This works even without a database because we check the type first.
     */
    @Test
    void saysNoToMadeUpTypes() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> lookup.getPopulation("planet", "Mars"));

        assertTrue(e.getMessage().contains("planet"));
    }

    /**
     * Every type apart from the world needs a name, so a null name should throw an error.
     */
    @Test
    void saysNoToNullNames() {
        assertThrows(IllegalArgumentException.class, () -> lookup.getPopulation("country", null));
    }

    /**
     * Goes through every type that needs a name and checks a blank name throws an error for each one.
     */
    @Test
    void saysNoToBlankNamesForEveryType() {
        String[] types = {"continent", "region", "country", "district", "city"};

        for (int i = 0; i < types.length; i++) {
            String type = types[i];
            assertThrows(IllegalArgumentException.class,
                    () -> lookup.getPopulation(type, "   "),
                    type + " should need a name");
        }
    }

    /**
     * The world doesn't need a name, so null should be fine (we just get nothing back
     * because there's no database).
     */
    @Test
    void worldIsFineWithNoName() {
        List<Population> answers = lookup.getPopulation("world", null);
        assertTrue(answers.isEmpty());
    }

    /**
     * Capitals and spaces in the type shouldn't matter.
     */
    @Test
    void capitalsAndSpacesDontMatter() {
        assertDoesNotThrow(() -> lookup.getPopulation("  WORLD ", ""));
        assertDoesNotThrow(() -> lookup.getPopulation(" Country ", "France"));
    }

    /**
     * With no database we should get an empty list back, not null and not a crash.
     */
    @Test
    void noDatabaseGivesEmptyList() {
        List<Population> answers = lookup.getPopulation("country", "France");

        assertNotNull(answers);
        assertTrue(answers.isEmpty());
    }

    // ===== the breakdown (UC4) =====

    /**
     * With no database all three breakdowns should give back an empty list and not crash.
     */
    @Test
    void noDatabaseGivesEmptyBreakdowns() {
        assertTrue(breakdown.getContinentBreakdown().isEmpty());
        assertTrue(breakdown.getRegionBreakdown().isEmpty());
        assertTrue(breakdown.getCountryBreakdown().isEmpty());
    }
}
