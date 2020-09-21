package org.robs.cucumber_base_http_java8.request_handlers;

import org.robs.cucumber_base_http_java8.HttpRequestStepsContext;

public interface HttpRequestHandler<T> {

    T handle(HttpRequestStepsContext context);

}
