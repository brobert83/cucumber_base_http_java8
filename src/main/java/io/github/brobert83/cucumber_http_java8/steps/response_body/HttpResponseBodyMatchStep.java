package io.github.brobert83.cucumber_http_java8.steps.response_body;

import io.cucumber.java.en.Then;
import io.github.brobert83.cucumber_http_java8.CucumberHttpContext;
import io.github.brobert83.cucumber_http_java8.assertions.GenericAssertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class HttpResponseBodyMatchStep {

    @Configuration
    public static class SpringConfig {

        public static final String DEFAULT_CONTENT_TYPE = "application/json";

        @Bean
        public Map<String, BiConsumer<String, String>> responseBodyMatchers(
                GenericAssertions genericAssertions) {

            Map<String, BiConsumer<String, String>> matchers = new HashMap<>();

            matchers.put("text/plain(:?;.*)?", genericAssertions::matchText);
            matchers.put("application/json(:?;.*)?", genericAssertions::matchJson);
            matchers.put("application/xml(:?;.*)?", genericAssertions::matchXml);

            return matchers;
        }

        @Bean
        public String httpResponseBodyDefaultContentType() {
            return DEFAULT_CONTENT_TYPE;
        }

    }

    @Autowired CucumberHttpContext cucumberHttpContext;
    @Autowired Map<String, BiConsumer<String, String>> responseBodyMatchers;
    @Autowired String httpResponseBodyDefaultContentType;

    @Then("^the response body matches$")
    @Then("^the response body matches '(.*)'$")
    public void verifyResponseBody(String expectedBody) {

        String body = cucumberHttpContext.getHttpResponse().getBody();

        List<String> contentTypeHeaderValues = cucumberHttpContext.getHttpResponse().getHeaders().get("Content-Type");

        String contentType = contentTypeHeaderValues.size() == 0 ? httpResponseBodyDefaultContentType : contentTypeHeaderValues.get(0);

        String knownContentTypes = String.join(",", responseBodyMatchers.keySet());

        BiConsumer<String, String> matcher = responseBodyMatchers.keySet()
                .stream()
                .filter(contentType::matches)
                .findFirst()
                .map(responseBodyMatchers::get)
                .orElseThrow(() -> new RuntimeException(String.format("Could not compare body for Content-type: '%s', known types are: %s", contentType, knownContentTypes)));

        matcher.accept(body, expectedBody);
    }

}
