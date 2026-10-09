# USE CASE: 3 Produce a Capital City Report

## CHARACTERISTIC INFORMATION
### Goal in Context
As a *Researcher* I want *to produce a report on capital cities organized by population* so that *I can examine administrative population concentrations.*

### Scope
System.

### Level
Primary task.

### Preconditions
Database contains current capital city linkages and population figures.

### Success End Condition
A report listing capital cities ordered by population is presented.

### Failed End Condition
No report is produced.

### Primary Actor
Researcher at the World Health Organisation.

### Trigger
A user request for capital city demographic data.

## MAIN SUCCESS SCENARIO
1. Employee selects the scope (World, Continent, or Region).
2. Employee indicates whether to limit to the Top N capital cities.
3. If limiting, employee inputs the value N.
4. System retrieves the corresponding capital city records.
5. System sorts the results from largest to smallest population.
6. System outputs the report with columns: Name, Country, and Population.

## EXTENSIONS
1a. **Unrecognized Continent or Region specified:**
  1. System flags that the region/continent is invalid.
  2. System halts report generation.
3a. **Invalid or missing N value:**
  1. System prompts for a valid numeric limit.

## SUB-VARIATIONS
This use case covers:
- All capital cities in the world organized by population.
- All capital cities in a continent organized by population.
- All capital cities in a region organized by population.
- The top N populated capital cities in the world.
- The top N populated capital cities in a continent.
- The top N populated capital cities in a region.
