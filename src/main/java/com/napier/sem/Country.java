package com.napier.sem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Holds the data for one country in a Country Report.
 * <p>
 * The report shows: Code, Name, Continent, Region, Population and Capital.
 * <p>
 * The country table only stores the capital as a city ID (a number).
 * To get the capital's name, the SQL query joins the city table.
 * <p>
 * Every value is checked in its setter. The constructor uses the setters,
 * so a Country can never be created with bad data.
 */
public class Country {

  // The continents that exist in the world database.
  public static final List<String> VALID_CONTINENTS = List.of(
          "Africa", "Antarctica", "Asia", "Europe",
          "North America", "Oceania", "South America");

  // Longest text we allow, to catch broken data.
  private static final int MAX_NAME_LENGTH = 100;
  private static final int MAX_REGION_LENGTH = 100;
  private static final int MAX_CAPITAL_NAME_LENGTH = 100;

  private String code;          // 3-letter code, e.g. "GBR"
  private String name;          // e.g. "United Kingdom"
  private String continent;     // e.g. "Europe"
  private String region;        // e.g. "British Islands"
  private Integer population;   // number of people, never negative
  private Integer capitalId;    // ID of the capital city, null if there is no capital
  private String capitalName;   // name of the capital city, null if there is no capital

  /**
   * Creates a new Country. Every value is checked before it is saved.
   *
   * @param code        3-letter country code
   * @param name        country name
   * @param continent   continent name
   * @param region      region name
   * @param population  number of people
   * @param capitalId   ID of the capital city, or null
   * @param capitalName name of the capital city, or null
   * @throws IllegalArgumentException if any value is not valid
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
   * Turns one row from the database into a Country.
   *
   * @param rs the query result, already moved to a row with rs.next()
   * @return a Country with the values from that row
   * @throws SQLException if a column cannot be read
   */
  public static Country fromResultSet(ResultSet rs) throws SQLException {
    // If a country has no capital, getInt() gives 0 instead of null.
    // wasNull() tells us the value was really empty, so we use null.
    int capital = rs.getInt("Capital");
    Integer capitalId = rs.wasNull() ? null : capital;

    return new Country(
            rs.getString("Code"),
            rs.getString("Name"),
            rs.getString("Continent"),
            rs.getString("Region"),
            rs.getInt("Population"),
            capitalId,
            rs.getString("CapitalName"));
  }

  public String getCode() {
    return code;
  }

  /**
   * Sets the country code. It must be exactly 3 letters.
   * Lower case is changed to upper case, so "gbr" becomes "GBR".
   */
  public void setCode(String code) {
    String cleanCode = requireString(code, "Code").toUpperCase();
    if (!cleanCode.matches("[A-Z]{3}")) {
      throw new IllegalArgumentException(
              "Code must be exactly 3 letters but was: " + cleanCode);
    }
    this.code = cleanCode;
  }

  public String getName() {
    return name;
  }

  /**
   * Sets the country name. It cannot be empty or too long.
   */
  public void setName(String name) {
    this.name = requireMaxLength(requireString(name, "Name"), "Name", MAX_NAME_LENGTH);
  }

  public String getContinent() {
    return continent;
  }

  /**
   * Sets the continent. It must be one of the names in VALID_CONTINENTS.
   */
  public void setContinent(String continent) {
    String cleanContinent = requireString(continent, "Continent");
    if (!VALID_CONTINENTS.contains(cleanContinent)) {
      throw new IllegalArgumentException("Continent '" + cleanContinent
              + "' does not match any known continent " + VALID_CONTINENTS);
    }
    this.continent = cleanContinent;
  }

  public String getRegion() {
    return region;
  }

  /**
   * Sets the region. It cannot be empty or too long.
   */
  public void setRegion(String region) {
    this.region = requireMaxLength(requireString(region, "Region"), "Region", MAX_REGION_LENGTH);
  }

  public Integer getPopulation() {
    return population;
  }

  /**
   * Sets the population. It cannot be empty or below 0.
   */
  public void setPopulation(Integer population) {
    if (population == null) {
      throw new IllegalArgumentException("Population must not be null.");
    }
    if (population < 0) {
      throw new IllegalArgumentException(
              "Population cannot be negative but was: " + population);
    }
    this.population = population;
  }

  public Integer getCapitalId() {
    return capitalId;
  }

  /**
   * Sets the capital city ID. It can be null (no capital),
   * but if there is a value it must be above 0.
   */
  public void setCapitalId(Integer capitalId) {
    if (capitalId != null && capitalId <= 0) {
      throw new IllegalArgumentException(
              "Capital city ID must be greater than 0 but was: " + capitalId);
    }
    this.capitalId = capitalId;
  }

  public String getCapitalName() {
    return capitalName;
  }

  /**
   * Sets the capital city name. An empty name is saved as null,
   * which means the country has no capital.
   */
  public void setCapitalName(String capitalName) {
    if (capitalName == null || capitalName.trim().isEmpty()) {
      this.capitalName = null;
      return;
    }
    this.capitalName = requireMaxLength(capitalName.trim(), "Capital name", MAX_CAPITAL_NAME_LENGTH);
  }

  /**
   * Returns this country as one line of the report, with the columns lined up.
   * The population has commas (e.g. 59,623,400) and a missing capital shows "N/A".
   */
  public String toReportRow() {
    return String.format("%-4s %-45s %-15s %-26s %,15d  %s",
            code, name, continent, region, population,
            capitalName == null ? "N/A" : capitalName);
  }

  /**
   * Returns all the values of this country. Useful when testing or debugging.
   */
  @Override
  public String toString() {
    return "Country{code=" + code + ", name=" + name + ", continent=" + continent
            + ", region=" + region + ", population=" + population
            + ", capitalId=" + capitalId + ", capitalName=" + capitalName + "}";
  }

  /**
   * Checks that a text value is not empty.
   * Returns the text with extra spaces removed from both ends.
   */
  private static String requireString(String value, String field) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException(field + " must not be blank or null.");
    }
    return value.trim();
  }

  /**
   * Checks that a text value is not longer than the limit.
   */
  private static String requireMaxLength(String value, String field, int max) {
    if (value.length() > max) {
      throw new IllegalArgumentException(
              field + " must not be longer than " + max + " characters.");
    }
    return value;
  }
}