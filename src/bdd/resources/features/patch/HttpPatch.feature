Feature: Steps for performing a PATCH request

  Scenario: Perform a PATCH request

    Given the http mock endpoint 'post_endpoint' for method 'PATCH' on path '/resources'
    Given the http mock endpoint 'post_endpoint' expects request header 'Content-Type'='application/json'
    Given the http mock endpoint 'post_endpoint' responds with body '{"id":10, "name":"Rob","status":"active"}'
    Given the http mock endpoint 'post_endpoint' responds with header 'Content-Type'='application/json'
    Given the http mock endpoint 'post_endpoint' responds with status code '200'
    Given the http mock endpoint 'post_endpoint' is made available

    Given a 'PATCH' request
    Given the request has header 'Content-Type'='application/json'

    When the request is sent to '/resources'

    Then the server responds with status code '200'
    Then the response body matches '{"id":10, "name":"Rob","status":"active"}'
    Then the response has header 'Content-Type'='application/json'

  Scenario: Perform a PATCH request (Heredoc)

    Given the http mock endpoint 'post_endpoint' for method 'PATCH' on path '/resources'
    Given the http mock endpoint 'post_endpoint' expects request header 'Content-Type'='application/json'
    Given the http mock endpoint 'post_endpoint' responds with body
    """
    {"id":10, "name":"Rob","status":"active"}
    """
    Given the http mock endpoint 'post_endpoint' responds with header 'Content-Type'='application/json'
    Given the http mock endpoint 'post_endpoint' responds with status code '200'
    Given the http mock endpoint 'post_endpoint' is made available

    Given a 'PATCH' request
    Given the request has header 'Content-Type'='application/json'

    When the request is sent to '/resources'

    Then the server responds with status code '200'
    Then the response body matches
    """
    {"id":10, "name":"Rob","status":"active"}
    """
    Then the response has header 'Content-Type'='application/json'