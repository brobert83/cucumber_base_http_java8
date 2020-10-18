# Cucumber HTTP steps 
![Build](https://github.com/brobert83/cucumber_base_http_java8/workflows/Build/badge.svg?branch=master) 
![Maven Central](https://img.shields.io/maven-central/v/io.github.brobert83/cucumber-http-java8)
![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/apache/maven.svg?label=License)
![Lines of code](https://img.shields.io/tokei/lines/github/brobert83/cucumber_base_http_java8)

**THIS LIBRARY IS VERY NEW, I'M NOT DONE WITH THE CORE YET, BUT IT'S USABLE**

This library aims to provide a bundle of Cucumber predefined steps to perform HTTP calls and perform verifications on the HTTP response.

There is a small test project demonstrating how to use this library here https://github.com/brobert83/cucumber_base_http_java8_springboot_test

### Visit the [WIKI](https://github.com/brobert83/cucumber_base_http_java8/wiki/home) for more information 

# Basic setup
```xml
<dependency>
    <groupId>io.github.brobert83</groupId>
    <artifactId>cucumber-http-java8</artifactId>
    <version>0.1.2</version>
    <scope>test</scope>
</dependency>     
```
### 2. Add the extraGlue
```java
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/bdd/resources/features",
        plugin = {"pretty", "html:target/cucumber.html"},
        extraGlue = {"io.github.brobert83.cucumber_http_java8"} // you cannot have both glue and extraGlue, be careful with this
)
public class CucumberTest {

}
```

## Steps provided
```gherkin 
Given a '(.*)' request
Given the request body is '(.*)'` 
Given the request has header '(.*)'='(.*)'
When the request is sent to '(.*)'
Then the server responds with status code '(.*)'
Then the response body matches '(.*)'`
Then the response has header '(.*)'='(.*)'
```        

## Reference doc
[Then the response body matches '(.*)'](src/main/java/io/github/brobert83/cucumber_http_java8/steps/response_body/HttpResponseBodyMatchStep.md)

# Example
```gherkin
Scenario: Perform a GET request

  Given a 'GET' request
  Given the request has header 'Content-Type'='application/json'
  
  When the request is sent to '/resources/1'
  
  Then the server responds with status code '200'
  Then the response body matches '{"name":"Rob","status":"active"}'
  Then the response has header 'Content-Type'='application/json'
```

# General considerations

- This main intention for this library is to be used in conjunction with Spring/Springboot
- The mechanism used to create interoperability is Spring beans wiring
