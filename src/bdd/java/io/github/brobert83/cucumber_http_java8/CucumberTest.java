package io.github.brobert83.cucumber_http_java8;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/bdd/resources/features",
        plugin = {"pretty", "html:target/cucumber.html"}
)
public class CucumberTest {

}
