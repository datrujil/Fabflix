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
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.BufferedReader;
import com.google.gson.JsonParser;


@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        String customerId = request.getParameter("id");
        System.out.println("id is" + customerId);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
//            Statement statement = conn.createStatement();

            // Query for movie list. CHANGE THE QUERY LATER to SELECT 20 MOST RATING MOVIES
            String query = "SELECT c.id, c.ccId, cc.firstName, cc.lastName, cc.expiration\n" +
                    "FROM customers AS c\n" +
                    "JOIN creditcards AS cc\n" +
                    "ON c.ccId = cc.id\n" +
                    "WHERE c.id = ?;\n";

            // Create a PreparedStatement with the query
            PreparedStatement preparedStatement = conn.prepareStatement(query);

            // Set the customer ID as a parameter
            preparedStatement.setString(1, customerId);

            // Perform the query
            ResultSet rs = preparedStatement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String id = rs.getString("id");
                String ccId = rs.getString("ccId");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String expiration = rs.getString("expiration");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", id);
                jsonObject.addProperty("ccId", ccId);
                jsonObject.addProperty("firstName", firstName);
                jsonObject.addProperty("lastName", lastName);
                jsonObject.addProperty("expiration", expiration);

                jsonArray.add(jsonObject);
            }
            rs.close();
            preparedStatement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonArray jsonArray = new JsonArray();

        // Read the JSON data from the request body
        StringBuilder jsonRequestBody = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonRequestBody.append(line);
            }
        }

        // Parse the JSON data
        JsonObject jsonRequest = new JsonParser().parse(jsonRequestBody.toString()).getAsJsonObject();

        // Obtain payment information from the JSON object
        String firstName = jsonRequest.get("firstName").getAsString();
        String lastName = jsonRequest.get("lastName").getAsString();
        String creditCardNumber = jsonRequest.get("creditCardNumber").getAsString();
        String expirationMonth = jsonRequest.get("expirationMonth").getAsString();
        String expirationYear = jsonRequest.get("expirationYear").getAsString();
        String expirationDay = jsonRequest.get("expirationDay").getAsString();

        System.out.println(firstName);
        System.out.println(lastName);
        System.out.println(creditCardNumber);


        // Call a method to check the data using an SQL query
        boolean dataIsValid = checkPaymentData(jsonArray, firstName, lastName, creditCardNumber, expirationMonth, expirationYear, expirationDay);

        if (dataIsValid) {
            System.out.println("SUCCESS");
            // Data is valid, proceed with payment processing
            response.setStatus(200);
            String responseData = jsonArray.toString();
            response.getWriter().write(responseData);
            System.out.println("Response Data: " + responseData);
        } else {
            // Data is not valid
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private boolean checkPaymentData(JsonArray jsonArray, String firstName, String lastName, String creditCardNumber, String expirationMonth, String expirationYear, String expirationDay) {
        try (Connection conn = dataSource.getConnection()) {
            // Create a PreparedStatement to execute the SQL query
            String query = "SELECT * FROM customers AS c JOIN creditcards AS cc ON c.ccId = cc.id WHERE " +
                    "cc.firstName = ? AND cc.lastName = ? AND cc.id = ? " +
            "AND YEAR(expiration) = ? AND MONTH(expiration) = ? AND DAY(expiration) = ?";

            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, creditCardNumber);
            preparedStatement.setString(4, expirationMonth);
            preparedStatement.setString(5, expirationYear);
            preparedStatement.setString(6, expirationDay);
            System.out.println(preparedStatement);

            ResultSet rs = preparedStatement.executeQuery();

            // Check if any rows were returned
            if (rs.next()) {
                System.out.println("FOUND");
                String id = rs.getString("id");
                String ccId = rs.getString("ccId");
                String fName = rs.getString("firstName");
                String lName = rs.getString("lastName");
                String expiration = rs.getString("expiration");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", id);
                jsonObject.addProperty("ccId", ccId);
                jsonObject.addProperty("firstName", fName);
                jsonObject.addProperty("lastName", lName);
                jsonObject.addProperty("expiration", expiration);

                jsonArray.add(jsonObject);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Data does not exist in the database, it's not valid
        return false;
    }
}