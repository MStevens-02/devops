package com.napier.sem;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import java.util.List;

/**
 * The main class. When the program starts it runs main().
 * main() connects to the database, prints the reports and then disconnects.
 */
public class App {

    public static void main(String[] args) {
        // make the object that connects to the database, then connect
        DatabaseConnector con = new DatabaseConnector();
        con.connect();

        // Create a way to read in the user input.txt file, take in the inputs and return all listed
        // Create a variable called input
        Properties input = new Properties();
        // then try to read the file
        try (InputStream in = App.class.getResourceAsStream("/input.txt")) {
            // if success then load the file
            input.load(in);
        } catch (IOException | NullPointerException e) {
            // if it fails to load the file, disconnect and stop
            System.out.println("Couldn't load input.txt");
            con.disconnect();
            return;
        }

        // use input.getProperty to capture n, continent, region, country, and district from input.txt
        // n is turned from text into a number with Integer.parseInt
        int n = Integer.parseInt(input.getProperty("n"));
        String continent = input.getProperty("continent");
        String region = input.getProperty("region");
        String country = input.getProperty("country");
        String district = input.getProperty("district");

        // City reports
        // create the city report object using the database connection
        CityReport cityReport = new CityReport(con.getConnection());

        // print all the cities in the world, continent, region, country and district
        cityReport.printCities(cityReport.getAllCitiesInWorld());
        cityReport.printCities(cityReport.getAllCitiesInContinent(continent));
        cityReport.printCities(cityReport.getAllCitiesInRegion(region));
        cityReport.printCities(cityReport.getAllCitiesInCountry(country));
        cityReport.printCities(cityReport.getAllCitiesInDistrict(district));

        // print the top n cities in the world
        cityReport.printCities(cityReport.getTopCitiesInWorld(n));

        // UC5 - print some population lookups
        showPopulationLookups(con);

        // UC4 - print the population breakdown reports
        showPopulationBreakdowns(con);

        // always disconnect when we are finished
        con.disconnect();
    }


    // ---------------------------------------------------------------------
    // UC5 - population lookups
    // ---------------------------------------------------------------------

    // Tries a few lookups so we can see that they work.
    public static void showPopulationLookups(DatabaseConnector con) {
        // this object runs the lookup queries
        PopulationReport report = new PopulationReport(con.getConnection());

        // good requests: each one should print a population
        printPopulation(report, "world", "");
        printPopulation(report, "continent", "Asia");
        printPopulation(report, "region", "Caribbean");
        printPopulation(report, "country", "France");
        printPopulation(report, "district", "Scotland");
        printPopulation(report, "city", "Edinburgh");

        // bad requests: each one should print a message and NOT crash the program
        printPopulation(report, "planet", "Mars");
        printPopulation(report, "country", "   ");
    }

    // Asks for one population and prints the answer.
    // type = world, continent, region, country, district or city
    // name = the name of the area (it is not used for the world)
    public static void printPopulation(PopulationReport report, String type, String name) {
        try {
            // ask the report for the answers
            List<Population> answers = report.getPopulation(type, name);

            if (answers.isEmpty()) {
                // the list is empty when the area was not found
                System.out.println("Nothing found for " + type + " " + name);
            } else {
                // a city can have more than one answer, so we use a loop
                for (Population answer : answers) {
                    System.out.println(answer);
                }
            }
        } catch (IllegalArgumentException e) {
            // this happens when the type or the name is not okay
            System.out.println("Bad request: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------------
    // UC4 - population breakdown reports
    // ---------------------------------------------------------------------

    // Prints the breakdown for every continent, every region and every country.
    public static void showPopulationBreakdowns(DatabaseConnector con) {
        // this object runs the breakdown queries
        PopulationBreakdownReport breakdownReport = new PopulationBreakdownReport(con.getConnection());

        printBreakdown("Continent", breakdownReport.getContinentBreakdown());
        printBreakdown("Region", breakdownReport.getRegionBreakdown());
        printBreakdown("Country", breakdownReport.getCountryBreakdown());
    }

    // Prints a title, a header line and then one line for each row.
    // title = the first column heading, for example "Continent"
    public static void printBreakdown(String title, List<PopulationBreakdown> rows) {
        // a blank line, then the report title
        System.out.println();
        System.out.println("Population breakdown by " + title);

        // the column headings (the numbers say how wide each column is)
        System.out.printf("%-30s %15s %26s %26s%n",
                title, "Total", "In cities (%)", "Not in cities (%)");

        // check there is something to print
        if (rows.isEmpty()) {
            System.out.println("No data found");
            return;
        }

        // print one line for each row
        for (PopulationBreakdown row : rows) {
            System.out.println(row);
        }
    }
}
