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

    // ===== UC1 - country reports =====

    /**
     * Prints all 6 country reports.
     *
     * @param con the database connection
     * @param n   how many countries to show in the "top N" reports
     */
    public static void showCountryReports(DatabaseConnector con, int n) {
        CountryReport countryReport = new CountryReport(con.getConnection());

        // the 3 "all countries" reports
        List<Country> world = countryReport.getAllCountries();
        CountryReport.printReport("All countries in the world", world);

        List<Country> europe = countryReport.getCountriesByContinent("Europe");
        CountryReport.printReport("All countries in Europe", europe);

        List<Country> caribbean = countryReport.getCountriesByRegion("Caribbean");
        CountryReport.printReport("All countries in the Caribbean", caribbean);

        // the 3 "top N" reports
        List<Country> topWorld = countryReport.getTopCountries(n);
        CountryReport.printReport("Top " + n + " countries in the world", topWorld);

        List<Country> topAsia = countryReport.getTopCountriesByContinent("Asia", n);
        CountryReport.printReport("Top " + n + " countries in Asia", topAsia);

        List<Country> topWesternEurope = countryReport.getTopCountriesByRegion("Western Europe", n);
        CountryReport.printReport("Top " + n + " countries in Western Europe", topWesternEurope);
    }

    // ===== UC5 - population lookups =====

    /**
     * Does a few population lookups so we can see they work.
     * The last two are wrong on purpose, to show the program doesn't crash.
     *
     * @param con the database connection
     */
    public static void showPopulationLookups(DatabaseConnector con) {
        PopulationReport report = new PopulationReport(con.getConnection());

        // these should all print a number
        printPopulation(report, "world", "");
        printPopulation(report, "continent", "Asia");
        printPopulation(report, "region", "Caribbean");
        printPopulation(report, "country", "France");
        printPopulation(report, "district", "Scotland");
        printPopulation(report, "city", "Edinburgh");

        // these are wrong on purpose, they should print a message and carry on
        printPopulation(report, "planet", "Mars");
        printPopulation(report, "country", "   ");
    }

    /**
     * Looks up one population and prints the answer (or answers).
     * If the type or name is wrong it prints a message instead of crashing.
     *
     * @param report the lookup to use
     * @param type   world, continent, region, country, district or city
     * @param name   the name of the place (not needed for the world)
     */
    public static void printPopulation(PopulationReport report, String type, String name) {
        try {
            List<Population> answers = report.getPopulation(type, name);

            if (answers.isEmpty()) {
                System.out.println("Nothing found for " + type + " " + name);
            }

            // some places (like Birmingham) have more than one answer so we loop through them
            for (Population answer : answers) {
                System.out.println(answer);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Bad request: " + e.getMessage());
        }
    }

    // ===== UC4 - population breakdown reports =====

    /**
     * Prints the population breakdown for every continent, region and country.
     *
     * @param con the database connection
     */
    public static void showPopulationBreakdowns(DatabaseConnector con) {
        PopulationBreakdownReport breakdownReport = new PopulationBreakdownReport(con.getConnection());

        List<PopulationBreakdown> continents = breakdownReport.getContinentBreakdown();
        printBreakdown("Continent", continents);

        List<PopulationBreakdown> regions = breakdownReport.getRegionBreakdown();
        printBreakdown("Region", regions);

        List<PopulationBreakdown> countries = breakdownReport.getCountryBreakdown();
        printBreakdown("Country", countries);
    }

    /**
     * Prints one breakdown report: a title, the column names, then one line for each place.
     *
     * @param title the first column name, like "Continent"
     * @param rows  the lines to print
     */
    public static void printBreakdown(String title, List<PopulationBreakdown> rows) {
        System.out.println();
        System.out.println("Population breakdown by " + title);

        // the numbers in here are how wide each column is so it all lines up
        System.out.println(String.format("%-30s %15s %26s %26s",
                title, "Total", "In cities (%)", "Not in cities (%)"));

        if (rows.isEmpty()) {
            System.out.println("No data found");
            return;
        }

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