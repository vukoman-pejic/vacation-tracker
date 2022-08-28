# vacation-tracker
Vacation tracker - technical assigment

This project is about uploading and searching vacation days in one firm. The project was coded in Java with Spring Boot framework.

For this project to work properly, you must create a database in PosgreSQL named userDB. Application.properties file is set so that it connects to this database. In resources/samples you have samples of csv files that are needed to fill up the given database. All functions have comments that are explaining what are they doing.

In a project there is package named loader. It contains a class named employeeLoader and in that class you will see a function named loadEmployees. When you first start a project uncomment the annotation above it (@PostConstruct). It will fill up your database with given employees. Every next time you are running this project, comment the given annotation.

Then in order to test the project to be working properly, open Postman, and in a project you have a file named postman_collection. In that file there are collections that will help you test this project if it is working properly.

Tests for this project are not written but you are very welcome to write them.

That would be all.
