package com.napier.sem;

import java.util.List;

/**
 * The main class. When the program starts it runs main().
 * main() connects to the database, prints the reports and then disconnects.
 */
public class App {

    public static void main(String[] args) {
        // N for the "top N" reports. The user can give it as the first argument,
        // e.g. "java -jar seMethods.jar 10". If nothing is given, 5 is used.
        int n = readN(args, 5);

        // make the object that connects to the database, then connect
        DatabaseConnector con = new DatabaseConnector();
        con.connect();

        // UC1 - country reports
        showCountryReports(con, n);

        // UC2 - city reports
        CityReport cityReport = new CityReport(con.getConnection());
        cityReport.printCities(cityReport.getTopCitiesInWorld(n));

        // UC5 - print some population lookups
        showPopulationLookups(con);

        // UC4 - print the population breakdown reports
        showPopulationBreakdowns(con);

        // UC - Language report
        showLanguageReport(con);

        // always disconnect when we are finished
        con.disconnect();
    }

    // UC1 - country reports

    // Prints all 6 country reports.
    // n = how many countries to show in the "top N" reports
    public static void showCountryReports(DatabaseConnector con, int n) {
        // this object runs the country queries
        CountryReport countryReport = new CountryReport(con.getConnection());

        countryReport.printReport("All countries in the world",
                countryReport.getAllCountries());

        countryReport.printReport("All countries in Europe",
                countryReport.getCountriesByContinent("Europe"));

        countryReport.printReport("All countries in the Caribbean",
                countryReport.getCountriesByRegion("Caribbean"));

        countryReport.printReport("Top " + n + " countries in the world",
                countryReport.getTopCountries(n));

        countryReport.printReport("Top " + n + " countries in Asia",
                countryReport.getTopCountriesByContinent("Asia", n));

        countryReport.printReport("Top " + n + " countries in Western Europe",
                countryReport.getTopCountriesByRegion("Western Europe", n));
    }

    // UC5 - population lookups

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

    // UC4 - population breakdown reports

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
        System.out.println(String.format("%-30s %15s %26s %26s",
                title, "Total", "In cities (%)", "Not in cities (%)"));

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


    // Language report
    public static void showLanguageReport(DatabaseConnector con) {
        LanguageReport languageReport = new LanguageReport(con.getConnection());
        languageReport.displayLanguageReport();
    }


    // Reading N

    // Reads N from the first program argument.
    // If there is no argument, or it is not a whole number above 0,
    // the default value is used instead.
    private static int readN(String[] args, int defaultValue) {
        if (args.length == 0) {
            return defaultValue;
        }
        try {
            int n = Integer.parseInt(args[0].trim());
            if (n >= 1) {
                return n;
            }
            System.out.println("N must be 1 or more. Using " + defaultValue + " instead.");
        } catch (NumberFormatException e) {
            System.out.println("N must be a whole number. Using " + defaultValue + " instead.");
        }
        return defaultValue;
    }
}