package es.eriktorr.katas.orders.infrastructure.database;

import org.springframework.jdbc.core.JdbcTemplate;

class JdbcTemplateAdvisoryLock extends AdvisoryLockTemplate {

    private final JdbcTemplate jdbcTemplate;

    JdbcTemplateAdvisoryLock(JdbcTemplate jdbcTemplate, int discriminator) {
        super(discriminator);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    void lock(long lockKey) {
        jdbcTemplate.execute(String.format(LOCK_SQL, lockKey));
    }

    @Override
    void unlock(long lockKey) {
        jdbcTemplate.execute(String.format(UNLOCK_SQL, lockKey));
    }

}