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

import static io.github.brobert83.cucumber_http_java8.steps.target_setup.SetupTargetStep.DEFAULT_TARGET_NAME;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class SendHttpRequestStep {

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

    }

    @Autowired CucumberHttpContext cucumberHttpContext;
    @Autowired Map<String, HttpRequestHandler<HttpResponse<String>>> unirestHttpHandlers;
    @Autowired @Qualifier("targets") Map<String, String> targets;

    @When("^the request is sent to server '(.*)' with path '(.*)'$")
    public void sendRequestAtTarget(String target, String uri) {
        send(target, uri);
    }

    @When("^the request is sent to '(.*)'$")
    public void sendRequest(String uri) {
        send(DEFAULT_TARGET_NAME, uri);
    }

    private void send(String target, String uri) {

        String baseUrl = targets.get(target);

        Optional.ofNullable(baseUrl).orElseThrow(() -> new RuntimeException(String.format("Server '%s' is not defined", target)));//todo: improve this error message

        cucumberHttpContext.setUrl(baseUrl + uri);

        String requestMethod = cucumberHttpContext.getRequestMethod();

        Optional<HttpRequestHandler<HttpResponse<String>>> handler = Optional.ofNullable(unirestHttpHandlers.get(requestMethod.toLowerCase()));
        handler.orElseThrow(() -> new RuntimeException(String.format("No handler found for '%s' method", requestMethod)));

        HttpResponse<String> response = handler.map(h -> h.handle(cucumberHttpContext)).orElse(null);

        cucumberHttpContext.setHttpResponse(response);
    }

}
