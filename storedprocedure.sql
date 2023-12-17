USE moviedb;
DROP PROCEDURE IF EXISTS add_movie;

DROP PROCEDURE IF EXISTS add_star;

-- Drop existing functions
-- Drop existing function
DROP FUNCTION IF EXISTS check_star_name;
DROP FUNCTION IF EXISTS get_next_stars_id;
DROP FUNCTION IF EXISTS get_next_movies_id;


#ADD STAR

DELIMITER $$
CREATE PROCEDURE add_star(
    IN star_name VARCHAR(100),
    IN star_year INT,
    OUT star_id VARCHAR(10),
    OUT message VARCHAR(100)	)
BEGIN
    SELECT check_star_name(star_name) INTO star_id; # don't have to check for add-star but need for add movie
    IF star_id IS NULL THEN
        SELECT get_next_stars_id() INTO star_id;
        INSERT INTO stars(id, name, birthYear)
        VALUES(star_id, star_name, NULL);
        SET message = CONCAT('. New star inserted. Star ID: ', star_id);
    ELSE
        IF star_year IS NOT NULL THEN
            SELECT get_next_stars_id() INTO star_id;
            INSERT INTO stars(id, name, birthYear)
            VALUES(star_id, star_name, star_year);
            SET message = CONCAT('New star is inserted. Star ID: ', star_id);
        ELSE
            SET message = CONCAT('The star is already in database. Star ID: ', star_id);
        END IF;
    END IF;
END $$
DELIMITER ;

DELIMITER $$
CREATE FUNCTION check_star_name(star_name VARCHAR(100))
    RETURNS VARCHAR(10)
    READS SQL DATA
BEGIN
    DECLARE star_id VARCHAR(10);

    SELECT id
    INTO star_id
    FROM stars
    WHERE name = star_name
    LIMIT 1;

    RETURN star_id;
END $$
DELIMITER ;

DELIMITER $$

CREATE FUNCTION get_next_stars_id()
    RETURNS VARCHAR(10)
    READS SQL DATA
BEGIN
    DECLARE last_id VARCHAR(10);
    SELECT MAX(id) INTO last_id FROM stars;
    #check it from the third char since first 2 chars are nm
    RETURN CONCAT('nm', COALESCE(CAST(SUBSTRING(last_id, 3) AS UNSIGNED), 0) + 1);
END $$

DELIMITER ;

# LINK STAR


DELIMITER $$
CREATE FUNCTION get_next_movies_id()
    RETURNS VARCHAR(10)
    READS SQL DATA
BEGIN
    DECLARE last_id VARCHAR(10);
    SELECT MAX(id) INTO last_id FROM movies;
    # Check it from the third char since first 2 chars are tt
    RETURN CONCAT('tt', COALESCE(CAST(SUBSTRING(last_id, 3) AS UNSIGNED), 0) + 1);
END $$

DELIMITER ;

#ADD GENRE
#LINK GENRE

DELIMITER $$

CREATE PROCEDURE add_movie (
    IN movieTitle VARCHAR(100),
    IN movieYear INTEGER,
    IN movieDirector VARCHAR(100),
    IN starName VARCHAR(100),
    IN starYear INTEGER,
    IN genreName VARCHAR(32),
    OUT movie_message VARCHAR(500)
)
BEGIN
    -- Declare variables
    DECLARE movieId VARCHAR(10);
    DECLARE starId VARCHAR(10);
    DECLARE genreId INT;
    DECLARE star_message VARCHAR(100);
#     DECLARE genre_message VARCHAR(100);

    -- Check if movie exists
    IF ((SELECT COUNT(*) FROM movies WHERE title = movieTitle AND year = movieYear AND director = movieDirector) > 0) THEN
        SET movie_message = ('FAIL TO ADD: Movie already exists.');
    END IF;

    -- Generate movie ID
    SELECT get_next_movies_id() INTO movieId;
    -- Insert into movies table
    INSERT INTO movies(id, title, year, director) VALUES (movieId, movieTitle, movieYear, movieDirector);

    -- Check if genre exists
    IF ((SELECT COUNT(*) FROM genres WHERE name = genreName) = 0) THEN
        -- Create genre
        SET genreId = (SELECT MAX(id) FROM genres) + 1;
        INSERT INTO genres(id, name) VALUES (genreId, genreName);
    ELSE
        -- Retrieve existing genre ID
        SET genreId = (SELECT id FROM genres WHERE name = genreName LIMIT 1);
    END IF;

    -- Link genre to movie
    INSERT INTO genres_in_movies(genreId, movieId) VALUES (genreId, movieId);

    -- Star
    -- Check if star exists
    IF ((SELECT COUNT(*) FROM stars WHERE name = starName) = 0) THEN
        -- Create star
        SELECT get_next_stars_id() INTO starId;
        CALL add_star(starName, starYear, starId, star_message);
#         INSERT INTO stars(id, name, birthYear) VALUES (starId, starName, NULL);
    ELSE
        -- Retrieve existing star ID
        SET starId = (SELECT id FROM stars WHERE name = starName LIMIT 1);
    END IF;

    -- Link star to movie
    INSERT INTO stars_in_movies(starId, movieId) VALUES (starId, movieId);

    -- Output success message
    SET movie_message = CONCAT('ADD SUCCESSFULLY: MovieId : ', movieId, ', StarId : ', starId, ', Genre Id : ', genreId);

END $$

DELIMITER ;

