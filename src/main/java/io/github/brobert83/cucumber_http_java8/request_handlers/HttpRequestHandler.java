package io.github.brobert83.cucumber_http_java8.request_handlers;

import io.github.brobert83.cucumber_http_java8.CucumberHttpContext;

public interface HttpRequestHandler<T> {

    T handle(CucumberHttpContext context);

}
