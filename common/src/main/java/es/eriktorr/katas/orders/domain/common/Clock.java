package es.eriktorr.katas.orders.domain.common;

import java.sql.Timestamp;

public class Clock {

    public Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

}