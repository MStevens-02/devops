# USE CASE: 6 Produce Language Report

## CHARACTERISTIC INFORMATION
### Goal in Context
As a *Researcher* I want *to produce a report on major global languages* so that *I can evaluate linguistic accessibility and global reach.*

### Scope
System.

### Level
Primary task.

### Preconditions
Country language data and national population counts exist in the database.

### Success End Condition
A report ranking Chinese, English, Hindi, Spanish, and Arabic by number of speakers and global percentage is displayed.

### Failed End Condition
No report is produced.

### Primary Actor
Researcher at the World Health Organisation.

### Trigger
A user requests the major languages comparative report.

## MAIN SUCCESS SCENARIO
1. Employee initiates the language report generation.
2. System identifies the target languages: Chinese, English, Hindi, Spanish, and Arabic.
3. System calculates the total number of speakers for each target language by computing the sum of `(Country Population * Language Percentage)` globally.
4. System retrieves total world population to calculate the global percentage per language.
5. System sorts the languages from highest to lowest number of speakers.
6. System outputs the report with columns: Language, Number of Speakers, and Percentage of World Population.

## EXTENSIONS
3a. **No country language data available:**
  1. System alerts the user that demographic language data is missing.
  2. Scenario terminates.

## SUB-VARIATIONS
This use case covers:
- Comparative report showing total speakers and percentage of world population for Chinese, English, Hindi, Spanish, and Arabic, ordered from greatest to smallest.
