Feature: Passenger API Endpoints

  Scenario: Get existing passenger by ID
    Given I authenticate as "test-passenger@gmail.com" with password "test-password"
    Given the passenger with ID "dc6ea845-03a1-4c74-9fbf-4bf6e4198578" exists
    When I send a GET request to "/api/v1/passengers/dc6ea845-03a1-4c74-9fbf-4bf6e4198578?active=true"
    Then the response status code should be 200

  Scenario: Get all passengers
    Given I authenticate as "test-passenger@gmail.com" with password "test-password"
    When I send a GET request to "/api/v1/passengers?page=0&limit=10"
    Then the response status code should be 200
    And the response body should contain "totalElements"

  Scenario: Add a new passenger
    Given I have a valid passenger payload
    When I send a POST request to "/api/v1/account/register" with the payload
    Then the response status code should be 201
    And the response body should contain "firstName": "John"

  Scenario: Edit an existing passenger
    Given I authenticate as "test-edit-passenger@gmail.com" with password "test-password"
    Given the passenger with ID "ed20a11d-9717-4ac6-b14a-119a2c7b9634" exists
    And I have an updated passenger payload
    When I send a PUT request to "/api/v1/account/ed20a11d-9717-4ac6-b14a-119a2c7b9634" with the payload
    Then the response status code should be 200
    And the response body should contain "firstName": "Jane"

  Scenario: Delete a passenger
    Given I authenticate as "test-delete-passenger@test.com" with password "test-password"
    Given the passenger with ID "48d48295-cf63-4e56-8088-7faed8057900" exists
    When I send a DELETE request to "/api/v1/account/48d48295-cf63-4e56-8088-7faed8057900"
    Then the response status code should be 204

  Scenario: Check if a passenger exists
    Given I authenticate as "test-passenger@gmail.com" with password "test-password"
    Given the passenger with ID "dc6ea845-03a1-4c74-9fbf-4bf6e4198578" exists
    When I send a GET request to "/api/v1/passengers/dc6ea845-03a1-4c74-9fbf-4bf6e4198578/exists"
    Then the response status code should be 200

  Scenario: Get a non-existent passenger
    Given I authenticate as "test-passenger@gmail.com" with password "test-password"
    When I send a GET request to "/api/v1/passengers/11111111-9999-1111-1111-111111111111"
    Then the response status code should be 404

  Scenario: Edit a non-existent passenger
    Given I authenticate as "test-passenger@gmail.com" with password "test-password"
    Given I have a valid passenger payload
    When I send a PUT request to "/api/v1/account/11111111-9999-1111-1111-111111111111" with the payload
    Then the response status code should be 403

  Scenario: Delete a non-existent passenger
    Given I authenticate as "test-passenger@gmail.com" with password "test-password"
    When I send a DELETE request to "/api/v1/account/11111111-9999-1111-1111-111111111111"
    Then the response status code should be 403
