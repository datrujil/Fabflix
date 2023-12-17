import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.Types;


@WebServlet(name = "AddMovie", urlPatterns = "/api/addmovie")
public class AddMovie extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        String movieTitle = request.getParameter("movie-title");
        String movieYear = request.getParameter("movie-year");
        String movieDirector = request.getParameter("movie-director");
        String movieGenre = request.getParameter("movie-genre");
        String movieStarName = request.getParameter("movie-star-name");
        String movieStarYear = request.getParameter("movie-star-year");



        JsonObject responseJsonObject = new JsonObject();
        try {
            Connection connection = dataSource.getConnection();
            String query = "CALL add_movie(?, ?, ?, ?,?,?,?)";
            CallableStatement procedureCall = connection.prepareCall(query);
            procedureCall.setString(1, movieTitle);
            procedureCall.setInt(2, Integer.parseInt(movieYear));
            procedureCall.setString(3, movieDirector);

            procedureCall.setString(4, movieStarName);
            if (movieStarYear == null || movieStarYear.isEmpty()) {
                procedureCall.setNull(5, Types.INTEGER);
            } else {
                try {
                    int starYear = Integer.parseInt(movieStarYear);
                    procedureCall.setInt(5, starYear);
                } catch (NumberFormatException e) {
                    // Handle the case where starBirthYear is not a valid integer
                    e.printStackTrace(); // Log or handle the exception as needed
                }
            }

            procedureCall.setString(6, movieGenre);

            procedureCall.registerOutParameter(7, Types.VARCHAR); //move_message

            procedureCall.execute();

            String message = procedureCall.getString(7);
            if (message != null)
            {
                responseJsonObject.addProperty("message", message);
            }


            response.setStatus(200);
            out.write(responseJsonObject.toString());


        } catch (Exception e) {
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
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Implement the logic for handling GET requests
        // (You can leave it empty if you don't need to handle GET requests differently)
    }

}

