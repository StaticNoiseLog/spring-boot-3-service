package com.staticnoiselog.springboot3service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Delegates to production code, but provides things for using containers at development time.
 */
@Configuration
public class TestServiceApplication {
    @Bean
    @ServiceConnection // "you can get the credentials for a connection to a DB from this bean"
    @RestartScope // this container shall endure beyond restarts
    public PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>("postgres:15.1-alpine");
    }

    public static void main(String[] args) {
        SpringApplication
                .from(SpringBoot3ServiceApplication::main)
                .with(TestServiceApplication.class) // this very file is used as the @Configuration
                .run(args);
    }
}
