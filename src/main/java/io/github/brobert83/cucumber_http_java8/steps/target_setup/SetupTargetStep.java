package io.github.brobert83.cucumber_http_java8.steps.target_setup;

import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class SetupTargetStep {

    public static final String DEFAULT_TARGET_NAME = "default";

    @Configuration
    @Lazy
    public static class SpringConfig {

        @Bean
        Map<String, String> targets(
                @Value("#{systemProperties['cucumber.http.default.target'] ?: 'http://localhost:8080'}") String defaultTarget) {

            Map<String, String> hosts = new HashMap<>();
            hosts.put(DEFAULT_TARGET_NAME, defaultTarget);
            return hosts;
        }

    }

    @Autowired @Qualifier("targets") Map<String, String> targets;

    @Given("^the target server '(.*)' with base url '(.*)'$")
    public void setTarget(String name, String path) {
        targets.put(name, path);
    }

}
