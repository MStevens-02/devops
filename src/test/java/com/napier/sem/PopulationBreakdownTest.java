package com.napier.sem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the PopulationBreakdown class (UC4).
 * <p>
 * These don't need the database. They check the maths (people not in cities and the
 * percentages) and that bad data gets turned away.
 */
public class PopulationBreakdownTest {

    /** Percentages are decimals, so we let them be off by a tiny tiny bit. */
    private static final double CLOSE_ENOUGH = 0.001;

    /** A made up place where exactly a quarter of the people live in cities. */
    private PopulationBreakdown testland = new PopulationBreakdown("Testland", 1000, 250);

    /** The real Asia numbers from the world database. */
    private PopulationBreakdown asia = new PopulationBreakdown("Asia", 3705025700L, 697604103L);

    /** Singapore has more city people than total people in the database, which is a data mistake. */
    private PopulationBreakdown singapore = new PopulationBreakdown("Singapore", 3567000, 4017733);

    // ===== holding the data =====

    /**
     * Whatever we put in, we should get back out.
     */
    @Test
    void keepsAllTheValues() {
        assertEquals("Testland", testland.getName());
        assertEquals(1000, testland.getTotalPopulation());
        assertEquals(250, testland.getCityPopulation());
    }

    /**
     * Spaces around the name get chopped off.
     */
    @Test
    void chopsOffSpacesAroundTheName() {
        PopulationBreakdown row = new PopulationBreakdown("  Asia ", 1000, 250);
        assertEquals("Asia", row.getName());
    }

    // ===== the maths =====

    /**
     * 1000 people with 250 in cities means 750 not in cities, which is 25% and 75%.
     */
    @Test
    void quarterInCitiesMaths() {
        assertEquals(750, testland.getNonCityPopulation());
        assertEquals(25.0, testland.getCityPercentage(), CLOSE_ENOUGH);
        assertEquals(75.0, testland.getNonCityPercentage(), CLOSE_ENOUGH);
    }

    /**
     * For Asia, the two people numbers should add back up to the total,
     * and the two percentages should add up to 100.
     */
    @Test
    void asiaAddsUp() {
        assertEquals(asia.getTotalPopulation(), asia.getCityPopulation() + asia.getNonCityPopulation());
        assertEquals(100.0, asia.getCityPercentage() + asia.getNonCityPercentage(), CLOSE_ENOUGH);
    }

    /**
     * About 18.83% of people in Asia live in cities.
     */
    @Test
    void asiaCityPercentIsRight() {
        assertEquals(18.83, asia.getCityPercentage(), 0.01);
    }

    /**
     * If everyone lives in cities it should be 100% and 0%.
     */
    @Test
    void everyoneInCities() {
        PopulationBreakdown row = new PopulationBreakdown("Citystate", 500, 500);

        assertEquals(0, row.getNonCityPopulation());
        assertEquals(100.0, row.getCityPercentage(), CLOSE_ENOUGH);
        assertEquals(0.0, row.getNonCityPercentage(), CLOSE_ENOUGH);
    }

    /**
     * If nobody lives in cities it should be 0% and 100%.
     */
    @Test
    void nobodyInCities() {
        PopulationBreakdown row = new PopulationBreakdown("Farmland", 500, 0);

        assertEquals(0.0, row.getCityPercentage(), CLOSE_ENOUGH);
        assertEquals(100.0, row.getNonCityPercentage(), CLOSE_ENOUGH);
    }

    /**
     * If nobody lives there at all we'd be dividing by 0, so it should just give 0% and not crash.
     */
    @Test
    void nobodyAtAllDoesNotCrash() {
        PopulationBreakdown antarctica = new PopulationBreakdown("Antarctica", 0, 0);

        assertEquals(0.0, antarctica.getCityPercentage(), CLOSE_ENOUGH);
        assertEquals(0.0, antarctica.getNonCityPercentage(), CLOSE_ENOUGH);
    }

    // ===== dodgy data =====

