package es.eriktorr.katas.orders.end_to_end;

import ch.qos.logback.classic.util.ContextInitializer;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features", plugin = { "pretty" })
public class OrderReceiptFeatureTest {

    @BeforeClass
    public static void setUp() {
        System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "logback-cucumber.xml");
    }

}