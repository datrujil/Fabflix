package edu.uci.ics.fabflixmobile.data.model;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;


import androidx.annotation.NonNull;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie {
    private final String id;
    private final String title;
    private final int year;
    private final String director;
    private final double rating;
    private final String genres;
    private final String stars;

    public Movie(String id, String name, int year, String director, double rating, String genres, String stars) {
        this.id = id;
        this.title = name;
        this.year = year;
        this.director = director;
        this.rating = rating;
        this.genres = genres;
        this.stars = stars;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public double getRating() {
        return rating;
    }

    public String getGenresString()
    {
        StringBuilder genresStr = new StringBuilder();
        String[] temp =  this.genres.split(",");
        for (int i = 0; i < (Math.min(temp.length, 3)); i++) {
            genresStr.append(temp[i]).append(", ");
        }
        return genresStr.substring(0, Math.max(genresStr.length() - 2, 0));
    }
    public String getStarsString()
    {
        StringBuilder starsStr = new StringBuilder();
        String[] temp =  this.stars.split(",");
        for (int i = 0; i < (Math.min(temp.length, 3)); i++) {
            starsStr.append(temp[i]).append(", ");
        }
        return starsStr.substring(0, Math.max(starsStr.length() - 2, 0));
    }

    public String getAllGenre()
    {
        return this.genres;
    }

    public String getAllStars()
    {
        return this.stars;
    }



    @NonNull
    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", name='" + title + '\'' +
                ", year=" + year +
                ", director='" + director + '\'' +
                ", rating=" + rating +
                '}';
    }
}