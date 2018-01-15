CREATE DATABASE IF NOT EXISTS movie;

DROP TABLE IF EXISTS movie.ratings;
CREATE TABLE If NOT EXISTS movie.ratings (
  user_id bigint,
  movie_id bigint,
  rating double,
  ts timestamp
);

DROP TABLE IF EXISTS movie.tags;
CREATE TABLE If NOT EXISTS movie.tags (
  user_id bigint,
  movie_id bigint,
  tag string,
  ts timestamp
);


DROP TABLE IF EXISTS movie.movies;
CREATE TABLE If NOT EXISTS movie.movies (
  movie_id bigint,
  title string,
  genres array<string>
);


DROP TABLE IF EXISTS movie.links;
CREATE TABLE If NOT EXISTS movie.links (
  movie_id bigint,
  url_ml string,
  url_im string,
  url_tm string
);
