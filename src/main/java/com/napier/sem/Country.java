package com.napier.sem;

/**
 * Holds one country for the country reports (UC1).
 * <p>
 * The report shows the code, name, continent, region, population and capital.
 * The country table only stores the capital as a city ID (just a number), so the SQL
 * in CountryReport joins the city table to get the capital's actual name.
 * <p>
 * Every setter checks the value first, and the constructor uses the setters,
 * so you can never make a Country with rubbish data in it.
 */
public class Country {

    /** The 3 letter code, like "GBR". */
    private String code;

    /** The name, like "United Kingdom". */
    private String name;

    /** The continent, like "Europe". */
    private String continent;

    /** The region, like "British Islands". */
    private String region;

    /** How many people live there. */
    private Integer population;

    /** The ID of the capital city, or null if there isn't one. */
    private Integer capitalId;

    /** The name of the capital city, or null if there isn't one. */
    private String capitalName;

    /**
     * Makes a new Country. Every value gets checked before it's saved.
     *
     * @param code        the 3 letter code
     * @param name        the country name
     * @param continent   the continent
     * @param region      the region
     * @param population  how many people live there
     * @param capitalId   the ID of the capital city, or null
     * @param capitalName the name of the capital city, or null
     * @throws IllegalArgumentException if any of the values are bad
     */
    public Country(String code, String name, String continent, String region,
                   Integer population, Integer capitalId, String capitalName) {
        setCode(code);
        setName(name);
        setContinent(continent);
        setRegion(region);
        setPopulation(population);
        setCapitalId(capitalId);
        setCapitalName(capitalName);
    }

    /**
     * Gets the code.
     *
     * @return the 3 letter code
     */
    public String getCode() {
        return code;
    }

    /**
     * Changes the code. It has to be exactly 3 letters (no numbers).
     * Small letters get turned into capitals, so "gbr" becomes "GBR".
     *
     * @param code the new code
     */
    public void setCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Code must not be blank or null.");
        }
        code = code.trim().toUpperCase();

        if (code.length() != 3) {
            throw new IllegalArgumentException("Code must be exactly 3 letters but was: " + code);
        }

        // go through each character and make sure it's a letter from A to Z
        for (int i = 0; i < code.length(); i++) {
            char letter = code.charAt(i);
            if (letter < 'A' || letter > 'Z') {
                throw new IllegalArgumentException("Code must be exactly 3 letters but was: " + code);
            }
        }

        this.code = code;
    }

    /**
     * Gets the name.
     *
     * @return the country name
     */
    public String getName() {
        return name;
    }

    /**
     * Changes the name. It can't be empty or longer than 100 characters.
     *
     * @param name the new name
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name must not be blank or null.");
        }
        if (name.trim().length() > 100) {
            throw new IllegalArgumentException("Name must not be longer than 100 characters.");
        }
        this.name = name.trim();
    }

    /**
     * Gets the continent.
     *
     * @return the continent
     */
    public String getContinent() {
        return continent;
    }

    /**
     * Changes the continent. It has to be one of the 7 real continents in the database.
     *
     * @param continent the new continent
     */
    public void setContinent(String continent) {
        if (continent == null || continent.trim().isEmpty()) {
            throw new IllegalArgumentException("Continent must not be blank or null.");
        }
        continent = continent.trim();

        if (!continent.equals("Africa") && !continent.equals("Antarctica")
                && !continent.equals("Asia") && !continent.equals("Europe")
                && !continent.equals("North America") && !continent.equals("Oceania")
                && !continent.equals("South America")) {
            throw new IllegalArgumentException("Continent '" + continent
                    + "' does not match any known continent");
        }

        this.continent = continent;
    }

    /**
     * Gets the region.
     *
     * @return the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * Changes the region. It can't be empty or longer than 100 characters.
     *
     * @param region the new region
     */
    public void setRegion(String region) {
        if (region == null || region.trim().isEmpty()) {
            throw new IllegalArgumentException("Region must not be blank or null.");
        }
        if (region.trim().length() > 100) {
            throw new IllegalArgumentException("Region must not be longer than 100 characters.");
        }
        this.region = region.trim();
    }

    /**
     * Gets the population.
     *
     * @return how many people live there
     */
    public Integer getPopulation() {
        return population;
    }

    /**
     * Changes the population. It can't be missing and it can't be below 0.
     *
     * @param population the new population
     */
    public void setPopulation(Integer population) {
        if (population == null) {
            throw new IllegalArgumentException("Population must not be null.");
        }
        if (population < 0) {
            throw new IllegalArgumentException("Population cannot be negative but was: " + population);
        }
        this.population = population;
    }

    /**
     * Gets the capital city ID.
     *
     * @return the ID, or null if there's no capital
     */
    public Integer getCapitalId() {
        return capitalId;
    }

    /**
     * Changes the capital city ID. Null is fine (it means no capital),
     * but if there is a number it has to be above 0.
     *
     * @param capitalId the new capital ID
     */
    public void setCapitalId(Integer capitalId) {
        if (capitalId != null && capitalId <= 0) {
            throw new IllegalArgumentException("Capital city ID must be greater than 0 but was: " + capitalId);
        }
        this.capitalId = capitalId;
    }

    /**
     * Gets the capital city name.
     *
     * @return the name, or null if there's no capital
     */
    public String getCapitalName() {
        return capitalName;
    }

    /**
     * Changes the capital city name. If it's empty we save null, which means "no capital".
     * It can't be longer than 100 characters.
     *
     * @param capitalName the new capital name
     */
    public void setCapitalName(String capitalName) {
        if (capitalName == null || capitalName.trim().isEmpty()) {
            this.capitalName = null;
            return;
        }
        if (capitalName.trim().length() > 100) {
            throw new IllegalArgumentException("Capital name must not be longer than 100 characters.");
        }
        this.capitalName = capitalName.trim();
    }

    /**
     * Makes one line of the report with all the columns lined up.
     * The population gets commas in it and a missing capital shows "N/A".
     *
     * @return one line of the report
     */
    public String toReportRow() {
        String capital = capitalName;
        if (capital == null) {
            capital = "N/A";
        }
        return String.format("%-4s %-45s %-15s %-26s %,15d  %s",
                code, name, continent, region, population, capital);
    }

    /**
     * Shows every value in the country. Handy for testing.
     *
     * @return all the values as text
     */
    @Override
    public String toString() {
        return "Country{code=" + code + ", name=" + name + ", continent=" + continent
                + ", region=" + region + ", population=" + population
                + ", capitalId=" + capitalId + ", capitalName=" + capitalName + "}";
    }
}
