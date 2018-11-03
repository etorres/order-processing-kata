package es.eriktorr.katas.orders.domain.common;

import java.time.LocalDateTime;

public class Clock {

    public LocalDateTime now() {
        return LocalDateTime.now();
    }

}