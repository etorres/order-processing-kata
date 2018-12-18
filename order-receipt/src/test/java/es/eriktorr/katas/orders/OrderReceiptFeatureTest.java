package es.eriktorr.katas.orders;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.runner.RunWith;

@Tag("regression")
@DisplayName("Order receipt end-to-end feature")
@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features")
public class OrderReceiptFeatureTest {

}