# USE CASE: 1 Produce a Country Report

## CHARACTERISTIC INFORMATION
### Goal in Context
As a *Researcher* I want *to generate a report on countries organised by population* so that *I can support strategic planning and demographic assessment.*

### Scope
System.

### Level
Primary task.

### Preconditions
The database contains up-to-date national demographic and geographic records.

### Success End Condition
A report listing the requested countries sorted by population in descending order is generated.

### Failed End Condition
No report is produced.

### Primary Actor
Researcher at the World Health Organisation.

### Trigger
A request from the employee for country population data.

## MAIN SUCCESS SCENARIO
1. Employee specifies the geographic boundary (World, Continent, or Region).
2. Employee chooses whether to view all countries or restrict the output to the Top N.
3. If Top N is requested, employee inputs the value of N.
4. System validates the criteria against available database records.
5. System extracts the matching country data.
6. System sorts the records from highest population to lowest.
7. System displays the report showing: Code, Name, Continent, Region, Population, and Capital.

## EXTENSIONS
1a. **Invalid or unrecognized Continent/Region:**
 1. System notifies the employee that the geographic area does not exist.
 2. System prompts for a valid entry.
3a. **Missing or non-positive value for N:**
 1. System informs the employee that N must be an integer greated than zero.
5a. **Database connection failure:**
 1. System displays an error message indicating data services are unreachable.
 2. Use case terminates.
 
## SUB-VARIATIONS
 This use case covers:
 - All countries in the world organized by population.
 - All countries in a continent organized by population.
 - All countries in a region organized by popultaion.
 - The top N populated countries in the world.
 - The top N populated countries in a continent.
 - The top N populated countries in a region.
