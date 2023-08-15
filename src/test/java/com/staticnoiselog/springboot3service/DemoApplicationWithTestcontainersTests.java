package com.staticnoiselog.springboot3service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * These tests use the TestcontainersConfiguration, which is a convenient way to provide a running PostgreSQL database
 * automatically for testing.
 */
@SpringBootTest
@Testcontainers
@ContextConfiguration(classes = TestcontainersConfiguration.class)
public class DemoApplicationWithTestcontainersTests {
    public static final String TEST_USER = "TestUser";

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void testReadFirstCustomer() {
        // Create a sample customer entity
        Customer customer = new Customer(1, TEST_USER);
        customerRepository.save(customer);

        // Read the first customer by name from the repository
        Iterable<Customer> customers = customerRepository.findByName(TEST_USER);
        Customer firstCustomer = customers.iterator().next();

        // Assert that the first customer is not null and has the expected values
        assertNotNull(firstCustomer);
        assertEquals(customer.id(), firstCustomer.id());
        assertEquals(customer.name(), firstCustomer.name());
    }
}