--liquibase formatted sql
--changeset db:1
CREATE TABLE airport (
  iata TEXT UNIQUE,
  icao TEXT UNIQUE,
  name TEXT,
  latitude DECIMAL NOT NULL,
  longitude DECIMAL NOT NULL
);

CREATE TABLE flight (
   source TEXT NOT NULL,
   destination TEXT NOT NULL
);
