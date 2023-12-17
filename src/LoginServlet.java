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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.jasypt.util.password.StrongPasswordEncryptor;


@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //get the input
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String formType = request.getParameter("form_type");
        PrintWriter out = response.getWriter();

        // create jsonObj for response
        JsonObject responseJsonObject = new JsonObject();
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("test:" + gRecaptchaResponse);
        if (formType.equals("website")) {
            try {
                // get reCAPTCHA request param
//        boolean verify = RecaptchaVerifyUtils.verify(gRecaptchaResponse);
                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
            } catch (Exception e) {
                //JsonObject jsonObject = new JsonObject();
                responseJsonObject.addProperty("errorMessage", e.getMessage());
                out.write(responseJsonObject.toString());
                // Log error to localhost log
                request.getServletContext().log("Error:", e);
                out.close();
                return;
                // Set response status to 500 (Internal Server Error)
            }
        }



        try (Connection conn = dataSource.getConnection()) {
            //query to get password and id of a customer with a certain email
            String query = "SELECT * from customers where email=?";
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1, username);
            // Perform the query
            ResultSet rs = statement.executeQuery();

            // info of the customer only 1 line
            if (rs.next()) {
                // check if the input password match with the real password
                String correctPassword = rs.getString("password");
                boolean success = new StrongPasswordEncryptor().checkPassword(password, correctPassword);


                if (success) {
                    //log in success
                    // set this user into the session
                    String userId = rs.getString("id");
                    request.getSession().setAttribute("user", new User(username, userId));

                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "Login successful");

                } else {
                    //password incorrect =>fail login
                    System.out.println(password);
                    System.out.println(correctPassword);

                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "Incorrect password!");
                    // Log to localhost log
                    request.getServletContext().log("Login failed for user: " + username);
                    //sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                }
            }
            else {
                //no sql ouput => email not exist
                System.out.println("print rs.nect(): " + rs.next());
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "User does not exist!");
                // Log to localhost log
                request.getServletContext().log("Login failed, user does not exist");


            }
            rs.close();
            statement.close();
            // write response out
            out.write(responseJsonObject.toString());
            response.setStatus(200);

        }
        catch (Exception e) {
            //JsonObject jsonObject = new JsonObject();
            responseJsonObject.addProperty("errorMessage", e.getMessage());
            out.write(responseJsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }
}

