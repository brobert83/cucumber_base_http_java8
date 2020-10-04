package io.github.brobert83.cucumber_http_java8;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import kong.unirest.HttpResponse;
import io.github.brobert83.cucumber_http_java8.request_handlers.HttpRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpRequestSteps {

    // User defined
    @Autowired Supplier<ObjectMapper> objectMapperSupplier;
    @Autowired Supplier<String> baseUrl;

    // internal defined
    @Autowired Map<String, HttpRequestHandler<HttpResponse<String>>> unirestHttpHandlers;
    @Autowired HttpRequestStepsContext httpRequestStepsContext;

    String appUrl(String uri) {
        return baseUrl.get() + uri;
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
        httpRequestStepsContext.setRequestBody(body);
    }

    @Given("^the request method is '(.*)'$")
    public void requestMethod(String method) {
        httpRequestStepsContext.setRequestMethod(method);
    }

    @Given("^the request has header '(.*)'='(.*)'$")
    public void requestHeader(String headerName, String headerValue) {
        httpRequestStepsContext.getRequestHeaders().put(headerName, headerValue);
    }

    @When("^the request is sent to '(.*)'$")
    public void sendRequest(String uri) {

        httpRequestStepsContext.setUrl(appUrl(uri));

        String requestMethod = httpRequestStepsContext.getRequestMethod();

        HttpResponse<String> response = Optional.ofNullable(unirestHttpHandlers.get(requestMethod.toLowerCase()))
                .map(handler -> handler.handle(httpRequestStepsContext))
                .orElseThrow(() -> new RuntimeException(String.format("No handler found for '%s'", requestMethod)));

        httpRequestStepsContext.setHttpResponse(response);
    }

    @Then("^the server responds with status code '(.*)'$")
    public void verifyStatusCode(int expectedStatusCode) {
        assertThat(httpRequestStepsContext.getHttpResponse().getStatus())
                .describedAs("Response code for body:\n" + httpRequestStepsContext.getHttpResponse().getBody())
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
        JsonNode expectedBodyNode = objectMapperSupplier.get().readTree(expectedBody);
        JsonNode actualBodyNode = objectMapperSupplier.get().readTree(httpRequestStepsContext.getHttpResponse().getBody());

        assertThat(actualBodyNode).isEqualTo(expectedBodyNode);
    }

    @Then("^the response has header '(.*)'='(.*)'$")
    public void verifyResponseHeader(String headerName, String headerValue) {

        List<String> headerValues = httpRequestStepsContext.getHttpResponse().getHeaders().get(headerName);

        assertThat(headerValues).isNotNull();
        assertThat(headerValues).contains(headerValue);
    }

}
