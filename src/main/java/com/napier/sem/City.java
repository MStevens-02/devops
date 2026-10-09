package com.napier.sem;

// Create a public class called city to store the methods
public class City {
    private long id;
    private String name;
    private String countryCode;
    private String district;
    private long population;


    // Returns the city's ID using a get method
    public long getId() {
        return id;
    }

    // Create a set method to valitate id
    public void setId(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("City id must be positive. " + id);
        }
        this.id = id;
    }

    // Create a get method to return name
    public String getName() {
        return name;
    }

    // Create a set method to validate name
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()){
            System.out.println("Name must not be Null or Blank");
        }
        this.name = name;
    }

    // Create a get CountryCode method to return CountryCode
    public String getCountryCode() {
        if (countryCode == null || countryCode.trim().isEmpty()){
            System.out.println("Country Code must not be Null or blank. ");
        }
        return countryCode;
    }

    // Create a set method for country code to validate it
    public void setCountryCode(){
        if (countryCode == null || countryCode.trim().isEmpty()){
            System.out.println("Country Code must not be Null or blank. ");
        }
    }

    // Create a set CountryCode method to return CountryCode
    public void setCountryCode(String countryCode) {
        if (countryCode == null || countryCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Country code must not be null or blank");
        }
        if (countryCode.trim().length() != 3) {
            throw new IllegalArgumentException("Country code must be 3 characters: " + countryCode);
        }
        this.countryCode = countryCode.trim();
    }

    // Create a get district method to return district
    public String getDistrict() {
        return district;
    }

    // Create a set district method to validate district
    public void setDistrict(String district) {
        if (district == null || district.trim().isEmpty()) {
            throw new IllegalArgumentException("District must not be null or blank");
        }
        this.district = district;
    }

    // Create a get method to return population
    public long getPopulation() {
        return population;
    }

    // Create a set method to validate population
    public void setPopulation(long population) {
        if (population < 0) {
            throw new IllegalArgumentException("Population must not be negative: " + population);
        }
        this.population = population;
    }


    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", district='" + district + '\'' +
                ", population=" + population +
                '}';
    }
}