    /**
     * Normal numbers, and numbers where city equals total, are both fine.
     */
    @Test
    void normalNumbersAreNotDodgy() {
        assertFalse(testland.isDataMismatch());
        assertFalse(new PopulationBreakdown("Citystate", 500, 500).isDataMismatch());
    }

    /**
     * More city people than total people is dodgy, and the non-city number goes below 0.
     */
    @Test
    void singaporeIsDodgy() {
        assertTrue(singapore.isDataMismatch());
        assertTrue(singapore.getNonCityPopulation() < 0);
    }

    // ===== bad data =====

    /**
     * Null, empty and just-spaces names should all throw an error.
     */
    @Test
    void saysNoToEmptyNames() {
        assertThrows(IllegalArgumentException.class, () -> new PopulationBreakdown(null, 100, 10));
        assertThrows(IllegalArgumentException.class, () -> new PopulationBreakdown("", 100, 10));
        assertThrows(IllegalArgumentException.class, () -> new PopulationBreakdown("   ", 100, 10));
    }

    /**
     * Minus numbers for either the total or the city people should throw an error.
     */
    @Test
    void saysNoToMinusNumbers() {
        assertThrows(IllegalArgumentException.class, () -> new PopulationBreakdown("Asia", -1, 0));
        assertThrows(IllegalArgumentException.class, () -> new PopulationBreakdown("Asia", 100, -1));
    }

    /**
     * If a setter says no, the old values should still be there afterwards.
     */
    @Test
    void badSetKeepsTheOldValues() {
        assertThrows(IllegalArgumentException.class, () -> testland.setTotalPopulation(-5));
        assertThrows(IllegalArgumentException.class, () -> testland.setCityPopulation(-5));

        assertEquals(1000, testland.getTotalPopulation());
        assertEquals(250, testland.getCityPopulation());
    }

    /**
     * A bad name in the setter should keep the old name.
     */
    @Test
    void badNameKeepsTheOldName() {
        assertThrows(IllegalArgumentException.class, () -> testland.setName("  "));
        assertEquals("Testland", testland.getName());
    }

    /**
     * The setters should actually change things when the data is fine,
     * and the maths should use the new numbers.
     */
    @Test
    void goodSetChangesTheValues() {
        testland.setName("Newland");
        testland.setTotalPopulation(200);
        testland.setCityPopulation(50);

        assertEquals("Newland", testland.getName());
        assertEquals(200, testland.getTotalPopulation());
        assertEquals(50, testland.getCityPopulation());
        assertEquals(150, testland.getNonCityPopulation());
        assertEquals(25.0, testland.getCityPercentage(), CLOSE_ENOUGH);
    }

    /**
     * Percentages that don't come out even (1 out of 3) should still work.
     */
    @Test
    void oneThirdWorks() {
        PopulationBreakdown row = new PopulationBreakdown("Thirdland", 3, 1);

        assertEquals(33.333, row.getCityPercentage(), CLOSE_ENOUGH);
        assertEquals(66.667, row.getNonCityPercentage(), CLOSE_ENOUGH);
    }

    // ===== printing =====

    /**
     * A normal line has the name and % signs, and no star because nothing's dodgy.
     */
    @Test
    void normalLineLooksRight() {
        String line = testland.toString();

        assertTrue(line.contains("Testland"));
        assertTrue(line.contains("%"));
        assertFalse(line.contains("*"));

        // the numbers and percentages should be in there too
        assertTrue(line.contains(String.format("%,d", 1000)));
        assertTrue(line.contains(String.format("%.2f", 25.0)));
        assertTrue(line.contains(String.format("%.2f", 75.0)));
    }

    /**
     * A dodgy line gets a star note stuck on the end.
     */
    @Test
    void dodgyLineGetsAStar() {
        assertTrue(singapore.toString().contains("*"));
    }

    /**
     * Big numbers should get commas in them so they're easy to read.
     */
    @Test
    void bigNumbersGetCommas() {
        String line = asia.toString();

        assertTrue(line.contains(String.format("%,d", 3705025700L)));
        assertTrue(line.contains(String.format("%,d", 697604103L)));
    }
}
