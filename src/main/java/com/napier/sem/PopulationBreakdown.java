package com.napier.sem;

/**
 * Holds one row of the population breakdown report (UC4).
 * For example: Asia, total 3,705,025,700, 697,604,103 live in cities (18.83%),
 * and the rest do not live in cities.
 */
public class PopulationBreakdown {

    // the continent, region or country name
    private String name;

    // everyone who lives there
    private long totalPopulation;

    // the people who live in cities
    private long cityPopulation;

    // The constructor runs when we write "new PopulationBreakdown(...)".
    public PopulationBreakdown(String name, long totalPopulation, long cityPopulation) {
        setName(name);
        setTotalPopulation(totalPopulation);
        setCityPopulation(cityPopulation);
    }

    // ----- getters -----

    public String getName() {
        return name;
    }

    public long getTotalPopulation() {
        return totalPopulation;
    }

    public long getCityPopulation() {
        return cityPopulation;
    }

    // ----- setters (they check the value is okay) -----

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        this.name = name.trim();
    }

    public void setTotalPopulation(long totalPopulation) {
        if (totalPopulation < 0) {
            throw new IllegalArgumentException("Total population must not be negative");
        }
        this.totalPopulation = totalPopulation;
    }

    public void setCityPopulation(long cityPopulation) {
        if (cityPopulation < 0) {
            throw new IllegalArgumentException("City population must not be negative");
        }
        this.cityPopulation = cityPopulation;
    }

    // ----- numbers worked out from the ones above -----

    // People not living in cities = everyone minus the city people.
    public long getNonCityPopulation() {
        return totalPopulation - cityPopulation;
    }

    // Percentage of people living in cities.
    public double getCityPercentage() {
        return getPercentage(cityPopulation);
    }

    // Percentage of people not living in cities.
    public double getNonCityPercentage() {
        return getPercentage(getNonCityPopulation());
    }

    // Works out part / total * 100.
    // If the total is 0 we give back 0, because dividing by 0 would cause an error.
    private double getPercentage(long part) {
        if (totalPopulation == 0) {
            return 0.0;
        }
        return part * 100.0 / totalPopulation;
    }

    // A few countries in the world database have more city people than the country total.
    // That makes the non-city number negative, so we flag it in the output.
    public boolean isDataMismatch() {
        return cityPopulation > totalPopulation;
    }

    // The text for one line of the report.
    // %-30s = text, left aligned, 30 characters wide.
    // %,15d = whole number with commas, 15 characters wide.
    // %6.2f = number with 2 decimal places.   %% = a percent sign.
    @Override
    public String toString() {
        String line = String.format("%-30s %,15d %,15d (%6.2f%%) %,15d (%6.2f%%)",
                name, totalPopulation,
                cityPopulation, getCityPercentage(),
                getNonCityPopulation(), getNonCityPercentage());
        if (isDataMismatch()) {
            line = line + "  * city total is bigger than country total";
        }
        return line;
    }
}
