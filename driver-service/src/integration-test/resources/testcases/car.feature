Feature: Car API Endpoints

  Scenario: Get existing car by ID
    Given the car with ID 100 exists
    When I send a GET request to "/api/v1/cars/100?active=true"
    Then the response status code should be 200

  Scenario: Get all cars
    When I send a GET request to "/api/v1/cars?page=0&limit=10"
    Then the response status code should be 200
    And the response body should contain "totalElements"

  Scenario: Add a new car
    Given I have a valid car payload
    When I send a POST request to "/api/v1/cars" with the payload
    Then the response status code should be 201

  Scenario: Edit an existing car
    Given the car with ID 101 exists
    And I have an updated car payload
    When I send a PUT request to "/api/v1/cars/101" with the payload
    Then the response status code should be 200

  Scenario: Delete a car
    Given the car with ID 100 exists
    When I send a DELETE request to "/api/v1/cars/100"
    Then the response status code should be 204

  Scenario: Get a non-existent car
    When I send a GET request to "/api/v1/cars/999"
    Then the response status code should be 404
