package es.eriktorr.katas.orders;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features", plugin = { "pretty" })
public class OrderReceiptFeatureTest {

}