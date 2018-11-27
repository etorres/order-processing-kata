CREATE TABLE event_store (
  event_id BIGSERIAL NOT NULL,
  timestamp TIMESTAMP NOT NULL,
  handle VARCHAR(64) NOT NULL,
  aggregate_id VARCHAR(64) NOT NULL,
  payload JSONB NOT NULL,
  PRIMARY KEY (event_id)
);

CREATE INDEX event_store_timestamp_idx ON event_store(timestamp);
CREATE INDEX event_store_handle_idx ON event_store(handle);
CREATE INDEX event_store_aggregate_idx ON event_store(aggregate_id);

CREATE TABLE orders (
  id VARCHAR(36) NOT NULL,
  store VARCHAR(36) NOT NULL,
  reference VARCHAR(36) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  PRIMARY KEY (id, store)
);

CREATE INDEX orders_created_at_idx ON orders(created_at);
CREATE INDEX orders_store_idx ON orders(store);

COMMIT;