Feature: Steps for performing a HEAD request

  Scenario: Perform a HEAD request

    Given the http mock endpoint 'resources_for_getting' for method 'HEAD' on path '/resources/1'
    Given the http mock endpoint 'resources_for_getting' expects request header 'Content-Type'='application/json'
    Given the http mock endpoint 'resources_for_getting' responds with header 'Content-Type'='application/json'
    Given the http mock endpoint 'resources_for_getting' responds with status code '200'
    Given the http mock endpoint 'resources_for_getting' is made available

    Given the request method is 'HEAD'
    Given the request has header 'Content-Type'='application/json'

    When the request is sent to '/resources/1'

    Then the server responds with status code '200'
    Then the response has header 'Content-Type'='application/json'
