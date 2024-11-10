package com.modsen.eureka;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

<<<<<<< HEAD:eureka/src/test/java/com/modsen/eureka/EurekaApplicationTests.java
@SpringBootTest
class EurekaApplicationTests {
=======
@SpringBootTest(classes = DriverServiceApplication.class)
@ActiveProfiles("test")
class DriverServiceApplicationTests {
>>>>>>> b1c7fa3 (feat: add integration tests and configure test separation):driver-service/src/test/java/com/modsen/driver/DriverServiceApplicationTests.java

//	@Test
//	void contextLoads() {
//	}

}
