package com.napier.sem;

// Create a public class called CapitalCity that extends City
public class CapitalCity extends City {
    private long id;
    private String name;
    private String countryCode;
    private String countryName;
    private long population;


    // Return the capital city's ID using a get method
    public long getId() { return id; }

    // Create a set method to validate id
    public void setId(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Capital city id must be positive: " + id);
        }
        this.id = id;
    }

    // Create a get method to return name
    public String getName() { return name; }

    // Create a set method to validate name
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Capital city name must not be null or blank");
        }
        this.name = name.trim();
    }

    // Create a get CountryCode method to return CountryCode
    public String getCountryCode() { return countryCode; }

    // Create a set method for country code to validate it
    public void setCountryCode(String countryCode) {
        if (countryCode == null || countryCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Country code must not be null or blank");
        }
        if (countryCode.trim().length() != 3) {
            throw new IllegalArgumentException("Country code must be 3 characters: " + countryCode);
        }
        this.countryCode = countryCode.trim();
    }

    // Create a get method to return country name
    public String getCountryName() { return countryName; }

    // Create a set method to validate country name
    public void setCountryName(String countryName) {
        if (countryName == null || countryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Country name must not be null or blank");
        }
        this.countryName = countryName.trim();
    }

    // Create a get method to return population
    public long getPopulation() { return population; }

    // Create a set method to validate population
    public void setPopulation(long population) {
        if (population < 0) {
            throw new IllegalArgumentException("Population must not be negative: " + population);
        }
        this.population = population;
    }

    // Create a toString method to return the capital city report columns
    @Override
    public String toString() {
        return "CapitalCity{" +
                "name='" + getName() + '\'' +
                ", country='" + getCountryName() + '\'' +
                ", population=" + getPopulation() +
                '}';
    }
}