Project
=======

Origin
------
Based on the presentation [Bootiful Spring Boot 3](https://www.devoxx.co.uk/talk/?id=11335) given by Josh Long in
2023-06.

This is [Josh's original GitHub repository](https://github.com/joshlong/bootiful-spring-boot-3).

Purpose
-------
Illustrate some features available with Spring 3.


spring initializr
=================

Settings
--------

- Project: Gradle-Kotlin
- Language: Java
- Spring Boot: 3.x
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
- GraalVM Native Support [Developer Tools]  
  Support for compiling Spring applications to native executables using the GraalVM native-image compiler.

Efficiency and Scalability
==========================

Java is already very efficient compared with other
languages: <https://thenewstack.io/which-programming-languages-use-the-least-electricity/>

Native Code With GraalVM
------------------------
GraalVM allows you to turn a Java program into native code if you supply (a lot of) configuration. Spring Boot 3 helps
to create this configuration which needs to be fed into the GraalVM compiler.

For this to work you have to install GraalVM.

    ./gradlew nativeCompile        # Compiles a native image for the main binary
    ./gradlew nativeTestCompile    # Compiles a native image for the test binary

Database
========

PostgreSQL Docker Image
-----------------------
This app expects a running PostgreSQL database. For testing, a convenient way to provide the DB is with a
[Testcontainers configuration](src/test/java/com/staticnoiselog/springboot3service/TestcontainersConfiguration.java)
(see below, under "Testing").

Of course, you can also provide the PostgreSQL database in the traditional way, without Testcontainers. In this case the
connection details (JDBC URL, database name, user, password)
from [application.properties](src/main/resources/application.properties) are used as usual.

Here are the commands to run PostgreSQL as a Docker image for this application:

    docker pull postgres
    docker run --name postgres-container-manual -e POSTGRES_DB=customerdb -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -d postgres

Once the container exists you can start it like this:

    docker start postgres-container-manual

The DB driver has to be on the path, but there is no need to configure it (something like
`spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver`), Spring Boot will figure it out.

Database Tables
----------------
The database tables are defined in [schema.sql](src/main/resources/schema.sql) and filled with data from
[data.sql](src/main/resources/data.sql).

In order for the application to initialize a DB, we have to set `spring.sql.init.mode`
in [application.properties](src/main/resources/application.properties). Note that setting this property to `always`
means that schema.sql and data.sql are executed each time the application is started. There is no magic involved here.
The two SQL scripts are simply executed, and the outcome is directly determined by the statements you provide within
them.


Execution
=========

Running the App in IntelliJ
---------------------------

- Start PostgreSQL
- Right-click on [DemoApplication](src/main/java/com/staticnoiselog/springboot3service/DemoApplication.java)
- Run ... or Debug ...

Alternatively, you can run the application with Testcontainers which will provide a PostgreSQL test container
automatically:

- Right-click
  on [DemoApplicationRunnerWithTestcontainers](src/test/java/com/staticnoiselog/springboot3service/runner/DemoApplicationRunnerWithTestcontainers.java)
- Run ... or Debug ...

Querying Application Data
-------------------------
<http://localhost:8080/customers>

<http://localhost:8080/customers/Olga> [byName]

Actuator
--------
<http://localhost:8080/actuator>

Containerization
----------------
With Spring Boot 3, you don't have to write a Dockerfile yourself. Instead, you can use **Cloud Native Buildpacks**. You
simply use a Gradle or Maven task to build an OCI (Open Container Initiative) image of the application:

    ./gradlew bootBuildImage # Builds an OCI image of the application using the output of the bootJar task

You should be aware that not having a Dockerfile in your project has disadvantages,
too. [This article](https://azureossd.github.io/2023/07/31/Using-pack-cli-and-buildpacks-to-deploy-Dockerfile-less-apps-to-Web-App-for-Containers/)
mentions pros and cons.

In practice, I have encountered problems with `bootBuildImage` (a download kept failing) that could only be resolved by
specifying Paketo Buildpacks versions explicitly. You can see how this is done in [build.gradle.kts](build.gradle.kts)
(look for `buildpacks.set`). Build reproducibility is another reason why you may want to do this.
See [Improving the Reproducibility of Spring Boot’s Docker Image Builder](https://candrews.integralblue.com/2022/10/improving-the-reproducibility-of-spring-boots-docker-image-builder/).

Bottom line: While Cloud Native Buildpacks look very appealing at first glance, they do not come without a burden. You
should carefully weigh the pros and cons before giving up on having your own Dockerfile in a production project.


Testing
=======

Testcontainers
--------------
[Testcontainers](https://java.testcontainers.org/) for Java is a library that supports JUnit tests, providing
lightweight, throwaway instances of common databases, Selenium web browsers, or anything else that can run in a Docker
container.

Spring Boot 3
[supports Testcontainers](https://spring.io/blog/2023/06/23/improved-testcontainers-support-in-spring-boot-3-1) and
Docker Compose.

[DemoApplicationWithTestcontainersTests](src/test/java/com/staticnoiselog/springboot3service/DemoApplicationWithTestcontainersTests.java)
shows how to
use [the TestcontainersConfiguration test configuration](src/test/java/com/staticnoiselog/springboot3service/TestcontainersConfiguration.java)
for Spring tests that need a PostgreSQL database.

### Using Testcontainers at Development Time

While Testcontainers are primarily designed for testing purposes, they can also serve as a convenient solution to
provide external services on a developer's machine during development. This can replace the need for separate
installations or manual initiation of Docker images.

Instead of launching [DemoApplication](src/main/java/com/staticnoiselog/springboot3service/DemoApplication.java)
directly as usual, you can start this application by running the `main` method
of [DemoApplicationRunnerWithTestcontainers](src/test/java/com/staticnoiselog/springboot3service/runner/DemoApplicationRunnerWithTestcontainers.java),
and you will automatically get a running PostgreSQL database as configured in
[this Testcontainers configuration](src/test/java/com/staticnoiselog/springboot3service/TestcontainersConfiguration.java).

### How Spring Boot Connects to Testcontainers

Spring Boot 3.1 introduces a
new [ConnectionDetails](https://spring.io/blog/2023/06/19/spring-boot-31-connectiondetails-abstraction) interface that
represents a connection to a remote service. It is extended by multiple other interfaces which model the connection to a
concrete remote service, e.g. `JdbcConnectionDetails` for connections to a database server through JDBC.

Test scenarios with Testcontainers use `ConnectionDetails` to access the Docker containers. See `@ServiceConnection`
in [TestcontainersConfiguration](src/test/java/com/staticnoiselog/springboot3service/TestcontainersConfiguration.java).

For JDBC, `JdbcConnectionDetails` (`extends ConnectionDetails`) would be used in place of the
properties `spring.datasource.url`, `spring.datasource.username` and `spring.datasource.password`. Using the additional
abstraction is more robust because the strings for these properties might change in the future.

The auto-configurations in Spring Boot have been changed to use the `ConnectionDetails` beans if they are available. In
such cases they will take precedence over the application configuration properties. If there is no
such `ConnectionDetails` bean, then the properties will be used.

This is why the `spring.datasource.*` properties in `application.properties` are ignored when you use Testcontainers to
provide a database. Credentials are generated and passed on to the application as `JdbcConnectionDetails`, overriding
any values defined in `application.properties`.

The `ConnectionDetails` abstraction was primarily introduced to support the new Docker Compose and Testcontainers
features. Sometime in the future, other interesting integrations might be built on top of it. For example, a Spring Boot
application running in [VMware Tanzu cloud](https://tanzu.vmware.com/tanzu) could discover the database which is
associated with the application and automatically provide a `JdbcConnectionDetails` (or `R2dbcConnectionDetails` for
reactive applications) bean which knows how to connect to that database. But until popular cloud platforms support this
mechanism, it does not make sense to use it outside of test and development scenarios. Therefore,
while `JdbcConnectionDetails` could be used to configure the database connection of this service, it still relies on the
traditional `spring.datasource.*` configuration values in `application.properties`.

### Accessing the Testcontainers PostgreSQL Instance

As described above, the connection details for services provided by Testcontainers are automatically communicated to
Spring Boot and you do not even get to see them. But if you want to use some kind of database client tool, you need the
following.

The Docker container name of the PostgreSQL instance started by Testcontainers is random, but can be easily found with
`docker ps` ("boring_gould" in this example). The database name, the username and the password all are "test". The port
can be found with this command:

    docker inspect boring_gould | grep -i HostPort

Observability
=============

Done with the `Observation` API. You need the `ObservationRegistry` and then you can do something like this:

        return Observation
                .createNotStarted("by-name", this.registry)
                .observe(() -> repository.findByName(name));

You can query this metrics data using the designation you gave it
("by-name" in this example): <http://localhost:8080/actuator/metrics/by-name>

Note that you must have visited the observed URL (<http://localhost:8080/customers/Olga>) at least once in order for the
metrics endpoint to show any data.

If you enable *distributed tracing* you could call another service without losing track.

Before Spring Boot 3, tracing was done with Sleuth, which is part of Spring Cloud. With Spring Boot 3, micrometer now
does both, **metrics** (as before) *and* **tracing**.

Therefore, the stack is now more logical with tracing at the bottom of the dependencies instead of at the top:

- Spring Cloud (no more need for Sleuth)
- Spring Boot 3
- Spring 6
- Micrometer (metrics and tracing)

Jakarta
=======

Note that with Spring Boot 3 and Spring 6 the switch to the `jakarta` package has been made.


Documentation
=============

REST API Documentation
----------------------
This approach to REST API documentation was not included in Josh's presentation, but I found it workable and really
convenient from the viewpoint of a developer.

With the three dependencies `springdoc-openapi-starter-webmvc-ui`, `therapi-runtime-javadoc`
and `therapi-runtime-javadoc-scribe`, REST API documentation is generated automatically from Javadoc and can be accessed
at http://localhost:8080/swagger-ui/index.html and http://localhost:8080/v3/api-docs.