package io.github.brobert83.cucumber_http_java8.request_handlers;

import io.github.brobert83.cucumber_http_java8.HttpRequestStepsContext;

public interface HttpRequestHandler<T> {

    T handle(HttpRequestStepsContext context);

}
