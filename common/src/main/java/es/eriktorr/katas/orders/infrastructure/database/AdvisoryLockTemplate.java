package es.eriktorr.katas.orders.infrastructure.database;

import java.util.function.Supplier;

abstract class AdvisoryLockTemplate {

    private static final long MAGIC_PRIME = 2223243435546756677L;

    static final String LOCK_SQL = "SELECT pg_advisory_lock(%d)";
    static final String UNLOCK_SQL = "SELECT pg_advisory_unlock(%d)";

    private final long lockKey;

    AdvisoryLockTemplate(int discriminator) {
        this.lockKey = MAGIC_PRIME + discriminator;
    }

    <T> T execute(Supplier<T> supplier) {
        try {
            lock(lockKey);
            return supplier.get();
        } finally {
            unlock(lockKey);
        }
    }

    abstract void lock(long lockKey);

    abstract void unlock(long lockKey);

}