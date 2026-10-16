# USE CASE: 5 Retrieve Population Figure

## CHARACTERISTIC INFORMATION
### Goal in Context
As a *Researcher* I want *to query the exact population of a specific geographical entity* so that *I have quick, targeted figures for documentation or decision-making.*

### Scope
System.

### Level
Primary task.

### Preconditions
Database contains populated census/demographic data.

### Success End Condition
A single quantitative figure representing the population of the requested area is returned.

### Failed End Condition
No figure is returned or an error is displayed.

### Primary Actor
Researcher at the World Health Organisation.

### Trigger
A direct query for a specific area's total population.

## MAIN SUCCESS SCENARIO
1. Employee chooses the category to query: World, Continent, Region, Country, District, or City.
2. Employee provides the specific name identifier (omitted if querying the World).
3. System queries the database for the matching entity.
4. System calculates or retrieves the aggregate population figure.
5. System displays the entity name and its total population count.

## EXTENSIONS
2a. **Entity not found in the database:**
  1. System reports that the entity does not exist.
  2. Scenario ends.

## SUB-VARIATIONS
This use case covers:
- Total population of the world.
- Total population of a selected continent.
- Total population of a selected region.
- Total population of a selected country.
- Total population of a selected district.
- Total population of a selected city.
