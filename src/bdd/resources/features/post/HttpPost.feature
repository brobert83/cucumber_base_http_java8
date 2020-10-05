Feature: Steps for performing a POST request

  Scenario: Perform a POST request

    Given the http mock endpoint 'post_endpoint' for method 'POST' on path '/resources'
    Given the http mock endpoint 'post_endpoint' expects request header 'Content-Type'='application/json'
    Given the http mock endpoint 'post_endpoint' expects request body
    """
    {"name":"Rob", "update":"yes"}
    """
    Given the http mock endpoint 'post_endpoint' responds with body '{"id":10, "name":"Rob","status":"active"}'
    Given the http mock endpoint 'post_endpoint' responds with header 'Content-Type'='application/json'
    Given the http mock endpoint 'post_endpoint' responds with status code '201'
    Given the http mock endpoint 'post_endpoint' is made available

    Given a 'POST' request
    Given the request has header 'Content-Type'='application/json'
    Given the request body is
    """
    {"name":"Rob", "update":"yes"}
    """
    When the request is sent to '/resources'

    Then the server responds with status code '201'
    Then the response body matches '{"id":10, "name":"Rob","status":"active"}'
    Then the response has header 'Content-Type'='application/json'

  Scenario: Perform a POST request (Heredoc)

    Given the http mock endpoint 'post_endpoint' for method 'POST' on path '/resources'
    Given the http mock endpoint 'post_endpoint' expects request header 'Content-Type'='application/json'
    Given the http mock endpoint 'post_endpoint' responds with body
    """
    {"id":10, "name":"Rob","status":"active"}
    """
    Given the http mock endpoint 'post_endpoint' responds with header 'Content-Type'='application/json'
    Given the http mock endpoint 'post_endpoint' responds with status code '201'
    Given the http mock endpoint 'post_endpoint' is made available

    Given a 'POST' request
    Given the request has header 'Content-Type'='application/json'

    When the request is sent to '/resources'

    Then the server responds with status code '201'
    Then the response body matches
    """
    {"id":10, "name":"Rob","status":"active"}
    """
    Then the response has header 'Content-Type'='application/json'