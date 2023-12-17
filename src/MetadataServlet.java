import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import java.sql.DatabaseMetaData;



// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "MetadataServlet", urlPatterns = "/api/metadata")
public class MetadataServlet extends HttpServlet {

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public String[] urls= {};

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

//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType("application/json");
//        String metadata = request.getParameter("metadata");
//        PrintWriter out = response.getWriter();
//        JsonArray jsonArray = new JsonArray();
//
//        try (Connection connection = dataSource.getConnection()) {
//            DatabaseMetaData metaData = connection.getMetaData();
//            ResultSet resultSet = metaData.getTables(null, null, null, new String[]{"TABLE"});
//
//            while (resultSet.next()) {
//                JsonObject responseJsonObject = new JsonObject(); // Move inside the loop
//
//                String tableName = resultSet.getString("TABLE_NAME");
//                responseJsonObject.addProperty("table", tableName);
//
//                // Get columns metadata for each table
//                ResultSet columnsResultSet = metaData.getColumns(null, null, tableName, "%");
//
//                while (columnsResultSet.next()) {
//                    String column = columnsResultSet.getString("COLUMN_NAME");
//                    String type = columnsResultSet.getString("TYPE_NAME");
//
//                    responseJsonObject.addProperty("column", column);
//                    responseJsonObject.addProperty("type", type);
//
//                    jsonArray.add(responseJsonObject);
//                }
//
//                columnsResultSet.close();
//            }
//
//            resultSet.close();
//
//            out.write(jsonArray.toString());
//            response.setStatus(200);
//
//        } catch (Exception e) {
//            // Write error message JSON object to output
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("errorMessage", e.getMessage());
//            out.write(jsonObject.toString());
//
//            // Log error to localhost log
//            request.getServletContext().log("Error:", e);
//            // Set response status to 500 (Internal Server Error)
//            response.setStatus(500);
//        } finally {
//            out.close();
//        }
//    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String metadata = request.getParameter("metadata");
        PrintWriter out = response.getWriter();
//        JsonArray jsonArray = new JsonArray();
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE\n" +
                    "FROM INFORMATION_SCHEMA.COLUMNS \n" +
                    "WHERE TABLE_SCHEMA = 'moviedb'";

            PreparedStatement statement = connection.prepareStatement(query);

            // Perform the query
            ResultSet resultSet = statement.executeQuery();

            JsonArray jsonArr = new JsonArray();

            while(resultSet.next()){
                String tableName = resultSet.getString("TABLE_NAME");
                String columnName = resultSet.getString("COLUMN_NAME");
                String dataType = resultSet.getString("DATA_TYPE");

                JsonObject columnJson = new JsonObject();
                columnJson.addProperty("table_name", tableName);
                columnJson.addProperty("column_name", columnName);
                columnJson.addProperty("data_type", dataType);

                jsonArr.add(columnJson);

            }
            out.write(jsonArr.toString());

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

    }

}