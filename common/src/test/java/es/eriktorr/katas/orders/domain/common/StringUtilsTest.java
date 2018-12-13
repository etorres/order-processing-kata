package es.eriktorr.katas.orders.domain.common;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.stream.Stream;

import static es.eriktorr.katas.orders.domain.common.StringUtils.trimJsonToEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("Null-safe String operations")
class StringUtilsTest {

    @TestFactory
    @DisplayName("Trim JSON payload to empty")
    Stream<DynamicTest> trimJsonPayloadToEmpty() {
        return Stream.of(
                new SimpleImmutableEntry<>((String) null, "{}"),
                new SimpleImmutableEntry<>("", "{}"),
                new SimpleImmutableEntry<>("     ", "{}"),
                new SimpleImmutableEntry<>("{abc}", "{abc}"),
                new SimpleImmutableEntry<>("    {abc}    ", "{abc}"),
                new SimpleImmutableEntry<>("    {  abc }    ", "{  abc }")
        ).map(sampleCase -> dynamicTest(String.format("trimJsonToEmpty(%s)=%s", sampleCase.getKey(), sampleCase.getValue()), () -> {
            val trimmedJsonPayload = trimJsonToEmpty(sampleCase.getKey());
            assertThat(trimmedJsonPayload).isEqualTo(sampleCase.getValue());
        }));
    }

}