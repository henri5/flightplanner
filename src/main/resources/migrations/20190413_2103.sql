--liquibase formatted sql
--changeset db:1
CREATE TABLE land_connection (
  airport_1 TEXT NOT NULL,
  airport_2 TEXT NOT NULL,
  distance INTEGER NOT NULL,
  UNIQUE (airport_1, airport_2)
);

INSERT INTO land_connection(airport_1, airport_2, distance)
SELECT airport_1, airport_2, distance
FROM (
 SELECT
   a1.iata AS airport_1,
   a2.iata AS airport_2,
   earth_distance(ll_to_earth(a1.latitude, a1.longitude), ll_to_earth(a2.latitude, a2.longitude)) / 1000 AS distance
 FROM airport a1 INNER JOIN airport a2 ON a1.iata <> a2.iata
 --bastard optimization
 WHERE abs(a1.longitude - a2.longitude) < 10
   AND abs(a1.latitude - a2.latitude) < 1) connections
WHERE distance < 100;
