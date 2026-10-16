package com.napier.sem;

/**
 * Starting point of the Population Reporting System.
 * <p>
 * It connects to the database, runs the reports and then disconnects.
 */
public class App {

    public static void main(String[] args) {

        // N for the "top N" reports. The user can give it as the first argument,
        // e.g. "java -jar seMethods.jar 10". If nothing is given, 5 is used.
        int n = readN(args, 5);

        // Connect to the world database
        DatabaseConnector con = new DatabaseConnector();
        con.connect();

        // Country reports (Use Case 1, reports 1-6)
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

        // The other reports are added here when each class is ready:
        // City reports (Use Case 2)            -> CityReport
        // Capital city reports (Use Case 3)    -> CapitalCityReport
        // Population breakdown (Use Case 4)    -> PopulationReport
        // Population lookups (Use Case 5)      -> PopulationReport
        // Language report (Use Case 6)         -> LanguageReport

        // Disconnect from the database
        con.disconnect();
    }

    /**
     * Reads N from the first program argument.
     * If there is no argument, or it is not a whole number above 0,
     * the default value is used instead.
     *
     * @param args         the program arguments
     * @param defaultValue the value to use when N is missing or invalid
     * @return the value of N
     */
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