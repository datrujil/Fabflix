import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.ArrayList;


// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "GenresServlet", urlPatterns = "/api/single-genre")
public class GenresServlet extends HttpServlet {

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long servletStartTime = System.nanoTime();
        long jdbcStartTime = 0;
        long jdbcEndTime = 0;

        response.setContentType("application/json");

        // Pagination parameters
        String pageParam = request.getParameter("page");
        String pageSizeParam = request.getParameter("pageSize");
        int page = 1; // Default page number
        int pageSize = 10; // Default page size
        if (pageParam != null && !pageParam.isEmpty()) {
            page = Integer.parseInt(pageParam);
        }
        if (pageSizeParam != null && !pageSizeParam.isEmpty()) {
            pageSize = Integer.parseInt(pageSizeParam);
        }
        // Calculate the offset based on page and pageSize
        int offset = (page - 1) * pageSize;

        // Search parameters
        String gid = request.getParameter("genreId");
        String startWith = request.getParameter("startWith");
        String inputTitle = request.getParameter("byTitle");
        String inputYearString = request.getParameter("byYear");
        int inputYear = -1;
        if (inputYearString != null && inputYearString != "") { inputYear = Integer.parseInt(inputYearString); }
        String inputDirector = request.getParameter("byDirector");

        String inputStar = request.getParameter("byStar");

        String firstChoice = request.getParameter("firstChoice");
        String firstPriorityType = request.getParameter("firstPriorityType");
        String secondChoice = request.getParameter("secondChoice");
        String secondPriorityType = request.getParameter("secondPriorityType");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            String whereClause = "";
            String orderBy = "";

            // First sort criterion
            if ((firstChoice != null && firstPriorityType != null) && (!firstChoice.isEmpty() && !firstPriorityType.isEmpty())) {
                orderBy += " ORDER BY ";
                if (firstChoice.equals("rating")) {
                    orderBy += "ratings.rating ";
                }
                else {
                    orderBy += "movies.title ";
                }

                if (firstPriorityType.equals("ascending")) {
                    orderBy += "ASC, ";
                } else {
                    orderBy += "DESC, ";
                }
            }

            // Second sort criterion
            if ((secondChoice != null && secondPriorityType != null) && (!secondChoice.isEmpty() && !secondPriorityType.isEmpty())) {
                if (secondChoice.equals("rating")) {
                    orderBy += "ratings.rating ";
                }
                else {
                    orderBy += "movies.title ";
                }

                if (secondPriorityType.equals("ascending")) {
                    orderBy += "ASC";
                } else {
                    orderBy += "DESC";
                }
            }
            System.out.println("orderBy:" + orderBy);



            String para = ""; // this can be gid or letter

            PreparedStatement statement;

