package com.staticnoiselog.springboot3service;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
public class DemoApplication {
    private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner(CustomerRepository repository) {
        return args -> repository.findAll().forEach(customer -> logger.info(customer.toString())); // print out content of DB on startup
    }
}

@Controller // just an HTTP controller, not a RestController
@ResponseBody
class CustomerHttpController {
    private final CustomerRepository repository;
    private final ObservationRegistry registry;

    CustomerHttpController(CustomerRepository repository, ObservationRegistry registry) {
        this.repository = repository;
        this.registry = registry;
    }

    /**
     * Read customer by name. The name must start with a capital letter.
     *
     * @param name String
     * @return {@literal Iterable<Customer>}
     */
    @GetMapping("/customers/{name}")
    Iterable<Customer> byName(@PathVariable String name) {
        Assert.state(Character.isUpperCase(name.charAt(0)), "the name must start with an uppercase letter");
        return Observation
                .createNotStarted("by-name", this.registry)
                .observe(() -> repository.findByName(name));
    }

    /**
     * Read all customers.
     *
     * @return {@literal Iterable<Customer>}
     */
    @GetMapping("/customers")
    Iterable<Customer> customers() {
        return this.repository.findAll();
    }
}

/**
 * Handles all IllegalStateException objects thrown by the app. Assures a consistent representation of errors by using
 * {@link ProblemDetail}, which is an implementation of RFC 7807. This must be enabled in <code>application.properties</code>
 * with the attribute <code>spring.mvc.problemdetails.enabled=true</code>.
 */
@ControllerAdvice
class ErrorHandlingControllerAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingControllerAdvice.class);

    @ExceptionHandler
    ProblemDetail handle(IllegalStateException ise, HttpServletRequest request) {
        request.getHeaderNames().asIterator().forEachRemaining(logger::info); // you have access to the HttpServletRequest
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST.value());
        pd.setDetail(ise.getMessage());
        return pd;
    }
}

/**
 * Repository for Customer objects that have an Integer as the primary key.
 */
interface CustomerRepository extends CrudRepository<Customer, Integer> {
    Iterable<Customer> findByName(String name);
}

// look ma, no Lombok!
record Customer(@Id Integer id, String name) {
}