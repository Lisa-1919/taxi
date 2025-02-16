Feature: Driver API Endpoints

  Scenario: Get existing driver by ID
    Given the driver with ID "11111111-1111-1111-1111-111111111111" exists
    When I send a GET request for a driver to "/api/v1/drivers/11111111-1111-1111-1111-111111111111?active=true"
    Then the response status code for driver should be 200
    And the response body for driver should contain "id": "11111111-1111-1111-1111-111111111111"
    And the response body for driver should contain "email"

  Scenario: Get a non-existent driver
    When I send a GET request for a driver to "/api/v1/drivers/11111111-9999-1111-1111-111111111111"
    Then the response status code for driver should be 404

  Scenario: Create a new driver
    Given I have a valid add driver payload
    When I send a POST request for a driver to "/api/v1/drivers" with the payload
    Then the response status code for driver should be 201
    And the response body for driver should contain "firstName": "new"

  Scenario: Add a driver with a bad request
    Given I have an invalid driver payload
    When I send a POST request for a driver to "/api/v1/drivers" with the payload
    Then the response status code for driver should be 400

  Scenario: Edit an existing driver
    Given the driver with ID "11111111-1111-1111-1111-111111111111" exists
    And I have a valid driver payload
    When I send a PUT request for a driver to "/api/v1/drivers/11111111-1111-1111-1111-111111111111" with the payload
    Then the response status code for driver should be 200
    And the response body for driver should contain "firstName": "John"

  Scenario: Edit a non-existent driver
    Given I have a valid driver payload
    When I send a PUT request for a driver to "/api/v1/drivers/11111111-9999-1111-1111-111111111111" with the payload
    Then the response status code for driver should be 404

  Scenario: Edit a driver with a bad request
    Given the driver with ID "11111111-1111-1111-1111-111111111111" exists
    And I have an invalid driver payload
    When I send a PUT request for a driver to "/api/v1/drivers/11111111-1111-1111-1111-111111111111" with the payload
    Then the response status code for driver should be 400

  Scenario: Delete a driver
    Given the driver with ID "11111111-2222-1111-1111-111111111111" exists
    When I send a DELETE request for a driver to "/api/v1/drivers/11111111-2222-1111-1111-111111111111"
    Then the response status code for driver should be 204

  Scenario: Delete a non-existent driver
    When I send a DELETE request for a driver to "/api/v1/drivers/11111111-9999-1111-1111-111111111111"
    Then the response status code for driver should be 404