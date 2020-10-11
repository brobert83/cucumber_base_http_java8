package io.github.brobert83.cucumber_http_java8.assertions;

import io.github.brobert83.cucumber_http_java8.support.JsonSupport;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;
import org.xmlunit.diff.ElementSelectors;

import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@AllArgsConstructor
@Component
public class GenericAssertions {

    @Configuration
    public static class SpringConfig {

        @Bean
        public BiFunction<String, String, Diff> xmlDiff() {

            return (actual, expected) -> DiffBuilder.compare(expected)
                    .withTest(actual)
                    .ignoreWhitespace()
                    .ignoreComments()
                    .checkForSimilar()
                    .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))
                    .build();
        }

    }

    private final JsonSupport jsonSupport;
    private final BiFunction<String, String, Diff> xmlDiff;

    public void matchText(String actualBody, String expectedBody) {
        assertThat(actualBody).isEqualTo(expectedBody);
    }

    public void matchJson(String actualBody, String expectedBody) {
        jsonSupport.processJson(actualBody, expectedBody, (bodyJson, expectedJson) -> assertThat(bodyJson).isEqualTo(expectedJson));
    }

    public void matchXml(String actualBody, String expectedBody) {

        Diff diff = xmlDiff.apply(actualBody,expectedBody);

        if (diff.hasDifferences()) {
            String problems = StreamSupport.stream(diff.getDifferences().spliterator(), false)
                    .map(Difference::toString)
                    .collect(Collectors.joining("\n"));
            fail("XML content does not match:\n" + problems);
        }
    }

}
