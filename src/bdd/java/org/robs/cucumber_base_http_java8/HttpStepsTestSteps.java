package org.robs.cucumber_base_http_java8;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.cucumber.java.en.Given;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.robs.cucumber_base_http_java8.request_handlers.unirest.HttpUnirestSpringConfig;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.slf4j.LoggerFactory.getLogger;

@ActiveProfiles("cucumber")
@CucumberContextConfiguration
@ContextConfiguration(classes = {
        HttpStepsTestSteps.SpringTestConfig.class,
        HttpUnirestSpringConfig.class
})
public class HttpStepsTestSteps {

    @Profile("cucumber")
    @Configuration
    public static class SpringTestConfig {

        @Bean
        WireMockServer wireMockServer() {

            WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());

            wireMockServer.start();

            logger.info("Started Wiremock server on port: {}", wireMockServer.port());

            WireMock.configureFor("localhost", wireMockServer.port());

            return wireMockServer;
        }

        // ==== REQUIRED BY HttpSteps ====
        @Bean
        Supplier<ObjectMapper> objectMapper() {
            return ObjectMapper::new;
        }

        @Bean
        Supplier<String> baseUrl(WireMockServer wireMockServer) {
            return () -> "http://localhost:" + wireMockServer.port();
        }

    }

    private static final Logger logger = getLogger(HttpStepsTestSteps.class);

    Map<String, EndpointMock> endpointsBuilders = new HashMap<>();

    @Autowired Supplier<ObjectMapper> objectMapper;
    @Autowired WireMockServer wireMockServer;

    @Getter @Setter
    static class EndpointMock {
        String path;
        String method, responseBody;
        Map<String, String> requestHeaders = new HashMap<>();
        Map<String, String> responseHeaders = new HashMap<>();
        int statusCode;
    }

    @Given("^the http mock endpoint '(.*)' for method '(.*)' on path '(.*)'$")
    public void setupMockEndpoint(String endpointName, String method, String path) {

        EndpointMock endpointMock = new EndpointMock();

        endpointsBuilders.put(endpointName, endpointMock);

        endpointMock.setMethod(method);
        endpointMock.setPath(path);
    }

    @Given("^the http mock endpoint '(.*)' expects request header '(.*)'='(.*)'$")
    public void setupEndpointExpectHeader(String endpointName, String headerName, String headerValue) {
        endpointsBuilders.get(endpointName).getRequestHeaders().put(headerName, headerValue);
    }

    @Given("^the http mock endpoint '(.*)' responds with body$")
    public void setupEndpointResponseBodyHeredoc(String endpointName, String responseBody) {
        setupEndpointResponseBody(endpointName, responseBody);
    }

    private void setupEndpointResponseBody(String endpointName, String responseBody) {
        endpointsBuilders.get(endpointName).setResponseBody(responseBody);
    }

    @Given("^the http mock endpoint '(.*)' responds with body '(.*)'$")
    public void setupEndpointResponseBodyParam(String endpointName, String responseBody) {
        setupEndpointResponseBody(endpointName, responseBody);
    }

    @Given("^the http mock endpoint '(.*)' responds with header '(.*)'='(.*)'$")
    public void setupEndpointResponseHeader(String endpointName, String headerName, String headerValue) {
        endpointsBuilders.get(endpointName).getResponseHeaders().put(headerName, headerValue);
    }

    @Given("^the http mock endpoint '(.*)' responds with status code '(.*)'$")
    public void setupEndpointStatusCode(String endpointName, int statusCode) {
        endpointsBuilders.get(endpointName).setStatusCode(statusCode);
    }

    Map<String, Function<String, MappingBuilder>> baseMappingBuilders = new HashMap<String, Function<String, MappingBuilder>>() {{
        put("get", path -> WireMock.get(urlEqualTo(path)));
    }};

    @Given("^the http mock endpoint '(.*)' is made available$")
    public void buildEndpointMock(String endpointName) {

        EndpointMock endpointMock = endpointsBuilders.get(endpointName);

        // Setup request
        // method and path
        AtomicReference<MappingBuilder> mappingBuilder = new AtomicReference<>(baseMappingBuilders.get(endpointMock.getMethod().toLowerCase()).apply(endpointMock.getPath()));

        // request headers
        endpointMock.getRequestHeaders().forEach(
                (headerName, headerValue) -> mappingBuilder.getAndUpdate(
                        builder -> builder.withHeader(headerName, WireMock.equalTo(headerValue))
                )
        );

        // Setup Response
        AtomicReference<ResponseDefinitionBuilder> responseDefinitionBuilder = new AtomicReference<>(aResponse());

        // response status
        responseDefinitionBuilder.getAndUpdate(builder -> builder.withStatus(endpointMock.getStatusCode()));

        // response headers
        endpointMock.getResponseHeaders().forEach((responseHeaderName, responseHeaderValue) -> responseDefinitionBuilder.getAndUpdate(builder -> builder.withHeader(responseHeaderName, responseHeaderValue)));

        // response body
        Optional.ofNullable(endpointMock.getResponseBody()).map(responseBody -> responseDefinitionBuilder.getAndUpdate(builder -> builder.withBody(responseBody)));

        mappingBuilder.getAndUpdate(b -> b.willReturn(responseDefinitionBuilder.get()));

        // make it happen
        stubFor(mappingBuilder.get());
    }

}
