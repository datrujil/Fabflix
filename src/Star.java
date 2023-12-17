import java.util.ArrayList;
import java.util.List;

public class Star{
    private String stageName;   // This attribute will allow us to tie casts xml file with actors xml file
    private String movieId;   // Movie ID this actor acted in
    private String id;
    private String name;
    private int birthYear;
    List<String> moviesIdActed = new ArrayList<>();

    public Star(){}

    public Star(String id, String name, int birthYear) {
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
    }
    public String getStageName() {
        return stageName;
    }
    public String getMovieId() {
        return movieId;
    }
    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }
    public int getBirthYear() {
        return birthYear;
    }
    public List<String> getMoviesIdActed() {
        return moviesIdActed;
    }

    public String toString() {

        return "Stage Name:" + getStageName() + ", " +
                "Name:" + getName() + ", " +
                "Id:" + getId() + ", " +
                "Birth Year:" + getBirthYear() + ", " +
                "Movies:" + getMoviesIdActed();
    }

    public void setStageName(String stageName){ this.stageName = stageName; }

    public void setMovieId(String movieId){ this.movieId = movieId; }
    public void setName(String name){
        if (this.name == null){
            this.name = name;
        } else {
            this.name = name + ' ' + this.name;
        }
    }
    public void setId(String id){ this.id = id; }
    public void setBirthYear(int birthYear){ this.birthYear = birthYear; }

    public void addMovieId(String movieId){
        moviesIdActed.add(movieId);
    }

    // Merge stars with the same stage name and add to the list of movies the star has acted in
    public static Star mergeStageAndStar(Star stage, Star star){
        star.addMovieId(stage.getMovieId());
        return star;
    }
}