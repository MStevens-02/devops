package com.napier.sem;

/**
 * One line of the population breakdown report (UC4).
 * <p>
 * For a place (a continent, a region or a country) it holds:
 * how many people live there in total, and how many of them live in cities.
 * From those two numbers we can work out everything else, like how many people
 * DON'T live in cities and what the percentages are.
 * <p>
 * Example: Asia has 3,705,025,700 people and 697,604,103 of them live in cities,
 * so that's about 18.83% in cities and 81.17% not in cities.
 */
public class PopulationBreakdown {

    /** The name of the place, like "Asia". */
    private String name;

    /** Everyone who lives there. */
    private long totalPopulation;

    /** Just the people who live in cities. */
    private long cityPopulation;

    /**
     * Makes a new line for the report.
     *
     * @param name            the name of the place (can't be empty)
     * @param totalPopulation everyone who lives there (can't be below 0)
     * @param cityPopulation  the people living in cities (can't be below 0)
     */
    public PopulationBreakdown(String name, long totalPopulation, long cityPopulation) {
        setName(name);
        setTotalPopulation(totalPopulation);
        setCityPopulation(cityPopulation);
    }

    /**
     * Gets the name of the place.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the total number of people.
     *
     * @return everyone who lives there
     */
    public long getTotalPopulation() {
        return totalPopulation;
    }

    /**
     * Gets the number of people living in cities.
     *
     * @return the city people
     */
    public long getCityPopulation() {
        return cityPopulation;
    }

    /**
     * Changes the name. Throws an error if it's null or just spaces.
     *
     * @param name the new name
     */
    public void setName(String name) {
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        this.name = name.trim();
    }

    /**
     * Changes the total number of people. Throws an error if it's a minus number.
     *
     * @param totalPopulation the new total
     */
    public void setTotalPopulation(long totalPopulation) {
        if (totalPopulation < 0) {
            throw new IllegalArgumentException("Total population must not be negative");
        }
        this.totalPopulation = totalPopulation;
    }

    /**
     * Changes the number of city people. Throws an error if it's a minus number.
     *
     * @param cityPopulation the new number of city people
     */
    public void setCityPopulation(long cityPopulation) {
        if (cityPopulation < 0) {
            throw new IllegalArgumentException("City population must not be negative");
        }
        this.cityPopulation = cityPopulation;
    }

    /**
     * Works out how many people DON'T live in cities. It's just everyone minus the city people.
     *
     * @return the people not living in cities
     */
    public long getNonCityPopulation() {
        return totalPopulation - cityPopulation;
    }

    /**
     * Works out what percent of people live in cities.
     *
     * @return the city percentage (0 to 100)
     */
    public double getCityPercentage() {
        // if nobody lives there we just say 0%, because dividing by 0 breaks things
        if (totalPopulation == 0) {
            return 0.0;
        }
        return (double) cityPopulation / totalPopulation * 100;
    }

    /**
     * Works out what percent of people DON'T live in cities.
     *
     * @return the non-city percentage (0 to 100)
     */
    public double getNonCityPercentage() {
        if (totalPopulation == 0) {
            return 0.0;
        }
        return (double) getNonCityPopulation() / totalPopulation * 100;
    }

    /**
     * Checks if the numbers don't make sense. A few countries in the database
     * (like Singapore) somehow have more city people than people in the whole country.
     * That's a mistake in the data, not in our code, so we just point it out.
     *
     * @return true if there are more city people than total people
     */
    public boolean isDataMismatch() {
        if (cityPopulation > totalPopulation) {
            return true;
        }
        return false;
    }

    /**
     * Makes the line of text that gets printed in the report.
     * The numbers in the format (like 30 and 15) are how wide each column is,
     * so everything lines up nicely. %% just prints a % sign.
     *
     * @return one line of the report
     */
    @Override
    public String toString() {
        String line = String.format("%-30s %,15d %,15d (%6.2f%%) %,15d (%6.2f%%)",
                name,
                totalPopulation,
                cityPopulation, getCityPercentage(),
                getNonCityPopulation(), getNonCityPercentage());

        // stick a note on the end if the numbers are dodgy
        if (isDataMismatch()) {
            line += "  * city total is bigger than country total";
        }
        return line;
    }
}
