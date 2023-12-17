import java.util.ArrayList;
import java.util.List;

public class Movie {
    private String title;
    private String id;
    private int year;
    private String director;
    List<String> genres = new ArrayList<>();

    public Movie(){}

    public Movie(String title, String id, int year, String director, List<String> genres) {

        this.title = title;
        this.id = id;
        this.year = year;
        this.director = director;
        this.genres.addAll(genres);
    }
    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public List<String> getGenres() {
        return genres;
    }

    public String toString() {

        return "Title:" + getTitle() + ", " +
                "ID:" + getId() + ", " +
                "Year:" + getYear() + ", " +
                "Director:" + getDirector() + ", " +
                "Genres:" + getGenres() + ", ";
    }

    public void setTitle(String title){ this.title = title; }

    public void setId(String id){ this.id = id; }

    public void setYear(int year){ this.year = year; }

    public void setDirector(String director){ this.director = director; }

    public void addGenre(String genre){
        genres.add(genre);
    }
}
