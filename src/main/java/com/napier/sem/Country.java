package com.napier.sem;


public class Country {

  private String code;
  private String name;
  private String continent;
  private String region;
  private long population;
  private long capital;

  public Country(String code, String name, String continent, String region, long population, long capital) {
    this.code = code;
    this.name = name;
    this.continent = continent;
    this.region = region;
    this.population = population;
    this.capital = capital;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    String cleancode = code.toUpperCase();
    if (cleancode.matches("[A-Z]{3}")) {
      throw  new IllegalArgumentException("Invalid code - Code must be exactly 3 characters but was: " + cleancode);
    }
    this.code = cleancode;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public String getContinent() {
    return continent;
  }

  public void setContinent(String continent) {
    this.continent = continent;
  }


  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }


  public long getPopulation() {
    return population;
  }

  public void setPopulation(long population) {
    this.population = population;
  }


  public long getCapital() {
    return capital;
  }

  public void setCapital(long capital) {
    this.capital = capital;
  }


}
