Feature: Steps for performing a DELETE request

  Scenario: Perform a DELETE request

    Given the http mock endpoint 'post_endpoint' for method 'DELETE' on path '/resources/10'
    Given the http mock endpoint 'post_endpoint' expects request header 'Content-Type'='application/json'
    Given the http mock endpoint 'post_endpoint' responds with status code '200'
    Given the http mock endpoint 'post_endpoint' is made available

    Given a 'DELETE' request
    Given the request has header 'Content-Type'='application/json'

    When the request is sent to '/resources/10'

    Then the server responds with status code '200'
