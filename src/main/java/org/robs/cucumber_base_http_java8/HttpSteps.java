package org.robs.cucumber_base_http_java8;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

//@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class HttpSteps {

    @Autowired Supplier<ObjectMapper> objectMapper;
    @Autowired Supplier<String> baseUrl;

    static Function<HttpStepsContext, HttpResponse<String>> sendPost = context ->
            Unirest
                    .post(context.getUrl())
                    .header("Content-Type", context.getContentType())
                    .body(context.getRequestBody())
                    .asString();

    static Function<HttpStepsContext, HttpResponse<String>> sendPut = context ->
            Unirest
                    .put(context.getUrl())
                    .header("Content-Type", context.getContentType())
                    .body(context.getRequestBody())
                    .asString();

    static Function<HttpStepsContext, HttpResponse<String>> sendGet = context ->
            Unirest
                    .get(context.getUrl())
                    .header("Content-Type", context.getContentType())
                    .asString();

    static Function<HttpStepsContext, HttpResponse<String>> sendDelete = context ->
            Unirest
                    .delete(context.getUrl())
                    .header("Content-Type", context.getContentType())
                    .asString();

    public static HttpStepsContext httpStepsContext = new HttpStepsContext();

    static{
        httpStepsContext.getHttpMethodHandlers().put("POST", sendPost);
        httpStepsContext.getHttpMethodHandlers().put("GET", sendGet);
        httpStepsContext.getHttpMethodHandlers().put("PUT", sendPut);
        httpStepsContext.getHttpMethodHandlers().put("DELETE", sendDelete);
    }

    String appUrl(String uri) {
        return baseUrl.get() + uri;
    }

    @Given("^the request body is$")
    public void requestBody(String body) {
        httpStepsContext.setRequestBody(body);
    }

    @Given("^the request method is '(.*)'$")
    public void requestMethod(String method) {
        httpStepsContext.setRequestMethod(method);
    }

    @Given("^the request Content-Type is '(.*)'$")
    public void contentType(String contentType) {
        httpStepsContext.setContentType(contentType);
    }

    @When("^the request is sent to '(.*)'$")
    public void sendRequest(String uri) {

        String requestMethod = httpStepsContext.getRequestMethod();

        Function<HttpStepsContext, HttpResponse<String>> handler = Optional.ofNullable(httpStepsContext.getHttpMethodHandlers().get(requestMethod))
                .orElseThrow(() -> new RuntimeException(String.format("No handler found for '%s'", requestMethod)));

        httpStepsContext.setUrl(appUrl(uri));
        httpStepsContext.setHttpResponse(handler.apply(httpStepsContext));
    }

    @Then("^the server responds with status code '(.*)'$")
    public void verifyStatusCode(int expectedStatusCode) {
        assertThat(httpStepsContext.getHttpResponse().getStatus())
                .describedAs("Response code for body:\n" + httpStepsContext.getHttpResponse().getBody())
                .isEqualTo(expectedStatusCode);
    }

    @Then("^the response body matches$")
    public void verifyResponseBody(String expectedBody) throws JsonProcessingException {

        JsonNode expectedBodyNode = objectMapper.get().readTree(expectedBody);
        JsonNode actualBodyNode = objectMapper.get().readTree(httpStepsContext.getHttpResponse().getBody());

        assertThat(actualBodyNode).isEqualTo(expectedBodyNode);
    }

}
