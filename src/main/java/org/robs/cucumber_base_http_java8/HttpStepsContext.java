package org.robs.cucumber_base_http_java8;

import kong.unirest.HttpResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Getter
@Setter
public class HttpStepsContext {

    private String requestMethod;
    private String requestBody;
    private String contentType;
    private String url;

    private HttpResponse<String> httpResponse;

    private Map<String, Function<HttpStepsContext, HttpResponse<String>>> httpMethodHandlers = new HashMap<>();

}
