package io.github.brobert83.cucumber_http_java8.request_handlers.unirest.common;

import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import io.github.brobert83.cucumber_http_java8.HttpRequestStepsContext;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

public class SendUnirestRequestWithBody implements BiFunction<HttpRequestWithBody, HttpRequestStepsContext, HttpResponse<String>>{

    @Override
    public HttpResponse<String> apply(HttpRequestWithBody requestWithBody, HttpRequestStepsContext context) {

        AtomicReference<HttpRequestWithBody> request = new AtomicReference<>(requestWithBody);

        context.getRequestHeaders().forEach((headerName, headerValue) -> request.getAndUpdate(p -> p.header(headerName, headerValue)));

        HttpRequestWithBody requestWithHeaders = request.get();

        return Optional.ofNullable(context.getRequestBody())
                .map(body -> requestWithHeaders.body(body).asString())
                .orElse(requestWithHeaders.asString());
    }

}
