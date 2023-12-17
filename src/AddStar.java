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


@WebServlet(name = "AddStar", urlPatterns = "/api/addstar")
public class AddStar extends HttpServlet {
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

        String starName = request.getParameter("star-name");
        String starBirthYear = request.getParameter("star-birthYear");

        JsonObject responseJsonObject = new JsonObject();
        try {
            Connection connection = dataSource.getConnection();
            String query = "CALL add_star(?, ?, ?, ?)";
            CallableStatement procedureCall = connection.prepareCall(query);
                procedureCall.setString(1, starName);
                if (starBirthYear == null || starBirthYear.isEmpty()) {
                    procedureCall.setNull(2, Types.INTEGER);
                } else {
                    try {
                        int birthYear = Integer.parseInt(starBirthYear);
                        procedureCall.setInt(2, birthYear);
                    } catch (NumberFormatException e) {
                        // Handle the case where starBirthYear is not a valid integer
                        e.printStackTrace(); // Log or handle the exception as needed
                    }
                }

            procedureCall.registerOutParameter(3, Types.VARCHAR);
            procedureCall.registerOutParameter(4, Types.VARCHAR);

                procedureCall.execute();


                String message = procedureCall.getString(4);

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

