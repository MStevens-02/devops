package com.napier.sem;

// Create a public class called CapitalCity that extends City
public class CapitalCity extends City {

    // Create a toString method to return the capital city report columns
    @Override
    public String toString() {
        return "CapitalCity{" +
                "name='" + getName() + '\'' +
                ", country='" + getCountryName() + '\'' +
                ", population=" + getPopulation() +
                '}';
    }
}