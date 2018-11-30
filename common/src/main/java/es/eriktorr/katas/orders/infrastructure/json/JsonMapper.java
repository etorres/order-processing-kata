package es.eriktorr.katas.orders.infrastructure.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonMapper<T> {

    private final ObjectMapper objectMapper;

    public JsonMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toJson(T object) {
        if (object == null) return null;
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("failed to serialize object to JSON: " + object, e);
        }
    }

    public T fromJson(String payload, Class<T> valueType) {
        if (payload == null) return null;
        try {
            return objectMapper.readValue(payload, valueType);
        } catch (IOException e) {
            throw new IllegalStateException("failed to deserialize payload: " + payload, e);
        }
    }

}