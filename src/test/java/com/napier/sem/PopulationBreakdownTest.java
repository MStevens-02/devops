package com.napier.sem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PopulationBreakdown class (UC4).
 * <p>
 * These tests do not need the database.
 * They check the maths (non-city people and the percentages)
 * and that bad data is refused.
 */
public class PopulationBreakdownTest {

    // Percentages are doubles, so we allow a tiny difference when comparing them.
    private static final double DELTA = 0.001;

    // Makes a simple row where exactly 25% live in cities.
    private PopulationBreakdown quarterInCities() {
        return new PopulationBreakdown("Testland", 1000, 250);
    }

    // Good data

    // A row made with good data keeps every value.
    @Test
    void validRowKeepsAllValues() {
        PopulationBreakdown row = quarterInCities();

        assertEquals("Testland", row.getName());
        assertEquals(1000, row.getTotalPopulation());
        assertEquals(250, row.getCityPopulation());
    }

    // Spaces around the name are removed.
    @Test
    void nameIsTrimmed() {
        PopulationBreakdown row = new PopulationBreakdown("  Asia ", 1000, 250);

        assertEquals("Asia", row.getName());
    }

    // The maths

    // Not in cities = total minus the city people.
    @Test
    void nonCityPopulationIsTotalMinusCity() {
        assertEquals(750, quarterInCities().getNonCityPopulation());
    }

    // 250 out of 1000 is 25%.
    @Test
    void cityPercentageIsWorkedOutCorrectly() {
        assertEquals(25.0, quarterInCities().getCityPercentage(), DELTA);
    }

    // 750 out of 1000 is 75%.
    @Test
    void nonCityPercentageIsWorkedOutCorrectly() {
        assertEquals(75.0, quarterInCities().getNonCityPercentage(), DELTA);
    }

    // The two percentages add up to 100.
    @Test
    void percentagesAddUpTo100() {
        PopulationBreakdown row = new PopulationBreakdown("Asia", 3705025700L, 697604103L);

        assertEquals(100.0, row.getCityPercentage() + row.getNonCityPercentage(), DELTA);
    }

    // City people plus non-city people give back the total.
    @Test
    void cityPlusNonCityEqualsTotal() {
        PopulationBreakdown row = new PopulationBreakdown("Asia", 3705025700L, 697604103L);

        assertEquals(row.getTotalPopulation(), row.getCityPopulation() + row.getNonCityPopulation());
    }

    // The real Asia numbers from the world database give about 18.83%.
    @Test
    void realAsiaNumbersGiveExpectedPercentage() {
        PopulationBreakdown asia = new PopulationBreakdown("Asia", 3705025700L, 697604103L);

        assertEquals(18.83, asia.getCityPercentage(), 0.01);
    }

    // Everyone lives in cities: 100% and 0%.
    @Test
    void everyoneInCities() {
        PopulationBreakdown row = new PopulationBreakdown("Citystate", 500, 500);

        assertEquals(100.0, row.getCityPercentage(), DELTA);
        assertEquals(0.0, row.getNonCityPercentage(), DELTA);
        assertEquals(0, row.getNonCityPopulation());
    }

    // Nobody lives in cities: 0% and 100%.
    @Test
    void nobodyInCities() {
        PopulationBreakdown row = new PopulationBreakdown("Farmland", 500, 0);

        assertEquals(0.0, row.getCityPercentage(), DELTA);
        assertEquals(100.0, row.getNonCityPercentage(), DELTA);
    }

    // A total of 0 (for example, Antarctica) must give 0% and must not divide by zero.
    @Test
    void zeroTotalGivesZeroPercentWithoutError() {
        PopulationBreakdown row = new PopulationBreakdown("Antarctica", 0, 0);

        assertEquals(0.0, row.getCityPercentage(), DELTA);
        assertEquals(0.0, row.getNonCityPercentage(), DELTA);
    }

    // The data mismatch flag

    // Normal data is not flagged.
    @Test
    void normalRowIsNotAMismatch() {
        assertFalse(quarterInCities().isDataMismatch());
    }

    // Exactly equal numbers are not a mismatch.
    @Test
    void equalCityAndTotalIsNotAMismatch() {
        assertFalse(new PopulationBreakdown("Citystate", 500, 500).isDataMismatch());
    }

    // More city people than total people is flagged (this happens for Singapore in the database).
    @Test
    void moreCityPeopleThanTotalIsAMismatch() {
        PopulationBreakdown singapore = new PopulationBreakdown("Singapore", 3567000, 4017733);

        assertTrue(singapore.isDataMismatch());
        assertTrue(singapore.getNonCityPopulation() < 0);
    }

    // Bad data

    // A null, empty or blank name is not allowed.
    @Test
    void badNamesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new PopulationBreakdown(null, 100, 10));
        assertThrows(IllegalArgumentException.class, () -> new PopulationBreakdown("", 100, 10));
        assertThrows(IllegalArgumentException.class, () -> new PopulationBreakdown("   ", 100, 10));
    }

    // A negative total is not allowed.
    @Test
    void negativeTotalIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new PopulationBreakdown("Asia", -1, 0));
    }

    // A negative city population is not allowed.
    @Test
    void negativeCityPopulationIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new PopulationBreakdown("Asia", 100, -1));
    }

    // A failed setter must not change the value that was already there.
    @Test
    void failedSetterKeepsOldValue() {
        PopulationBreakdown row = quarterInCities();

        assertThrows(IllegalArgumentException.class, () -> row.setTotalPopulation(-5));

        assertEquals(1000, row.getTotalPopulation());
    }

    // Text

    // The report line has the name, and percent signs for both percentages.
    @Test
    void toStringShowsNameAndPercentages() {
        String line = quarterInCities().toString();

        assertTrue(line.contains("Testland"));
        assertTrue(line.contains("%"));
        assertFalse(line.contains("*"));
    }

    // A mismatch row gets a star note at the end of the line.
    @Test
    void toStringFlagsAMismatch() {
        String line = new PopulationBreakdown("Singapore", 3567000, 4017733).toString();

        assertTrue(line.contains("*"));
    }
}
