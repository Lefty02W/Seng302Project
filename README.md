# SENG302 Project Team 700
This is Travel EA, a website for storing and organising destinations and trips.
It can be packaged to a deliverable form using the `sbt dist` command.

### Import Instructions
With the repository cloned, it can be imported using the following steps.

1. Open IntelliJ and choose Import project.

2. Find the "Team 700" folder and click OK.

3. Choose Import project from external model, select sbt from the list and click next.
   a. Note: If sbt is not available, add the Scala plugin from the Welcome --> Configure --> Plugins page.

4. Project options:
    a. For Download options, untick "Library sources" and tick "sbt sources" and "Use sbt shell for build and import".
    b. Choose 1.8 for ProjectJDK.
    c. Finish. 

### Run the project

1. Once the project is imported, open sbt shell in IntelliJ and enter "run"
2. An instance of the application is now hosted locally at the address: <localhost:9000>


### Run all tests in the project
1. Once the project is imported, open sbt shell in IntelliJ and enter "test"
2. All tests, including both Cucumber and JUnit tests, will be run and the output shown.


### Admin & User Credentials

Admin:

* Username: test.admin@gmail.com

* Password: testAdmin
 
User:

* Username: test.user@gmail.com

* Password: testUser


### Connect to a Dedicated server instance

We have two instances of the project running on services in our virtual machine.

#### Production (port 443):

Hosts tagged commits only. Uses the prod database `seng302-2019-team700-prod`

<http://csse-s302g7.canterbury.ac.nz:443/>

#### Development (port 8443):

Hosts all commits. Uses the test database `seng302-2019-team700-test`

<http://csse-s302g7.canterbury.ac.nz:8443/>

### Dependencies
The dependencies for our project can be found via the `build.sbt` file. These include:
 * Mockito
 * Cucmber tests
 * Selenium
 * MySQL Java Connector
 
 Outside of these, we have approved business cases for the following:
 * Bootstrap version 4.2.1, bootstrap-select, bootstrap datatables

### Contributors
Team 700 consists of:
* Ambrose Ledbrook
* Luke Walsh
* Harry Feasey
* Chuan Law
* Liam Gray
* George Khella
* Sam Verdellen
* Jade Martin 

### Liscencing
By using this repository you accept that the code and all resources pertaining to the Team 700 project
are subject to the Creative-Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0) license.
This means you are free to:
 *  Share — copy and redistribute the material in any medium or format
 *  Adapt — remix, transform, and build upon the material 
 
 Under the following terms:
* Attribution — You must give appropriate credit, provide a link to the license, and indicate if changes were made. You may do so in any reasonable manner, but not in any way that suggests the licensor endorses you or your use.

* NonCommercial — You may not use the material for commercial purposes. 

A full version of the license is available here: <https://creativecommons.org/licenses/by-nc/4.0/legalcode>


### References
* [Play documentation](https://playframework.com/documentation/latest/
* [EBean](https://www.playframework.com/documentation/latest/JavaEbean) is a Java ORM library that uses SQL.The documentation can be found [here](https://ebean-orm.github.io/).
* For Java Forms, Please see [here](<https://playframework.com/documentation/latest/JavaForms>).
* Our [Creative-Commons License](https://creativecommons.org/licenses/by-nc/4.0/legalcode)




