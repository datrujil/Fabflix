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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleStarsServlet", urlPatterns = "/api/single-star")
public class SingleStarsServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

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

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting starId: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Construct a query with parameter represented by "?"
//            String query = "SELECT stars_in_movies.starId, stars.name AS starName, stars.birthYear, stars_in_movies.movieId, movies.title, movies.year, movies.director FROM stars LEFT JOIN stars_in_movies ON stars.id = stars_in_movies.starId LEFT JOIN movies ON movies.id = stars_in_movies.movieId WHERE stars.id = ?";
            String query = "SELECT stars_in_movies.starId, stars.name AS starName, stars.birthYear, stars_in_movies.movieId, movies.title, movies.year, movies.director FROM stars " +
                    "LEFT JOIN stars_in_movies ON stars.id = stars_in_movies.starId " +
                    "LEFT JOIN movies ON movies.id = stars_in_movies.movieId " +
                    "WHERE stars.id = ? " +
                    "GROUP BY " +
                    "    stars_in_movies.starId, stars.name, stars.birthYear, stars_in_movies.movieId, movies.title, movies.year, movies.director " +
                    "ORDER BY " +
                    "    movies.year DESC, movies.title ASC";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String starId = rs.getString("starId");
                String starName = rs.getString("starName");
                String starDob = rs.getString("birthYear");

                String movieId = rs.getString("movieId");
                String movieTitle = rs.getString("title");
                String movieYear = rs.getString("year");
                String movieDirector = rs.getString("director");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("starId", starId);
                jsonObject.addProperty("starName", starName);
                jsonObject.addProperty("birthYear", starDob);
                jsonObject.addProperty("movieId", movieId);
                jsonObject.addProperty("title", movieTitle);
                jsonObject.addProperty("year", movieYear);
                jsonObject.addProperty("director", movieDirector);

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

    }

}