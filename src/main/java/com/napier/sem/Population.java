package com.napier.sem;

/**
 * Holds one population answer for a lookup (UC5).
 * For example: name = "France", totalPopulation = 67,000,000.
 */
public class Population {

    // the name of the area, for example "France" or "Asia"
    private String name;

    // how many people live there (long because the world number is very big)
    private long totalPopulation;

    // The constructor runs when we write "new Population(...)".
    // It uses the setters so the checks below always happen.
    public Population(String name, long totalPopulation) {
        setName(name);
        setTotalPopulation(totalPopulation);
    }

    // ----- getters: let other classes read the values -----

    public String getName() {
        return name;
    }

    public long getTotalPopulation() {
        return totalPopulation;
    }

    // ----- setters: change a value, but only if it is okay -----

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        this.name = name.trim();
    }

    public void setTotalPopulation(long totalPopulation) {
        // a population can not be less than 0
        if (totalPopulation < 0) {
            throw new IllegalArgumentException("Total population must not be negative");
        }
        this.totalPopulation = totalPopulation;
    }

    // The text that is printed, for example: France has a population of 67,000,000
    // The %,d part puts commas in the number.
    @Override
    public String toString() {
        return String.format("%s has a population of %,d", name, totalPopulation);
    }
}
