package com.napier.sem;
import java.util.List;

/**
 * Country representation of each row.
 * <p>
 * As Coursework states that columns required is: Code, Name, Continent,
 * Region, Population and Capital City.
 * <p>
 * This uses the country table from the world database. Capital is stored as a number
 * to then be able to be look up the city from the city table. This also stores the city's
 * name by using the SQL Statement to Join the city table to find the city's name.
 * <p>
 * All fields of the country is validated and checked in the setters ensuring there is no
 * invalid data given.
 *
 */
public class Country {

  // List of all possible continents that exist in the database and in the world.
  public static final List<String> VALID_CONTINENTS = List.of(
          "Africa", "Antarctica", "Asia", "Europe",
          "North America", "Oceania", "South America");

  // Maximum Lengths gave 100 as a rough guide.
  private static final int MAX_NAME_LENGTH = 100;
  private static final int MAX_REGION_LENGTH = 100;
  private static final int MAX_CAPITAL_NAME_LENGTH = 100;


  private String code;
  private String name;
  private String continent;
  private String region;
  private Integer population;
  private Integer capitalId; // This will be null if the country does not have a Capital City
  private String capitalName; // This will be null if the country does not have a Capital City


  /**
   * @param code
   * @param name
   * @param continent
   * @param region
   * @param population
   * @param capitalId
   * @param capitalName
   * @throws IllegalArgumentException if any value is invalid.
   */
  public Country(String code, String name, String continent, String region, Integer population, Integer capitalId, String capitalName) {
    setCode(code);
    setName(name);
    setContinent(continent);
    setRegion(region);
    setPopulation(population);
    setCapitalId(capitalId);
    setCapitalName(capitalName);
  }



  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    String cleanCode = requireString(code, "Code").toUpperCase();
    if (!cleanCode.matches("[A-Z]{3}")) {
      throw new IllegalArgumentException("Invalid code - Code must be exactly 3 characters but was: " + cleanCode);
    }
    this.code = cleanCode;
  }



  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = requireLengthMax(requireString(name, "Name"),"Name", MAX_NAME_LENGTH);
  }



  public String getContinent() {
    return continent;
  }

  public void setContinent(String continent) {
    String cleanContinent = requireString(continent, "Continent");
    for (String valid_continent : VALID_CONTINENTS) {
      if (valid_continent.equals(cleanContinent)) {
        this.continent = cleanContinent;
        return;
      }
    }
    throw  new IllegalArgumentException("Invalid continent - Continent: " + cleanContinent + " does not match any known continent - " + VALID_CONTINENTS);
  }



  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = requireLengthMax(requireString(region, "Region"),"Region", MAX_REGION_LENGTH);
  }



  public Integer getPopulation() {
    return population;
  }

  public void setPopulation(Integer population) {
    if (population < 0){
      throw new IllegalArgumentException("Population cannot be less than 0 but was given: " + population);
    }
    this.population = population;
  }



  public Integer getCapitalId() {
    return capitalId;
  }

  public void setCapitalId(Integer capital) {
    if (capital != null && capital <= 0){
      throw new IllegalArgumentException("Capital City ID cannot be less than 0 but was given: " + capital);
    }
    this.capitalId = capital;
  }



  public String getCapitalName() {
    return capitalName;
  }

  public void setCapitalName(String capitalName) {
    if (capitalName == null || capitalName.trim().isEmpty()) {
      this.capitalName = capitalName;
      return;
    }
    this.capitalName = requireLengthMax(capitalName.trim(),"Capital Name", MAX_CAPITAL_NAME_LENGTH);
  }




  public String toReportRow() {
    return String.format("%-4s %-45s %-15s %-26s %,15d  %s",
            code, name, continent, region, population,
            capitalName == null ? "N/A" : capitalName);
  }

  @Override
  public String toString() {
    return "Country{code=" + code + ", name=" + name + ", continent=" + continent
            + ", region=" + region + ", population=" + population
            + ", capitalId=" + capitalId + ", capitalName=" + capitalName + "}";
  }




  private static String requireString(String value, String field){
    if (value == null || value.trim().isEmpty()){
      throw new IllegalArgumentException(field + " must not be blank or null.");
    }
  return  value.trim();
  }

  private static String requireLengthMax(String value, String field, int max){
    if (value.length() > max){
      throw new IllegalArgumentException(field + " must not be longer than " + max + " characters.");
    }
    return value;
  }


}
