package es.eriktorr.katas.orders.end_to_end;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features", plugin = { "pretty" })
public class OrderReceiptFeatureTest {

}