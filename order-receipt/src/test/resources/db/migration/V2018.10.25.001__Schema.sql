CREATE TABLE event_store (
  event_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  timestamp TIMESTAMP NOT NULL,
  handle VARCHAR(64) NOT NULL,
  aggregate_id VARCHAR(64) NOT NULL,
  payload CLOB NOT NULL
);

CREATE INDEX event_store_timestamp_idx ON event_store(timestamp);
CREATE INDEX event_store_handle_idx ON event_store(handle);
CREATE INDEX event_store_aggregate_idx ON event_store(aggregate_id);

COMMIT;