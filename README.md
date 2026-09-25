# Team 5 - Population Reporting System

![workflow](https://github.com/MStevens-02/devops/actions/workflows/build.yml/badge.svg)
[![LICENSE](https://img.shields.io/github/license/MStevens-02/devops.svg?style=flat-square)](https://github.com/MStevens-02/devops/blob/master/LICENSE)
[![Releases](https://img.shields.io/github/release/MStevens-02/devops/all.svg?style=flat-square)](https://github.com/MStevens-02/devops/releases)
![GitHub commit activity](https://img.shields.io/github/commit-activity/m/MStevens-02/devops)

## Project Overview
This project is part of the **SET08103 Software Engineering Methods** module at Edinburgh Napier University. It is a population reporting system that produces demographic reports based on world population data.

The application is built using Java and Maven, utilizes a MySQL database for data retrieval, and runs inside Docker containers. We are employing a Continuous Integration (CI) pipeline via GitHub Actions to automate the build and testing processes.

## Features
- Generates reports on global, continental, and regional populations.
- Identifies the top populated countries and cities.
- Provides urban vs. non-urban population percentages.
- Tracks specific language usage statistics worldwide.

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
