Feature: Ride API Endpoints

  Scenario: Get ride by ID
    Given the ride with ID 101 exists
    When I send a GET request to "/api/v1/rides/101"
    Then the response status code should be 200

  Scenario: Get all rides
    When I send a GET request to "/api/v1/rides?page=0&limit=10"
    Then the response status code should be 200
    And the response body should contain "totalElements"

  Scenario: Add a new ride
    Given I have a valid ride payload
    When I send a POST request to "/api/v1/rides" with the payload
    Then the response status code should be 201

  Scenario: Update ride status
    Given the ride with ID 101 exists
    And I have a valid status update payload
    When I send a PUT request to "/api/v1/rides/101/status" with the payload
    Then the response status code should be 200