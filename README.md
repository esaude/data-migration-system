data-migration-system
=====================

A system to migrate from SQL-based databases into OpenMRS.

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
For the development of the data migration tool, here are the minimum specs required for the development environment:
 
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

Test and Production Environments Specs
------------------------------

In order to execute the system in test environment make sure you have the following tools installed:

-         Java 7 (http://www.oracle.com/technetwork/java/javase/downloads/jre7-downloads-1880261.html) - **Make sure you download a 32 bit JDK (Windows X86)**
-         Apache Maven 3 (http://maven.apache.org/download.cgi)
-         Git (http://git-scm.com/downloads)

Perform the following steps using command line:

1. Clone the project repository from github `git clone https://github.com/esaude/data-migration-system.git`

2. Navigate to your project root directory and run the command `mvn package`. This step will generate the distribution of the tool `data-migration-system-[VERSION]-dist.zip` in zip format. The generated file will be located in `PROJECT_ROOT\target`

3. Extract the distribution file to a suitable location

4. Configure the input and runtime properties in `...\data-migration-system-[VERSION]\src\main\resources\config.xml`

5. Open command line

6. Change your PATH to use Java 7 32 bit: `path=%PATH%;YOUR_JAVA32_INSTALATION_LOCATION\bin` or set the classpath in Environment Variables;

7. Make sure you are now using Java 7 32 bit: `java -version` should display the details of your java in the classpath

8. Execute the data cleaning tool `esaude-dct-cleaner.jar` for SESP located in `...\data-migration-system-[VERSION]\src\main\resources` - use the command `java -jar esaude-dct-cleaner.jar` and provide the absolute path of your SESP database when requested, e.g. `C:/databases/Bkup_CS Namige_2014-09-22 11.28.mdb` and press `enter` key

9. Execute the jar file `PROJECT_ROOT\data-migration-system-1.0-SNAPSHOT.jar` located in `PROJECT_ROOT\data-migration-system-[VERSION]`. Run the command `java -jar data-migration-system-[VERSION].jar`

