Feature: Driver API Endpoints

  Scenario: Get existing driver by ID
    Given I authenticate as "test-driver@gmail.com" with password "test-password"
    Given the driver with ID "4ef35903-b7dc-4865-a893-d115c4a303e5" exists
    When I send a GET request for a driver to "/api/v1/drivers/4ef35903-b7dc-4865-a893-d115c4a303e5?active=true"
    Then the response status code for driver should be 200
    And the response body for driver should contain "id": "4ef35903-b7dc-4865-a893-d115c4a303e5"
    And the response body for driver should contain "email"

  Scenario: Get a non-existent driver
    Given I authenticate as "test-driver@gmail.com" with password "test-password"
    When I send a GET request for a driver to "/api/v1/drivers/11111111-9999-1111-1111-111111111111"
    Then the response status code for driver should be 404

  Scenario: Create a new driver
    Given I have a valid add driver payload
    When I send a POST request for a driver to "/api/v1/account/register" with the payload
    Then the response status code for driver should be 201
    And the response body for driver should contain "firstName": "new"

  Scenario: Add a driver with a bad request
    Given I have an invalid driver payload
    When I send a POST request for a driver to "/api/v1/account/register" with the payload
    Then the response status code for driver should be 400

  Scenario: Edit an existing driver
    Given I authenticate as "test-edit-driver@gmail.com" with password "test-password"
    Given the driver with ID "01394741-947d-42b5-97aa-61536c934a3f" exists
    And I have a valid driver payload
    When I send a PUT request for a driver to "/api/v1/account/01394741-947d-42b5-97aa-61536c934a3f" with the payload
    Then the response status code for driver should be 200
    And the response body for driver should contain "firstName": "John"

  Scenario: Edit a non-existent driver
    Given I authenticate as "test-driver@gmail.com" with password "test-password"
    Given I have a valid driver payload
    When I send a PUT request for a driver to "/api/v1/account/11111111-9999-1111-1111-111111111111" with the payload
    Then the response status code for driver should be 403

  Scenario: Edit a driver with a bad request
    Given I authenticate as "test-driver@gmail.com" with password "test-password"
    Given the driver with ID "4ef35903-b7dc-4865-a893-d115c4a303e5" exists
    And I have an invalid driver payload
    When I send a PUT request for a driver to "/api/v1/account/4ef35903-b7dc-4865-a893-d115c4a303e5" with the payload
    Then the response status code for driver should be 400

  Scenario: Delete a driver
    Given I authenticate as "test-delete-driver@test.com" with password "test-password"
    Given the driver with ID "e1aac6dd-8630-48f0-bf48-3e26d13525c0" exists
    When I send a DELETE request for a driver to "/api/v1/account/e1aac6dd-8630-48f0-bf48-3e26d13525c0"
    Then the response status code for driver should be 204

  Scenario: Delete a non-existent driver
    Given I authenticate as "test-driver@gmail.com" with password "test-password"
    When I send a DELETE request for a driver to "/api/v1/account/11111111-9999-1111-1111-111111111111"
    Then the response status code for driver should be 403