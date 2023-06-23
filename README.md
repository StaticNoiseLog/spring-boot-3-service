Project
=======

Origin
------
Based on the presentation [Bootiful Spring Boot 3](https://www.devoxx.co.uk/talk/?id=11335) given by Josh Long in
2023-06.

This is [Josh's original GitHub repository](https://github.com/joshlong/bootiful-spring-boot-3).

Purpose
-------
Illustrate features of Spring 3 with documentation.


spring initializr
=================

Settings
--------

- Project: Gradle-Kotlin
- Language: Java
- Spring Boot: 3.1.0
- Group: com.staticnoiselog
- Artifact: spring-boot-3-service [name of ZIP archive to download, name of main directory]
- Name: Spring Boot 3 Service
- Description: Demo Project Spring Boot 3
- Package name: com.staticnoiselog.spring-boot-3-service [suggested, fixed automatically]  
  *The original package name 'com.staticnoiselog.spring-boot-3-service' is invalid and this project uses '
  com.staticnoiselog.springboot3service' instead.*
- Packaging: Jar
- Java: 17 [Spring 3 requires Java 17 or higher and is based on Spring 6]

Dependencies
------------

- GraalVM Native Support [Developer Tools]  
  Support for compiling Spring applications to native executables using the GraalVM native-image compiler.
- PostgreSQL Driver [SQL]  
  A JDBC and R2DBC driver that allows Java programs to connect to a PostgreSQL database using standard, database
  independent Java code.
- Spring Data JDBC [SQL]  
  Persist data in SQL stores with plain JDBC using Spring Data.
- Spring Web [Web]  
  Build web, including RESTful, applications using Spring MVC. Uses Apache Tomcat as the default embedded container.
- Spring Boot DevTools [Developer Tools]  
  Provides fast application restarts, LiveReload, and configurations for enhanced development experience.
- Testcontainers [Testing]  
  Provide lightweight, throwaway instances of common databases, Selenium web browsers, or anything else that can run in
  a Docker container.
- Spring Boot Actuator [Ops]  
  Supports built in (or custom) endpoints that let you monitor and manage your application - such as application health,
  metrics, sessions, etc.

Efficiency and Scalability
==========================

Java is already very efficient compared with other
languages: <https://thenewstack.io/which-programming-languages-use-the-least-electricity/>

GraalVM
-------
GraalVM allows you to turn a Java program into native code if you supply (a lot of) configuration. Spring Boot 3 helps
to create this configuration which needs to be fed into the GraalVM compiler.

For this to work you have to install GraalVM.

    ./gradlew nativeCompile        # Compiles a native image for the main binary
    ./gradlew nativeTestCompile    # Compiles a native image for the test binary


Database
========

Postgres Docker Image
---------------------
This app expects a Postgres DB running on port 5432 with an empty database called `customerdb`. You can provide it with
Docker:

    docker pull postgres
    docker run --name postgres-container -e POSTGRES_DB=customerdb -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -d postgres

JDBC URL, database user and password are configured
in [application.properties](src/main/resources/application.properties).

The DB driver has to be on the path, but there is no need to configure it (something like
`spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver`), Spring Boot will figure it out.

Database Tables
----------------
The database tables are defined in [schema.sql](src/main/resources/schema.sql) and filled with data from
[data.sql](src/main/resources/data.sql).

In order for the application to initialize a DB, we have to set `spring.sql.init.mode`
in [application.properties](src/main/resources/application.properties).
Note that setting this property to `always` means that schema.sql and data.sql are executed each time the application is
started.


Execution
=========

Running the App in IntelliJ
---------------------------

- Right-click on SpringBoot3ServiceApplication
- Run ... or Debug ...

Querying Application Data
-------------------------
<http://localhost:8080/customers>

<http://localhost:8080/customers/Olga> [byName]

Actuator
--------
<http://localhost:8080/actuator>

Containerization
-----------------
You don't write a Dockerfile yourself. Instead you use **Cloud Native Buildpacks**. You simply use a Gradle or Maven
task to build an OCI (Open Container Initiative) image of the application:

    ./gradlew bootBuildImage # Builds an OCI image of the application using the output of the bootJar task

    ./mvn spring-boot:build-image

Testing
=======

Spring Boot 3 Supports Testcontainers and Docker Compose
--------------------------------------------------------
Demonstrated
in [TestServiceApplication](src/test/java/com/staticnoiselog/springboot3service/TestServiceApplication.java).


Observability
=============

Done with the `Observation` API. You need the `ObservationRegistry` and then you can do something like this:

        return Observation
                .createNotStarted("by-name", this.registry)
                .observe(() -> repository.findByName(name));

You can query this metrics data using the designation you gave it ("
by-name"): <http://localhost:8080/actuator/metrics/by-name>

If you enable *distributed tracing* you could call another service without losing track.

Before Spring Boot 3, tracing was done with Sleuth, which is part of Spring Cloud.
With Spring Boot 3 micrometer now does both, **metrics** (as before) and **tracing**.

Therefore, the stack is now more logical with tracing at the bottom of the dependencies instead of at the top:

- Spring Cloud (no more need for Sleuth)
- Spring Boot 3
- Spring 6
- Micrometer (metrics and tracing)

Jakarta
=======

Note that with Spring Boot 3 and Spring 6 the switch to `jakarta` package has been made.