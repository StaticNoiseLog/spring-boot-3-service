package com.staticnoiselog.springboot3service;

import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Test configuration for a Spring Boot application using <a href="https://java.testcontainers.org/">Testcontainers</a>
 * (a Java library that provides lightweight, disposable Docker containers for testing).
 * <p>
 * Uses the PostgreSQLContainer class from Testcontainers to create an instance of a PostgreSQL container with the
 * latest version and provides a connection to it.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {
    // The @TestConfiguration annotation indicates that beans from this class should only be used by tests.
    @Bean
    // `@ServiceConnection` discovers the type of container that is annotated and creates a ConnectionDetails bean for
    // it (JdbcConnectionDetails in this case).
    @ServiceConnection
    // The @RestartScope annotation is from spring-boot-devtools. During development, you can use Spring Boot DevTools
    // to reload the code changes without having to completely restart the application. With this annotation, when
    // devtools reloads your application, the same container will be reused instead of re-creating it. Note that if you
    // stop the application, the container is always gone.
    @RestartScope
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:latest");
    }
}