            //SEARCH
            if (((gid == null || gid == "") && (startWith == null || startWith == "")) && (inputTitle != null || inputYearString != null || inputDirector != null || inputStar != null)){
                String titleCondition = "";
                String yearCondition = "";
                String directorCondition = "";
                String starCondition = "";

                ArrayList<String> searchCondition = new ArrayList<>();
                int conditionCount = 0;
                String[] words = inputTitle.split(" ");

                if (inputTitle != null && !inputTitle.isEmpty()) {

                    titleCondition += " AND MATCH(movies.title) AGAINST ('";
                    for (String word : words) {
                        titleCondition += "+" + word + "* ";
                    }
                    titleCondition += "' IN BOOLEAN MODE)\n";
                }

                // fuzzy Search
                // Specify a threshold for fuzzy search
                int fuzzySearchThreshold = 2;

                if (inputYear != -1) {
                    yearCondition = " AND movies.year = ?";
                    searchCondition.add(String.valueOf(inputYear));
                    conditionCount++;
                }

                if (inputDirector != null && inputDirector != "") {
                    directorCondition = " AND movies.director LIKE ?";
                    inputDirector  = "%" + inputDirector + "%";
                    searchCondition.add(inputDirector);
                    conditionCount++;
                }

                if (inputStar != null && inputStar != "") {
                    starCondition = " AND stars.name LIKE ?";
                    inputStar  = "%" + inputStar + "%";
                    searchCondition.add(inputStar);
                    conditionCount++;
                }

                whereClause = "WHERE" + titleCondition + yearCondition + directorCondition + starCondition;
                int indexOfAnd = whereClause.indexOf("AND");
                if (indexOfAnd != -1) { whereClause = whereClause.replaceFirst("AND ", ""); }

                String query = "SELECT movies.id, movies.title, movies.year, movies.director, ratings.rating AS rating,\n" +
                        "       GROUP_CONCAT(DISTINCT stars_in_movies.starId ORDER BY stars.name ASC) AS starIds,\n" +
                        "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT genres.name ORDER BY genres.name ASC SEPARATOR ','), ',', 3) AS genres,\n" +
                        "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT genres_in_movies.genreId ORDER BY genres.name ASC), ',', 3) AS genreId,\n" +
                        "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT stars.name ORDER BY stars.name ASC SEPARATOR ','), ',', 3) AS star\n" +
                        "FROM movies\n" +
                        "INNER JOIN genres_in_movies ON movies.id = genres_in_movies.movieId\n" +
                        "LEFT JOIN ratings ON ratings.movieId = movies.id\n" +
                        "INNER JOIN genres ON genres.id = genres_in_movies.genreId\n" +
                        "INNER JOIN stars_in_movies ON movies.id = stars_in_movies.movieId\n" +
                        "INNER JOIN stars ON stars_in_movies.starId = stars.id\n" +
                        whereClause +
                        " GROUP BY movies.id, movies.title, movies.year, movies.director, ratings.rating\n" + orderBy;

                // Add LIMIT and OFFSET for pagination
                query += " LIMIT ? OFFSET ?";
                // Create the PreparedStatement with the query
                statement = conn.prepareStatement(query);


                // Parameter setting logic for search conditions
                if ((inputTitle != null || inputYearString != null || inputDirector != null || inputStar != null)) {
                    int paramIndex = 1; // Index to keep track of parameter position

                    if (inputYear != -1) {
                        statement.setInt(paramIndex, inputYear);
                        paramIndex++;
                    }

                    if (inputDirector != null && inputDirector != "") {
                        statement.setString(paramIndex, inputDirector);
                        paramIndex++;
                    }

                    if (inputStar != null && inputStar != "") {
                        statement.setString(paramIndex, inputStar);
                        paramIndex++;
                    }

                    statement.setInt(paramIndex, pageSize); // Set LIMIT parameter
                    paramIndex++;
                    statement.setInt(paramIndex, offset); // Set OFFSET parameter
                }

                //statement = conn.prepareStatement(query);
                System.out.println("statement :" + statement);
                int size = searchCondition.size();
                int count = 0;

                while (size > count) {
                    if (searchCondition.get(count) == String.valueOf(inputYear)) {
                        statement.setInt(count+1, inputYear);
                    }
                    else {
                        statement.setString(count + 1, searchCondition.get(count));
                    }
                    count++ ;
                }
            }
            else {
                if(gid != null && !gid.isEmpty()) {
                    whereClause = "WHERE movies.id IN (SELECT movieId FROM genres_in_movies WHERE genreId = ?)";
                    para = gid;
                }
                if (startWith != null && !startWith.isEmpty() ){
                    if (startWith.equals("*"))
                    {
                        whereClause = "WHERE movies.title not REGEXP ?";
                        para = "^[0-9a-z]";
                    }
                    else {
                        whereClause = "WHERE movies.title LIKE ?";
                        para = startWith + "%";
                    }
                }

                String query = "SELECT movies.id, movies.title, movies.year, movies.director, ratings.rating AS rating,\n" +
                        "       GROUP_CONCAT(DISTINCT stars_in_movies.starId ORDER BY stars.name ASC) AS starIds,\n" +
                        "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT genres.name ORDER BY genres.name ASC SEPARATOR ','), ',', 3) AS genres,\n" +
                        "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT genres_in_movies.genreId ORDER BY genres.name ASC), ',', 3) AS genreId,\n" +
                        "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT stars.name ORDER BY stars.name ASC SEPARATOR ','), ',', 3) AS star\n" +
                        "FROM movies\n" +
                        "INNER JOIN genres_in_movies ON movies.id = genres_in_movies.movieId\n" +
                        "LEFT JOIN ratings ON ratings.movieId = movies.id\n" +
                        "INNER JOIN genres ON genres.id = genres_in_movies.genreId\n" +
                        "INNER JOIN stars_in_movies ON movies.id = stars_in_movies.movieId\n" +
                        "INNER JOIN stars ON stars_in_movies.starId = stars.id\n" +
                        whereClause +
                        " GROUP BY movies.id, movies.title, movies.year, movies.director, ratings.rating\n" + orderBy;

                System.out.println("whereClause2:" + whereClause);

                // Add LIMIT and OFFSET for pagination
                query += " LIMIT ? OFFSET ?";
                // Create the PreparedStatement with the query
                statement = conn.prepareStatement(query);
                statement.setString(1, para); // Set LIMIT parameter
                statement.setInt(2, pageSize); // Set LIMIT parameter
                statement.setInt(3, offset); // Set OFFSET parameter
            }

