package com.napier.sem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class CountryReport {

    private static final String BASE_QUERY =
            "SELECT country.Code, country.Name, country.Continent, country.Region, " +
                    "country.Population, country.Capital, city.Name AS CapitalName " +
                    "FROM country " +
                    "LEFT JOIN city ON country.Capital = city.ID";

    private final Connection con;

    public CountryReport(Connection con) {
        if (con == null){
            throw new java.lang.NullPointerException("Con argument cannot be null");
        }
        this.con = con;
    }

    Statement stmt = con.createStatement();
    ResultSet rset = stmt.executeQuery(BASE_QUERY);



}
