use moviedb;
drop procedure add_movie;
DELIMITER //

CREATE PROCEDURE add_movie (
    IN in_title VARCHAR(100), 
    IN in_year INTEGER, 
    IN in_director VARCHAR(100),
    IN in_starName VARCHAR(100),
    IN in_genreName VARCHAR(32)
)
main: BEGIN
    DECLARE new_movie_id VARCHAR(10);
    DECLARE new_star_id VARCHAR(10);
    DECLARE existing_star_id VARCHAR(10);
    DECLARE new_genre_id INTEGER;
    DECLARE existing_genre_id INTEGER;

    -- Check if the movie already exists
    IF EXISTS (SELECT id FROM movies WHERE title = in_title AND year = in_year AND director = in_director) THEN
        SELECT 'Movie already exists.' AS message;
        LEAVE main;
    END IF;

    -- Generate new movie ID
    SELECT CONCAT('tt',  SUBSTRING(max(id), 3)+1) INTO new_movie_id FROM movies;
    INSERT INTO movies (id, title, year, director) VALUES (new_movie_id, in_title, in_year, in_director);

    -- Check if the star exists
    SELECT id INTO existing_star_id FROM stars WHERE name = in_starName;
    IF existing_star_id IS NULL THEN
        -- Generate new star ID
        select max(id) into existing_star_id from stars;
        SELECT CONCAT('nm', SUBSTRING(existing_star_id, 3)+1) INTO new_star_id;
        INSERT INTO stars (id, name, birthYear) VALUES (new_star_id, in_starName, NULL);
        SET existing_star_id = new_star_id;
    END IF;

    -- Check if the genre exists
    SELECT id INTO existing_genre_id FROM genres WHERE name = in_genreName;
    IF existing_genre_id IS NULL THEN
		SELECT MAX(id)+1 FROM genres INTO new_genre_id;
        INSERT INTO genres (id,name) VALUES (new_genre_id,in_genreName);
        SET existing_genre_id = new_genre_id;
    END IF;

    -- Link star to movie
    INSERT INTO stars_in_movies (starId, movieId) VALUES (existing_star_id, new_movie_id);

    -- Link genre to movie
    INSERT INTO genres_in_movies (genreId, movieId) VALUES (existing_genre_id, new_movie_id);

    SELECT 'Movie, star, and genre added/linked successfully.' AS message;
END main //

DELIMITER ;
