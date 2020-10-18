package io.github.brobert83.cucumber_http_java8.request_handlers.unirest.common;

import io.github.brobert83.cucumber_http_java8.CucumberHttpContext;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

@Component
public class SendUnirestRequestWithBody implements BiFunction<HttpRequestWithBody, CucumberHttpContext, HttpResponse<String>> {

    @Override
    public HttpResponse<String> apply(HttpRequestWithBody requestWithBody, CucumberHttpContext context) {

        AtomicReference<HttpRequestWithBody> request = new AtomicReference<>(requestWithBody);

        context.getRequestHeaders().forEach((headerName, headerValue) -> request.getAndUpdate(p -> p.header(headerName, headerValue)));

        HttpRequestWithBody requestWithHeaders = request.get();

        return Optional.ofNullable(context.getRequestBody())
                .map(body -> requestWithHeaders.body(body).asString())
                .orElseGet(requestWithHeaders::asString);
    }

}
