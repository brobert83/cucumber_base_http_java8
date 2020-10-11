Feature: The response body asserts ue the Content-type response header to identify the format

  Scenario Outline: Perform a TEXT assert

    Given the http mock endpoint 'endpoint_1' for method '<method>' on path '/resources/1'
    Given the http mock endpoint 'endpoint_1' expects request header 'Content-Type'='application/json'
    Given the http mock endpoint 'endpoint_1' responds with body
    """
    Rob is active
    """
    Given the http mock endpoint 'endpoint_1' responds with header 'Content-Type'='text/plain'
    Given the http mock endpoint 'endpoint_1' responds with status code '200'
    Given the http mock endpoint 'endpoint_1' is made available

    Given a 'GET' request
    Given the request has header 'Content-Type'='application/json'

    When the request is sent to '/resources/1'

    Then the server responds with status code '200'
    Then the response has header 'Content-Type'='text/plain'
    Then the response body matches 'Rob is active'

    Examples:
      | method |
      | GET    |
      | POST   |
      | PUT    |
      | PATCH  |

  Scenario Outline: Perform a JSON assert

    Given the http mock endpoint 'endpoint_2' for method '<method>' on path '/resources/2'
    Given the http mock endpoint 'endpoint_2' expects request header 'Content-Type'='application/json'
    Given the http mock endpoint 'endpoint_2' responds with body
    """
    { "name" : "Rob" , "status" : "active" }
    """
    Given the http mock endpoint 'endpoint_2' responds with header 'Content-Type'='application/json'
    Given the http mock endpoint 'endpoint_2' responds with status code '200'
    Given the http mock endpoint 'endpoint_2' is made available

    Given a 'GET' request
    Given the request has header 'Content-Type'='application/json'

    When the request is sent to '/resources/2'

    Then the server responds with status code '200'
    Then the response has header 'Content-Type'='application/json'
    Then the response body matches
    """
    { "status" : "active", "name" : "Rob"  }
    """

    Examples:
      | method |
      | GET    |
      | POST   |
      | PUT    |
      | PATCH  |

  Scenario Outline: Perform a JSON assert (default)

    Given the http mock endpoint 'endpoint_3' for method '<method>' on path '/resources/3'
    Given the http mock endpoint 'endpoint_3' expects request header 'Content-Type'='application/json'
    Given the http mock endpoint 'endpoint_3' responds with body
    """
    { "name" : "Rob" , "status" : "active" }
    """
    Given the http mock endpoint 'endpoint_3' responds with status code '200'
    Given the http mock endpoint 'endpoint_3' is made available

    Given a 'GET' request
    Given the request has header 'Content-Type'='application/json'

    When the request is sent to '/resources/3'

    Then the server responds with status code '200'
    Then the response body matches
    """
    { "name" : "Rob" , "status" : "active" }
    """

    Examples:
      | method |
      | GET    |
      | POST   |
      | PUT    |
      | PATCH  |

  Scenario Outline: Perform a XML assert

    Given the http mock endpoint 'endpoint_4' for method '<method>' on path '/resources/4'
    Given the http mock endpoint 'endpoint_4' expects request header 'Content-Type'='application/json'
    Given the http mock endpoint 'endpoint_4' responds with body
    """
    <unit>
      <status>active</status>
      <name>Rob</name>
    </unit>
    """
    Given the http mock endpoint 'endpoint_4' responds with header 'Content-Type'='application/xml'
    Given the http mock endpoint 'endpoint_4' responds with status code '200'
    Given the http mock endpoint 'endpoint_4' is made available

    Given a 'GET' request
    Given the request has header 'Content-Type'='application/json'

    When the request is sent to '/resources/4'

    Then the server responds with status code '200'
    Then the response has header 'Content-Type'='application/xml'
    Then the response body matches
    """
    <unit>
      <name>Rob</name>
      <status>active</status>
    </unit>
    """

    Examples:
      | method |
      | GET    |
      | POST   |
      | PUT    |
      | PATCH  |
