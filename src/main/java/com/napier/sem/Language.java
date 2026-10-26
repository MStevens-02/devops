package com.napier.sem;

/**
 * Represents language population statistics globally.
 * Encapsulates the language name, total number of speakers worldwide,
 * and its percentage share of the total world population.
 * Includes validation rules within setters to ensure data integrity.
 */
public class Language {

    /** The name of the language (e.g., English, Chinese). */
    private String languageName;

    /** Total number of speakers worldwide (stored as a long to prevent integer overflow). */
    private long totalSpeakers;

    /** The language's share of the global population expressed as a percentage (0.0 to 100.0). */
    private double worldPercentage;

    /**
     * Constructor to initialize a Language object with validation.
     *
     * @param languageName    The name of the language
     * @param totalSpeakers   Total number of speakers
     * @param worldPercentage Percentage of the world population
     */
    public Language(String languageName, long totalSpeakers, double worldPercentage) {
        setLanguageName(languageName);
        setTotalSpeakers(totalSpeakers);
        setWorldPercentage(worldPercentage);
    }

    /**
     * Gets the name of the language.
     * @return languageName
     */
    public String getLanguageName() {
        return languageName;
    }

    /**
     * Sets the language name. Validates that the name is not null or blank.
     *
     * @param languageName The language name to set
     * @throws IllegalArgumentException if the name is null or empty
     */
    public void setLanguageName(String languageName) {
        if (languageName == null || languageName.trim().isEmpty()) {
            throw new IllegalArgumentException("Language name cannot be null or blank.");
        }
        this.languageName = languageName;
    }

    /**
     * Gets the total number of speakers.
     * @return totalSpeakers
     */
    public long getTotalSpeakers() {
        return totalSpeakers;
    }

    /**
     * Sets the total number of speakers. Validates that the count is non-negative.
     *
     * @param totalSpeakers Total speakers count
     * @throws IllegalArgumentException if speakers count is negative
     */
    public void setTotalSpeakers(long totalSpeakers) {
        if (totalSpeakers < 0) {
            throw new IllegalArgumentException("Total speakers cannot be negative.");
        }
        this.totalSpeakers = totalSpeakers;
    }

    /**
     * Gets the world percentage share.
     * @return worldPercentage
     */
    public double getWorldPercentage() {
        return worldPercentage;
    }

    /**
     * Sets the world percentage share. Validates that it falls within 0 and 100.
     *
     * @param worldPercentage Percentage value (0 to 100)
     * @throws IllegalArgumentException if percentage is out of valid bounds
     */
    public void setWorldPercentage(double worldPercentage) {
        if (worldPercentage < 0.0 || worldPercentage > 100.0) {
            throw new IllegalArgumentException("World percentage must be between 0 and 100.");
        }
        this.worldPercentage = worldPercentage;
    }

    /**
     * Formats the language object into an aligned string representation
     * with comma-separated numbers and 2-decimal percentage rounding.
     *
     * @return Formatted string for console reporting
     */
    @Override
    public String toString() {
        return String.format("%-20s %,20d %23.2f%%",
                languageName, totalSpeakers, worldPercentage);
    }
}