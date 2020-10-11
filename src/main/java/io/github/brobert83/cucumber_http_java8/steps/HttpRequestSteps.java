package io.github.brobert83.cucumber_http_java8.steps;

import io.cucumber.java.en.Given;
import io.github.brobert83.cucumber_http_java8.CucumberHttpContext;
import io.github.brobert83.cucumber_http_java8.request_handlers.HttpRequestHandler;
import kong.unirest.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class HttpRequestSteps {

    @Autowired Map<String, HttpRequestHandler<HttpResponse<String>>> unirestHttpHandlers;
    @Autowired CucumberHttpContext cucumberHttpContext;

    @Given("^a '(.*)' request$")
    public void requestMethod(String method) {
        cucumberHttpContext.newContext();
        cucumberHttpContext.setRequestMethod(method);
    }

    @Given("^the request body is$")
    @Given("^the request body is '(.*)'$")
    public void setRequestBody(String body) {
        cucumberHttpContext.setRequestBody(body);
    }

    @Given("^the request has header '(.*)'='(.*)'$")
    public void requestHeader(String headerName, String headerValue) {
        cucumberHttpContext.getRequestHeaders().put(headerName, headerValue);
    }

}
