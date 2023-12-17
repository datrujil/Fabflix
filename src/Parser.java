import java.sql.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Parser implements Runnable{
    private int insertedGenres = 0;
    private int insertedGenresInMovies = 0;
    private int insertedStarsInMovies = 0;
    private int missingStars = 0;
    private int insertedStars = 0;
    private int insertedMovies = 0;
    private int duplicateStars = 0;
    private int duplicateMovies = 0;
    private int inconsistentMovies = 0;
    private int moviecount = 0;

    private List<Movie> newMovieData;
    private List<Star> newStageData;
    private List<Star> newStarData;

    private final Connection connection;

    // Constructor to receive the Connection
    public Parser(Connection connection) {
        this.connection = connection;
    }

    public static void main(String[] args) {
        String username = "mytestuser";
        String password = "My6$Password";
        String url = "jdbc:mysql://localhost:3306/moviedb";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                Parser parser = new Parser(conn);

                // Create a Thread and start it
                Thread thread = new Thread(parser);
                thread.start();

                // Optionally, you can wait for the thread to finish using thread.join()
                thread.join();
            }
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException | InterruptedException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
    }

    private void parseAndInsertData(Connection connection) throws SQLException {
        // SAX PARSE THE XML FILES
        MovieSAXParser msp = new MovieSAXParser();
        msp.runExample();
        newMovieData = msp.getData();
        StageSAXParser stsp = new StageSAXParser();
        stsp.runExample();
        newStageData = stsp.getData();
        StarSAXParser spe = new StarSAXParser();
        spe.runExample();
        newStarData = spe.getData();

        // Merge Stage and Star data in parallel
        mergeStageAndStarData();

        // Use ExecutorService to parallelize database inserts
        ExecutorService threadPool = Executors.newFixedThreadPool(5);

        // Submit tasks for movie and genre insertion
        threadPool.submit(() -> insertMovieAndGenreData(connection));
        // Submit tasks for star insertion
        threadPool.submit(() -> insertStarData(connection));

        // Shutdown the thread pool when all tasks are submitted
        threadPool.shutdown();

        try {
            // Wait for all tasks to complete or timeout after a certain period
            threadPool.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            // Handle interruption appropriately
        }

        // Access the counters after processing
        int insertedStars = this.insertedStars;
        int insertedMovies = this.insertedMovies;
        int duplicateStars = this.duplicateStars;
        int duplicateMovies = this.inconsistentMovies;
        int missingStars = this.missingStars;
        int insertedStarsInMovies = this.insertedStarsInMovies;
        int insertedGenres = this.insertedGenres;
        int insertedGenresInMovies = this.insertedGenresInMovies;

        // Use the counters as needed
        System.out.println("Inserted Stars: " + insertedStars);
        System.out.println("Inserted Movies: " + insertedMovies);
        System.out.println("Duplicate Stars: " + duplicateStars);
        System.out.println("Duplicate Movies: " + duplicateMovies);
        System.out.println("Missing Stars: " + missingStars);
        System.out.println("Inserted stars_in_movies: " + insertedStarsInMovies);
        System.out.println("Inserted genres: " + insertedGenres);
        System.out.println("Inserted genres_in_movies: " + insertedGenresInMovies);
    }


    private void insertStarDataIntoDatabase(Connection connection, Star star) throws SQLException {
        if (isStarAlreadyExists(connection, star.getId())) {
            duplicateStars++;
            return;
        }

        // Insert the star as usual
        try (PreparedStatement preparedStatement1 = connection.prepareStatement("INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)")) {
            preparedStatement1.setString(1, star.getId());
            preparedStatement1.setString(2, star.getName());
            preparedStatement1.setInt(3, star.getBirthYear());
            preparedStatement1.executeUpdate();
            insertedStars++;
        } catch (SQLException e){
            missingStars++;
        }

        try (PreparedStatement preparedStatement2 = connection.prepareStatement("INSERT INTO stars_in_movies (starId, movieId) VALUES (?, ?)")) {
            List<String> movieIds = star.getMoviesIdActed();

            for (String movieId : movieIds) {
                // Check if the movieId exists in the movies table
                if (!isMovieAlreadyExists(connection, movieId)) {
                    continue;
                }
                preparedStatement2.setString(1, star.getId());
                preparedStatement2.setString(2, movieId);
                try{
                    preparedStatement2.executeUpdate();
                    insertedStarsInMovies++;
                } catch (SQLException e){
                    missingStars++;
                }
            }
        } catch (SQLException e) {
            missingStars++;
        }
    }

    private void insertMovieDataIntoDatabase(Connection connection, Movie movie) throws SQLException {
        try {
            if (!isMovieAlreadyExists(connection, movie.getId())) {
                try (PreparedStatement preparedStatement3 = connection.prepareStatement(
                        "INSERT INTO movies (id, title, year, director) VALUES (?, ?, ?, ?)")) {

                    preparedStatement3.setString(1, movie.getId());
                    preparedStatement3.setString(2, movie.getTitle());
                    preparedStatement3.setInt(3, movie.getYear());
                    preparedStatement3.setString(4, movie.getDirector());
                    preparedStatement3.executeUpdate();
                    insertedMovies++;
                } catch (SQLException e){
                    inconsistentMovies++;
                }
            }
        } catch (SQLException e) {
            inconsistentMovies++;
            // Rollback transaction or handle the error appropriately
        }
        List<String> genres = movie.getGenres();

        for (String genre : genres){
            try {
                insertGenreDataIntoDatabase(connection, genre, movie.getId());
            } catch (SQLException e){
                inconsistentMovies++;
            }
        }
    }

    private void insertGenreDataIntoDatabase(Connection connection, String genre, String id) throws SQLException{
        try {
            if (!isGenreAlreadyExists(connection, genre)) {
                try (PreparedStatement preparedStatementGenre = connection.prepareStatement(
                        "INSERT INTO genres (name) VALUES (?)")){
                    preparedStatementGenre.setString(1, genre);
                    preparedStatementGenre.executeUpdate();
                    insertedGenres++;
                }
            }
        } catch (SQLException e) {
            inconsistentMovies++;
            // Rollback transaction or handle the error appropriately
        }

        try (PreparedStatement preparedStatement4 = connection.prepareStatement(
                "INSERT INTO genres_in_movies (genreId, movieId) VALUES (?, ?)")) {
            int genreId = getGenreId(connection, genre);
            if (genreId != -1) {
                if (!isGenreInMovieAlreadyExists(connection, genreId, id)){
                    preparedStatement4.setInt(1, genreId);
                    preparedStatement4.setString(2, id);
                    preparedStatement4.executeUpdate();
                    insertedGenresInMovies++;
                } else {
                    inconsistentMovies++;
                }
            } else {
                inconsistentMovies++;
            }
        } catch (SQLException e){
            inconsistentMovies++;
        }
    }

    private boolean isMovieAlreadyExists(Connection connection, String movieId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT 1 FROM movies WHERE id = ? LIMIT 1")) {
            preparedStatement.setString(1, movieId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                boolean exists = resultSet.next();
                if (exists) {
                    inconsistentMovies++;
                }
                return exists;
            }
        }
    }

    private boolean isStarAlreadyExists(Connection connection, String starId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT 1 FROM stars WHERE id = ? LIMIT 1")) {
            preparedStatement.setString(1, starId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                boolean exists = resultSet.next();
                if (exists) {
                    duplicateStars++;
                }
                return exists;
            }
        }
    }

    private boolean isGenreAlreadyExists(Connection connection, String genre) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT 1 FROM genres WHERE name = ? LIMIT 1")) {
            preparedStatement.setString(1, genre);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private boolean isGenreInMovieAlreadyExists(Connection connection, int genreId, String movieId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT 1 FROM genres_in_movies WHERE genreId = ? AND movieId = ? LIMIT 1")) {
            preparedStatement.setInt(1, genreId);
            preparedStatement.setString(2, movieId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private int getGenreId(Connection connection, String genre) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT id FROM genres WHERE name = ? LIMIT 1")) {
            preparedStatement.setString(1, genre);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                } else {
                    // Genre not found
                    return -1; // or another value indicating the absence of the genre
                }
            }
        }
    }

    private void mergeStageAndStarData() {
        // Parallelize the merging of Stage and Star data
        newStarData.parallelStream().forEach(currentStar -> {
            newStageData.parallelStream().filter(currentStage ->
                            currentStage.getStageName().equals(currentStar.getStageName()))
                    .findFirst()
                    .ifPresent(currentStage -> {
                        Star mergedStar = Star.mergeStageAndStar(currentStage, currentStar);
                        // Do something with the mergedStar, e.g., add it to a list
                    });
        });
    }

    private void insertMovieAndGenreData(Connection connection) {
        // Parallelize movie and genre insertion
        newMovieData.parallelStream().forEach(newMovieDatum -> {
            try {
                insertMovieDataIntoDatabase(connection, newMovieDatum);
            } catch (SQLException e) {
                inconsistentMovies++;
            }
            newMovieDatum.getGenres().parallelStream().forEach(genre ->
            {
                try {
                    insertGenreDataIntoDatabase(connection, genre, newMovieDatum.getId());
                } catch (SQLException e) {
                    inconsistentMovies++;
                }
            });
        });
    }

    private void insertStarData(Connection connection) {
        // Parallelize star insertion
        newStarData.parallelStream().forEach(newStarDatum ->
        {
            try {
                insertStarDataIntoDatabase(connection, newStarDatum);
            } catch (SQLException e) {
                missingStars++;
            }
        });
    }


    @Override
    public void run() {
        try {
            parseAndInsertData(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
    }
}