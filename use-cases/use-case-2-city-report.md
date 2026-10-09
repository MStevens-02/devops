# USE CASE: 2 Produce a City Report

## CHARACTERISTIC INFORMATION
### Goal in Context
As a *Researcher* I want *to produce a report on cities organised by population* so that *I can support urban planning and regional demographic analysis.*

### Scope
System.

### Level
Primary task.

### Preconditions
Database contains current world city population and geographical data.

### Success End Condition
A report listing the requested cities sorted by population is generated.

### Failed End Condition
No report is produced.

### Primary Actor
Researcher at the World Health Organisation.

### Trigger
A user request for city demographic data.

## MAIN SUCCESS SCENARIO
1. Employee specifies the geographic scope (World, Continent, Region, Country, or District).
2. Employee specifies whether an entire list or Top N cities is needed.
3. If Top N is selected, employee provides the value of N.
4. System validates the parameters against the database.
5. System extracts matching city records.
6. System orders records by population in descending order.
7. System renders the report displaying: Name, Country, District, and Population.

## EXTENSIONS
2a. **Invalid geographic filter supplied:**
  1. System informs employee that the specified area does not exist.
  2. System prompts for re-entry or cancels.
3a. **Non-positive or missing integer for N:**
  1. System displays an error stating N must be an integer greater than zero.
5a. **Database connection failure:**
  1. System notifies the user that the database is unreachable and logs the error.

## SUB-VARIATIONS
This use case covers:
- All cities in the world organized by population.
- All cities in a continent organized by population.
- All cities in a region organized by population.
- All cities in a country organized by population.
- All cities in a district organized by population.
- The top N populated cities in the world.
- The top N populated cities in a continent.
- The top N populated cities in a region.
- The top N populated cities in a country.
- The top N populated cities in a district.
