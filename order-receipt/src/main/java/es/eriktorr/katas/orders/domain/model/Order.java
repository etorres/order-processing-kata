package es.eriktorr.katas.orders.domain.model;

import lombok.Value;

import java.util.UUID;

@Value
public class Order {

    private final UUID uuid;

}