package io.github.brobert83.cucumber_http_java8.steps;

import io.cucumber.java.en.Then;
import io.github.brobert83.cucumber_http_java8.CucumberHttpContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class HttpResponseSteps {

    @Autowired CucumberHttpContext cucumberHttpContext;

    @Then("^the server responds with status code '(.*)'$")
    public void verifyStatusCode(int expectedStatusCode) {
        assertThat(cucumberHttpContext.getHttpResponse().getStatus())
                .describedAs("Response code for body:\n" + cucumberHttpContext.getHttpResponse().getBody())
                .isEqualTo(expectedStatusCode);
    }

    @Then("^the response has header '(.*)'='(.*)'$")
    public void verifyResponseHeader(String headerName, String headerValue) {

        List<String> headerValues = cucumberHttpContext.getHttpResponse().getHeaders().get(headerName);

        assertThat(headerValues).isNotNull();
        assertThat(headerValues).contains(headerValue);
    }

}
