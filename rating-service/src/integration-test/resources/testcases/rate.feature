Feature: Rate API Endpoints

  Scenario: Get rate by ID
    Given the rate with ID 100 exists
    When I send a GET request to "/api/v1/rates/100"
    Then the response status code should be 200

  Scenario: Get all rates
    When I send a GET request to "/api/v1/rates?page=0&size=10"
    Then the response status code should be 200
    And the response body should contain "totalElements"

  Scenario: Get all rates from passengers
    When I send a GET request to "/api/v1/rates/from-passengers?page=0&size=10"
    Then the response status code should be 200
    And the response body should contain "totalElements"

  Scenario: Get all rates for a specific passenger
    When I send a GET request to "/api/v1/rates/from-passengers/11111111-1111-1111-1111-111111111111?page=0&size=10"
    Then the response status code should be 200
    And the response body should contain "totalElements"

  Scenario: Add a new rate
    Given I have a valid rate payload
    When I send a POST request to "/api/v1/rates" with the payload
    Then the response status code should be 201
