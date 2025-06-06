# TSN: Customizable Social Network

## 1. Project Overview

VibeLink is a simulated social platform where users can share content related to their interests, such as reading, gaming, cooking, hiking, and photography. The platform supports social interactions like following and liking between users. This project uses the Neo4j graph database to store and manage data, utilizing Cypher queries for data creation, retrieval, and relationship establishment.

## 2. Project Directory Structure and Introduction

The project has a clean and simple directory structure. Below is a brief description of the main components:

```plaintext
social-recommend/
├── src                      		 # Source code directory
│   └── java                  		 # Main Java code for the application
│   └── resources                	 # Configuration resources
│    	├── static			 # Static assets (e.g., CSS, images)
│	├── templates			 # Front-end pages (e.g., HTML)
│    	└── application.properties 	 # Key configuration file
├── .gitignore                           # Git ignore file configuration
├── LICENSE                              # License file, under MIT license
├── README.md                            # Project documentation
└── pom.xml                              # Maven build configuration file
```

* `src/`: Contains the system's core business logic and controller classes.
* `resources/`: Configuration and template resources, especially `application.properties` which sets runtime parameters.
* `pom.xml` is the Maven configuration file that defines dependencies and build settings.
* `LICENSE`: Project license (MIT).
* `README.md`: Provides an overview of the system, setup instructions, and configuration notes.

## 3. Project Configuration File

**Key configurations are located in `resources/application.properties`. These include but are not limited to:**

```properties
# Neo4j database connection settings
spring.neo4j.uri = bolt://localhost:7687
spring.neo4j.authentication.username = neo4j
spring.neo4j.authentication.password = admin2025

# File upload path
file.upload-dir = upload/
file.staticAccessPath = /upload/**
```

⚠️ Please modify the above settings according to your actual environment to ensure the correct database connection and file path.

## 4. Instructions for Use

### 1. Environment Requirements

* Install Neo4j graph database
* Install Java 17 or higher
* Install Maven 3.6 or higher
* Install Spring Boot 2.7 or higher
* It is recommended to use an IDE (e.g., IntelliJ IDEA) for development and debugging

### 2. Operation Steps

1. Start the local Neo4j database and ensure the connection settings are correct.
2. Run the Spring Boot project (via IDE or with `mvn spring-boot:run`).

## 5. Notes

* ⚠️ Be sure to start the Neo4j database before starting the backend service, otherwise connection errors will occur.
* If you encounter issues with permissions, paths, or database connection, please first check whether `application.properties` is correctly configured.
