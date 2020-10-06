# Base for Cucumber HTTP steps [![Build Status](https://travis-ci.com/brobert83/cucumber_base_http_java8.svg?branch=master)](https://travis-ci.com/brobert83/cucumber_base_http_java8)
This library aims to provide a bundle of Cucumber predefined steps to perform HTTP calls and perform verifications on the HTTP response.

There is a small test project demonstrating how to use this library here https://github.com/brobert83/cucumber_base_http_java8_springboot_test

#### [Roadmap](dev/Roadmap.md) 

#### [For developers](dev/Devnotes.md)

## Steps provided
```java
@Given("^a '(.*)' request$")
@Given("^the request body is$")
@Given("^the request body is '(.*)'$")
@Given("^the request has header '(.*)'='(.*)'$")

@When("^the request is sent to '(.*)'$")

@Then("^the server responds with status code '(.*)'$")
@Then("^the response body matches$")
@Then("^the response body matches '(.*)'$")
@Then("^the response has header '(.*)'='(.*)'$")
```

### Example
#### Testing a GET
```gherkin
Given a 'GET' request
Given the request has header 'Content-Type'='application/json'

When the request is sent to '/resources/1'

Then the server responds with status code '200'
Then the response body matches '{"name":"Rob","status":"active"}'
Then the response has header 'Content-Type'='application/json'
```

#### Testing a POST
```gherkin
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
```

# How to use this library to test a typical Spring boot app

## A. Setup

### 1. Add the maven dependency
```xml
<dependency>
    <groupId>io.github.brobert83</groupId>
    <artifactId>cucumber-http-java8</artifactId>
    <version>0.1.1</version>
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

### 3. Create a test spring configuration and define 2 beans required

```java
@ActiveProfiles("cucumber")
@CucumberContextConfiguration
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {
                StepsBase.SpringTestConfig.class, // the class just below
                CucumberBaseSpringConfig.class, // library class, required
                MySpringBootApp.class, // your @SpringBootApplication class
                MySpringApplicationConfiguration.class // a class annotated with @Configuration
        }
)
public class StepsBase {

    @Lazy // lazy is needed because @LocalServerPort is not available at context creation time, take a little while for the servlet container to be up
    @Profile("cucumber")
    @Configuration
    public static class SpringTestConfig {

        // ==== Customize the ObjectMapper used to perform body json comparisons ====
        // this defaults to new ObjectMapper if not declared
        @Bean
        Supplier<ObjectMapper> objectMapperSupplier(ObjectMapper objectMapper) {
            return () -> objectMapper;
        }
        
        // ==== Customize how the url is constructed (the base part) ====
        // this defaults to "http://localhost:8080" if not declared 
        @Bean
        Supplier<String> baseUrl(@LocalServerPort int port) {
            return () -> "http://localhost:" + port;
        }

    }

}
```

## B. Interoperability with your own step definitions

#### The *cucumberHttpContext* bean is used to pass state in between steps

In case you want to perform additional assertions on the http response or tune the request, you can inject the CucumberHttpContext bean in your step definition class

For example:
```java
public class MySteps {
    
    @Autowired CucumberHttpContext cucumberHttpContext;

    @Given("^some custom setup$")
    public void someSetup(){
        //..
        cucumberHttpContext.getRequestHeaders().put("john","smith");
        //...
    }

    @Given("^some custom check$")
    public void someCheck(){
        //..
        assertThat(cucumberHttpContext.getHttpResponse().getBody()).contains("apples");
        //...
    }
}
```
#### The *unirestHttpHandlers* bean holds the request handlers

In case you want to change how the http call is made for a particular method type, inject the *unirestHttpHandlers* bean and replace the entries you want.

A way of doing that (a little hacky) is to declare a dummy bean in your spring config, inject it via parameter, and modify it there.

_Note_: The underlying library used to make the calls is Unirest and the current implementation does not allow for generic response types, so you will need to use it.

Like so: 
```java
    
    @Configuration
    public static class SpringTestConfig {

        @Bean
        boolean customPostHandler(Map<String, HttpRequestHandler<HttpResponse<String>>> unirestHttpHandlers){

            unirestHttpHandlers.put("post", context -> {
                
                // do your customization here
                return null;
            });

            return true;
        }
}
```

# General considerations

- This main intention for this library is to be used in conjunction with Spring/Springboot
- The mechanism used to create interoperability is Spring beans wiring
- Currently only json response body is supported, anything else and the body assert will fail
