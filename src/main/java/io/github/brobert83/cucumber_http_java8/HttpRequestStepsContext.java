package io.github.brobert83.cucumber_http_java8;

import kong.unirest.HttpResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class HttpRequestStepsContext {

    private String requestMethod;
    private String requestBody;
    private String url;

    private Map<String,String> requestHeaders = new HashMap<>();
    private Map<String,String> responseHeaders = new HashMap<>();

    private HttpResponse<String> httpResponse;

}
