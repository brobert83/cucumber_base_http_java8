package io.github.brobert83.cucumber_http_java8;

import io.github.brobert83.cucumber_http_java8.request_handlers.HttpRequestHandler;
import io.github.brobert83.cucumber_http_java8.request_handlers.unirest.common.SendUnirestRequestWithBody;
import io.github.brobert83.cucumber_http_java8.request_handlers.unirest.common.SendUnirestRequestWithoutBody;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
public class CucumberHttpSpringConfig {

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
    SendUnirestRequestWithBody sendUnirestRequestWithBody() {
        return new SendUnirestRequestWithBody();
    }

    @Bean
    SendUnirestRequestWithoutBody sendUnirestRequestWithoutBody() {
        return new SendUnirestRequestWithoutBody();
    }

    @Bean
    CucumberHttpContext httpRequestStepsContext() {
        return new CucumberHttpContext();
    }

}
