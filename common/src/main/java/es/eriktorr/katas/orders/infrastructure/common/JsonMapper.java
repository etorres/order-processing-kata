package es.eriktorr.katas.orders.infrastructure.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

class JsonMapper<T> {

    private final Class<T> valueType;
    private final ObjectMapper objectMapper;

    JsonMapper(Class<T> valueType, ObjectMapper objectMapper) {
        this.valueType = valueType;
        this.objectMapper = objectMapper;
    }

    String toJson(T object) {
        if (object == null) return null;
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("failed to serialize object to JSON: " + object, e);
        }
    }

    T fromJson(String payload) {
        if (payload == null) return null;
        try {
            return objectMapper.readValue(payload, valueType);
        } catch (IOException e) {
            throw new IllegalStateException("failed to deserialize payload: " + payload, e);
        }
    }

}