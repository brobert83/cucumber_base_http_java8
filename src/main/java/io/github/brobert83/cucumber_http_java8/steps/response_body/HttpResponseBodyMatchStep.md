# Definition
```gherkin
Then the response body matches 'expected body here'
Then the response body matches
"""
expected body here
"""
```
# Details

This step will compare the response body with a expected value. 

**IMPORTANT**: The assertion logic will use the response header 'Content-type' to identify the content type and perform specialized comparisons based on that

If no 'Content-type' header is present, the comparison will default to 'application/json'

Supported types:
- application/json (uses Jackson)
- application/xml (uses XMLUnit)
- text/plain (simple string comparison)

The content type value match is done using a regular expression, so for example `application/xml;charset=UTF-8` or `application/xml;` will match, but `application/xml-something` wont.

Unknown content types will throw an exception.

# Examples

```gherkin
Scenario: Perform a JSON match assert

  Given a 'GET' request

  When the request is sent to '/resources/1'

  Then the server responds with status code '200'
  Then the response has header 'Content-Type'='application/json'
  Then the response body matches
  """
  { "name" : "Rob" , "status" : "active" }
  """
```

```gherkin
Scenario: Perform a XML match assert

  Given a 'GET' request

  When the request is sent to '/resources/1'

  Then the server responds with status code '200'
  Then the response has header 'Content-Type'='application/xml'
  Then the response body matches
  """
  <unit>
    <name>Rob</name>
    <status>active</status>
  </unit>
  """
```

```gherkin
Scenario: Perform a TEXT match assert

  Given a 'GET' request

  When the request is sent to '/resources/1'

  Then the server responds with status code '200'
  Then the response has header 'Content-Type'='text/plain'
  Then the response body matches 'Rob is active'
```
