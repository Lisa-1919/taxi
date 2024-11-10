package com.modsen.driver.e2e;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("testcases/car.feature")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME,value = "com.modsen.driver.step")
@ConfigurationParameter(key = Constants.EXECUTION_DRY_RUN_PROPERTY_NAME,value = "false")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME,value = "pretty, html:target/cucumber-report/cucumber.html")
@ActiveProfiles("e2e")
public class CarTestRunner {
}
