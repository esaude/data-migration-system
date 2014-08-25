data-migration-system
=====================

A system to migrate from SQL-based databases into OpenMRS

Overview
--------
The main architectural idea is to have a tool that takes a matching file as input, validates the matches, translates them into SQL SELECT, INSERT and/or UPDATE statements (mapping), and execute the resulting mapping.

To accomplish the underlying idea the tool must be:
-  **Reusable** - Developed once and used when needed. The mapping is created based on input logic
-  **Configurable** - The migration logic is structured in a user friendly and versatile input worksheet
-  **Auditable** - Provide feedback to the user and the tool itself
-  **General Purpose** - Can be used out of the box (evolving OpenMRS as the target database)

Development Environment Specs
------------------------------
For the development of data migration tool here are the minimum specs of the development environment:
 
-         Windows OS 7+ (currently being used by the majority)
-         Eclipse IDE (preferably Indigo, Juno, Kepler or Luna)
-         Java 7 (not recommend the Java 8 because there were some compatibility            issues when creating the environment)
-         Apache Maven 3 (http://maven.apache.org/download.cgi)
-         Git (http://git-scm.com/downloads)
 
**Optionally:**
-         EGit - plugin for Eclipse (Eclipse Marketplace)
-         GitHub Extensions -  plugin for Eclipse (Eclipse Marketplace)
-         EclEmma - plugin for Eclipse used for test coverage (Eclipse Marketplace)
-         Maven Integration for Eclipse (Eclipse Marketplace)
 
The project is *mavenized* (maven project). Some dependencies to consider:
-         JaCoCo - For test coverage control, considering that one of our criteria for the DoD (Definition of Done) is that all code should be 100% test coverage.
-         Mokito - for unit testing without necessarily exist all integrated systems, eg. databases (https://code.google.com/p/mockito/)
 
-         Jaxcel API - for XLS processing (http://www.vogella.com/tutorials/JavaExcel/article.html)
 
-         Lambaj - used for flexible manipulation of Javacollections
 
-         JUnit - For unit and integration tests

