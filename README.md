# Team 5 - Population Reporting System

![workflow](https://github.com/MStevens-02/devops/actions/workflows/build.yml/badge.svg)
[![LICENSE](https://img.shields.io/github/license/MStevens-02/devops.svg?style=flat-square)](https://github.com/MStevens-02/devops/blob/master/LICENSE)
[![Releases](https://img.shields.io/github/release/MStevens-02/devops/all.svg?style=flat-square)](https://github.com/MStevens-02/devops/releases)
![GitHub commit activity](https://img.shields.io/github/commit-activity/m/MStevens-02/devops)
[![codecov](https://codecov.io/gh/MStevens-02/devops/branch/master/graph/badge.svg?token=ZFMG96FXDM)](https://codecov.io/gh/MStevens-02/devops)

## Project Overview
This project is part of the **SET08103 Software Engineering Methods** module at Edinburgh Napier University. It is a population reporting system that produces demographic reports based on world population data.

The application is built using Java and Maven, utilizes a MySQL database for data retrieval, and runs inside Docker containers. We are employing a Continuous Integration (CI) pipeline via GitHub Actions to automate the build and testing processes.

## Features
- Generates reports on global, continental, and regional populations.
- Identifies the top populated countries and cities.
- Provides urban vs. non-urban population percentages.
- Tracks specific language usage statistics worldwide.

## Use Case Diagram
The diagram below shows the six use cases (UC1–UC6), grouped by report type. Full descriptions are in the [`use-cases`](use-cases/) folder.

![Use Case Diagram](use-cases/use-case-diagram.drawio.svg)

## Report Results
 
The application produces all 32 reports requested in the brief. N defaults to 5 and can be passed as the first argument (e.g. `java -jar seMethods.jar 10`). Example places are set in [`input.txt`](src/main/resources/input.txt) and [`App.java`](src/main/java/com/napier/sem/App.java).
 
Screenshots are taken from the program running in Docker against the `world` database. Reports that return a long list (for example all 4,079 cities in the world) show the start of the output.

### Summary
 
| # | Report | Use case | Status |
|---|---|---|---|
| 1 | [All countries in the world](screenshots/uc1-01-countries-world.png) | UC1 | ✅ Met |
| 2 | [All countries in a continent (Europe)](screenshots/uc1-02-countries-continent.png) | UC1 | ✅ Met |
| 3 | [All countries in a region (Caribbean)](screenshots/uc1-03-countries-region.png) | UC1 | ✅ Met |
| 4 | [Top N countries in the world](screenshots/uc1-04-top-countries-world.png) | UC1 | ✅ Met |
| 5 | [Top N countries in a continent (Asia)](screenshots/uc1-05-top-countries-continent.png) | UC1 | ✅ Met |
| 6 | [Top N countries in a region (Western Europe)](screenshots/uc1-06-top-countries-region.png) | UC1 | ✅ Met |
| 7 | [All cities in the world](screenshots/uc2-01-cities-world.png) | UC2 | ✅ Met |
| 8 | [All cities in a continent (Asia)](screenshots/uc2-02-cities-continent.png) | UC2 | ✅ Met |
| 9 | [All cities in a region (Caribbean)](screenshots/uc2-03-cities-region.png) | UC2 | ✅ Met |
| 10 | [All cities in a country (France)](screenshots/uc2-04-cities-country.png) | UC2 | ✅ Met |
| 11 | [All cities in a district (Scotland)](screenshots/uc2-05-cities-district.png) | UC2 | ✅ Met |
| 12 | [Top N cities in the world](screenshots/uc2-06-top-cities-world.png) | UC2 | ✅ Met |
| 13 | [Top N cities in a continent (Asia)](screenshots/uc2-07-top-cities-continent.png) | UC2 | ✅ Met |
| 14 | [Top N cities in a region (Caribbean)](screenshots/uc2-08-top-cities-region.png) | UC2 | ✅ Met |
| 15 | [Top N cities in a country (France)](screenshots/uc2-09-top-cities-country.png) | UC2 | ✅ Met |
| 16 | [Top N cities in a district (Scotland)](screenshots/uc2-10-top-cities-district.png) | UC2 | ✅ Met |
| 17 | [All capital cities in the world](screenshots/uc3-01-capitals-world.png) | UC3 | ✅ Met |
| 18 | [All capital cities in a continent (Asia)](screenshots/uc3-02-capitals-continent.png) | UC3 | ✅ Met |
| 19 | [All capital cities in a region (Caribbean)](screenshots/uc3-03-capitals-region.png) | UC3 | ✅ Met |
| 20 | [Top N capital cities in the world](screenshots/uc3-04-top-capitals-world.png) | UC3 | ✅ Met |
| 21 | [Top N capital cities in a continent (Asia)](screenshots/uc3-05-top-capitals-continent.png) | UC3 | ✅ Met |
| 22 | [Top N capital cities in a region (Caribbean)](screenshots/uc3-06-top-capitals-region.png) | UC3 | ✅ Met |
| 23 | [Population in and out of cities for each continent](screenshots/uc4-01-breakdown-continent.png) | UC4 | ✅ Met |
| 24 | [Population in and out of cities for each region](screenshots/uc4-02-breakdown-region.png) | UC4 | ✅ Met |
| 25 | [Population in and out of cities for each country](screenshots/uc4-03-breakdown-country.png) | UC4 | ✅ Met |
| 26 | [Population of the world](screenshots/uc5-01-population-world.png) | UC5 | ✅ Met |
| 27 | [Population of a continent (Asia)](screenshots/uc5-02-population-continent.png) | UC5 | ✅ Met |
| 28 | [Population of a region (Caribbean)](screenshots/uc5-03-population-region.png) | UC5 | ✅ Met |
| 29 | [Population of a country (France)](screenshots/uc5-04-population-country.png) | UC5 | ✅ Met |
| 30 | [Population of a district (Scotland)](screenshots/uc5-05-population-district.png) | UC5 | ✅ Met |
| 31 | [Population of a city (Edinburgh)](screenshots/uc5-06-population-city.png) | UC5 | ✅ Met |
| 32 | [Speakers of each language and % of world population](screenshots/uc6-01-language-report.png) | UC6 | ✅ Met |
 
### UC1 – Country Reports
Use case: [`use-case-1-country-report.md`](use-cases/use-case-1-country-report.md)  
Columns: Code, Name, Continent, Region, Population, Capital.
 
#### 1. All countries in the world
![All countries in the world](screenshots/uc1-01-countries-world.png)
 
#### 2. All countries in a continent (Europe)
![All countries in a continent (Europe)](screenshots/uc1-02-countries-continent.png)
 
#### 3. All countries in a region (Caribbean)
![All countries in a region (Caribbean)](screenshots/uc1-03-countries-region.png)
 
#### 4. Top N countries in the world
![Top N countries in the world](screenshots/uc1-04-top-countries-world.png)
 
#### 5. Top N countries in a continent (Asia)
![Top N countries in a continent (Asia)](screenshots/uc1-05-top-countries-continent.png)
 
#### 6. Top N countries in a region (Western Europe)
![Top N countries in a region (Western Europe)](screenshots/uc1-06-top-countries-region.png)
 
### UC2 – City Reports
Use case: [`use-case-2-city-report.md`](use-cases/use-case-2-city-report.md)  
Columns: Name, Country, District, Population.
 
#### 7. All cities in the world
![All cities in the world](screenshots/uc2-01-cities-world.png)
 
#### 8. All cities in a continent (Asia)
![All cities in a continent (Asia)](screenshots/uc2-02-cities-continent.png)
 
#### 9. All cities in a region (Caribbean)
![All cities in a region (Caribbean)](screenshots/uc2-03-cities-region.png)
 
#### 10. All cities in a country (France)
![All cities in a country (France)](screenshots/uc2-04-cities-country.png)
 
#### 11. All cities in a district (Scotland)
![All cities in a district (Scotland)](screenshots/uc2-05-cities-district.png)
 
#### 12. Top N cities in the world
![Top N cities in the world](screenshots/uc2-06-top-cities-world.png)
 
#### 13. Top N cities in a continent (Asia)
![Top N cities in a continent (Asia)](screenshots/uc2-07-top-cities-continent.png)
 
#### 14. Top N cities in a region (Caribbean)
![Top N cities in a region (Caribbean)](screenshots/uc2-08-top-cities-region.png)
 
#### 15. Top N cities in a country (France)
![Top N cities in a country (France)](screenshots/uc2-09-top-cities-country.png)
 
#### 16. Top N cities in a district (Scotland)
![Top N cities in a district (Scotland)](screenshots/uc2-10-top-cities-district.png)
 
### UC3 – Capital City Reports
Use case: [`use-case-3-capital-city-report.md`](use-cases/use-case-3-capital-city-report.md)  
Columns: Name, Country, Population.
 
#### 17. All capital cities in the world
![All capital cities in the world](screenshots/uc3-01-capitals-world.png)
 
#### 18. All capital cities in a continent (Asia)
![All capital cities in a continent (Asia)](screenshots/uc3-02-capitals-continent.png)
 
#### 19. All capital cities in a region (Caribbean)
![All capital cities in a region (Caribbean)](screenshots/uc3-03-capitals-region.png)
 
#### 20. Top N capital cities in the world
![Top N capital cities in the world](screenshots/uc3-04-top-capitals-world.png)
 
#### 21. Top N capital cities in a continent (Asia)
![Top N capital cities in a continent (Asia)](screenshots/uc3-05-top-capitals-continent.png)
 
#### 22. Top N capital cities in a region (Caribbean)
![Top N capital cities in a region (Caribbean)](screenshots/uc3-06-top-capitals-region.png)
 
### UC4 – Population Breakdown Reports
Use case: [`use-case-4-population-breakdown.md`](use-cases/use-case-4-population-breakdown.md)  
Columns: Name, Total population, Population living in cities (%), Population not living in cities (%).
 
#### 23. Population in and out of cities for each continent
![Population in and out of cities for each continent](screenshots/uc4-01-breakdown-continent.png)
 
#### 24. Population in and out of cities for each region
![Population in and out of cities for each region](screenshots/uc4-02-breakdown-region.png)
 
#### 25. Population in and out of cities for each country
![Population in and out of cities for each country](screenshots/uc4-03-breakdown-country.png)
 
### UC5 – Population Lookups
Use case: [`use-case-5-population-lookup.md`](use-cases/use-case-5-population-lookup.md)
 
#### 26. Population of the world
![Population of the world](screenshots/uc5-01-population-world.png)
 
#### 27. Population of a continent (Asia)
![Population of a continent (Asia)](screenshots/uc5-02-population-continent.png)
 
#### 28. Population of a region (Caribbean)
![Population of a region (Caribbean)](screenshots/uc5-03-population-region.png)
 
#### 29. Population of a country (France)
![Population of a country (France)](screenshots/uc5-04-population-country.png)
 
#### 30. Population of a district (Scotland)
![Population of a district (Scotland)](screenshots/uc5-05-population-district.png)
 
#### 31. Population of a city (Edinburgh)
![Population of a city (Edinburgh)](screenshots/uc5-06-population-city.png)
 
### UC6 – Language Report
Use case: [`use-case-6-language-report.md`](use-cases/use-case-6-language-report.md)  
Chinese, English, Hindi, Spanish and Arabic, from most to fewest speakers, with the percentage of the world population.
 
#### 32. Speakers of each language and % of world population
![Speakers of each language and % of world population](screenshots/uc6-01-language-report.png)

## Technologies Used
- **Language:** Java 17
- **Build Tool:** Maven
- **Database:** MySQL
- **Containerization:** Docker
- **CI/CD:** GitHub Actions
- **Version Control:** GitFlow (master, develop, release, and feature branches)

## Team Members
- [Michael Stevens] 
- [Karyna Blinova] 
- [Rostyk Korolov]
- [Stefan Borisov] 
- [Callum Crozier] 

## How to Run the Project
1. Clone the repository: `git clone https://github.com/MStevens-02/devops.git`
2. Build the project using Maven: `mvn clean package`
3. Build the Docker image: `docker build -t devopsimage .`
4. Run the Docker container linked to the database.

## Reporting a bug

1. Go to **Issues → New issue** and choose the **Bug report** template.
2. Fill in the steps to reproduce, what you expected, what actually happened, and add a screenshot if you can.
3. The issue gets the `bug` label and is added to the project board.
4. The fix is made on its own branch and merged through a pull request that says `Fixes #<issue number>`, which closes the bug automatically.

Examples: [#256 City reports have no title](https://github.com/MStevens-02/devops/issues/256), [#262 printCapitalCities crashes with a null capital](https://github.com/MStevens-02/devops/issues/262).
