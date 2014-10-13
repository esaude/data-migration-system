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

### Mac OS X Setup

You may need to make one or more of the following changes to get the system up and running on OS X.

#### 1. Configure Java version

The code in this repository requires Java 7 to compile, so if you're running multiple versions of Java, you'll need to use something like [jenv](http://www.jenv.be/) to ensure that Maven uses the correct Java installation to compile the code.

#### 2. Configure file encoding

The files in this repository use the `ISO-8859-1` file encoding. This could cause errors that look like:

````
[ERROR] Codable.java:[5,14] error: unmappable character for encoding UTF8
`````

If you see such errors, you may need to tell Maven to use the correct encoding by adding `<encoding>ISO-8859-1</encoding>` to the compiler plugin configuration in `pom.xml` as follows:

````
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <source>1.7</source>
        <target>1.7</target>
        <encoding>ISO-8859-1</encoding>
    </configuration>
</plugin>
````

#### 3. Correct Windows-specific file locations

In `src/main/resources/config.xml`, there are references to `C:/EMR_Architecture/OpenMRS_SESP_Matching_Table_210820141022.xls`. You'll need to change these to valid locations on your local filesystem in order to run the tests.

