package es.eriktorr.katas.orders.end_to_end;

import cucumber.api.TypeRegistry;
import cucumber.api.TypeRegistryConfigurer;
import io.cucumber.cucumberexpressions.ParameterType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static java.util.Locale.ENGLISH;

public class TypeRegistryConfiguration implements TypeRegistryConfigurer {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public Locale locale() {
        return ENGLISH;
    }

    @Override
    public void configureTypeRegistry(TypeRegistry typeRegistry) {
        typeRegistry.defineParameterType(new ParameterType<>(
                "storeId",
                "\\d{2}-\\d{3}-\\d{3}",
                String.class,
                (String value) -> value)
        );
        typeRegistry.defineParameterType(new ParameterType<>(
                "localDateTime",
                "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{9}",
                LocalDateTime.class,
                (String value) -> LocalDateTime.parse(value, DEFAULT_FORMATTER))
        );
    }

}