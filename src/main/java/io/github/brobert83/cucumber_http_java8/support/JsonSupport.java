package io.github.brobert83.cucumber_http_java8.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@AllArgsConstructor
public class JsonSupport {

    @Configuration
    public static class SpringConfig {

        public final static ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper();

        @Bean
        JsonSupport jsonSupport(
                @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
                @Autowired(required = false)
                        Supplier<ObjectMapper> objectMapperSupplier) {

            return new JsonSupport(
                    Optional.ofNullable(objectMapperSupplier)
                            .map(Supplier::get)
                            .orElse(DEFAULT_OBJECT_MAPPER)
            );
        }
    }

    private final ObjectMapper objectMapper;

    public void processJson(String first, String second, BiConsumer<JsonNode, JsonNode> jsonProcessor) {

        JsonNode firstNode;
        JsonNode secondNode;

        try {
            firstNode = objectMapper.readTree(first);
            secondNode = objectMapper.readTree(second);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not parse json", e);
        }

        jsonProcessor.accept(secondNode, firstNode);
    }

}
