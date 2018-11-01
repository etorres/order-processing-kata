package es.eriktorr.katas.orders.infrastructure.common;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JsonMapper<T> {

    protected final ObjectMapper objectMapper;

    protected JsonMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy();
    }

    public abstract String toJson(T object);

    public abstract T fromJson(String payload);

}