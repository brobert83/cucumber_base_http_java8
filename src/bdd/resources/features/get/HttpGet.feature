Feature: Steps for performing a GET request

  Scenario: Perform a GET request and verify the status code

    Given the http mock endpoint 'resources_for_getting' for method 'GET' on path '/resources/1'
    Given the http mock endpoint 'resources_for_getting' expects request header 'Content-Type'='application/json'
    Given the http mock endpoint 'resources_for_getting' responds with body '{"name":"Rob","status":"active"}'
    Given the http mock endpoint 'resources_for_getting' responds with header 'Content-Type'='application/json'
    Given the http mock endpoint 'resources_for_getting' responds with status code '200'
    Given the http mock endpoint 'resources_for_getting' is made available

    Given the request method is 'GET'
    Given the request has header 'Content-Type'='application/json'

    When the request is sent to '/resources/1'

    Then the server responds with status code '200'
    Then the response body matches '{"name":"Rob","status":"active"}'
    Then the response has header 'Content-Type'='application/json'

  Scenario: Perform a GET request and verify the status code (Heredoc)

    Given the http mock endpoint 'resources_for_getting' for method 'GET' on path '/resources/1'
    Given the http mock endpoint 'resources_for_getting' expects request header 'Content-Type'='application/json'
    Given the http mock endpoint 'resources_for_getting' responds with body
    """
    { "name" : "Rob" , "status" : "active" }
    """
    Given the http mock endpoint 'resources_for_getting' responds with header 'Content-Type'='application/json'
    Given the http mock endpoint 'resources_for_getting' responds with status code '200'
    Given the http mock endpoint 'resources_for_getting' is made available

    Given the request method is 'GET'
    Given the request has header 'Content-Type'='application/json'

    When the request is sent to '/resources/1'

    Then the server responds with status code '200'
    Then the response body matches
    """
    { "name" : "Rob" , "status" : "active" }
    """
    Then the response has header 'Content-Type'='application/json'
