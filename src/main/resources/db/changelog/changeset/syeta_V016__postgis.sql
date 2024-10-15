CREATE EXTENSION postgis;

ALTER TABLE events ADD COLUMN location geography(Point, 4326);

CREATE INDEX idx_event_location ON events USING GIST(location);

