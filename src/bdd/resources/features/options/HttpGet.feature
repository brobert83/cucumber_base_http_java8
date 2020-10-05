Feature: Steps for performing a OPTIONS request

  Scenario: Perform a OPTIONS request

    Given the http mock endpoint 'resources_for_getting' for method 'OPTIONS' on path '/resources/1'
    Given the http mock endpoint 'resources_for_getting' expects request header 'Content-Type'='application/json'
    Given the http mock endpoint 'resources_for_getting' responds with body '{"name":"Rob","status":"active"}'
    Given the http mock endpoint 'resources_for_getting' responds with header 'Content-Type'='application/json'
    Given the http mock endpoint 'resources_for_getting' responds with status code '200'
    Given the http mock endpoint 'resources_for_getting' is made available

    Given a 'OPTIONS' request
    Given the request has header 'Content-Type'='application/json'

    When the request is sent to '/resources/1'

    Then the server responds with status code '200'
    Then the response body matches '{"name":"Rob","status":"active"}'
    Then the response has header 'Content-Type'='application/json'

  Scenario: Perform a OPTIONS request (Heredoc)

    Given the http mock endpoint 'resources_for_getting' for method 'OPTIONS' on path '/resources/1'
    Given the http mock endpoint 'resources_for_getting' expects request header 'Content-Type'='application/json'
    Given the http mock endpoint 'resources_for_getting' responds with body
    """
    { "name" : "Rob" , "status" : "active" }
    """
    Given the http mock endpoint 'resources_for_getting' responds with header 'Content-Type'='application/json'
    Given the http mock endpoint 'resources_for_getting' responds with status code '200'
    Given the http mock endpoint 'resources_for_getting' is made available

    Given a 'OPTIONS' request
    Given the request has header 'Content-Type'='application/json'

    When the request is sent to '/resources/1'

    Then the server responds with status code '200'
    Then the response body matches
    """
    { "name" : "Rob" , "status" : "active" }
    """
    Then the response has header 'Content-Type'='application/json'
