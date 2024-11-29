package com.modsen.driver.it;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("integration-test")
public class BaseIntegrationTest {
    static final PostgreSQLContainer postgreSQLContainer;

    static {
        postgreSQLContainer = (PostgreSQLContainer) new PostgreSQLContainer("postgres:15")
                .withDatabaseName("driver-test-db")
                .withUsername("postgres")
                .withPassword("WC4ty37xd3")
                .withReuse(true);
        postgreSQLContainer.setCommand("postgres", "-c", "max_connections=20000");
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    public static void configureTestDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
