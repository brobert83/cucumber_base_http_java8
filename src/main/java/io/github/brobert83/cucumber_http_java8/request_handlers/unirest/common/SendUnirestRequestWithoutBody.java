package io.github.brobert83.cucumber_http_java8.request_handlers.unirest.common;

import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import io.github.brobert83.cucumber_http_java8.CucumberHttpContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

@Component
public class SendUnirestRequestWithoutBody implements BiFunction<GetRequest, CucumberHttpContext, HttpResponse<String>> {

    // Unirest uses 'GetRequest' for all request types without a body
    @Override
    public HttpResponse<String> apply(GetRequest getRequest, CucumberHttpContext context) {

        AtomicReference<GetRequest> request = new AtomicReference<>(getRequest);

        context.getRequestHeaders().forEach((headerName, headerValue) -> request.getAndUpdate(p -> p.header(headerName, headerValue)));

        return request.get().asString();
    }

}
