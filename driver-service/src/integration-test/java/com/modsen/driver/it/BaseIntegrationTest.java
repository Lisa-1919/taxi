package com.modsen.driver.it;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@ActiveProfiles("integration-test")
public class BaseIntegrationTest {
    static final PostgreSQLContainer postgreSQLContainer;
    static final RedisContainer redisContainer;

    static {
        postgreSQLContainer = (PostgreSQLContainer) new PostgreSQLContainer("postgres:15")
                .withDatabaseName("driver-test-db")
                .withUsername("postgres")
                .withPassword("WC4ty37xd3")
                .withReuse(true);
        postgreSQLContainer.setCommand("postgres", "-c", "max_connections=20000");
        postgreSQLContainer.start();

        redisContainer = new RedisContainer(DockerImageName.parse("redis:6.2.6"));
        redisContainer.start();
    }

    @DynamicPropertySource
    public static void configureTestDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);

        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", redisContainer::getRedisPort);
    }
}
