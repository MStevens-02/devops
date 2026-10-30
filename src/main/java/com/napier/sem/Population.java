package com.napier.sem;

/**
 * This is basically just a little box that holds one answer from the population lookup (UC5).
 * <p>
 * It holds two things: the name of the place (like "France") and how many people live there.
 * That's it, nothing fancy.
 */
public class Population {

    /** The name of the place, like "France" or "Asia" or "World". */
    private String name;

    /** How many people live there. It's a long because the world number is too big for an int. */
    private long totalPopulation;

    /**
     * Makes a new Population box.
     *
     * @param name            the name of the place (can't be empty)
     * @param totalPopulation how many people live there (can't be less than 0)
     */
    public Population(String name, long totalPopulation) {
        setName(name);
        setTotalPopulation(totalPopulation);
    }

    /**
     * Gets the name of the place.
     *
     * @return the name, like "France"
     */
    public String getName() {
        return name;
    }

    /**
     * Gets how many people live there.
     *
     * @return the number of people
     */
    public long getTotalPopulation() {
        return totalPopulation;
    }

    /**
     * Changes the name. If the name is rubbish (null or just spaces) it throws an error
     * and the old name stays the same.
     *
     * @param name the new name
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        // get rid of any spaces at the start and end
        this.name = name.trim();
    }

    /**
     * Changes the number of people. You can't have less than 0 people,
     * so a minus number throws an error and the old number stays the same.
     *
     * @param totalPopulation the new number of people
     */
    public void setTotalPopulation(long totalPopulation) {
        if (totalPopulation < 0) {
            throw new IllegalArgumentException("Total population must not be negative");
        }
        this.totalPopulation = totalPopulation;
    }

    /**
     * Turns this into a sentence we can print out.
     * The %,d bit puts commas in the big number so it's easier to read.
     *
     * @return something like "France has a population of 59,225,700"
     */
    @Override
    public String toString() {
        return String.format("%s has a population of %,d", name, totalPopulation);
    }
}
