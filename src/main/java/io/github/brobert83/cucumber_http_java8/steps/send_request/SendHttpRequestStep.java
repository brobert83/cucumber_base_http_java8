package io.github.brobert83.cucumber_http_java8.steps.send_request;

import io.cucumber.java.en.When;
import io.github.brobert83.cucumber_http_java8.CucumberHttpContext;
import io.github.brobert83.cucumber_http_java8.request_handlers.HttpRequestHandler;
import io.github.brobert83.cucumber_http_java8.request_handlers.unirest.common.SendUnirestRequestWithBody;
import io.github.brobert83.cucumber_http_java8.request_handlers.unirest.common.SendUnirestRequestWithoutBody;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class SendHttpRequestStep {

    private final static String DEFAULT_BASE_URL = "http://localhost:8080";

    @Configuration
    @Lazy
    public static class SpringConfig {

        @Bean
        public Map<String, HttpRequestHandler<HttpResponse<String>>> unirestHttpHandlers(
                SendUnirestRequestWithBody sendUnirestRequestWithBody,
                SendUnirestRequestWithoutBody sendUnirestRequestWithoutBody) {

            Map<String, HttpRequestHandler<HttpResponse<String>>> handlers = new HashMap<>();
            handlers.put("post", context -> sendUnirestRequestWithBody.apply(Unirest.post(context.getUrl()), context));
            handlers.put("put", context -> sendUnirestRequestWithBody.apply(Unirest.put(context.getUrl()), context));
            handlers.put("delete", context -> sendUnirestRequestWithBody.apply(Unirest.delete(context.getUrl()), context));
            handlers.put("patch", context -> sendUnirestRequestWithBody.apply(Unirest.patch(context.getUrl()), context));

            handlers.put("get", context -> sendUnirestRequestWithoutBody.apply(Unirest.get(context.getUrl()), context));
            handlers.put("head", context -> sendUnirestRequestWithoutBody.apply(Unirest.head(context.getUrl()), context));
            handlers.put("options", context -> sendUnirestRequestWithoutBody.apply(Unirest.options(context.getUrl()), context));

            return handlers;
        }

        @Bean
        String cucumberHttpBaseUrl(
                @Autowired(required = false)
                @Qualifier("baseUrl")
                        Supplier<String> baseUrl){

            return Optional.ofNullable(baseUrl).map(Supplier::get).orElse(DEFAULT_BASE_URL);
        }

    }

    @Autowired CucumberHttpContext cucumberHttpContext;
    @Autowired String cucumberHttpBaseUrl;
    @Autowired Map<String, HttpRequestHandler<HttpResponse<String>>> unirestHttpHandlers;

    String appUrl(String uri) {
        return cucumberHttpBaseUrl + uri;
    }

    @When("^the request is sent to '(.*)'$")
    public void sendRequest(String uri) {

        cucumberHttpContext.setUrl(appUrl(uri));

        String requestMethod = cucumberHttpContext.getRequestMethod();

        Optional<HttpRequestHandler<HttpResponse<String>>> handler = Optional.ofNullable(unirestHttpHandlers.get(requestMethod.toLowerCase()));
        handler.orElseThrow(() -> new RuntimeException(String.format("No handler found for '%s' method", requestMethod)));

        HttpResponse<String> response = handler.map(h -> h.handle(cucumberHttpContext)).orElse(null);

        cucumberHttpContext.setHttpResponse(response);
    }

}
