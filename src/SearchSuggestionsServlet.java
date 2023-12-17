import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@WebServlet(name="SearchSuggestionsServlet", urlPatterns="/api/search-suggestion")
public class SearchSuggestionsServlet extends HttpServlet{
    public static HashMap<Integer, String> movieMap = new HashMap<>();

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json"); // Response mime type
        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()){
            // setup the response json arrray
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("query");
            String[] keywords = query.split("\\s+");

            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                out.write(jsonArray.toString());
                return;
            }

            // TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars
//            try{
//                PreparedStatement indexStatement = conn.prepareStatement("ALTER TABLE movies ADD FULLTEXT(title)");
//                indexStatement.executeUpdate();
//            } catch (SQLException e){
//                System.out.println(e);
//            }

            StringBuilder condition = new StringBuilder();
            for (int i = 0; i < keywords.length; i++){
                if (i > 0){
                    condition.append(" ");
                }
                condition.append('+').append(keywords[i]).append('*');
            }
            PreparedStatement movieStatement = conn.prepareStatement("SELECT m.id, m.title FROM movies AS m WHERE MATCH (title) AGAINST (? IN BOOLEAN MODE) ORDER BY m.title ASC LIMIT 10");
            movieStatement.setString(1, condition.toString());
            System.out.println(movieStatement);

            ResultSet movieResultSet = movieStatement.executeQuery();

            while (movieResultSet.next()){
                String id = movieResultSet.getString("id");
                String title = movieResultSet.getString("title");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("value", title);

                JsonObject additionalJsonObject = new JsonObject();
                additionalJsonObject.addProperty("id", id);

                jsonObject.add("data", additionalJsonObject);
                jsonArray.add(jsonObject);
            }
            movieResultSet.close();
            movieStatement.close();

            out.write(jsonArray.toString());
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        } finally {
            out.close();
        }
    }
}
