package io.github.brobert83.cucumber_http_java8.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.brobert83.cucumber_http_java8.CucumberHttpContext;
import kong.unirest.HttpResponse;
import io.github.brobert83.cucumber_http_java8.request_handlers.HttpRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpRequestSteps {

    private final static ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper();
    private final static String DEFAULT_BASE_URL = "http://localhost:8080";;

    @Autowired(required = false) Supplier<ObjectMapper> objectMapperSupplier;
    @Autowired(required = false) Supplier<String> baseUrl;
    @Autowired Map<String, HttpRequestHandler<HttpResponse<String>>> unirestHttpHandlers;
    @Autowired CucumberHttpContext cucumberHttpContext;

    ObjectMapper objectMapper() {
        return Optional.ofNullable(objectMapperSupplier).map(Supplier::get).orElse(DEFAULT_OBJECT_MAPPER);
    }

    String baseUrl() {
        return Optional.ofNullable(baseUrl).map(Supplier::get).orElse(DEFAULT_BASE_URL);
    }

    String appUrl(String uri) {
        return baseUrl() + uri;
    }

    @Given("^a '(.*)' request$")
    public void requestMethod(String method) {
        cucumberHttpContext.newContext();
        cucumberHttpContext.setRequestMethod(method);
    }

    @Given("^the request body is$")
    public void setRequestBodyHereDoc(String body) {
        setRequestBody(body);
    }

    @Given("^the request body is '(.*)'$")
    public void setRequestBodyParam(String body) {
        setRequestBody(body);
    }

    private void setRequestBody(String body) {
        cucumberHttpContext.setRequestBody(body);
    }

    @Given("^the request has header '(.*)'='(.*)'$")
    public void requestHeader(String headerName, String headerValue) {
        cucumberHttpContext.getRequestHeaders().put(headerName, headerValue);
    }

    @When("^the request is sent to '(.*)'$")
    public void sendRequest(String uri) {

        cucumberHttpContext.setUrl(appUrl(uri));

        String requestMethod = cucumberHttpContext.getRequestMethod();

        Optional<HttpRequestHandler<HttpResponse<String>>> handler = Optional.ofNullable(unirestHttpHandlers.get(requestMethod.toLowerCase()));
        handler.orElseThrow(() -> new RuntimeException(String.format("No handler found for '%s'", requestMethod)));

        HttpResponse<String> response = handler.map(h -> h.handle(cucumberHttpContext)).orElse(null);

        cucumberHttpContext.setHttpResponse(response);
    }

    @Then("^the server responds with status code '(.*)'$")
    public void verifyStatusCode(int expectedStatusCode) {
        assertThat(cucumberHttpContext.getHttpResponse().getStatus())
                .describedAs("Response code for body:\n" + cucumberHttpContext.getHttpResponse().getBody())
                .isEqualTo(expectedStatusCode);
    }

    @Then("^the response body matches$")
    public void verifyResponseBodyHeredoc(String expectedBody) throws JsonProcessingException {
        verifyResponseBody(expectedBody);
    }

    @Then("^the response body matches '(.*)'$")
    public void verifyResponseBodyParam(String expectedBody) throws JsonProcessingException {
        verifyResponseBody(expectedBody);
    }

    private void verifyResponseBody(String expectedBody) throws JsonProcessingException {
        JsonNode expectedBodyNode = objectMapper().readTree(expectedBody);
        JsonNode actualBodyNode = objectMapper().readTree(cucumberHttpContext.getHttpResponse().getBody());

        assertThat(actualBodyNode).isEqualTo(expectedBodyNode);
    }

    @Then("^the response has header '(.*)'='(.*)'$")
    public void verifyResponseHeader(String headerName, String headerValue) {

        List<String> headerValues = cucumberHttpContext.getHttpResponse().getHeaders().get(headerName);

        assertThat(headerValues).isNotNull();
        assertThat(headerValues).contains(headerValue);
    }

}
