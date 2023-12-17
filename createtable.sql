CREATE DATABASE IF NOT EXISTS moviedb;

USE moviedb;

CREATE TABLE movies (
                        id VARCHAR(10) DEFAULT '' NOT NULL PRIMARY KEY,
                        title VARCHAR(100) DEFAULT '' NOT NULL,
                        year INTEGER NOT NULL,
                        director VARCHAR(100) DEFAULT '' NOT NULL
);

CREATE TABLE stars (
                       id VARCHAR(10) DEFAULT '' NOT NULL PRIMARY KEY,
                       name VARCHAR(100) DEFAULT '' NOT NULL,
                       birthYear INTEGER
);

CREATE TABLE stars_in_movies(
                                starId VARCHAR(10) DEFAULT '' NOT NULL,
                                movieId VARCHAR(10) DEFAULT '' NOT NULL,
                                FOREIGN KEY (starId) REFERENCES stars(id),
                                FOREIGN KEY (movieId) REFERENCES movies(id)
);

CREATE TABLE genres(
                       id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(32) DEFAULT '' NOT NULL
);

CREATE TABLE genres_in_movies(
                                 genreId INTEGER NOT NULL,
                                 movieId VARCHAR(10) DEFAULT ''  NOT NULL,
                                 FOREIGN KEY (genreId) REFERENCES genres(id),
                                 FOREIGN KEY (movieId) REFERENCES movies(id)
);

-- create creditcards table before customers table
CREATE TABLE creditcards(
                            id VARCHAR(20) DEFAULT '' NOT NULL PRIMARY KEY,
                            firstName VARCHAR(50) DEFAULT '' NOT NULL,
                            lastName VARCHAR(50) DEFAULT '' NOT NULL,
                            expiration DATE NOT NULL
);

CREATE TABLE customers(
                          id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
                          firstName VARCHAR(50) DEFAULT '' NOT NULL,
                          lastName VARCHAR(50) DEFAULT '' NOT NULL,
                          ccId VARCHAR(20) DEFAULT '' NOT NULL,
                          address VARCHAR(200) DEFAULT '' NOT NULL,
                          email VARCHAR(50) DEFAULT '' NOT NULL,
                          password VARCHAR(20) DEFAULT '' NOT NULL,
                          FOREIGN KEY (ccId) REFERENCES creditcards(id)
);

CREATE TABLE sales(
                      id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
                      customerId INTEGER NOT NULL,
                      movieId VARCHAR(10) DEFAULT '' NOT NULL,
                      saleDate DATE NOT NULL,
                      FOREIGN KEY (customerId) REFERENCES customers(id),
                      FOREIGN KEY (movieId) REFERENCES movies(id)
);

CREATE TABLE ratings(
                        movieId VARCHAR(10) DEFAULT '' NOT NULL,
                        rating FLOAT,
                        numVotes INTEGER,
                        FOREIGN KEY (movieId) REFERENCES movies(id)
);