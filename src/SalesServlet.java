import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.BufferedReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Date;

@WebServlet(name = "SalesServlet", urlPatterns = "/api/sales")
public class SalesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (Connection conn = dataSource.getConnection()) {
            // Retrieve the sales data from the database
            String query = "SELECT s.id, s.customerId, s.movieId, s.saleDate, m.title FROM sales AS s JOIN movies AS m ON s.movieId = m.id WHERE customerId = ?";
            int customerId = Integer.parseInt(request.getParameter("customerId"));

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setInt(1, customerId);
                JsonArray salesArray = new JsonArray();
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        JsonObject sale = new JsonObject();
                        sale.addProperty("id", rs.getInt("id"));
                        sale.addProperty("customerId", rs.getInt("customerId"));
                        sale.addProperty("movieId", rs.getString("movieId"));
                        sale.addProperty("saleDate", rs.getDate("saleDate").toString());
                        sale.addProperty("title", rs.getString("title"));
                        salesArray.add(sale);
                    }
                }

                // Set the response content type to JSON
                response.setContentType("application/json");

                // Send the JSON response to the client
                response.getWriter().write(new Gson().toJson(salesArray));
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection conn = dataSource.getConnection()) {
            // Obtain the data from the request
            StringBuilder requestBody = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            // Parse the JSON data
            JsonObject salesData = JsonParser.parseString(requestBody.toString()).getAsJsonObject();

            // Obtain the data from the request
            //int salesId = generateRandomSaleId(); // Generate a random sales ID
            int customerId = salesData.get("customerId").getAsInt();
            String movieTitle = salesData.get("movieTitle").getAsString();
            String movieId = getMovieIdByTitle(conn, movieTitle);
            java.util.Date utilDate = new java.util.Date();
            java.sql.Date saleDate = new java.sql.Date(utilDate.getTime());

            // Insert the data into the sales table
            String insertQuery = "INSERT INTO sales (customerId, movieId, saleDate) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(insertQuery)) {
                //preparedStatement.setInt(1, salesId);
                preparedStatement.setInt(1, customerId);
                preparedStatement.setString(2, movieId);
                preparedStatement.setDate(3, saleDate);
                preparedStatement.executeUpdate();
            }

            response.setStatus(200);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // Helper methods to generate random data
//    private int generateRandomSaleId() {
//        // Replace with your logic to get a random movie ID
//        int randomId = new Random().nextInt(1000) + 1;
//        return randomId;
//    }

    // Method to get movieId by movie title
    private String getMovieIdByTitle(Connection conn, String movieTitle) throws SQLException {
        String query = "SELECT id FROM movies WHERE title = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, movieTitle);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("id");
                }
            }
        }
        return "N/A"; // Return -1 if movie title is not found
    }

}
