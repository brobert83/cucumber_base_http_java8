Feature: Steps for performing firing requests against multiple hosts

  Scenario: Perform requests vs multiple targets

    Given the http mock endpoint 'endpoint_1' for method 'GET' on path '/sv1/1'
    Given the http mock endpoint 'endpoint_1' responds with body '{ "name" : "Rob" , "status" : "active" }'
    Given the http mock endpoint 'endpoint_1' responds with status code '200'
    Given the http mock endpoint 'endpoint_1' is made available

    Given the http mock endpoint 'endpoint_2' for method 'GET' on path '/sv2/100'
    Given the http mock endpoint 'endpoint_2' responds with body '{ "track" : "yes" , "trace" : "true" }'
    Given the http mock endpoint 'endpoint_2' responds with status code '200'
    Given the http mock endpoint 'endpoint_2' is made available

    Given the target server 'server1' pointing at wiremock server with base path '/sv1'
    Given the target server 'server2' pointing at wiremock server with base path '/sv2'

    Given a 'GET' request

    When the request is sent to server 'server1' with path '/1'

    Then the server responds with status code '200'
    Then the response body matches
    """
    { "name" : "Rob" , "status" : "active" }
    """

    Given a 'GET' request
    Given the request has header 'Content-Type'='application/json'

    When the request is sent to server 'server2' with path '/100'

    Then the server responds with status code '200'
    Then the response body matches
    """
    { "track" : "yes" , "trace" : "true" }
    """
