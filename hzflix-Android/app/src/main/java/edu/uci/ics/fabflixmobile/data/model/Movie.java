package edu.uci.ics.fabflixmobile.data.model;

import java.util.List;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie {
    private final String name;
    private final short year;

    private String director;
    private List<String> genres;
    private List<String> stars;
    private String movieId;

    public Movie(String name, short year,String director, List<String> genres, List<String> stars, String movieId) {
        this.name = name;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
        this.movieId = movieId;
    }

    public String getName() {
        return name;
    }

    public short getYear() {
        return year;
    }

    public String getMovieId() {return movieId;}

    public List<String> getGenres() {
        return genres;
    }

    public List<String> getStars() {
        return stars;
    }

    public String getDirector() {
        return director;
    }
}