package com.staticnoiselog.springboot3service.runner;

import com.staticnoiselog.springboot3service.DemoApplication;
import com.staticnoiselog.springboot3service.TestcontainersConfiguration;
import org.springframework.boot.SpringApplication;

/**
 * Demonstrates how to use Testcontainers for running the actual application during development. This may be more
 * convenient than starting required external services manually (PostgreSQL in this case).
 * <p>
 * This code is under the test directory so that all "org.testcontainers" libraries can remain in the
 * <code>testImplementation</code> dependency configuration.
 * <p>
 * Note that the <code>main</code> method could be placed in any test file (Josh puts it directly in the
 * TestcontainersConfiguration). But for illustration, it is clearer to maintain it in a distinct class with a name that
 * describes its purpose.
 */
public class DemoApplicationRunnerWithTestcontainers {
    public static void main(String[] args) {
        SpringApplication
                .from(DemoApplication::main)
                .with(TestcontainersConfiguration.class) // using the test configuration to run our application
                .run(args);
    }
}
