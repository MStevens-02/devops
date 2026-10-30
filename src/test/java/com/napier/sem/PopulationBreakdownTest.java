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
        // step 1: make a row (1000 people, 250 in cities)
        // step 2: check the name and both numbers are the same as we put in
        PopulationBreakdown row = quarterInCities();

        assertEquals("Testland", row.getName());
        assertEquals(1000, row.getTotalPopulation());
        assertEquals(250, row.getCityPopulation());
    }

    // Spaces around the name are removed.
    @Test
    void nameIsTrimmed() {
        // step 1: make a row with spaces around the name
        // step 2: check the spaces were removed
        PopulationBreakdown row = new PopulationBreakdown("  Asia ", 1000, 250);

        assertEquals("Asia", row.getName());
    }

    // The maths

    // Not in cities = total minus the city people.
    @Test
    void nonCityPopulationIsTotalMinusCity() {
        // step 1: use the row with 1000 total and 250 in cities
        // step 2: check people not in cities is 1000 - 250 = 750
        assertEquals(750, quarterInCities().getNonCityPopulation());
    }

    // 250 out of 1000 is 25%.
    @Test
    void cityPercentageIsWorkedOutCorrectly() {
        // step 1: use the row with 1000 total and 250 in cities
        // step 2: check the city percentage is 25
        assertEquals(25.0, quarterInCities().getCityPercentage(), DELTA);
    }

    // 750 out of 1000 is 75%.
    @Test
    void nonCityPercentageIsWorkedOutCorrectly() {
        // step 1: use the row with 1000 total and 250 in cities
        // step 2: check the non-city percentage is 75
        assertEquals(75.0, quarterInCities().getNonCityPercentage(), DELTA);
    }

    // The two percentages add up to 100.
    @Test
    void percentagesAddUpTo100() {
        // step 1: make a row using the Asia numbers
        // step 2: add the two percentages together
        // step 3: check the answer is 100
        PopulationBreakdown row = new PopulationBreakdown("Asia", 3705025700L, 697604103L);

        assertEquals(100.0, row.getCityPercentage() + row.getNonCityPercentage(), DELTA);
    }

    // City people plus non-city people give back the total.
    @Test
    void cityPlusNonCityEqualsTotal() {
        // step 1: make a row using the Asia numbers
        // step 2: add city people and non-city people together
        // step 3: check it gives back the total
        PopulationBreakdown row = new PopulationBreakdown("Asia", 3705025700L, 697604103L);

        assertEquals(row.getTotalPopulation(), row.getCityPopulation() + row.getNonCityPopulation());
    }

    // The real Asia numbers from the world database give about 18.83%.
    @Test
    void realAsiaNumbersGiveExpectedPercentage() {
        // step 1: make a row with the real Asia numbers from the database
        // step 2: check the city percentage is about 18.83
        PopulationBreakdown asia = new PopulationBreakdown("Asia", 3705025700L, 697604103L);

        assertEquals(18.83, asia.getCityPercentage(), 0.01);
    }

    // Everyone lives in cities: 100% and 0%.
    @Test
    void everyoneInCities() {
        // step 1: make a row where all 500 people live in cities
        // step 2: check city is 100%, non-city is 0% and 0 people are outside cities
        PopulationBreakdown row = new PopulationBreakdown("Citystate", 500, 500);

        assertEquals(100.0, row.getCityPercentage(), DELTA);
        assertEquals(0.0, row.getNonCityPercentage(), DELTA);
        assertEquals(0, row.getNonCityPopulation());
    }

    // Nobody lives in cities: 0% and 100%.
    @Test
    void nobodyInCities() {
        // step 1: make a row where 0 of the 500 people live in cities
        // step 2: check city is 0% and non-city is 100%
        PopulationBreakdown row = new PopulationBreakdown("Farmland", 500, 0);

        assertEquals(0.0, row.getCityPercentage(), DELTA);
        assertEquals(100.0, row.getNonCityPercentage(), DELTA);
    }

    // A total of 0 (for example, Antarctica) must give 0% and must not divide by zero.
    @Test
    void zeroTotalGivesZeroPercentWithoutError() {
        // step 1: make a row with 0 total people (like Antarctica)
        // step 2: check both percentages are 0 and nothing crashed (no dividing by zero)
        PopulationBreakdown row = new PopulationBreakdown("Antarctica", 0, 0);

        assertEquals(0.0, row.getCityPercentage(), DELTA);
        assertEquals(0.0, row.getNonCityPercentage(), DELTA);
    }

    // The data mismatch flag

    // Normal data is not flagged.
    @Test
    void normalRowIsNotAMismatch() {
        // step 1: use the normal row (250 of 1000)
        // step 2: check it is not flagged as a mismatch
        assertFalse(quarterInCities().isDataMismatch());
    }

    // Exactly equal numbers are not a mismatch.
    @Test
    void equalCityAndTotalIsNotAMismatch() {
        // step 1: make a row where city people equal the total (500 and 500)
        // step 2: check it is not flagged, because equal is fine
        assertFalse(new PopulationBreakdown("Citystate", 500, 500).isDataMismatch());
    }

    // More city people than total people is flagged (this happens for Singapore in the database).
    @Test
    void moreCityPeopleThanTotalIsAMismatch() {
        // step 1: make a row like Singapore where cities have MORE people than the country total
        // step 2: check it is flagged as a mismatch
        // step 3: check the non-city number is below 0
        PopulationBreakdown singapore = new PopulationBreakdown("Singapore", 3567000, 4017733);

        assertTrue(singapore.isDataMismatch());
        assertTrue(singapore.getNonCityPopulation() < 0);
    }

    // Bad data

    // A null, empty or blank name is not allowed.
    @Test
    void badNamesAreRejected() {
        // step 1: try to make a row with a null name, an empty name and a blank name
        // step 2: check each one gives an error
        assertThrows(IllegalArgumentException.class, () -> new PopulationBreakdown(null, 100, 10));
        assertThrows(IllegalArgumentException.class, () -> new PopulationBreakdown("", 100, 10));
        assertThrows(IllegalArgumentException.class, () -> new PopulationBreakdown("   ", 100, 10));
    }

    // A negative total is not allowed.
    @Test
    void negativeTotalIsRejected() {
        // step 1: try to make a row with a total of -1
        // step 2: check it gives an error
        assertThrows(IllegalArgumentException.class, () -> new PopulationBreakdown("Asia", -1, 0));
    }

    // A negative city population is not allowed.
    @Test
    void negativeCityPopulationIsRejected() {
        // step 1: try to make a row with -1 city people
        // step 2: check it gives an error
        assertThrows(IllegalArgumentException.class, () -> new PopulationBreakdown("Asia", 100, -1));
    }

    // A failed setter must not change the value that was already there.
    @Test
    void failedSetterKeepsOldValue() {
        // step 1: make the normal row (total 1000)
        // step 2: try to set the total to -5 and check it gives an error
        // step 3: check the total is still 1000
        PopulationBreakdown row = quarterInCities();

        assertThrows(IllegalArgumentException.class, () -> row.setTotalPopulation(-5));

        assertEquals(1000, row.getTotalPopulation());
    }

    // Text

    // The report line has the name, and percent signs for both percentages.
    @Test
    void toStringShowsNameAndPercentages() {
        // step 1: turn the normal row into text
        // step 2: check the text has the name and a % sign
        // step 3: check it has no * because it is not a mismatch
        String line = quarterInCities().toString();

        assertTrue(line.contains("Testland"));
        assertTrue(line.contains("%"));
        assertFalse(line.contains("*"));
    }

    // A mismatch row gets a star note at the end of the line.
    @Test
    void toStringFlagsAMismatch() {
        // step 1: turn the Singapore row into text
        // step 2: check the text has a * in it
        String line = new PopulationBreakdown("Singapore", 3567000, 4017733).toString();

        assertTrue(line.contains("*"));
    }
}
