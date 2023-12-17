import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// Declaring a WebServlet called ShoppingServlet, which maps to url "/cart"
@WebServlet(name="ShoppingServlet", urlPatterns="/api/cart")
public class ShoppingServlet extends HttpServlet {
    private List<CartItem> cartItems = new ArrayList<>();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        if ("getData".equals(action)) {
            // Call the custom method to retrieve user data
            getUserData(request, response);
        } else {
            // Use Gson to serialize cartItems to JSON
            Gson gson = new Gson();
            String cartJson = gson.toJson(cartItems);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(cartJson);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Parse the JSON data from the request using Gson
        Gson gson = new Gson();
        CartItem newItem = gson.fromJson(request.getReader(), CartItem.class);
        // Check if the item with the same title already exists in the cart
        boolean itemExists = false;
        for (CartItem existingItem : cartItems) {
            if (existingItem.getTitle().equals(newItem.getTitle())) {
                // Item with the same title exists, update the quantity
                existingItem.setQuantity(newItem.getQuantity());
                itemExists = true;
                break;
            }
        }
        if (!itemExists) {
            // Item with the same title doesn't exist, add the new item
            cartItems.add(newItem);
        }
        // Serialize updated cartItems to JSON
        String updatedCartJson = gson.toJson(cartItems);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(updatedCartJson);
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String movieTitle = request.getParameter("title");

        // Remove the item with the specified title from the server-side storage (cart)
        cartItems.removeIf(item -> item.getTitle().equals(movieTitle));


        // Serialize and return the updated cart
        Gson gson = new Gson();
        String updatedCartJson = gson.toJson(cartItems);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(updatedCartJson);
    }

    protected void getUserData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Retrieve the user object from the session
        User user = (User) request.getSession().getAttribute("user");

        // Create a JSON object to hold the user data
        JsonObject userData = new JsonObject();

        if (user != null) {
            userData.addProperty("status", "success");
            userData.addProperty("userId", user.getId());
            userData.addProperty("username", user.getUsername());
            // Add more user-related data as needed
        } else {
            userData.addProperty("status", "error");
            userData.addProperty("message", "User not logged in");
        }

        // Set the response content type to JSON
        response.setContentType("application/json");

        // Write the JSON data to the response
        response.getWriter().write(userData.toString());
    }


}
