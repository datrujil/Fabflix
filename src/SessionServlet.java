import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

// Declaring a WebServlet called SessionServlet, which maps to url "/session"
@WebServlet(name = "SessionServlet", urlPatterns = "/api/session")
public class SessionServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String title = "Session Tracking Example";

        // Get a instance of current session on the request
        HttpSession session = request.getSession(true);
        String heading = "Shopping Cart";

        // Retrieve cart data from session
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");

        if (cartItems == null) {
            cartItems = new ArrayList<>(); // Initialize an empty cart
        }

        // Update the cart data in the session
        session.setAttribute("cartItems", cartItems);

        // Retrieve data named "accessCount" from session, which count how many times the user requested before
        Integer accessCount = (Integer) session.getAttribute("accessCount");

        if (accessCount == null) {
            // Which means the user is never seen before
            accessCount = 0;
            heading = "Welcome, New-Comer";
        } else {
            // Which means the user has requested before, thus user information can be found in the session
            heading = "Welcome Back";
            accessCount++;
        }

        // Update the new accessCount to session, replacing the old value if existed
        session.setAttribute("accessCount", accessCount);

        String action = request.getParameter("action");

        if ("add".equals(action)){
            String movieTitle = request.getParameter("title");
            double moviePrice = Double.parseDouble(request.getParameter("price"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));

            boolean found = false;
            for (CartItem item : cartItems){
                if (item.getTitle().equals(movieTitle)){
                    item.setQuantity(item.getQuantity() + quantity);
                    found = true;
                    break;
                }
            }

            if (!found){
                CartItem newItem = new CartItem(movieTitle, moviePrice, quantity);
                cartItems.add(newItem);
            }

            session.setAttribute("cartItems", cartItems);
        }


        // Retrieve the movie information you want to add to the cart
        String movieTitle = request.getParameter("movieTitle");
        String moviePriceParam = request.getParameter("moviePrice");
        if (moviePriceParam != null){
            double moviePrice = Double.parseDouble(moviePriceParam);
            // Check if the movie is already in the cart
            boolean movieAlreadyInCart = false;
            for (CartItem item : cartItems) {
                if (item.getTitle().equals(movieTitle)) {
                    item.setQuantity(item.getQuantity() + 1);
                    movieAlreadyInCart = true;
                    break;
                }
            }

            if (!movieAlreadyInCart) {
                CartItem newItem = new CartItem(movieTitle, moviePrice, 1);
                cartItems.add(newItem);
            }
        }

        out.println("<html><head><title>" + title + "</title></head>\n" +
                "<body bgcolor=\"#FDF5E6\">\n" +
                "<h1 ALIGN=\"center\">" + heading + "</h1>\n" +
                "<h2>Information on Your Session:</H2>\n" +
                "<table border=1 align=\"center\">\n" +
                "  <tr bgcolor=\"#FFAD00\">\n" +
                "    <th>Info Type<th>Value\n" +
                "  <tr>\n" +
                "    <td>ID\n" +
                "    <td>" + session.getId() + "\n" +
                "  <tr>\n" +
                "    <td>Creation Time\n" +
                "    <td>" +
                new Date(session.getCreationTime()) + "\n" +
                "  <tr>\n" +
                "    <td>Time of Last Access\n" +
                "    <td>" +
                new Date(session.getLastAccessedTime()) + "\n" +
                "  <tr>\n" +
                "    <td>Number of Previous Accesses\n" +
                "    <td>" + accessCount + "\n" +
                "  </tr>" +
                "</table>\n");

        for (CartItem item : cartItems) {
            out.println("  <tr>\n" +
                    "    <td>" + item.getTitle() + "\n" +
                    "    <td>" + item.getQuantity() + "\n" +
                    "  </tr>");
        }

        // The following two statements show how to retrieve parameters in the request. The URL format is something like:
        // http://localhost:8080/cs122b-fall21-project2-session-example/Session?myname=Chen%20Li
        String myName = request.getParameter("myname");
        if (myName != null)
            out.println("Hey " + myName + "<br><br>");

        out.println("</body></html>");
    }
}