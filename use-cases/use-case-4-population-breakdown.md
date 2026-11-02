# USE CASE: 4 Produce Population Breakdown Report

## CHARACTERISTIC INFORMATION
### Goal in Context
As a *Researcher* I want *to generate a breakdown of people living in cities versus outside cities* so that *I can assess urbanisation rates across different geographic levels.*

### Scope
System.

### Level
Primary task.

### Preconditions
Database contains accurate total population data as well as city population figures.

### Success End Condition
A comparative report displaying total, urban, and non-urban population counts and percentages is delivered.

### Failed End Condition
No report is produced.

### Primary Actor
Researcher at the World Health Organisation.

### Trigger
A request for an urban/rural population analysis.

## MAIN SUCCESS SCENARIO
1. Employee specifies the aggregation boundary: Continent, Region, or Country.
2. System computes total population for each geographic entity in that boundary.
3. System aggregates the population living inside cities for each entity.
4. System computes:
   - Population living outside cities (Total - Urban).
   - Percentage of total living in cities.
   - Percentage of total living outside cities.
5. System presents the report containing: Name of Area, Total Population, Living in Cities (Count & %), Not Living in Cities (Count & %).

## EXTENSIONS
1a. **Invalid boundary selection:**
  1. System alerts employee to choose Continent, Region, or Country.
3a. **Data discrepancy (City population exceeds total population):**
  1. System flags data inconsistency for admin review and defaults non-urban to 0.

## SUB-VARIATIONS
This use case covers:
- Population breakdown (urban vs. rural) for each continent.
- Population breakdown (urban vs. rural) for each region.
- Population breakdown (urban vs. rural) for each country.
