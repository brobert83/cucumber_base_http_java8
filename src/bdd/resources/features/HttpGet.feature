Feature: Steps for performing a GET request

  Scenario: Perform a GET request and verify the status code

    Given the 'resources_for_getting' endpoint for method 'GET' on path '/resources/1'
    Given the 'resources_for_getting' endpoint expects request header 'Content-Type'='application/json'
    Given the 'resources_for_getting' endpoint responds with body
    """
    { "name" : "Rob" , "status" : "active" }
    """
    Given the 'resources_for_getting' endpoint responds with header 'Content-Type'='application/json'
    Given the 'resources_for_getting' endpoint responds with status code '200'
    Given the 'resources_for_getting' endpoint is available

    Given the request method is 'GET'
    Given the request Content-Type is 'application/json'
    When the request is sent to '/resources/1'
    Then the server responds with status code '200'
    Then the request body is
    """
    { "name" : "Rob" , "status" : "active" }
    """