            // Time before JDBC execution
            jdbcStartTime = System.nanoTime();

            // Perform the query
            ResultSet rs = statement.executeQuery();

            // Time after JDBC execution
            jdbcEndTime = System.nanoTime();

            JsonArray jsonArray = new JsonArray();
            int rowCount = 0;
            // Iterate through each row of rs
            while (rs.next()) {

                String id = rs.getString("id");
                String title = rs.getString("title");
                String year = rs.getString("year");
                String director = rs.getString("director");
                String rating = rs.getString("rating");
                String genres = rs.getString("genres");
                String genreId = rs.getString("genreId");
                String star = rs.getString("star");
                String starIds = rs.getString("starIds");
                rowCount++;


                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", id);
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("year", year);
                jsonObject.addProperty("director", director);
                jsonObject.addProperty("rating", rating);
                jsonObject.addProperty("genres", genres);
                jsonObject.addProperty("genresId", genreId);
                jsonObject.addProperty("star", star);
                jsonObject.addProperty("starIds", starIds);
                // calculate maxPAge

                //int jsonLength = (jsonObject.entrySet().size());
                jsonObject.addProperty("rowCount", rowCount);

                jsonArray.add(jsonObject);

            }
            rs.close();
            statement.close();

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
        // Always remember to close db connection after usage. Here it's done by try-with-resources

        long servletEndTime = System.nanoTime();

        long servletElapsedTime = servletEndTime - servletStartTime;
        long jdbcElapsedTime = jdbcEndTime - jdbcStartTime;

        try {
            String currentDir = System.getProperty("user.dir");
            String filePath = currentDir + File.separator + "log_file.txt";

            // Create a File object for the new text file
            File newTextFile = new File(filePath);
            System.out.println(newTextFile);

            // Create the file if it doesn't exist
            if (newTextFile.createNewFile()) {
                System.out.println("Text file created: " + newTextFile.getName());
            } else {
                System.out.println("Text file already exists.");
            }

            // Write measurements to a file if needed
            FileWriter fileWriter = new FileWriter(newTextFile, true);
            try {
                fileWriter.write("search servlet total execution time (TS): " + servletElapsedTime + " nanoseconds\n");
                fileWriter.write("JDBC execution time (TJ): " + jdbcElapsedTime + " nanoseconds\n");
            } catch (IOException e) {
                // Handle IOException
                e.printStackTrace();
            } finally {
                // Close the FileWriter in the finally block
                if (fileWriter != null) {
                    try {
                        fileWriter.close(); // Close the FileWriter
                    } catch (IOException e) {
                        e.printStackTrace(); // Handle exceptions while closing
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating the text file.");
            e.printStackTrace();
        }
    }


}