package io.github.brobert83.cucumber_http_java8;

import kong.unirest.HttpResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CucumberHttpContext {

    private Data data;

    public void newContext(){
        data = new Data();
    }

    public String getRequestMethod() {
        return data.getRequestMethod();
    }

    public String getRequestBody() {
        return data.getRequestBody();
    }

    public String getUrl() {
        return data.getUrl();
    }

    public void setHttpResponse(HttpResponse<String> httpResponse) {
        data.setHttpResponse(httpResponse);
    }

    public Map<String, String> getRequestHeaders() {
        return data.getRequestHeaders();
    }

    public HttpResponse<String> getHttpResponse() {
        return data.getHttpResponse();
    }

    public void setRequestMethod(String requestMethod) {
        data.setRequestMethod(requestMethod);
    }

    public void setRequestBody(String requestBody) {
        data.setRequestBody(requestBody);
    }

    public void setUrl(String url) {
        data.setUrl(url);
    }

    @Getter
    @Setter
    static class Data {
        private String requestMethod;
        private String requestBody;
        private String url;

        private Map<String, String> requestHeaders = new HashMap<>();

        private HttpResponse<String> httpResponse;
    }

}
