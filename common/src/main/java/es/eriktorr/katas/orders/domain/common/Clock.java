package es.eriktorr.katas.orders.domain.common;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Clock {

    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    public Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

}