package com.sample.sem;

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

    // Create a get CountryCode method to validate it
    public String getCountryCode() {
        if (countryCode == null || countryCode.trim().isEmpty()){
            System.out.println("Country Code must not be Null or blank. ");
        }
        return countryCode;
    }

    // Create a set CountryCode method to return CountryCode
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    // Create a get district method to return district
    public String getDistrict() {
        return district;
    }

    // Create a set district method to validate district
    public void setDistrict(String district) {
        if(countryCode == null || countryCode.trim().isEmpty()){
            System.out.println("District must not be Null or Blank. ");
        }
        this.district = district;
    }

    // Create a get method to return population
    public long getPopulation() {
        return population;
    }

    // Create a set method to validate population
    public void setPopulation(long population) {
        if (population <= 0){
            System.out.println("Population must not be negative. ");
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
