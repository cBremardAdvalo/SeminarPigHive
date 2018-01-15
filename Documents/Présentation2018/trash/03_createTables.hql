CREATE DATABASE IF NOT EXISTS restaurant;

DROP TABLE IF EXISTS restaurant.reserve;
CREATE TABLE If NOT EXISTS restaurant.reserve (
  air_store_id string,
  hpg_store_id string,
  reserve_datetime timestamp,
  visit_datetime timestamp,
  reserve_visitors int
);



DROP TABLE IF EXISTS restaurant.info;
CREATE TABLE If NOT EXISTS restaurant.info (
  air_store_id string,
  hpg_store_id string,
  genre_name string,
  area_name string,
  latitude double,
  longitude double
);
