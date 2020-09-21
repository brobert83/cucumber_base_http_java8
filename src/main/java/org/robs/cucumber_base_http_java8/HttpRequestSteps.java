package org.robs.cucumber_base_http_java8;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import kong.unirest.HttpResponse;
import org.robs.cucumber_base_http_java8.request_handlers.HttpRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpRequestSteps {

    // User defined
    @Autowired Supplier<ObjectMapper> objectMapper;
    @Autowired Supplier<String> baseUrl;

    // internal defined
    @Autowired Map<String, HttpRequestHandler<HttpResponse<String>>> unirestHttpHandlers;

    HttpRequestStepsContext context = new HttpRequestStepsContext();

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
        context.setRequestBody(body);
    }

    @Given("^the request method is '(.*)'$")
    public void requestMethod(String method) {
        context.setRequestMethod(method);
    }

    @Given("^the request has header '(.*)'='(.*)'$")
    public void requestHeader(String headerName, String headerValue) {
        context.getRequestHeaders().put(headerName, headerValue);
    }

    @When("^the request is sent to '(.*)'$")
    public void sendRequest(String uri) {

        context.setUrl(appUrl(uri));

        String requestMethod = context.getRequestMethod();

        HttpResponse<String> response = Optional.ofNullable(unirestHttpHandlers.get(requestMethod.toLowerCase()))
                .map(handler -> handler.handle(context))
                .orElseThrow(() -> new RuntimeException(String.format("No handler found for '%s'", requestMethod)));

        context.setHttpResponse(response);
    }

    @Then("^the server responds with status code '(.*)'$")
    public void verifyStatusCode(int expectedStatusCode) {
        assertThat(context.getHttpResponse().getStatus())
                .describedAs("Response code for body:\n" + context.getHttpResponse().getBody())
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
        JsonNode expectedBodyNode = objectMapper.get().readTree(expectedBody);
        JsonNode actualBodyNode = objectMapper.get().readTree(context.getHttpResponse().getBody());

        assertThat(actualBodyNode).isEqualTo(expectedBodyNode);
    }

    @Then("^the response has header '(.*)'='(.*)'$")
    public void verifyResponseHeader(String headerName, String headerValue) {

        List<String> headerValues = context.getHttpResponse().getHeaders().get(headerName);

        assertThat(headerValues).isNotNull();
        assertThat(headerValues).contains(headerValue);
    }

}
