# Base for Cucumber HTTP steps [![Build Status](https://travis-ci.com/brobert83/cucumber_base_http_java8.svg?branch=master)](https://travis-ci.com/brobert83/cucumber_base_http_java8)
This library aims to provide a bundle of Cucumber predefined steps to perform HTTP calls and perform verifications on the HTTP response.

There is a small test project demonstrating how to use this library here https://github.com/brobert83/cucumber_base_http_java8_springboot_test

#### Todos & Ideas are [here](dev/IdeasTodos.md) 

## Steps provided
```java
@Given("^the request body is$")
@Given("^the request body is '(.*)'$")
@Given("^the request method is '(.*)'$")
@Given("^the request has header '(.*)'='(.*)'$")

@When("^the request is sent to '(.*)'$")

@Then("^the server responds with status code '(.*)'$")
@Then("^the response body matches$")
@Then("^the response body matches '(.*)'$")
@Then("^the response has header '(.*)'='(.*)'$")
```

### Example
```gherkin
Given the request method is 'GET'
Given the request has header 'Content-Type'='application/json'

When the request is sent to '/resources/1'

Then the server responds with status code '200'
Then the response body matches '{"name":"Rob","status":"active"}'
Then the response has header 'Content-Type'='application/json'
```

# How to use this library to test a typical Spring boot app

## A. Setup

### 1. Add the maven dependency
```xml
<dependency>
    <groupId>org.robs</groupId>
    <artifactId>cucumber_base_http_java8</artifactId>
    <version>${cucumber_base_http_java8.version}</version>
    <scope>test</scope>
</dependency>     
```
### 2. Add the extraGlue
```java
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/bdd/resources/features",
        plugin = {"pretty", "html:target/cucumber.html"},
        extraGlue = {"org.robs.cucumber_base_http_java8"} // you cannot have both glue and extraGlue, be carefull with this
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

        // ==== REQUIRED BY HttpRequestSteps ====
        // In this example the global objectMapper is used, but another object mapper can be created here with different properties if needed
        // this object is used internally to perform comparisons on json body strings
        @Bean
        Supplier<ObjectMapper> objectMapperSupplier(ObjectMapper objectMapper) {
            return () -> objectMapper;
        }
        
        // ==== REQUIRED BY HttpRequestSteps ====
        // this value will be used to construct the whole url for the requests 
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
        @Bean
        Supplier<String> baseUrl(@LocalServerPort int port) {
            return () -> "http://localhost:" + port;
        }

    }

}
```

## B. Interoperability with your own step definitions

The *HttpRequestStepsContext* bean is used to pass state in between steps

In case you want to perform additional assertions on the http response or tune the request programmatically, you can inject the HttpRequestStepsContext in your step definition class

For example:
```java
public class MySteps {
    
    @Autowired HttpRequestStepsContext httpRequestStepsContext;

    @Given("^some programmatic setup$")
    public void someSetup(){
        //..
        httpRequestStepsContext.getRequestHeaders().put("john","smith");
        //...
    }

    @Given("^some programmatic check$")
    public void someCheck(){
        //..
        assertThat(httpRequestStepsContext.getHttpResponse().getBody()).contains("apples");
        //...
    }
}
```

# General considerations

- This main intention for this library is to be used in conjunction with Spring/Springboot
- The mechanism used to create interoperability is Spring beans wiring
- Currently only json response body is supported, anything else and the body assert will fail
