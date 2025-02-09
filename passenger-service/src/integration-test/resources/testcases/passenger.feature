Feature: Passenger API Endpoints

  Scenario: Get existing passenger by ID
    Given the passenger with ID "11111111-1111-1111-1111-111111111111" exists
    When I send a GET request to "/api/v1/passengers/11111111-1111-1111-1111-111111111111?active=true"
    Then the response status code should be 200

  Scenario: Get all passengers
    When I send a GET request to "/api/v1/passengers?page=0&limit=10"
    Then the response status code should be 200
    And the response body should contain "totalElements"

  Scenario: Add a new passenger
    Given I have a valid passenger payload
    When I send a POST request to "/api/v1/passengers" with the payload
    Then the response status code should be 201
    And the response body should contain "firstName": "John"

  Scenario: Edit an existing passenger
    Given the passenger with ID "11111111-1111-1111-1111-111111111111" exists
    And I have an updated passenger payload
    When I send a PUT request to "/api/v1/passengers/11111111-1111-1111-1111-111111111111" with the payload
    Then the response status code should be 200
    And the response body should contain "firstName": "Jane"

  Scenario: Delete a passenger
    Given the passenger with ID "11111111-0000-1111-1111-111111111111" exists
    When I send a DELETE request to "/api/v1/passengers/11111111-0000-1111-1111-111111111111"
    Then the response status code should be 204

  Scenario: Check if a passenger exists
    Given the passenger with ID "11111111-1111-1111-1111-111111111111" exists
    When I send a GET request to "/api/v1/passengers/11111111-1111-1111-1111-111111111111/exists"
    Then the response status code should be 200

  Scenario: Get a non-existent passenger
    When I send a GET request to "/api/v1/passengers/11111111-9999-1111-1111-111111111111"
    Then the response status code should be 404

  Scenario: Edit a non-existent passenger
    Given I have a valid passenger payload
    When I send a PUT request to "/api/v1/passengers/11111111-9999-1111-1111-111111111111" with the payload
    Then the response status code should be 404

  Scenario: Delete a non-existent passenger
    When I send a DELETE request to "/api/v1/passengers/11111111-9999-1111-1111-111111111111"
    Then the response status code should be 404
