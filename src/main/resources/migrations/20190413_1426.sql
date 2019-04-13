--liquibase formatted sql
--changeset db:1
CREATE EXTENSION cube;

CREATE EXTENSION earthdistance;

ALTER TABLE flight
  ADD COLUMN distance INTEGER;

UPDATE flight
  SET distance = earth_distance(ll_to_earth(sa.latitude, sa.longitude), ll_to_earth(da.latitude, da.longitude)) / 1000
  FROM airport sa, airport da WHERE flight.source = sa.iata AND flight.destination = da.iata;

DELETE FROM flight
WHERE distance IS NULL OR source = destination;
